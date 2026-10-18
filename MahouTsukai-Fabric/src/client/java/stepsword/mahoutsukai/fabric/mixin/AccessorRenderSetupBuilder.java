package stepsword.mahoutsukai.fabric.mixin;

import java.util.Map;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSetupBuilder.class)
public interface AccessorRenderSetupBuilder {
   @Accessor("textures")
   Map mahou$getTextures();
}
