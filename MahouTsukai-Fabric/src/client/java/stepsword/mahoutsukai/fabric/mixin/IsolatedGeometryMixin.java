package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import com.llamalad7.mixinextras.sugar.Local;

/** Native custom geometry shares a buffer per layer, even across separate entities. */
@Mixin(CustomFeatureRenderer.Storage.class)
public abstract class IsolatedGeometryMixin {
 @ModifyVariable(method="add",at=@At("HEAD"),argsOnly=true)
 private SubmitNodeCollector.CustomGeometryRenderer mahou$isolate(SubmitNodeCollector.CustomGeometryRenderer renderer,@Local(argsOnly=true) RenderType type){
  if(!type.pipeline().getLocation().getNamespace().equals("mahoutsukai")||type.pipeline().getVertexFormatMode()!=com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP)return renderer;
  return (pose,vertices)->{var strip=new stepsword.mahoutsukai.render.IsolatedTriangleStrip(vertices);renderer.render(pose,strip);strip.finish();};
 }
}
