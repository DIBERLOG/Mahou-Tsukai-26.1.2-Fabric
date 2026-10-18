package stepsword.mahoutsukai.item.morgan;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity;
import stepsword.mahoutsukai.item.GrowSwordItem;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.ItemAbilityPacket;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class Morgan extends GrowSwordItem {
   public static HashMap<UUID, Morgan.MorganStorage> staffs = new HashMap<>();

   public Morgan() {
      super(ToolMaterial.IRON, -2.4F, 3, MTConfig.POWER_CONSOLIDATION_DURABILITY);
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      if (handIn == InteractionHand.MAIN_HAND) {
         player.startUsingItem(InteractionHand.MAIN_HAND);
      }

      return InteractionResult.SUCCESS;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      Morgan.MorganStorage storage = null;
      if (staffs.containsKey(player.getUUID())) {
         storage = staffs.get(player.getUUID());
      } else {
         storage = new Morgan.MorganStorage();
         staffs.put(player.getUUID(), storage);
      }

      if (!player.level().isClientSide()) {
         SwordMahou mahou = Utils.getSwordMahou(stack);
         Utils.setSwordMahou(stack, mahou);
         if (player instanceof Player && storage.ball == null) {
            if (!EffectUtil.hasBuff(player, ModEffects.CM_COOLDOWN)) {
               if (PlayerManaManager.drainMana((Player)player, MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST, false, false)
                  == MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST) {
                  List<Integer> settings = MahouSettings.getIntSettings((Player)player, MahouSettings.Spell.MORGAN_BALL);
                  storage.ball = new MorganBallEntity(
                     player.level(),
                     player,
                     Utils.colorConvert(settings.get(0)),
                     Utils.colorConvert(settings.get(1)),
                     Utils.colorConvert(settings.get(2)),
                     0.3F,
                     Utils.colorConvert(settings.get(3)),
                     Utils.colorConvert(settings.get(4)),
                     Utils.colorConvert(settings.get(5)),
                     0.0F,
                     mahou.getAttackDamage()
                  );
                  Vec3 eye = player.getEyePosition(1.0F);
                  storage.ball.setPos(eye.x, eye.y, eye.z);
                  storage.ball.setDeltaMovement(player.getLookAngle().normalize().scale(0.3F));
                  player.level().addFreshEntity(storage.ball);
                  EffectUtil.buff(player, ModEffects.CM_COOLDOWN, false, MTConfig.MORGAN_CALIBURN_POWER_COOLDOWN);
               }
            } else {
               ItemAbilityPacket.cooldownMessage((Player)player, player.getEffect(ModEffects.CM_COOLDOWN).getDuration());
            }
         }
      }
   }

   public boolean releaseUsing(ItemStack stack, Level world, LivingEntity player, int useTicks) {
      Morgan.MorganStorage storage;
      if (staffs.containsKey(player.getUUID())) {
         storage = staffs.get(player.getUUID());
      } else {
         storage = new Morgan.MorganStorage();
         staffs.put(player.getUUID(), storage);
      }

      storage.ball = null;
      return true;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.POWER_CONSOLIDATION_DURABILITY;
   }

   public boolean isCombineRepairable(ItemStack stack) {
      return false;
   }

   public class MorganStorage {
      public MorganBallEntity ball = null;
   }
}
