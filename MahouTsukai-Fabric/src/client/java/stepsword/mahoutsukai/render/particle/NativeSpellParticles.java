package stepsword.mahoutsukai.render.particle;

import java.util.Collection;
import java.util.List;
import net.minecraft.client.particle.*;
import stepsword.mahoutsukai.fabric.mixin.ParticleEngineAccessor;

/** The native engine owns expiration, capacity eviction, resource reload and dimension changes. */
public final class NativeSpellParticles {
 public static Collection<? extends Particle> current(ParticleEngine engine){
  var group=((ParticleEngineAccessor)engine).mahoutsukai$particles().get(ParticleRenderType.NO_RENDER);
  return group==null?List.of():group.getAll();
 }
}
