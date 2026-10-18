package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys;

public class LongDistanceAttackPacket implements CustomPacketPayload {
   public static final Type<LongDistanceAttackPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","long_distance_attack_packet"));
   public int entityId;
   public static final StreamCodec<ByteBuf, LongDistanceAttackPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityId, LongDistanceAttackPacket::new
   );

   public LongDistanceAttackPacket(Entity e) {
      this.entityId = e.getId();
   }

   public LongDistanceAttackPacket(int i) {
      this.entityId = i;
   }

   public static void doAttack(Player player, Entity entity) {
      if (entity != null && player != null) {
         ItemStack held = player.getMainHandItem();
         if (entity != player
            && entity instanceof LivingEntity
            && held != null
            && !held.isEmpty()
            && held.getItem() instanceof ProximityProjectionKeys
            && entity.isAlive()) {
            // Runtime IDs are untrusted: recompute the original cursor selection on the server.
            var selection=new java.util.HashSet<>(java.util.Set.of(stepsword.mahoutsukai.util.Utils.MAHOU_SELECTION.WEAPON));
            if(player.level().isClientSide() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND)) return;
            var selected=stepsword.mahoutsukai.util.Utils.selectEntityNearCursor(player,stepsword.mahoutsukai.config.MTConfig.GLOBAL_LOOK_RANGE,player.level(),e->stepsword.mahoutsukai.util.Utils.getPredicate(selection,e,player),true);
            if(selected==entity) player.attack(entity);
         }
      }
   }

   public Type<LongDistanceAttackPacket> type() {
      return TYPE;
   }
}
