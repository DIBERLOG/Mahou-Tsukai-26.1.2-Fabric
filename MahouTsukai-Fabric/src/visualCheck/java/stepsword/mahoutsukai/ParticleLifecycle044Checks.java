package stepsword.mahoutsukai;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleResources;
import stepsword.mahoutsukai.render.particle.NativeSpellParticles;
import stepsword.mahoutsukai.render.particle.ParticleLightning;
import stepsword.mahoutsukai.render.particle.ParticleSmokeRing;
import stepsword.mahoutsukai.render.particle.ParticleSphere;
import stepsword.mahoutsukai.render.particle.ParticleSphereExplosion;
import stepsword.mahoutsukai.render.particle.ProjectedFogParticle;

/** Real native queue ownership, without clearing or adding effects to the user's particle engine. */
public final class ParticleLifecycle044Checks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 private static boolean drawable(ParticleEngine engine,Particle particle){return particle.isAlive()&&NativeSpellParticles.current(engine).contains(particle);}
 private static ParticleSphereExplosion explosion(ClientLevel level){
  return new ParticleSphereExplosion(level,new float[]{.85F,.8F,1,1,1,1,1,1},0,0,0);
 }
 private static List<Particle> effects(ClientLevel level){
  var sphere=new ParticleSphere(level,0,0,0,1,0,0,0,"mahoutsukai:textures/block/white.png",.3F,1,1,1,.4F,false,.4);
  var blast=explosion(level);blast.maxRadius=30;blast.addDelay(7);blast.addDelay2(20);
  var ring=new ParticleSmokeRing(level,Color.WHITE,0,0,0);ring.setLifetime(12);ring.delay=7;
  var lightning=new ParticleLightning(level,Color.WHITE,Color.MAGENTA,0,0,0,1,0,0,0);
  return List.of(sphere,blast,ring,lightning,ProjectedFogParticle.ambient(level,0,0,0));
 }
 /** An unrelated particle must retain native ticking and membership in the same NO_RENDER group. */
 private static final class OrdinaryParticle extends Particle {
  int ticks;
  OrdinaryParticle(ClientLevel level,int lifetime){super(level,0,0,0);hasPhysics=false;setLifetime(lifetime);}
  @Override public ParticleRenderType getGroup(){return ParticleRenderType.NO_RENDER;}
  @Override public void tick(){ticks++;super.tick();}
 }
 public static void run(Minecraft mc)throws Exception{
  require(mc.level!=null,"A real client level is required for native particle constructors");
  // Fabric initializes particle providers once. Reuse those resources, preserving their reload hook.
  var resourcesField=ParticleEngine.class.getDeclaredField("resourceManager");resourcesField.setAccessible(true);
  var resources=(ParticleResources)resourcesField.get(mc.particleEngine);
  var reloadField=ParticleResources.class.getDeclaredField("onReload");reloadField.setAccessible(true);
  var originalReload=(Runnable)reloadField.get(resources);
  ParticleEngine engine=null;
  try{
   engine=new ParticleEngine(mc.level,resources);
   resources.onReload(originalReload);
   require(reloadField.get(resources)==originalReload,"Constructing the isolated engine must retain the real engine's reload callback");
   var particles=effects(mc.level);
   require(NativeSpellParticles.current(engine).isEmpty(),"Constructing an effect must not make it render before native admission");
   for(var particle:particles)engine.add(particle);
   require(NativeSpellParticles.current(engine).isEmpty(),"Pending particles wait for the native engine's next tick");
   var ordinary=new OrdinaryParticle(mc.level,4);engine.add(ordinary);engine.tick();
   require(NativeSpellParticles.current(engine).size()==6,"All five real custom types and an unrelated particle enter the native group");
   for(var particle:particles)require(drawable(engine,particle),"An admitted live effect is renderable: "+particle.getClass().getSimpleName());
   for(int i=0;i<20;i++)engine.tick();
   require(ordinary.ticks==5&&!ordinary.isAlive(),"The unrelated particle keeps its exact native age and expiry");
   require(particles.get(1).isAlive()&&drawable(engine,particles.get(1)),"The actual delayed explosion stays visible through its intended hold, rather than being deleted early");
   for(int i=0;i<810;i++)engine.tick();
   for(var particle:particles)require(!particle.isAlive()&&!drawable(engine,particle),"Native lifetime removes the complete effect: "+particle.getClass().getSimpleName());
   require(NativeSpellParticles.current(engine).isEmpty()&&engine.countParticles().equals("0"),"Natural expiry leaves no manually renderable effects or native particles");

   var removed=effects(mc.level);for(var particle:removed)engine.add(particle);engine.tick();
   for(var particle:removed){particle.remove();require(!drawable(engine,particle),"An explicitly removed effect cannot render before the native queue is pruned");}
   engine.tick();require(NativeSpellParticles.current(engine).isEmpty(),"The native engine prunes explicitly removed effects");

   var cleared=effects(mc.level);for(var particle:cleared)engine.add(particle);engine.tick();engine.clearParticles();
   require(NativeSpellParticles.current(engine).isEmpty(),"Native clearParticles immediately removes the renderer's only source");
   for(var particle:cleared)require(particle.isAlive()&&!drawable(engine,particle),"A cleared but still-alive object must never become a frozen sphere");
   var pending=explosion(mc.level);engine.add(pending);engine.clearParticles();engine.tick();
   require(pending.isAlive()&&!drawable(engine,pending),"Clearing the pending admission queue cannot leave a constructor-registered ghost");

   var previousWorld=effects(mc.level);for(var particle:previousWorld)engine.add(particle);engine.tick();engine.setLevel(null);
   for(var particle:previousWorld)require(particle.isAlive()&&!drawable(engine,particle),"Native world unload removes render eligibility without depending on Particle.remove");
   engine.setLevel(mc.level);engine.tick();require(NativeSpellParticles.current(engine).isEmpty(),"Reassigning the client level cannot resurrect old effects");

   var evicted=explosion(mc.level);engine.add(evicted);
   ParticleSphereExplosion newest=null;
   for(int i=0;i<16384;i++){newest=explosion(mc.level);engine.add(newest);}
   engine.tick();
   require(NativeSpellParticles.current(engine).size()==16384,"The actual native particle group enforces its 16384-entry cap");
   require(evicted.isAlive()&&!drawable(engine,evicted),"A live effect silently evicted by the native queue cannot remain visible");
   require(newest!=null&&drawable(engine,newest),"The newest admitted custom effect remains available");
   var unrelated=new OrdinaryParticle(mc.level,10);engine.add(unrelated);engine.tick();
   require(NativeSpellParticles.current(engine).contains(unrelated)&&unrelated.isAlive(),"Custom effect rendering does not filter an unrelated particle out of the native group");
   engine.tick();require(unrelated.ticks==1,"Native ticking of unrelated particles survives a saturated custom queue");
   engine.clearParticles();require(NativeSpellParticles.current(engine).isEmpty(),"Clearing a saturated group leaves no residual spheres");
   Files.writeString(Path.of("particle-lifecycle-check.passed"),"PASS: five real custom particle types in an isolated native ParticleEngine; constructor/pending admission, delayed explosion hold, natural lifetime, explicit remove, clearParticles, pending clear, setLevel unload/reassignment and 16385-custom-particle eviction at native cap 16384; unrelated particle membership and ticking preserved; user's actual engine and world untouched\n");
  }finally{
   if(engine!=null)engine.clearParticles();
   resources.onReload(originalReload);
   require(reloadField.get(resources)==originalReload,"The original resource reload callback must be restored even after a failed check");
  }
 }
}
