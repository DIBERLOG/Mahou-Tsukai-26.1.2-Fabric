package stepsword.mahoutsukai;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.*;
import stepsword.mahoutsukai.fabric.*;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;
/** Native regression checks for the deliberately changed progression rules. */
public final class Progression026Checks {
 private static java.util.Optional<net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>> serverRecipe(net.minecraft.server.level.ServerLevel world,net.minecraft.world.item.crafting.CraftingInput input){return world.getServer().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,input,world);}
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void start(net.minecraft.server.MinecraftServer server)throws Exception{
  var world=server.overworld();server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),"forceload add 560 512 592 576");
  var fixture=DoomIntegration.player(world,"ProgressionFixture",net.minecraft.world.level.GameType.CREATIVE,550);run(fixture);world.removePlayerImmediately(fixture,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
  var rain=DoomIntegration.player(world,"Rain026",net.minecraft.world.level.GameType.SURVIVAL,560);rain.setNoGravity(true);
  server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),"fillbiome 560 64 512 563 300 515 minecraft:plains");server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),"weather rain");world.setRainLevel(1);
  for(int y=rain.blockPosition().getY()+1;y<world.getMaxY();y++)world.removeBlock(new net.minecraft.core.BlockPos(560,y,512),false);
  var stick=new ItemStack(Items.STICK);stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect.setStrengthened(stick,world);
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(new net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick(){int age,stage,changedAt;boolean done;net.minecraft.core.BlockPos roof;
   public void onEndTick(net.minecraft.server.MinecraftServer running){if(done)return;age++;
    try{
     if(stage==0){if(!world.canSeeSky(rain.blockPosition())){require(age<200,"Native sky-light fixture did not finish lighting");return;}roof=rain.blockPosition().above(3);world.setBlockAndUpdate(roof,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());stage=1;changedAt=age;return;}
     if(age-changedAt<20)return;
     if(stage==1){require(!world.isRainingAt(rain.blockPosition()),"Native rain does not reach covered player");rain.setItemInHand(InteractionHand.OFF_HAND,stick.copy());stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysInRain(rain);require(rain.getOffhandItem().is(Items.STICK),"Roof prevents Emrys transformation");world.removeBlock(roof,false);stage=2;changedAt=age;return;}
     require(world.isRainingAt(rain.blockPosition()),"Native rain reaches player after sky lighting updates");stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysInRain(rain);require(rain.getOffhandItem().is(ModItems.emrysStaff.get()),"Rain creates Emrys without lightning");
     // Projectile queries require native entity-ticking chunks, after the force-load tickets settle.
     MentalDisplacementChecks.run(rain);
     Nobu031Checks.run(rain);Fireball031Checks.run(rain);ArchmageCombat034Checks.run(rain);
     Progression027Checks.runGravity(world);
     server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),"weather clear");world.setRainLevel(0);world.removePlayerImmediately(rain,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);server.getCommands().performPrefixedCommand(server.createCommandSourceStack(),"forceload remove 560 512 592 576");
     java.nio.file.Files.writeString(java.nio.file.Path.of("../../../analysis/phase026-progression-server.passed"),"PASS: progression regression suite, native crafting and automatic recipe discovery, 3000 seeded dungeon loot rolls, actual eating and cancellation, rain/roof lifecycle and Emrys without lightning; real world ticks and sky lighting\n");System.out.println("PASS: PROGRESSION_SERVER_026");done=true;server.halt(false);
    }catch(Exception|AssertionError failure){throw new IllegalStateException("Progression server regression failed",failure);}
   }
  });
 }
 public static void run(ServerPlayer fixture)throws Exception{
  GuideCatalog.reload(fixture.level().getServer());
  var catalogField=GuideCatalog.class.getDeclaredField("entries");catalogField.setAccessible(true);
  var catalog=(java.util.List<GuideCatalog.Entry>)catalogField.get(null);
  PresentationResearchChecks.run(fixture.level(),catalog);
  RealityMarbleBossChecks.run(fixture);
  GroundSpawn036Checks.run(fixture);
  ArchmageDefeat036Checks.run(fixture);
  BossVictory037Checks.run(fixture);
  WilliamFlightChecks.run(fixture);
  stepsword.mahoutsukai.entity.mahoujin.GauntletFlightChecks.run(fixture);
  require(catalog.stream().filter(e->e.kind().equals("craft")&&e.id().endsWith("_2")&&e.output()==2).count()==7,"All seven original mortar recipes are displayed with two outputs");
  for(var pair:stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar.recipes.entrySet())if(pair.getValue() instanceof net.minecraft.world.level.block.EntityBlock block){
   var tile=block.newBlockEntity(net.minecraft.core.BlockPos.ZERO,pair.getValue().defaultBlockState());
   if(tile instanceof stepsword.mahoutsukai.tile.ScrollGiver giver&&!giver.getItemToGive().isEmpty()){
    String output=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(giver.getItemToGive().getItem()).toString();
    require(catalog.stream().anyMatch(e->e.kind().equals("ritual")&&e.item().equals(output)),"Catalogue uses actual ritual output: "+output);
   }
  }
  var world=fixture.level();var player=DoomIntegration.player(world,"Progression026",net.minecraft.world.level.GameType.SURVIVAL,560);
  try{
   var mana=Utils.getPlayerMahou(player);mana.setMaxMana(20000);mana.setStoredMana(1000);mana.setManaToUseCounter(1000000);player.getInventory().clearContent();
   require(PlayerManaManager.drainMana(player,100,true,true)==100 && mana.getStoredMana()==950,"100-cost spell spends exactly 50 mana and preserves caller contract");
   require(PlayerManaManager.drainMana(player,3,true,true)==3 && mana.getStoredMana()==948,"Odd cost rounds half up");
   require(PlayerManaManager.drainManaExact(player,100,true,true)==100 && mana.getStoredMana()==848,"Transfers remain one-for-one");
   var refundGem=new ItemStack(ModItems.REGISTERED.get("attuned_diamond"));var gemState=Utils.getGemMahou(refundGem);gemState.setStoredMana(100);Utils.setGemMahou(refundGem,gemState);player.getInventory().setItem(0,refundGem);mana.setStoredMana(0);
   require(!PlayerManaManager.castPaid(player,100,()->false)&&mana.getStoredMana()==0&&Utils.getGemMahou(refundGem).getStoredMana()==100,"Failed cast refunds original gem allocation");player.getInventory().clearContent();
   mana.setStoredMana(1);require(PlayerManaManager.drainMana(player,100,true,true)==0 && mana.getStoredMana()==1,"Insufficient discounted payment is atomic");
   mana.setStoredMana(1000);var gandr=new ItemStack(ModItems.gandrScroll.get());var scroll=(SpellScroll)gandr.getItem();player.setItemInHand(InteractionHand.MAIN_HAND,gandr);scroll.use(world,player,InteractionHand.MAIN_HAND);scroll.onUseTick(world,player,gandr,71999);
   require(!scroll.releaseUsing(gandr,world,player,71999)&&mana.getStoredMana()==1000&&gandr.getCount()==1,"Early Gandr cancellation keeps mana and scroll");player.stopUsingItem();
   require(!stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll.gandrs.containsKey(player.getUUID()),"Cancelled Gandr preview removed immediately");
   var strengthening=new ItemStack(ModItems.REGISTERED.get("scroll_strengthening"));player.setItemInHand(InteractionHand.MAIN_HAND,strengthening);player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);((SpellScroll)strengthening.getItem()).useAction(strengthening,world,player,true);require(mana.getStoredMana()==1000&&strengthening.getCount()==1,"Invalid strengthening target costs nothing");
   var code=new ItemStack(ModItems.mysticCode.get());player.setItemInHand(InteractionHand.MAIN_HAND,code);var menu=new stepsword.mahoutsukai.containers.MysticCodeInventory(77,player.getInventory(),code);
   for(String id:java.util.List.of("scroll_presence_concealment","scroll_mystic_staff","scroll_weapon_shooter","scroll_treasury_projection","scroll_proximity_projection","scroll_spatial_disorientation","scroll_probability_alter","scroll_projection"))require(!menu.getSlot(0).mayPlace(new ItemStack(ModItems.REGISTERED.get(id))),"Weapon scroll rejected by native server slot: "+id);
   require(menu.getSlot(0).mayPlace(new ItemStack(ModItems.gandrScroll.get())),"Combat scroll remains loadable");
   code.set(net.minecraft.core.component.DataComponents.CONTAINER,net.minecraft.world.item.component.ItemContainerContents.fromItems(java.util.List.of(new ItemStack(ModItems.explosionStaffScroll.get()))));
   code.getItem().releaseUsing(code,world,player,71000);require(Utils.getMCInventory(code).getItem(0).getCount()==1 && mana.getStoredMana()==1000,"Legacy weapon scroll in Mystic Code cannot bypass cast restriction");
   for(int amount:new int[]{50,100,250,500,1000}){
    mana.setMaxMana(100);mana.setStoredMana(50);var capacity=new ItemStack(ModItems.REGISTERED.get("scroll_mana_"+amount));player.setItemInHand(InteractionHand.MAIN_HAND,capacity);capacity.getItem().use(world,player,InteractionHand.MAIN_HAND);
    require(capacity.isEmpty()&&mana.getMaxMana()==100+amount&&mana.getStoredMana()==50+amount&&mana.hasMagic(),"Capacity scroll persists exact increase: "+amount);
   }
   int cap=ManaConfig.current.MAX_MANA_CAP();mana.setMaxMana(cap);var capped=new ItemStack(ModItems.REGISTERED.get("scroll_mana_1000"));player.setItemInHand(InteractionHand.MAIN_HAND,capped);capped.getItem().use(world,player,InteractionHand.MAIN_HAND);require(capped.getCount()==1&&mana.getMaxMana()==cap,"Capacity cap never consumes scroll");
   require(MageProgression.level(100)==1&&MageProgression.level(20000)==11&&MageProgression.level(25000)==12&&MageProgression.regenBonus(20000)>MageProgression.regenBonus(100),"Mage levels extend above 20000 and regeneration increases");
   mana.setMaxMana(20000);mana.setStoredMana(0);MageProgression.restoreFromFood(player,4);require(mana.getStoredMana()==31,"Food grants nutrition and level mana");
   PlayerManaManager.recoverManaFromSleep(player);require(mana.getStoredMana()==10031,"Sleep restores configured fraction of maximum mana");
   var flame=new ItemStack(ModItems.REGISTERED.get("scroll_black_flame"));player.setItemInHand(InteractionHand.MAIN_HAND,flame);stepsword.mahoutsukai.util.EffectUtil.buff(player,stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES,true,200);player.setShiftKeyDown(true);flame.getItem().use(world,player,InteractionHand.MAIN_HAND);require(!player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES)&&flame.getCount()==1,"Black Flame stops with Shift right-click without spending scroll");player.setShiftKeyDown(false);
   var clarent=new ItemStack(ModItems.REGISTERED.get("clarent"));var stats=Utils.getSwordMahou(clarent);stats.setAttackDamage(0);Utils.setSwordMahou(clarent,stats);player.setItemInHand(InteractionHand.MAIN_HAND,clarent);clarent.getItem().use(world,player,InteractionHand.MAIN_HAND);
   stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingAttack(player,player.damageSources().generic(),100);require(Utils.getSwordMahou(clarent).getAttackDamage()==50,"Clarent permanently stores half incoming damage");clarent.getItem().releaseUsing(clarent,world,player,71900);player.stopUsingItem();require(Utils.getSwordMahou(clarent).getAttackDamage()==50,"Clarent power no longer decays on release");
   var cow=EntityType.COW.create(world,net.minecraft.world.entity.EntitySpawnReason.COMMAND);cow.setPos(player.position());world.addFreshEntity(cow);stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingHurt(cow,player.damageSources().playerAttack(player),1);require(Utils.getSwordMahou(clarent).getAttackDamage()==50&&Utils.getSwordMahou(clarent.copy()).getAttackDamage()==50,"Clarent keeps power after attack and native item copy");cow.discard();
   require(stepsword.mahoutsukai.item.clarent.Clarent.auraColor(0)!=stepsword.mahoutsukai.item.clarent.Clarent.auraColor(100)&&stepsword.mahoutsukai.item.clarent.Clarent.auraColor(100)!=stepsword.mahoutsukai.item.clarent.Clarent.auraColor(1000),"Clarent aura changes with permanent power tiers");
   require(new ItemStack(ModItems.REGISTERED.get("proximity_projection_keys")).getMaxDamage()==130,"Black Keys durability is 130");require(stepsword.mahoutsukai.config.MTConfig.NOBU_DISTANCE_REQUIREMENT==100,"Nobu requirement reduced to 100");
   require(DungeonScrollLoot.eligible(net.minecraft.resources.Identifier.withDefaultNamespace("chests/simple_dungeon"))&&!DungeonScrollLoot.eligible(net.minecraft.resources.Identifier.withDefaultNamespace("chests/village/village_fisher")),"Scroll loot limited to intended dungeon tables");
   player.addEffect(new net.minecraft.world.effect.MobEffectInstance(stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES,200));int stopMana=mana.getStoredMana();
   stepsword.mahoutsukai.networking.StopMagicPacket.handle(player);require(!player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_FLAME_EYES)&&mana.getStoredMana()==stopMana,"Stop key server handler removes Black Flame without mana cost");
   var powders=java.util.List.of("mahoutsukai:powdered_quartz","mahoutsukai:powdered_quartz","mahoutsukai:powdered_quartz");
   require(!GuideCatalog.meetsThreshold(powders,java.util.Map.of("mahoutsukai:powdered_quartz",1)),"One of three ingredient units keeps ordinary ritual locked");
   require(GuideCatalog.meetsThreshold(powders,java.util.Map.of("mahoutsukai:powdered_quartz",2)),"Two duplicate units reveal an ordinary three-powder ritual");
   require(!GuideCatalog.meetsThreshold(java.util.List.of("a","b","c","d"),java.util.Map.of("a",50))&&GuideCatalog.meetsThreshold(java.util.List.of("a","b","c","d"),java.util.Map.of("a",1,"b",1)),"Threshold counts required units and cannot reuse excess of one ingredient");
   GuideCatalog.reload(world.getServer());player.getInventory().clearContent();player.setAttached(GuideCatalog.KNOWLEDGE,java.util.List.of("craft/scroll_doom"));player.getInventory().setItem(0,new ItemStack(ModItems.REGISTERED.get("powdered_ender"),2));player.getInventory().setItem(1,new ItemStack(ModItems.REGISTERED.get("powdered_sculk")));GuideCatalog.discover(player);
   require(!player.getAttached(GuideCatalog.KNOWLEDGE).contains("craft/scroll_doom"),"DOOM stays secret with every ingredient; obsolete discovery is removed");
   String ordinaryDiscovery=catalog.stream().filter(e->e.kind().equals("ritual")&&e.item().equals("mahoutsukai:scroll_summon_familiar")).findFirst().orElseThrow().id();
   player.getInventory().clearContent();player.getInventory().setItem(0,new ItemStack(ModItems.REGISTERED.get("powdered_quartz"),2));player.getInventory().setItem(1,new ItemStack(ModItems.REGISTERED.get("spell_cloth")));GuideCatalog.discover(player);
   require(player.getAttached(GuideCatalog.KNOWLEDGE).contains(ordinaryDiscovery),"Ordinary ritual discovery still uses real ingredients");
   player.getInventory().clearContent();GuideCatalog.discover(player);require(player.getAttached(GuideCatalog.KNOWLEDGE).contains(ordinaryDiscovery),"Ordinary discovery survives removal of ingredients");
   var knowledgeSave=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess());player.saveWithoutId(knowledgeSave);
   player.setAttached(GuideCatalog.KNOWLEDGE,java.util.List.of());player.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),knowledgeSave.buildResult()));
   require(player.getAttached(GuideCatalog.KNOWLEDGE).contains(ordinaryDiscovery)&&!player.getAttached(GuideCatalog.KNOWLEDGE).contains("craft/scroll_doom"),"Ordinary discoveries and DOOM secrecy survive native player save/reload");
   var doom=new ItemStack(ModItems.REGISTERED.get("scroll_doom"));player.setItemInHand(InteractionHand.MAIN_HAND,doom);mana.setStoredMana(20000);((stepsword.mahoutsukai.doom.DoomHolder)player).mahoutsukai$setDoomReadyAt(0);doom.getItem().releaseUsing(doom,world,player,71999);require(doom.getCount()==1&&mana.getStoredMana()==20000,"Early DOOM cancellation is free");
   var recipe=world.getServer().getRecipeManager().byKey(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","scroll_doom")));require(recipe.isPresent(),"Real DOOM crafting recipe loaded");
   var grid=net.minecraft.world.item.crafting.CraftingInput.of(3,1,java.util.List.of(new ItemStack(ModItems.REGISTERED.get("powdered_ender")),new ItemStack(ModItems.REGISTERED.get("powdered_sculk")),new ItemStack(ModItems.REGISTERED.get("powdered_ender"))));
   var craft=serverRecipe(world,grid);require(craft.isPresent()&&craft.get().value().assemble(grid).is(ModItems.REGISTERED.get("scroll_doom")),"Native crafting grid produces DOOM");
   require(craft.get().value().isSpecial()&&craft.get().value().display().isEmpty(),"DOOM has no vanilla recipe-book display or recipe award");
   require(player.awardRecipes(java.util.List.of(craft.get()))==0&&!player.getRecipeBook().contains(craft.get().id()),"Explicit recipe award cannot reveal DOOM");
   player.getRecipeBook().add(craft.get().id());GuideCatalog.discover(player);require(!player.getRecipeBook().contains(craft.get().id()),"Previously discovered vanilla DOOM recipe is removed on discovery refresh");
   for(int sculk=0;sculk<3;sculk++){var input=new java.util.ArrayList<ItemStack>();for(int slot=0;slot<3;slot++)input.add(new ItemStack(ModItems.REGISTERED.get(slot==sculk?"powdered_sculk":"powdered_ender")));var permutation=net.minecraft.world.item.crafting.CraftingInput.of(3,1,input);require(serverRecipe(world,permutation).orElseThrow().value().assemble(permutation).is(ModItems.REGISTERED.get("scroll_doom")),"Secret craft remains shapeless: "+sculk);}
   var incomplete=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(new ItemStack(ModItems.REGISTERED.get("powdered_ender")),new ItemStack(ModItems.REGISTERED.get("powdered_sculk"))));require(serverRecipe(world,incomplete).stream().noneMatch(r->r.value().assemble(incomplete).is(ModItems.REGISTERED.get("scroll_doom"))),"Secret recipe still requires every ingredient unit");
   var craftingMenu=new net.minecraft.world.inventory.CraftingMenu(88,player.getInventory(),net.minecraft.world.inventory.ContainerLevelAccess.create(world,player.blockPosition()));
   for(int slot=0;slot<grid.size();slot++)craftingMenu.getInputGridSlots().get(slot).set(grid.getItem(slot).copy());
   var crafted=craftingMenu.getResultSlot().remove(1);require(crafted.is(ModItems.REGISTERED.get("scroll_doom")),"Native result slot offers the hidden craft");craftingMenu.getResultSlot().onTake(player,crafted);
   require(craftingMenu.getInputGridSlots().stream().allMatch(slot->slot.getItem().isEmpty())&&!player.getRecipeBook().contains(craft.get().id()),"Taking DOOM consumes ingredients without revealing its recipe");
   player.getInventory().clearContent();for(int i=0;i<grid.size();i++)player.getInventory().setItem(i,grid.getItem(i).copy());
   net.minecraft.advancements.CriteriaTriggers.INVENTORY_CHANGED.trigger(player,player.getInventory(),new ItemStack(ModItems.REGISTERED.get("powdered_sculk")));
   var unlock=world.getServer().getAdvancements().get(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","recipes/scroll_doom"));require(unlock!=null&&!player.getAdvancements().getOrStartProgress(unlock).isDone()&&!player.getRecipeBook().contains(craft.get().id()),"Full ingredients do not trigger a DOOM recipe advancement or vanilla discovery");
   player.getInventory().setItem(4,new ItemStack(Items.SCULK));player.getInventory().setItem(5,new ItemStack(ModItems.REGISTERED.get("hammer")));net.minecraft.advancements.CriteriaTriggers.INVENTORY_CHANGED.trigger(player,player.getInventory(),new ItemStack(Items.SCULK));
   var dustUnlock=world.getServer().getAdvancements().get(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","recipes/powdered_sculk"));require(dustUnlock!=null&&player.getAdvancements().getOrStartProgress(dustUnlock).isDone(),"Ordinary sculk dust recipe still unlocks automatically");
   var dustGrid=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(new ItemStack(Items.SCULK),new ItemStack(ModItems.REGISTERED.get("hammer"))));
   var dustRecipe=serverRecipe(world,dustGrid);require(dustRecipe.isPresent()&&dustRecipe.get().value().assemble(dustGrid).is(ModItems.REGISTERED.get("powdered_sculk")),"Sculk and hammer craft sculk dust");
   require(!dustRecipe.get().value().isSpecial()&&!dustRecipe.get().value().display().isEmpty()&&player.getRecipeBook().contains(dustRecipe.get().id()),"Ordinary shapeless recipes keep display, awards and ingredient discovery");
   require(ModItems.REGISTERED.get("hammer").getCraftingRemainder(dustGrid.getItem(1))!=null,"Hammer survives dust crafting");
   var table=world.getServer().reloadableRegistries().getLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE,net.minecraft.resources.Identifier.withDefaultNamespace("chests/simple_dungeon")));
   var lootParams=new net.minecraft.world.level.storage.loot.LootParams.Builder(world).withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN,player.position()).create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.CHEST);
   var random=net.minecraft.util.RandomSource.create(260026);var counts=new java.util.TreeMap<String,Integer>();
   for(int n=0;n<3000;n++)for(var item:table.getRandomItems(lootParams,random)){String id=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.getItem()).getPath();if(id.startsWith("scroll_"))counts.merge(id,item.getCount(),Integer::sum);}
   require(counts.getOrDefault("scroll_mana_1000",0)>0&&counts.getOrDefault("scroll_mana_50",0)>10*counts.get("scroll_mana_1000")&&counts.getOrDefault("scroll_mana_100",0)>10*counts.get("scroll_mana_1000"),"Native dungeon loot yields common 50/100 and rare 1000 mana scrolls");
   require(counts.keySet().stream().anyMatch(id->id.startsWith("scroll_")&&!id.startsWith("scroll_mana_")),"Native dungeon loot yields spell scrolls");java.nio.file.Files.writeString(java.nio.file.Path.of("../../../analysis/phase026-loot-sample.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(counts));
    require(!DungeonScrollLoot.library(net.minecraft.resources.Identifier.withDefaultNamespace("chests/simple_dungeon"))&&DungeonScrollLoot.library(net.minecraft.resources.Identifier.withDefaultNamespace("chests/stronghold_library")),"William is limited to book-heavy chests");
    var libraryTable=world.getServer().reloadableRegistries().getLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE,net.minecraft.resources.Identifier.withDefaultNamespace("chests/stronghold_library")));
    int williams=0;
    for(int n=0;n<2000;n++)for(var item:libraryTable.getRandomItems(lootParams,random))if(item.is(ModItems.william.get())){williams++;
     require(Utils.getFictionMahou(item).getStyle()==DungeonScrollLoot.WILLIAM_STYLE&&Utils.getFictionMahou(item).getFame()==DungeonScrollLoot.WILLIAM_FAME,"Found William carries working fiction data");
     require(item.get(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT)!=null,"Found William has readable written content");
     require(stepsword.mahoutsukai.item.william.William.getManaValue(item)>0,"Found William is a working artifact, not a blank");
    }
    require(williams>0,"Book-heavy dungeon chests can actually contain William");
   mana.setMaxMana(20000);mana.setStoredMana(0);player.getFoodData().setFoodLevel(10);var food=new ItemStack(Items.APPLE,2);player.setItemInHand(InteractionHand.MAIN_HAND,food);player.startUsingItem(InteractionHand.MAIN_HAND);
   var finish=LivingEntity.class.getDeclaredMethod("completeUsingItem");finish.setAccessible(true);finish.invoke(player);require(mana.getStoredMana()==31&&food.getCount()==1,"Native completed eating restores mana and consumes one food");
   int beforeCancel=mana.getStoredMana();player.startUsingItem(InteractionHand.MAIN_HAND);player.stopUsingItem();require(mana.getStoredMana()==beforeCancel,"Cancelled eating grants no mana");
   java.nio.file.Files.writeString(java.nio.file.Path.of("../../../analysis/phase026-progression.passed"),"PASS: native discounted payment, exact transfers, cancelled and invalid casts, server Mystic Code restrictions, all capacity scrolls and cap, progression, food/sleep, Black Flame stop, permanent Clarent, item copy, Black Keys, Nobu and DOOM recipe\n");
   Progression027Checks.run(player);
   System.out.println("PASS: PROGRESSION_026");
  }finally{SpellCasting.cancel(player);world.removePlayerImmediately(player,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);}
 }
}
