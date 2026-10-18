package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;
import stepsword.mahoutsukai.networking.AuthorityPacket;
import stepsword.mahoutsukai.networking.AuthoritySmokeRingPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class BorrowedAuthorityPotion extends ScrollPotion implements ExtraPotionInterface {
   public static final Identifier speedauthority = Identifier.fromNamespaceAndPath("mahoutsukai", "authority_speed");

   protected BorrowedAuthorityPotion() {
      super(ModEffects.getColorNumber(255, 255, 51));
   }

   public static void authorityPlayerTick(Player player) {
      Level world = player.level();
      if (!world.isClientSide()) {
         if (player.hasEffect(ModEffects.BORROWED_AUTHORITY)) {
            if (!player.isDiscrete()) {
               AttributeModifier a = new AttributeModifier(speedauthority, MTConfig.AUTHORITY_SPEED_MULTIPLIER, Operation.ADD_MULTIPLIED_BASE);
               if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(speedauthority)) {
                  player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(a);
               }
            } else {
               player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedauthority);
            }
         } else {
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedauthority);
         }
      }
   }

   public static void authorityLivingUpdate(LivingEntity entity) {
      int offspeed = 5;
      if (entity != null) {
         ILivingMahou mahou = Utils.getLivingMahou(entity);
         if (mahou != null && mahou.getAuthorityHit()) {
            Vec3 xyz = entity.getDeltaMovement();
            if (!entity.level().isClientSide()) {
               if (xyz.lengthSqr() < offspeed * offspeed) {
                  mahou.setAuthorityHit(false);
                  entity.noPhysics = false;
               } else {
                  entity.needsSync = true;
                  entity.setOnGround(false);
                  entity.noPhysics = MTConfig.AUTHORITY_BLOCK_RESISTANCE_LIMIT > 0;
                  double factor1 = MTConfig.AUTHORITY_NORMAL_SLOW_DOWN_FACTOR;
                  xyz = xyz.scale(factor1);
                  entity.setDeltaMovement(xyz);
                  sendAuthoritySmokeRing(entity, xyz);
                  if (MTConfig.AUTHORITY_BLOCK_RESISTANCE_LIMIT > 0) {
                     Vec3 epos = entity.position();
                     Vec3 motionV = entity.getDeltaMovement();
                     List<BlockPos> affected = new ArrayList<>();

                     for (int i = 0; i < motionV.length(); i++) {
                        BlockPos nextTick = Utils.toBlockPos(epos.add(motionV.normalize().scale(i)));
                        Stream<BlockPos> poss = BlockPos.betweenClosedStream(nextTick.offset(-1, -1, -1), nextTick.offset(1, 1, 1));
                        poss.forEach(
                           tmp -> {
                              BlockPos p = new BlockPos(tmp.getX(), tmp.getY(), tmp.getZ());
                              BlockState bs = entity.level().getBlockState(p);
                              if (!Utils.isBlockAir(entity.level(), p)
                                 && bs.getBlock().getExplosionResistance() <= MTConfig.AUTHORITY_BLOCK_RESISTANCE_LIMIT + 0.01
                                 && entity.level().getBlockEntity(p) == null) {
                                 affected.add(p);
                              } else if (!Utils.isBlockAir(entity.level(), p)) {
                                 entity.noPhysics = false;
                              }
                           }
                        );
                     }

                     breakBlocks(affected,entity);
                  }

                  entity.setDeltaMovement(xyz);
               }
            }
         }
      }
   }

   public static void authorityAttackFromPlayer(Player player, Entity target) {
      double damage = MTConfig.AUTHORITY_DAMAGE_TO_OTHER;
      double damageToSelf = MTConfig.AUTHORITY_DAMAGE_TO_SELF;
      if (player.hasEffect(ModEffects.BORROWED_AUTHORITY) && !player.level().isClientSide() && target instanceof LivingEntity && target != player && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpell(player.level(),player.getUUID(),target)) {
         Vec3 pv = player.position();
         Vec3 look = pv.subtract(target.position());
         MandatoryFun.completeFunReq(player, MandatoryFun.FUN_REQS.BORROWED_AUTHORITY_PUNCH);
         borrowedAuthorityBoop(target, 16.0F, look.x, look.y, look.z, 2.0);
         ILivingMahou targetMahou = Utils.getLivingMahou((LivingEntity)target);
         if (!EffectUtil.hasBuff((LivingEntity)target, ModEffects.BA_COOLDOWN)) {
            targetMahou.setAuthorityHit(true);
            target.hurtServer((ServerLevel)player.level(),player.damageSources().playerAttack(player), (float)damage);
            player.hurtServer((ServerLevel)player.level(),player.damageSources().playerAttack(player), (float)damageToSelf);
            Vec3 epos = target.position();
            Vec3 motionV = target.getDeltaMovement();
            makeHoleWithTarget(target.level(), target, epos, motionV);
            EffectUtil.buff((LivingEntity)target, ModEffects.BA_COOLDOWN, false, 8);
         }
      }
   }

   public static void makeHoleWithTarget(Level world, Entity target, Vec3 epos, Vec3 motionV) {
      if (MTConfig.AUTHORITY_BLOCK_RESISTANCE_LIMIT != 0) {
         List<BlockPos> affected = new ArrayList<>();

         for (int i = 0; i < motionV.length(); i++) {
            BlockPos nextTick = Utils.toBlockPos(epos.add(motionV.normalize().scale(i)));
            Stream<BlockPos> poss = BlockPos.betweenClosedStream(nextTick.offset(-1, -1, -1), nextTick.offset(1, 1, 1));
            poss.forEach(
               tmp -> {
                  BlockPos p = new BlockPos(tmp.getX(), tmp.getY(), tmp.getZ());
                  BlockState bs = world.getBlockState(p);
                  if (!Utils.isBlockAir(world, p)
                     && bs.getBlock().getExplosionResistance() <= MTConfig.AUTHORITY_BLOCK_RESISTANCE_LIMIT + 0.01
                     && target.level().getBlockEntity(p) == null) {
                     affected.add(p);
                  }
               }
            );
         }

         breakBlocks(affected,target);
      }
   }

   private static void sendAuthoritySmokeRing(Entity target, Vec3 direction) {
      Vec3 center = target.getBoundingBox().getCenter();
      PacketHandler.sendTracking(target, new AuthoritySmokeRingPacket(center.x, center.y, center.z, direction.x, direction.y, direction.z));
   }

   public static void borrowedAuthorityBoop(Entity entity, float strength, double x, double y, double z, double yboost) {
      entity.needsSync = true;
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      if(f<1.0E-6F)return;
      Vec3 motion = entity.getDeltaMovement();
      double motionX = motion.x;
      double motionY = motion.y;
      double motionZ = motion.z;
      motionX = 0.0;
      motionZ = 0.0;
      motionY = 0.0;
      motionX -= x / f * strength;
      motionZ -= z / f * strength;
      motionY -= y / f * strength;
      entity.hurtMarked = true;
      if (entity.onGround()) {
         motionY += yboost;
      }

      entity.setDeltaMovement(motionX, motionY, motionZ);
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      if(!livingEntity.level().isClientSide()){livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedauthority);notifyNotAuthority(livingEntity);}
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
      if(!livingEntity.level().isClientSide())notifyIfAuthority(livingEntity);
   }

   public static void notifyIfAuthority(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      if (EffectUtil.hasBuff(e, ModEffects.BORROWED_AUTHORITY)) {
         notifyTracking(lst, true, e.level());
      }
   }

   public static void notifyNotAuthority(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      notifyTracking(lst, false, e.level());
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
         for (LivingEntity e : entities) {
            PacketHandler.sendTracking(e, new AuthorityPacket(e.getId(), r));
         }
      }
   }
 private static void breakBlocks(List<BlockPos> affected,Entity target){
  Player owner=target instanceof Player p?p:target instanceof LivingEntity living?living.getLastHurtByPlayer():null;
  EffectUtil.applySpellBlockChanges(affected,target,owner,MTConfig.AUTHORITY_DROPS_BLOCKS,new ArrayList<>(),target.position(),-1,(state,e,drop,pos)->{if(state.isAir() || e.level().getBlockEntity(pos)!=null)return false;if(drop)net.minecraft.world.level.block.Block.dropResources(state,e.level(),pos);return e.level().removeBlock(pos,false);});
 }

}
