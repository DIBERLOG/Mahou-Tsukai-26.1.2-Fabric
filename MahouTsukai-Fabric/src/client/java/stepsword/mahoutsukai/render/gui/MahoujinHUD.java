package stepsword.mahoutsukai.render.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.item.catalysts.PowderedCatalyst;
import stepsword.mahoutsukai.util.UnorderedList;
import stepsword.mahoutsukai.util.Utils;

public class MahoujinHUD {
   public static void renderHUD(GuiGraphicsExtractor graphics, Minecraft mc) {
      int xc = mc.getWindow().getGuiScaledWidth() / 2;
      int yc = mc.getWindow().getGuiScaledHeight() / 2;
      int radius = 24;
      Player player = mc.player;
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         UnorderedList ul = mahou.getLastRecipe();
         if (ul != null) {
            List<String> catalysts = ul.getOrder();
            if (ul.size() == 3 && catalysts.size() == 3) {
               boolean clothRequired = mahou.getLastRecipeCloth();
               boolean hasClothInInventory = false;
               boolean hasAllCatalysts = true;
               Set<String> hs = ul.getKeys();
               HashMap<String, Integer> counts = new HashMap<>();

               for (int i = 0; i < player.getInventory().getNonEquipmentItems().size(); i++) {
                  ItemStack stack = (ItemStack)player.getInventory().getNonEquipmentItems().get(i);
                  if (stack.getItem() instanceof PowderedCatalyst) {
                     PowderedCatalyst c = (PowderedCatalyst)stack.getItem();

                     for (String s : hs) {
                        if (c.getCatalystName().equals(s)) {
                           if (counts.containsKey(s)) {
                              counts.put(s, counts.get(s) + stack.getCount());
                           } else {
                              counts.put(s, stack.getCount());
                           }
                        }
                     }
                  } else if (!Utils.snapshotNestedItems(stack).isEmpty()) {
                     for(ItemStack st2:Utils.snapshotNestedItems(stack)){
                        if(st2.getItem() instanceof PowderedCatalyst catalyst && hs.contains(catalyst.getCatalystName()))counts.merge(catalyst.getCatalystName(),st2.getCount(),Integer::sum);
                        if(st2.is(ModBlocks.spellClothBlock.get().asItem()))hasClothInInventory=true;
                     }
                  }

                  if (stack.getItem() == Item.byBlock((Block)ModBlocks.spellClothBlock.get())) {
                     hasClothInInventory = true;
                  }
               }

               for (String s : hs) {
                  if (counts.containsKey(s)) {
                     if (ul.get(s) > counts.get(s)) {
                        hasAllCatalysts = false;
                     }
                  } else {
                     hasAllCatalysts = false;
                  }
               }

               if (hasAllCatalysts && (hasClothInInventory || !clothRequired)) {
                  for (String s : catalysts) {
                     ItemStack stack = new ItemStack((ItemLike)MahoujinRecipeRegistrar.ingredients.get(s));
                     graphics.item(stack, xc + radius + 32, yc - 8);
                     yc -= 11;
                  }

                  if (clothRequired) {
                     ItemStack stack = new ItemStack(Item.byBlock((Block)ModBlocks.spellClothBlock.get()));
                     graphics.item(stack, xc + radius + 32, yc - 8);
                     yc -= 8;
                  }
               }

               graphics.text(mc.font, "+", xc + radius + 14, yc + 10, -1, true);
            }
         }
      }
   }
}
