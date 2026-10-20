package stepsword.mahoutsukai.fabric.mixin;

import java.util.Map;
import net.minecraft.client.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
 @Accessor("particles") Map<ParticleRenderType,ParticleGroup<?>> mahoutsukai$particles();
}
