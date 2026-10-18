package stepsword.mahoutsukai.render.gui;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.Keybinds;
import stepsword.mahoutsukai.item.replica.Replica;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.networking.ReplicaSwitchPacket;

public class ReplicaGui extends Screen {
   private int NUM_SECTORS = 7;
   int slotSelected = -1;
   ItemStack stack;
   Level world;

   public ReplicaGui(Level w, ItemStack i) {
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

            float rad = j * degPer;
            float xp = x + Mth.cos(rad) * radius;
            float yp = y + Mth.sin(rad) * radius;
            float xsp = xp - 4.0F;
            String name;
            if (j < this.NUM_SECTORS - 1) {
               if (this.stack.getItem() instanceof Replica) {
                  name = Replica.getDamageTypeForSlot(this.stack, j);
               } else {
                  name = "EMPTY";
               }
            } else {
               name = "CLEAR ALL";
            }

            int width = this.font.width(name);
            double mod = 0.6;
            float nextRad = (j + 1) * degPer;
            float x3 = x + Mth.cos(nextRad) * radius;
            float y3 = y + Mth.sin(nextRad) * radius;
            xsp = xp + x + x3 - 33.0F;
            float ysp = yp + y + y3;
            xsp /= 3.0F;
            ysp /= 3.0F;
            graphics.text(this.font, name, (int)xsp, (int)ysp, -1, false);
            mod = 0.8;
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
            ReplicaSwitchPacket message = new ReplicaSwitchPacket(slot);
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(message);
         }
      }

      ImmutableSet<KeyMapping> set = ImmutableSet.of(
         this.minecraft.options.keyUp,
         this.minecraft.options.keyLeft,
         this.minecraft.options.keyDown,
         this.minecraft.options.keyRight,
         this.minecraft.options.keyShift,
         this.minecraft.options.keySprint,
         new KeyMapping[]{this.minecraft.options.keyJump}
      );
      UnmodifiableIterator var5 = set.iterator();

      while (var5.hasNext()) {
         KeyMapping k = (KeyMapping)var5.next();
         KeyMapping.set(net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.getBoundKeyOf(k), k.isDown());
      }
   }
}
