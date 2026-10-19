package local.mahouparity;

import com.google.gson.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;

/** The same test source runs beside the unmodified NeoForge JAR and the Fabric candidate. */
public final class ScenarioProbe {
 public static JsonObject run(MinecraftServer server) {
  var world=server.overworld();var result=new JsonObject();
  var profile=new com.mojang.authlib.GameProfile(UUID.fromString("31f48839-e8a6-42f2-adab-a141fe00a024"),"ScenarioOracle");
  var player=new ServerPlayer(server,world,profile,ClientInformation.createDefault());
  // A server mechanics fixture: packets have a sink; networking is covered by live client checks.
  player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(server,new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(profile,false)){
   @Override public void send(net.minecraft.network.protocol.Packet<?> packet){}
  };
  player.setPos(8.5,280,10.5);
  var base=BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse("mahoutsukai:mahoujin"));
  var pos=new BlockPos(8,280,8);world.setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());
  var powders=BuiltInRegistries.ITEM.stream().filter(i->i instanceof stepsword.mahoutsukai.item.catalysts.PowderedCatalyst).sorted(Comparator.comparing(i->BuiltInRegistries.ITEM.getKey(i).toString())).toList();
  if(powders.size()!=7)throw new AssertionError("Expected seven original catalysts, got "+powders.size());
  var trials=new JsonObject();
  try {
   for(var a:powders)for(var b:powders)for(var c:powders)for(boolean cloth:new boolean[]{false,true})for(boolean fae:new boolean[]{false,true}){
    world.removeBlock(pos,false);world.setBlockAndUpdate(pos,base.defaultBlockState());
    var circle=(MahoujinTileEntity)world.getBlockEntity(pos);circle.setCloth(cloth);circle.setFay(fae);
    String key="";var trial=new JsonObject();
    for(var item:List.of(a,b,c)){
     key+=BuiltInRegistries.ITEM.getKey(item).getPath()+"/";
     var stack=new ItemStack(item);player.setItemInHand(InteractionHand.MAIN_HAND,stack);
     world.getBlockState(pos).useItemOn(stack,world,player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
     if(!stack.isEmpty())throw new AssertionError("Native catalyst was not consumed: "+key);
    }
    trial.addProperty("block",BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock()).toString());
    var tile=(MahoujinTileEntity)world.getBlockEntity(pos);
    trial.addProperty("cloth",tile.hasCloth());trial.addProperty("fae",tile.isFay());
    trial.addProperty("caster_preserved",player.getUUID().equals(tile.getCasterUUID()));
    trial.add("catalyst_order",new Gson().toJsonTree(tile.getCatalysts().getOrder()));
    var saved=tile.saveWithoutMetadata(world.registryAccess());
    trial.addProperty("saved_cloth",saved.getBooleanOr("cloth",false));trial.addProperty("saved_fae",saved.getBooleanOr("fay",false));
    trials.add(key+cloth+"/"+fae,trial);
   }
   result.add("ritual_trials",trials);
   result.addProperty("recipe_count",MahoujinRecipeRegistrar.recipes.size());
   var items=new JsonObject();
   for(var item:BuiltInRegistries.ITEM.stream().filter(i->BuiltInRegistries.ITEM.getKey(i).getNamespace().equals("mahoutsukai")).sorted(Comparator.comparing(i->BuiltInRegistries.ITEM.getKey(i).toString())).toList()){
    String id=BuiltInRegistries.ITEM.getKey(item).toString();if(id.startsWith("mahoutsukai:scroll_mana_")||id.equals("mahoutsukai:powdered_sculk")||id.equals("mahoutsukai:scroll_doom")||id.equals("mahoutsukai:scroll_fireball")||id.equals("mahoutsukai:rhongomyniad"))continue;
    var stack=new ItemStack(item);var entry=new JsonObject();entry.addProperty("max_damage",stack.getMaxDamage());entry.addProperty("max_stack",stack.getMaxStackSize());
    entry.addProperty("duration",item.getUseDuration(stack,player));entry.addProperty("animation",item.getUseAnimation(stack).toString());
    if(item instanceof SpellScroll scroll)entry.addProperty("initial_mana_cost",scroll.getInitialManaCost());
    items.add(id,entry);
   }
   result.add("items",items);
   var effects=new JsonObject();
   for(var effect:BuiltInRegistries.MOB_EFFECT){var id=BuiltInRegistries.MOB_EFFECT.getKey(effect);if(!id.getNamespace().equals("mahoutsukai"))continue;
    var entry=new JsonObject();entry.addProperty("color",effect.getColor());entry.addProperty("category",effect.getCategory().toString());entry.addProperty("instant",effect.isInstantenous());
    var ticks=new JsonArray();for(int amplifier:new int[]{0,1,4})for(int duration:new int[]{0,1,5,20,40,100,200,1200})ticks.add(effect.shouldApplyEffectTickThisTick(duration,amplifier));entry.add("tick_schedule",ticks);effects.add(id.toString(),entry);
   }
   result.add("effects",effects);
   var entities=new JsonObject();
   for(var type:BuiltInRegistries.ENTITY_TYPE){var id=BuiltInRegistries.ENTITY_TYPE.getKey(type);if(!id.getNamespace().equals("mahoutsukai")||id.getPath().equals("doom")||id.getPath().equals("fireball"))continue;
    var entity=type.create(world,EntitySpawnReason.LOAD);if(entity==null)throw new AssertionError("Cannot create "+id);
    var entry=new JsonObject();entry.addProperty("width",entity.getBbWidth());entry.addProperty("height",entity.getBbHeight());entry.addProperty("no_gravity",entity.isNoGravity());entry.addProperty("pickable",entity.isPickable());entry.addProperty("fire_immune",entity.fireImmune());
    if(entity instanceof LivingEntity living)entry.addProperty("max_health",living.getMaxHealth());entities.add(id.toString(),entry);entity.discard();
   }
   result.add("entities",entities);return result;
  }finally{world.removeBlock(pos,false);player.discard();}
 }
}
