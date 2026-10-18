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
import stepsword.mahoutsukai.networking.FogProjectorUpdatePacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.tile.FogProjectorTileEntity;

public class FogProjectorScreen extends Screen {
   private MahouEditBox particle_fog_size_widget;
   private MahouEditBox particle_fog_color_r_widget;
   private MahouEditBox particle_fog_color_g_widget;
   private MahouEditBox particle_fog_color_b_widget;
   private MahouEditBox particle_fog_color_a_widget;
   private MahouEditBox particle_fog_thickness_widget;
   private MahouEditBox particle_fog_speed_widget;
   private Checkbox particle_fog_random_x_widget;
   private Checkbox particle_fog_random_y_widget;
   private Checkbox particle_fog_random_z_widget;
   private MahouEditBox particle_fog_direction_x_widget;
   private MahouEditBox particle_fog_direction_y_widget;
   private MahouEditBox particle_fog_direction_z_widget;
   private MahouEditBox particle_fog_frequency_num_widget;
   private MahouEditBox particle_fog_frequency_ticks_widget;
   private MahouEditBox camera_fog_radius_widget;
   private MahouEditBox camera_fog_color_r_widget;
   private MahouEditBox camera_fog_color_g_widget;
   private MahouEditBox camera_fog_color_b_widget;
   private MahouEditBox camera_fog_strength_widget;
   private Checkbox camera_fog_toggle_widget;
   private final ArrayList<MahouEditBox> boxes = new ArrayList<>();
   private final ArrayList<Checkbox> toggles = new ArrayList<>();
   private final ArrayList<StringWidget> labels = new ArrayList<>();
   private final ProjectorScreenScrollLayout scrollLayout = new ProjectorScreenScrollLayout();
   public static int camera_fog_strength_max = 60;
   Level world;
   BlockPos pos;

   public FogProjectorScreen(Level w, BlockPos p) {
      super(Component.literal("fogprojector.screen"));
      this.world = w;
      this.pos = p;
   }

   public void init() {
      OnValueChange onCheckboxChanged = (checkbox, value) -> this.updateTE();
      this.boxes.clear();
      this.toggles.clear();
      this.labels.clear();
      this.scrollLayout.reset();
      FogProjectorTileEntity projector = null;
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
         x2 = sw / 2 + 90;
      }

      if (this.world.getBlockEntity(this.pos) instanceof FogProjectorTileEntity) {
         projector = (FogProjectorTileEntity)this.world.getBlockEntity(this.pos);
      }

      this.particle_fog_size_widget = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 0, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_size_widget")
      );
      this.particle_fog_color_r_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 0, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_color_r_widget")
      );
      this.particle_fog_color_g_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 1, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_color_g_widget")
      );
      this.particle_fog_color_b_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 2, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_color_b_widget")
      );
      this.particle_fog_color_a_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 3, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_color_a_widget")
      );
      this.particle_fog_thickness_widget = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 1, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_thickness_widget")
      );
      this.particle_fog_speed_widget = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 2, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_speed_widget")
      );
      this.particle_fog_random_x_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.particle_fog_random_x_widget"), this.font)
         .pos(x1, startery + (distance + h) * 4)
         .maxWidth(w)
         .selected(true)
         .onValueChange(onCheckboxChanged)
         .build();
      this.particle_fog_random_y_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.particle_fog_random_y_widget"), this.font)
         .pos(x1, startery + (distance + h) * 5)
         .maxWidth(w)
         .selected(true)
         .onValueChange(onCheckboxChanged)
         .build();
      this.particle_fog_random_z_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.particle_fog_random_z_widget"), this.font)
         .pos(x1, startery + (distance + h) * 6)
         .maxWidth(w)
         .selected(true)
         .onValueChange(onCheckboxChanged)
         .build();
      this.particle_fog_direction_x_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 4, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_direction_x_widget")
      );
      this.particle_fog_direction_y_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 5, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_direction_y_widget")
      );
      this.particle_fog_direction_z_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 6, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_direction_z_widget")
      );
      this.particle_fog_frequency_num_widget = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 7, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_frequency_num_widget")
      );
      this.particle_fog_frequency_ticks_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 7, w, h, Component.translatable("mahoutsukai.fog_projector.particle_fog_frequency_ticks_widget")
      );
      this.camera_fog_radius_widget = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 9, w, h, Component.translatable("mahoutsukai.fog_projector.camera_fog_radius_widget")
      );
      this.camera_fog_color_r_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 9, w, h, Component.translatable("mahoutsukai.fog_projector.camera_fog_color_r_widget")
      );
      this.camera_fog_color_g_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 10, w, h, Component.translatable("mahoutsukai.fog_projector.camera_fog_color_g_widget")
      );
      this.camera_fog_color_b_widget = new MahouEditBox(
         this.font, x2, startery + (distance + h) * 11, w, h, Component.translatable("mahoutsukai.fog_projector.camera_fog_color_b_widget")
      );
      this.camera_fog_strength_widget = new MahouEditBox(
         this.font, x1, startery + (distance + h) * 10, w, h, Component.translatable("mahoutsukai.fog_projector.camera_fog_strength_widget")
      );
      this.camera_fog_toggle_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.camera_fog_toggle_widget"), this.font)
         .pos(x1, startery + (distance + h) * 11)
         .maxWidth(w)
         .selected(false)
         .onValueChange(onCheckboxChanged)
         .build();
      if (projector != null) {
         this.particle_fog_size_widget.setValue(projector.get_particle_fog_size() + "");
         this.particle_fog_color_r_widget.setValue(projector.get_particle_fog_color_r() + "");
         this.particle_fog_color_g_widget.setValue(projector.get_particle_fog_color_g() + "");
         this.particle_fog_color_b_widget.setValue(projector.get_particle_fog_color_b() + "");
         this.particle_fog_color_a_widget.setValue(projector.get_particle_fog_color_a() + "");
         this.particle_fog_thickness_widget.setValue(projector.get_particle_fog_thickness() + "");
         this.particle_fog_speed_widget.setValue(projector.get_particle_fog_speed() + "");
         this.particle_fog_direction_x_widget.setValue(projector.get_particle_fog_direction_x() + "");
         this.particle_fog_direction_y_widget.setValue(projector.get_particle_fog_direction_y() + "");
         this.particle_fog_direction_z_widget.setValue(projector.get_particle_fog_direction_z() + "");
         this.particle_fog_frequency_num_widget.setValue(projector.get_particle_fog_frequency_num() + "");
         this.particle_fog_frequency_ticks_widget.setValue(projector.get_particle_fog_frequency_ticks() + "");
         this.camera_fog_radius_widget.setValue(projector.get_camera_fog_radius() + "");
         this.camera_fog_color_r_widget.setValue(projector.get_camera_fog_color_r() + "");
         this.camera_fog_color_g_widget.setValue(projector.get_camera_fog_color_g() + "");
         this.camera_fog_color_b_widget.setValue(projector.get_camera_fog_color_b() + "");
         this.camera_fog_strength_widget.setValue(projector.get_camera_fog_strength() + "");
         this.particle_fog_random_x_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.particle_fog_random_x_widget"), this.font)
            .pos(x1, startery + (distance + h) * 4)
            .maxWidth(w)
            .selected(projector.get_particle_fog_random_x())
            .onValueChange(onCheckboxChanged)
            .build();
         this.particle_fog_random_y_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.particle_fog_random_y_widget"), this.font)
            .pos(x1, startery + (distance + h) * 5)
            .maxWidth(w)
            .selected(projector.get_particle_fog_random_y())
            .onValueChange(onCheckboxChanged)
            .build();
         this.particle_fog_random_z_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.particle_fog_random_z_widget"), this.font)
            .pos(x1, startery + (distance + h) * 6)
            .maxWidth(w)
            .selected(projector.get_particle_fog_random_z())
            .onValueChange(onCheckboxChanged)
            .build();
         this.camera_fog_toggle_widget = Checkbox.builder(Component.translatable("mahoutsukai.fog_projector.camera_fog_toggle_widget"), this.font)
            .pos(x1, startery + (distance + h) * 11)
            .maxWidth(w)
            .selected(projector.get_camera_fog_toggle())
            .onValueChange(onCheckboxChanged)
            .build();
      }

      this.boxes
         .addAll(
            Arrays.asList(
               this.particle_fog_size_widget,
               this.particle_fog_color_r_widget,
               this.particle_fog_color_g_widget,
               this.particle_fog_color_b_widget,
               this.particle_fog_color_a_widget,
               this.particle_fog_thickness_widget,
               this.particle_fog_speed_widget,
               this.particle_fog_direction_x_widget,
               this.particle_fog_direction_y_widget,
               this.particle_fog_direction_z_widget,
               this.particle_fog_frequency_num_widget,
               this.particle_fog_frequency_ticks_widget,
               this.camera_fog_radius_widget,
               this.camera_fog_color_r_widget,
               this.camera_fog_color_g_widget,
               this.camera_fog_color_b_widget,
               this.camera_fog_strength_widget
            )
         );
      this.toggles
         .addAll(
            Arrays.asList(
               this.particle_fog_random_x_widget, this.particle_fog_random_y_widget, this.particle_fog_random_z_widget, this.camera_fog_toggle_widget
            )
         );

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

      for (Checkbox toggle : this.toggles) {
         ret |= toggle.keyPressed(event);
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
      float particle_fog_size_value = this.parseFloatLimit(this.particle_fog_size_widget.getValue(), 0.0F, 30.0F);
      int particle_fog_color_r_value = this.parseIntLimit(this.particle_fog_color_r_widget.getValue(), 0, 255);
      int particle_fog_color_g_value = this.parseIntLimit(this.particle_fog_color_g_widget.getValue(), 0, 255);
      int particle_fog_color_b_value = this.parseIntLimit(this.particle_fog_color_b_widget.getValue(), 0, 255);
      int particle_fog_color_a_value = this.parseIntLimit(this.particle_fog_color_a_widget.getValue(), 0, 255);
      float particle_fog_thickness_value = this.parseFloatLimit(this.particle_fog_thickness_widget.getValue(), 0.0F, 1.0F);
      float particle_fog_speed_value = this.parseFloatLimit(this.particle_fog_speed_widget.getValue(), 0.0F, 255.0F);
      boolean particle_fog_random_x_value = this.particle_fog_random_x_widget.selected();
      boolean particle_fog_random_y_value = this.particle_fog_random_y_widget.selected();
      boolean particle_fog_random_z_value = this.particle_fog_random_z_widget.selected();
      float particle_fog_direction_x_value = this.parseFloatLimit(this.particle_fog_direction_x_widget.getValue(), -2.0F, 2.0F);
      float particle_fog_direction_y_value = this.parseFloatLimit(this.particle_fog_direction_y_widget.getValue(), -2.0F, 2.0F);
      float particle_fog_direction_z_value = this.parseFloatLimit(this.particle_fog_direction_z_widget.getValue(), -2.0F, 2.0F);
      int particle_fog_frequency_num_value = this.parseIntLimit(this.particle_fog_frequency_num_widget.getValue(), 0, 100);
      int particle_fog_frequency_ticks_value = this.parseIntLimit(this.particle_fog_frequency_ticks_widget.getValue(), 0, 1000000);
      float camera_fog_radius_value = this.parseFloatLimit(this.camera_fog_radius_widget.getValue(), 0.0F, (float)MTConfig.FOG_PROJECTOR_MAX_RADIUS);
      int camera_fog_color_r_value = this.parseIntLimit(this.camera_fog_color_r_widget.getValue(), 0, 255);
      int camera_fog_color_g_value = this.parseIntLimit(this.camera_fog_color_g_widget.getValue(), 0, 255);
      int camera_fog_color_b_value = this.parseIntLimit(this.camera_fog_color_b_widget.getValue(), 0, 255);
      float camera_fog_strength_value = this.parseFloatLimit(this.camera_fog_strength_widget.getValue(), 0.0F, camera_fog_strength_max);
      boolean camera_fog_toggle_value = this.camera_fog_toggle_widget.selected();
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
         new FogProjectorUpdatePacket(
            this.pos,
            particle_fog_size_value,
            particle_fog_color_r_value,
            particle_fog_color_g_value,
            particle_fog_color_b_value,
            particle_fog_color_a_value,
            particle_fog_thickness_value,
            particle_fog_speed_value,
            particle_fog_random_x_value,
            particle_fog_random_y_value,
            particle_fog_random_z_value,
            particle_fog_direction_x_value,
            particle_fog_direction_y_value,
            particle_fog_direction_z_value,
            true,
            particle_fog_frequency_num_value,
            particle_fog_frequency_ticks_value,
            camera_fog_radius_value,
            camera_fog_color_r_value,
            camera_fog_color_g_value,
            camera_fog_color_b_value,
            camera_fog_strength_value,
            camera_fog_toggle_value
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
