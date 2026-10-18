package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;

public class SelectiveDisplacementPacket implements CustomPacketPayload {
   public static final Type<SelectiveDisplacementPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","selective_displacement_packet"));
   public int id1 = 0;
   public int id2 = 0;
   public static final StreamCodec<ByteBuf, SelectiveDisplacementPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.id1, ByteBufCodecs.INT, p -> p.id2, SelectiveDisplacementPacket::new
   );

   public SelectiveDisplacementPacket(int i, int i2) {
      this.id1 = i;
      this.id2 = i2;
   }

   public static void handleAbilitySelectiveDisplacement(ServerPlayer player, int id, int id2) {
      if (EffectUtil.hasBuff(player, ModEffects.SELECTIVE_DISPLACEMENT) && !EffectUtil.hasBuff(player, ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN) && id != 0) {
         Entity e = player.level().getEntity(id);
         Entity e2;
         if (id2 != 0 && id2 != id) {
            e2 = player.level().getEntity(id2);
         } else {
            e2 = player;
         }

         // The client supplies runtime entity IDs: enforce the original selection AABB on the server.
         var allowed=player.getBoundingBox().inflate(MTConfig.SELECTIVE_DISPLACEMENT_RANGE);
         if(e==null || e2==null || !e.isAlive() || !e2.isAlive() || !allowed.intersects(e.getBoundingBox()) || !allowed.intersects(e2.getBoundingBox())) return;
         if (e != null && e2 != null && e != e2 && EffectUtil.getDimension(e.level()) == EffectUtil.getDimension(e2.level())) {
            double x = e.getX();
            double y = e.getY();
            double z = e.getZ();
            double x2 = e2.getX();
            double y2 = e2.getY();
            double z2 = e2.getZ();
            if (e2.getPassengers().size() > 0) {
               e2.ejectPassengers();
            }

            if (e.getPassengers().size() > 0) {
               e.ejectPassengers();
            }

            MahouTsukaiTeleporter.teleport(e, e2.getX(), e2.getY(), e2.getZ(), EffectUtil.getDimension(e.level()));
            Vec3 facing = new Vec3(x2, y2, z2).subtract(new Vec3(x, y, z));
            facing = facing.normalize();
            float pitch = (float)Math.asin(-facing.y);
            float yaw = (float)Math.atan2(facing.x, facing.z);
            if (e2 instanceof LivingEntity) {
               e2.setYRot(EffectUtil.toDegrees(-yaw));
               e2.setXRot(EffectUtil.toDegrees(pitch));
            }

            facing = facing.multiply(-1.0, -1.0, -1.0);
            float pitch2 = (float)Math.asin(-facing.y);
            float yaw2 = (float)Math.atan2(facing.x, facing.z);
            if (e instanceof LivingEntity) {
               e.setYRot(EffectUtil.toDegrees(-yaw2));
               e.setXRot(EffectUtil.toDegrees(pitch2));
            }

            MahouTsukaiTeleporter.teleport((Entity)e2, x, y, z, EffectUtil.getDimension(e2.level()));
            ((Entity)e2).hurtMarked = true;
            e.hurtMarked = true;
            e2.setOnGround(false);
            e.setOnGround(false);
            Entity target = null;
            if (e == player) {
               target = (Entity)e2;
            }

            if (e2 == player) {
               target = e;
            }

            if (target != null && ContractMahoujinTileEntity.isImmuneToSpell(player.level(), player.getUUID(), target)) {
               ModTriggers.BOOGIE_WOOGIE.trigger(player);
            }

            if (e != player && e2 != player) {
               MandatoryFun.completeFunReq(player, MandatoryFun.FUN_REQS.SWAP_TWO_THINGS);
            }

            EffectUtil.buff(player, ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN, false, MTConfig.SELECTIVE_DISPLACEMENT_COOLDOWN);
         }
      }
   }

   public Type<SelectiveDisplacementPacket> type() {
      return TYPE;
   }
}
