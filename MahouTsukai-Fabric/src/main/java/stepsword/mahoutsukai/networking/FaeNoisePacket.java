package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class FaeNoisePacket implements CustomPacketPayload {
   public static final Type<FaeNoisePacket> TYPE = new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "fae_noise_packet"));
   public float vol;
   public float pit;
   public int x;
   public int y;
   public int z;
   public SoundEvent sound;
   public SoundSource cat;
   public static final StreamCodec<ByteBuf, FaeNoisePacket> STREAM_CODEC = new StreamCodec<ByteBuf, FaeNoisePacket>() {
      public FaeNoisePacket decode(ByteBuf buf) {
         FaeNoisePacket packet = new FaeNoisePacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, FaeNoisePacket packet) {
         packet.encode(buf);
      }
   };

   public FaeNoisePacket() {
   }

   public FaeNoisePacket(int x, int y, int z, SoundEvent sound, SoundSource cat, float vol, float pit) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.sound = sound;
      this.cat = cat;
      this.vol = vol;
      this.pit = pit;
   }

   public void decode(ByteBuf buf) {
      this.sound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.byId(buf.readInt());
      this.cat = this.readEnumValue(SoundSource.class, buf);
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.vol = buf.readFloat();
      this.pit = buf.readFloat();
   }

   public <T extends Enum<T>> T readEnumValue(Class<T> enumClass, ByteBuf buf) {
      return enumClass.getEnumConstants()[buf.readInt()];
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(BuiltInRegistries.SOUND_EVENT.getId(this.sound));
      buf.writeInt(this.cat.ordinal());
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeFloat(this.vol);
      buf.writeFloat(this.pit);
   }

   public Type<FaeNoisePacket> type() {
      return TYPE;
   }
}
