package stepsword.mahoutsukai.client;
import net.minecraft.client.Minecraft;
import java.awt.Color;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.networking.*;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity;
import stepsword.mahoutsukai.item.spells.mystic.MysticStaff.Bakuretsu;
import stepsword.mahoutsukai.render.particle.*;
import stepsword.mahoutsukai.sounds.BeamMovingSound;
public final class StaffClientEffects {
 private static Double oldSensitivity;
 public static void tick(){var mc=Minecraft.getInstance();boolean holding=mc.player!=null && mc.player.isUsingItem() && mc.player.getUseItem().getItem() instanceof stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff && stepsword.mahoutsukai.util.Utils.getMode(mc.player.getUseItem())==2 && mc.player.getTicksUsingItem()>12;if(holding && oldSensitivity==null){oldSensitivity=mc.options.sensitivity().get();mc.options.sensitivity().set(oldSensitivity*.34f);}else if(!holding)reset();}
 public static void reset(){if(oldSensitivity!=null){Minecraft.getInstance().options.sensitivity().set(oldSensitivity);oldSensitivity=null;}}

   public static void handleBakuretsu(BakuretsuPacket message) {
      Player player = Minecraft.getInstance().player;
      Bakuretsu bakuretsu = new Bakuretsu(message.strength, (float)message.posX, (float)message.posY, (float)message.posZ, 0.0F);
      Vec3 pos = new Vec3(bakuretsu.posX, bakuretsu.posY, bakuretsu.posZ);
      float[] colors = new float[]{message.r, message.g, message.b, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
      float[] colors2 = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
      double var10004 = bakuretsu.posX;
      double var10005 = bakuretsu.posY;
      ParticleSphereExplosion p = new ParticleSphereExplosion(Minecraft.getInstance().level, colors, var10004, var10005, bakuretsu.posZ);
      p.maxRadius = message.strength * 5;
      p.quality = 12;
      p.addDelay2(20);
      Minecraft.getInstance().particleEngine.add(p);
      bakuretsuSmokeRing(bakuretsu, colors2, message.strength, 0.125F, 0.5625F);
      bakuretsuSmokeRing(bakuretsu, colors2, message.strength, 0.5F, 0.6875F);
      bakuretsuSmokeRing(bakuretsu, colors2, message.strength, 0.875F, 0.5625F);

      for (int m = 0; m < message.strength * 1.8; m++) {
         float pitch = (float)(Math.random() * 180.0);
         float yaw = (float)(Math.random() * 360.0);
         Vec3 v = Vec3.directionFromRotation(yaw, pitch).scale(message.strength * (1.0 - Math.random() * 0.01F));
         Vec3 r1 = pos.add(v);
         p = new ParticleSphereExplosion(Minecraft.getInstance().level, colors, r1.x, r1.y, r1.z);
         p.maxRadius = (float)(message.strength * Math.random() * 0.3 + 10.0);
         p.quality = 9;
         p.addDelay((int)(Math.random() * 10.0));
         p.addDelay2((int)(Math.random() * 30.0));
         Minecraft.getInstance().particleEngine.add(p);
      }

      double motionX = player.getDeltaMovement().x;
      double motionY = player.getDeltaMovement().y;
      double motionZ = player.getDeltaMovement().z;
      motionX += message.motionX;
      motionY += message.motionY;
      motionZ += message.motionZ;
      player.setDeltaMovement(motionX, motionY, motionZ);
   }

   public static void bakuretsuSmokeRing(Bakuretsu bakuretsu, float[] colors, int strength, float height, float sizemod) {
      ParticleSmokeRing smoke = new ParticleSmokeRing(
         Minecraft.getInstance().level,
         new Color(colors[0], colors[1], colors[2]),
         bakuretsu.posX,
         bakuretsu.posY - strength / 2.0F + height * strength,
         bakuretsu.posZ
      );
      smoke.yaw = 90.0F;
      smoke.pitch = 0.0F;
      smoke.quality = 128;
      smoke.delay = 5;
      smoke.heightControl = strength * sizemod;
      smoke.startingRadius = strength * 4 * sizemod;
      smoke.earlyDeath = 1.0E-5F;
      if (strength > 20) {
         smoke.setLifetime((int)(200.0 + strength * 8 * (0.5 + Math.random() * 0.5)));
         smoke.alpha_step = 9.0F;
      }

      Minecraft.getInstance().particleEngine.add(smoke);
   }

   public static void beamHandler(BeamPacket message) {
      if (Minecraft.getInstance().level != null) {
         Entity entity = Minecraft.getInstance().level.getEntity(message.entityId);
         if (entity instanceof MysticStaffBeamMahoujinEntity) {
            BeamMovingSound s = new BeamMovingSound(Minecraft.getInstance().player, (MysticStaffBeamMahoujinEntity)entity);
            Minecraft.getInstance().getSoundManager().play(s);
         }
      }
   }

}
