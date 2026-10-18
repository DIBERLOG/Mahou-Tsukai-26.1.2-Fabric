package stepsword.mahoutsukai.render.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MahouImageButton extends Button {
   private final Identifier Identifier;
   private final int xTexStart;
   private final int yTexStart;
   private final int yDiffTex;
   private final int textureWidth;
   private final int textureHeight;

   public MahouImageButton(int p_169011_, int p_169012_, int p_169013_, int p_169014_, int p_169015_, int p_169016_, Identifier p_169017_, OnPress p_169018_) {
      this(p_169011_, p_169012_, p_169013_, p_169014_, p_169015_, p_169016_, p_169014_, p_169017_, 256, 256, p_169018_);
   }

   public MahouImageButton(
      int p_94269_, int p_94270_, int p_94271_, int p_94272_, int p_94273_, int p_94274_, int p_94275_, Identifier p_94276_, OnPress p_94277_
   ) {
      this(p_94269_, p_94270_, p_94271_, p_94272_, p_94273_, p_94274_, p_94275_, p_94276_, 256, 256, p_94277_);
   }

   public MahouImageButton(
      int p_94230_,
      int p_94231_,
      int p_94232_,
      int p_94233_,
      int p_94234_,
      int p_94235_,
      int p_94236_,
      Identifier p_94237_,
      int p_94238_,
      int p_94239_,
      OnPress p_94240_
   ) {
      this(p_94230_, p_94231_, p_94232_, p_94233_, p_94234_, p_94235_, p_94236_, p_94237_, p_94238_, p_94239_, p_94240_, Component.literal(""));
   }

   public MahouImageButton(
      int p_94242_,
      int p_94243_,
      int p_94244_,
      int p_94245_,
      int p_94246_,
      int p_94247_,
      int p_94248_,
      Identifier p_94249_,
      int p_94250_,
      int p_94251_,
      OnPress p_94252_,
      Component p_94254_
   ) {
      super(p_94242_, p_94243_, p_94244_, p_94245_, p_94254_, p_94252_, DEFAULT_NARRATION);
      this.textureWidth = p_94250_;
      this.textureHeight = p_94251_;
      this.xTexStart = p_94246_;
      this.yTexStart = p_94247_;
      this.yDiffTex = p_94248_;
      this.Identifier = p_94249_;
   }

   public void setPosition(int x, int y) {
      this.setX(x);
      this.setY(y);
   }

   protected void extractContents(GuiGraphicsExtractor p_281670_, int p_282682_, int p_281714_, float p_282542_) {
      int var5 = this.yTexStart;
      if (this.isHoveredOrFocused()) {
         var5 += this.yDiffTex;
      }

      p_281670_.blit(
         RenderPipelines.GUI_TEXTURED,
         this.Identifier,
         this.getX(),
         this.getY(),
         this.xTexStart,
         var5,
         this.width,
         this.height,
         this.textureWidth,
         this.textureHeight
      );
      if (this.isHoveredOrFocused()) {
      }
   }
}
