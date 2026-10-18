package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper;

public class RipperUseEntityPacket implements CustomPacketPayload {
   public static final Type<RipperUseEntityPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","ripper_use_entity"));
   public InteractionHand hand = InteractionHand.MAIN_HAND;
   public int id = 0;
   public static final StreamCodec<ByteBuf, RipperUseEntityPacket> STREAM_CODEC = new StreamCodec<ByteBuf, RipperUseEntityPacket>() {
      public RipperUseEntityPacket decode(ByteBuf buf) {
         RipperUseEntityPacket packet = new RipperUseEntityPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, RipperUseEntityPacket packet) {
         packet.encode(buf);
      }
   };

   public RipperUseEntityPacket() {
   }

   public RipperUseEntityPacket(InteractionHand h, int i) {
      this.hand = h;
      this.id = i;
   }

   public void decode(ByteBuf buf) {
      this.id = buf.readInt();
      int h = buf.readInt();
      if (h >= 0 && h < InteractionHand.values().length) {
         this.hand = InteractionHand.values()[h];
      } else {
         this.hand = InteractionHand.MAIN_HAND;
      }
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeInt(this.hand.ordinal());
   }

   public static boolean handle(ServerPlayer player,int id,InteractionHand hand){
      if(!(player.getMainHandItem().getItem() instanceof TheRipper) || !(player.getOffhandItem().getItem() instanceof TheRipper) || player.isSpectator() || stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player))return false;
      Entity target=player.level().getEntity(id);
      if(!(target instanceof LivingEntity living) || !living.isAlive() || target==player || stepsword.mahoutsukai.effects.secret.GeasEffect.hasGeas(player,living))return false;
      double max=player.hasLineOfSight(target)?36:9;
      if(player.distanceToSqr(target)>=max)return false;
      player.resetLastActionTime();TheRipper.attack(player,target);return true;
   }

   public Type<RipperUseEntityPacket> type() {
      return TYPE;
   }
}
