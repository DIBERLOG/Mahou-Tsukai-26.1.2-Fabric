package stepsword.mahoutsukai.fluids;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.chunk.LevelChunk;
import stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou;
public class MurkyWaterBlock extends LiquidBlock {
 private static final Set<LevelChunk> dirty=new HashSet<>();
 public MurkyWaterBlock(){super(ModFluids.MURKY_STILL,Properties.of().liquid().noLootTable().noCollision().replaceable().strength(100).setId(ResourceKey.create(Registries.BLOCK,Identifier.fromNamespaceAndPath("mahoutsukai","murky_water"))));}
 protected void onPlace(BlockState state,Level world,BlockPos pos,BlockState previous,boolean moving){
  super.onPlace(state,world,pos,previous,moving);
  if(!world.isClientSide()){var chunk=world.getChunkAt(pos);chunk.getAttachedOrCreate(ChunkMahou.TYPE).lakeBlocks.add(pos.immutable());chunk.markUnsaved();dirty.add(chunk);}
 }
 protected void affectNeighborsAfterRemoval(BlockState state,ServerLevel world,BlockPos pos,boolean moving){
  var chunk=world.getChunkAt(pos);chunk.getAttachedOrCreate(ChunkMahou.TYPE).lakeBlocks.remove(pos);chunk.markUnsaved();dirty.add(chunk);
  super.affectNeighborsAfterRemoval(state,world,pos,moving);
 }
 public static void murkyUpdateTick(ServerLevel world){
  if(world.getGameTime()%4!=0)return;
  var iterator=dirty.iterator();while(iterator.hasNext()){
   var chunk=iterator.next();if(chunk.getLevel()!=world)continue;
   chunk.setAttached(ChunkMahou.TYPE,chunk.getAttachedOrCreate(ChunkMahou.TYPE).copy());iterator.remove();
  }
 }
 public static void clear(){dirty.clear();}
 public static void repairLakeOnLoad(ServerLevel world,LevelChunk chunk){
  if(!stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_RETRO_FOG_FIX)return;
  var data=chunk.getAttachedOrCreate(ChunkMahou.TYPE);if(!data.needsRetroFix)return;
  var repaired=data.copy();var origin=chunk.getPos();
  for(var pos:BlockPos.betweenClosed(origin.getMinBlockX(),world.getMinY(),origin.getMinBlockZ(),origin.getMaxBlockX(),world.getMaxY()-1,origin.getMaxBlockZ()))
   if(chunk.getBlockState(pos).getBlock() instanceof MurkyWaterBlock)repaired.lakeBlocks.add(pos.immutable());
  repaired.needsRetroFix=false;chunk.setAttached(ChunkMahou.TYPE,repaired);chunk.markUnsaved();
 }
}
