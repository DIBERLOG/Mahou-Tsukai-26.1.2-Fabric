package stepsword.mahoutsukai.render.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import stepsword.mahoutsukai.dataattachments.settingsmahou.ISettingsMahou;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.SettingsUpdatePacket;
import stepsword.mahoutsukai.util.Utils;

public class SpellSettingGUI extends Screen {
   public MahouEditBox[] rgb;
   public MahouEditBox[] rgb2;
   int currentY;
   int currentX;
   Font font;
   MahouSettings.Spell spell;
   Component tc;
   int colorw = 30;
   int colorh = 20;
   int yspace = 10;

   public SpellSettingGUI(Component tc, Font font, MahouSettings.Spell spell) {
      super(tc);
      this.rgb = new MahouEditBox[3];
      this.rgb2 = new MahouEditBox[3];
      this.currentY = 60;
      this.currentX = 120;
      this.font = font;
      this.tc = tc;
      this.spell = spell;
   }

   protected void init() {
      if (MahouSettings.hasPrimary(this.spell)) {
         for (MahouEditBox box : this.rgb) {
            if (box != null) {
               this.addRenderableWidget(box);
            }
         }
      }

      if (MahouSettings.hasSecondary(this.spell)) {
         for (MahouEditBox box : this.rgb2) {
            if (box != null) {
               this.addRenderableWidget(box);
            }
         }
      }

      super.init();
   }

   public SpellSettingGUI createPrimaryColorSetting() {
      this.rgb[0] = this.createNewTextSetting(this.colorw, this.colorh, "mahoutsukai.projector.rvalue");
      this.rgb[1] = this.createNewTextSetting(this.colorw, this.colorh, "mahoutsukai.projector.gvalue");
      this.rgb[2] = this.createNewTextSetting(this.colorw, this.colorh, "mahoutsukai.projector.bvalue");
      List<Integer> lst = MahouSettings.getIntSettings(Minecraft.getInstance().player, this.spell);
      this.rgb[0].setValue(lst.get(0) + "");
      this.rgb[1].setValue(lst.get(1) + "");
      this.rgb[2].setValue(lst.get(2) + "");
      return this;
   }

   public SpellSettingGUI createSecondaryColorSetting() {
      this.rgb2[0] = this.createNewTextSetting(this.colorw, this.colorh, "mahoutsukai.projector.rvalue");
      this.rgb2[1] = this.createNewTextSetting(this.colorw, this.colorh, "mahoutsukai.projector.gvalue");
      this.rgb2[2] = this.createNewTextSetting(this.colorw, this.colorh, "mahoutsukai.projector.bvalue");
      List<Integer> lst = MahouSettings.getIntSettings(Minecraft.getInstance().player, this.spell);
      this.rgb2[0].setValue(lst.get(3) + "");
      this.rgb2[1].setValue(lst.get(4) + "");
      this.rgb2[2].setValue(lst.get(5) + "");
      return this;
   }

   public MahouEditBox createNewTextSetting(int w, int h, String translation) {
      MahouEditBox ret = new MahouEditBox(this.font, this.currentX, this.currentY, w, h, Component.translatable(translation));
      this.advanceY(h);
      return ret;
   }

   public void advanceY(int h) {
      this.currentY = this.currentY + h + this.yspace;
   }

   public boolean charTyped(CharacterEvent event) {
      boolean handled = super.charTyped(event);
      if (handled) {
         this.updateSettings();
      }

      return handled;
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      return super.mouseClicked(event, doubleClick);
   }

   public boolean keyPressed(KeyEvent event) {
      boolean handled = super.keyPressed(event);
      if (handled) {
         this.updateSettings();
      }

      return handled;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float partial) {
      super.extractRenderState(graphics, mx, my, partial);
      this.drawRightAligned(graphics, this.font, this.tc.getString(), this.currentX, 40, 14737632);
      if (MahouSettings.hasPrimary(this.spell)) {
         for (int i = 0; i < this.rgb.length; i++) {
            if (this.rgb[i] != null) {
               this.drawStringForMessage(graphics, this.rgb[i]);
            }
         }
      }

      if (MahouSettings.hasSecondary(this.spell)) {
         for (int i = 0; i < this.rgb2.length; i++) {
            if (this.rgb2[i] != null) {
               this.drawStringForMessage(graphics, this.rgb2[i]);
            }
         }
      }
   }

   private void renderDirtBackground(GuiGraphicsExtractor graphics) {
   }

   public void drawStringForMessage(GuiGraphicsExtractor graphics, MahouEditBox w) {
      int offset1 = -3;
      this.drawRightAligned(graphics, this.font, w.getMessage().getString(), w.getX() + offset1, w.getY() + w.getHeight() / 4, 14737632);
   }

   public void drawRightAligned(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int color) {
      graphics.text(font, text, x - font.width(text), y, ARGB.opaque(color), true);
   }

   public void updateSettings() {
      List<Integer> lst = new ArrayList<>();
      if (MahouSettings.hasPrimary(this.spell)) {
         for (int i = 0; i < this.rgb.length; i++) {
            if (this.rgb[i] != null) {
               lst.add(this.parseIntLimit(this.rgb[i].getValue(), 0, 255));
            }
         }
      }

      if (MahouSettings.hasSecondary(this.spell)) {
         for (int i = 0; i < this.rgb2.length; i++) {
            if (this.rgb2[i] != null) {
               lst.add(this.parseIntLimit(this.rgb2[i].getValue(), 0, 255));
            }
         }
      }

      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new SettingsUpdatePacket(this.spell.ordinal(), lst));
      ISettingsMahou settings = Utils.getSettingsMahou(Minecraft.getInstance().player);
      settings.setIntSettings(this.spell.ordinal(), lst);
   }

   public int parseIntLimit(String s, int bottom, int top) {
      try {
         return Math.min(top, Math.max(Integer.parseInt(s), bottom));
      } catch (Exception e) {
         return 0;
      }
   }

   public float parseFloatLimit(String s, float bottom, float top) {
      try {
         return Math.min(top, Math.max(Float.parseFloat(s), bottom));
      } catch (Exception e) {
         return 0.0F;
      }
   }

   public void onClose() {
      this.updateSettings();
      Minecraft.getInstance().setScreen(new PlayerSettingsGUI(Minecraft.getInstance().level,Minecraft.getInstance().player.getUUID()));
   }
}
