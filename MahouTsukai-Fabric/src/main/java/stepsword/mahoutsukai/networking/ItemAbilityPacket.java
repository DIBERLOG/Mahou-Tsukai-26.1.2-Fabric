package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.potion.RagePotion;
import stepsword.mahoutsukai.util.EffectUtil;

import stepsword.mahoutsukai.item.replica.Replica;
import stepsword.mahoutsukai.entity.mahoujin.ReplicaTeleportMahoujinEntity;
public class ItemAbilityPacket implements CustomPacketPayload {
   public static final Type<ItemAbilityPacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","item_ability"));
   ItemAbilityPacket.ABILITY ability;
   public static final StreamCodec<ByteBuf, ItemAbilityPacket> STREAM_CODEC = new StreamCodec<ByteBuf, ItemAbilityPacket>() {
      public ItemAbilityPacket decode(ByteBuf buf) {
         ItemAbilityPacket packet = new ItemAbilityPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, ItemAbilityPacket packet) {
         packet.encode(buf);
      }
   };

   public ItemAbilityPacket() {
   }

   public ItemAbilityPacket(ItemAbilityPacket.ABILITY a) {
      this.ability = a;
   }

   public void decode(ByteBuf buf) {
      int n = buf.readInt();
      if (n >=0 && n < ItemAbilityPacket.ABILITY.values().length) {
         this.ability = ItemAbilityPacket.ABILITY.values()[n];
      } else {
         this.ability = ItemAbilityPacket.ABILITY.CALIBURN;
      }
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.ability.ordinal());
   }

   public static void handleNative(ItemAbilityPacket packet,ServerPlayer player){if(stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) || player.isSpectator())return;if(packet.ability==ABILITY.CALIBURN)Caliburn.handleAbilityCaliburn(player);else if(packet.ability==ABILITY.MORGAN)handleAbilityMorgan(player);else if(packet.ability==ABILITY.REPLICA)handleAbilityReplica(player);}
   public static void handleAbilityMorgan(ServerPlayer player) {
      ItemStack stack = player.getMainHandItem();
      if (stack.getItem() instanceof Morgan) {
         if (!EffectUtil.hasBuff(player, ModEffects.CM_COOLDOWN)) {
            EffectUtil.buff(player, ModEffects.RAGE, false, MTConfig.MORGAN_RAGE_TIME);
            RagePotion.notifyIfRage(player);
            EffectUtil.buff(player, ModEffects.CM_COOLDOWN, false, MTConfig.MORGAN_CALIBURN_POWER_COOLDOWN);
         } else {
            cooldownMessage(player, player.getEffect(ModEffects.CM_COOLDOWN).getDuration());
         }
      }
   }

   public static void cooldownMessage(Player player, int seconds) {
      player.sendOverlayMessage(Component.translatable("mahoutsukai.help.ability_cooldown").append(" " + seconds / 20));
   }

   public Type<ItemAbilityPacket> type() {
      return TYPE;
   }

   public enum ABILITY {
      MORGAN,
      CALIBURN,
      REPLICA;
   }
   public static void handleAbilityReplica(ServerPlayer player) {
      if (player.getUseItem().getItem() instanceof Replica) {
         if (!EffectUtil.hasBuff(player, ModEffects.CM_COOLDOWN)) {
            Vec3 teleport = Replica.getTeleportLocation(player.getUseItem());
            Identifier dim = Replica.getTeleportDimension(player.getUseItem());
            if (dim == null) {
               dim = Level.OVERWORLD.identifier();
            }

            if (teleport != null) {
               boolean allowed = true;
               if (MTConfig.REPLICA_TELEPORT_MAX_DISTANCE > 0.0
                  && teleport.distanceToSqr(player.position()) > MTConfig.REPLICA_TELEPORT_MAX_DISTANCE * MTConfig.REPLICA_TELEPORT_MAX_DISTANCE) {
                  allowed = false;
               }

               if (!MTConfig.REPLICA_TELEPORT_CROSS_DIMENSION && !dim.equals(EffectUtil.getDimension(player.level()))) {
                  allowed = false;
               }

               if (allowed && PlayerManaManager.drainMana(player, MTConfig.REPLICA_TELEPORT_MANA_COST, false, false) == MTConfig.REPLICA_TELEPORT_MANA_COST) {
                  boolean friendlyOnly = Replica.getFriendlyOnly(player.getUseItem());
                  ReplicaTeleportMahoujinEntity repte = new ReplicaTeleportMahoujinEntity(
                     player.level(), player, 0.24F, 0.94F, 1.0F, 0.8F, dim, teleport, friendlyOnly
                  );
                  repte.setPos(player.getX(), player.getY() + 0.005, player.getZ());
                  player.level().addFreshEntity(repte);
                  ReplicaTeleportMahoujinEntity repte2 = new ReplicaTeleportMahoujinEntity(
                     player.level(), player, 0.24F, 0.94F, 1.0F, 0.8F, dim, teleport, friendlyOnly, false
                  );
                  repte2.setPos(teleport.x, teleport.y + 0.005, teleport.z);
                  player.level().addFreshEntity(repte2);
                  EffectUtil.buff(player, ModEffects.CM_COOLDOWN, false, MTConfig.MORGAN_CALIBURN_POWER_COOLDOWN);
               }
            }
         } else {
            cooldownMessage(player, player.getEffect(ModEffects.CM_COOLDOWN).getDuration());
         }
      }
   }
}
