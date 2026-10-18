package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;

import stepsword.mahoutsukai.potion.ForesightPotion;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class ButterflyHitPacket implements CustomPacketPayload {
   public static final Type<ButterflyHitPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","butterfly_hit"));
   public static final StreamCodec<ByteBuf, ButterflyHitPacket> STREAM_CODEC = StreamCodec.unit(new ButterflyHitPacket());

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ButterflyHitPacket;
   }

   public Type<ButterflyHitPacket> type() {
      return TYPE;
   }
}
