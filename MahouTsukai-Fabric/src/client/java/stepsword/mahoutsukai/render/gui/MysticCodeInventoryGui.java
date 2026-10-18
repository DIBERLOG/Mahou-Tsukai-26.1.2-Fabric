package stepsword.mahoutsukai.render.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import stepsword.mahoutsukai.containers.MysticCodeInventory;

public class MysticCodeInventoryGui extends AbstractContainerScreen<MysticCodeInventory> {
   public static Identifier MYSTIC_CODE_GUI_TEXTURE = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/gui/mystic_code_gui.png");

   public MysticCodeInventoryGui(MysticCodeInventory screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn, 176, 133);
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float partialTicks) {
      super.extractRenderState(graphics, mx, my, partialTicks);
   }

   protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
      graphics.text(this.font, this.title.getString(), 8, 6, 0xFF404040, false);
   }

   public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(RenderPipelines.GUI_TEXTURED, MYSTIC_CODE_GUI_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      super.extractContents(graphics, mouseX, mouseY, partialTicks);
   }
}
