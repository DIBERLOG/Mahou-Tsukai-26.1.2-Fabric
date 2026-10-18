package stepsword.mahoutsukai.render.gui.guide;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.item.guide.ContentsPage;
import stepsword.mahoutsukai.item.guide.DoubleInfoPage;
import stepsword.mahoutsukai.item.guide.GuidebookInstance;
import stepsword.mahoutsukai.item.guide.InfoPage;
import stepsword.mahoutsukai.item.guide.SpellPage;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.SavePagePacket;
import stepsword.mahoutsukai.render.gui.MahouImageButton;

public class PageGui extends Screen {
   Identifier guidebookTex = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/gui/guidebook.png");
   public int xSize;
   public int ySize;
   public int tx = 0;
   public int ty = 10;
   public int tx2 = 256;
   public int ty2 = 191;
   public int tleftButtonTx = 72;
   public int tleftButtonTy = 211;
   public int tleftButtonTx2 = 18;
   public int tleftButtonTy2 = 10;
   public int trightButtonTx = 91;
   public int trightButtonTy = 211;
   public int trightButtonTx2 = 18;
   public int trightButtonTy2 = 10;
   public int thomeButtonTx = 115;
   public int thomeButtonTy = 211;
   public int thomeButtonTx2 = 10;
   public int thomeButtonTy2 = 10;
   ArrayList<Button> buttonList = new ArrayList<>();
   MahouImageButton leftArrow;
   MahouImageButton rightArrow;
   MahouImageButton home;
   int id = 0;
   public float scaleFactor;
   GuidebookInstance guidebook;

   public PageGui(GuidebookInstance guidebook) {
      super(Component.translatable(""));
      this.guidebook = guidebook;
      this.scaleFactor = 1.2328767F;
      this.xSize = (int)(this.scaleFactor * this.tx2);
      this.ySize = (int)(this.scaleFactor * this.ty2);
      this.width = this.xSize;
      this.height = this.ySize;
      this.leftArrow = new MahouImageButton(
         0, 0, this.tleftButtonTx2, this.tleftButtonTy2, this.tleftButtonTx, this.tleftButtonTy, 13, this.guidebookTex, this::actionPerformed
      );
      this.rightArrow = new MahouImageButton(
         0, 0, this.trightButtonTx2, this.trightButtonTy2, this.trightButtonTx, this.trightButtonTy, 13, this.guidebookTex, this::actionPerformed
      );
      this.home = new MahouImageButton(
         0, 0, this.thomeButtonTx2, this.thomeButtonTy2, this.thomeButtonTx, this.thomeButtonTy, 13, this.guidebookTex, this::actionPerformed
      );
      this.addExtraButton(this.leftArrow);
      this.addExtraButton(this.rightArrow);
      this.addExtraButton(this.home);
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.leftArrow.setPosition(i + 20, j + this.ySize - 20);
      this.rightArrow.setPosition(i + this.xSize - 38, j + this.ySize - 20);
      this.home.setPosition(i + 20, j + 15);
      this.drawTexturedModalRect(graphics, i, j, this.tx, this.ty, this.tx2, this.ty2, this.xSize, this.ySize);

      for (int r = 0; r < this.buttonList.size(); r++) {
         this.buttonList.get(r).extractRenderState(graphics, mouseX, mouseY, partialTicks);
      }
   }

   public void drawTexturedModalRect(GuiGraphicsExtractor graphics, int x, int y, int textureX, int textureY, int tx2, int ty2, int width, int height) {
      this.drawTexturedModalRect(graphics, this.guidebookTex, x, y, textureX, textureY, tx2, ty2, width, height);
   }

   public void drawTexturedModalRect(
      GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int textureX, int textureY, int tx2, int ty2, int width, int height
   ) {
      graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, textureX, textureY, width, height, tx2, ty2, 256, 256);
   }

   public void addExtraButton(Button button) {
      this.buttonList.add(button);
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      for (Button b : this.buttonList) {
         if (b.mouseClicked(event, doubleClick)) {
            this.setFocused(b);
         }
      }

      return super.mouseClicked(event, doubleClick);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double side, double up) {
      if (up < 0.0) {
         this.actionPerformed(this.rightArrow);
      } else if (up > 0.0) {
         this.actionPerformed(this.leftArrow);
      }

      return true;
   }

   public void onClose() {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new SavePagePacket(this.guidebook.getCurrentPage()));
      super.onClose();
   }

   protected void actionPerformed(AbstractWidget button) {
      if (button == this.leftArrow) {
         if (this.guidebook.getCurrentPage() > 0) {
            this.navigateToPage(this.guidebook.getCurrentPage() - 1);
         }
      } else if (button == this.rightArrow) {
         if (this.guidebook.getPages().size() > this.guidebook.getCurrentPage() + 1) {
            this.navigateToPage(this.guidebook.getCurrentPage() + 1);
         }
      } else if (button == this.home && this.guidebook.getCurrentPage() > 0) {
         this.navigateToPage(0);
      }
   }

   public void navigateToPage(int page) {
      if (page >= 0 && page < this.guidebook.getPages().size()) {
         this.guidebook.setCurrentPage(page);
         if (this.guidebook.getPage() instanceof SpellPage) {
            Minecraft.getInstance().setScreen(new SpellPageGui(this.guidebook, (SpellPage)this.guidebook.getPage()));
         } else if (this.guidebook.getPage() instanceof ContentsPage) {
            Minecraft.getInstance().setScreen(new ContentsPageGui(this.guidebook, (ContentsPage)this.guidebook.getPage()));
         } else if (this.guidebook.getPage() instanceof InfoPage) {
            Minecraft.getInstance().setScreen(new InfoPageGui(this.guidebook, (InfoPage)this.guidebook.getPage()));
         } else if (this.guidebook.getPage() instanceof DoubleInfoPage) {
            Minecraft.getInstance().setScreen(new DoubleInfoPageGui(this.guidebook, (DoubleInfoPage)this.guidebook.getPage()));
         }
      }
   }

   public GuidebookInstance getGuidebook() {
      return this.guidebook;
   }
}
