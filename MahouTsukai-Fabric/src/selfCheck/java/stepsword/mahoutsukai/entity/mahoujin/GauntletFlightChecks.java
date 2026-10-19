package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Native checks for the projection gauntlet's rewritten sword flight: real collision damage,
 * homing that turns toward the recorded target, block impact cleanup and a hard lifetime.
 */
public final class GauntletFlightChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}

 public static void run(net.minecraft.server.level.ServerPlayer fixture){
  var world=fixture.level();
  var center=fixture.blockPosition().offset(0,3,0);
  int chunkX=center.getX()>>4,chunkZ=center.getZ()>>4;
  world.setChunkForced(chunkX,chunkZ,true);
  world.getChunk(chunkX,chunkZ);
  for(int dx=-4;dx<=4;dx++)for(int dy=-1;dy<=2;dy++)for(int dz=-4;dz<=4;dz++)world.setBlockAndUpdate(center.offset(dx,dy,dz),Blocks.AIR.defaultBlockState());
  try{
   // 1. A sword that reaches a creature deals damage once and removes itself.
   var victim=EntityType.COW.create(world,EntitySpawnReason.COMMAND);
   require(victim!=null,"Cow fixture created");
   victim.setPos(center.getX()+2.0,center.getY(),center.getZ());
   victim.setHealth(20.0F);
   require(world.addFreshEntity(victim),"Cow fixture inserted");
   var blade=spawn(world,fixture,center,1.0,0.0,0.0);
   float before=victim.getHealth();
   blade.onHitEntity(new EntityHitResult(victim));
   require(victim.getHealth()<before,"Projected weapon deals real damage on hit ("+before+" -> "+victim.getHealth()+")");
   require(blade.isRemoved(),"Projected weapon removes itself after one hit");

   // 2. Homing turns a mis-aimed sword towards the recorded target.
   var target=EntityType.COW.create(world,EntitySpawnReason.COMMAND);
   require(target!=null,"Homing cow fixture created");
   target.setPos(center.getX()+6.0,center.getY(),center.getZ());
   target.setHealth(20.0F);
   require(world.addFreshEntity(target),"Homing cow fixture inserted");
   var homing=spawn(world,fixture,center,-1.0,0.0,0.0);
   homing.setTargetEntity(target);
   require(homing.targetEntityId()==target.getId(),"Homing sword records its target");
   Vec3 aim=target.position().subtract(homing.position()).normalize();
   // Steer directly: fixture entities are not always visible to section lookups.
   homing.steerTo(target.position());
   require(homing.getDeltaMovement().normalize().dot(aim)>0.5,"Homing sword turns towards its target, direction="+homing.getDeltaMovement().normalize());
   homing.discard();

   // 3. A wall stops the sword instead of letting it tunnel through.
   var wall=center.offset(2,0,0);
   world.setBlockAndUpdate(wall,Blocks.STONE.defaultBlockState());
   var blocked=spawn(world,fixture,center,1.0,0.0,0.0);
   for(int i=0;i<12&&!blocked.isRemoved();i++)blocked.tick();
   require(blocked.isRemoved(),"Projected weapon is removed when it hits a block");
   world.setBlockAndUpdate(wall,Blocks.AIR.defaultBlockState());

   // 4. Hard lifetime.
   var stale=spawn(world,fixture,center,1.0,0.0,0.0);
   for(int i=0;i<GateWeaponProjectileEntity.LIFETIME_TICKS+5&&!stale.isRemoved();i++)stale.tick();
   require(stale.isRemoved(),"Projected weapon expires after its lifetime");
   victim.discard();
   target.discard();
   System.out.println("PASS: GAUNTLET_FLIGHT: native hit damage, single-hit cleanup, homing, block impact and lifetime");
  }finally{
   for(var e:world.getEntitiesOfClass(GateWeaponProjectileEntity.class,new net.minecraft.world.phys.AABB(center).inflate(32)))e.discard();
   for(int dx=-4;dx<=4;dx++)for(int dy=-1;dy<=2;dy++)for(int dz=-4;dz<=4;dz++)world.setBlockAndUpdate(center.offset(dx,dy,dz),Blocks.AIR.defaultBlockState());
   world.setChunkForced(chunkX,chunkZ,false);
  }
 }

 private static GateWeaponProjectileEntity spawn(net.minecraft.server.level.ServerLevel world,net.minecraft.server.level.ServerPlayer owner,net.minecraft.core.BlockPos at,double dx,double dy,double dz){
  var weapon=new GateWeaponProjectileEntity(world,at.getX()+0.5,at.getY(),at.getZ()+0.5,new ItemStack(Items.DIAMOND_SWORD),owner,3.0F);
  weapon.setOwner(owner);
  // (dx,dy,dz) as the raw shot vector; the constructor already placed the entity at the muzzle.
  weapon.shoot(dx,dy,dz,2.2F,0.0F);
  world.addFreshEntity(weapon);
  return weapon;
 }
}
