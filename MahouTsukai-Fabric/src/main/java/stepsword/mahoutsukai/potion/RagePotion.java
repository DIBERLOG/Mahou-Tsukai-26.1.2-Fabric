package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.RagePacket;
import stepsword.mahoutsukai.util.EffectUtil;

public class RagePotion extends MobEffect implements ExtraPotionInterface {
   protected RagePotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   public static void notifyIfRage(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      if (EffectUtil.hasBuff(e, ModEffects.RAGE)) {
         notifyTracking(lst, true, e.level());
      }
   }

   public static void notifyNotRage(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      notifyTracking(lst, false, e.level());
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
         for (LivingEntity e : entities) {
            PacketHandler.sendTracking(e, new RagePacket(e.getId(), r));
         }
      }
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      notifyNotRage(livingEntity);
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
      notifyIfRage(livingEntity);
   }
}
