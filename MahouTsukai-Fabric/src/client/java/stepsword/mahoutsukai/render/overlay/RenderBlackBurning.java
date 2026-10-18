package stepsword.mahoutsukai.render.overlay;

import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class RenderBlackBurning {
   static Identifier burn0 = Identifier.fromNamespaceAndPath("mahoutsukai", "black_fire_layer_0");
   static Identifier burn1 = Identifier.fromNamespaceAndPath("mahoutsukai", "black_fire_layer_1");
   static SpriteId m0 = Sheets.BLOCKS_MAPPER.apply(burn0);
   static SpriteId m1 = Sheets.BLOCKS_MAPPER.apply(burn1);
   public static void renderEntityFire(LivingEntityRenderState state,PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector) {
      if (((stepsword.mahoutsukai.fabric.BlackBurningRenderState)(Object)state).mahoutsukai$isBlackBurning()) {
         Quaternionf cameraRotation = new Quaternionf(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
         TextureAtlasSprite fire0 = Minecraft.getInstance().getAtlasManager().get(m0);
         TextureAtlasSprite fire1 = Minecraft.getInstance().getAtlasManager().get(m1);
         matrix.pushPose();
         float scale = state.boundingBoxWidth * 1.4F;
         matrix.scale(scale, scale, scale);
         float radius = 0.5F;
         float height = state.boundingBoxHeight / scale;
         float yOffset = 0.0F;
         float zOffset = 0.0F;
         matrix.mulPose(cameraRotation);
         matrix.translate(0.0F, 0.0F, 0.3F - (int)height * 0.02F);
         int step = 0;
         int lightCoords = LightCoordsUtil.withBlock(state.lightCoords, 15);

         while (height > 0.0F) {
            TextureAtlasSprite sprite = step % 2 == 0 ? fire0 : fire1;
            float u0 = sprite.getU0();
            float v0 = sprite.getV0();
            float u1 = sprite.getU1();
            float v1 = sprite.getV1();
            if (step / 2 % 2 == 0) {
               float tmp = u1;
               u1 = u0;
               u0 = tmp;
            }

            float quadRadius = radius;
            float quadYOffset = yOffset;
            float quadZOffset = zOffset;
            float quadU0 = u0;
            float quadV0 = v0;
            float quadU1 = u1;
            float quadV1 = v1;
            int quadLightCoords = lightCoords;
            collector.submitCustomGeometry(matrix, Sheets.cutoutBlockSheet(), (pose, buffer) -> {
               fireVertex(pose, buffer, -quadRadius, 0.0F - quadYOffset, quadZOffset, quadU1, quadV1, quadLightCoords);
               fireVertex(pose, buffer, quadRadius, 0.0F - quadYOffset, quadZOffset, quadU0, quadV1, quadLightCoords);
               fireVertex(pose, buffer, quadRadius, 1.4F - quadYOffset, quadZOffset, quadU0, quadV0, quadLightCoords);
               fireVertex(pose, buffer, -quadRadius, 1.4F - quadYOffset, quadZOffset, quadU1, quadV0, quadLightCoords);
            });
            height -= 0.45F;
            yOffset -= 0.45F;
            radius *= 0.9F;
            zOffset -= 0.03F;
            step++;
         }

         matrix.popPose();
      }
   }

   private static void fireVertex(Pose pose, VertexConsumer buffer, float x, float y, float z, float u, float v, int lightCoords) {
      buffer.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setUv1(0, 10).setLight(lightCoords).setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   public static void renderOverlayFire(PoseStack matrix, float partialTicks,net.minecraft.client.renderer.MultiBufferSource buffers) {
      Minecraft mc = Minecraft.getInstance();
      boolean flag = mc.getCameraEntity() instanceof LivingEntity && ((LivingEntity)mc.getCameraEntity()).isSleeping();
      if (mc.options.getCameraType().isFirstPerson() && !flag && EffectUtil.hasBuff(mc.player, ModEffects.BLACK_BURNING)) {
         renderFire(mc, matrix,buffers);
         hurtCameraEffect(matrix, partialTicks);
      }
   }

   public static void hurtCameraEffect(PoseStack matrixStackIn, float partialTicks) {
      if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity livingentity) {
         float f = livingentity.hurtTime - partialTicks;
         if (livingentity.getHealth() <= 0.0F) {
            float f1 = Math.min(livingentity.deathTime + partialTicks, 20.0F);
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (f1 + 200.0F)));
         }

         if (f <= 0.0F || livingentity.hurtDuration <= 0) {
            return;
         }

         f /= livingentity.hurtDuration;
         f = Mth.sin(f * f * f * f * (float) Math.PI);
         matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-f * 14.0F));
      }
   }

   private static void renderFire(Minecraft minecraftIn, PoseStack matrixStackIn,net.minecraft.client.renderer.MultiBufferSource buffers) {
      TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getAtlasManager().get(m1);
      float f = textureatlassprite.getU0();
      float f1 = textureatlassprite.getU1();
      float f2 = (f + f1) / 2.0F;
      float f3 = textureatlassprite.getV0();
      float f4 = textureatlassprite.getV1();
      float f5 = (f3 + f4) / 2.0F;
      float f6 = uvShrinkRatio(textureatlassprite);
      float f7 = Mth.lerp(f6, f, f2);
      float f8 = Mth.lerp(f6, f1, f2);
      float f9 = Mth.lerp(f6, f3, f5);
      float f10 = Mth.lerp(f6, f4, f5);
      RenderType renderType = RenderTypes.fireScreenEffect(textureatlassprite.atlasLocation());
      VertexConsumer bufferbuilder = buffers.getBuffer(renderType);

      for (int i = 0; i < 2; i++) {
         matrixStackIn.pushPose();
         matrixStackIn.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0);
         matrixStackIn.mulPose(Axis.YP.rotationDegrees((i * 2 - 1) * 10.0F));
         Matrix4f matrix4f = matrixStackIn.last().pose();
         bufferbuilder.addVertex(matrix4f, -0.5F, -0.5F, -0.5F).setUv(f8, f10).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         bufferbuilder.addVertex(matrix4f, 0.5F, -0.5F, -0.5F).setUv(f7, f10).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         bufferbuilder.addVertex(matrix4f, 0.5F, 0.5F, -0.5F).setUv(f7, f9).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         bufferbuilder.addVertex(matrix4f, -0.5F, 0.5F, -0.5F).setUv(f8, f9).setColor(1.0F, 1.0F, 1.0F, 0.9F);
         matrixStackIn.popPose();
      }


   }

   public static float atlasSize(TextureAtlasSprite t) {
      float f = t.contents().width() / (t.getU1() - t.getU0());
      float f1 = t.contents().height() / (t.getV1() - t.getV0());
      return Math.max(f1, f);
   }

   public static float uvShrinkRatio(TextureAtlasSprite t) {
      return 4.0F / atlasSize(t);
   }
}
