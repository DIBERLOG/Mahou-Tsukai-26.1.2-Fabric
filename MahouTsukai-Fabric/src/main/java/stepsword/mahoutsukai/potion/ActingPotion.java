package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.networking.ActingPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class ActingPotion extends MobEffect implements ExtraPotionInterface {
   protected ActingPotion() {
      super(MobEffectCategory.NEUTRAL, ModEffects.getColorNumber(252, 45, 45));
   }

   public static void notifyIfActing(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      if (EffectUtil.hasBuff(e, ModEffects.ACTING)) {
         notifyTracking(lst, true, e.level());
      }
   }

   public static void notifyNotActing(LivingEntity e) {
      ArrayList<LivingEntity> lst = new ArrayList<>();
      lst.add(e);
      notifyTracking(lst, false, e.level());
   }

   public static void notifyTracking(ArrayList<LivingEntity> entities, boolean r, Level world) {
      if (world instanceof ServerLevel) {
         for (LivingEntity e : entities) {
            ActingPacket p = new ActingPacket(e.getId(), r, Utils.getLivingMahou(e).getActing());
            PacketHandler.sendTracking(e, p);
            if (e instanceof ServerPlayer player) {
               PacketHandler.sendTo(player, p);
            }
         }
      }
   }

   @Override
   public void onRemove(LivingEntity livingEntity, MobEffect effect) {
      if (livingEntity != null) {
         Utils.getLivingMahou(livingEntity).setActing(0);
      }

      notifyNotActing(livingEntity);
   }

   @Override
   public void onAdd(LivingEntity livingEntity, MobEffect effect) {
      notifyIfActing(livingEntity);
   }
}
