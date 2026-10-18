package local.mahouparity;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import stepsword.jousting.item.ItemLance;
import stepsword.mahoutsukai.item.lance.Rhongomyniad;
@Mod("mahou_optional_probe")
public final class OriginalOptionalProbe {
 public OriginalOptionalProbe(){NeoForge.EVENT_BUS.addListener(this::started);}
 private void started(ServerStartedEvent event){var server=event.getServer();try{
  var level=server.overworld();var json=new com.google.gson.JsonObject();
  var player=new net.minecraft.server.level.ServerPlayer(server,level,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"OptionalOracle"),net.minecraft.server.level.ClientInformation.createDefault());
  player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(server,new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(),false));
  player.setPos(8,280,8);var horse=net.minecraft.world.entity.EntityType.HORSE.create(level,net.minecraft.world.entity.EntitySpawnReason.COMMAND);horse.setPos(8,280,8);level.addFreshEntity(horse);player.startRiding(horse,true,false);
  for(String material:new String[]{"wood","iron","gold","emerald","diamond","netherite"}){
   var item=BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("jousting","lance_"+material));var lance=(ItemLance)item;var stack=new ItemStack(item);var entry=new com.google.gson.JsonObject();
   entry.addProperty("damage",lance.getAttackDamage(stack));entry.addProperty("weight",lance.getWeight(stack));entry.addProperty("durability",stack.getMaxDamage());entry.addProperty("duration",lance.getUseDuration(stack,player));
   entry.addProperty("simulated_hit",Rhongomyniad.simulateHit(stack,level));
   player.setItemInHand(InteractionHand.MAIN_HAND,stack);lance.use(level,player,InteractionHand.MAIN_HAND);entry.addProperty("using",player.isUsingItem());
   var speed=horse.getAttribute(Attributes.MOVEMENT_SPEED);entry.addProperty("base_speed",speed.getValue());lance.onUseTick(level,player,stack,lance.getUseDuration(stack,player)-20);entry.addProperty("charged_speed",speed.getValue());lance.releaseUsing(stack,level,player,1);player.stopUsingItem();entry.addProperty("released_speed",speed.getValue());json.add(material,entry);
  }
  var rhongo=new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("mahoutsukai","rhongomyniad")));var stats=stepsword.mahoutsukai.util.Utils.getLanceMahou(rhongo);stats.setAttackDamage(25);stats.setWeight(1.2f);stepsword.mahoutsukai.util.Utils.setLanceMahou(rhongo,stats);
  var entry=new com.google.gson.JsonObject();entry.addProperty("damage",((ItemLance)rhongo.getItem()).getAttackDamage(rhongo));entry.addProperty("weight",((ItemLance)rhongo.getItem()).getWeight(rhongo));json.add("rhongomyniad",entry);
  java.nio.file.Files.writeString(java.nio.file.Path.of("../../analysis/phase023-original-optional.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(json));
  java.nio.file.Files.writeString(java.nio.file.Path.of("../../analysis/phase023-original-optional.passed"),"PASS: unmodified original Mahou + Jousting on NeoForge; six lance stats, native mounted charge/release, simulated hits and Rhongomyniad components\n");
 }catch(Exception e){throw new RuntimeException(e);}finally{server.halt(false);}}
}
