package stepsword.mahoutsukai.doom;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;
/** User's additional spell, not recovered Mahou Tsukai code. */
public final class DoomScroll extends SpellScroll {
 public static final int COOLDOWN = 20 * 60 * 5;
 public static final double TARGET_RANGE = 96;
 /** The first visible surface, including a mob; empty sky uses the ray endpoint. */
 public static net.minecraft.world.phys.Vec3 target(ServerPlayer player) {
  var start = player.getEyePosition();
  var hit = player.pick(TARGET_RANGE, 1, false);
  var end = hit.getLocation();
  var entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
   player.level(), player, start, end, player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1),
   e -> e.isAlive() && !e.isSpectator() && e.isPickable() && !e.isPassengerOfSameVehicle(player), 0);
  return entityHit == null ? end : entityHit.getLocation();
 }
 public DoomScroll() { super("doom"); }
 @Override public void useAction(ItemStack stack, Level level, LivingEntity entity, boolean consume) {
  if (!(entity instanceof ServerPlayer player)) return;
  long now = level.getServer().overworld().getGameTime();
  var holder = (DoomHolder)player;
  if (holder.mahoutsukai$getDoomReadyAt() > now) {
   player.sendOverlayMessage(Component.translatable("mahoutsukai.doom.cooldown", (holder.mahoutsukai$getDoomReadyAt() - now + 19) / 20));
   return;
  }
  if (!SpellScroll.matchCaster(player, Utils.getScrollMahou(stack))) return;
  var mahou = Utils.getPlayerMahou(player);
  // Personal mana only: empty players cannot cast a nuclear spell from an incidental battery.
  if (!player.isCreative() && (mahou.getStoredMana() < 100 || mahou.getStoredMana() < mahou.getMaxMana() * 0.95)) {
   player.sendOverlayMessage(Component.translatable("mahoutsukai.doom.mana"));
   return;
  }
  DoomEntity doom = new DoomEntity(stepsword.mahoutsukai.entity.ModEntities.DOOM.get(), level);
  doom.setPos(target(player));
  doom.setOwner(player.getUUID());
  if (!level.addFreshEntity(doom)) return;
  if (!player.isCreative()) {
   int cost = Math.max(1, (int)Math.ceil(mahou.getStoredMana() * 0.95));
   mahou.setStoredMana(mahou.getStoredMana() - cost);
   stepsword.mahoutsukai.mana.PlayerManaManager.updateManaCounter(player, cost);
  }
  holder.mahoutsukai$setDoomReadyAt(now + COOLDOWN);
  player.getCooldowns().addCooldown(stack, COOLDOWN);
  if (consume && (!player.isCreative() || stepsword.mahoutsukai.config.MTConfig.CREATIVE_MODE_SPELLS)) stack.shrink(1);
  level.playSound(null, doom.getX(), doom.getY(), doom.getZ(), net.minecraft.sounds.SoundEvents.ENDER_DRAGON_GROWL,
   net.minecraft.sounds.SoundSource.PLAYERS, 8, 0.55F);
  level.playSound(null, doom.getX(), doom.getY(), doom.getZ(), net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE,
   net.minecraft.sounds.SoundSource.PLAYERS, 8, 0.6F);
 }
}
