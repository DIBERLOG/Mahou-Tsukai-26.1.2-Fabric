package stepsword.mahoutsukai.render.pose;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;


public class ClarentPose {
   private static final float RIGHT_X = -8.0F;
   private static final float ARM_Y = 2.0F;
   private static final float ARM_Z = 0.0F;
   private static final float X_ROT = (float)Math.toRadians(150.0);
   private static final float Y_ROT = (float)Math.toRadians(180.0);
   private static final float Z_ROT = (float)Math.toRadians(-20.0);

   public static void applyTransform(HumanoidModel<?> model, HumanoidRenderState entity, HumanoidArm arm) {
      if (arm == HumanoidArm.RIGHT) {
         poseArm(model.rightArm, -8.0F, 2.0F, 0.0F, X_ROT, Y_ROT, Z_ROT);
      } else {
         poseArm(model.leftArm, 8.0F, 2.0F, 0.0F, X_ROT, -Y_ROT, -Z_ROT);
      }
   }

   private static void poseArm(ModelPart arm, float x, float y, float z, float xRot, float yRot, float zRot) {
      arm.x = x;
      arm.y = y;
      arm.z = z;
      arm.xRot = xRot;
      arm.yRot = yRot;
      arm.zRot = zRot;
   }
}
