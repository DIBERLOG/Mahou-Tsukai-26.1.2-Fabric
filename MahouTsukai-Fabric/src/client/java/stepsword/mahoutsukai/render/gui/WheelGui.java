package stepsword.mahoutsukai.render.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.Keybinds;
import stepsword.mahoutsukai.item.mysticcode.MysticCode;
import stepsword.mahoutsukai.networking.MysticCodeSwitchPacket;
import stepsword.mahoutsukai.networking.PacketHandler;

public class WheelGui extends Screen {
   private int NUM_SECTORS = 3;
   private static final int LEFT_LABEL_AXIS_PADDING = 14;
   private static final int RIGHT_LABEL_AXIS_PADDING = 2;
   int slotSelected = -1;
   ItemStack stack;
   Level world;

   public WheelGui(Level w, ItemStack i) {
      super(Component.literal(""));
      this.world = w;
      this.stack = i;
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
      int x = this.width / 2;
      int y = this.height / 2;
      int maxRadius = 80;
      double angle = mouseAngle(x, y, mouseX, mouseY);
      float step = (float) (Math.PI / 180.0);
      float degPer = (float) (Math.PI * 2) / this.NUM_SECTORS;
      this.slotSelected = -1;

      for (int i = 0; i < this.NUM_SECTORS; i++) {
         boolean mouseInSector = degPer * i < angle && angle < degPer * (i + 1);
         float radius = maxRadius;
         if (mouseInSector) {
            radius *= 1.025F;
         }

         int gs = 64;
         if (i % 2 == 0) {
            gs += 25;
         }

         int r = gs;
         int g = gs;
         int b = gs;
         int a = 102;
         if (i == 0) {
            graphics.fill(x - 3, y - 3, x + 3, y + 3, -1438629824);
         }

         if (mouseInSector) {
            this.slotSelected = i;
            b = 255;
            g = 255;
            r = 255;
         }

         WheelSectorRenderer.fillSector(graphics, x, y, degPer * i, degPer * (i + 1), Mth.floor(radius), a << 24 | r << 16 | g << 8 | b);
      }

      graphics.nextStratum();
      if (this.stack != null) {
         for (int j = 0; j < this.NUM_SECTORS; j++) {
            boolean mouseInSector = degPer * j < angle && angle < degPer * (j + 1);
            float radius = maxRadius;
            if (mouseInSector) {
               radius *= 1.025F;
            }

            String name;
            if (this.stack.getItem() instanceof MysticCode) {
               name = ((MysticCode)this.stack.getItem()).getSlotsForWheel(this.stack).get(j);
               if (name.startsWith("Scroll of the")) {
                  name = name.substring("Scroll of the".length());
               } else if (name.startsWith("Scroll of")) {
                  name = name.substring("Scroll of".length());
               }
            } else {
               name = "SLOT EMPTY";
            }

            int width = this.font.width(name);
            int verticalOffset = Mth.floor(radius * 0.42F);
            int labelX;
            int labelY;
            if (j == 1) {
               labelX = x - 14 - width;
               labelY = y - 9 / 2;
            } else {
               labelX = x + 2;
               labelY = y + (j == 2 ? -verticalOffset : verticalOffset) - 9 / 2;
            }

            graphics.text(this.font, name, labelX, labelY, -1, true);
         }
      }
   }

   private static double mouseAngle(int x, int y, int mx, int my) {
      return (Mth.atan2(my - y, mx - x) + (Math.PI * 2)) % (Math.PI * 2);
   }

   public void tick() {
      super.tick();
      if (!Keybinds.changeMysticCode.isDown() && !InputConstants.isKeyDown(Minecraft.getInstance().getWindow(),net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.getBoundKeyOf(Keybinds.changeMysticCode).getValue())) {
         this.minecraft.setScreen(null);
         if (this.slotSelected != -1) {
            int slot = this.slotSelected;
            MysticCodeSwitchPacket message = new MysticCodeSwitchPacket(slot);
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(message);
         }
      }
   }
}
