package stepsword.mahoutsukai.render.gui.guide;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.item.guide.SpellPage;

public class LinkGuiButton extends Button {
   public String localized;
   public int pageLink;
   ContentsPageGui gui;
   boolean hovered;
   int wrap;

   public LinkGuiButton(int x, int y, int w, int h, String localized, int link, ContentsPageGui gui, int wrap) {
      super(x, y, w, h, Component.literal(""), btn -> gui.navigateToPage(link), DEFAULT_NARRATION);
      this.pageLink = link;
      this.gui = gui;
      if (gui.guidebook.getPages().get(this.pageLink) instanceof SpellPage) {
         this.localized = ChatFormatting.ITALIC + localized;
      } else {
         this.localized = ChatFormatting.BOLD + localized;
      }

      this.wrap = wrap;
   }

   protected void extractContents(GuiGraphicsExtractor graphics, int mx, int my, float partialTicks) {
      Font font = Minecraft.getInstance().font;
      int wr = this.localized.length() - this.sizeStringToWidth(this.localized, this.wrap, font);
      if (wr > 3) {
         this.localized = this.localized.substring(0, this.localized.length() - wr) + "...";
      } else if (wr > 0 && wr < 3) {
         this.localized = this.localized.substring(0, this.localized.length() - 3) + "...";
      }

      this.hovered = mx >= this.getX() && my >= this.getY() && mx < this.getX() + font.width(this.localized) && my < this.getY() + this.getHeight();
      int color = 4473924;
      if (this.hovered) {
         color = 0;
      }

      graphics.text(font, this.localized, this.getX(), this.getY(), DoubleInfoPageGui.opaque(color), false);
   }

   public int sizeStringToWidth(String str, int wrapWidth, Font font) {
      int i = Math.max(1, wrapWidth);
      int j = str.length();
      float f = 0.0F;
      int k = 0;
      int l = -1;
      boolean flag = false;
      boolean flag1 = true;

      while (k < j) {
         char c0 = str.charAt(k);
         switch (c0) {
            case '\n':
               k--;
               break;
            case ' ':
               l = k;
            default:
               if (f != 0.0F) {
                  flag1 = false;
               }

               f += this.getCharWidth(c0, font);
               if (flag) {
                  f++;
               }
               break;
            case '§':
               if (k < j - 1) {
                  ChatFormatting cf = ChatFormatting.getByCode(str.charAt(++k));
                  if (cf == ChatFormatting.BOLD) {
                     flag = true;
                  } else if (cf != null && !cf.isFormat()) {
                     flag = false;
                  }
               }
         }

         if (c0 == '\n') {
            l = ++k;
            break;
         }

         if (f > i) {
            if (flag1) {
               k++;
            }
            break;
         }

         k++;
      }

      return k != j && l != -1 && l < k ? l : k;
   }

   public float getCharWidth(char character, Font fontr) {
      return fontr.width(character + "");
   }
}
