package stepsword.mahoutsukai.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public class FogProjectorValues {
   public static final String PARTICLE_FOG_SIZE = "PARTICLE_FOG_SIZE";
   public static final String PARTICLE_FOG_COLOR_R = "PARTICLE_FOG_COLOR_R";
   public static final String PARTICLE_FOG_COLOR_G = "PARTICLE_FOG_COLOR_G";
   public static final String PARTICLE_FOG_COLOR_B = "PARTICLE_FOG_COLOR_B";
   public static final String PARTICLE_FOG_COLOR_A = "PARTICLE_FOG_COLOR_A";
   public static final String PARTICLE_FOG_THICKNESS = "PARTICLE_FOG_THICKNESS";
   public static final String PARTICLE_FOG_SPEED = "PARTICLE_FOG_SPEED";
   public static final String PARTICLE_FOG_FREQUENCY_NUM = "PARTICLE_FOG_FREQUENCY_NUM";
   public static final String PARTICLE_FOG_FREQUENCY_TICKS = "PARTICLE_FOG_FREQUENCY_TICKS";
   public static final String PARTICLE_FOG_RANDOM_X = "PARTICLE_FOG_RANDOM_X";
   public static final String PARTICLE_FOG_RANDOM_Y = "PARTICLE_FOG_RANDOM_Y";
   public static final String PARTICLE_FOG_RANDOM_Z = "PARTICLE_FOG_RANDOM_Z";
   public static final String PARTICLE_FOG_DIRECTION_X = "PARTICLE_FOG_DIRECTION_X";
   public static final String PARTICLE_FOG_DIRECTION_Y = "PARTICLE_FOG_DIRECTION_Y";
   public static final String PARTICLE_FOG_DIRECTION_Z = "PARTICLE_FOG_DIRECTION_Z";
   public static final String PARTICLE_FOG_SHADERS = "PARTICLE_FOG_SHADERS";
   public static final String CAMERA_FOG_RADIUS = "CAMERA_FOG_RADIUS";
   public static final String CAMERA_FOG_COLOR_R = "CAMERA_FOG_COLOR_R";
   public static final String CAMERA_FOG_COLOR_G = "CAMERA_FOG_COLOR_G";
   public static final String CAMERA_FOG_COLOR_B = "CAMERA_FOG_COLOR_B";
   public static final String CAMERA_FOG_STRENGTH = "CAMERA_FOG_STRENGTH";
   public static final String CAMERA_FOG_TOGGLE = "CAMERA_FOG_TOGGLE";
   public float particle_fog_size = 8.0F;
   public int particle_fog_color_r = 229;
   public int particle_fog_color_g = 238;
   public int particle_fog_color_b = 229;
   public int particle_fog_color_a = 255;
   public float particle_fog_thickness = 0.07F;
   public float particle_fog_speed = 0.01F;
   public boolean particle_fog_random_x = true;
   public boolean particle_fog_random_y = true;
   public boolean particle_fog_random_z = true;
   public float particle_fog_direction_x = 0.0F;
   public float particle_fog_direction_y = 0.0F;
   public float particle_fog_direction_z = 0.0F;
   public boolean particle_fog_shaders = true;
   public int particle_fog_frequency_num = 0;
   public int particle_fog_frequency_ticks = 20;
   public float camera_fog_radius = 20.0F;
   public int camera_fog_color_r = 200;
   public int camera_fog_color_g = 200;
   public int camera_fog_color_b = 200;
   public float camera_fog_strength = 60.0F;
   public boolean camera_fog_toggle = true;
   public static final Codec<FogProjectorValues> CODEC = CompoundTag.CODEC.xmap(FogProjectorValues::readFromTag, FogProjectorValues::writeToTag);

   public static FogProjectorValues readFromTag(CompoundTag tag) {
      FogProjectorValues v = new FogProjectorValues();
      v.read(tag);
      return v;
   }

   public static CompoundTag writeToTag(FogProjectorValues v) {
      return v.write(new CompoundTag());
   }

   public FogProjectorValues() {
   }

   public FogProjectorValues(List<Integer> ints, List<Float> floats, List<Boolean> bools) {
      this.setAllInts(new ArrayList<>(ints));
      this.setAllFloats(new ArrayList<>(floats));
      this.setAllBools(new ArrayList<>(bools));
   }

   public ArrayList<Float> getAllFloats() {
      return new ArrayList<>(
         Arrays.asList(
            this.particle_fog_size,
            this.particle_fog_thickness,
            this.particle_fog_speed,
            this.particle_fog_direction_x,
            this.particle_fog_direction_y,
            this.particle_fog_direction_z,
            this.camera_fog_radius,
            this.camera_fog_strength
         )
      );
   }

   public void setAllFloats(ArrayList<Float> floats) {
      this.particle_fog_size = floats.get(0);
      this.particle_fog_thickness = floats.get(1);
      this.particle_fog_speed = floats.get(2);
      this.particle_fog_direction_x = floats.get(3);
      this.particle_fog_direction_y = floats.get(4);
      this.particle_fog_direction_z = floats.get(5);
      this.camera_fog_radius = floats.get(6);
      this.camera_fog_strength = floats.get(7);
   }

   public ArrayList<Integer> getAllInts() {
      return new ArrayList<>(
         Arrays.asList(
            this.particle_fog_color_r,
            this.particle_fog_color_g,
            this.particle_fog_color_b,
            this.particle_fog_color_a,
            this.particle_fog_frequency_num,
            this.particle_fog_frequency_ticks,
            this.camera_fog_color_r,
            this.camera_fog_color_g,
            this.camera_fog_color_b
         )
      );
   }

   public void setAllInts(ArrayList<Integer> ints) {
      this.particle_fog_color_r = ints.get(0);
      this.particle_fog_color_g = ints.get(1);
      this.particle_fog_color_b = ints.get(2);
      this.particle_fog_color_a = ints.get(3);
      this.particle_fog_frequency_num = ints.get(4);
      this.particle_fog_frequency_ticks = ints.get(5);
      this.camera_fog_color_r = ints.get(6);
      this.camera_fog_color_g = ints.get(7);
      this.camera_fog_color_b = ints.get(8);
   }

   public ArrayList<Boolean> getAllBools() {
      return new ArrayList<>(
         Arrays.asList(this.particle_fog_shaders, this.particle_fog_random_x, this.particle_fog_random_y, this.particle_fog_random_z, this.camera_fog_toggle)
      );
   }

   public void setAllBools(ArrayList<Boolean> ints) {
      this.particle_fog_shaders = ints.get(0);
      this.particle_fog_random_x = ints.get(1);
      this.particle_fog_random_y = ints.get(2);
      this.particle_fog_random_z = ints.get(3);
      this.camera_fog_toggle = ints.get(4);
   }

   public void read(CompoundTag compound) {
      this.particle_fog_size = compound.getFloatOr("PARTICLE_FOG_SIZE", 8.0F);
      this.particle_fog_color_r = compound.getIntOr("PARTICLE_FOG_COLOR_R", 229);
      this.particle_fog_color_g = compound.getIntOr("PARTICLE_FOG_COLOR_G", 238);
      this.particle_fog_color_b = compound.getIntOr("PARTICLE_FOG_COLOR_B", 229);
      this.particle_fog_color_a = compound.getIntOr("PARTICLE_FOG_COLOR_A", 255);
      this.particle_fog_thickness = compound.getFloatOr("PARTICLE_FOG_THICKNESS", 0.07F);
      this.particle_fog_speed = compound.getFloatOr("PARTICLE_FOG_SPEED", 0.01F);
      this.particle_fog_random_x = compound.getBooleanOr("PARTICLE_FOG_RANDOM_X", true);
      this.particle_fog_random_y = compound.getBooleanOr("PARTICLE_FOG_RANDOM_Y", true);
      this.particle_fog_random_z = compound.getBooleanOr("PARTICLE_FOG_RANDOM_Z", true);
      this.particle_fog_direction_x = compound.getFloatOr("PARTICLE_FOG_DIRECTION_X", 0.0F);
      this.particle_fog_direction_y = compound.getFloatOr("PARTICLE_FOG_DIRECTION_Y", 0.0F);
      this.particle_fog_direction_z = compound.getFloatOr("PARTICLE_FOG_DIRECTION_Z", 0.0F);
      this.particle_fog_shaders = compound.getBooleanOr("PARTICLE_FOG_SHADERS", true);
      this.particle_fog_frequency_num = compound.getIntOr("PARTICLE_FOG_FREQUENCY_NUM", 0);
      this.particle_fog_frequency_ticks = compound.getIntOr("PARTICLE_FOG_FREQUENCY_TICKS", 20);
      this.camera_fog_radius = compound.getFloatOr("CAMERA_FOG_RADIUS", 20.0F);
      this.camera_fog_color_r = compound.getIntOr("CAMERA_FOG_COLOR_R", 200);
      this.camera_fog_color_g = compound.getIntOr("CAMERA_FOG_COLOR_G", 200);
      this.camera_fog_color_b = compound.getIntOr("CAMERA_FOG_COLOR_B", 200);
      this.camera_fog_strength = compound.getFloatOr("CAMERA_FOG_STRENGTH", 60.0F);
      this.camera_fog_toggle = compound.getBooleanOr("CAMERA_FOG_TOGGLE", true);
   }

   public CompoundTag write(CompoundTag compound) {
      compound.putFloat("PARTICLE_FOG_SIZE", this.particle_fog_size);
      compound.putInt("PARTICLE_FOG_COLOR_R", this.particle_fog_color_r);
      compound.putInt("PARTICLE_FOG_COLOR_G", this.particle_fog_color_g);
      compound.putInt("PARTICLE_FOG_COLOR_B", this.particle_fog_color_b);
      compound.putInt("PARTICLE_FOG_COLOR_A", this.particle_fog_color_a);
      compound.putFloat("PARTICLE_FOG_THICKNESS", this.particle_fog_thickness);
      compound.putFloat("PARTICLE_FOG_SPEED", this.particle_fog_speed);
      compound.putBoolean("PARTICLE_FOG_RANDOM_X", this.particle_fog_random_x);
      compound.putBoolean("PARTICLE_FOG_RANDOM_Y", this.particle_fog_random_y);
      compound.putBoolean("PARTICLE_FOG_RANDOM_Z", this.particle_fog_random_z);
      compound.putFloat("PARTICLE_FOG_DIRECTION_X", this.particle_fog_direction_x);
      compound.putFloat("PARTICLE_FOG_DIRECTION_Y", this.particle_fog_direction_y);
      compound.putFloat("PARTICLE_FOG_DIRECTION_Z", this.particle_fog_direction_z);
      compound.putBoolean("PARTICLE_FOG_SHADERS", this.particle_fog_shaders);
      compound.putInt("PARTICLE_FOG_FREQUENCY_NUM", this.particle_fog_frequency_num);
      compound.putInt("PARTICLE_FOG_FREQUENCY_TICKS", this.particle_fog_frequency_ticks);
      compound.putFloat("CAMERA_FOG_RADIUS", this.camera_fog_radius);
      compound.putInt("CAMERA_FOG_COLOR_R", this.camera_fog_color_r);
      compound.putInt("CAMERA_FOG_COLOR_G", this.camera_fog_color_g);
      compound.putInt("CAMERA_FOG_COLOR_B", this.camera_fog_color_b);
      compound.putFloat("CAMERA_FOG_STRENGTH", this.camera_fog_strength);
      compound.putBoolean("CAMERA_FOG_TOGGLE", this.camera_fog_toggle);
      return compound;
   }

   public float get_particle_fog_size() {
      return this.particle_fog_size;
   }

   public void set_particle_fog_size(float particle_fog_size) {
      this.particle_fog_size = particle_fog_size;
   }

   public int get_particle_fog_color_r() {
      return this.particle_fog_color_r;
   }

   public void set_particle_fog_color_r(int particle_fog_color_r) {
      this.particle_fog_color_r = particle_fog_color_r;
   }

   public int get_particle_fog_color_g() {
      return this.particle_fog_color_g;
   }

   public void set_particle_fog_color_g(int particle_fog_color_g) {
      this.particle_fog_color_g = particle_fog_color_g;
   }

   public int get_particle_fog_color_b() {
      return this.particle_fog_color_b;
   }

   public void set_particle_fog_color_b(int particle_fog_color_b) {
      this.particle_fog_color_b = particle_fog_color_b;
   }

   public int get_particle_fog_color_a() {
      return this.particle_fog_color_a;
   }

   public void set_particle_fog_color_a(int particle_fog_color_a) {
      this.particle_fog_color_a = particle_fog_color_a;
   }

   public float get_particle_fog_thickness() {
      return this.particle_fog_thickness;
   }

   public void set_particle_fog_thickness(float particle_fog_thickness) {
      this.particle_fog_thickness = particle_fog_thickness;
   }

   public float get_particle_fog_speed() {
      return this.particle_fog_speed;
   }

   public void set_particle_fog_speed(float particle_fog_speed) {
      this.particle_fog_speed = particle_fog_speed;
   }

   public boolean get_particle_fog_random_x() {
      return this.particle_fog_random_x;
   }

   public void set_particle_fog_random_x(boolean particle_fog_random_x) {
      this.particle_fog_random_x = particle_fog_random_x;
   }

   public boolean get_particle_fog_random_y() {
      return this.particle_fog_random_y;
   }

   public void set_particle_fog_random_y(boolean particle_fog_random_y) {
      this.particle_fog_random_y = particle_fog_random_y;
   }

   public boolean get_particle_fog_random_z() {
      return this.particle_fog_random_z;
   }

   public void set_particle_fog_random_z(boolean particle_fog_random_z) {
      this.particle_fog_random_z = particle_fog_random_z;
   }

   public float get_particle_fog_direction_x() {
      return this.particle_fog_direction_x;
   }

   public void set_particle_fog_direction_x(float particle_fog_direction_x) {
      this.particle_fog_direction_x = particle_fog_direction_x;
   }

   public float get_particle_fog_direction_y() {
      return this.particle_fog_direction_y;
   }

   public void set_particle_fog_direction_y(float particle_fog_direction_y) {
      this.particle_fog_direction_y = particle_fog_direction_y;
   }

   public float get_particle_fog_direction_z() {
      return this.particle_fog_direction_z;
   }

   public void set_particle_fog_direction_z(float particle_fog_direction_z) {
      this.particle_fog_direction_z = particle_fog_direction_z;
   }

   public boolean get_particle_fog_shaders() {
      return this.particle_fog_shaders;
   }

   public void set_particle_fog_shaders(boolean particle_fog_shaders) {
      this.particle_fog_shaders = particle_fog_shaders;
   }

   public int get_particle_fog_frequency_num() {
      return this.particle_fog_frequency_num;
   }

   public void set_particle_fog_frequency_num(int particle_fog_frequency_num) {
      this.particle_fog_frequency_num = particle_fog_frequency_num;
   }

   public int get_particle_fog_frequency_ticks() {
      return this.particle_fog_frequency_ticks;
   }

   public void set_particle_fog_frequency_ticks(int particle_fog_frequency_ticks) {
      this.particle_fog_frequency_ticks = particle_fog_frequency_ticks;
   }

   public float get_camera_fog_radius() {
      return this.camera_fog_radius;
   }

   public void set_camera_fog_radius(float camera_fog_radius) {
      this.camera_fog_radius = camera_fog_radius;
   }

   public int get_camera_fog_color_r() {
      return this.camera_fog_color_r;
   }

   public void set_camera_fog_color_r(int camera_fog_color_r) {
      this.camera_fog_color_r = camera_fog_color_r;
   }

   public int get_camera_fog_color_g() {
      return this.camera_fog_color_g;
   }

   public void set_camera_fog_color_g(int camera_fog_color_g) {
      this.camera_fog_color_g = camera_fog_color_g;
   }

   public int get_camera_fog_color_b() {
      return this.camera_fog_color_b;
   }

   public void set_camera_fog_color_b(int camera_fog_color_b) {
      this.camera_fog_color_b = camera_fog_color_b;
   }

   public float get_camera_fog_strength() {
      return this.camera_fog_strength;
   }

   public void set_camera_fog_strength(float camera_fog_strength) {
      this.camera_fog_strength = camera_fog_strength;
   }

   public boolean get_camera_fog_toggle() {
      return this.camera_fog_toggle;
   }

   public void set_camera_fog_toggle(boolean camera_fog_toggle) {
      this.camera_fog_toggle = camera_fog_toggle;
   }

   public void decode(ByteBuf buf) {
      ArrayList<Integer> ints = new ArrayList<>();
      ArrayList<Float> floats = new ArrayList<>();
      ArrayList<Boolean> bools = new ArrayList<>();
      int sz = buf.readInt();

      for (int m = 0; m < sz; m++) {
         ints.add(buf.readInt());
      }

      int sz2 = buf.readInt();

      for (int m = 0; m < sz2; m++) {
         floats.add(buf.readFloat());
      }

      int sz3 = buf.readInt();

      for (int m = 0; m < sz3; m++) {
         bools.add(buf.readBoolean());
      }

      this.setAllBools(bools);
      this.setAllFloats(floats);
      this.setAllInts(ints);
   }

   public void encode(ByteBuf buf) {
      ArrayList<Integer> ints = this.getAllInts();
      ArrayList<Float> floats = this.getAllFloats();
      ArrayList<Boolean> bools = this.getAllBools();
      buf.writeInt(ints.size());

      for (int m = 0; m < ints.size(); m++) {
         buf.writeInt(ints.get(m));
      }

      buf.writeInt(floats.size());

      for (int m = 0; m < floats.size(); m++) {
         buf.writeFloat(floats.get(m));
      }

      buf.writeInt(bools.size());

      for (int m = 0; m < bools.size(); m++) {
         buf.writeBoolean(bools.get(m));
      }
   }

   @Override
   public int hashCode() {
      return 13 * this.getAllInts().hashCode() + 11 * this.getAllBools().hashCode() + 7 * this.getAllFloats().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof FogProjectorValues b)
         ? false
         : b.getAllFloats().equals(this.getAllFloats()) && b.getAllBools().equals(this.getAllBools()) && b.getAllInts().equals(this.getAllInts());
   }
}
