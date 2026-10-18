package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class AuthorityStickPacket implements CustomPacketPayload {
   public static final Type<AuthorityStickPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","authority_stick"));
   boolean stick;
   public static final StreamCodec<ByteBuf, AuthorityStickPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, p -> p.stick, AuthorityStickPacket::new
   );

   public AuthorityStickPacket(boolean stick) {
      this.stick = stick;
   }


   public static void authorityStick(AuthorityStickPacket message, ServerPlayer player) {
      if (EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY)) {
         if (message.stick) {
            EffectUtil.buff(player, ModEffects.WALL_STICK, false, 2000);
         } else {
            EffectUtil.debuff(player, ModEffects.WALL_STICK);
         }
      }
   }

   public Type<AuthorityStickPacket> type() {
      return TYPE;
   }
}
