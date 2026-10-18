package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderWeaponProjectile extends EntityRenderer<WeaponProjectileEntity, RenderWeaponProjectile.WeaponProjectileRenderState> {
   private final ItemModelResolver itemModelResolver;

   public RenderWeaponProjectile(Context renderManager) {
      super(renderManager);
      this.itemModelResolver = renderManager.getItemModelResolver();
   }

   public RenderWeaponProjectile.WeaponProjectileRenderState createRenderState() {
      return new RenderWeaponProjectile.WeaponProjectileRenderState();
   }

   public void extractRenderState(WeaponProjectileEntity weapon, RenderWeaponProjectile.WeaponProjectileRenderState state, float partialTicks) {
      super.extractRenderState(weapon, state, partialTicks);
      state.ryaw = 90.0F + weapon.yRotO + (weapon.yRot - weapon.yRotO) * partialTicks;
      state.rpitch = 135.0F - weapon.xRotO + (weapon.xRot - weapon.xRotO) * partialTicks;
      state.itemRotation = stepsword.mahoutsukai.fabric.JoustingCompatibility.rotation(weapon.getStack().getItem());
      this.itemModelResolver.updateForNonLiving(state.item, weapon.getStack(), ItemDisplayContext.GROUND, weapon);
   }

   public void submit(
      RenderWeaponProjectile.WeaponProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera
   ) {
      poseStack.pushPose();
      float scale = 2.5F;
      poseStack.mulPose(RenderUtils.getQuaternion(new Vector3f(0.0F, 1.0F, 0.0F), state.ryaw, true));
      poseStack.mulPose(RenderUtils.getQuaternion(new Vector3f(0.0F, 0.0F, 1.0F), state.rpitch, true));
      poseStack.translate(-0.59, -0.59, 0.0);
      poseStack.scale(scale, scale, scale);
      if (state.itemRotation != 0.0F) {
         poseStack.translate(0.0F, 0.1875F, 0.0F);
         poseStack.mulPose(RenderUtils.getQuaternion(new Vector3f(0.0F, 0.0F, 1.0F), state.itemRotation, true));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         poseStack.translate(0.0F, -1.5625F, 0.0F);
      }

      state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static class WeaponProjectileRenderState extends DummyEntityRenderState {
      public final ItemStackRenderState item = new ItemStackRenderState();
      public float ryaw;
      public float rpitch;
      public float itemRotation;
   }
}
