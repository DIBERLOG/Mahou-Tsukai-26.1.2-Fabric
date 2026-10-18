package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBufferWrapper;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderGateWeaponProjectile extends EntityRenderer<GateWeaponProjectileEntity, RenderGateWeaponProjectile.GateWeaponProjectileRenderState> {
   private static final Identifier LOCATION_BLOCKS = Identifier.fromNamespaceAndPath("minecraft", "textures/atlas/blocks.png");
   private static final String TREASURY_CIRCLE_SCOPE = "treasury_projection_circle";
   private static final float TREASURY_ITEM_LATERAL_OFFSET = -0.3F;
   private static final float TREASURY_ITEM_DEPTH_OFFSET = 0.0F;
   private final ItemModelResolver itemModelResolver;

   public RenderGateWeaponProjectile(Context renderManager) {
      super(renderManager);
      this.itemModelResolver = renderManager.getItemModelResolver();
   }

   public RenderGateWeaponProjectile.GateWeaponProjectileRenderState createRenderState() {
      return new RenderGateWeaponProjectile.GateWeaponProjectileRenderState();
   }

   public void extractRenderState(GateWeaponProjectileEntity weapon, RenderGateWeaponProjectile.GateWeaponProjectileRenderState state, float partialTicks) {
      super.extractRenderState(weapon, state, partialTicks);
      state.stack = weapon.getStack().copy();
      state.ryaw = 90.0F + weapon.yRotO + (weapon.yRot - weapon.yRotO) * partialTicks;
      state.rpitch = 135.0F - weapon.xRotO + (weapon.xRot - weapon.xRotO) * partialTicks;
      state.yRotate = -90.0F + weapon.yRotO + (weapon.yRot - weapon.yRotO) * partialTicks;
      state.pRotate = 90.0F + weapon.xRotO + (weapon.xRot - weapon.xRotO) * partialTicks;
      state.progress = weapon.getProgress();
      state.progressCap = weapon.getProgressCap();
      state.showCircle = state.progress < state.progressCap;
      state.yTranslate = state.progress + (state.progress - weapon.lastProgress) * partialTicks - 2.0F;
      state.colors = weapon.getColors();
      state.itemRotation = stepsword.mahoutsukai.fabric.JoustingCompatibility.rotation(state.stack.getItem());
      this.itemModelResolver.updateForNonLiving(state.item, state.stack, ItemDisplayContext.GROUND, weapon);
   }

   public void submit(
      RenderGateWeaponProjectile.GateWeaponProjectileRenderState state, PoseStack matrix, SubmitNodeCollector submitNodeCollector, CameraRenderState camera
   ) {
      float scale = 2.5F;
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      matrix.pushPose();
      matrix.pushPose();
      RenderUtils.rotateQ(state.yRotate, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(state.pRotate, 0.0F, 0.0F, 1.0F, matrix);
      matrix.translate(0.0F, state.yTranslate, 0.0F);
      matrix.scale(scale, scale, scale);
      if (state.showCircle) {
         this.drawCircle(new RenderBufferWrapper.SubmitNodeCollectorWrapper(submitNodeCollector), matrix, state.colors);
      }

      matrix.popPose();
      matrix.mulPose(RenderUtils.getQuaternion(new Vector3f(0.0F, 1.0F, 0.0F), state.ryaw, true));
      matrix.mulPose(RenderUtils.getQuaternion(new Vector3f(0.0F, 0.0F, 1.0F), state.rpitch, true));
      matrix.translate(0.25, 0.0, 0.0);
      matrix.scale(scale, scale, scale);
      Matrix4f clipMatrix = new Matrix4f(matrix.last().pose());
      if (state.itemRotation != 0.0F) {
         matrix.translate(0.0F, 0.1875F, 0.0F);
         matrix.mulPose(RenderUtils.getQuaternion(new Vector3f(0.0F, 0.0F, 1.0F), state.itemRotation, true));
         matrix.scale(0.5F, 0.5F, 0.5F);
         matrix.translate(0.0F, -1.5625F, 0.0F);
      }

      var clipped=stepsword.mahoutsukai.render.ClipSubmitNodeCollector.wrap(submitNodeCollector,state.showCircle,135-state.rpitch,state.ryaw-90,state.x,state.y,state.z,state.yTranslate,scale,clipMatrix);
      state.item.submit(matrix,clipped,15728880,OverlayTexture.NO_OVERLAY,state.outlineColor);
      buffer.endBatch();
      matrix.popPose();
      super.submit(state, matrix, submitNodeCollector, camera);
   }

   protected boolean affectedByCulling(GateWeaponProjectileEntity entity) {
      return false;
   }

   public Identifier getTextureLocation(GateWeaponProjectileEntity entity) {
      return LOCATION_BLOCKS;
   }

   private void drawCircle(RenderBufferWrapper buffer, PoseStack matrix, float[] colors) {
      int lightmapX = 240;
      int lightmapY = 240;
      float r = colors[0];
      float g = colors[1];
      float b = colors[2];
      float a = colors.length > 3 ? colors[3] : 1.0F;
      RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("treasury_projection_circle", RenderUtils.mahoujin, 0, false);
      buffer.submit(matrix, type, (pose, vertexConsumer) -> {
         Matrix4f positionMatrix = pose.pose();
         vertexConsumer.addVertex(positionMatrix, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
         vertexConsumer.addVertex(positionMatrix, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
         vertexConsumer.addVertex(positionMatrix, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
         vertexConsumer.addVertex(positionMatrix, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
   }

   public static class GateWeaponProjectileRenderState extends DummyEntityRenderState {
      public ItemStack stack = ItemStack.EMPTY;
      public final ItemStackRenderState item = new ItemStackRenderState();
      public float ryaw;
      public float rpitch;
      public float yRotate;
      public float pRotate;
      public float progress;
      public float progressCap;
      public float yTranslate;
      public float itemRotation;
      public boolean showCircle;
      public float[] colors;
   }
}
