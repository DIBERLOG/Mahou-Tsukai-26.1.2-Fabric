package stepsword.mahoutsukai.item.spells.projection;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou;
import stepsword.mahoutsukai.effects.projection.ProjectionSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class ProjectionSpellScroll extends SpellScroll {
   public static final String MEMORIZED_WEAPON = "MAHOUTSUKAI_MEMORIZED_WEAPON";

   public ProjectionSpellScroll() {
      super(new Properties().component(DataComponents.STACK_HOLDERS_COMPONENT, new StackHolderMahou()));
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.PROJECTION_MANA_COST;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, context, display, tooltip, flagIn);
      String s = "";
      Item item = stack.getItem();
      if (item instanceof ProjectionSpellScroll) {
         StackHolderMahou stackHolderMahou = Utils.getStackHolderMahou(stack);
         if (stackHolderMahou != null) {
            Optional<ItemStack> weapon = stackHolderMahou.getStack();
            if (weapon.isPresent() && !weapon.get().isEmpty()) {
               Item i = weapon.get().getItem();
               if (weapon.get().isEnchanted()) {
                  s = s + "Enchanted ";
               }

               s = s + i.getName(weapon.get()).getString();
               tooltip.accept(Component.literal(ChatFormatting.GREEN + s));
            }
         }
      }
   }

   @Override public CastOutcome cast(Player user,ScrollMahou binding,ItemStack stack){
      boolean memorized=Utils.getStackHolderMahou(stack).getStack().isPresent();
      boolean produced=doSpell(user,binding,stack);
      return new CastOutcome(produced || !memorized && Utils.getStackHolderMahou(stack).getStack().isPresent(),produced);
   }
   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      StackHolderMahou mahou = Utils.getStackHolderMahou(stack);
      Optional<ItemStack> memorized = mahou.getStack();
      if (!memorized.isEmpty() && memorized.get().getItem() != Items.AIR) {
         return ProjectionSpellEffect.projection(user, memorized.orElse(null));
      }

      ItemStack mem = ProjectionSpellEffect.memorize(user);
      if (mem != null && !mem.isEmpty()) {
         user.sendOverlayMessage(Component.literal(ChatFormatting.RED + "Memorized!"));
         mahou.setStack(mem);
         Utils.setStackHolderMahou(stack, mahou);
         Utils.updateHeldItem(user);
      }

      return false;
   }
}
