package stepsword.mahoutsukai.client;

import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;
import stepsword.mahoutsukai.networking.DoneMentalPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.potion.BoundPotion;
import stepsword.mahoutsukai.util.Utils;

public class MentalDisplacementClientEffect {
   private static final int STARTUP_INPUT_GRACE_TICKS = 5;
   private static long mentalDisplacementStartTick = Long.MIN_VALUE;
   private static boolean waitingForStartupInputRelease = false;
   public static Entity mentalPlaceholder = null;

   public static void mentalDisplacementInputUpdate(ClientInput mi, Player player) {
      boolean down = mi.keyPresses.backward();
      boolean up = mi.keyPresses.forward();
      boolean left = mi.keyPresses.left();
      boolean right = mi.keyPresses.right();
      boolean jumping = mi.keyPresses.jump();
      boolean shift = mi.keyPresses.shift();
      if (player != null && Minecraft.getInstance().getCameraEntity() instanceof MentalDisplacementEntity entity) {
         entity.updateInputs(down, up, left, right, jumping, shift);
         entity.syncLookToPlayer(player);
         if (MentalDisplacementSpellEffect.checkDistance(entity.origPos, Utils.toBlockPos(new Vec3(entity.getX(), entity.getY(), entity.getZ())))
            < MTConfig.MENTAL_DISPLACEMENT_RANGE) {
            mi.keyPresses = BoundPotion.STANDING_STILL;
         } else {
            endTravel(entity, player);
         }
      }
   }

   /** Reports the spirit's final position before the camera returns to the body. */
   private static void endTravel(MentalDisplacementEntity spirit, Player player) {
      var packet = new DoneMentalPacket(spirit);
      Minecraft.getInstance().setCameraEntity(player);
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(packet);
      mentalDisplacementStartTick = Long.MIN_VALUE;
      waitingForStartupInputRelease = false;
   }

   public static void mentalDisplacementMouseInput() {
      if (Minecraft.getInstance().getCameraEntity() instanceof MentalDisplacementEntity) {
         KeyMapping k = Minecraft.getInstance().options.keyAttack;
         if (k.consumeClick()) {
            k.setDown(false);
            if (!isSuppressingStartupInput()) {
               mentalDisplacementDisallowedAction(Minecraft.getInstance().player);
            }
         }
      }
   }

   public static void mentalDisplacementClientTick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.getCameraEntity() instanceof MentalDisplacementEntity entity && mc.player != null) {
         clearStartupInputSuppressionIfReleased();
         entity.updateInputs(
            mc.options.keyDown.isDown(),
            mc.options.keyUp.isDown(),
            mc.options.keyLeft.isDown(),
            mc.options.keyRight.isDown(),
            mc.options.keyJump.isDown(),
            mc.options.keyShift.isDown()
         );
         entity.syncLookToPlayer(mc.player);
      }
   }

   public static boolean mentalDisplacementRenderHand() {
      IMahou mahou = Utils.getPlayerMahou(Minecraft.getInstance().player);
      return mahou != null && Minecraft.getInstance().getCameraEntity() instanceof MentalDisplacementEntity;
   }

   /** Remaining controlled seconds, or -1 when the player is not travelling. */
   public static int secondsLeft() {
      if (Minecraft.getInstance().getCameraEntity() instanceof MentalDisplacementEntity spirit) {
         return Math.max(0, (spirit.ticksLeft() + 19) / 20);
      }
      return -1;
   }

   public static boolean mentalDisplacementDisallowedAction(Player player) {
      boolean ret = false;
      if (player != null && Minecraft.getInstance().getCameraEntity() instanceof MentalDisplacementEntity spirit) {
         if (isSuppressingStartupInput()) {
            return true;
         }

         endTravel(spirit, player);
         ret = true;
      }

      return ret;
   }

   public static void mentalDisplacementEntityJoin(Entity entity) {
      if (entity instanceof MentalDisplacementEntity) {
         Minecraft.getInstance().execute(() -> {
            if (!tryStartMentalDisplacementCamera(entity)) {
               Minecraft.getInstance().execute(() -> tryStartMentalDisplacementCamera(entity));
            }
         });
      }
   }

   private static boolean tryStartMentalDisplacementCamera(Entity entity) {
      if (entity instanceof MentalDisplacementEntity mentalEntity && entity.level().isClientSide() && entity.isAlive()) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && player.getUUID().equals(mentalEntity.getOwnerId())) {
            Minecraft.getInstance().setCameraEntity(entity);
            mentalEntity.origPos = Utils.toBlockPos(new Vec3(entity.getX(), entity.getY(), entity.getZ()));
            mentalEntity.player = player;
            mentalEntity.setOldPosAndRot();
            if (Minecraft.getInstance().level != null) {
               mentalDisplacementStartTick = Minecraft.getInstance().level.getGameTime();
            }

            waitingForStartupInputRelease = true;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean isSuppressingStartupInput() {
      if (Minecraft.getInstance().level == null) {
         return false;
      }

      if (mentalDisplacementStartTick != Long.MIN_VALUE && Minecraft.getInstance().level.getGameTime() - mentalDisplacementStartTick < 5L) {
         return true;
      }

      if (waitingForStartupInputRelease) {
         KeyMapping attack = Minecraft.getInstance().options.keyAttack;
         KeyMapping use = Minecraft.getInstance().options.keyUse;
         if (!attack.isDown() && !use.isDown()) {
            waitingForStartupInputRelease = false;
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private static void clearStartupInputSuppressionIfReleased() {
      if (waitingForStartupInputRelease && !Minecraft.getInstance().options.keyAttack.isDown() && !Minecraft.getInstance().options.keyUse.isDown()) {
         waitingForStartupInputRelease = false;
      }
   }
}
