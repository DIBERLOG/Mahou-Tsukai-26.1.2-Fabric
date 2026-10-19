package stepsword.mahoutsukai;

import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.fabric.*;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.networking.SpellVisualPacket;

/** Native registry, saved progression and packet/asset checks for the presentation update. */
public final class PresentationResearchChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void run(net.minecraft.server.level.ServerLevel world,List<GuideCatalog.Entry> entries)throws Exception{
  require(stepsword.mahoutsukai.potion.ModEffects.getColorNumber(204,204,140)==0xCCCC8C,"Insight particles preserve their intended gold color");
  require(stepsword.mahoutsukai.potion.ModEffects.getColorNumber(255,0,0)==0xFF0000&&stepsword.mahoutsukai.potion.ModEffects.getColorNumber(0,255,0)==0x00FF00&&stepsword.mahoutsukai.potion.ModEffects.getColorNumber(0,0,255)==0x0000FF,"Effect RGB channels occupy separate bytes");
  require(stepsword.mahoutsukai.potion.ModEffects.getColorNumber(255,255,255)==0xFFFFFF&&stepsword.mahoutsukai.potion.ModEffects.getColorNumber(0,0,0)==0,"Effect RGB preserves white and black endpoints");
  var identities=new HashSet<Integer>();int scrolls=0;
  for(var item:ModItems.REGISTERED.values())if(item instanceof stepsword.mahoutsukai.item.spells.SpellScroll){var style=SpellPresentation.of(item);require(SpellPresentation.supports(item),"Every scroll has presentation");require(identities.add(style.seed()),"Scroll signatures do not collide");require(style.pitch()>0&&style.pitch()<2,"Valid playback pitch");scrolls++;}
  var individualEvents=new HashSet<String>();var individualAssets=new HashSet<String>();
  for(var entry:ModItems.REGISTERED.entrySet())if(entry.getValue() instanceof stepsword.mahoutsukai.item.spells.SpellScroll)for(boolean release:new boolean[]{false,true}){
   String stage=release?"release":"charge",name=entry.getKey().replaceFirst("^scroll_",""),event="spell."+name+"."+stage;
   var sound=stepsword.mahoutsukai.sounds.ModSounds.spellCast(entry.getValue(),release);
   require(BuiltInRegistries.SOUND_EVENT.getKey(sound).toString().equals("mahoutsukai:"+event),"Individual registered spell audio: "+event);
   require(individualEvents.add(event),"No shared individual spell events");
   try(var stream=PresentationResearchChecks.class.getResourceAsStream("/assets/mahoutsukai/sounds/spells/"+name+"_"+stage+".ogg")){
    require(stream!=null,"Packaged individual asset: "+event);byte[] data=stream.readAllBytes();
    require(data.length>1000&&new String(data,0,4,java.nio.charset.StandardCharsets.US_ASCII).equals("OggS"),"Individual Vorbis asset: "+event);
    require(individualAssets.add(java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(data))),"Individual spells must not reuse audio bytes: "+event);
   }
  }
  require(individualEvents.size()==scrolls*2&&individualAssets.size()==scrolls*2,"Every actual SpellScroll has separate charge and activation audio");
  for(var item:ModItems.REGISTERED.values())if(SpellPresentation.supports(item)&&!(item instanceof stepsword.mahoutsukai.item.spells.SpellScroll))for(boolean release:new boolean[]{false,true})require(stepsword.mahoutsukai.sounds.ModSounds.spellCast(item,release)==stepsword.mahoutsukai.sounds.ModSounds.cast(SpellPresentation.of(item).soundKey(),release),"Staff and capacity scrolls preserve family audio");
  for(var family:SpellPresentation.Family.values())for(String stage:List.of("charge","release")){
   String key=family.name().toLowerCase(Locale.ROOT);
   require(BuiltInRegistries.SOUND_EVENT.getKey(stepsword.mahoutsukai.sounds.ModSounds.cast(key,stage.equals("release"))).getNamespace().equals("mahoutsukai"),"Custom audio registered");
   try(var stream=PresentationResearchChecks.class.getResourceAsStream("/assets/mahoutsukai/sounds/cast/"+key+"_"+stage+".ogg")){require(stream!=null&&new String(stream.readNBytes(4),java.nio.charset.StandardCharsets.US_ASCII).equals("OggS"),"Packaged Vorbis asset: "+key+stage);}
  }
  for(String name:List.of("tripod_film"))try(var stream=PresentationResearchChecks.class.getResourceAsStream("/assets/mahoutsukai/sounds/doom/"+name+".ogg")){require(stream!=null&&new String(stream.readNBytes(4),java.nio.charset.StandardCharsets.US_ASCII).equals("OggS"),"Packaged film recording: "+name);}
  try(var reader=new java.io.InputStreamReader(PresentationResearchChecks.class.getResourceAsStream("/assets/mahoutsukai/sounds.json"),java.nio.charset.StandardCharsets.UTF_8)){
   var sounds=com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();for(String event:List.of("doom.arrival_horn","doom.impact_horn"))require(sounds.getAsJsonObject(event).getAsJsonArray("sounds").get(0).getAsJsonObject().get("name").getAsString().equals("mahoutsukai:doom/tripod_film"),"Both DOOM stages use the film recording");
   for(String event:individualEvents){require(sounds.has(event),"Individual spell event in sounds.json: "+event);String[] parts=event.split("\\.");require(sounds.getAsJsonObject(event).getAsJsonArray("sounds").get(0).getAsJsonObject().get("name").getAsString().equals("mahoutsukai:spells/"+parts[1]+"_"+parts[2]),"Individual event resolves its own audio: "+event);}
  }
  var packet=new SpellVisualPacket(new net.minecraft.world.phys.Vec3(1,2,3),SpellPresentation.of(ModItems.REGISTERED.get("scroll_summon_familiar")));
  var buffer=io.netty.buffer.Unpooled.buffer();try{SpellVisualPacket.STREAM_CODEC.encode(buffer,packet);require(packet.equals(SpellVisualPacket.STREAM_CODEC.decode(buffer)),"Visual event round trip");require(!buffer.isReadable(),"Visual event consumes exact payload");}finally{buffer.release();}
  var nodes=ResearchProgress.nodes(entries);var keys=new HashSet<String>();nodes.forEach(n->require(keys.add(n.key()),"Unique research keys"));
  var secret=entries.stream().filter(e->e.item().equals("mahoutsukai:scroll_doom")).toList();require(secret.size()==1&&secret.getFirst().kind().equals("secret")&&secret.getFirst().ingredients().isEmpty()&&secret.getFirst().pattern().isEmpty(),"Server catalogue retains only a non-disclosing DOOM entry");
  var doomNode=nodes.stream().filter(n->n.key().equals("doom")).findFirst().orElseThrow();require(doomNode.recipe().isEmpty()&&doomNode.parents().equals(List.of("mana1000"))&&doomNode.goals().equals(List.of("mahoutsukai:scroll_doom")),"Secret DOOM research has no material prerequisites or recipe link");
  var sculkNode=nodes.stream().filter(n->n.key().equals("sculk")).findFirst().orElseThrow();require(sculkNode.goals().equals(List.of("mahoutsukai:powdered_sculk")),"Sculk is an independent material discovery");
  for(String language:List.of("ru_ru","en_us"))try(var reader=new java.io.InputStreamReader(PresentationResearchChecks.class.getResourceAsStream("/assets/mahoutsukai/lang/"+language+".json"),java.nio.charset.StandardCharsets.UTF_8)){
   var text=com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();for(String key:List.of("mahoutsukai.handbook.changes.body","mahoutsukai.handbook.doom.body","mahoutsukai.research.doom.body","mahoutsukai.catalog.secret")){String body=text.get(key).getAsString().toLowerCase(Locale.ROOT);require(java.util.List.of("powder","sculk","ender","\u043f\u043e\u0440\u043e\u0448","\u0441\u043a\u0430\u043b\u043a","\u044d\u043d\u0434\u0435\u0440").stream().noneMatch(body::contains),"DOOM help contains no ingredient hints: "+language+key);}
   for(String key:List.of("mahoutsukai.research.sculk.title","mahoutsukai.research.sculk.body"))require(!text.get(key).getAsString().toLowerCase(Locale.ROOT).contains("doom"),"Sculk help does not reveal its relation to DOOM");
  }
  for(var node:nodes){require(node.parents().stream().allMatch(keys::contains),"Research parent exists");for(String goal:node.goals())require(BuiltInRegistries.ITEM.containsKey(net.minecraft.resources.Identifier.parse(goal)),"Research item exists: "+goal);}
  require(nodes.stream().filter(n->n.key().startsWith("learn_")).count()==entries.stream().filter(e->e.kind().equals("ritual")).count(),"All ritual families represented");
  require(nodes.stream().filter(n->n.key().equals("fireball")&&n.recipe().equals("craft/scroll_fireball")&&n.parents().equals(List.of("family_mystic"))).count()==1,"Fireball has a visible recipe research node in the mystic branch");
  var visited=new HashSet<String>();boolean changed;do{changed=false;for(var n:nodes)if(!visited.contains(n.key())&&visited.containsAll(n.parents())){visited.add(n.key());changed=true;}}while(changed);require(visited.size()==nodes.size(),"Research graph has no cycles or unreachable parents");
  var player=DoomIntegration.player(world,"Research029",net.minecraft.world.level.GameType.SURVIVAL,552);
  try{
   player.getInventory().clearContent();player.setAttached(GuideCatalog.KNOWLEDGE,List.of());
   player.getInventory().setItem(0,new ItemStack(ModItems.REGISTERED.get("scroll_summon_familiar")));GuideCatalog.discover(player);
   require(!player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE).contains("research/first_scroll"),"Prerequisites cannot be skipped by progress display");
   player.getInventory().clearContent();int slot=0;for(String id:List.of("guidebook","hammer","dagger","powdered_quartz","spell_cloth"))player.getInventory().setItem(slot++,new ItemStack(ModItems.REGISTERED.get(id)));
   GuideCatalog.discover(player);var known=player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE);
   require(known.contains("research/first_scroll")&&known.contains("research/family_familiar"),"Earlier acquisition credited after prerequisites, even after item removal");
   require(!known.contains("research/helper"),"Owning a scroll does not fake a summoned familiar");
   stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.summonFamiliar(player);GuideCatalog.discover(player);
   require(player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE).contains("research/helper"),"Real summon completes helper research");
   player.getInventory().clearContent();GuideCatalog.discover(player);require(player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE).contains("research/tools"),"Discoveries persist after ingredients are gone");
   var codec=com.mojang.serialization.Codec.STRING.listOf();var json=codec.encodeStart(com.mojang.serialization.JsonOps.INSTANCE,player.getAttachedOrCreate(GuideCatalog.KNOWLEDGE)).getOrThrow();require(codec.parse(com.mojang.serialization.JsonOps.INSTANCE,json).getOrThrow().contains("research/helper"),"Persistent research data round trip");
   for(var mode:List.of(net.minecraft.world.level.GameType.SURVIVAL,net.minecraft.world.level.GameType.CREATIVE)){
    player.setGameMode(mode);var rows=com.google.gson.JsonParser.parseString(GuideCatalog.payload(player)).getAsJsonArray();int secretRows=0;
    for(var value:rows){var row=value.getAsJsonObject();if(row.get("item").getAsString().equals("mahoutsukai:scroll_doom")){
     require(!row.toString().contains("powdered_")&&!row.toString().contains("craft/scroll_doom"),"Network catalogue does not disclose secret ingredients: "+mode);
     if(!row.get("kind").getAsString().equals("research")){secretRows++;require(row.get("kind").getAsString().equals("secret")&&row.getAsJsonArray("ingredients").isEmpty()&&row.getAsJsonArray("pattern").isEmpty(),"DOOM remains mysterious even in creative catalogue");}
    }}require(secretRows==1,"DOOM remains visible as one secret spell: "+mode);
   }
  }finally{var helper=stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.remove(player.getUUID());if(helper!=null)helper.discard();world.removePlayerImmediately(player,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);}
  System.out.println("PASS: PRESENTATION_RESEARCH: "+scrolls+" scroll signatures, "+individualEvents.size()+" individual + 20 shared/DOOM audio events / "+(individualAssets.size()+19)+" assets, event codec, "+nodes.size()+" valid nodes, real familiar and persistent prerequisite progression");
 }
}
