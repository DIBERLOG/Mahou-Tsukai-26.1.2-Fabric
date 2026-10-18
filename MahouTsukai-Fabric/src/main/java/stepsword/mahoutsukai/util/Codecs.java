package stepsword.mahoutsukai.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.Utf8String;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Original collection codecs; renderer-dependent codecs remain in recovered/. */
public final class Codecs {
 public static final Codec<stepsword.mahoutsukai.render.enchant.ProjectorValues> PROJECTOR_VALUES_CODEC=com.mojang.serialization.codecs.RecordCodecBuilder.create(i->i.group(Codec.INT.listOf().fieldOf("ints").forGetter(stepsword.mahoutsukai.render.enchant.ProjectorValues::getAllInts),Codec.FLOAT.listOf().fieldOf("floats").forGetter(stepsword.mahoutsukai.render.enchant.ProjectorValues::getAllFloats),Codec.BOOL.listOf().fieldOf("bools").forGetter(stepsword.mahoutsukai.render.enchant.ProjectorValues::getAllBools)).apply(i,stepsword.mahoutsukai.render.enchant.ProjectorValues::new));
 public static final Codec<stepsword.mahoutsukai.util.FogProjectorValues> FOG_PROJECTOR_VALUES_CODEC=com.mojang.serialization.codecs.RecordCodecBuilder.create(i->i.group(Codec.INT.listOf().fieldOf("ints").forGetter(stepsword.mahoutsukai.util.FogProjectorValues::getAllInts),Codec.FLOAT.listOf().fieldOf("floats").forGetter(stepsword.mahoutsukai.util.FogProjectorValues::getAllFloats),Codec.BOOL.listOf().fieldOf("bools").forGetter(stepsword.mahoutsukai.util.FogProjectorValues::getAllBools)).apply(i,stepsword.mahoutsukai.util.FogProjectorValues::new));

    public static final Codec<HashMap<String, Boolean>> HASHMAP_STRING_BOOL_CODEC =
        Codec.unboundedMap(Codec.STRING, Codec.BOOL).xmap(HashMap::new, hm -> hm);
    public static final Codec<HashSet<String>> STRING_HASHSET_CODEC =
        Codec.STRING.listOf().xmap(HashSet::new, ArrayList::new);
   public static StreamCodec<ByteBuf, ArrayList<String>> STRING_ARRAY_LIST = new StreamCodec<ByteBuf, ArrayList<String>>() {
      public ArrayList<String> decode(ByteBuf b) {
         int sz = b.readInt();
         if(sz<0 || sz>4096)throw new IllegalArgumentException("Invalid collection length");
         ArrayList<String> ret = new ArrayList<>();

         for (int m = 0; m < sz; m++) {
            String s = Utf8String.read(b, 32767);
            ret.add(s);
         }

         return ret;
      }

      public void encode(ByteBuf b, ArrayList<String> s) {
         b.writeInt(s.size());

         for (int m = 0; m < s.size(); m++) {
            Utf8String.write(b, s.get(m), 32767);
         }
      }
   };
   public static StreamCodec<ByteBuf, Vec3> VEC3_STREAM = new StreamCodec<ByteBuf, Vec3>() {
      public Vec3 decode(ByteBuf b) {
         double x = b.readDouble();
         double y = b.readDouble();
         double z = b.readDouble();
         return new Vec3(x, y, z);
      }

      public void encode(ByteBuf b, Vec3 s) {
         b.writeDouble(s.x);
         b.writeDouble(s.y);
         b.writeDouble(s.z);
      }
   };
}
