package stepsword.mahoutsukai.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import stepsword.mahoutsukai.doom.DoomEntity;

/**
 * Local sky darkening near an active DOOM. Purely a client overlay: it never changes world time,
 * weather or the sky of other players, and it fades smoothly on detonation, on leaving the radius,
 * on death and on disconnect.
 */
public final class DoomSkyEffect {
 private static final int RANGE = 160;
 private static float intensity;
 private static float target;

 private DoomSkyEffect() {}

 public static void reset() {
  intensity = 0;
  target = 0;
 }

 public static float intensity() {
  return intensity;
 }

 public static void tick() {
  var minecraft = Minecraft.getInstance();
  var player = minecraft.player;
  float next = 0;
  if (player != null && minecraft.level != null && !player.isDeadOrDying() && !player.isSpectator()) {
   DoomEntity nearest = null;
   double best = Double.MAX_VALUE;
   for (var doom : minecraft.level.getEntitiesOfClass(DoomEntity.class, player.getBoundingBox().inflate(RANGE))) {
    double distance = doom.distanceToSqr(player);
    if (distance < best) {
     best = distance;
     nearest = doom;
    }
   }
   if (nearest != null) {
    double distance = Math.sqrt(best);
    float falloff = (float)Math.clamp(1.0 - (distance - 32.0) / (RANGE - 32.0), 0.0, 1.0);
    int age = nearest.age();
    float progress = Math.clamp(age / (float)DoomEntity.PREPARATION_TICKS, 0.0F, 1.0F);
    next = falloff * (0.30F + 0.70F * progress);
    if (age >= DoomEntity.PREPARATION_TICKS) {
     next *= Math.max(0.0F, 1.0F - (age - DoomEntity.PREPARATION_TICKS) / 60.0F);
    }
   }
  }
  target = next;
  // Fast attack, slow release, so the darkening returns smoothly instead of snapping.
  float rate = target > intensity ? 0.10F : 0.035F;
  intensity += (target - intensity) * rate;
  if (intensity < 0.004F) intensity = 0;
 }

 public static void render(GuiGraphicsExtractor graphics) {
  if (intensity <= 0 || Minecraft.getInstance().level == null) return;
  int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
  int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
  int horizon = (int)(height * 0.82F);
  int alpha=(int)(intensity*118F);
  graphics.fillGradient(0,0,width,horizon,(alpha<<24)|0x0A0214,0x000A0214);
 }
}
