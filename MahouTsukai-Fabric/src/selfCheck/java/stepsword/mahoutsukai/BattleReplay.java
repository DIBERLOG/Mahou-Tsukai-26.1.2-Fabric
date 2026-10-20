package stepsword.mahoutsukai;
import java.nio.file.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.util.Utils;
/** Local development replay; never included in the production mod or registered in a normal server. */
public final class BattleReplay {
 public static void init(){
  var request=Path.of("battle-replay.request");int[] pending={0};
  ServerTickEvents.END_SERVER_TICK.register(server->{
   try{
    var player=server.getPlayerList().getPlayerByName("Player733");
    if(player==null||!player.isAlive())return;
    if(Files.isRegularFile(request)){
     String action=Files.readString(request).strip();
     Files.delete(request);
     if(action.equals("recast")){
      if(!RealityMarbleSpellEffect.inMarble(player.level()))throw new AssertionError("Recast probe needs the actual arena");
      var mahou=Utils.getPlayerMahou(player);var boss=RealityMarbleBossEntity.liveFor(player.level(),mahou.getEnemy());
      if(boss==null)throw new AssertionError("Recast probe needs the live linked opponent");
      var position=player.position();var bossPosition=boss.position();var origin=mahou.getPlayerSavedSpawn();String dimension=mahou.getPlayerOldDimension();int mana=mahou.getStoredMana();
      var look=boss.getBoundingBox().getCenter().subtract(player.getEyePosition());
      player.setYRot((float)Math.toDegrees(Math.atan2(-look.x,look.z)));player.setXRot((float)-Math.toDegrees(Math.atan2(look.y,Math.sqrt(look.x*look.x+look.z*look.z))));
      boolean cast=stepsword.mahoutsukai.mana.PlayerManaManager.castPaid(player,stepsword.mahoutsukai.config.MTConfig.MARBLE_MANA_COST,()->RealityMarbleSpellEffect.goToMarble(player));
      if(cast||mahou.getStoredMana()!=mana||!boss.getUUID().equals(mahou.getEnemy())||!player.position().equals(position)||!boss.position().equals(bossPosition)||!java.util.Objects.equals(origin,mahou.getPlayerSavedSpawn())||!java.util.Objects.equals(dimension,mahou.getPlayerOldDimension()))throw new AssertionError("A live aimed recast must leave mana, positions and encounter origins intact");
      long count=player.level().getEntitiesOfClass(RealityMarbleBossEntity.class,boss.getBoundingBox().inflate(64),e->e.isAlive()&&!e.isRemoved()).size();
      if(count!=1)throw new AssertionError("Recast must retain exactly one living Archmage in this arena: "+count);
      Files.writeString(Path.of("battle-recast.passed"),"PASS: actual client in Reality Marble; aimed paid recast refused, exactly one Archmage, same enemy UUID and positions, unchanged mana and return origin; boss="+boss.getUUID()+"\n");
      System.out.println("PASS: BATTLE_RECAST_038");return;
     }
     if(!action.equals("enter"))throw new IllegalArgumentException("Unknown local battle replay request");
     if(RealityMarbleSpellEffect.inMarble(player.level()))throw new IllegalStateException("Already in Reality Marble; leave normally before a fresh entry");
     var mahou=Utils.getPlayerMahou(player);
     var marble=server.getLevel(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,RealityMarbleSpellEffect.marble));
     var previous=marble==null?null:RealityMarbleBossEntity.liveFor(marble,mahou.getEnemy());
     // A replay explicitly replaces only this development player's previous opponent.
     if(previous!=null)previous.discard();
     mahou.setEnemy(null);
     // Clear only the previous development battle, preserving natural bosses and user summons.
     for(var level:server.getAllLevels())for(var entity:level.getAllEntities())
      if(entity instanceof RealityMarbleBossEntity&&entity.entityTags().contains("Archmage034Battle"))entity.discard();
     player.setXRot(90); // A solo entry; do not pull an unrelated cursor target from the old arena.
     if(!RealityMarbleSpellEffect.goToMarble(player))throw new AssertionError("Natural Reality Marble entry failed");
     pending[0]=10;
    }
    if(pending[0]>0&&--pending[0]==0){
     if(!RealityMarbleSpellEffect.inMarble(player.level()))throw new AssertionError("Player did not arrive in Reality Marble");
     var enemy=Utils.getPlayerMahou(player).getEnemy();var entity=enemy==null?null:player.level().getEntity(enemy);
     if(!(entity instanceof RealityMarbleBossEntity boss)||!boss.isAlive())throw new AssertionError("Solo entry must retain its live boss as the opponent");
     var support=net.minecraft.core.BlockPos.containing(player.getX(),player.getY()-.01,player.getZ());
     if(player.level().getBlockState(support).getCollisionShape(player.level(),support).isEmpty())throw new AssertionError("The real client must arrive on solid terrain");
     boss.setHealth(boss.getMaxHealth());
     player.teleportTo(player.level(),player.getX(),player.getY(),player.getZ(),java.util.Set.of(),0,0,false);
     Files.writeString(Path.of("battle-replay.passed"),"PASS: natural grounded Reality Marble entry, live arena boss and exact enemy UUID; player="+player.getUUID()+" boss="+boss.getUUID()+" position="+player.position()+" bossPosition="+boss.position()+" arrivalTicks="+boss.arrivalTicks()+"\n");
     System.out.println("PASS: BATTLE_REPLAY_REALITY_MARBLE");
    }
   }catch(Exception|AssertionError failure){
    pending[0]=0;
    System.err.println("Local battle replay check failed: "+failure.getMessage());
    try{Files.writeString(Path.of("battle-replay.failed"),failure.toString()+"\n");}catch(java.io.IOException io){io.printStackTrace();}
   }
  });
 }
}
