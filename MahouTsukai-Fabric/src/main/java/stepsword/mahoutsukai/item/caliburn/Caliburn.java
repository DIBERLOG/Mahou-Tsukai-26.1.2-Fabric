package stepsword.mahoutsukai.item.caliburn;

import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;
import stepsword.mahoutsukai.entity.mahoujin.SmiteEntity;
import stepsword.mahoutsukai.item.GrowSwordItem;
import stepsword.mahoutsukai.item.ModeItem;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.ItemAbilityPacket;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.FakeSkeleton;
import stepsword.mahoutsukai.util.Utils;

public class Caliburn extends GrowSwordItem implements ModeItem {
   public Caliburn() {
      super(ToolMaterial.IRON, -2.4F, 3, MTConfig.POWER_CONSOLIDATION_DURABILITY);
   }

   @Override
   public Class<Caliburn.CaliburnMode> getModes() {
      return Caliburn.CaliburnMode.class;
   }

   @Override
   public boolean canSwitch(ItemStack stack) {
      return false;
   }

   public Caliburn.CaliburnMode getMode(ItemStack stack) {
      return Caliburn.CaliburnMode.CALIBURN;
   }

   public static float simulateHit(ItemStack s, Level world) {
      float ret = 3.0F;
      int health = 5000000;
      if (Attributes.ATTACK_DAMAGE.value() instanceof RangedAttribute) {
         ((RangedAttribute)Attributes.ATTACK_DAMAGE.value()).maxValue = Math.max(
            ((RangedAttribute)Attributes.ATTACK_DAMAGE.value()).maxValue, MTConfig.POWER_CONSOLIDATION_ATTACK_CAP
         );
      }

      if (s != null) {
         ItemStack s1 = s.copy();
         FakeSkeleton fake_target = new FakeSkeleton(world);
         fake_target.h = 5000000.0F;
         Player fake_player = SafeFakePlayer.createFakePlayerIfNull((ServerLevel)world);
         equipForSimulation(s1, fake_player);
         fake_player.attackStrengthTicker = 1000;
         fake_player.attack(fake_target);
         ret = health - fake_target.h;
      }

      return ret;
   }

   public static void equipForSimulation(ItemStack stack,Player player){
      // The reusable fake player must shed the previous weapon before another simulation.
      player.getMainHandItem().forEachModifier(EquipmentSlot.MAINHAND,(attribute,modifier)->{
         var instance=player.getAttributes().getInstance(attribute);if(instance!=null)instance.removeModifier(modifier.id());
      });
      player.setItemInHand(InteractionHand.MAIN_HAND,stack);
      updateAttr(stack,player,EquipmentSlot.MAINHAND);
   }
   public static void updateAttr(ItemStack s1, Player fake_player, EquipmentSlot hand) {
      s1.forEachModifier(hand, (p_352705_, p_352706_) -> {
         AttributeInstance attributeinstance = fake_player.getAttributes().getInstance(p_352705_);
         if (attributeinstance != null) {
            attributeinstance.removeModifier(p_352706_.id());
            attributeinstance.addTransientModifier(p_352706_);
         }

         if (fake_player.level() instanceof ServerLevel serverlevel) {
            EnchantmentHelper.runLocationChangedEffects(serverlevel, s1, fake_player, hand);
         }
      });
   }

   public static boolean specialTarget(LivingEntity entity) {
      return EffectUtil.inEntityBlacklist(entity, MTConfig.POWER_CONSOLIDATION_FEAR_BLACKLIST)
         ? false
         : entity.isInvertedHealAndHarm()
            || entity.getMainHandItem().getItem() instanceof Morgan
            || entity.getOffhandItem().getItem() instanceof Morgan
            || containsKeyword(entity.getName().getString().toLowerCase(Locale.ENGLISH))
            || containsKeyword(entity.getDisplayName().toString().toLowerCase(Locale.ENGLISH));
   }

   public static boolean containsKeyword(String name) {
      List<? extends String> arr = MTConfig.UNHOLY_WORDS;

      for (int i = 0; i < arr.size(); i++) {
         if (name.contains(arr.get(i))) {
            return true;
         }
      }

      int numKeys = arr.size();

      for (int i = 1; i < numKeys + 1; i++) {
         if (name.contains(Component.translatable("mahoutsukai.caliburnkeyword" + i).getString())) {
            return true;
         }
      }

      return false;
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      player.startUsingItem(handIn);
      return InteractionResult.CONSUME;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity player, int timeLeft) {
      if (!worldIn.isClientSide()) {
         Caliburn.CaliburnMode mode = this.getMode(stack);
         if (player.isShiftKeyDown()) {
            this.switchMode(stack);
         } else if (mode == Caliburn.CaliburnMode.CALIBURN) {
            int radius = MTConfig.POWER_CONSOLIDATION_PULL_RADIUS;
            AABB aabb = new AABB(player.blockPosition().offset(-radius, -4, -radius).getCenter(), player.blockPosition().offset(radius, 4, radius).getCenter());
            List<LivingEntity> lst = worldIn.getEntitiesOfClass(LivingEntity.class, aabb);
            double score = -1.0;
            double chosendist = 0.0;
            LivingEntity chosen = null;

            for (LivingEntity target : lst) {
               if (!target.getUUID().equals(player.getUUID()) && EffectUtil.isLookingAtMe(target, player, 45) && specialTarget(target)) {
                  double d = Math.abs(
                     EffectUtil.pointToLineDistance(target.position(), player.getEyePosition(1.0F), player.getLookAngle().add(player.getEyePosition(1.0F)))
                  );
                  double e = target.position().distanceTo(player.position());
                  if (score == -1.0 || score > d * d + e) {
                     score = d * d + e;
                     chosen = target;
                     chosendist = e;
                  }
               }
            }

            if (chosen != null) {
               Vec3 chosenDirection = chosen.position().subtract(player.position());
               boop(player, (float)(chosendist / 8.0), -chosenDirection.x, -chosenDirection.y, -chosenDirection.z);
               boop(chosen, (float)(chosendist / 8.0), chosenDirection.x, chosenDirection.y, chosenDirection.z);
            }
         } else if (mode == Caliburn.CaliburnMode.EXCALIBUR) {
         }
      }

      super.releaseUsing(stack, worldIn, player, timeLeft);
      return true;
   }

   public static void boop(Entity entity, float strength, double x, double y, double z) {
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      double motionX = 0.0;
      double motionY = 0.0;
      double motionZ = 0.0;
      motionX -= x / f * strength;
      motionZ -= z / f * strength;
      motionY -= y / f * strength;
      if (entity.onGround()) {
         motionY /= 2.0;
         motionY += 0.3;
         if (motionY > 0.6) {
            motionY = 0.6;
         }
      }

      entity.setDeltaMovement(new Vec3(motionX, motionY, motionZ));
      entity.needsSync = true;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.POWER_CONSOLIDATION_DURABILITY;
   }

   public boolean isCombineRepairable(ItemStack stack) {
      return false;
   }

   public static void upgradeDamageCap() {
      if (Attributes.ATTACK_DAMAGE.value() instanceof RangedAttribute) {
         ((RangedAttribute)Attributes.ATTACK_DAMAGE.value()).maxValue = Math.max(
            ((RangedAttribute)Attributes.ATTACK_DAMAGE.value()).maxValue, MTConfig.POWER_CONSOLIDATION_ATTACK_CAP
         );
      }
   }

   public static void handleAbilityCaliburn(ServerPlayer player) {
      ItemStack stack = player.getMainHandItem();
      if (stack.getItem() instanceof Caliburn item) {
         SwordMahou mahou = Utils.getSwordMahou(stack);
         Utils.setSwordMahou(stack, mahou);
         if (!EffectUtil.hasBuff(player, ModEffects.CM_COOLDOWN)) {
            boolean hit = false;
            if (item.getMode(stack) == Caliburn.CaliburnMode.CALIBURN) {
               hit = caliburnSmites(player, mahou);
            } else {
               hit = excaliburSmite(player, mahou);
            }

            if (hit) {
               EffectUtil.buff(player, ModEffects.CM_COOLDOWN, false, MTConfig.MORGAN_CALIBURN_POWER_COOLDOWN);
            }
         } else {
            ItemAbilityPacket.cooldownMessage(player, player.getEffect(ModEffects.CM_COOLDOWN).getDuration());
         }
      }
   }

   public static boolean caliburnSmites(ServerPlayer player, SwordMahou mahou) {
      int radius = MTConfig.POWER_CONSOLIDATION_SMITE_RADIUS;
      AABB aabb = new AABB(player.position().add(-radius, -radius / 2, -radius), player.position().add(radius, radius / 2, radius));
      boolean hit = false;
      if (PlayerManaManager.drainMana(player, MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST, false, false) == MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST) {
         int targets = 0;
         List<LivingEntity> lst = player.level().getEntitiesOfClass(LivingEntity.class, aabb);

         for (LivingEntity target : lst) {
            if (!target.getUUID().equals(player.getUUID()) && specialTarget(target)) {
               targets++;
            }
         }

         for (LivingEntity target : lst) {
            if (!target.getUUID().equals(player.getUUID()) && specialTarget(target)) {
               SmiteEntity smite = new SmiteEntity(target.level(), target, 0.9019608F, 0.9019608F, 0.16470589F, 1.0F, 0.1F, mahou.getAttackDamage() / targets);
               smite.setPos(target.getX(), target.getY(), target.getZ());
               hit = true;
               target.level().addFreshEntity(smite);
            }
         }
      }

      return hit;
   }

   public static boolean excaliburSmite(ServerPlayer player, SwordMahou mahou) {
      int radius = MTConfig.POWER_CONSOLIDATION_SMITE_RADIUS;
      new AABB(player.position().add(-radius, -radius / 2, -radius), player.position().add(radius, radius / 2, radius));
      if (PlayerManaManager.drainMana(player, MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST, false, false) == MTConfig.CALIBURN_MORGAN_ABILITY_MANA_COST) {
      }

      return true;
   }

   public enum CaliburnMode {
      CALIBURN,
      EXCALIBUR;
   }
}
