package stepsword.mahoutsukai.item.clarent;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.item.GrowSwordItem;
import stepsword.mahoutsukai.util.Utils;

public class Clarent extends GrowSwordItem {
   protected String name;
   public static String MT_ATTACK_DAMAGE = "mahoutsukai_clarent_attack";

   public Clarent() {
      super(ToolMaterial.IRON, 1.0F, 10, MTConfig.CLARENT_DURABILITY);
   }

   public static float calculateDamageFromWeapon(ItemStack s) {
      float r = 1.0F;
      ItemAttributeModifiers map = GrowSwordItem.attributes(s,s.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,ItemAttributeModifiers.EMPTY));
      float d = 1.0F;

      for (Entry me : map.modifiers()) {
         if (me.attribute().is(Attributes.ATTACK_DAMAGE)) {
            AttributeModifier m = me.modifier();
            if (m.operation() == Operation.ADD_VALUE) {
               r = (float)(r + m.amount());
            }

            d = r;
            if (m.operation() == Operation.ADD_MULTIPLIED_BASE) {
               d = (float)(d + r * m.amount());
            }

            if (m.operation() == Operation.ADD_MULTIPLIED_TOTAL) {
               d = (float)(d * (1.0 + m.amount()));
            }
         }
      }

      return d;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BLOCK;
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      if (handIn == InteractionHand.MAIN_HAND) {
         player.startUsingItem(handIn);
      }

      return InteractionResult.CONSUME;
   }

   public boolean releaseUsing(ItemStack stack, Level world, LivingEntity player, int cnt) {
      if (!world.isClientSide()) {
         SwordMahou mahou = Utils.getSwordMahou(stack);
         if (mahou == null) {
            mahou = new SwordMahou();
         }

         mahou.setAttackDamage(
            Math.min(
               MTConfig.CLARENT_ATTACK_CAP,
               Math.max(0.0F, mahou.getAttackDamage() - (this.getUseDuration(stack, player) - cnt) * (float)MTConfig.CLARENT_DECREASE_PER_BLOCKING_TICK)
            )
         );
         Utils.setSwordMahou(stack, mahou);
      }

      super.releaseUsing(stack, world, player, cnt);
      return true;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.CLARENT_DURABILITY;
   }
}
