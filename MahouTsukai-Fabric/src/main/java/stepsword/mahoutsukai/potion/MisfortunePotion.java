package stepsword.mahoutsukai.potion;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou;
import stepsword.mahoutsukai.item.kodoku.KodokuItem;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

import net.minecraft.world.entity.player.Player;
public class MisfortunePotion extends MobEffect {
 private static net.minecraft.world.Container inventory(LivingEntity e){return e instanceof Player p?p.getInventory():e instanceof net.minecraft.world.Container c?c:null;}

   protected MisfortunePotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(22, 22, 22));
   }

   public static void misfortuneLivingUpdate(LivingEntity living) {
      if (EffectUtil.hasBuff(living, ModEffects.MISFORTUNE) && !living.level().isClientSide() && living.level().getGameTime() % 4L == 0L) {
         int level = EffectUtil.getBuffLevel(living, ModEffects.MISFORTUNE);
         level -= getMaxKodoku(living);
         if (level > 0) {
            double lightningchance = MTConfig.KODOKU_MISFORTUNE_LIGHTNING_CHANCE;
            double dropitemchance = MTConfig.KODOKU_MISFORTUNE_DROP_CHANCE;
            double aggrochance = MTConfig.KODOKU_MISFORTUNE_AGGRO_CHANCE;
            double tripchance = MTConfig.KODOKU_MISFORTUNE_TRIP_CHANCE;
            float aggro = living.getRandom().nextFloat();
            float trip = living.getRandom().nextFloat();
            float drop = living.getRandom().nextFloat();
            float lightning = living.getRandom().nextFloat();
            if (aggro < aggrochance * Math.sqrt(level)) {
               int range = 40;
               AABB aabb = new AABB(
                  living.getX() - range, living.getY() - 10.0, living.getZ() - range, living.getX() + range, living.getY() + 10.0, living.getZ() + range
               );
               List<Mob> lst = living.level().getEntitiesOfClass(Mob.class, aabb);
               if (lst.size() > 0) {
                  lst.get(living.getRandom().nextInt(lst.size())).setLastHurtByMob(living);
               }
            }

            if (drop < dropitemchance * Math.sqrt(level)) {
               var inv = inventory(living);
               if (inv != null && inv.getContainerSize() > 0) {
                  int slot = living.getRandom().nextInt(inv.getContainerSize());
                  ItemStack s = inv.getItem(slot);
                  if (!s.isEmpty() && !s.has(net.minecraft.core.component.DataComponents.CONTAINER) && !s.has(net.minecraft.core.component.DataComponents.BUNDLE_CONTENTS) && !inv.removeItem(slot,1).isEmpty()) {
                     living.spawnAtLocation((ServerLevel)living.level(), s.copyWithCount(1), 1.0F);
                  }
               }
            }

            if (trip < tripchance * Math.sqrt(level)) {
               Vec3 motion = living.getDeltaMovement();
               double motionX = motion.x;
               double motionY = motion.y;
               double motionZ = motion.z;
               motionX += living.getLookAngle().x * 2.0;
               motionZ += living.getLookAngle().z * 2.0;
               living.hurtMarked = true;
               living.setDeltaMovement(motionX, motionY, motionZ);
            }

            if (lightning < lightningchance * Math.sqrt(level)) {
               LightningBolt lightningb = (LightningBolt)EntityType.LIGHTNING_BOLT.create(living.level(), EntitySpawnReason.EVENT);
               if (lightningb != null) {
                  lightningb.moveOrInterpolateTo(new Vec3(living.getX(), living.getY(), living.getZ()));
                  lightningb.setVisualOnly(false);
                  ((ServerLevel)living.level()).addFreshEntity(lightningb);
               }
            }
         }
      }
   }

   public static void misfortuneBreak(Player p){if(p.hasEffect(ModEffects.MISFORTUNE))p.getMainHandItem().hurtAndBreak(EffectUtil.getBuffLevel(p,ModEffects.MISFORTUNE)/Math.max(1,MTConfig.KODOKU_TOOL_BREAK_DIVISOR),p,EquipmentSlot.MAINHAND);}
   public static int getMaxKodoku(LivingEntity living) {
      var inventory = inventory(living);
      int maxkodoku = 0;
      if (inventory != null) {
         for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack s = inventory.getItem(i);
            if (!s.isEmpty() && s.getItem() instanceof KodokuItem) {
               KodokuMahou mahou = Utils.getKodokuMahou(s);
               if (mahou != null && mahou.getKodoku() > maxkodoku) {
                  maxkodoku = mahou.getKodoku();
               }
            }
         }
      }

      return maxkodoku;
   }
}