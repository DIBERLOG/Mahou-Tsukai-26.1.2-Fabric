package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import stepsword.mahoutsukai.item.replica.Replica;

public class ReplicaSwitchPacket implements CustomPacketPayload {
 public static final Type<ReplicaSwitchPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","replica_switch"));
   int slot;
   public static final StreamCodec<ByteBuf, ReplicaSwitchPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, p -> p.slot, ReplicaSwitchPacket::new);

   public ReplicaSwitchPacket(int s) {
      this.slot = s;
   }

   public static void handle(ReplicaSwitchPacket message, net.minecraft.server.level.ServerPlayer player) {
      handleReplicaSwitch(message, player);
   }

   public static void handleReplicaSwitch(ReplicaSwitchPacket message, Player player) {
      if(message.slot<0 || message.slot>6 || player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND))return;
      ItemStack stack = player.getMainHandItem();
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof Replica) {
         if (message.slot == 6) {
            Replica.clearDamageTypes(stack);
         } else {
            Replica.setSelectedSlot(stack, message.slot);
         }
      }
   }

   public Type<ReplicaSwitchPacket> type() {
      return TYPE;
   }
}
