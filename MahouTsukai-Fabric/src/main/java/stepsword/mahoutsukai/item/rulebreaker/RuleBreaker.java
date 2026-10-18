package stepsword.mahoutsukai.item.rulebreaker;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.potion.ProbabilityAlterPotion;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class RuleBreaker extends Item {
   public static String MODULUS_TAG = "MAHOUTSUKAI_RB_MODULUS";
   public static String DESIGNATION_TAG = "MAHOUTSUKAI_RB_DESIGNATION";

   public RuleBreaker() {
      super(
         ItemBase.withRegisteredId(new Properties())
            .component(DataComponents.RULE_BREAKER_COMPONENT, new RuleBreakerMahou())
            .stacksTo(1)
            .durability(MTConfig.RULE_BREAKER_DURABILITY)
      );
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.RULE_BREAKER_DURABILITY;
   }

   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!world.isClientSide()) {
         if (player.isDiscrete()) {
            setDesignation(stack, getDesignation(stack) + 1);
         } else if (!EffectUtil.hasBuff(player, ModEffects.PROBABILITY_ALTER)) {
            player.hurtServer((net.minecraft.server.level.ServerLevel)world, player.damageSources().generic(), 2.0F);
            int mod = getModulus(stack);
            int des = getDesignation(stack);
            ProbabilityAlterPotion.createProbabilityAlter(player, des, mod);
            EffectUtil.buff(player, ModEffects.PROBABILITY_ALTER, false, MTConfig.RULE_BREAKER_DURATION);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
         }
      }

      return InteractionResult.SUCCESS;
   }

   public static void ruleBreakerLivingHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource source) {
      Entity e = source.getEntity();
      if (e instanceof LivingEntity && ((LivingEntity)e).getMainHandItem().getItem() instanceof RuleBreaker) {
         ItemStack stack = ((LivingEntity)e).getMainHandItem();
         if (!target.level().isClientSide() && !EffectUtil.hasBuff(target, ModEffects.PROBABILITY_ALTER)) {
            int mod = getModulus(stack);
            int des = getDesignation(stack);
            ProbabilityAlterPotion.createProbabilityAlter(target, des, mod);
            EffectUtil.buff(target, ModEffects.PROBABILITY_ALTER, false, MTConfig.RULE_BREAKER_DURATION);
         }
      }
   }

   public static void setModulus(ItemStack stack, int m) {
      RuleBreakerMahou mahou = Utils.getRuleBreakerMahou(stack);
      mahou.setModulus(Math.max(2, m));
      Utils.setRuleBreakerMahou(stack, mahou);
   }

   public static void setDesignation(ItemStack stack, int d) {
      RuleBreakerMahou mahou = Utils.getRuleBreakerMahou(stack);
      mahou.setDesignation(Math.floorMod(d, getModulus(stack)));
      Utils.setRuleBreakerMahou(stack, mahou);
   }

   public static int getModulus(ItemStack stack) {
      return Utils.getRuleBreakerMahou(stack).getModulus();
   }

   public static int getDesignation(ItemStack stack) {
      return Utils.getRuleBreakerMahou(stack).getDesignation();
   }
}
