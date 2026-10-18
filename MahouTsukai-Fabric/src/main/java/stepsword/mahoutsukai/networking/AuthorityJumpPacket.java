package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.potion.WallStickPotion;
import stepsword.mahoutsukai.util.EffectUtil;

public class AuthorityJumpPacket implements CustomPacketPayload {
   public static final Type<AuthorityJumpPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","authority_jump"));
   double lookX;
   double lookY;
   double lookZ;
   long held;
   public static final StreamCodec<ByteBuf, AuthorityJumpPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.DOUBLE,
      p -> p.lookX,
      ByteBufCodecs.DOUBLE,
      p -> p.lookY,
      ByteBufCodecs.DOUBLE,
      p -> p.lookZ,
      ByteBufCodecs.VAR_LONG,
      p -> p.held,
      AuthorityJumpPacket::new
   );

   public AuthorityJumpPacket(double x, double y, double z, long h) {
      this.lookX = x;
      this.lookY = y;
      this.lookZ = z;
      this.held = h;
   }


   public static void authorityJump(AuthorityJumpPacket packet, ServerPlayer player) {
      if (packet.held>0 && packet.held<=12000 && Double.isFinite(packet.lookX) && Double.isFinite(packet.lookY) && Double.isFinite(packet.lookZ) && EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY) && !stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) && WallStickPotion.onWallOrGround(player)) {
         var look=player.getLookAngle();boop(player, Math.min((float)packet.held / 4.0F, 5.0F), -look.x, -look.y, -look.z);
         EffectUtil.buff(player, ModEffects.AUTHORITY_JUMP, false, 900, false);
      }
   }

   public static void boop(Entity entity, float strength, double x, double y, double z) {
      entity.needsSync = true;
      float f = Mth.sqrt((float)(x * x + z * z + y * y));
      double motionX = 0.0;
      double motionY = 0.0;
      double motionZ = 0.0;
      motionX -= x / f * strength;
      motionZ -= z / f * strength;
      motionY -= y / f * strength;
      entity.hurtMarked = true;
      entity.setDeltaMovement(new Vec3(motionX, motionY, motionZ));
   }

   public Type<AuthorityJumpPacket> type() {
      return TYPE;
   }
}
