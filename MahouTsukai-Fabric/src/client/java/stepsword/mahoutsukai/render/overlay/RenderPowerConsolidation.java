package stepsword.mahoutsukai.render.overlay;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.util.Utils;
/** Original lake proximity, gray mist blend and fourth-power density curve, adapted to native FogData. */
public final class RenderPowerConsolidation {
 public static float distanceToMurkyWater(Entity entity){
  if(entity==null)return -1;
  int r=(int)MTConfig.POWER_CONSOLIDATION_FOG_RADIUS;
  var center=entity.blockPosition();var min=ChunkPos.containing(center.offset(-r,-r,-r));var max=ChunkPos.containing(center.offset(r,r,r));float nearest=-1;
  for(int x=min.x();x<=max.x();x++)for(int z=min.z();z<=max.z();z++){
   if(!entity.level().hasChunk(x,z))continue;
   var data=entity.level().getChunk(x,z).getAttached(ChunkMahou.TYPE);if(data==null)continue;
   for(var p:data.lakeBlocks){
    if(Math.abs(p.getX()-center.getX())<r && Math.abs(p.getY()-center.getY())<r && Math.abs(p.getZ()-center.getZ())<r && entity.level().getBlockState(p).is(ModBlocks.murkyFluid.get())){
     float d=(float)entity.position().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(p));if(nearest<0 || d<nearest)nearest=d;
    }
   }
  }
  return nearest;
 }
 public static void apply(Entity entity,FogData fog){
  if(entity==null)return;
  if(Utils.isInMurkyWater(entity)){
   fog.environmentalStart=-8;fog.environmentalEnd=5;fog.color.set(0,60/255f,0,1);return;
  }
  double top=MTConfig.POWER_CONSOLIDATION_FOG_RADIUS,d=distanceToMurkyWater(entity);
  if(d<0 || top<=0 || d>top)return;
  float strength=(float)(2*(top-d)/top),weight=1+strength;
  fog.color.set((fog.color.x+180/255f*strength)/weight,(fog.color.y+180/255f*strength)/weight,(fog.color.z+180/255f*strength)/weight,fog.color.w);
  if(d>0){
   double factor=1-Math.min(50-50/top*d,60)/60;
   double scale=factor*factor*factor*factor;
   float start=fog.environmentalStart,end=fog.environmentalEnd;
   fog.environmentalStart=start==0?0:net.minecraft.util.Mth.clamp((float)(scale*start),1,start);
   fog.environmentalEnd=net.minecraft.util.Mth.clamp((float)(scale*end),4,end);
  }
 }
}
