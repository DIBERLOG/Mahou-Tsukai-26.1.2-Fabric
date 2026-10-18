package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class AuthorityAttackPacket implements CustomPacketPayload {
   public static final Type<AuthorityAttackPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","authority_attack"));
   public int entityId;
   public static final StreamCodec<ByteBuf, AuthorityAttackPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.entityId, AuthorityAttackPacket::new
   );

   public AuthorityAttackPacket(Entity e) {
      this.entityId = e.getId();
   }

   public AuthorityAttackPacket(int i) {
      this.entityId = i;
   }


   public static void doAttack(Player player, Entity entity) {
      if (entity != null
         && player != null
         && entity != player
         && entity instanceof LivingEntity
         && EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY)
         && !EffectUtil.hasBuff((LivingEntity)entity, ModEffects.BA_COOLDOWN)
         && entity.isAlive() && !player.isSpectator() && !stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) && player.distanceToSqr(entity)<16 && player.hasLineOfSight(entity) && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpell(player.level(),player.getUUID(),entity)) {
         stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityAttackFromPlayer(player,entity);player.attack(entity);
      }
   }

   public Type<AuthorityAttackPacket> type() {
      return TYPE;
   }
}
