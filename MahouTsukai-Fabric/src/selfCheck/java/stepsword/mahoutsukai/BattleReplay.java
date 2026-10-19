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
     if(!Files.readString(request).strip().equals("enter"))throw new IllegalArgumentException("Unknown local battle replay request");
     Files.delete(request);
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
   }catch(Exception|AssertionError failure){throw new IllegalStateException("Local battle replay failed",failure);}
  });
 }
}
