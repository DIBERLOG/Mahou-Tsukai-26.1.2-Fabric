package stepsword.mahoutsukai;

import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.fabric.GuideCatalog;
import stepsword.mahoutsukai.entity.*;
import stepsword.mahoutsukai.networking.DoneMentalPacket;

/** Review probes only; never included in the distributed mod. Records defects without hiding them. */
public final class Audit032ServerChecks {
 public static void init(){
  int[] ticks={0};
  ServerTickEvents.END_SERVER_TICK.register(server->{if(++ticks[0]!=30)return;
   var results=new JsonObject();
   try{
    var world=server.overworld();var base=new BlockPos(800,300,800);
    world.setChunkForced(50,50,true);world.getChunk(50,50);
    for(int x=0;x<8;x++)world.setBlockAndUpdate(base.offset(x,0,0),Blocks.STONE.defaultBlockState());
    var player=DoomIntegration.player(world,"Audit032",GameType.SURVIVAL,800);
    player.teleportTo(800.5,301,800.5);
    var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);
    mahou.setMentalDisplacementPos(null);
    var origin=player.position();
    DoneMentalPacket.onMessage(new DoneMentalPacket(804.5,301,800.5),player);
    results.addProperty("unauthorized_mental_packet_rejected",player.position().distanceToSqr(origin)<0.01);
    results.addProperty("unauthorized_mental_packet_distance",player.position().distanceTo(origin));

    var boss=new RealityMarbleBossEntity(ModEntities.REALITY_MARBLE_BOSS.get(),world);
    boss.snapTo(player.getX()+2,301,800.5,0,0);
    boss.setNoAi(true);
    boss.setTarget(player);
    var burst=RealityMarbleBossEntity.class.getDeclaredMethod("sonicBurst",net.minecraft.server.level.ServerLevel.class);
    burst.setAccessible(true);
    clientReady(player,false);
    burst.invoke(boss,world);
    for(int i=0;i<10;i++)boss.tick();
    boolean waited=boss.arrivalTicks()==50&&boss.sonicBursts==0&&boss.attackTicks()==0;
    clientReady(player,true);
    for(int i=0;i<50;i++){boss.tick();waited&=boss.attackTicks()==0&&boss.sonicBursts==0;}
    results.addProperty("boss_waits_for_arrival_and_client",waited&&boss.arrivalTicks()==0);
    burst.invoke(boss,world);
    results.addProperty("boss_delays_hit_until_telegraph_ends",boss.sonicHits==0&&boss.attackTicks()==RealityMarbleBossEntity.CAST_TICKS&&boss.sonicBursts==1);
    results.addProperty("boss_hits_during_telegraph",boss.sonicHits);
    results.addProperty("boss_telegraph_remaining",boss.attackTicks());
    boss.setInvulnerable(true);
    boolean accepted=boss.hurtServer(world,world.damageSources().playerAttack(player),1);
    results.addProperty("boss_rejected_damage_does_not_credit_reward",!accepted&&boss.attackerCount()==0);
    results.addProperty("boss_rejected_damage_credited_attackers",boss.attackerCount());

    player.getInventory().clearContent();player.setAttached(GuideCatalog.KNOWLEDGE,List.of());
    var payload=JsonParser.parseString(GuideCatalog.payload(player)).getAsJsonArray();
    var lockedIds=new HashSet<String>();
    for(var value:payload){var row=value.getAsJsonObject();if(row.has("unlocked")&&!row.get("unlocked").getAsBoolean())lockedIds.add(row.get("id").getAsString());}
    int rawDisclosed=0;
    for(var value:payload){var row=value.getAsJsonObject();if(row.has("unlocked")&&!row.get("unlocked").getAsBoolean()&&row.getAsJsonArray("ingredients").size()>0)rawDisclosed++;}
    results.addProperty("locked_catalog_payload_redacted",rawDisclosed==0);
    int disclosed=0;String example="";
    for(var value:payload){var row=value.getAsJsonObject();if(row.get("kind").getAsString().equals("research")&&row.has("ingredients")&&lockedIds.contains(row.get("recipe").getAsString())){disclosed++;if(example.isEmpty())example=row.toString();}}
    results.addProperty("locked_recipes_not_disclosed_through_research",disclosed==0);
    results.addProperty("locked_recipe_disclosures",disclosed);results.addProperty("locked_recipe_example",example);
    for(String check:java.util.List.of("unauthorized_mental_packet_rejected","boss_waits_for_arrival_and_client","boss_delays_hit_until_telegraph_ends","boss_rejected_damage_does_not_credit_reward","locked_recipes_not_disclosed_through_research","locked_catalog_payload_redacted"))
     if(!results.get(check).getAsBoolean())throw new AssertionError(check);
    Files.writeString(Path.of("audit-033.passed"),"PASS: six negative security/gameplay and entrance regression probes\n");
    boss.discard();world.removePlayerImmediately(player,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
    world.setChunkForced(50,50,false);
   }catch(Throwable error){results.addProperty("audit_error",error.toString());error.printStackTrace();}
   try{Files.writeString(Path.of("../../../analysis/codex-audit-032-probes.json").toAbsolutePath().normalize(),new GsonBuilder().setPrettyPrinting().create().toJson(results));}
   catch(Exception error){try{Files.writeString(Path.of("audit-032-probes.json"),new GsonBuilder().setPrettyPrinting().create().toJson(results));}catch(Exception nested){nested.printStackTrace();}}
   System.out.println("AUDIT_032_RESULTS="+results);server.halt(false);
  });
 }
 private static void clientReady(net.minecraft.server.level.ServerPlayer player,boolean ready){
  var level=player.level();
  player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(level.getServer(),new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND),player,net.minecraft.server.network.CommonListenerCookie.createInitial(player.getGameProfile(),false)){
   @Override public boolean hasClientLoaded(){return ready;}
  };
 }
}
