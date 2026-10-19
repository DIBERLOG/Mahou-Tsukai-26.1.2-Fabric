package stepsword.mahoutsukai.render.gui.guide;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.item.guide.GuidebookInstance;
import stepsword.mahoutsukai.item.guide.SpellPage;
import stepsword.mahoutsukai.util.UnorderedList;

public class SpellPageGui extends PageGui {
   SpellPage page;

   public SpellPageGui(GuidebookInstance guidebook, SpellPage page) {
      super(guidebook);
      this.page = page;
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
      if(!GuideCatalogScreen.knows(this.page.getRecipe())){
         int x=(this.width-this.xSize)/2+30,y=(this.height-this.ySize)/2+40;
         DoubleInfoPageGui.drawWordWrap(this.font,graphics,Component.translatable("mahoutsukai.catalog.locked",2),x,y,this.xSize-60,3355443);return;
      }
      UnorderedList recipe = this.page.getRecipe();
      int m = (this.width - this.xSize) / 2;
      int n = (this.height - this.ySize) / 2;
      float f = 2.56F;
      int w1 = m + 63;
      int h1 = n + (int)(f * 24.0F);
      int h2 = h1 + 20;
      int space = 9;
      int loc = 0;

      for (String s : recipe.getKeys()) {
         int count = recipe.get(s);

         for (int i = 0; i < count; i++) {
            if (loc == 0) {
               graphics.item(new ItemStack((ItemLike)MahoujinRecipeRegistrar.ingredients.get(s)), w1 + space, h1 - 1);
            } else if (loc == 1) {
               graphics.item(new ItemStack((ItemLike)MahoujinRecipeRegistrar.ingredients.get(s)), w1, h2);
            } else {
               graphics.item(new ItemStack((ItemLike)MahoujinRecipeRegistrar.ingredients.get(s)), w1 + space * 2 + 1, h2 - 1);
            }

            loc++;
         }
      }

      this.drawTexturedModalRect(
         graphics, Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin.png"), m + 30, n + 30, 0, 0, 256, 256, 101, 101
      );
      int loc2 = 1;

      for (String s : recipe.getKeys()) {
         for (int i = 0; i < recipe.get(s); i++) {
            DoubleInfoPageGui.drawWordWrap(
               this.font,
               graphics,
               Component.literal(
                  MahoujinRecipeRegistrar.ingredients.get(s).getName(new ItemStack((ItemLike)MahoujinRecipeRegistrar.ingredients.get(s))).getString()
               ),
               m + 30,
               n + 130 + space * loc2++,
               this.xSize / 2 - 40,
               3355443
            );
         }
      }

      DoubleInfoPageGui.drawWordWrap(
         this.font, graphics, Component.literal(this.page.getCloth()), m + 30, n + 130 + space * loc2++, this.xSize / 2 - 40, 3355443
      );
      DoubleInfoPageGui.drawWordWrap(
         this.font, graphics, Component.literal(ChatFormatting.BOLD + this.page.getTitle()), m + this.xSize / 2 + 10, n + 10, this.xSize / 2 - 40, 0
      );
      DoubleInfoPageGui.drawWordWrap(
         this.font, graphics, Component.literal(this.page.getDescription()), m + this.xSize / 2 + 10, n + 40, this.xSize / 2 - 40, 1118481
      );
   }
}
