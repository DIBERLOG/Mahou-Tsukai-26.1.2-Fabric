package stepsword.mahoutsukai.render.entity.kodoku;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.entity.kodoku.KodokuEntity;

public class RenderKodoku extends MobRenderer<KodokuEntity, LivingEntityRenderState, ModelKodoku> {
   private static final Identifier KODOKU = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/kodoku.png");

   public RenderKodoku(Context renderManagerIn) {
      super(renderManagerIn, new ModelKodoku(renderManagerIn.bakeLayer(ModelKodoku.KODOKU_LAYER)), 0.3F);
   }

   public Identifier getTextureLocation(LivingEntityRenderState state) {
      return KODOKU;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }
}
