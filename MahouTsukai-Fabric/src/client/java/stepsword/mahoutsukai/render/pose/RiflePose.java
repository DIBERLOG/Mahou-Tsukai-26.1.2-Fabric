package stepsword.mahoutsukai.render.pose;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;


public class RiflePose {
   public static void applyTransform(HumanoidModel<?> model, HumanoidRenderState entity, HumanoidArm arm) {
      float bob = entity.ageInTicks;
      model.rightArm.yRot = -0.1F + model.head.yRot;
      model.leftArm.yRot = 0.1F + model.head.yRot + 0.4F;
      model.rightArm.xRot = (float) (-Math.PI / 2) + model.head.xRot;
      model.leftArm.xRot = (float) (-Math.PI / 2) + model.head.xRot;
      if (arm == HumanoidArm.RIGHT) {
         unbobModelPart(model.rightArm, bob, 1.0F);
         unbobModelPart(model.leftArm, bob, -1.0F);
      }
   }

   public static void unbobModelPart(ModelPart pModelPart, float pAgeInTicks, float pMultiplier) {
      pModelPart.zRot = pModelPart.zRot - pMultiplier * (Mth.cos(pAgeInTicks * 0.09F) * 0.05F + 0.05F);
      pModelPart.xRot = pModelPart.xRot - pMultiplier * Mth.sin(pAgeInTicks * 0.067F) * 0.05F;
   }
}
