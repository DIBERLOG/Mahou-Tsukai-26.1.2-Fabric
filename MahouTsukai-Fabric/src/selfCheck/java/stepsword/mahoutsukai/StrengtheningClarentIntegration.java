package stepsword.mahoutsukai;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.potion.ModEffects;
public final class StrengtheningClarentIntegration {
 private static net.minecraft.world.entity.animal.cow.Cow wounded;
 private static void require(boolean value,String message) { if(!value) throw new AssertionError(message); }
 private static ItemStack cast(ServerPlayer player) {
  var scroll=new ItemStack(ModItems.REGISTERED.get("scroll_strengthening"));((stepsword.mahoutsukai.item.spells.SpellScroll)scroll.getItem()).releaseUsing(scroll,player.level(),player,71999);return scroll;
 }
 public static void run(ServerPlayer player) {
  var world=player.level();player.setPos(480.5,250,550.5);player.getInventory().clearContent();player.setHealth(20);player.invulnerableTime=0;
  var mahou=((MahouHolder)player).mahoutsukai$getMahou();mahou.setMaxMana(2000);mahou.setStoredMana(1000);
  var sword=new ItemStack(Items.IRON_SWORD);player.setItemInHand(InteractionHand.OFF_HAND,sword);
  require(cast(player).isEmpty() && StrengtheningSpellEffect.getStrengthened(sword)==5 && mahou.getStoredMana()==950 && sword.has(net.minecraft.core.component.DataComponents.UNBREAKABLE),"Original strengthening grants five uses, temporary unbreakable and costs fifty");
  var isolated=sword.copy();StrengtheningSpellEffect.reduceStrengthened(isolated,1);require(StrengtheningSpellEffect.getStrengthened(sword)==5 && StrengtheningSpellEffect.getStrengthened(isolated)==4,"Strengthening mutation preserves native item copy isolation");
  player.setItemInHand(InteractionHand.MAIN_HAND,sword);player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);
  var target=EntityType.COW.create(world,EntitySpawnReason.COMMAND);target.setPos(480.5,250,546.5);world.addFreshEntity(target);
  target.hurtServer(world,player.damageSources().playerAttack(player),1);
  require(target.getHealth()==5 && StrengtheningSpellEffect.getStrengthened(sword)==4,"Native player attack gains original four damage and consumes charge");target.discard();
  StrengtheningSpellEffect.reduceStrengthened(sword,4);require(!sword.has(net.minecraft.core.component.DataComponents.UNBREAKABLE),"Final use removes temporary unbreakable");
  sword.set(net.minecraft.core.component.DataComponents.UNBREAKABLE,net.minecraft.util.Unit.INSTANCE);StrengtheningSpellEffect.setStrengthened(sword,world);StrengtheningSpellEffect.reduceStrengthened(sword,5);require(sword.has(net.minecraft.core.component.DataComponents.UNBREAKABLE),"Original permanent unbreakable survives strengthening expiry");
  var pick=new ItemStack(Items.WOODEN_PICKAXE);require(!pick.isCorrectToolForDrops(Blocks.IRON_ORE.defaultBlockState()),"Unstrengthened wood pick cannot harvest iron");StrengtheningSpellEffect.setStrengthened(pick,world);require(pick.isCorrectToolForDrops(Blocks.IRON_ORE.defaultBlockState()) && !pick.isCorrectToolForDrops(Blocks.DIAMOND_ORE.defaultBlockState()),"Native strengthening raises tool one tier");
  player.setItemInHand(InteractionHand.MAIN_HAND,pick);float enhanced=player.getDestroySpeed(Blocks.STONE.defaultBlockState());StrengtheningSpellEffect.reduceStrengthened(pick,5);float ordinary=player.getDestroySpeed(Blocks.STONE.defaultBlockState());require(Math.abs(enhanced-ordinary-3)<0.00001,"Native strengthened mining adds original three speed");
  var armor=new ItemStack(Items.IRON_CHESTPLATE);StrengtheningSpellEffect.setStrengthened(armor,world);player.setItemSlot(EquipmentSlot.CHEST,armor);player.invulnerableTime=0;player.hurtServer(world,player.damageSources().generic(),1);require(StrengtheningSpellEffect.getStrengthened(armor)==4,"Native incoming hit consumes strengthened armor use");player.setItemSlot(EquipmentSlot.CHEST,ItemStack.EMPTY);
  player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(Items.IRON_SWORD));mahou.setStoredMana(1000);cast(player);
  player.setItemInHand(InteractionHand.MAIN_HAND,player.getOffhandItem());player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);
  var dragon=EntityType.ENDER_DRAGON.create(world,EntitySpawnReason.COMMAND);dragon.setPos(480.5,250,550.5);world.addFreshEntity(dragon);
  var breath=new AreaEffectCloud(world,480.5,250,550.5);breath.setOwner(dragon);breath.setRadius(3);world.addFreshEntity(breath);
  require(breath.getOwner()==dragon,"Native dragon breath fixture retains owner");
  net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.invoker().interact(player,world,InteractionHand.MAIN_HAND);
  var clarent=player.getMainHandItem();require(clarent.is(ModItems.REGISTERED.get("clarent")) && Utils.getSwordMahou(clarent).getAttackDamage()==0 && clarent.has(net.minecraft.core.component.DataComponents.UNBREAKABLE) && breath.getRadius()==2.5f,"Actual right click transmutes strengthened sword in dragon breath into original Clarent");
  dragon.discard();breath.discard();player.setHealth(20);player.invulnerableTime=0;
  clarent.getItem().use(world,player,InteractionHand.MAIN_HAND);require(player.isUsingItem(),"Native Clarent enters original blocking stance");player.hurtServer(world,player.damageSources().generic(),4);
  require(Utils.getSwordMahou(clarent).getAttackDamage()==2,"Clarent blocking stance stores original half of incoming damage");
  clarent.getItem().releaseUsing(clarent,world,player,71950);player.stopUsingItem();require(Math.abs(Utils.getSwordMahou(clarent).getAttackDamage()-1)<0.0001,"Clarent release subtracts original per-blocking-tick charge");
  var state=Utils.getSwordMahou(clarent);state.setAttackDamage(100);Utils.setSwordMahou(clarent,state);
  var values=new ArrayList<Double>();clarent.forEachModifier(EquipmentSlot.MAINHAND,(attribute,modifier)-> {if(attribute.is(Attributes.ATTACK_DAMAGE)) values.add(modifier.amount());});
  require(values.size()==1 && values.getFirst()==100,"Native attribute enumeration replaces sword damage without duplicating base modifier");
  mahou.setStoredMana(1000);wounded=EntityType.COW.create(world,EntitySpawnReason.COMMAND);wounded.setPos(480.5,250,545.5);wounded.setNoAi(true);wounded.setNoGravity(true);wounded.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500);wounded.setHealth(500);world.addFreshEntity(wounded);
  wounded.hurtServer(world,player.damageSources().playerAttack(player),1);
  require(wounded.getHealth()==399 && mahou.getStoredMana()==998 && Utils.getSwordMahou(clarent).getAttackDamage()==0 && wounded.hasEffect(ModEffects.WOUNDED),"Clarent spends stored charge and two mana, deals weapon-relative one hundred one damage and inflicts original wound");
  Utils.getLivingMahou(wounded).setLastWoundedPos(wounded.position());wounded.setPos(wounded.getX()+3,wounded.getY(),wounded.getZ());wounded.invulnerableTime=0;
  var ritualPos=new BlockPos(490,250,550);ExchangeCircleIntegration.ritual(player,ritualPos,"strengthening",true,"diamond","diamond","iron");world.setBlockAndUpdate(ritualPos,Blocks.AIR.defaultBlockState());
  System.out.println("PASS: STRENGTHENING_CLARENT_INTEGRATION: scroll/cost/charges/item copies, native combat/mining/tier/armor, temporary and permanent unbreakable, dragon-breath acquisition, blocking charge/decay, dynamic attributes and counterattack/wound");
 }
 public static boolean verifyWoundTick() {
  if(wounded==null || wounded.level().getGameTime()%10!=0) return false;
  require(Math.abs(wounded.getHealth()-398.8f)<0.001,"Original wound movement damage runs from actual native living tick");wounded.discard();wounded=null;
  System.out.println("PASS: WOUNDED_NATIVE_TICK: movement inflicts original wound damage, preserving vanilla hurt cooldown");return true;
 }
}
