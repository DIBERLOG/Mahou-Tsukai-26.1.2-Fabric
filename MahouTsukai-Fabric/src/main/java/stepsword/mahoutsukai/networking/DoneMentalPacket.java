package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;

public class DoneMentalPacket implements CustomPacketPayload {
   public static final Type<DoneMentalPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","done_mental"));
   public static final StreamCodec<ByteBuf, DoneMentalPacket> STREAM_CODEC = StreamCodec.unit(new DoneMentalPacket());


   public static void onMessage(DoneMentalPacket message, Player player) {
      if (player != null) {
         stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).setMentalDisplacementPos(null);
         if(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(serverPlayer,stepsword.mahoutsukai.util.Utils.getPlayerMahou(player));
         int range = MTConfig.MENTAL_DISPLACEMENT_RANGE * 3;
         Vec3 post = player.position().add(new Vec3(range, range, range));
         Vec3 posb = player.position().add(new Vec3(-range, -range, -range));

         for (MentalDisplacementEntity e : player.level()
            .getEntitiesOfClass(MentalDisplacementEntity.class, new AABB(posb.x, posb.y, posb.z, post.x, post.y, post.z))) {
            if (player.getUUID().equals(e.getOwnerId())) {
               e.discard();
            }
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof DoneMentalPacket;
   }

   public Type<DoneMentalPacket> type() {
      return TYPE;
   }
}
