package stepsword.mahoutsukai.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity;

public class BeamMovingSound extends AbstractTickableSoundInstance {
   private final Player player;
   private final MysticStaffBeamMahoujinEntity beam;
   private float lastvolume = 0.0F;

   public BeamMovingSound(Player player, MysticStaffBeamMahoujinEntity beam) {
      super((SoundEvent)ModSounds.BEAM_TWINKLE.get(), SoundSource.NEUTRAL, RandomSource.create());
      this.player = player;
      this.beam = beam;
      this.attenuation = Attenuation.NONE;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.001F;
      this.lastvolume = 0.001F;
   }

   public void tick() {
      if (this.beam.isAlive()) {
         float len = this.beam.getBeamLength();
         float pt = this.beam.getRotationPitch();
         float yw = this.beam.getRotationYaw();
         Vec3 direction = Vec3.directionFromRotation(90.0F - pt, 360.0F - yw + 180.0F).normalize();
         Vec3 position = this.beam.position();
         double d = this.distanceToLine(position, position.add(direction.scale(len)), this.player.position().add(new Vec3(0.0, 1.0, 0.0)));
         this.lastvolume = this.volume;
         if (d > 128.0) {
            this.volume = 0.0F;
         } else {
            this.pitch = 0.3F + 0.4F * this.beam.getSphereSize() / 0.7F;
            this.pitch = this.pitch + this.beam.getBeamSize() * 0.4F / 0.7F;
            this.volume = (float)Math.min((this.beam.getSphereSize() * 0.1F + this.beam.getBeamSize() * 0.1F) * (4.0 / d), 1.0);
            this.volume = (float)(this.volume * MTConfig.MYSTIC_STAFF_BEAM_VOLUME_FACTOR);
         }
      } else {
         this.stop();
      }
   }

   public double distanceToLine(Vec3 v, Vec3 w, Vec3 p) {
      Vec3 VW = v.subtract(w);
      if (VW.lengthSqr() == 0.0) {
         return p.distanceTo(v);
      } else {
         Vec3 z = v.subtract(VW.scale(v.subtract(p).dot(VW) / VW.dot(VW)));
         double dv = z.distanceToSqr(v);
         double dw = z.distanceToSqr(w);
         double dvw = v.distanceToSqr(w);
         if (dv > dvw) {
            return p.distanceTo(w);
         } else {
            return dw > dvw ? p.distanceTo(v) : p.distanceTo(z);
         }
      }
   }
}
