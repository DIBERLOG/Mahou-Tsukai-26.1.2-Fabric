package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.displacement.MentalDisplacementSpellEffect;
import stepsword.mahoutsukai.entity.MentalDisplacementEntity;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.networking.DoneMentalPacket;

/**
 * Native checks for the reworked spiritual (mental) displacement: the body must follow the
 * controlled form, the form must expire after the configured duration, and arrival must be safe.
 * <p>The fixture is a small platform high in the air and every placed block is restored, so the
 * shared rain/Emrys fixture in the same test world is never disturbed.</p>
 */
public final class MentalDisplacementChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static final BlockPos BASE=new BlockPos(575,300,530);
 private static final int PLATFORM=7;

 public static void run(ServerPlayer fixture)throws Exception{
  var world=fixture.level();
  require(MTConfig.MENTAL_DISPLACEMENT_MAX_TICKS==1200,"Controlled travel defaults to 60 seconds");
  var player=DoomIntegration.player(world,"MentalDisplacement032",GameType.SURVIVAL,560);
  int chunkX=BASE.getX()>>4,chunkZ=BASE.getZ()>>4;
  world.setChunkForced(chunkX,chunkZ,true);
  world.getChunk(chunkX,chunkZ);
  try{
   for(int dx=-PLATFORM;dx<=PLATFORM;dx++)for(int dz=-PLATFORM;dz<=PLATFORM;dz++){
    world.setBlockAndUpdate(BASE.offset(dx,0,dz),Blocks.STONE.defaultBlockState());
    for(int dy=1;dy<=4;dy++)world.setBlockAndUpdate(BASE.offset(dx,dy,dz),Blocks.AIR.defaultBlockState());
   }
   player.teleportTo(BASE.getX()+.5,BASE.getY()+1,BASE.getZ()+.5);
   player.setItemInHand(InteractionHand.MAIN_HAND,new net.minecraft.world.item.ItemStack(ModItems.mentalDisplacementScroll.get()));

   // Leaving the body is a real server action that remembers the origin for the travel radius.
   require(MentalDisplacementSpellEffect.leaveBody(player),"Leaving the body creates the controlled form");
   require(stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).getMentalDisplacementPos()!=null,"Origin is saved while the spirit travels");
   discardOwned(world,player);
   stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).setMentalDisplacementPos(null);
   // The controlled form counts down and delivers the body where it stopped.
   Vec3 origin=player.position();
   var spirit=new MentalDisplacementEntity(world,BASE,player);
   spirit.setOwnerId(player);
   spirit.player=player;
   require(spirit.ticksLeft()==MTConfig.MENTAL_DISPLACEMENT_MAX_TICKS,"Countdown starts at the configured duration");
   for(int i=0;i<MTConfig.MENTAL_DISPLACEMENT_MAX_TICKS;i++)spirit.tick();
   require(spirit.isRemoved(),"Controlled form expires after the configured duration");
   require(player.position().distanceTo(origin)<1.6,"Expiry delivers the body to where the spirit stopped");

   // Arrival is refused inside a wall; the body must land in free space with head room.
   BlockPos wall=new BlockPos(BASE.getX()-3,BASE.getY()+1,BASE.getZ());
   for(int dy=0;dy<3;dy++)world.setBlockAndUpdate(wall.above(dy),Blocks.STONE.defaultBlockState());
   require(MentalDisplacementSpellEffect.leaveBody(player),"Second controlled form created");
   discardOwned(world,player);
   MentalDisplacementSpellEffect.arrive(player,new Vec3(wall.getX()+.5,wall.getY(),wall.getZ()+.5));
   require(world.getBlockState(BlockPos.containing(player.position())).isAir(),"Arrival never lands inside a wall");
   require(world.getBlockState(BlockPos.containing(player.position()).above()).isAir(),"Arrival leaves head room");

   // Forged destinations must be ignored: only an owned server-side spirit authorizes exit.
   BlockPos exit=BASE.offset(2,1,2);
   var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);
   Vec3 beforePacket=player.position();
   DoneMentalPacket.onMessage(new DoneMentalPacket(40000000,301,40000000),player);
   require(player.position().distanceToSqr(beforePacket)<.001,"No active travel rejects exit packet");
   mahou.setMentalDisplacementPos(player.blockPosition());
   DoneMentalPacket.onMessage(new DoneMentalPacket(exit.getX(),exit.getY(),exit.getZ()),player);
   require(player.position().distanceToSqr(beforePacket)<.001,"Stale travel without owned spirit cannot teleport");
   mahou.setMentalDisplacementPos(player.blockPosition());
   var owned=new MentalDisplacementEntity(world,player.blockPosition(),player);
   owned.setOwnerId(player);owned.setPos(exit.getX()+.5,exit.getY(),exit.getZ()+.5);
   require(world.addFreshEntity(owned),"Server spirit inserted for authorized early exit");
   DoneMentalPacket.onMessage(new DoneMentalPacket(-40000000,-1000,-40000000),player);
   require(owned.isRemoved(),"Early exit discards the actual owned spirit");
   require(player.position().distanceTo(owned.position())<.01,"Early exit uses server spirit position, ignores forged packet coordinates");
   require(mahou.getMentalDisplacementPos()==null,"Early exit clears saved state");
   Vec3 afterExit=player.position();
   DoneMentalPacket.onMessage(new DoneMentalPacket(800,301,800),player);
   require(player.position().distanceToSqr(afterExit)<.001,"Replayed exit cannot move player");

   // An unloaded or out-of-world target never moves the body.
   Vec3 before=player.position();
   MentalDisplacementSpellEffect.arrive(player,new Vec3(40000000,64,40000000));
   require(player.position().distanceTo(before)<.01,"An unloaded or out-of-world target never moves the body");
   System.out.println("PASS: MENTAL_DISPLACEMENT_032: 60 s countdown, expiry delivery, early exit, state cleanup, wall, head room and out-of-world safety");
  }finally{
   discardOwned(world,player);
   stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).setMentalDisplacementPos(null);
   for(int dx=-PLATFORM;dx<=PLATFORM;dx++)for(int dz=-PLATFORM;dz<=PLATFORM;dz++)for(int dy=0;dy<=4;dy++)world.setBlockAndUpdate(BASE.offset(dx,dy,dz),Blocks.AIR.defaultBlockState());
   world.removePlayerImmediately(player,Entity.RemovalReason.DISCARDED);
   world.setChunkForced(chunkX,chunkZ,false);
  }
 }

 /** Best-effort cleanup of any controlled form owned by this player. */
 private static void discardOwned(net.minecraft.server.level.ServerLevel world,ServerPlayer player){
  var area=new net.minecraft.world.phys.AABB(player.blockPosition()).inflate(256);
  for(var spirit:world.getEntitiesOfClass(MentalDisplacementEntity.class,area,e->player.getUUID().equals(e.getOwnerId())))spirit.discard();
 }
}
