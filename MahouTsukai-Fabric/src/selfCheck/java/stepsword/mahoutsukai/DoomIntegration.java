package stepsword.mahoutsukai;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.doom.DoomEntity;
import stepsword.mahoutsukai.entity.ModEntities;

public final class DoomIntegration {
 private static void require(boolean value, String reason) { if (!value) throw new AssertionError(reason); }
 static ServerPlayer player(ServerLevel world, String name, net.minecraft.world.level.GameType mode, double x) {
  var profile = new com.mojang.authlib.GameProfile(UUID.randomUUID(), name);
  var player = new ServerPlayer(world.getServer(), world, profile, net.minecraft.server.level.ClientInformation.createDefault());
  player.connection = new net.minecraft.server.network.ServerGamePacketListenerImpl(world.getServer(),
   new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND), player,
   net.minecraft.server.network.CommonListenerCookie.createInitial(profile, false));
  player.connection.handleAcceptPlayerLoad(new net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket());
  player.setPos(x,250,512);
  player.setGameMode(mode);
  world.addNewPlayer(player);
  return player;
 }
 public static void run(ServerPlayer fixture) {
  ServerLevel world = fixture.level();
  Progression027Checks.runGravity(world);
  BlockPos center = new BlockPos(512, 250, 512);
  BlockPos protectedPos = center.offset(2, 0, 0);
  // Outside the dense -15..15 refill but inside the attraction erosion shell (radius up to 26):
  // one claim-protected stone and one bedrock must survive the pull phase.
  BlockPos erosionProtected = center.offset(0, 0, 18);
  BlockPos erosionBedrock = center.offset(18, 0, 0);
  for(var dense:BlockPos.betweenClosed(center.offset(-15,-15,-15),center.offset(15,15,15)))world.setBlock(dense,Blocks.AIR.defaultBlockState(),18);
  net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, entity) -> !pos.equals(protectedPos) && !pos.equals(erosionProtected));
  for (var pos : java.util.List.of(center, protectedPos, center.offset(39,0,0), center.offset(41,0,0), erosionProtected))
   world.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
  world.setBlockAndUpdate(center.offset(1,0,0), Blocks.BEDROCK.defaultBlockState());
  world.setBlockAndUpdate(erosionBedrock, Blocks.BEDROCK.defaultBlockState());
  // Cast through the actual item, not just the explosion entity constructor.
  fixture.setPos(512.5,270,512.5);
  fixture.setXRot(90); fixture.setYRot(0);
  var expectedPoint = fixture.pick(96, 1, false).getLocation();
  require(stepsword.mahoutsukai.doom.DoomScroll.target(fixture).distanceToSqr(expectedPoint) < 1e-8, "DOOM targets the exact aimed block surface");
  var aimedMob = EntityType.COW.create(world, EntitySpawnReason.COMMAND);
  aimedMob.setPos(512.5, 260, 512.5);world.addFreshEntity(aimedMob);
  var mobPoint = stepsword.mahoutsukai.doom.DoomScroll.target(fixture);
  require(aimedMob.getBoundingBox().inflate(.001).contains(mobPoint) && mobPoint.y > expectedPoint.y, "Visible aimed mob takes priority over ground behind it");
  var wall = new BlockPos(512, 266, 512);world.setBlockAndUpdate(wall, Blocks.STONE.defaultBlockState());
  require(Math.abs(stepsword.mahoutsukai.doom.DoomScroll.target(fixture).y - 267) < .001, "DOOM cannot target an entity through a solid wall");
  world.removeBlock(wall, false);aimedMob.discard();
  fixture.setXRot(-90);
  var sky = fixture.getEyePosition().add(fixture.getViewVector(1).scale(96));
  require(stepsword.mahoutsukai.doom.DoomScroll.target(fixture).distanceToSqr(sky) < 1e-8, "Empty sky places DOOM at the 96-block aim endpoint");
  fixture.setXRot(90);
  var mahou = ((stepsword.mahoutsukai.fabric.MahouHolder)fixture).mahoutsukai$getMahou();
  mahou.setMaxMana(1000); mahou.setStoredMana(1000);
  var castScroll = new ItemStack(stepsword.mahoutsukai.item.ModItems.REGISTERED.get("scroll_doom"));
  var spell = (stepsword.mahoutsukai.doom.DoomScroll)castScroll.getItem();
  ((stepsword.mahoutsukai.doom.DoomHolder)fixture).mahoutsukai$setDoomReadyAt(0);
  spell.releaseUsing(castScroll,world,fixture,71900);
  var castEntities = world.getEntitiesOfClass(DoomEntity.class, new net.minecraft.world.phys.AABB(center).inflate(30));
  require(castScroll.isEmpty() && mahou.getStoredMana() == 525 && castEntities.size() == 1, "Real DOOM scroll targets block, spends 47.5 percent and creates effect");
  require(castEntities.getFirst().position().distanceToSqr(expectedPoint) < 1e-8, "Actual cast preserves the selected point without a vertical offset");
  long deadline = ((stepsword.mahoutsukai.doom.DoomHolder)fixture).mahoutsukai$getDoomReadyAt();
  require(deadline == world.getServer().overworld().getGameTime() + 6000, "DOOM cooldown five minutes");
  var secondScroll = new ItemStack(stepsword.mahoutsukai.item.ModItems.REGISTERED.get("scroll_doom"));
  spell.releaseUsing(secondScroll,world,fixture,71900);
  require(!secondScroll.isEmpty() && world.getEntitiesOfClass(DoomEntity.class, new net.minecraft.world.phys.AABB(center).inflate(30)).size() == 1 && mahou.getStoredMana() == 525, "Cooldown prevents repeat spending and casting");
  for(int tick=0;tick<5999;tick++)fixture.getCooldowns().tick();
  require(fixture.getCooldowns().isOnCooldown(secondScroll),"Native item cooldown remains active until the last tick of five minutes");
  fixture.getCooldowns().tick();
  require(!fixture.getCooldowns().isOnCooldown(secondScroll),"Native item cooldown expires exactly after 6000 ticks");
  var playerSave = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess());
  fixture.saveWithoutId(playerSave);
  require(playerSave.buildResult().getLongOr("mahoutsukai:doom_ready_at",0) == deadline,"DOOM cooldown saved with player");
  castEntities.forEach(Entity -> Entity.discard());
  var sounds=new java.util.ArrayList<net.minecraft.network.protocol.game.ClientboundSoundPacket>();
  var particles=new java.util.ArrayList<net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket>();
  var observer=player(world,"DoomAudioObserver",net.minecraft.world.level.GameType.CREATIVE,608);
  observer.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(world.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),observer,net.minecraft.server.network.CommonListenerCookie.createInitial(observer.getGameProfile(),false)){
   @Override public void send(net.minecraft.network.protocol.Packet<?> packet){
    if(packet instanceof net.minecraft.network.protocol.game.ClientboundSoundPacket sound)sounds.add(sound);
    if(packet instanceof net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket particle)particles.add(particle);
   }
  };
  world.getServer().getPlayerList().getPlayers().add(observer);
  try {
  var survivor = player(world,"DoomTotem",net.minecraft.world.level.GameType.SURVIVAL,562);
  survivor.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND,new ItemStack(Items.TOTEM_OF_UNDYING));
  var victim = player(world,"DoomVictim",net.minecraft.world.level.GameType.SURVIVAL,561);
  victim.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.RESISTANCE,6000,4));
  victim.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,new ItemStack(Items.NETHERITE_CHESTPLATE));
  var creative = player(world,"DoomCreative",net.minecraft.world.level.GameType.CREATIVE,560);
  var spectator = player(world,"DoomSpectator",net.minecraft.world.level.GameType.SPECTATOR,559);
  fixture.setPos(512,250,512);
  world.addNewPlayer(fixture);
  var inside = EntityType.COW.create(world, EntitySpawnReason.COMMAND);
  var outside = EntityType.COW.create(world, EntitySpawnReason.COMMAND);
  var totem = EntityType.ZOMBIE.create(world, EntitySpawnReason.COMMAND);
  inside.setPos(562, 250, 512);
  outside.setPos(573, 250, 512);
  totem.setPos(563, 250, 512);
  totem.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
  world.addFreshEntity(inside); world.addFreshEntity(outside); world.addFreshEntity(totem);
  for(var dense:BlockPos.betweenClosed(center.offset(-15,-15,-15),center.offset(15,15,15)))if(!dense.equals(protectedPos)&&!dense.equals(center.offset(1,0,0)))world.setBlock(dense,Blocks.STONE.defaultBlockState(),18);
  System.out.println("DOOM_DENSE_BLOCKS=29791");
  var doom = new DoomEntity(ModEntities.DOOM.get(), world);
  doom.setOwner(fixture.getUUID());
  doom.setPos(512,250,512);
  for (int i=0;i<159;i++) doom.tick();
  require(inside.isAlive() && !world.getBlockState(center).isAir(), "DOOM waits full eight seconds");
  require(doom.erodedCount() > 0 && doom.erodedCount() < 8000, "Terrain erodes during attraction within a bounded budget, leaving the dense core for the finale");
  require(world.getBlockState(erosionProtected).is(Blocks.STONE), "Attraction erosion respects claim cancellation");
  require(world.getBlockState(erosionBedrock).is(Blocks.BEDROCK), "Attraction erosion preserves bedrock");
  System.out.println("DOOM fixture victims visible=" + world.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, new net.minecraft.world.phys.AABB(450,190,450,580,310,580)).size());
  long blastStarted=System.nanoTime();doom.tick();System.out.println("DOOM_ONE_TICK_MS="+(System.nanoTime()-blastStarted)/1000000.0);
  require(sounds.stream().filter(s->s.getSound().value()==stepsword.mahoutsukai.sounds.ModSounds.DOOM_ARRIVAL_HORN.get()).count()==1,"Arrival horn plays exactly once when DOOM appears");
  require(sounds.stream().filter(s->s.getSound().value()==stepsword.mahoutsukai.sounds.ModSounds.DOOM_IMPACT_HORN.get()).count()==1,"Impact horn plays exactly once when DOOM detonates");
  require(sounds.stream().filter(s->s.getSound().value()==stepsword.mahoutsukai.sounds.ModSounds.DOOM_IMPACT.get()).count()==1,"Original heavy impact layer strikes once");
  require(sounds.stream().filter(s->s.getSound().value()==stepsword.mahoutsukai.sounds.ModSounds.DOOM_ARRIVAL_HORN.get()||s.getSound().value()==stepsword.mahoutsukai.sounds.ModSounds.DOOM_IMPACT_HORN.get()).allMatch(s->s.getPitch()==1F),"Film signal retains original playback pitch");
  long heartbeats=sounds.stream().filter(s->s.getSound().value()==net.minecraft.sounds.SoundEvents.WARDEN_HEARTBEAT).count();
  require(heartbeats==15,"Accelerating heartbeat reaches observer 96 blocks away, got "+heartbeats);
  require(sounds.stream().filter(s->s.getSound().value()==stepsword.mahoutsukai.sounds.ModSounds.DOOM_RUMBLE.get()).count()==2,"Rumble starts at ticks 5 and 95 without overlapping its 4.35-second playback");
  for(var sound:java.util.List.of(net.minecraft.sounds.SoundEvents.BEACON_AMBIENT,net.minecraft.sounds.SoundEvents.WARDEN_SONIC_CHARGE,net.minecraft.sounds.SoundEvents.WARDEN_SONIC_BOOM,stepsword.mahoutsukai.sounds.ModSounds.DOOM_ARRIVAL_HORN.get(),stepsword.mahoutsukai.sounds.ModSounds.DOOM_IMPACT_HORN.get(),net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE.value()))
   require(sounds.stream().anyMatch(s->s.getSound().value()==sound&&s.getVolume()>=8&&s.getX()==512&&s.getY()==250&&s.getZ()==512),"Native DOOM sound packet originates at target and reaches 96-block observer: "+sound);
  require(particles.stream().anyMatch(p->p.getParticle().getType()==net.minecraft.core.particles.ParticleTypes.FLASH&&p.getY()==250),"Native flash packet stays at target height and reaches distant observer");
  require(particles.stream().filter(p->p.getParticle().getType()==net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL).count()==468,"Bounded inward trails reach distant observer");
  System.out.println("PASS: DOOM_AUDIO_PARTICLES: native packets at target, 96-block observer receives 15 accelerating heartbeats, beacon/sonic charge, one arrival horn and one impact horn, 468 inward trails and exact-height flash");
  System.out.println("DOOM fixture health=" + inside.getHealth() + ", outside=" + outside.getHealth() + ", totem=" + totem.getHealth());
  require(!inside.isAlive() && outside.isAlive(), "DOOM kills mobs within 60, excludes outside radius");
  require(!victim.isAlive() && !fixture.isAlive(), "DOOM kills survival players including caster and ignores vanilla armor/resistance immunity");
  require(survivor.isAlive() && survivor.getOffhandItem().isEmpty(), "DOOM respects player totem");
  require(creative.getHealth() == creative.getMaxHealth() && spectator.getHealth() == spectator.getMaxHealth(),"Creative and spectator excluded");
  require(totem.isAlive() && totem.getOffhandItem().isEmpty(), "DOOM respects native totem survival");
  require(doom.workCursor()==81*81*81 && world.getBlockState(center).isAir() && world.getBlockState(center.offset(39,0,0)).isAir(), "Entire blast commits in its first detonation tick");
  for(int x:new int[]{-15,15})for(int y:new int[]{-15,15})for(int z:new int[]{-15,15})require(world.getBlockState(center.offset(x,y,z)).isAir(),"All dense-fixture octants removed in first detonation tick");
  // Save after the atomic destruction and reload through native entity persistence.
  var output = net.minecraft.world.level.storage.TagValueOutput.createWithoutContext(net.minecraft.util.ProblemReporter.DISCARDING);
  doom.saveWithoutId(output);
  var reloaded = new DoomEntity(ModEntities.DOOM.get(), world);
  reloaded.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, world.registryAccess(), output.buildResult()));
  require(reloaded.age() == 160 && reloaded.workCursor() == doom.workCursor() && reloaded.erodedCount() == doom.erodedCount() && fixture.getUUID().equals(reloaded.getOwner()), "DOOM persists preparation, owner, work cursor and attraction erosion");
  for (int i=0;i<250 && !reloaded.isRemoved();i++) reloaded.tick();
  require(reloaded.isRemoved(), "DOOM finishes shockwave after one-tick destruction");
  require(world.getBlockState(center).isAir() && world.getBlockState(center.offset(39,0,0)).isAir(), "Inside block radius destroyed");
  require(world.getBlockState(center.offset(41,0,0)).is(Blocks.STONE), "Outside block radius preserved");
  require(world.getBlockState(center.offset(1,0,0)).is(Blocks.BEDROCK), "Bedrock preserved");
  require(world.getBlockState(protectedPos).is(Blocks.STONE), "Claim cancellation preserved");
  require(totem.isAlive(), "Reload after detonation does not apply lethal damage twice");
  inside.discard(); outside.discard(); totem.discard();
  for (var player : java.util.List.of(survivor,victim,creative,spectator,fixture))
   world.removePlayerImmediately(player,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
  System.out.println("PASS: DOOM_INTEGRATION: exact block/entity/occluded/sky aiming, five-minute native cooldown boundary and saved deadline, eight-second preparation, lethal radius, totem, bounded block radius, claim cancellation, bedrock, native save/reload");
  }finally{world.getServer().getPlayerList().getPlayers().remove(observer);world.removePlayerImmediately(observer,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);}
 }
}
