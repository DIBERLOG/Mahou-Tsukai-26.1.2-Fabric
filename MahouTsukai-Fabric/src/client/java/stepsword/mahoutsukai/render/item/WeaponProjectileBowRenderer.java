package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ItemUseRenderBridge;

import stepsword.mahoutsukai.item.spells.projection.WeaponProjectile.WeaponProjectileBow;

import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderBufferWrapper;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.SpiralGeometry;
import stepsword.mahoutsukai.util.Utils;

public class WeaponProjectileBowRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private final RenderBaseItem.ModelKeyPair model;
   private final float progress;

   public WeaponProjectileBowRenderer() {
      this(RenderBaseItem.standby, 0.0F);
   }

   public WeaponProjectileBowRenderer(RenderBaseItem.ModelKeyPair model, float progress) {
      this.model = model;
      this.progress = progress;
   }

   public WeaponProjectileBowRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
      this();
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof WeaponProjectileBow;
      state.time = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(this.model.key);
      state.stack = stack;
      ItemDisplayContext transform = ItemUseRenderBridge.getDisplayContext(stack);
      state.transform = transform != null ? transform : ItemDisplayContext.NONE;
      state.useTicks = ItemUseRenderBridge.getUseTicks(stack);
      state.progress = state.useTicks > 0.0F ? WeaponProjectileBow.getPowerForTime((int)state.useTicks) : this.progress;
      return state;
   }

   public void submit(
      @Nullable MagicalItemRenderState state,
      PoseStack matrix,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (state != null && state.render) {
         matrix.pushPose();
         renderAmmo(state, matrix, submitNodeCollector, lightCoords, overlayCoords, outlineColor);
         renderBowEffects(
            state.stack, state.transform, matrix, new RenderBufferWrapper.SubmitNodeCollectorWrapper(submitNodeCollector), state.time, state.progress
         );
         matrix.popPose();
      }
   }

   private static void renderAmmo(
      MagicalItemRenderState state, PoseStack matrix, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor
   ) {
      if (!(state.useTicks <= 0.0F)) {
         ItemStack ammo = Utils.getAmmoStack(state.stack);
         if (ammo != null && !ammo.isEmpty()) {
            ItemStackRenderState ammoState = new ItemStackRenderState();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getItemModelResolver().updateForTopItem(ammoState, ammo, ItemDisplayContext.GUI, minecraft.level, null, 0);
            matrix.pushPose();

            try {
               float pullcap = Mth.clamp(state.progress, 0.0F, 1.5F);
               float scale = 0.6F;
               float tp = pullcap * 0.08F;
               RenderUtils.rotateQ(90.0F, 0.0F, 0.0F, 1.0F, matrix);
               matrix.translate(0.4F - tp, -0.5F - tp, 0.5F);
               matrix.scale(scale, scale, scale);
               ammoState.submit(matrix, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, outlineColor);
            } finally {
               matrix.popPose();
            }
         }
      }
   }

   private static void renderBowEffects(ItemStack stack, ItemDisplayContext transform, PoseStack matrix, RenderBufferWrapper buffer, long time, float pull) {
      float pullcap = Mth.clamp(pull, 0.0F, 1.5F);
      RenderType runes1 = getRuneType("weapon_projectile_bow_1", transform);
      RenderType runes2 = getRuneType("weapon_projectile_bow_2", transform);
      RenderType runes3 = getRuneType("weapon_projectile_bow_3", transform);
      RenderType runes4 = getRuneType("weapon_projectile_bow_4", transform);
      RenderType runes5 = getRuneType("weapon_projectile_bow_5", transform);
      RenderType runes6 = getRuneType("weapon_projectile_bow_6", transform);
      float cy = 0.5F;
      float lengthen = 0.005F;
      float innerradius = 0.01F;
      float h = 0.07F;
      float texs = 1.0F - (float)(time * 4L % 360L) / 360.0F;
      float ring = 88.0F;
      float size = 0.16F;
      float r = 0.7F;
      float g = 1.0F;
      float b = 1.0F;
      float a = 1.0F;
      float r2 = 0.7F;
      float g2 = 1.0F;
      float b2 = 1.0F;
      int lightmapX = 255;
      int lightmapY = 255;
      int segments = 220;
      float[] colors = Utils.getColor(stack);
      if (colors != null && colors.length > 5) {
         r = colors[0] / 255.0F;
         g = colors[1] / 255.0F;
         b = colors[2] / 255.0F;
         r2 = colors[3] / 255.0F;
         g2 = colors[4] / 255.0F;
         b2 = colors[5] / 255.0F;
      }

      float bowcurve = 20.0F + pullcap / 1.5F * 20.0F;
      matrix.pushPose();
      RenderUtils.rotateQ(-50.0F, 0.0F, 0.0F, 1.0F, matrix);
      matrix.translate(-0.3, 0.25, 0.5);
      renderSpiral(matrix, buffer, cy, ring, size / 3.0F, h, segments, lightmapX, lightmapY, r, g, b, a, runes1, lengthen, innerradius, texs, bowcurve, 0);
      renderSpiral(matrix, buffer, cy, ring, size / 3.0F, h, segments, lightmapX, lightmapY, r, g, b, a, runes2, lengthen, innerradius, texs, bowcurve, 120);
      renderSpiral(matrix, buffer, cy, ring, size / 3.0F, h, segments, lightmapX, lightmapY, r, g, b, a, runes3, lengthen, innerradius, texs, bowcurve, 240);
      RenderUtils.rotateQ(180.0F, 0.0F, 0.0F, 1.0F, matrix);
      RenderUtils.rotateQ(180.0F, 0.0F, 1.0F, 0.0F, matrix);
      matrix.translate(0.0, -0.7, 0.0);
      renderSpiral(matrix, buffer, cy, ring, size / 3.0F, h, segments, lightmapX, lightmapY, r, g, b, a, runes4, lengthen, innerradius, texs, bowcurve, 0);
      renderSpiral(matrix, buffer, cy, ring, size / 3.0F, h, segments, lightmapX, lightmapY, r, g, b, a, runes5, lengthen, innerradius, texs, bowcurve, 120);
      renderSpiral(matrix, buffer, cy, ring, size / 3.0F, h, segments, lightmapX, lightmapY, r, g, b, a, runes6, lengthen, innerradius, texs, bowcurve, 240);
      matrix.popPose();
      if (pullcap > 0.0F) {
         matrix.pushPose();
         float scale = 0.2F;
         RenderUtils.rotateQ(40.0F, 0.0F, 0.0F, 1.0F, matrix);
         RenderUtils.rotateQ(-15.0F, 0.0F, 1.0F, 0.0F, matrix);
         matrix.scale(0.8F, 0.8F, 0.8F);
         matrix.translate(0.4F, 0.0F, -0.05F);
         float startShow = 0.0F;
         float startIncrement = 0.1F;
         float progress = pull;
         renderCircle(
            -time,
            0.5,
            0.4,
            0.3,
            matrix,
            buffer,
            scale,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.37,
            0.4,
            0.2,
            matrix,
            buffer,
            scale / 2.0F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.57,
            0.4,
            0.18,
            matrix,
            buffer,
            scale / 3.0F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.47,
            0.4,
            0.13,
            matrix,
            buffer,
            scale / 1.6F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.38,
            0.4,
            0.08,
            matrix,
            buffer,
            scale / 4.0F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.53,
            0.4,
            0.03,
            matrix,
            buffer,
            scale / 2.8F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.43,
            0.4,
            0.0,
            matrix,
            buffer,
            scale / 1.7F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.37,
            0.4,
            -0.09,
            matrix,
            buffer,
            scale / 2.6F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.45,
            0.4,
            -0.1,
            matrix,
            buffer,
            scale / 2.9F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.41,
            0.4,
            -0.16,
            matrix,
            buffer,
            scale / 3.1F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         startShow = 0.0F;
         RenderUtils.rotateQ(180.0F, 0.0F, 1.0F, 0.0F, matrix);
         matrix.translate(-1.05F, 0.0F, -0.9F);
         renderCircle(
            -time,
            0.5,
            0.4,
            0.3,
            matrix,
            buffer,
            scale,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.37,
            0.4,
            0.2,
            matrix,
            buffer,
            scale / 2.0F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.57,
            0.4,
            0.18,
            matrix,
            buffer,
            scale / 3.0F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.47,
            0.4,
            0.13,
            matrix,
            buffer,
            scale / 1.6F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.38,
            0.4,
            0.08,
            matrix,
            buffer,
            scale / 4.0F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.53,
            0.4,
            0.03,
            matrix,
            buffer,
            scale / 2.8F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.43,
            0.4,
            0.0,
            matrix,
            buffer,
            scale / 1.7F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.37,
            0.4,
            -0.09,
            matrix,
            buffer,
            scale / 2.6F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            -time,
            0.45,
            0.4,
            -0.1,
            matrix,
            buffer,
            scale / 2.9F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         renderCircle(
            time,
            0.41,
            0.4,
            -0.16,
            matrix,
            buffer,
            scale / 3.1F,
            r2,
            g2,
            b2,
            Mth.clamp(progress - startShow++ * startIncrement, 0.0F, 1.0F),
            lightmapX,
            lightmapY,
            transform
         );
         matrix.popPose();
      }
   }

   private static RenderType getRuneType(String scope, ItemDisplayContext transform) {
      RenderType type = MahoujinRenderType.createScopedRunesRenderType(scope, RenderUtils.runes, 0);
      return type;
   }

   public static void renderCircle(
      long time,
      double x,
      double y,
      double z,
      PoseStack matrix,
      RenderBufferWrapper buffer,
      float scale,
      float r,
      float g,
      float b,
      float a,
      int lightmapX,
      int lightmapY,
      ItemDisplayContext transform
   ) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ((float)time, 0.0F, 1.0F, 0.0F, matrix);
      RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.mahoujin,0,false);
      buffer.submit(matrix, type, (pose, bb) -> {
         Matrix4f m = pose.pose();
         bb.addVertex(m, -0.5F * scale, 0.0F, -0.5F * scale).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(m, -0.5F * scale, 0.0F, 0.5F * scale).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(m, 0.5F * scale, 0.0F, 0.5F * scale).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lightmapX, lightmapY);
         bb.addVertex(m, 0.5F * scale, 0.0F, -0.5F * scale).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lightmapX, lightmapY);
      });
      matrix.popPose();
   }

   private static void renderSpiral(
      PoseStack matrix,
      RenderBufferWrapper buffer,
      double cy,
      float flatness,
      float radius,
      float height,
      int segments,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      RenderType type,
      float lengthen,
      float innerRadius,
      float texs,
      float curveEnd,
      int startDegrees
   ) {
      SpiralGeometry spiral = new SpiralGeometry(flatness, radius, height);
      spiral.setStartingY(cy);
      spiral.setNumSegments(segments);
      spiral.setTurns(6.0F);
      spiral.setLengthen(lengthen);
      spiral.setInnerRadius(innerRadius);
      spiral.setTexs(texs);
      spiral.setCurveEnd(curveEnd);
      spiral.setStartDegrees(startDegrees);
      spiral.setDuplicateCaps(true);
      spiral.setColor(new float[]{r, g, b, a});
      spiral.setLx(lx);
      spiral.setLy(ly);
      spiral.setType(type);
      spiral.render(matrix, buffer);
   }
}
