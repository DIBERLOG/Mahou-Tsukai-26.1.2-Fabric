package stepsword.mahoutsukai;

import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.entity.mahoujin.WilliamEntity;
import stepsword.mahoutsukai.item.ModItems;

/**
 * Native checks for the William artifact entity's motion bookkeeping. The renderer interpolates
 * the book's roll from {@code py}; it must hold the previous roll, otherwise every frame
 * interpolates from zero and the book visually snaps and spins erratically.
 */
public final class WilliamFlightChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}

 public static void run(net.minecraft.server.level.ServerPlayer fixture){
  var world=fixture.level();
  var william=new WilliamEntity(world,fixture,1.0F,new ItemStack(ModItems.william.get()));
  william.setPos(fixture.getX()+2.0,fixture.getY()+1.0,fixture.getZ()+2.0);
  william.setRotationRoll(90.0F);
  william.tick();
  require(Math.abs(william.py-90.0F)<0.001F,"William remember the previous roll for smooth interpolation, got py="+william.py);
  william.setRotationRoll(200.0F);
  william.tick();
  require(Math.abs(william.py-200.0F)<0.001F,"William updates the interpolation base on every tick, got py="+william.py);
  require(Math.abs(william.getDeltaMovement().y)<=0.05,"William hover stays gentle instead of accelerating away");
  // Regression for the watchdog collision sweep: corrupt boxes and extreme impulses stay bounded.
  for(var motion:java.util.List.of(new net.minecraft.world.phys.Vec3(1e9,1e9,-1e9),new net.minecraft.world.phys.Vec3(Double.NaN,0,0),new net.minecraft.world.phys.Vec3(0,Double.POSITIVE_INFINITY,0))){
   var before=william.position();
   william.setBoundingBox(new net.minecraft.world.phys.AABB(-64,Double.NEGATIVE_INFINITY,-64,64,Double.POSITIVE_INFINITY,64));
   william.move(net.minecraft.world.entity.MoverType.SELF,motion);
   require(william.position().distanceTo(before)<=.5001,"William collision sweep is bounded");
   require(william.getBoundingBox().getYsize()<=1.001,"William repairs nonfinite physical collision box");
  }
  long started=System.nanoTime();
  for(int i=0;i<2400;i++){william.tick();require(Double.isFinite(william.getY()),"Long hover remains finite");}
  require(System.nanoTime()-started<10_000_000_000L,"2400 William ticks finish without watchdog-scale collision scan");
  william.discard();
  System.out.println("PASS: WILLIAM_FLIGHT: previous-roll interpolation, gentle hover, invalid impulse/box recovery and 2400 ticks");
 }
}
