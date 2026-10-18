package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity;
import stepsword.mahoutsukai.entity.mahoujin.GunEntity;
import stepsword.mahoutsukai.item.nobu.Nobu;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class NobuBeamCreatePacket implements CustomPacketPayload {
 public static final Type<NobuBeamCreatePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","nobu_beam_create"));
   public List<Integer> eids = new ArrayList<>();
   public int fire = 0;
   public static final StreamCodec<ByteBuf, NobuBeamCreatePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT.apply(ByteBufCodecs.list()), p -> p.eids, ByteBufCodecs.INT, p -> p.fire, NobuBeamCreatePacket::new
   );

   public NobuBeamCreatePacket(List<Integer> i, int b) {
      this.eids = new ArrayList<>(i);
      this.fire = b;
   }

   public NobuBeamCreatePacket(List<Integer> i, NobuBeamCreatePacket.NobuFirePacket b) {
      this.eids = new ArrayList<>(i);
      this.fire = b.ordinal();
   }

   public static void handle(NobuBeamCreatePacket message, net.minecraft.server.level.ServerPlayer player) {
      if(player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND) || !(player.getMainHandItem().getItem() instanceof Nobu) || message.fire<0 || message.fire>=NobuFirePacket.values().length || message.eids.size()>8)return;
      if(message.fire==NobuFirePacket.CREATE_BEAM.ordinal() && (!player.isUsingItem() || Utils.getMode(player.getUseItem())!=0))return;
      if(message.fire==NobuFirePacket.FIRE_BEAM.ordinal() && (!player.isUsingItem() || Utils.getMode(player.getUseItem())!=0))return;
      Level world = player.level();
      if (message.fire == NobuBeamCreatePacket.NobuFirePacket.CREATE_BEAM.ordinal()) {
         for (Integer i : message.eids) {
            Entity e = world.getEntity(i);
            if (e instanceof net.minecraft.world.entity.LivingEntity && e.isAlive() && e!=player && player.distanceToSqr(e)<=2500 && player.hasLineOfSight(e) && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpell(player.level(),player.getUUID(),e)) {
               summonBeamAt(player, e);
            }
         }
      } else if (message.fire == NobuBeamCreatePacket.NobuFirePacket.FIRE_BEAM.ordinal()) {
         if (MTConfig.NOBU_PERSONAL_DRUM) {
            int drum = player.random.nextInt(3);
            SoundEvent sound = new SoundEvent[]{(SoundEvent)ModSounds.TAIKO1.get(), (SoundEvent)ModSounds.TAIKO2.get(), (SoundEvent)ModSounds.TAIKO3.get()}[drum];
            player.level().playSound(null, Utils.toBlockPos(player.position()), sound, SoundSource.NEUTRAL, (float)MTConfig.NOBU_DRUM_VOLUME, 1.0F);
         }

         fireBeamAt(player);
      } else if (message.fire == NobuBeamCreatePacket.NobuFirePacket.FIRE_GUN.ordinal()) {
         if (fireGuns(player)) {
            player.level()
               .playSound(
                  null, Utils.toBlockPos(player.position()), (SoundEvent)ModSounds.TAIKOFIRE.get(), SoundSource.NEUTRAL, (float)MTConfig.NOBU_DRUM_VOLUME, 1.0F
               );
         }
      } else if (message.fire == NobuBeamCreatePacket.NobuFirePacket.RAISE_ANGLE.ordinal()) {
         player.level()
            .playSound(
               null, Utils.toBlockPos(player.position()), (SoundEvent)ModSounds.TAIKOBOSS.get(), SoundSource.NEUTRAL, (float)MTConfig.NOBU_DRUM_VOLUME, 1.0F
            );
         pitchGuns(player, true);
      } else if (message.fire == NobuBeamCreatePacket.NobuFirePacket.LOWER_ANGLE.ordinal()) {
         player.level()
            .playSound(
               null, Utils.toBlockPos(player.position()), (SoundEvent)ModSounds.TAIKOBOSS.get(), SoundSource.NEUTRAL, (float)MTConfig.NOBU_DRUM_VOLUME, 1.0F
            );
         pitchGuns(player, false);
      }
   }

   public static void summonBeamAt(Player player, Entity entity) {
      for (Entity e : player.level().getEntities(player, player.getBoundingBox().inflate(4.0), a -> a instanceof GunBeamProjectileEntity)) {
         if (e instanceof GunBeamProjectileEntity beam
            && beam.getCasterUUID() != null
            && beam.getCasterUUID().equals(player.getUUID())
            && beam.getTarget() == entity.getId()) {
            return;
         }
      }

      float beamHeight = player.random.nextFloat() * 2.0F;
      Vec3 target = entity.getEyePosition();
      Vec3 caster = player.position();
      Vec3 spawn = new Vec3(caster.x, 0.8F + beamHeight + caster.y, caster.z);
      Vec3 aim = target.subtract(spawn).normalize();
      spawn = spawn.add(aim.scale(2.0)).add(aim.yRot(Math.toRadians(90.0F)).normalize().scale(player.random.nextFloat() * 4.0F - 2.0F));
      aim = target.subtract(spawn).normalize();
      float[] colors = Nobu.getColor(player, 0);
      GunBeamProjectileEntity beam = new GunBeamProjectileEntity(
         player.level(), player.getUUID(), colors[0], colors[1], colors[2], 0.6F, colors[3], colors[4], colors[5], 1.0F, 0.15F, 0.0F, entity.getId()
      );
      beam.setPos(spawn);
      beam.setRotationPitch(EffectUtil.toDegrees((float)java.lang.Math.asin(-aim.y)) + 90.0F);
      beam.setRotationYaw(EffectUtil.toDegrees((float)java.lang.Math.atan2(aim.x, aim.z)));
      player.level().addFreshEntity(beam);
   }

   public static void fireBeamAt(@NotNull Player player) {
      player.getCooldowns().addCooldown(player.getUseItem(), 20);
      player.releaseUsingItem();

      for (Entity e : player.level().getEntities(player, player.getBoundingBox().inflate(30.0), a -> a instanceof GunBeamProjectileEntity)) {
         if (e instanceof GunBeamProjectileEntity beam
            && beam.getCasterUUID() != null
            && beam.getCasterUUID().equals(player.getUUID())
            && !beam.getFired()
            && beam.owner == null) {
            Vec3 spawn = beam.position();
            if (Nobu.doShotMana(player)) {
               beam.setFired(true);
               if (beam.getTarget() > 0) {
                  Entity target = beam.level().getEntity(beam.getTarget());
                  if (target != null && target != player) {
                     Vec3 targetPos = target.getEyePosition();
                     Vec3 aim = targetPos.subtract(spawn).normalize();
                     beam.setDeltaMovement(aim.x * 5.0, aim.y * 5.0, aim.z * 5.0);
                     beam.aimAt(targetPos);
                  }
               } else {
                  beam.discard();
               }
            }
         }
      }
   }

   public static boolean fireGuns(@NotNull Player player) {
      if (Nobu.player_storage.containsKey(player.getUUID())) {
         Nobu.NobuUserStorage storage = Nobu.player_storage.get(player.getUUID());
         if (!storage.isFiring) {
            storage.isFiring = true;
            return !storage.guns.isEmpty();
         }
      }

      return false;
   }

   public static void pitchGuns(@NotNull Player player, boolean up) {
      if (Nobu.player_storage.containsKey(player.getUUID())) {
         Nobu.NobuUserStorage storage = Nobu.player_storage.get(player.getUUID());

         for (GunEntity ge : storage.guns.values()) {
            ge.adjustPitch(up);
         }
      }
   }

   public Type<NobuBeamCreatePacket> type() {
      return TYPE;
   }

   public enum NobuFirePacket {
      CREATE_BEAM,
      FIRE_BEAM,
      FIRE_GUN,
      RAISE_ANGLE,
      LOWER_ANGLE;
   }
}

