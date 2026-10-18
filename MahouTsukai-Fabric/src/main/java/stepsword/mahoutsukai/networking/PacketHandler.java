package stepsword.mahoutsukai.networking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
public final class PacketHandler {
 public static void sendTo(ServerPlayer player,CustomPacketPayload packet){if(player.connection!=null && ServerPlayNetworking.canSend(player,packet.type()))ServerPlayNetworking.send(player,packet);}
 public static void sendTracking(Entity entity,CustomPacketPayload packet){for(var player:PlayerLookup.tracking(entity))sendTo(player,packet);}
 public static void sendTrackingBlock(net.minecraft.core.BlockPos pos,net.minecraft.world.level.Level level,CustomPacketPayload packet){if(level instanceof net.minecraft.server.level.ServerLevel world)for(var player:PlayerLookup.tracking(world,pos))sendTo(player,packet);}

}
