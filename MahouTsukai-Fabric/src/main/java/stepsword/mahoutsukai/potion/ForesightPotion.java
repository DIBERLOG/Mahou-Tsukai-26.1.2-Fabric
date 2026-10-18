package stepsword.mahoutsukai.potion;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.butterfly.ButterflyEntity;
public class ForesightPotion extends net.minecraft.world.effect.MobEffect {
 protected ForesightPotion(){super(net.minecraft.world.effect.MobEffectCategory.NEUTRAL,ModEffects.getColorNumber(252,45,45));}
 public static boolean checkBlockAndChunk(BlockPos pos,Level level){
  if(MTConfig.BUTTERFLY_EFFECT_BLOCK_LIMIT<=0 || MTConfig.BUTTERFLY_EFFECT_CHUNK_LIMIT<=0)return false;
  var cp=new ChunkPos(pos.getX()>>4,pos.getZ()>>4);var all=level.getEntitiesOfClass(ButterflyEntity.class,new AABB(cp.getMinBlockX(),level.getMinY(),cp.getMinBlockZ(),cp.getMaxBlockX()+1,level.getMaxY(),cp.getMaxBlockZ()+1));
  return all.size()<MTConfig.BUTTERFLY_EFFECT_CHUNK_LIMIT && all.stream().filter(e->pos.equals(e.origPos)).count()<MTConfig.BUTTERFLY_EFFECT_BLOCK_LIMIT;
 }
 private static ButterflyEntity butterfly(Player p){return new ButterflyEntity(p.level(),p,p.getRandom().nextInt(6000000),p.getRandom().nextFloat()*.5f+.2f);}
 private static boolean add(Player p,ButterflyEntity b){if(!checkBlockAndChunk(b.origPos,p.level()) || !p.level().addFreshEntity(b))return false;stepsword.mahoutsukai.advancements.ModTriggers.BUTTERFLY.trigger((ServerPlayer)p);if(MTConfig.BUTTERFLY_EFFECT_SINGLE_USE)p.removeEffect(ModEffects.FORESIGHT);return true;}
 public static boolean deferPlace(Player p,BlockPos pos,Direction face,BlockState state,ItemStack consumed){if(p==null || p.level().isClientSide() || !p.hasEffect(ModEffects.FORESIGHT) || state.hasBlockEntity())return false;var b=butterfly(p);b.setPlaceEffect(pos,face,state,consumed);return add(p,b);}
 public static boolean deferBreak(Level world,Player p,BlockPos pos,BlockState state){if(world.isClientSide() || !p.hasEffect(ModEffects.FORESIGHT) || state.isAir() || state.hasBlockEntity())return false;var hit=p.pick(8,1,false);if(!(hit instanceof BlockHitResult r))return false;var b=butterfly(p);b.setBreakEffect(pos,r.getDirection(),state);return add(p,b);}
 public static void butterflyHit(Player p){if(p==null || p.level().isClientSide() || p.isSpectator() || p.hasEffect(ModEffects.BOUND) || !p.hasEffect(ModEffects.FORESIGHT))return;var b=butterfly(p);b.setHitEffect(p,p.getMainHandItem(),p.getOffhandItem());add(p,b);}
}
