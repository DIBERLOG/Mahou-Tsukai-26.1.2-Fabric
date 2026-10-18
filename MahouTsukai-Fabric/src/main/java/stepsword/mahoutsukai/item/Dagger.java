package stepsword.mahoutsukai.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class Dagger extends ItemBase {
   public Dagger() {
      super("dagger", new Properties().stacksTo(1));
   }

   @Override public ItemStackTemplate getCraftingRemainder(ItemStack input) { return new ItemStackTemplate(this); }
   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!world.isClientSide()) {
         player.hurtServer((net.minecraft.server.level.ServerLevel)world, player.damageSources().generic(), 2.0F);
         EffectUtil.buff(player, ModEffects.BLEEDING, false, 300);
      }

      return InteractionResult.SUCCESS;
   }
}
