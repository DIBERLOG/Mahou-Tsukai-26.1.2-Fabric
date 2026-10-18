package stepsword.mahoutsukai.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class MahouEditBox extends EditBox {
   public MahouEditBox(Font p_94114_, int p_94115_, int p_94116_, int p_94117_, int p_94118_, Component p_94119_) {
      super(p_94114_, p_94115_, p_94116_, p_94117_, p_94118_, p_94119_);
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(event.buttonInfo())) {
            boolean flag = this.isMouseOver(event.x(), event.y());
            if (flag) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(event, doubleClick);
               return true;
            }
         }

         this.setFocused(false);
         return false;
      } else {
         this.setFocused(false);
         return false;
      }
   }

   public void onClick(MouseButtonEvent event, boolean doubleClick) {
      super.onClick(event, doubleClick);
      this.setFocused(true);
   }
}
