package stepsword.mahoutsukai.render.gui;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;

public class PlayerSettingsGUI extends Screen {
   Level world;
   UUID uuid;
   ButtonList buttons;

   public PlayerSettingsGUI(Level w, UUID u) {
      super(Component.translatable("mahoutsukai.settings.screen"));
      this.world = w;
      this.uuid = u;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
   }

   protected void init() {
      int top = 30;
      int buttonheight = 24;
      this.buttons = new ButtonList(Minecraft.getInstance(), this.width, this.height, top, buttonheight, buttonheight);
      int n = 0;
      this.addColorSettingButton("mahoutsukai.settings.rho_aias", MahouSettings.Spell.RHO_AIAS);
      this.addColorSettingButton("mahoutsukai.settings.gandr", MahouSettings.Spell.GANDR);
      this.addColorSettingButton("mahoutsukai.settings.treasury_weapons", MahouSettings.Spell.TREASURY_WEAPONS);
      this.addColorSettingButton("mahoutsukai.settings.treasury_chains", MahouSettings.Spell.TREASURY_CHAINS);
      this.addColorSettingButton("mahoutsukai.settings.morgan", MahouSettings.Spell.MORGAN);
      this.addColorSettingButton("mahoutsukai.settings.morgan_ball", MahouSettings.Spell.MORGAN_BALL);
      this.addColorSettingButton("mahoutsukai.settings.mystic_staff_big", MahouSettings.Spell.MYSTIC_STAFF_BIG);
      this.addColorSettingButton("mahoutsukai.settings.mystic_staff_aoe", MahouSettings.Spell.MYSTIC_STAFF_AOE);
      this.addColorSettingButton("mahoutsukai.settings.mystic_staff_beam", MahouSettings.Spell.MYSTIC_STAFF_BEAM);
      this.addColorSettingButton("mahoutsukai.settings.weapon_projectile", MahouSettings.Spell.WEAPON_PROJECTILE);
      this.addColorSettingButton("mahoutsukai.settings.nobu", MahouSettings.Spell.NOBU);
      this.addColorSettingButton("mahoutsukai.settings.william_book", MahouSettings.Spell.WILLIAM_BOOK);
      this.addColorSettingButton("mahoutsukai.settings.william_magic", MahouSettings.Spell.WILLIAM_MAGIC);
      this.addColorSettingButton("mahoutsukai.settings.rlyeh_book", MahouSettings.Spell.RLYEH_BOOK);
      this.addColorSettingButton("mahoutsukai.settings.rlyeh_magic", MahouSettings.Spell.RLYEH_MAGIC);
      this.addRenderableWidget(this.buttons);
      super.init();
   }

   public void addColorSettingButton(String s, MahouSettings.Spell spell) {
      SpellSettingGUI gui = new SpellSettingGUI(Component.translatable(s), this.font, spell);
      if (MahouSettings.primaryColor.contains(spell)) {
         gui = gui.createPrimaryColorSetting();
      }

      if (MahouSettings.secondaryColor.contains(spell)) {
         gui = gui.createSecondaryColorSetting();
      }

      this.addButton(s, 24, gui);
   }

   public void addButton(String translation, int h, final SpellSettingGUI gui) {
      this.buttons.addEntry(this.buttons.new ButtonListEntry(new Builder(Component.translatable(translation), new OnPress() {
         public void onPress(Button button) {
            Minecraft.getInstance().setScreen(gui);
         }
      }).pos(0, 0).size(60, h).build()));
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      return this.buttons.mouseClicked(event, doubleClick) || super.mouseClicked(event, doubleClick);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
      return this.buttons.mouseScrolled(mouseX, mouseY, deltaX, deltaY) || super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
   }

   public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
      return this.buttons.mouseDragged(event, dragX, dragY) || super.mouseDragged(event, dragX, dragY);
   }
}
