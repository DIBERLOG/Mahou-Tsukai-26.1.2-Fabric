package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import stepsword.mahoutsukai.effects.secret.GeasEffect;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.morgan.Morgan;

public class SweepAttackPacket implements CustomPacketPayload {
   public int entityId;
   public static final StreamCodec<ByteBuf, SweepAttackPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, p -> p.entityId, SweepAttackPacket::new);

   public SweepAttackPacket(Entity e) {
      this.entityId = e.getId();
   }

   public SweepAttackPacket(int i) {
      this.entityId = i;
   }

   public static final Type<SweepAttackPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","sweep_attack"));
   public static boolean canAttack(Player player,Entity target){
      if(player==null || !(target instanceof LivingEntity living) || !target.isAlive() || target==player || target.level()!=player.level() || player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND) || GeasEffect.hasGeas(player,living))return false;
      var item=player.getMainHandItem().getItem();
      if(!(item instanceof Caliburn) && !(item instanceof Morgan))return false;
      int radius=stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_SWEEP_RADIUS;
      return player.getBoundingBox().inflate(radius).intersects(target.getBoundingBox())
         && stepsword.mahoutsukai.util.EffectUtil.isLookingAtMe(living,player)
         && Math.abs(stepsword.mahoutsukai.util.EffectUtil.pointToLineDistance(target.position(),player.getEyePosition(1),player.getEyePosition(1).add(player.getLookAngle())))<stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_SWEEP_LOOK_RADIUS;
   }
   public static void doAttack(Player player, Entity entity) {
      if (canAttack(player,entity) && !player.level().isClientSide()) {
         ItemStack held = player.getMainHandItem();
         if (entity != player
            && entity instanceof LivingEntity
            && held != null
            && !held.isEmpty()
            && (held.getItem() instanceof Caliburn || held.getItem() instanceof Morgan)
            && entity.isAlive()
            && !GeasEffect.hasGeas(player, (LivingEntity)entity)) {
            player.attackStrengthTicker = 1000;
            player.attack(entity);
            held.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
         }
      }
   }

   public Type<SweepAttackPacket> type() {
      return TYPE;
   }
}
