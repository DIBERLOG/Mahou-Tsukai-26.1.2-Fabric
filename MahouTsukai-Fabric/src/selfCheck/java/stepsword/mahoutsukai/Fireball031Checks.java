package stepsword.mahoutsukai;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.fabric.SpellCasting;
import stepsword.mahoutsukai.fireball.FireballEntity;
import stepsword.mahoutsukai.fireball.FireballScroll;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.util.Utils;

/** Real server entities, crafting-independent paid casting, native collisions and persistence. */
public final class Fireball031Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static FireballEntity shot(ServerPlayer caster,Vec3 origin,Vec3 velocity){
  var entity=new FireballEntity(ModEntities.FIREBALL.get(),caster.level());entity.setOwner(caster);entity.setPos(origin);entity.setDeltaMovement(velocity);
  require(caster.level().addFreshEntity(entity),"Fireball fixture spawns in native world");return entity;
 }
 private static <T extends LivingEntity>T mob(ServerLevel world,EntityType<T> type,double x,double z){
  var target=type.create(world,EntitySpawnReason.COMMAND);target.setPos(x,250.8,z);target.setNoGravity(true);
  target.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(20);target.setHealth(20);
  if(target instanceof net.minecraft.world.entity.Mob entity)entity.setNoAi(true);
  world.addFreshEntity(target);return target;
 }
 private static void fly(FireballEntity entity,int ticks){for(int i=0;i<ticks&&!entity.isRemoved();i++)entity.tick();}
 private static void ready(ServerPlayer player,ItemStack scroll){
  player.setPos(576.5,250,544.5);player.setXRot(0);player.setYRot(0);player.stopUsingItem();player.setItemInHand(InteractionHand.MAIN_HAND,scroll);
  for(int i=0;i<FireballScroll.COOLDOWN_TICKS;i++)player.getCooldowns().tick();
 }
 public static void run(ServerPlayer fixture){
  var world=fixture.level();var tracked=new ArrayList<Entity>();
  var player=DoomIntegration.player(world,"Fireball031",GameType.SURVIVAL,576.5);tracked.add(player);player.setNoGravity(true);
  var arena=new AABB(568,247,536,591,257,567);
  for(var pos:BlockPos.betweenClosed(568,247,536,590,256,566))world.setBlock(pos,Blocks.AIR.defaultBlockState(),18);
  var stale=world.getEntitiesOfClass(FireballEntity.class,arena);if(!stale.isEmpty())System.out.println("Fireball fixture reset: removing "+stale.size()+" old projectiles from the isolated arena");stale.forEach(Entity::discard);
  try{
   var scrollItem=ModItems.REGISTERED.get("scroll_fireball");require(scrollItem instanceof FireballScroll,"Custom fireball scroll registered");
   var input=net.minecraft.world.item.crafting.CraftingInput.of(3,1,java.util.List.of(new ItemStack(ModItems.REGISTERED.get("spell_cloth")),new ItemStack(net.minecraft.world.item.Items.BLAZE_POWDER),new ItemStack(net.minecraft.world.item.Items.GUNPOWDER)));
   var recipe=world.getServer().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,input,world).orElseThrow();
   require(recipe.value().assemble(input).is(scrollItem)&&!recipe.value().isSpecial()&&!recipe.value().display().isEmpty(),"Fireball has a normal visible crafting recipe");
   player.getInventory().clearContent();player.getInventory().setItem(0,input.getItem(0).copy());stepsword.mahoutsukai.fabric.GuideCatalog.discover(player);
   require(!player.getAttachedOrCreate(stepsword.mahoutsukai.fabric.GuideCatalog.KNOWLEDGE).contains("craft/scroll_fireball"),"One of three ingredients does not reveal Fireball recipe");
   player.getInventory().setItem(1,input.getItem(1).copy());stepsword.mahoutsukai.fabric.GuideCatalog.discover(player);
   require(player.getAttachedOrCreate(stepsword.mahoutsukai.fabric.GuideCatalog.KNOWLEDGE).contains("craft/scroll_fireball"),"Two of three ingredients reveal Fireball catalogue recipe");
   player.getInventory().setItem(2,input.getItem(2).copy());net.minecraft.advancements.CriteriaTriggers.INVENTORY_CHANGED.trigger(player,player.getInventory(),input.getItem(2));
   require(player.getRecipeBook().contains(recipe.id()),"All Fireball materials automatically unlock vanilla recipe");
   var crafting=new net.minecraft.world.inventory.CraftingMenu(89,player.getInventory(),net.minecraft.world.inventory.ContainerLevelAccess.create(world,player.blockPosition()));
   for(int i=0;i<3;i++)crafting.getInputGridSlots().get(i).set(input.getItem(i).copy());
   var output=crafting.getResultSlot().remove(1);require(output.is(scrollItem),"Native crafting result supplies Fireball scroll");crafting.getResultSlot().onTake(player,output);
   require(crafting.getInputGridSlots().stream().allMatch(slot->slot.getItem().isEmpty()),"Fireball crafting consumes exactly its three ingredients");
   var spell=(FireballScroll)scrollItem;var scroll=new ItemStack(scrollItem,4);ready(player,scroll);
   var mana=Utils.getPlayerMahou(player);mana.setMaxMana(20000);mana.setStoredMana(1000);mana.setManaToUseCounter(1000000);player.getInventory().clearContent();player.setItemInHand(InteractionHand.MAIN_HAND,scroll);
   require(SpellCasting.requiredTicks(scroll)==24&&spell.getInitialManaCost()==80,"Fireball uses 1.2-second cast and raw80/effective40 mana");
   spell.use(world,player,InteractionHand.MAIN_HAND);
   require(!spell.releaseUsing(scroll,world,player,72000-23)&&scroll.getCount()==4&&mana.getStoredMana()==1000,"Early fireball cancellation spends no mana or scroll");player.stopUsingItem();
   spell.releaseUsing(scroll,world,player,72000-24);
   var launched=world.getEntitiesOfClass(FireballEntity.class,arena);
   require(launched.size()==1&&scroll.getCount()==3&&mana.getStoredMana()==960,"Successful native fireball spends40 mana and one scroll: projectiles="+launched.size()+", scrolls="+scroll.getCount()+", mana="+mana.getStoredMana()+", caster="+player.position()+", cooldown="+player.getCooldowns().isOnCooldown(scroll));
   var projectile=launched.getFirst();tracked.add(projectile);
   require(projectile.getOwner()==player&&Math.abs(projectile.getDeltaMovement().length()-1.2)<.00001,"Native fireball has correct owner and constant initial speed");
   spell.useAction(scroll,world,player,true);
   require(mana.getStoredMana()==960&&scroll.getCount()==3&&world.getEntitiesOfClass(FireballEntity.class,arena).size()==1,"Server cooldown prevents duplicate fireball and payment");
   for(int i=0;i<39;i++)player.getCooldowns().tick();require(player.getCooldowns().isOnCooldown(scroll),"Fireball cooldown lasts39 full ticks");player.getCooldowns().tick();require(!player.getCooldowns().isOnCooldown(scroll),"Fireball cooldown ends at exactly40 ticks");
   var primary=mob(world,EntityType.COW,576.5,550);var splash=mob(world,EntityType.COW,578.1,550);tracked.add(primary);tracked.add(splash);
   fly(projectile,10);
   require(projectile.isRemoved()&&primary.getHealth()==8,"Real projectile collision deals12HP once to direct target");
   require(splash.getHealth()<20&&splash.getHealth()>12,"Nearby target receives falling splash damage below8HP");
   require(primary.getLastHurtByPlayer()==player&&primary.getRemainingFireTicks()>=80,"Fireball damage credits caster and ignites accepted hit for4seconds");primary.discard();splash.discard();

   // Solid cover must block both the projectile and its blast; terrain and adjacent air stay intact.
   for(var pos:BlockPos.betweenClosed(575,250,549,578,253,549))world.setBlock(pos,Blocks.OAK_PLANKS.defaultBlockState(),18);
   var covered=mob(world,EntityType.COW,576.5,550.5);tracked.add(covered);
   var wallShot=shot(player,player.getEyePosition().add(0,0,.5),new Vec3(0,0,1.2));tracked.add(wallShot);fly(wallShot,10);
   require(wallShot.isRemoved()&&covered.getHealth()==20,"Solid wall blocks fireball and blast damage behind cover");
   for(var pos:BlockPos.betweenClosed(575,250,549,578,253,549))require(world.getBlockState(pos).is(Blocks.OAK_PLANKS),"Fireball does not break wooden wall");
   require(world.getBlockState(new BlockPos(576,251,548)).isAir(),"Fireball never places environmental fire");covered.discard();
   for(var pos:BlockPos.betweenClosed(575,250,549,578,253,549))world.removeBlock(pos,false);
   var muzzle=new BlockPos(576,251,545);world.setBlock(muzzle,Blocks.STONE.defaultBlockState(),18);ready(player,scroll);spell.useAction(scroll,world,player,true);
   require(mana.getStoredMana()==960&&scroll.getCount()==3&&!player.getCooldowns().isOnCooldown(scroll),"Obstructed muzzle refunds payment and sets no cooldown");world.removeBlock(muzzle,false);
   mana.setStoredMana(39);spell.useAction(scroll,world,player,true);require(mana.getStoredMana()==39&&scroll.getCount()==3&&world.getEntitiesOfClass(FireballEntity.class,arena).isEmpty(),"Insufficient fireball mana cannot spawn or consume scroll");mana.setStoredMana(960);

   var wet=mob(world,EntityType.COW,576.5,550);tracked.add(wet);var water=new BlockPos(576,251,548);world.setBlock(water,Blocks.WATER.defaultBlockState(),18);
   var wetShot=shot(player,player.getEyePosition().add(0,0,.5),new Vec3(0,0,1.2));tracked.add(wetShot);fly(wetShot,10);
   require(wetShot.isRemoved()&&wetShot.getZ()<=548.001&&wet.getHealth()==20&&!wet.isOnFire(),"Crossing water quenches fireball before collision or blast");
   require(world.getBlockState(water).is(Blocks.WATER),"Quenching preserves water");world.removeBlock(water,false);wet.discard();
   var beforeWater=mob(world,EntityType.COW,576.5,547.9);tracked.add(beforeWater);world.setBlock(water,Blocks.WATER.defaultBlockState(),18);
   var earlyHit=shot(player,new Vec3(576.5,player.getEyeY(),547),new Vec3(0,0,1.2));tracked.add(earlyHit);earlyHit.tick();
   require(earlyHit.isRemoved()&&earlyHit.getZ()<548&&beforeWater.getHealth()==8&&beforeWater.isOnFire(),"Entity before water within the same movement step takes12HP instead of being skipped");
   world.removeBlock(water,false);beforeWater.discard();

   var blaze=mob(world,EntityType.BLAZE,576.5,550);tracked.add(blaze);var immuneShot=shot(player,player.getEyePosition().add(0,0,.5),new Vec3(0,0,1.2));tracked.add(immuneShot);fly(immuneShot,10);
   require(immuneShot.isRemoved()&&blaze.getHealth()==blaze.getMaxHealth()&&!blaze.isOnFire(),"Native fire-immune target takes no fireball damage or burning");blaze.discard();
   var resistant=mob(world,EntityType.COW,576.5,550);resistant.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE,200));tracked.add(resistant);
   var resistantShot=shot(player,player.getEyePosition().add(0,0,.5),new Vec3(0,0,1.2));tracked.add(resistantShot);fly(resistantShot,10);
   require(resistantShot.isRemoved()&&resistant.getHealth()==20&&!resistant.isOnFire(),"Fire Resistance and rejected native damage prevent applied burning");resistant.discard();
   var protectedMob=new Cow(EntityType.COW,world){@Override public boolean hurtServer(ServerLevel level,net.minecraft.world.damagesource.DamageSource source,float amount){return false;}};
   protectedMob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(20);protectedMob.setHealth(20);protectedMob.setPos(576.5,250.8,550);protectedMob.setNoGravity(true);protectedMob.setNoAi(true);world.addFreshEntity(protectedMob);tracked.add(protectedMob);
   var protectedShot=shot(player,player.getEyePosition().add(0,0,.5),new Vec3(0,0,1.2));tracked.add(protectedShot);fly(protectedShot,10);
   require(protectedShot.isRemoved()&&protectedMob.getHealth()==20&&!protectedMob.isOnFire(),"Damage-hook veto remains authoritative and prevents ignition");protectedMob.discard();

   var teammate=DoomIntegration.player(world,"FireballFriend031",GameType.SURVIVAL,576.5);teammate.setPos(576.5,250.8,550);tracked.add(teammate);
   var team=world.getScoreboard().addPlayerTeam("fireball031");team.setAllowFriendlyFire(false);world.getScoreboard().addPlayerToTeam(player.getScoreboardName(),team);world.getScoreboard().addPlayerToTeam(teammate.getScoreboardName(),team);
   try{var friendlyShot=shot(player,player.getEyePosition().add(0,0,.5),new Vec3(0,0,1.2));tracked.add(friendlyShot);fly(friendlyShot,10);require(friendlyShot.isRemoved()&&teammate.getHealth()==20&&!teammate.isOnFire(),"Native no-friendly-fire rule protects player from direct and splash damage");}finally{world.getScoreboard().removePlayerTeam(team);world.removePlayerImmediately(teammate,Entity.RemovalReason.DISCARDED);}

   // Mystic Code pays for the same scroll and obeys its cooldown, including after switching hand items.
   ready(player,scroll);var code=new ItemStack(ModItems.mysticCodeFirstSorcery.get());
   code.set(net.minecraft.core.component.DataComponents.CONTAINER,net.minecraft.world.item.component.ItemContainerContents.fromItems(java.util.List.of(new ItemStack(scrollItem))));player.setItemInHand(InteractionHand.MAIN_HAND,code);
   code.getItem().releaseUsing(code,world,player,72000-23);require(mana.getStoredMana()==960&&world.getEntitiesOfClass(FireballEntity.class,arena).isEmpty(),"Early Mystic Code fireball cancellation is free");
   code.getItem().releaseUsing(code,world,player,72000-24);var codeShots=world.getEntitiesOfClass(FireballEntity.class,arena);tracked.addAll(codeShots);
   require(codeShots.size()==1&&mana.getStoredMana()==920&&Utils.getMCInventory(code).getItem(0).getCount()==1,"First Sorcery code launches fireball, pays40 and retains loaded scroll");
   spell.useAction(scroll,world,player,true);require(mana.getStoredMana()==920&&world.getEntitiesOfClass(FireballEntity.class,arena).size()==1,"Switching from Mystic Code to direct scroll cannot bypass server cooldown");codeShots.forEach(Entity::discard);

   var moving=shot(player,new Vec3(580,252,560),new Vec3(.6,0,0));tracked.add(moving);moving.tick();
   var save=TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess());moving.saveWithoutId(save);
   var restored=new FireballEntity(ModEntities.FIREBALL.get(),world);restored.load(TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),save.buildResult()));
   require(restored.age()==1&&Math.abs(restored.distanceTravelled()-.6)<.00001&&restored.getOwner()==player&&restored.getDeltaMovement().equals(moving.getDeltaMovement()),"Native save/reload preserves owner, velocity, age and travelled distance");moving.discard();
   var stationary=shot(player,new Vec3(580,252,560),Vec3.ZERO);tracked.add(stationary);fly(stationary,79);require(!stationary.isRemoved(),"Missed fireball remains for79 ticks");stationary.tick();require(stationary.isRemoved()&&stationary.age()==80,"Missed fireball expires at80 ticks");
   var rangeTag=save.buildResult().copy();rangeTag.putDouble("mahoutsukai_fireball_distance",95.5);rangeTag.putInt("mahoutsukai_fireball_age",1);
   var limited=new FireballEntity(ModEntities.FIREBALL.get(),world);limited.load(TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),rangeTag));limited.setPos(580,252,560);limited.setDeltaMovement(new Vec3(1.2,0,0));var rangeStart=limited.position();
   var beyondRangeWater=new BlockPos(581,252,560);world.setBlock(beyondRangeWater,Blocks.WATER.defaultBlockState(),18);limited.tick();
   require(limited.isRemoved()&&Math.abs(limited.distanceTravelled()-96)<.00001&&Math.abs(limited.position().distanceTo(rangeStart)-.5)<.00001,"Range limit survives reload and clips movement before checking water beyond96 blocks");world.removeBlock(beyondRangeWater,false);
   var unloaded=new BlockPos(1000000,252,1000000);require(!world.hasChunkAt(unloaded),"Unloaded chunk test starts outside native tickets");var orphan=new FireballEntity(ModEntities.FIREBALL.get(),world);orphan.setOwner(player);orphan.setPos(unloaded.getCenter());orphan.setDeltaMovement(new Vec3(0,0,1.2));orphan.tick();require(orphan.isRemoved()&&!world.hasChunkAt(unloaded),"Fireball discards at unloaded chunks without loading them");
   System.out.println("PASS: FIREBALL_031 native cast/payment/cancel/code/cooldown, direct and splash combat, wall/terrain/water, fire immunity and damage veto, friendly fire, owner attribution, save/reload and bounded lifecycle");
  }finally{
   for(var entity:tracked)if(entity instanceof ServerPlayer nativePlayer)world.removePlayerImmediately(nativePlayer,Entity.RemovalReason.DISCARDED);else entity.discard();
   world.getEntitiesOfClass(FireballEntity.class,arena).forEach(Entity::discard);
   for(var pos:BlockPos.betweenClosed(568,247,536,590,256,566))world.setBlock(pos,Blocks.AIR.defaultBlockState(),18);
  }
 }
}
