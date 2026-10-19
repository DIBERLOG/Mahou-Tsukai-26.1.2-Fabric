package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;

/**
 * Ends controlled spirit travel and reports where the spirit actually stopped.
 * <p>The target is absolute, so the server re-validates it before moving the body: this packet can
 * never teleport further than the spirit itself was allowed to travel.</p>
 */
public record DoneMentalPacket(double x, double y, double z) implements CustomPacketPayload {
   public static final Type<DoneMentalPacket> TYPE = new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "done_mental"));
   public static final StreamCodec<ByteBuf, DoneMentalPacket> STREAM_CODEC = new StreamCodec<ByteBuf, DoneMentalPacket>() {
      public DoneMentalPacket decode(ByteBuf buf) {
         return new DoneMentalPacket(buf.readDouble(), buf.readDouble(), buf.readDouble());
      }

      public void encode(ByteBuf buf, DoneMentalPacket packet) {
         buf.writeDouble(packet.x);
         buf.writeDouble(packet.y);
         buf.writeDouble(packet.z);
      }
   };

   public DoneMentalPacket(net.minecraft.world.entity.Entity spirit) {
      this(spirit.getX(), spirit.getY(), spirit.getZ());
   }

   /**
    * @param message where the spirit stopped, or {@code null} for a cleanup that must not move the body
    */
   public static void onMessage(DoneMentalPacket message, Player player) {
      if (!(player instanceof net.minecraft.server.level.ServerPlayer) || !player.isAlive()) return;
      var mahou = stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);
      if (mahou == null || mahou.getMentalDisplacementPos() == null) return;
      mahou.setMentalDisplacementPos(null);
      int range = MTConfig.MENTAL_DISPLACEMENT_RANGE * 3;
      Vec3 post = player.position().add(new Vec3(range, range, range));
      Vec3 posb = player.position().add(new Vec3(-range, -range, -range));
      MentalDisplacementEntity nearest = null;
      double best = Double.MAX_VALUE;
      for (MentalDisplacementEntity e : player.level()
         .getEntitiesOfClass(MentalDisplacementEntity.class, new AABB(posb.x, posb.y, posb.z, post.x, post.y, post.z))) {
         if (!player.getUUID().equals(e.getOwnerId())) continue;
         double distance = e.position().distanceToSqr(player.position());
         if (distance < best) {
            best = distance;
            nearest = e;
         }
         e.discard();
      }
      if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
         stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(serverPlayer, mahou);
      }
      if (message != null && nearest != null) {
         MentalDisplacementSpellEffect.arrive(player, nearest.position());
      }
   }

   public Type<DoneMentalPacket> type() {
      return TYPE;
   }
}
