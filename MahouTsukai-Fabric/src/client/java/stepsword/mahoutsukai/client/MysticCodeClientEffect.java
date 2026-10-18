package stepsword.mahoutsukai.client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import stepsword.mahoutsukai.render.RenderUtils;
public final class MysticCodeClientEffect {
   public static void renderArmFirstPerson(
      PoseStack matrixStackIn, SubmitNodeCollector submitNodeCollector, int combinedLightIn, float equippedProgress, float swingProgress, HumanoidArm side
   ) {
      matrixStackIn.pushPose();
      boolean flag = side != HumanoidArm.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = Mth.sqrt(swingProgress);
      float f2 = -0.3F * Mth.sin(f1 * (float) Math.PI);
      float f3 = 0.4F * Mth.sin(f1 * (float) (Math.PI * 2));
      float f4 = -0.4F * Mth.sin(swingProgress * (float) Math.PI);
      equippedProgress = 0.0F;
      matrixStackIn.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equippedProgress * -0.6F, f4 + -0.71999997F);
      AbstractClientPlayer abstractclientPlayer = Minecraft.getInstance().player;
      AvatarRenderer<AbstractClientPlayer> playerrenderer = Minecraft.getInstance().getEntityRenderDispatcher().getPlayerRenderer(abstractclientPlayer);
      if (flag) {
         matrixStackIn.translate(-0.175, 0.275, 0.170625);
         RenderUtils.rotateQ(2.0F, 0.0F, 1.0F, 0.0F, matrixStackIn);
         RenderUtils.rotateQ(26.0F, 0.0F, 0.0F, 1.0F, matrixStackIn);
         RenderUtils.rotateQ(-9.0F, 1.0F, 0.0F, 0.0F, matrixStackIn);
         matrixStackIn.scale(0.8F, 0.8F, 0.8F);
      } else {
         matrixStackIn.translate(0.175, 0.275, 0.170625);
         RenderUtils.rotateQ(-1.0F, 0.0F, 1.0F, 0.0F, matrixStackIn);
         RenderUtils.rotateQ(-26.0F, 0.0F, 0.0F, 1.0F, matrixStackIn);
         RenderUtils.rotateQ(-9.0F, 1.0F, 0.0F, 0.0F, matrixStackIn);
         matrixStackIn.scale(0.8F, 0.8F, 0.8F);
      }

      matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * 45.0F));
      float f5 = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
      float f6 = Mth.sin(f1 * (float) Math.PI);
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
      matrixStackIn.translate(f * -1.0F, 3.6F, 3.5);
      matrixStackIn.mulPose(Axis.ZP.rotationDegrees(f * 120.0F));
      matrixStackIn.mulPose(Axis.XP.rotationDegrees(200.0F));
      matrixStackIn.mulPose(Axis.YP.rotationDegrees(f * -135.0F));
      matrixStackIn.translate(f * 5.6F, 0.0, 0.0);
      if (flag) {
         playerrenderer.renderRightHand(
            matrixStackIn,
            submitNodeCollector,
            combinedLightIn,
            abstractclientPlayer.getSkin().body().texturePath(),
            abstractclientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE)
         );
      } else {
         playerrenderer.renderLeftHand(
            matrixStackIn,
            submitNodeCollector,
            combinedLightIn,
            abstractclientPlayer.getSkin().body().texturePath(),
            abstractclientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE)
         );
      }

      matrixStackIn.popPose();
   }
}
