package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.Leylines;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;

/** Real registry/world checks, called only by the test mod. */
public final class MagicIntegration {
 private static void require(boolean value, String reason) { if (!value) throw new AssertionError(reason); }
 public static void run(ServerPlayer player) {
  var world = player.level();
  player.connection = new net.minecraft.server.network.ServerGamePacketListenerImpl(world.getServer(),
   new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND), player,
   net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(), false));
  player.connection.handleAcceptPlayerLoad(new net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket());
  var mahou = ((MahouHolder)player).mahoutsukai$getMahou();
  player.getInventory().clearContent();
  player.setPos(150.5, 80, 75.5);
  mahou.setMaxMana(1000);
  mahou.setStoredMana(0);
  int mageBonus=stepsword.mahoutsukai.fabric.MageProgression.regenBonus(1000);
  player.getFoodData().setFoodLevel(20);
  player.getFoodData().setSaturation(5);
  require(Leylines.nearbyLeyValue(player.blockPosition()) == 0, "Away from ley lines");
  require(PlayerManaManager.getManaRegen(player) == 1+mageBonus, "Original hunger ceiling plus ley penalty");
  PlayerManaManager.onPlayerTick(player, 99);
  require(mahou.getStoredMana() == 0, "Periodic recovery waits 100 ticks");
  PlayerManaManager.onPlayerTick(player, 100);
  require(mahou.getStoredMana() == 1+mageBonus, "Periodic recovery at 100 ticks");
  mahou.getGeas().clear();
  mahou.getGeas().add("minecraft:cow");
  mahou.getGeas().add("Daisy==MAHOU==minecraft:cow");
  mahou.getGeas().add("Item:Diamond");
  require(PlayerManaManager.getManaRegen(player) == 4+mageBonus, "Only unnamed non-item Geas contributes");
  mahou.getGeas().clear();
  MTConfig.LEY_AFFECTS_MANA = false;
  require(PlayerManaManager.getManaRegen(player) == 2+mageBonus, "Disabling ley penalty preserves hunger recovery");
  MTConfig.LEY_AFFECTS_MANA = true;
  player.setPos(0, 250, 0);
  int nearLey = Leylines.nearbyLeyValue(BlockPos.ZERO);
  require(nearLey >= 13, "Ley intersection strength can spawn fae");
  require(PlayerManaManager.getManaRegen(player) == 2+mageBonus + (int)(-1F + nearLey / 19F * 1000 * 0.01F), "Exact ley float arithmetic");
  MTConfig.LEY_DIMENSION_LIST = java.util.List.of("minecraft");
  require(PlayerManaManager.getManaRegen(player) == 2+mageBonus, "Namespace dimension blacklist suppresses ley bonus");
  MTConfig.LEY_DIMENSION_LIST = java.util.List.of();
  // Force the original random threshold, without replacing the fae spawn branch.
  MTConfig.FAE_SPAWN_RATE = 10;
  mahou.setHasMagic(true);
  mahou.setFaeSpawn(true);
  int before = world.getEntitiesOfClass(stepsword.mahoutsukai.entity.fae.FaeEntity.class, player.getBoundingBox().inflate(20)).size();
  PlayerManaManager.getManaRegen(player);
  var faeList = world.getEntitiesOfClass(stepsword.mahoutsukai.entity.fae.FaeEntity.class, player.getBoundingBox().inflate(20));
  require(faeList.size() == before + 1, "Original regen branch spawns real registered fae");
  var fae = faeList.getLast();
  require(fae.getMaxHealth() == 12 && fae.isFlying(), "Original fae attributes and flight");
  require(fae.isFood(new ItemStack(ModItems.REGISTERED.get("powdered_diamond"))) && fae.isInvisibleTo(player), "Original breeding food and sight requirement");
  MTConfig.FAE_SPAWN_RATE = 1;
  mahou.setStoredMana(200);
  ItemStack sight = new ItemStack(ModItems.REGISTERED.get("scroll_fay_sight"));
  ((SpellScroll)sight.getItem()).releaseUsing(sight, world, player, 71900);
  require(sight.isEmpty() && mahou.getStoredMana() == 150 && player.hasEffect(ModEffects.FAY_SIGHT_EYES), "Original scroll release spends mana, applies effect and consumes scroll");
  require(!fae.isInvisibleTo(player), "Fay Sight reveals real fae");
  player.removeEffect(ModEffects.FAY_SIGHT_EYES);
  ItemStack bound = new ItemStack(ModItems.REGISTERED.get("scroll_fay_sight"));
  var binding = stepsword.mahoutsukai.util.Utils.getScrollMahou(bound);
  binding.setCasterUUID(java.util.UUID.randomUUID());
  bound.set(stepsword.mahoutsukai.datacomponents.DataComponents.SCROLL_COMPONENT, binding);
  ((SpellScroll)bound.getItem()).releaseUsing(bound, world, player, 71900);
  require(!bound.isEmpty() && mahou.getStoredMana() == 150 && !player.hasEffect(ModEffects.FAY_SIGHT_EYES), "Other caster cannot spend or activate bound scroll");
  BlockPos floor = new BlockPos(16, 270, 16);
  world.setBlockAndUpdate(floor, Blocks.STONE.defaultBlockState());
  world.setBlockAndUpdate(floor.above(), Blocks.AIR.defaultBlockState());
  world.setBlockAndUpdate(floor.above(2), Blocks.AIR.defaultBlockState());
  player.setPos(16.5, 260, 16.5);
  ItemStack ascent = new ItemStack(ModItems.REGISTERED.get("scroll_ascension"));
  ((SpellScroll)ascent.getItem()).releaseUsing(ascent, world, player, 71900);
  require(ascent.isEmpty() && mahou.getStoredMana() == 135 && player.getY() == 271, "Ascension teleports above highest safe floor and costs 30");
  player.setHealth(20); player.invulnerableTime = 0;
  var dagger = new ItemStack(ModItems.dagger.get());
  player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,dagger);
  dagger.getItem().use(world,player,net.minecraft.world.InteractionHand.MAIN_HAND);
  require(player.getHealth() == 18 && player.hasEffect(ModEffects.BLEEDING), "Original dagger self-wound and bleeding");
  player.removeEffect(ModEffects.BLEEDING);
  var rule = new ItemStack(ModItems.ruleBreaker.get());
  stepsword.mahoutsukai.item.rulebreaker.RuleBreaker.setModulus(rule,7);
  stepsword.mahoutsukai.item.rulebreaker.RuleBreaker.setDesignation(rule,3);
  MTConfig.RULE_BREAKER_DURABILITY = 80;
  require(rule.getMaxDamage() == 80, "Native stack durability follows original runtime config");
  MTConfig.RULE_BREAKER_DURABILITY = 40;
  player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,rule);
  player.invulnerableTime = 0;
  rule.getItem().use(world,player,net.minecraft.world.InteractionHand.MAIN_HAND);
  require(player.getHealth() == 16 && rule.getDamageValue() == 1 && player.hasEffect(ModEffects.PROBABILITY_ALTER), "Rule Breaker self-use wounds, consumes durability and applies effect");
  require(player.getRandom() instanceof stepsword.mahoutsukai.item.rulebreaker.ProbabilityAlterRandom && player.getRandom().nextInt(100) == 3,
   "Original deterministic RNG installed");
  var pig = net.minecraft.world.entity.EntityType.PIG.create(world,net.minecraft.world.entity.EntitySpawnReason.COMMAND);
  pig.hurtServer(world,player.damageSources().playerAttack(player),1);
  require(pig.hasEffect(ModEffects.PROBABILITY_ALTER) && pig.getRandom().nextInt(100) == 3, "Native incoming damage hook applies Rule Breaker to victim");
  player.removeEffect(ModEffects.PROBABILITY_ALTER);
  stepsword.mahoutsukai.potion.ProbabilityAlterPotion.applyProbabilityAlter(player);
  require(!(player.getRandom() instanceof stepsword.mahoutsukai.item.rulebreaker.ProbabilityAlterRandom), "Effect expiry restores normal random source");
  player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
  faeList.forEach(entity -> entity.discard());
  world.setBlockAndUpdate(floor, Blocks.AIR.defaultBlockState());
  System.out.println("PASS: MAGIC_INTEGRATION: periodic regen, hunger, leylines, Geas contribution, dimension filter, real fae spawn and two original scrolls");
 }
}
