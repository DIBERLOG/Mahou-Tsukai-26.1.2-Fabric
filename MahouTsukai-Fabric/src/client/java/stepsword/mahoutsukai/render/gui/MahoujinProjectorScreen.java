package stepsword.mahoutsukai.render.gui;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Checkbox.OnValueChange;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;

public class MahoujinProjectorScreen extends Screen {
   private MahouEditBox rvalue;
   private MahouEditBox gvalue;
   private MahouEditBox bvalue;
   private MahouEditBox avalue;
   private MahouEditBox xvalue;
   private MahouEditBox yvalue;
   private MahouEditBox zvalue;
   private MahouEditBox sizevalue;
   private MahouEditBox yawvalue;
   private MahouEditBox speedvalue;
   private MahouEditBox yspeedvalue;
   private MahouEditBox pspeedvalue;
   private MahouEditBox pitchvalue;
   private MahouEditBox ringvalue;
   private MahouEditBox imagevalue;
   private MahouEditBox runesvalue;
   private MahouEditBox ringheightvalue;
   private MahouEditBox rotationoffsetvalue;
   private MahouEditBox prepitchvalue;
   private MahouEditBox preyawvalue;
   private MahouEditBox orbitspeedvalue;
   private MahouEditBox gifspeedvalue;
   private Checkbox showcirclevalue;
   private Checkbox showringvalue;
   private Checkbox giflayervalue;
   private ArrayList<MahouEditBox> boxes = new ArrayList<>();
   private ArrayList<Checkbox> toggles = new ArrayList<>();
   private ArrayList<StringWidget> labels = new ArrayList<>();
   private final ProjectorScreenScrollLayout scrollLayout = new ProjectorScreenScrollLayout();
   Level world;
   BlockPos pos;

   public MahoujinProjectorScreen(Level w, BlockPos p) {
      super(Component.literal("projector.screen"));
      this.world = w;
      this.pos = p;
   }

   public void init() {
      OnValueChange onCheckboxChanged = (checkbox, value) -> this.updateTE();
      this.boxes.clear();
      this.toggles.clear();
      this.labels.clear();
      this.scrollLayout.reset();
      MahoujinProjectorTileEntity projector = null;
      int sw = Minecraft.getInstance().getWindow().getGuiScaledWidth();
      int x1 = 70;
      int x2 = 280;
      int startery = 4;
      int distance = 5;
      int h = 20;
      int w = 137;
      int guiwidth = 420;
      if (guiwidth <= sw) {
         x1 = (sw - guiwidth) / 2 + 60;
         x2 = sw / 2 + 60;
      }

      if (this.world.getBlockEntity(this.pos) instanceof MahoujinProjectorTileEntity) {
         projector = (MahoujinProjectorTileEntity)this.world.getBlockEntity(this.pos);
      }

      this.rvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 0, w, h, Component.translatable("mahoutsukai.projector.rvalue"));
      this.gvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 1, w, h, Component.translatable("mahoutsukai.projector.gvalue"));
      this.bvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 2, w, h, Component.translatable("mahoutsukai.projector.bvalue"));
      this.avalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 3, w, h, Component.translatable("mahoutsukai.projector.avalue"));
      this.xvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 0, w, h, Component.translatable("mahoutsukai.projector.xvalue"));
      this.yvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 1, w, h, Component.translatable("mahoutsukai.projector.yvalue"));
      this.zvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 2, w, h, Component.translatable("mahoutsukai.projector.zvalue"));
      this.sizevalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 3, w, h, Component.translatable("mahoutsukai.projector.sizevalue"));
      this.yawvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 4, w, h, Component.translatable("mahoutsukai.projector.yawvalue"));
      this.pitchvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 5, w, h, Component.translatable("mahoutsukai.projector.pitchvalue"));
      this.speedvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 4, w, h, Component.translatable("mahoutsukai.projector.speedvalue"));
      this.ringvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 5, w, h, Component.translatable("mahoutsukai.projector.ringvalue"));
      this.ringheightvalue = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 6, w, h, Component.translatable("mahoutsukai.projector.ringheightvalue")
      );
      this.rotationoffsetvalue = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 6, w, h, Component.translatable("mahoutsukai.projector.rotationoffsetvalue")
      );
      boolean showCircle = true;
      boolean showRing = true;
      boolean gifLayer = false;
      this.imagevalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 8 - 2, w, h, Component.translatable("mahoutsukai.projector.imagevalue"));
      this.runesvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 9 - 2, w, h, Component.translatable("mahoutsukai.projector.runesvalue"));
      this.yspeedvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 10, w, h, Component.translatable("mahoutsukai.projector.yspeedvalue"));
      this.pspeedvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 10, w, h, Component.translatable("mahoutsukai.projector.pspeedvalue"));
      this.prepitchvalue = new MahouEditBox(this.font, x1, startery + (distance + h) * 11, w, h, Component.translatable("mahoutsukai.projector.prepitchvalue"));
      this.preyawvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 11, w, h, Component.translatable("mahoutsukai.projector.preyawvalue"));
      this.orbitspeedvalue = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 12, w, h, Component.translatable("mahoutsukai.projector.orbitspeedvalue")
      );
      this.gifspeedvalue = new MahouEditBox(this.font, x2, startery + (distance + h) * 12, w, h, Component.translatable("mahoutsukai.projector.gifspeedvalue"));
      if (projector != null) {
         this.rvalue.setValue(projector.pv.getCr() + "");
         this.gvalue.setValue(projector.pv.getCg() + "");
         this.bvalue.setValue(projector.pv.getCb() + "");
         this.avalue.setValue(projector.pv.getCa() + "");
         this.xvalue.setValue(projector.pv.getCx() + "");
         this.yvalue.setValue(projector.pv.getCy() + "");
         this.zvalue.setValue(projector.pv.getCz() + "");
         this.sizevalue.setValue(projector.pv.getCsize() + "");
         this.yawvalue.setValue(projector.pv.getCyaw() + "");
         this.pitchvalue.setValue(projector.pv.getCpitch() + "");
         this.speedvalue.setValue(projector.pv.getCspeed() + "");
         this.yspeedvalue.setValue(projector.pv.getCyspeed() + "");
         this.pspeedvalue.setValue(projector.pv.getCpspeed() + "");
         this.ringvalue.setValue(projector.pv.getCringangle() + "");
         this.imagevalue.setValue(projector.pv.getImage() + "");
         this.runesvalue.setValue(projector.pv.getRunes() + "");
         this.ringheightvalue.setValue(projector.pv.getHeight() + "");
         this.rotationoffsetvalue.setValue(projector.pv.getRotationOffset() + "");
         this.orbitspeedvalue.setValue(projector.pv.getCorbitspeed() + "");
         this.preyawvalue.setValue(projector.pv.getCpreyaw() + "");
         this.prepitchvalue.setValue(projector.pv.getCprepitch() + "");
         this.gifspeedvalue.setValue(projector.pv.getGifSpeed() + "");
         showCircle = projector.pv.getShowCircle();
         showRing = projector.pv.getShowRing();
         gifLayer = projector.pv.getGifLayer();
      }

      this.showcirclevalue = Checkbox.builder(Component.literal("Show Circle"), this.font)
         .pos(x1, startery + (distance + h) * 7)
         .maxWidth(w)
         .selected(showCircle)
         .onValueChange(onCheckboxChanged)
         .build();
      this.showringvalue = Checkbox.builder(Component.literal("Show Ring"), this.font)
         .pos(x2, startery + (distance + h) * 7)
         .maxWidth(w)
         .selected(showRing)
         .onValueChange(onCheckboxChanged)
         .build();
      this.giflayervalue = Checkbox.builder(Component.literal("Gif Layer"), this.font)
         .pos(x1, startery + (distance + h) * 8)
         .maxWidth(w)
         .selected(gifLayer)
         .onValueChange(onCheckboxChanged)
         .build();
      this.boxes
         .addAll(
            Arrays.asList(
               this.rvalue,
               this.gvalue,
               this.bvalue,
               this.avalue,
               this.xvalue,
               this.yvalue,
               this.zvalue,
               this.sizevalue,
               this.yawvalue,
               this.pitchvalue,
               this.speedvalue,
               this.yspeedvalue,
               this.pspeedvalue,
               this.ringvalue,
               this.imagevalue,
               this.runesvalue,
               this.ringheightvalue,
               this.rotationoffsetvalue,
               this.orbitspeedvalue,
               this.preyawvalue,
               this.prepitchvalue,
               this.gifspeedvalue
            )
         );
      this.toggles.addAll(Arrays.asList(this.showcirclevalue, this.showringvalue, this.giflayervalue));

      for (MahouEditBox box : this.boxes) {
         this.addRenderableWidget(box);
         this.scrollLayout.register(box, box.getY());
      }

      for (Checkbox toggle : this.toggles) {
         this.addRenderableWidget(toggle);
         this.scrollLayout.register(toggle, toggle.getY());
      }

      this.labels.addAll(ProjectorScreenLabels.createLabels(this.font, this.boxes));

      for (StringWidget label : this.labels) {
         this.addRenderableOnly(label);
         this.scrollLayout.register(label, label.getY());
      }

      this.scrollLayout.setViewport(4, this.height - 4);
      this.scrollLayout.apply();
      super.init();
   }

   public boolean charTyped(CharacterEvent event) {
      boolean ret = false;

      for (EditBox box : this.boxes) {
         ret |= box.charTyped(event);
      }

      this.updateTE();
      return ret || super.charTyped(event);
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(KeyEvent event) {
      boolean ret = false;

      for (EditBox box : this.boxes) {
         ret |= box.keyPressed(event);
      }

      this.updateTE();
      return ret || super.keyPressed(event);
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      boolean ret = super.mouseClicked(event, doubleClick);
      this.updateTE();
      return ret;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
      if (this.scrollLayout.scrollBy(deltaY)) {
         this.scrollLayout.apply();
         return true;
      } else {
         return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
      }
   }

   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
      this.scrollLayout.drawScrollbar(graphics, this.width);
   }

   public void updateTE() {
      float offsetcap = (float)MTConfig.PROJECTOR_OFFSET_CAP;
      float sizecap = (float)MTConfig.PROJECTOR_SIZE_CAP;
      int r = this.parseIntLimit(this.rvalue.getValue(), 0, 255);
      int g = this.parseIntLimit(this.gvalue.getValue(), 0, 255);
      int b = this.parseIntLimit(this.bvalue.getValue(), 0, 255);
      int a = this.parseIntLimit(this.avalue.getValue(), 0, 255);
      float x = this.parseFloatLimit(this.xvalue.getValue(), -offsetcap, offsetcap);
      float y = this.parseFloatLimit(this.yvalue.getValue(), -offsetcap, offsetcap);
      float z = this.parseFloatLimit(this.zvalue.getValue(), -offsetcap, offsetcap);
      float size = this.parseFloatLimit(this.sizevalue.getValue(), 0.0F, sizecap);
      float speed = this.parseFloatLimit(this.speedvalue.getValue(), -360.0F, 360.0F);
      float yspeed = this.parseFloatLimit(this.yspeedvalue.getValue(), -360.0F, 360.0F);
      float pspeed = this.parseFloatLimit(this.pspeedvalue.getValue(), -360.0F, 360.0F);
      float yaw = this.parseFloatLimit(this.yawvalue.getValue(), 0.0F, 360.0F);
      float pitch = this.parseFloatLimit(this.pitchvalue.getValue(), 0.0F, 360.0F);
      float ring = this.parseFloatLimit(this.ringvalue.getValue(), 0.0F, 90.0F);
      int image = this.parseIntLimit(this.imagevalue.getValue(), -1, 255);
      int runes = this.parseIntLimit(this.runesvalue.getValue(), -1, 255);
      float height = this.parseFloatLimit(this.ringheightvalue.getValue(), -200.0F, 200.0F);
      float roffset = this.parseFloatLimit(this.rotationoffsetvalue.getValue(), -360.0F, 360.0F);
      float preyaw = this.parseFloatLimit(this.preyawvalue.getValue(), 0.0F, 360.0F);
      float prepitch = this.parseFloatLimit(this.prepitchvalue.getValue(), 0.0F, 360.0F);
      float orbitspeed = this.parseFloatLimit(this.orbitspeedvalue.getValue(), -360.0F, 360.0F);
      float gifspeed = this.parseFloatLimit(this.gifspeedvalue.getValue(), -360.0F, 360.0F);
      boolean sc = this.showcirclevalue.selected();
      boolean sr = this.showringvalue.selected();
      boolean gl = this.giflayervalue.selected();
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
         new MahoujinProjectorUpdatePacket(
            r,
            g,
            b,
            a,
            x,
            y,
            z,
            size,
            speed,
            yaw,
            pitch,
            ring,
            sc,
            sr,
            this.pos,
            image,
            runes,
            height,
            yspeed,
            pspeed,
            roffset,
            prepitch,
            preyaw,
            orbitspeed,
            gifspeed,
            gl
         )
      );
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
}
