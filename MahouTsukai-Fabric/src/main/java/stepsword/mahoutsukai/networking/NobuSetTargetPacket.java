package stepsword.mahoutsukai.networking;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;

import stepsword.mahoutsukai.item.nobu.Nobu;

public class NobuSetTargetPacket implements CustomPacketPayload {
 public static final Type<NobuSetTargetPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","nobu_set_target"));
   public Either<Integer, BlockPos> target = Either.left(0);
   public static final StreamCodec<ByteBuf, NobuSetTargetPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.either(ByteBufCodecs.INT, BlockPos.STREAM_CODEC), p -> p.target, NobuSetTargetPacket::new
   );

   public NobuSetTargetPacket(Either<Integer, BlockPos> target) {
      this.target = target;
   }

   public NobuSetTargetPacket(int m) {
      this.target = Either.left(m);
   }

   public NobuSetTargetPacket(BlockPos pos) {
      this.target = Either.right(pos);
   }

   public static void handle(NobuSetTargetPacket message, net.minecraft.server.level.ServerPlayer player) {
      if(player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND) || !(player.getMainHandItem().getItem() instanceof Nobu))return;
      Nobu.NobuUserStorage storage = Nobu.getStorage(player);
      if(storage==null)return;
      if (storage.target == null) {
         message.target.ifLeft(a -> {
            Entity e = player.level().getEntity(a);
            if (e instanceof net.minecraft.world.entity.LivingEntity && e.isAlive() && e!=player && player.distanceToSqr(e)<=2500 && player.hasLineOfSight(e)) {
               storage.target = Either.left(e);
               if (player.getMainHandItem().getItem() instanceof Nobu) {
                  Nobu.setSelectedTarget(player.getMainHandItem(), a);
               }
            }
         }).ifRight(b -> {if(player.position().distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(b))<=2500 && player.level().hasChunkAt(b))storage.target = Either.right(b);});
      }
   }

   public Type<NobuSetTargetPacket> type() {
      return TYPE;
   }
}
