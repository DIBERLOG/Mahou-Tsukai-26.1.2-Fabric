package stepsword.mahoutsukai;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect;

/** Native terrain checks, called only by the isolated server check runner. */
public final class GroundSpawn036Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}

 public static void run(ServerPlayer fixture){
  ServerLevel world=fixture.level();
  var center=new BlockPos(1424,260,1424);
  Map<BlockPos,BlockState> previous=new HashMap<>();
  try{
   layer(world,center,Blocks.STONE.defaultBlockState(),previous);
   Vec3 ground=RealityMarbleSpellEffect.safeMarbleSpawn(world,new BlockPos(1424,120,1424),1.5,3);
   require(ground!=null && ground.equals(new Vec3(1424.5,261,1424.5)),"Stale Y=120 is replaced by actual supported ground for a three-block boss");
   require(ground.equals(RealityMarbleSpellEffect.safeMarbleSpawn(world,new BlockPos(1424,310,1424),.6,3)),"Player and boss use the same physical floor despite stored sky coordinates");

   layer(world,center,Blocks.STONE_SLAB.defaultBlockState(),previous);
   Vec3 slab=RealityMarbleSpellEffect.safeMarbleSpawn(world,center,1.5,3);
   require(slab!=null && Math.abs(slab.y-260.5)<.0001,"Spawn uses collision top of bottom slabs, without floating half a block");

   layer(world,center,Blocks.STONE.defaultBlockState(),previous);
   layer(world,center.above(),Blocks.WATER.defaultBlockState(),previous);
   require(RealityMarbleSpellEffect.safeMarbleSpawn(world,center,1.5,3)==null,"A flooded area cannot become an underwater spawn or a hovering fallback");
   layer(world,center.above(),Blocks.AIR.defaultBlockState(),previous);
   layer(world,center.above(),Blocks.LAVA.defaultBlockState(),previous);
   require(RealityMarbleSpellEffect.safeMarbleSpawn(world,center,1.5,3)==null,"A lava-covered area is rejected throughout the bounded search");
   layer(world,center.above(),Blocks.AIR.defaultBlockState(),previous);

   layer(world,center.above(3),Blocks.STONE.defaultBlockState(),previous);
   Vec3 roof=RealityMarbleSpellEffect.safeMarbleSpawn(world,center,1.5,3);
   require(roof!=null && roof.y==264 && world.noCollision(null,new AABB(roof.x-.75,roof.y+.001,roof.z-.75,roof.x+.75,roof.y+3,roof.z+.75)),"A low ceiling never clips the archmage: safe arrival is on the clear roof");
   layer(world,center.above(3),Blocks.AIR.defaultBlockState(),previous);

   var raised=center.above(20);
   layer(world,raised,Blocks.STONE.defaultBlockState(),previous);
   Vec3 raisedSpawn=RealityMarbleSpellEffect.safeMarbleSpawn(world,center,1.5,3);
   require(raisedSpawn!=null && raisedSpawn.y==281,"Changed terrain height is resolved on every arrival");

   var outside=new BlockPos(30_000_100,120,30_000_100);
   require(!world.hasChunkAt(outside),"Border fixture starts outside generated chunks");
   require(RealityMarbleSpellEffect.safeMarbleSpawn(world,outside,1.5,3)==null && !world.hasChunkAt(outside),"An out-of-border hint cannot generate chunks or return an unsafe fallback");
   require(RealityMarbleSpellEffect.safeMarbleSpawn(world,center,Double.NaN,3)==null && RealityMarbleSpellEffect.safeMarbleSpawn(world,center,1.5,-1)==null,"Invalid entity dimensions are rejected");
  }finally{
   previous.forEach((pos,state)->world.setBlockAndUpdate(pos,state));
  }
  System.out.println("PASS: GROUNDED_MARBLE_036: stale sky Y, three-block clearance, fractional collision support, water/lava rejection, low ceiling, changed terrain and border without chunk generation");
 }

 private static void layer(ServerLevel world,BlockPos center,BlockState state,Map<BlockPos,BlockState> previous){
  for(var at:BlockPos.betweenClosed(center.offset(-9,0,-9),center.offset(9,0,9))){
   BlockPos pos=at.immutable();
   previous.putIfAbsent(pos,world.getBlockState(pos));
   world.setBlockAndUpdate(pos,state);
  }
 }
}
