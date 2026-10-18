package stepsword.mahoutsukai;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.networking.KeyPressPacket;
import stepsword.mahoutsukai.config.MTConfig;
public final class RitualIntegration {
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 private static BlockHitResult hit(BlockPos pos) { return new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false); }
 private static void powder(ServerPlayer player, BlockPos pos, String id, int count) {
  var stack=new ItemStack(ModItems.REGISTERED.get("powdered_"+id),count);
  player.setItemInHand(InteractionHand.MAIN_HAND,stack);
  for(int i=0;i<count;i++) player.level().getBlockState(pos).useItemOn(stack,player.level(),player,InteractionHand.MAIN_HAND,hit(pos));
  require(stack.isEmpty(),"Original powder clicks consume each catalyst");
 }
 public static void run(ServerPlayer player) {
  var world=player.level(); var mahou=((MahouHolder)player).mahoutsukai$getMahou();
  var pos=new BlockPos(528,250,530);
  var crafting=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(new ItemStack(Items.WHITE_WOOL),new ItemStack(ModItems.dagger.get())));
  var clothRecipe=world.getServer().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,crafting,world).orElseThrow().value();
  require(clothRecipe.assemble(crafting).is(ModBlocks.spellClothBlock.get().asItem()) && clothRecipe.assemble(crafting).getCount()==4
   && clothRecipe.getRemainingItems(crafting).get(1).is(ModItems.dagger.get()),"Original cloth recipe produces four and returns dagger");
  var lootPos=new BlockPos(540,250,530);
  var fae=new stepsword.mahoutsukai.entity.fae.FaeEntity(world);fae.setPos(Vec3.atCenterOf(lootPos));world.addFreshEntity(fae);
  fae.hurtServer(world,player.damageSources().genericKill(),100);
  int essenceCount=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(lootPos).inflate(2),e->e.getItem().is(ModItems.faeEssence.get()))
   .stream().mapToInt(e->e.getItem().getCount()).sum();
  require(essenceCount>=1 && essenceCount<=2,"Original fae loot table drops one or two essence");
  fae.discard();
  player.getInventory().clearContent();
  player.setPos(528.5,250,531.5);player.setYRot(180);player.setYHeadRot(180);player.setXRot(0);
  world.setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());
  world.setBlockAndUpdate(pos,ModBlocks.spellClothBlock.get().defaultBlockState());
  player.invulnerableTime=0;player.setHealth(20);
  player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(ModItems.dagger.get()));
  ModItems.dagger.get().use(world,player,InteractionHand.MAIN_HAND);
  mahou.setHasMagic(false);
  KeyPressPacket.handle(new KeyPressPacket("key.invalid"),player);
  require(world.getBlockState(pos).is(ModBlocks.spellClothBlock.get()),"Unknown key cannot draw circle");
  KeyPressPacket.handle(new KeyPressPacket("key.drawMahoujin"),player);
  require(world.getBlockState(pos).is(ModBlocks.mahoujin.get()),"Original M key server handler draws blood circle");
  var circle=(MahoujinTileEntity)world.getBlockEntity(pos);
  require(circle.hasCloth() && !circle.isFay() && player.getUUID().equals(circle.getCasterUUID()) && mahou.hasMagic(),"Drawing preserves cloth, binds caster and opens magic");
  require(!player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLEEDING),"Drawing consumes bleeding");
  powder(player,pos,"eye",3);
  require(world.getBlockState(pos).is(ModBlocks.faySightEyesMahoujin.get()) && mahou.getLastRecipe().size()==3 && mahou.getLastRecipeCloth(),"Three eye catalysts transform cloth circle into Fay Sight and remember recipe");
  var tag=world.getBlockEntity(pos).saveWithoutMetadata(world.registryAccess());
  require(tag.getBooleanOr("cloth",false) && net.minecraft.core.UUIDUtil.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,tag.get("caster_uuid")).getOrThrow().equals(player.getUUID()),"Original circle NBT cloth/caster");
  player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
  world.getBlockState(pos).useWithoutItem(world,player,hit(pos));
  var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_fay_sight")));
  require(world.getBlockState(pos).isAir() && drops.size()==1,"Native empty-hand use produces spell scroll and removes circle");
  drops.forEach(e->e.discard());
  // Fae Essence path uses the original asynchronous server execute hook.
  world.setBlockAndUpdate(pos,ModBlocks.spellClothBlock.get().defaultBlockState());
  var essence=new ItemStack(ModItems.faeEssence.get(),2);player.setItemInHand(InteractionHand.MAIN_HAND,essence);
  ModItems.faeEssence.get().useOn(new net.minecraft.world.item.context.UseOnContext(player,InteractionHand.MAIN_HAND,hit(pos)));
  circle=(MahoujinTileEntity)world.getBlockEntity(pos);
  require(essence.getCount()==1 && circle.isFay() && circle.hasCloth(),"Fae Essence draws fae circle and consumes one essence");
  // Repeat recipe from direct cloth + catalysts inside an actual Fabric item storage provider.
  var bag=new net.minecraft.world.SimpleContainer(1);
  bag.setItem(0,new ItemStack(ModItems.REGISTERED.get("powdered_eye"),3));
  net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.ITEM.registerForItems((stack,context)->
   net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage.of(bag,null),Items.BOWL);
  player.getInventory().clearContent();
  player.getInventory().setItem(1,new ItemStack(Items.BOWL));
  player.getInventory().setItem(2,new ItemStack(ModBlocks.spellClothBlock.get()));
  world.getBlockState(pos).useWithoutItem(world,player,hit(pos));
  require(world.getBlockState(pos).is(ModBlocks.faySightEyesMahoujin.get()) && bag.isEmpty()
   && player.getInventory().getItem(2).isEmpty(),"Repeat recipe extracts catalysts through Fabric bag storage and consumes cloth");
  world.getBlockState(pos).useWithoutItem(world,player,hit(pos));
  drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_fay_sight")));
  require(drops.size()==1 && stepsword.mahoutsukai.util.Utils.getScrollMahou(drops.getFirst().getItem()).getCasterUUID().orElseThrow().equals(stepsword.mahoutsukai.fabric.MahouIds.FAE_UUID),"Fae ritual scroll keeps universal fae binding");
  drops.forEach(e->e.discard());
  world.setBlockAndUpdate(pos,ModBlocks.mahoujin.get().defaultBlockState());
  player.getInventory().setItem(2,new ItemStack(ModBlocks.spellClothBlock.get()));
  world.getBlockState(pos).useWithoutItem(world,player,hit(pos));
  require(((MahoujinTileEntity)world.getBlockEntity(pos)).getNumCatalysts()==0 && player.getInventory().getItem(2).getCount()==1,"Insufficient repeat leaves cloth and circle unchanged");
  powder(player,pos,"eye",3);
  require(world.getBlockState(pos).is(ModBlocks.mahoujin.get()),"Fay Sight recipe requires cloth");
  // Ground Ascension uses original above-check and battery-first payment.
  var ascent=pos.offset(5,0,0);var upper=ascent.above(10);var batteryPos=ascent.offset(1,0,0);
  world.setBlockAndUpdate(ascent,ModBlocks.mahoujin.get().defaultBlockState());
  powder(player,ascent,"ender",2);powder(player,ascent,"gold",1);
  require(world.getBlockState(ascent).is(ModBlocks.ascensionMahoujin.get()),"Original Ascension powder combination works without cloth");
  var ascender=(stepsword.mahoutsukai.tile.displacement.AscensionMahoujinTileEntity)world.getBlockEntity(ascent);
  ascender.setCasterUUID(player.getUUID());
  world.setBlockAndUpdate(upper,Blocks.STONE.defaultBlockState());
  world.setBlockAndUpdate(upper.above(),Blocks.AIR.defaultBlockState());world.setBlockAndUpdate(upper.above(2),Blocks.AIR.defaultBlockState());
  world.setBlockAndUpdate(batteryPos,ModBlocks.circuit.get().defaultBlockState());
  var battery=(stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity)world.getBlockEntity(batteryPos);
  battery.setOwnerUUID(player.getUUID());battery.setOn(true);battery.setMana(100);
  var cow=net.minecraft.world.entity.EntityType.COW.create(world,net.minecraft.world.entity.EntitySpawnReason.COMMAND);
  cow.setPos(ascent.getX()+0.5,ascent.getY(),ascent.getZ()+0.5);world.addFreshEntity(cow);
  ascender.checkAbove(ascender);
  require(cow.getY()==upper.getY()+1 && battery.getMana()==70,"Ascension ground circle teleports entity above roof and drains 30 battery mana");
  cow.discard();
  // Two probability circles + chorus fruit produce the original Rule Breaker modulus.
  var anchor1=pos.offset(0,0,5);var anchor2=pos.offset(3,0,5);
  for(var anchor:java.util.List.of(anchor1,anchor2)) {
   world.setBlockAndUpdate(anchor,ModBlocks.mahoujin.get().defaultBlockState());
   powder(player,anchor,"gold",1);powder(player,anchor,"diamond",1);powder(player,anchor,"quartz",1);
   require(world.getBlockState(anchor).is(ModBlocks.probabilityAlterMahoujin.get()),"Probability Alter original three-catalyst combination");
   world.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(world,anchor.getX()+0.5,anchor.getY()+0.1,anchor.getZ()+0.5,new ItemStack(Items.CHORUS_FRUIT)));
  }
  player.getInventory().clearContent();mahou.setMaxMana(6000);mahou.setStoredMana(6000);player.setPos(529,250,531);
  var probability=new ItemStack(ModItems.REGISTERED.get("scroll_probability_alter"));
  ((stepsword.mahoutsukai.item.spells.SpellScroll)probability.getItem()).releaseUsing(probability,world,player,71999);
  var weapons=player.getInventory().getNonEquipmentItems().stream().filter(st->st.is(ModItems.ruleBreaker.get())).toList();
  require(probability.isEmpty() && mahou.getStoredMana()==1000 && weapons.size()==1
   && stepsword.mahoutsukai.item.rulebreaker.RuleBreaker.getModulus(weapons.getFirst())==2,"Probability Alter consumes fruit anchors and 5000 mana to create modulus-two Rule Breaker");
  var geasPos=pos.offset(8,0,5);
  world.setBlockAndUpdate(geasPos,ModBlocks.mahoujin.get().defaultBlockState());
  ((MahoujinTileEntity)world.getBlockEntity(geasPos)).setCloth(true);
  powder(player,geasPos,"gold",1);powder(player,geasPos,"emerald",1);powder(player,geasPos,"iron",1);
  require(world.getBlockState(geasPos).is(ModBlocks.geasMahoujin.get()),"Original Geas ritual recipe");
  world.getBlockState(geasPos).useWithoutItem(world,player,hit(geasPos));
  var geasDrops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(geasPos).inflate(2),entity->entity.getItem().is(ModItems.REGISTERED.get("scroll_geas")));
  require(geasDrops.size()==1,"Geas cloth circle gives original scroll");
  GeasIntegration.run(player,geasDrops.getFirst().getItem());geasDrops.getFirst().discard();
  for(var at:java.util.List.of(pos,ascent,upper,batteryPos,anchor1,anchor2))world.setBlockAndUpdate(at,Blocks.AIR.defaultBlockState());
  System.out.println("PASS: RITUAL_INTEGRATION: blood circle, cloth, catalysts, native scroll drop, essence, bag recipe repeat, Ascension ground ritual, Rule Breaker creation");
 }
}


