package stepsword.mahoutsukai.item.replica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.BlocksAttacks.DamageReduction;
import net.minecraft.world.item.component.BlocksAttacks.ItemDamageFunction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.replica.ReplicaMahou;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.ReplicaKnockbackParticlePacket;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class Replica extends ItemBase {

   public static HashMap<String, DamageSource> DAMAGE_SOURCE_REGISTRY = new HashMap<>();

   public Replica() {
      super(
         "replica",
         new Properties()
            .stacksTo(1)
            .durability(MTConfig.REPLICA_DURABILITY)
            .delayedComponent(
               DataComponents.BLOCKS_ATTACKS,
               context -> new BlocksAttacks(
                  0.25F,
                  1.0F,
                  List.of(new DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                  new ItemDamageFunction(Float.MAX_VALUE, 0.0F, 0.0F),
                  Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                  Optional.of(SoundEvents.SHIELD_BLOCK),
                  Optional.of(SoundEvents.SHIELD_BREAK)
               )
            )
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
      );
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BLOCK;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      if (!player.level().isClientSide() && player.isSprinting()) {
         coverMove(player);
      }

      player.startUsingItem(handIn);
      return InteractionResult.CONSUME;
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      if (count == 72000 && !player.level().isClientSide() && player instanceof Player) {
         if (!player.isSprinting() && player.isDiscrete()) {
            saveTeleportLocation(stack, (Player)player);
         }

         if (!EffectUtil.hasBuff(player, ModEffects.REPLICA_SHOCKWAVE_COOLDOWN)
            && PlayerManaManager.drainMana((Player)player, MTConfig.REPLICA_ATTACK_MANA_COST, false, false) == MTConfig.REPLICA_ATTACK_MANA_COST) {
            this.bigKnockback((Player)player, stack);
            EffectUtil.buff(player, ModEffects.REPLICA_SHOCKWAVE_COOLDOWN, false, MTConfig.REPLICA_SHOCKWAVE_COOLDOWN, false);
         }
      }

      player.setDeltaMovement(0.0, 0.0, 0.0);
      player.hurtMarked = true;
   }

   public void bigKnockback(Player playerIn, ItemStack stack) {
      double maxRadius = MTConfig.REPLICA_ATTACK_RANGE;
      ReplicaKnockbackParticlePacket packet = new ReplicaKnockbackParticlePacket(
         playerIn.getX(), playerIn.getY() + playerIn.getBbHeight() / 2.0F, playerIn.getZ(), 0.2F, 0.2F, 0.2F
      );
      PacketHandler.sendTracking(playerIn, packet);
      PacketHandler.sendTo((ServerPlayer)playerIn, packet);
      List<LivingEntity> lst = playerIn.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
               playerIn.getX() - maxRadius,
               playerIn.getY() - maxRadius,
               playerIn.getZ() - maxRadius,
               playerIn.getX() + maxRadius,
               playerIn.getY() + maxRadius,
               playerIn.getZ() + maxRadius
            )
         );
      int multiplier = 0;
      double damage = MTConfig.REPLICA_BASE_DAMAGE;

      for (LivingEntity e : lst) {
         if (ContractMahoujinTileEntity.isImmuneToSpell(playerIn.level(), playerIn.getUUID(), e)) {
            multiplier++;
         }
      }

      for (LivingEntity e : lst) {
         if (!ContractMahoujinTileEntity.isImmuneToSpell(playerIn.level(), playerIn.getUUID(), e)) {
            String type = getDamageTypeForSlot(stack, getSelectedSlot(stack));
            DamageSource source = getDamageSource(type, playerIn);
            e.hurtServer((net.minecraft.server.level.ServerLevel)e.level(),source, (float)Math.min(multiplier * damage, MTConfig.REPLICA_MAX_DAMAGE));
            Vec3 v = e.position().subtract(playerIn.position());
            this.boop(e, 3.0F, -v.x, -v.y, -v.z);
         }
      }
   }

   public static void coverMove(Player player) {
      double totalRange = MTConfig.REPLICA_COVER_MOVE_RANGE;
      Vec3 myloc = new Vec3(player.getX(), player.getY(), player.getZ());
      List<LivingEntity> lst = player.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
               player.getX() - totalRange,
               player.getY() - totalRange,
               player.getZ() - totalRange,
               player.getX() + totalRange,
               player.getY() + totalRange,
               player.getZ() + totalRange
            )
         );
      ArrayList<LivingEntity> inRange = new ArrayList<>();
      HashSet<LivingEntity> needsHelp = new HashSet<>();
      HashSet<LivingEntity> players = new HashSet<>();

      for (LivingEntity tmp : lst) {
         if (EffectUtil.inRange(tmp, player, myloc, (int)totalRange, false)
            && ContractMahoujinTileEntity.isImmuneToSpell(player.level(), player.getUUID(), tmp)
            && player != tmp) {
            inRange.add(tmp);
            if (tmp instanceof Player) {
               players.add(tmp);
            }

            if (needsHelp(tmp)) {
               needsHelp.add(tmp);
            }
         }
      }

      Collections.sort(inRange, getDistanceSorter(player));
      LivingEntity e = coverMoveTarget(inRange, needsHelp, players);
      if (e != null) {
         Vec3 facing = coverMoveFacing(e);
         if (facing == null) {
            MahouTsukaiTeleporter.teleport(player, EffectUtil.getDimension(player.level()), e.position().add(new Vec3(1.0, 0.0, 0.0)));
            player.hurtMarked = true;
         } else {
            facing = facing.normalize();
            float pitch = (float)Math.asin(-facing.y);
            float yaw = (float)Math.atan2(facing.x, facing.z);
            player.yRot = EffectUtil.toDegrees(-yaw);
            player.xRot = EffectUtil.toDegrees(pitch);
            MahouTsukaiTeleporter.teleport(player, EffectUtil.getDimension(player.level()), e.position().add(facing.scale(0.2)));
            player.hurtMarked = true;
         }
      }
   }

   public static LivingEntity coverMoveTarget(ArrayList<LivingEntity> inRange, HashSet<LivingEntity> needsHelp, HashSet<LivingEntity> players) {
      for (LivingEntity living : inRange) {
         if (needsHelp.contains(living) && players.contains(living)) {
            return living;
         }
      }

      for (LivingEntity living : inRange) {
         if (needsHelp.contains(living)) {
            return living;
         }
      }

      for (LivingEntity living : inRange) {
         if (players.contains(living)) {
            return living;
         }
      }

      Iterator var7 = inRange.iterator();
      return var7.hasNext() ? (LivingEntity)var7.next() : null;
   }

   public static void saveTeleportLocation(ItemStack s, Player player) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      mahou.setLoc(player.position());
      mahou.setDim(EffectUtil.getDimension(player.level()).toString());
      Utils.setReplicaMahou(s, mahou);
   }

   public static Vec3 getTeleportLocation(ItemStack s) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      return mahou.getLoc().isEmpty() ? null : mahou.getLoc().get();
   }

   public static Identifier getTeleportDimension(ItemStack s) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      return mahou.getDim().isEmpty() ? null : Identifier.parse(mahou.getDim().get());
   }

   public static boolean getFriendlyOnly(ItemStack s) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      return mahou.getFriendly();
   }

   public static void setFriendlyOnly(ItemStack s, boolean b) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      mahou.setFriendly(b);
      Utils.setReplicaMahou(s, mahou);
   }

   public static Vec3 coverMoveFacing(LivingEntity e) {
      return coverMoveFacing(e, null);
   }

   public static Vec3 coverMoveFacing(LivingEntity e, Entity ignore) {
      int inTroubleRange = 15;
      List<LivingEntity> lst = e.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
               e.getX() - inTroubleRange,
               e.getY() - inTroubleRange,
               e.getZ() - inTroubleRange,
               e.getX() + inTroubleRange,
               e.getY() + inTroubleRange,
               e.getZ() + inTroubleRange
            )
         );
      Collections.sort(lst, getDistanceSorter(e));
      lst.remove(e);
      if (ignore != null) {
         lst.remove(ignore);
      }

      Vec3 ret = null;

      for (LivingEntity attacker : lst) {
         if (ret == null) {
            ret = attacker.position().subtract(e.position());
         }

         if (attacker.getLastHurtByMob() == e
            || attacker instanceof Mob && ((Mob)attacker).getTarget() == e
            || attacker instanceof Player && !ContractMahoujinTileEntity.isImmuneToSpell(e.level(), e.getUUID(), attacker)) {
            return attacker.position().subtract(e.position());
         }
      }

      return ret;
   }

   public static Comparator<LivingEntity> getDistanceSorter(final LivingEntity player) {
      return new Comparator<LivingEntity>() {
         public int compare(LivingEntity o1, LivingEntity o2) {
            double d = o1.distanceTo(player) - o2.distanceTo(player);
            if (d < 0.0) {
               return -1;
            } else {
               return d > 0.0 ? 1 : 0;
            }
         }
      };
   }

   public static boolean needsHelp(LivingEntity e) {
      int inTroubleRange = 15;

      for (LivingEntity attacker : e.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
               e.getX() - inTroubleRange,
               e.getY() - inTroubleRange,
               e.getZ() - inTroubleRange,
               e.getX() + inTroubleRange,
               e.getY() + inTroubleRange,
               e.getZ() + inTroubleRange
            )
         )) {
         if (attacker.getLastHurtByMob() == e || attacker instanceof Mob && ((Mob)attacker).getTarget() == e) {
            return true;
         }
      }

      return false;
   }

   public static void replicaBreakBlock(Player player) {
      if (player.getMainHandItem().getItem() instanceof Replica) {
         ItemStack s = player.getMainHandItem();
         boolean friendly = getFriendlyOnly(s);
         setFriendlyOnly(s, !friendly);
         MutableComponent tt;
         if (friendly) {
            tt = Component.translatable("mahoutsukai.replica.friendlyoff");
         } else {
            tt = Component.translatable("mahoutsukai.replica.friendlyon");
         }

         player.sendOverlayMessage(tt);
      }
   }

   private void boop(Entity entity, float strength, double x, double y, double z) {
      entity.needsSync = true;
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      if(f<1.0E-6)return;
      Vec3 motion = entity.getDeltaMovement();
      double motionX = motion.x();
      double motionY = motion.y();
      double motionZ = motion.z();
      motionX /= 2.0;
      motionZ /= 2.0;
      motionY /= 2.0;
      motionX -= x / f * strength;
      motionZ -= z / f * strength;
      motionY -= y / f * strength;
      entity.hurtMarked = true;
      if (entity.onGround()) {
         motionY /= 2.0;
         motionY += strength;
         if (motionY > 0.4F) {
            motionY = 0.4F;
         }
      }

      entity.setDeltaMovement(motionX, motionY, motionZ);
   }

   public static void setSelectedSlot(ItemStack s, int i) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      mahou.setSlot(i);
      Utils.setReplicaMahou(s, mahou);
   }

   public static int getSelectedSlot(ItemStack s) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      return mahou.getSlot();
   }

   public static String getDamageTypeForSlot(ItemStack s, int i) {
      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      return mahou.getTypes() != null && i>=0 && mahou.getTypes().size() > i ? mahou.getTypes().get(i) : "EMPTY";
   }

   public static void tryAddDamageType(ItemStack s, DamageSource source, LivingEntity e) {
      String type = source.getMsgId();
      if (DAMAGE_SOURCE_REGISTRY.isEmpty()) {
         initVanillaDamageTypes(e.damageSources());
      }

      if (!EffectUtil.inGenericBlacklist(type, MTConfig.REPLICA_BANNED_DAMAGE_TYPES)
         && (DAMAGE_SOURCE_REGISTRY.containsKey(type) || !getDamageSource(type, e).equals(e.damageSources().generic()))) {
         ReplicaMahou mahou = Utils.getReplicaMahou(s);
         ArrayList<String> new_types = new ArrayList<>();
         boolean added = false;

         for (int i = 0; i < 6; i++) {
            String slot = getDamageTypeForSlot(s, i);
            if (slot.equals(type)) {
               added = true;
            }

            if (slot.equals("EMPTY")) {
               if (!added) {
                  new_types.add(type);
                  added = true;
               }
            } else {
               new_types.add(slot);
            }
         }

         mahou.setTypes(new_types);
         Utils.setReplicaMahou(s, mahou);
      }
   }



   public static boolean replicaLivingHurt(LivingEntity target,DamageSource source){if(target.getUseItem().getItem() instanceof Replica){for(int i=0;i<6;i++)if(getDamageTypeForSlot(target.getUseItem(),i).equals(source.getMsgId()))return true;}return false;}


   public static void replicaDamageSource(LivingEntity entity, DamageSource source) {
      if (!entity.level().isClientSide() && entity.isBlocking() && entity.getUseItem().getItem() instanceof Replica) {
         tryAddDamageType(entity.getUseItem(), source, entity);
         entity.getUseItem().hurtAndBreak(1, entity, entity.getUsedItemHand()==InteractionHand.OFF_HAND?EquipmentSlot.OFFHAND:EquipmentSlot.MAINHAND);
      }

      if (!MTConfig.REPLICA_DISABLED) {
         boolean blocking = entity.isBlocking();
         boolean hasShield = entity.getUseItem().getItem() instanceof ShieldItem;
         boolean isPlayer = entity instanceof ServerPlayer;
         if (blocking && hasShield && isPlayer) {
            ServerPlayer e = (ServerPlayer)entity;
            IMahou mahou = Utils.getPlayerMahou(e);
            if (mahou != null) {
               boolean damageExchange = mahou.getDamageExchangeUses() > 0.9 * MTConfig.DAMAGE_EXCHANGE_CAP;
               boolean deathCollection = mahou.getDeathCollectionUsesLeft() >= Math.min(100.0, MTConfig.DEATH_COLLECTION_MAX);
               boolean immunityExchange = EffectUtil.hasBuff(e, ModEffects.IMMUNITY_EXCHANGE);
               boolean armor = e.getArmorValue() > 15;
               boolean mainHand = e.getMainHandItem().getItem() == Items.SHIELD;
               boolean strengthenedMain = StrengtheningSpellEffect.getStrengthened(e.getMainHandItem()) > 0;
               boolean offHand = e.getOffhandItem().getItem() == Items.SHIELD && StrengtheningSpellEffect.getStrengthened(e.getOffhandItem()) > 0;
               if (offHand) {
                  PlayerHelp.sendHelpMessage(e, PlayerHelp.Message.REPLICA_WRONG_HAND);
               } else {
                  int count = 0;
                  boolean[] conditions = new boolean[]{damageExchange, deathCollection, immunityExchange, armor};

                  for (int i = 0; i < conditions.length; i++) {
                     if (conditions[i]) {
                        count++;
                     }
                  }

                  if (count >= 3 && mainHand) {
                     PlayerHelp.sendHelpMessage(e, PlayerHelp.Message.REPLICA_NOT_STRENGTHENED);
                  }
               }

               if (strengthenedMain && mainHand) {
                  if (damageExchange) {
                     if (deathCollection) {
                        if (immunityExchange) {
                           if (armor) {
                              e.getInventory().removeItemNoUpdate(e.getInventory().getSelectedSlot());
                              ItemStack change = new ItemStack((ItemLike)ModItems.replica.get());
                              if (MTConfig.REPLICA_IS_UNBREAKABLE) {
                                 change.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
                              }

                              if (!e.getInventory().add(e.getInventory().getSelectedSlot(), change)) {
                                 e.drop(change, false);
                              }

                              ModTriggers.REPLICA.trigger(e);
                           } else {
                              PlayerHelp.sendHelpMessage(e, PlayerHelp.Message.REPLICA_NOT_ENOUGH_ARMOR);
                           }
                        } else {
                           PlayerHelp.sendHelpMessage(e, PlayerHelp.Message.REPLICA_NO_IMMUNITY_EXCHANGE);
                        }
                     } else {
                        PlayerHelp.sendHelpMessage(e, PlayerHelp.Message.REPLICA_NOT_ENOUGH_SOULS);
                     }
                  } else {
                     PlayerHelp.sendHelpMessage(e, PlayerHelp.Message.REPLICA_NOT_ENOUGH_DAMAGE_ABSORPTION);
                  }
               }
            }
         }
      }
   }

   public static void clearDamageTypes(ItemStack s) {
      ArrayList<String> newlist = new ArrayList<>();

      for (int i = 0; i < 6; i++) {
         newlist.add("EMPTY");
      }

      ReplicaMahou mahou = Utils.getReplicaMahou(s);
      mahou.setTypes(newlist);
      Utils.setReplicaMahou(s, mahou);
   }

   public static void initVanillaDamageTypes(DamageSources srcs) {
      addToRegistry(
         srcs.inFire(),
         srcs.lightningBolt(),
         srcs.onFire(),
         srcs.lava(),
         srcs.hotFloor(),
         srcs.inWall(),
         srcs.cramming(),
         srcs.drown(),
         srcs.starve(),
         srcs.cactus(),
         srcs.fall(),
         srcs.flyIntoWall(),
         srcs.fellOutOfWorld(),
         srcs.generic(),
         srcs.magic(),
         srcs.wither(),
         srcs.dragonBreath(),
         srcs.dryOut(),
         srcs.sweetBerryBush()
      );
   }

   public static void addToRegistry(DamageSource... src) {
      for (int i = 0; i < src.length; i++) {
         DAMAGE_SOURCE_REGISTRY.put(src[i].getMsgId(), src[i]);
      }
   }

   public static DamageSource getDamageSource(String key, LivingEntity e) {
      DamageSource ret = e.damageSources().generic();
      DamageSources srcs = e.damageSources();
      initVanillaDamageTypes(e.damageSources());
      if (DAMAGE_SOURCE_REGISTRY.containsKey(key)) {
         ret = DAMAGE_SOURCE_REGISTRY.get(key);
      } else {
         DamageSource BEE_STING = srcs.sting(e);
         DamageSource MOB = srcs.mobAttack(e);
         DamageSource INDIRECT = srcs.noAggroMobAttack(e);
         DamageSource ARROW = srcs.arrow(new Arrow(EntityType.ARROW, e.level()), e);
         DamageSource EXPLOSION = srcs.explosion(e, null);
         DamageSource THORNS = srcs.thorns(e);
         DamageSource WITHER_SKULL = srcs.witherSkull(new WitherSkull(EntityType.WITHER_SKULL, e.level()), e);
         DamageSource TRIDENT = srcs.trident(e, e);
         DamageSource FIREWORK = srcs.fireworks(new FireworkRocketEntity(e.level(), new ItemStack(Items.FIREWORK_ROCKET), e), e);
         DamageSource THROWN = srcs.thrown(e, e);
         HashMap<String, DamageSource> map = new HashMap<>();
         map.put(BEE_STING.getMsgId(), BEE_STING);
         map.put(MOB.getMsgId(), MOB);
         map.put(INDIRECT.getMsgId(), INDIRECT);
         map.put(ARROW.getMsgId(), ARROW);
         map.put(EXPLOSION.getMsgId(), EXPLOSION);
         map.put(THORNS.getMsgId(), THORNS);
         map.put(WITHER_SKULL.getMsgId(), WITHER_SKULL);
         map.put(TRIDENT.getMsgId(), TRIDENT);
         map.put(FIREWORK.getMsgId(), FIREWORK);
         map.put(THROWN.getMsgId(), THROWN);
         if (map.containsKey(key)) {
            ret = map.get(key);
         }
      }

      return ret;
   }

   public static boolean protects(net.minecraft.server.level.ServerLevel world,Vec3 pos){for(Player p:world.players())if(p.getUseItem().getItem() instanceof Replica && isPointInRadiusOfCircle(p,pos))return true;return false;}


   public static boolean isPointInRadiusOfCircle(Player player, Vec3 pos) {
      Vec3 sphereMid = getSphereMid(player);
      double distance = pos.distanceTo(sphereMid);
      return distance < getSphereMidRadius();
   }

   public static Vec3 getSphereMid(Player player) {
      return player.position().subtract(player.getLookAngle().normalize().scale(getSphereMidRadius() - 0.5));
   }

   public static float getSphereMidRadius() {
      return 3.8F;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.REPLICA_DURABILITY;
   }


}