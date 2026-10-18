package stepsword.mahoutsukai.render.entity.familiar;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class FamiliarRenderState extends LivingEntityRenderState {
   public float flapAngle;
   public int color = -1;
   public ModelFamiliar.State familiarPose = ModelFamiliar.State.STANDING;
}
