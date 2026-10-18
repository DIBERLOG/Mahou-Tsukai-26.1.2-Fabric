package stepsword.mahoutsukai.datacomponents.gemmahou;

import java.util.Objects;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class GemMahou implements CopyComponent<GemMahou> {
   public static final com.mojang.serialization.Codec<GemMahou> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(
      instance -> instance.group(com.mojang.serialization.Codec.INT.fieldOf("storedMana").forGetter(GemMahou::getStoredMana)).apply(instance, GemMahou::new)
   );
   public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GemMahou> STREAM_CODEC =
      net.minecraft.network.codec.StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.INT, GemMahou::getStoredMana, GemMahou::new);
   private int storedMana = 0;

   public GemMahou() {
   }

   public GemMahou(int s) {
      this.storedMana = s;
   }

   public int getStoredMana() {
      return this.storedMana;
   }

   public void setStoredMana(int storedMana) {
      this.storedMana = storedMana;
   }

   public GemMahou copy() {
      return new GemMahou(this.storedMana);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.storedMana);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof GemMahou b ? b.storedMana == this.storedMana : false;
   }
}
