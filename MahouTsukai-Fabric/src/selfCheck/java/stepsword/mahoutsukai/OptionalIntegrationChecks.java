package stepsword.mahoutsukai;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.fabric.*;

public final class OptionalIntegrationChecks {
 static void require(boolean ok,String text){if(!ok)throw new AssertionError(text);}
 public static void run(ServerPlayer fixture)throws Exception{
  if(net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("apotheosis"))Class.forName("stepsword.mahoutsukai.ApotheosisIntegrationChecks").getMethod("run").invoke(null);
  else {var stack=new ItemStack(Items.DIAMOND_SWORD);require(ApotheosisCompatibility.unsocket(stack)==stack,"Absent optional Apotheosis keeps the stack intact");}
  if(JoustingCompatibility.loaded())jousting(fixture);
  else require(!stepsword.mahoutsukai.item.ModItems.REGISTERED.containsKey("rhongomyniad"),"Optional Rhongomyniad must not register without Jousting");
 }
 private static void jousting(ServerPlayer fixture)throws Exception{
  var golden=com.google.gson.JsonParser.parseString(java.nio.file.Files.readString(java.nio.file.Path.of("../../../analysis/phase023-original-optional.json"))).getAsJsonObject();
  var world=fixture.level();var p=DoomIntegration.player(world,"JoustingNative",net.minecraft.world.level.GameType.SURVIVAL,560);
  double[] damages={3,6,4.5,4,6.5,5.5},weights={1.3F,2,1.5,1,3,2.2F};int[] durability={320,600,400,1612,1268,2550};int index=0;
  var horse=net.minecraft.world.entity.EntityType.HORSE.create(world,net.minecraft.world.entity.EntitySpawnReason.COMMAND);horse.setPos(p.position());world.addFreshEntity(horse);
  require(p.startRiding(horse,true,false),"Real player mounts real horse");
  for(String material:new String[]{"wood","iron","gold","emerald","diamond","netherite"}){
   var item=BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("jousting","lance_"+material));
   require(item instanceof stepsword.jousting.item.ItemLance,"Original lance ID registered: "+material);var lance=(stepsword.jousting.item.ItemLance)item;var stack=new ItemStack(item);
   require(lance.getAttackDamage(stack)==damages[index]&&lance.getWeight(stack)==weights[index]&&stack.getMaxDamage()==durability[index],"Original lance defaults "+material);
   var oracle=golden.getAsJsonObject(material);float actualHit=stepsword.mahoutsukai.item.lance.Rhongomyniad.simulateHit(stack,world);require(lance.getUseDuration(stack,p)==oracle.get("duration").getAsInt() && actualHit==oracle.get("simulated_hit").getAsFloat(),"Unmodified NeoForge lance oracle: "+material+" native duration="+lance.getUseDuration(stack,p)+" hit="+actualHit+" expected="+oracle);
   p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);require(lance.use(world,p,net.minecraft.world.InteractionHand.MAIN_HAND).consumesAction()&&p.isUsingItem(),"Mounted native lance use "+material);
   lance.onUseTick(world,p,stack,lance.getUseDuration(stack,p)-20);require(horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).hasModifier(stepsword.jousting.item.ItemLance.speedlance),"Mounted charge applies original speed modifier");
   require(Math.abs(horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).getValue()-oracle.get("charged_speed").getAsDouble())<1e-9,"Unmodified NeoForge charge speed oracle");
   lance.releaseUsing(stack,world,p,10);p.stopUsingItem();require(!horse.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).hasModifier(stepsword.jousting.item.ItemLance.speedlance),"Release removes mounted charge speed");index++;
  }
  p.stopRiding();var rhongo=new ItemStack(stepsword.mahoutsukai.item.ModItems.REGISTERED.get("rhongomyniad"));var lance=(stepsword.mahoutsukai.item.lance.Rhongomyniad)rhongo.getItem();p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,rhongo);
  var values=stepsword.mahoutsukai.util.Utils.getLanceMahou(rhongo);values.setAttackDamage(25);values.setWeight(1.2f);stepsword.mahoutsukai.util.Utils.setLanceMahou(rhongo,values);
  require(lance.getAttackDamage(rhongo)==26&&Math.abs(lance.getWeight(rhongo)-1.2)<.00001,"Rhongomyniad uses persistent original damage/weight components");
  require(JoustingCompatibility.rotation(rhongo.getItem())==-180&&JoustingCompatibility.rotation(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("jousting","lance_iron")))==-90,"Original Treasury lance rotations");
  var contents=(stepsword.mahoutsukai.item.spells.projection.TreasuryProjection.TreasuryProjectionGauntlet)stepsword.mahoutsukai.item.ModItems.treasuryProjectionGauntlet.get();
  require(contents.getProjectables(p).stream().anyMatch(s->s.getItem()==lance),"Treasury accepts actual Jousting lances");
  var target=net.minecraft.world.entity.EntityType.COW.create(world,net.minecraft.world.entity.EntitySpawnReason.COMMAND);target.setPos(p.position().add(1,0,0));world.addFreshEntity(target);float health=target.getHealth();lance.attack(p,target);require(target.getHealth()<health,"Rhongomyniad native attack damages real target");
  target.discard();horse.discard();p.discard();System.out.println("PASS: JOUSTING_INTEGRATION: six original lance IDs/defaults, native mounted use/charge/release, Rhongomyniad persistent stats/real damage, Treasury acceptance and original rotations");
 }
}
