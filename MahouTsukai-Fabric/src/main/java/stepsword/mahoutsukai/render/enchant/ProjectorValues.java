package stepsword.mahoutsukai.render.enchant;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import stepsword.mahoutsukai.config.MTConfig;

public class ProjectorValues {
   public static final Codec<ProjectorValues> CODEC = CompoundTag.CODEC.xmap(ProjectorValues::readFromTag, ProjectorValues::writeToTag);
   public float csize = 1.0F;
   public int cr = 255;
   public int cg = 255;
   public int cb = 20;
   public int ca = 200;
   public float cx = 0.0F;
   public float cy = 0.0F;
   public float cz = 0.0F;
   public float cspeed = (float)MTConfig.PROJECTOR_DEFAULT_ROTATION_SPEED;
   public float cyawspeed = 0.0F;
   public float cpitspeed = 0.0F;
   public float cpitch = 0.0F;
   public float cyaw = 0.0F;
   public float crotation = 0.0F;
   public float cringangle = 0.0F;
   public float cpreyaw = 0.0F;
   public float cprepitch = 0.0F;
   public float corbitspeed = 0.0F;
   public float corbit = 0.0F;
   public float gif_frame = 0.0F;
   public float gif_speed = 1.0F;
   public boolean gif_layer = false;
   public boolean showCircle = true;
   public boolean showRing = true;
   public float height = 0.125F;
   public float rotationoffset = 0.0F;
   public int image = -1;
   public int runes = -1;
   public static final String csizeTAG = "CIRCLE_SIZE";
   public static final String crTAG = "CIRCLE_R";
   public static final String cgTAG = "CIRCLE_G";
   public static final String cbTAG = "CIRCLE_B";
   public static final String caTAG = "CIRCLE_A";
   public static final String cxTAG = "CIRCLE_X";
   public static final String cyTAG = "CIRCLE_Y";
   public static final String czTAG = "CIRCLE_Z";
   public static final String cspeedTAG = "CIRCLE_SPEED";
   public static final String cyspeedTAG = "CIRCLE_SPEED_YAW";
   public static final String cpspeedTAG = "CIRCLE_SPEED_PITCH";
   public static final String cpitchTAG = "CIRCLE_PITCH";
   public static final String cyawTAG = "CIRCLE_YAW";
   public static final String crotationTAG = "CIRCLE_ROTATION";
   public static final String cringangleTAG = "CIRCLE_RING_ANGLE";
   public static final String cpreyawTAG = "CIRCLE_PRE_YAW";
   public static final String cprepitchTAG = "CIRCLE_PRE_PITCH";
   public static final String corbitspeedTAG = "CIRCLE_ORBIT_SPEED";
   public static final String corbitTAG = "CIRCLE_ORBIT";
   public static final String showCircleTAG = "CIRCLE_SHOW_C";
   public static final String showRingTAG = "CIRCLE_SHOW_R";
   public static final String imageTAG = "IMAGE_LOAD";
   public static final String runesTAG = "RUNES_LOAD";
   public static final String heightTAG = "RING_HEIGHT";
   public static final String rotationOffsetTAG = "ROTATION_OFFSET";
   public static final String gifSpeedTAG = "GIF_SPEED";
   public static final String gifLayerTag = "GIF_LAYER";

   public static ProjectorValues readFromTag(CompoundTag tag) {
      ProjectorValues v = new ProjectorValues();
      v.read(tag);
      return v;
   }

   public static CompoundTag writeToTag(ProjectorValues v) {
      return v.write(new CompoundTag());
   }

   public ProjectorValues() {
   }

   public ProjectorValues(List<Integer> ints, List<Float> floats, List<Boolean> bools) {
      this.setAllFloats(new ArrayList<>(floats));
      this.setAllInts(new ArrayList<>(ints));
      this.setAllBools(new ArrayList<>(bools));
   }

   public CompoundTag write(CompoundTag compound) {
      compound.putFloat("CIRCLE_SIZE", this.csize);
      compound.putInt("CIRCLE_R", this.cr);
      compound.putInt("CIRCLE_G", this.cg);
      compound.putInt("CIRCLE_B", this.cb);
      compound.putInt("CIRCLE_A", this.ca);
      compound.putFloat("CIRCLE_X", this.cx);
      compound.putFloat("CIRCLE_Y", this.cy);
      compound.putFloat("CIRCLE_Z", this.cz);
      compound.putFloat("CIRCLE_SPEED", this.cspeed);
      compound.putFloat("CIRCLE_SPEED_PITCH", this.cpitspeed);
      compound.putFloat("CIRCLE_SPEED_YAW", this.cyawspeed);
      compound.putFloat("CIRCLE_YAW", this.cyaw);
      compound.putFloat("CIRCLE_PITCH", this.cpitch);
      compound.putFloat("CIRCLE_ROTATION", this.crotation);
      compound.putFloat("CIRCLE_RING_ANGLE", this.cringangle);
      compound.putFloat("CIRCLE_PRE_PITCH", this.cprepitch);
      compound.putFloat("CIRCLE_PRE_YAW", this.cpreyaw);
      compound.putFloat("CIRCLE_ORBIT_SPEED", this.corbitspeed);
      compound.putFloat("CIRCLE_ORBIT", this.corbit);
      compound.putBoolean("CIRCLE_SHOW_C", this.showCircle);
      compound.putBoolean("CIRCLE_SHOW_R", this.showRing);
      compound.putInt("IMAGE_LOAD", this.image);
      compound.putInt("RUNES_LOAD", this.runes);
      compound.putFloat("RING_HEIGHT", this.height);
      compound.putFloat("ROTATION_OFFSET", this.rotationoffset);
      compound.putFloat("GIF_SPEED", this.gif_speed);
      compound.putBoolean("GIF_LAYER", this.gif_layer);
      return compound;
   }

   public void read(CompoundTag compound) {
      this.csize = compound.getFloatOr("CIRCLE_SIZE", 1.0F);
      this.cr = compound.getIntOr("CIRCLE_R", 255);
      this.cg = compound.getIntOr("CIRCLE_G", 255);
      this.cb = compound.getIntOr("CIRCLE_B", 20);
      this.ca = compound.getIntOr("CIRCLE_A", 200);
      this.cx = compound.getFloatOr("CIRCLE_X", 0.0F);
      this.cy = compound.getFloatOr("CIRCLE_Y", 0.0F);
      this.cz = compound.getFloatOr("CIRCLE_Z", 0.0F);
      this.cspeed = compound.getFloatOr("CIRCLE_SPEED", 0.0F);
      this.cyawspeed = compound.getFloatOr("CIRCLE_SPEED_YAW", 0.0F);
      this.cpitspeed = compound.getFloatOr("CIRCLE_SPEED_PITCH", 0.0F);
      this.cpitch = compound.getFloatOr("CIRCLE_PITCH", 0.0F);
      this.cyaw = compound.getFloatOr("CIRCLE_YAW", 0.0F);
      this.crotation = compound.getFloatOr("CIRCLE_ROTATION", 0.0F);
      this.cringangle = compound.getFloatOr("CIRCLE_RING_ANGLE", 0.0F);
      this.showCircle = compound.getBooleanOr("CIRCLE_SHOW_C", true);
      this.showRing = compound.getBooleanOr("CIRCLE_SHOW_R", true);
      this.image = compound.getIntOr("IMAGE_LOAD", -1);
      this.runes = compound.getIntOr("RUNES_LOAD", -1);
      this.height = compound.getFloatOr("RING_HEIGHT", 0.125F);
      this.rotationoffset = compound.getFloatOr("ROTATION_OFFSET", 0.0F);
      this.cprepitch = compound.getFloatOr("CIRCLE_PRE_PITCH", 0.0F);
      this.cpreyaw = compound.getFloatOr("CIRCLE_PRE_YAW", 0.0F);
      this.corbitspeed = compound.getFloatOr("CIRCLE_ORBIT_SPEED", 0.0F);
      this.gif_speed = compound.getFloatOr("GIF_SPEED", 1.0F);
      this.gif_layer = compound.getBooleanOr("GIF_LAYER", false);
   }

   public float getCsize() {
      return this.csize;
   }

   public int getCr() {
      return this.cr;
   }

   public int getCg() {
      return this.cg;
   }

   public int getCb() {
      return this.cb;
   }

   public int getCa() {
      return this.ca;
   }

   public float getCx() {
      return this.cx;
   }

   public float getCy() {
      return this.cy;
   }

   public float getCz() {
      return this.cz;
   }

   public float getCpitch() {
      return this.cpitch;
   }

   public float getCyspeed() {
      return this.cyawspeed;
   }

   public float getCpspeed() {
      return this.cpitspeed;
   }

   public float getCspeed() {
      return this.cspeed;
   }

   public float getCyaw() {
      return this.cyaw;
   }

   public float getCringangle() {
      return this.cringangle;
   }

   public float getRotation() {
      return this.crotation;
   }

   public boolean getShowCircle() {
      return this.showCircle;
   }

   public boolean getShowRing() {
      return this.showRing;
   }

   public int getImage() {
      return this.image;
   }

   public int getRunes() {
      return this.runes;
   }

   public float getHeight() {
      return this.height;
   }

   public float getRotationOffset() {
      return this.rotationoffset;
   }

   public float getCorbitspeed() {
      return this.corbitspeed;
   }

   public float getCprepitch() {
      return this.cprepitch;
   }

   public float getCpreyaw() {
      return this.cpreyaw;
   }

   public float getGifSpeed() {
      return this.gif_speed;
   }

   public boolean getGifLayer() {
      return this.gif_layer;
   }

   public ArrayList<Float> getAllFloats() {
      return new ArrayList<>(
         Arrays.asList(
            this.cx,
            this.cy,
            this.cz,
            this.csize,
            this.cspeed,
            this.cyaw,
            this.cpitch,
            this.cringangle,
            this.height,
            this.rotationoffset,
            this.cyawspeed,
            this.cpitspeed,
            this.corbitspeed,
            this.cpreyaw,
            this.cprepitch,
            this.gif_speed
         )
      );
   }

   public void setAllFloats(ArrayList<Float> floats) {
      this.cx = floats.get(0);
      this.cy = floats.get(1);
      this.cz = floats.get(2);
      this.csize = floats.get(3);
      this.cspeed = floats.get(4);
      this.cyaw = floats.get(5);
      this.cpitch = floats.get(6);
      this.cringangle = floats.get(7);
      this.height = floats.get(8);
      this.rotationoffset = floats.get(9);
      this.cyawspeed = floats.get(10);
      this.cpitspeed = floats.get(11);
      this.corbitspeed = floats.get(12);
      this.cpreyaw = floats.get(13);
      this.cprepitch = floats.get(14);
      this.gif_speed = floats.get(15);
   }

   public ArrayList<Integer> getAllInts() {
      return new ArrayList<>(Arrays.asList(this.cr, this.cg, this.cb, this.ca, this.image, this.runes));
   }

   public void setAllInts(ArrayList<Integer> ints) {
      this.cr = ints.get(0);
      this.cg = ints.get(1);
      this.cb = ints.get(2);
      this.ca = ints.get(3);
      this.image = ints.get(4);
      this.runes = ints.get(5);
   }

   public ArrayList<Boolean> getAllBools() {
      return new ArrayList<>(Arrays.asList(this.showCircle, this.showRing, this.gif_layer));
   }

   public void setAllBools(ArrayList<Boolean> ints) {
      this.showCircle = ints.get(0);
      this.showRing = ints.get(1);
      this.gif_layer = ints.get(2);
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
      return !(obj instanceof ProjectorValues b)
         ? false
         : b.getAllFloats().equals(this.getAllFloats()) && b.getAllBools().equals(this.getAllBools()) && b.getAllInts().equals(this.getAllInts());
   }
}
