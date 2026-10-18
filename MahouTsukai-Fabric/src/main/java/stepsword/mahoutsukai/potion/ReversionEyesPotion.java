package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.livingmahou.ILivingMahou;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;

import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.RevertedPacket;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class ReversionEyesPotion extends EyesPotion {
   public ReversionEyesPotion() {
      super(ModEffects.getColorNumber(242, 7, 23));
   }

   public static void reversionEyesPlayerTick(Player player) {
      if (EffectUtil.hasBuff(player, ModEffects.REVERSION_EYES)) {
         addRevert(player);
      }
   }

   public static void reversionEyesLivingUpdate(LivingEntity entity) {
      Level world = entity.level();
      if (!world.isClientSide() && isEntityReverted(entity)) {
         cantBreatheUnderwater(entity);
         canBurn(entity);
         burnInDay(entity);
         noMoreEffects(entity);
         if (!continueReverting(entity)) {
            EffectUtil.debuff(entity, ModEffects.REVERTED);
            ArrayList<LivingEntity> lst = new ArrayList<>();
            lst.add(entity);
         }
      }
   }

   public static boolean continueReverting(LivingEntity victim) {
      int totalRange = MTConfig.REVERSION_EYES_RANGE_FROM_USER;
      Vec3 myloc = new Vec3(victim.getX(), victim.getY(), victim.getZ());
      boolean keepReverting = false;

      for (Player tmp : victim.level()
         .getEntitiesOfClass(
            Player.class,
            new AABB(
               victim.getX() - totalRange,
               victim.getY() - totalRange,
               victim.getZ() - totalRange,
               victim.getX() + totalRange,
               victim.getY() + totalRange,
               victim.getZ() + totalRange
            )
         )) {
         if (!tmp.getUUID().equals(victim.getUUID())
            && EffectUtil.hasBuff(tmp, ModEffects.REVERSION_EYES)
            && EffectUtil.inRange(victim, tmp, myloc, MTConfig.REVERSION_EYES_RANGE_FROM_LOOK_VEC, false)) {
            keepReverting = true;
         }
      }

      return keepReverting;
   }

   public static void addRevert(LivingEntity target) {
      int totalRange = MTConfig.REVERSION_EYES_RANGE_FROM_USER;
      List<LivingEntity> lst = target.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
               target.getX() - totalRange,
               target.getY() - totalRange,
               target.getZ() - totalRange,
               target.getX() + totalRange,
               target.getY() + totalRange,
               target.getZ() + totalRange
            )
         );
      ArrayList<LivingEntity> toSendE = new ArrayList<>();

      for (LivingEntity tmp : lst) {
         if (EffectUtil.inRange(tmp, target, tmp.position(), MTConfig.REVERSION_EYES_RANGE_FROM_LOOK_VEC, false)
            && !ContractMahoujinTileEntity.isImmuneToSpell(target.level(), target.getUUID(), tmp)
            && !isEntityReverted(tmp)) {
            setEntityReverted(tmp);
            toSendE.add(tmp);
         }
      }

      notifyTracking(toSendE, true, target.level());
   }

   public static void setEntityReverted(LivingEntity target) {
      EffectUtil.buff(target, ModEffects.REVERTED, false, 100);
   }

   public static boolean isEntityReverted(LivingEntity target) {
      return EffectUtil.hasBuff(target, ModEffects.REVERTED);
   }

   public static void noMoreEffects(LivingEntity e) {
      if (!e.level().isClientSide()) {
         ArrayList<Holder<MobEffect>> toremove = new ArrayList<>();

         for (MobEffectInstance effect : e.getActiveEffects()) {
            if (effect.getEffect() != ModEffects.REVERTED) {
               toremove.add(effect.getEffect());
            }
         }

         for (Holder<MobEffect> p : toremove) {
            try {
               if (!(p.value() instanceof RevertedPotion)) {
                  e.removeEffect(p);
               }
            } catch (Exception var5) {
            }
         }

         if (e instanceof Player) {
            IMahou mahou = Utils.getPlayerMahou((Player)e);
            if (mahou != null) {
               mahou.clearBuffs();
            }
         }

         ILivingMahou mahou = Utils.getLivingMahou(e);
         if (mahou != null) {
            mahou.clearBuffs();
         }
      } else {
         // Original client proxy implementation is empty. Native effect synchronization handles the visible state.
      }
   }

   public static void notifyIfReverted(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      if (isEntityReverted(e)) {
         notifyTracking(lst, true, e.level());
      }
   }

   public static void notifyNotReverted(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      notifyTracking(lst, false, e.level());
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
         for (LivingEntity e : entities) {
            PacketHandler.sendTracking(e, new RevertedPacket(e.getId(), r));
         }
      }
   }

   public static void canBurn(LivingEntity e) {
      if (e.fireImmune()) {
         if (e.isInLava()) {
            if(e.level() instanceof ServerLevel server)e.hurtServer(server,e.damageSources().lava(),4);
            e.setLastHurtByPlayer((java.util.UUID)null,100);
            e.igniteForSeconds(15.0F);
         }

         if (!BlockPos.betweenClosedStream(e.getBoundingBox().deflate(0.001)).noneMatch(posIn -> {
            BlockState state = e.level().getBlockState(posIn);
            return state.is(BlockTags.FIRE) || state.is(Blocks.LAVA) || state.is(stepsword.mahoutsukai.block.ModBlocks.blackFireBlock.get());
         })) {
            if(e.level() instanceof ServerLevel server)e.hurtServer(server,e.damageSources().inFire(),1);
         }
      }
   }

   public static void burnInDay(LivingEntity e) {
      if (e instanceof Husk) {
         float f = e.getLightLevelDependentMagicValue();
         if (f > 0.5F
            && e.level().getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && e.level().canSeeSky(Utils.toBlockPos(e.getX(), e.getY() + e.getEyeHeight(), e.getZ()))) {
            boolean flag = true;
            ItemStack itemstack = e.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
               if (itemstack.isDamageableItem()) {
                  itemstack.setDamageValue(itemstack.getDamageValue() + e.level().getRandom().nextInt(2));
                  if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                     e.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  }
               }

               flag = false;
            }

            if (flag) {
               e.igniteForSeconds(8.0F);
            }
         }
      }
   }

   public static void cantBreatheUnderwater(LivingEntity e) {
      if (e.isInWater()) {
         if (e instanceof WaterAnimal) {
            e.setAirSupply(-20);
         }

         if (e.canBreatheUnderwater() || e.hasEffect(MobEffects.WATER_BREATHING)) {
            e.setAirSupply(decreaseAirSupply(e, e.getAirSupply()));
            if (e.getAirSupply() <= 0 && (e.level().getGameTime()%50000L) % 20L == 0L) {
               e.setAirSupply(0);

               for (int i = 0; i < 8; i++) {
                  float f2 = e.getRandom().nextFloat() - e.getRandom().nextFloat();
                  float f = e.getRandom().nextFloat() - e.getRandom().nextFloat();
                  float f1 = e.getRandom().nextFloat() - e.getRandom().nextFloat();
                  e.level()
                     .addParticle(
                        ParticleTypes.BUBBLE,
                        e.getX() + f2,
                        e.getY() + f,
                        e.getZ() + f1,
                        e.getDeltaMovement().x,
                        e.getDeltaMovement().y,
                        e.getDeltaMovement().z
                     );
               }

               if(e.level() instanceof ServerLevel server)e.hurtServer(server,e.damageSources().drown(),2);
            }
         }
      }
   }

   public static int decreaseAirSupply(LivingEntity e, int air) {
      int i = EnchantmentHelper.getEnchantmentLevel(e.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.RESPIRATION), e);
      return i > 0 && e.getRandom().nextInt(i + 1) > 0 ? air : air - 1;
   }
}
