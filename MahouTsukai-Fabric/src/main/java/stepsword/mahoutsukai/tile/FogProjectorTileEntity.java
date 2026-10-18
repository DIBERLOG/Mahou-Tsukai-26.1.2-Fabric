package stepsword.mahoutsukai.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import stepsword.mahoutsukai.block.FogProjector;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.util.FogProjectorValues;

public class FogProjectorTileEntity extends TickingTileEntity {
   public FogProjectorValues pv = new FogProjectorValues();
   public boolean showBox = false;

   public FogProjectorTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType<?>)ModTileEntities.fogProjector.get(), p, bs);
   }

   public FogProjectorTileEntity(boolean showBox) {
      this(new BlockPos(0, 0, 0), ((FogProjector)ModBlocks.fogProjector.get()).defaultBlockState());
      this.showBox = showBox;
   }

   protected void loadAdditional(ValueInput input) {
      input.read("pv", FogProjectorValues.CODEC).ifPresent(values -> this.pv = values);
      super.loadAdditional(input);
   }

   protected void saveAdditional(ValueOutput output) {
      output.store("pv", FogProjectorValues.CODEC, this.pv);
      super.saveAdditional(output);
   }

   protected void applyImplicitComponents(DataComponentGetter components) {
      super.applyImplicitComponents(components);
      var values=components.get(DataComponents.FOG_PROJECTOR_VALUES_COMPONENT);
      this.pv=values==null?null:FogProjectorValues.readFromTag(FogProjectorValues.writeToTag(values));
      if (this.pv == null) {
         this.pv = new FogProjectorValues();
      }
   }

   protected void collectImplicitComponents(Builder builder) {
      builder.set(DataComponents.FOG_PROJECTOR_VALUES_COMPONENT, this.pv == null ? new FogProjectorValues() : FogProjectorValues.readFromTag(FogProjectorValues.writeToTag(this.pv)));
   }

   public CompoundTag writeToNBT(CompoundTag compound) {
      return this.pv.write(compound);
   }

   // Client particle emission is registered in ProjectorClient.

   public float get_particle_fog_size() {
      return this.pv.particle_fog_size;
   }

   public void set_particle_fog_size(float particle_fog_size) {
      this.pv.particle_fog_size = particle_fog_size;
   }

   public int get_particle_fog_color_r() {
      return this.pv.particle_fog_color_r;
   }

   public void set_particle_fog_color_r(int particle_fog_color_r) {
      this.pv.particle_fog_color_r = particle_fog_color_r;
   }

   public int get_particle_fog_color_g() {
      return this.pv.particle_fog_color_g;
   }

   public void set_particle_fog_color_g(int particle_fog_color_g) {
      this.pv.particle_fog_color_g = particle_fog_color_g;
   }

   public int get_particle_fog_color_b() {
      return this.pv.particle_fog_color_b;
   }

   public void set_particle_fog_color_b(int particle_fog_color_b) {
      this.pv.particle_fog_color_b = particle_fog_color_b;
   }

   public int get_particle_fog_color_a() {
      return this.pv.particle_fog_color_a;
   }

   public void set_particle_fog_color_a(int particle_fog_color_a) {
      this.pv.particle_fog_color_a = particle_fog_color_a;
   }

   public float get_particle_fog_thickness() {
      return this.pv.particle_fog_thickness;
   }

   public void set_particle_fog_thickness(float particle_fog_thickness) {
      this.pv.particle_fog_thickness = particle_fog_thickness;
   }

   public float get_particle_fog_speed() {
      return this.pv.particle_fog_speed;
   }

   public void set_particle_fog_speed(float particle_fog_speed) {
      this.pv.particle_fog_speed = particle_fog_speed;
   }

   public boolean get_particle_fog_random_x() {
      return this.pv.particle_fog_random_x;
   }

   public void set_particle_fog_random_x(boolean particle_fog_random_x) {
      this.pv.particle_fog_random_x = particle_fog_random_x;
   }

   public boolean get_particle_fog_random_y() {
      return this.pv.particle_fog_random_y;
   }

   public void set_particle_fog_random_y(boolean particle_fog_random_y) {
      this.pv.particle_fog_random_y = particle_fog_random_y;
   }

   public boolean get_particle_fog_random_z() {
      return this.pv.particle_fog_random_z;
   }

   public void set_particle_fog_random_z(boolean particle_fog_random_z) {
      this.pv.particle_fog_random_z = particle_fog_random_z;
   }

   public float get_particle_fog_direction_x() {
      return this.pv.particle_fog_direction_x;
   }

   public void set_particle_fog_direction_x(float particle_fog_direction_x) {
      this.pv.particle_fog_direction_x = particle_fog_direction_x;
   }

   public float get_particle_fog_direction_y() {
      return this.pv.particle_fog_direction_y;
   }

   public void set_particle_fog_direction_y(float particle_fog_direction_y) {
      this.pv.particle_fog_direction_y = particle_fog_direction_y;
   }

   public float get_particle_fog_direction_z() {
      return this.pv.particle_fog_direction_z;
   }

   public void set_particle_fog_direction_z(float particle_fog_direction_z) {
      this.pv.particle_fog_direction_z = particle_fog_direction_z;
   }

   public boolean get_particle_fog_shaders() {
      return this.pv.particle_fog_shaders;
   }

   public void set_particle_fog_shaders(boolean particle_fog_shaders) {
      this.pv.particle_fog_shaders = particle_fog_shaders;
   }

   public int get_particle_fog_frequency_num() {
      return this.pv.particle_fog_frequency_num;
   }

   public void set_particle_fog_frequency_num(int particle_fog_frequency_num) {
      this.pv.particle_fog_frequency_num = particle_fog_frequency_num;
   }

   public int get_particle_fog_frequency_ticks() {
      return this.pv.particle_fog_frequency_ticks;
   }

   public void set_particle_fog_frequency_ticks(int particle_fog_frequency_ticks) {
      this.pv.particle_fog_frequency_ticks = particle_fog_frequency_ticks;
   }

   public float get_camera_fog_radius() {
      return this.pv.camera_fog_radius;
   }

   public void set_camera_fog_radius(float camera_fog_radius) {
      this.pv.camera_fog_radius = camera_fog_radius;
   }

   public int get_camera_fog_color_r() {
      return this.pv.camera_fog_color_r;
   }

   public void set_camera_fog_color_r(int camera_fog_color_r) {
      this.pv.camera_fog_color_r = camera_fog_color_r;
   }

   public int get_camera_fog_color_g() {
      return this.pv.camera_fog_color_g;
   }

   public void set_camera_fog_color_g(int camera_fog_color_g) {
      this.pv.camera_fog_color_g = camera_fog_color_g;
   }

   public int get_camera_fog_color_b() {
      return this.pv.camera_fog_color_b;
   }

   public void set_camera_fog_color_b(int camera_fog_color_b) {
      this.pv.camera_fog_color_b = camera_fog_color_b;
   }

   public float get_camera_fog_strength() {
      return this.pv.camera_fog_strength;
   }

   public void set_camera_fog_strength(float camera_fog_strength) {
      this.pv.camera_fog_strength = camera_fog_strength;
   }

   public boolean get_camera_fog_toggle() {
      return this.pv.camera_fog_toggle;
   }

   public void set_camera_fog_toggle(boolean camera_fog_toggle) {
      this.pv.camera_fog_toggle = camera_fog_toggle;
   }
}
