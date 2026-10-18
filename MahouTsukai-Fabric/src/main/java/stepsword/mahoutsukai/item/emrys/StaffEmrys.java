package stepsword.mahoutsukai.item.emrys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.entity.MahouLightningBoltEntity;
import stepsword.mahoutsukai.entity.mahoujin.LightningEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class StaffEmrys extends ItemBase {
   public static HashMap<UUID, StaffEmrys.StaffEmrysUserStorage> staffs = new HashMap<>();

   public StaffEmrys() {
      super("staff_emrys", new Properties().stacksTo(1));
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      staffs.remove(player.getUUID());
      if (handIn == InteractionHand.OFF_HAND) {
         player.startUsingItem(handIn);
         return InteractionResult.PASS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
      return 72000;
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      StaffEmrys.StaffEmrysUserStorage storage;
      if (staffs.containsKey(player.getUUID())) {
         storage = staffs.get(player.getUUID());
      } else {
         storage = new StaffEmrys.StaffEmrysUserStorage();
         staffs.put(player.getUUID(), storage);
      }

      if (!player.level().isClientSide()
         && player instanceof Player
         && PlayerManaManager.drainMana((Player)player, MTConfig.EMRYS_MANA_COST_FOCUSED / 20, false, false) == MTConfig.EMRYS_MANA_COST_FOCUSED / 20) {
         double d = 30.0;
         double addX = player.getRandom().nextInt((int)d) + player.getRandom().nextFloat() - d / 2.0;
         double addZ = player.getRandom().nextInt((int)d) + player.getRandom().nextFloat() - d / 2.0;
         double addY = player.getRandom().nextInt(2) - 2;
         int charge = this.getRemainingUseDuration(stack, player, count);
         ((ServerLevel)player.level()).rainLevel = Math.min(charge / 40.0F, 2.4F);
         ((ServerLevel)player.level()).thunderLevel = Math.min(charge / 40.0F, 2.4F);
         if (charge / 15.0F > 2.4F) {
            if (player.getRandom().nextFloat() < 0.2F) {
               int y = level.getHeight(Types.WORLD_SURFACE, (int)(player.getX() + addX), (int)(player.getZ() + addZ));
               addLightningBolt((ServerLevel)player.level(), new MahouLightningBoltEntity(player.level(), player.getX() + addX, y, player.getZ() + addZ, false));
            }

            boolean ball = true;
            if (storage.lightnings != null) {
               for (int i = 0; i < storage.lightnings.size(); i++) {
                  if (storage.lightnings.get(i).isAlive() && ball) {
                     storage.lightnings.get(i).setHasBall(ball);
                     ball = false;
                  } else {
                     storage.lightnings.get(i).setHasBall(false);
                  }
               }
            }

            if (player.level().getGameTime() % 10L == 0L) {
               float radius = MTConfig.EMRYS_MAX_RANGE;
               AABB aabb = new AABB(
                  player.getX() - radius,
                  player.getY() - radius,
                  player.getZ() - radius,
                  player.getX() + radius,
                  player.getY() + radius * 2.0F,
                  player.getZ() + radius
               );
               List<LivingEntity> lst = player.level().getEntitiesOfClass(LivingEntity.class, aabb);
               Vec3 myloc = player.position();
               lst.sort((e1, e2) -> (int)(e1.position().distanceToSqr(myloc) - e2.position().distanceToSqr(myloc)));
               storage.lightnings.removeIf(a -> !a.isAlive());
               storage.connected = new HashSet<>();
               storage.lightnings.forEach(a -> {
                  if (a != null && a.target != null) {
                     storage.connected.add(a.target.getUUID());
                  }
               });

               for (LivingEntity e : lst) {
                  if (storage.lightnings.size() >= 3) {
                     break;
                  }

                  if (!e.getUUID().equals(player.getUUID())
                     && !storage.connected.contains(e.getUUID())
                     && !ContractMahoujinTileEntity.isImmuneToSpell(player.level(), player.getUUID(), e)
                     && this.validTarget(player, e)) {
                     LightningEntity le = new LightningEntity(player.level(), player, e, 1.0F, 1.0F, 1.0F, 1.0F, 0.02F, false);
                     player.level().addFreshEntity(le);
                     storage.lightnings.add(le);
                  }
               }

               if (storage.lightnings != null) {
                  for (int i = 0; i < storage.lightnings.size(); i++) {
                     if (i == 0) {
                        storage.lightnings.get(i).setHasBall(true);
                     } else {
                        storage.lightnings.get(i).setHasBall(false);
                     }
                  }
               }
            }
         }
      }
   }

   public boolean validTarget(LivingEntity caster, LivingEntity target) {
      if (!target.isAlive()) {
         return false;
      }

      boolean inrange = EffectUtil.inRange(target, caster, caster.position(), MTConfig.EMRYS_MAX_RANGE, false);
      if (inrange) {
         Vec3 targetEye = target.getEyePosition(1.0F);
         Vec3 casterEye = caster.getEyePosition(1.0F);
         Vec3 diff = targetEye.subtract(casterEye);
         double len = diff.length();
         Vec3 dir = diff.normalize();

         for (int i = 0; i < len; i++) {
            BlockPos p = Utils.toBlockPos(casterEye.add(dir.scale(i)));
            if (!caster.level().isEmptyBlock(p)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean releaseUsing(ItemStack stack, Level world, LivingEntity caster, int t) {
      StaffEmrys.StaffEmrysUserStorage storage;
      if (staffs.containsKey(caster.getUUID())) {
         storage = staffs.get(caster.getUUID());
      } else {
         storage = new StaffEmrys.StaffEmrysUserStorage();
         staffs.put(caster.getUUID(), storage);
      }

      if (!world.isClientSide()) {
         Utils.schedule(world, () -> {
            for (LightningEntity e : storage.lightnings) {
               e.discard();
            }

            staffs.remove(caster.getUUID());
         });
      }

      super.releaseUsing(stack, world, caster, t);
      return true;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BOW;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.POWER_CONSOLIDATION_DURABILITY;
   }

   public static void addLightningBolt(ServerLevel world, MahouLightningBoltEntity entityIn) {
      world.addFreshEntity(entityIn);
   }

   public static void zap(LivingEntity e, boolean ignoreZapped, Player caster) {
      if (e != null && !e.level().isClientSide()) {
         int radius = 4;
         AABB aabb = new AABB(e.getX() - radius, e.getY() - radius, e.getZ() - radius, e.getX() + radius, e.getY() + radius, e.getZ() + radius);

         for (LivingEntity target : e.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if ((!EffectUtil.hasBuff(target, ModEffects.ZAP_COOLDOWN) || ignoreZapped)
               && target.getMainHandItem().getItem() != ModItems.emrysStaff.get()
               && target.getOffhandItem().getItem() != ModItems.emrysStaff.get()
               && caster != null
               && PlayerManaManager.drainMana(caster, MTConfig.EMRYS_MANA_COST_PASSIVE, false, false) == MTConfig.EMRYS_MANA_COST_PASSIVE) {
               target.level().addFreshEntity(new LightningEntity(e.level(), e, target, 1.0F, 1.0F, 1.0F, 1.0F, 0.01F, true));
            }
         }
      }
   }

   public static void emrysHit(Player player,net.minecraft.world.entity.Entity target){if(!player.level().isClientSide() && player.getOffhandItem().getItem()==ModItems.emrysStaff.get() && target instanceof LivingEntity living)zap(living,false,player);}
   public int getRemainingUseDuration(ItemStack stack,LivingEntity entity,int charge){return getUseDuration(stack,entity)-charge;}
   public static float emrysLivingHurt(LivingEntity target,net.minecraft.world.damagesource.DamageSource source,float damage){
      if(source.is(DamageTypes.LIGHTNING_BOLT) && (target.getOffhandItem().getItem()==ModItems.emrysStaff.get() || target.getMainHandItem().getItem()==ModItems.emrysStaff.get()))return 0;
      if(source.getEntity() instanceof Player attacker && attacker.getOffhandItem().getItem()==ModItems.emrysStaff.get())zap(target,false,attacker);
      return damage;
   }
   public static void emrysStruckByLightning(net.minecraft.world.entity.Entity target){if(target instanceof Player player && !player.level().isClientSide() && !MTConfig.DISABLE_EMRYS && player.getOffhandItem().is(Items.STICK) && StrengtheningSpellEffect.getStrengthened(player.getOffhandItem())>0)turnStickIntoItem(player,new ItemStack(ModItems.emrysStaff.get()));}
   public static void turnStickIntoItem(Player player, ItemStack change) {
      if (!player.getInventory().isEmpty()) {
         ItemStack stack = player.getInventory().getItem(40);
         if (stack.getItem() == Items.STICK && StrengtheningSpellEffect.getStrengthened(stack) > 0) {
            player.getInventory().setItem(40, change);
            ModTriggers.EMRYS.trigger((ServerPlayer)player);
         }
      }
   }

   public class StaffEmrysUserStorage {
      public ArrayList<LightningEntity> lightnings = new ArrayList<>();
      public HashSet<UUID> connected = new HashSet<>();
   }
}