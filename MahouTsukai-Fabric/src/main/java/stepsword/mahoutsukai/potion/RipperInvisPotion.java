package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.RipperInvisiblePacket;
import stepsword.mahoutsukai.util.EffectUtil;

public class RipperInvisPotion extends CooldownPotion implements ExtraPotionInterface {
   protected RipperInvisPotion() {
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      if(livingEntity.level().isClientSide())return;
      notifyNotInvisible(livingEntity);
      EffectUtil.debuff(livingEntity, MobEffects.INVISIBILITY);
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
      if(livingEntity.level().isClientSide())return;
      notifyIfInvisible(livingEntity);
      EffectUtil.buff(livingEntity, MobEffects.INVISIBILITY, false, 2000000000, false);
   }

   public static void notifyIfInvisible(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      if (EffectUtil.hasBuff(e, ModEffects.RIPPER_INVISIBILITY)) {
         notifyTracking(lst, true, e.level());
      }
   }

   public static void notifyNotInvisible(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      notifyTracking(lst, false, e.level());
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
         for (LivingEntity e : entities) {
            PacketHandler.sendTracking(e, new RipperInvisiblePacket(e.getId(), r));
         }
      }
   }

   public static void presenceConcealmentLivingTick(LivingEntity e) {
      if (EffectUtil.hasBuff(e, ModEffects.RIPPER_INVISIBILITY)) {
         RipperInvisCooldownPotion.nudge(e);
      }
   }

   public static boolean presenceConcealmentNoSound(Entity source){return source instanceof LivingEntity living && EffectUtil.hasBuff(living,ModEffects.RIPPER_INVISIBILITY);}
}
