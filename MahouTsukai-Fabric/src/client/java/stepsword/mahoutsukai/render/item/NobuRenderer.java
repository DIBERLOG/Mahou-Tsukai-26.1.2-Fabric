package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.item.nobu.Nobu;
import stepsword.mahoutsukai.render.IEntityAwareRenderer;
import stepsword.mahoutsukai.render.entity.RenderGunEntity;
import stepsword.mahoutsukai.util.Utils;

public class NobuRenderer implements IEntityAwareRenderer, SpecialModelRenderer<MagicalItemRenderState> {
   private final double x;
   private final double y;
   private final double z;
   private final float xr;
   private final float yr;
   private final float zr;
   private final float size;

   public NobuRenderer() {
      this(0.0, 1.0, 4.0, 90.0F, 45.0F, 0.0F, 1.0F);
   }

   public NobuRenderer(double x, double y, double z, float xr, float yr, float zr, float size) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.xr = xr;
      this.yr = yr;
      this.zr = zr;
      this.size = size;
   }

   public NobuRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
      this();
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof Nobu;
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
         RenderGunEntity.renderGun(this.x, this.y, this.z, this.xr, this.yr, this.zr, this.size, matrix, submitNodeCollector, getColors(null));
         matrix.popPose();
      }
   }

   @Override
   public void render(LivingEntity entity, ItemStack stack, ItemDisplayContext transform, PoseStack matrix, MultiBufferSource buffer, int var1, int var2) {
      matrix.pushPose();
      double x = 0.0;
      double y = 1.0;
      double z = 0.5;
      float xr = 90.0F;
      float yr = 45.0F;
      float zr = 0.0F;
      float size = 1.0F;
      boolean using = entity != null && stack.getItem() instanceof Nobu && entity.getUseItem().equals(stack);
      if (transform.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
         if (using) {
            x = 0.5;
            y = 0.5;
            z = -0.7;
            xr = 0.0F;
            yr = 0.0F;
            zr = 90.0F;
         } else {
            x = 0.5;
            y = 1.5;
            z = 0.5;
            xr = 0.0F;
            yr = -90.0F;
            zr = 90.0F;
         }
      }

      if (transform.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)) {
         if (using) {
            x = 0.5;
            y = 0.5;
            z = -0.7;
            xr = 0.0F;
            yr = 0.0F;
            zr = 90.0F;
         } else {
            x = 0.5;
            y = 1.5;
            z = 0.5;
            xr = 0.0F;
            yr = -90.0F;
            zr = 90.0F;
         }
      }

      if (transform.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)) {
         if (using) {
            x = 0.5;
            y = 0.4;
            z = -0.4;
            xr = 0.0F;
            yr = -7.0F;
            zr = 90.0F;
         } else {
            x = 0.3;
            y = 0.8;
            z = -1.0;
            xr = 0.0F;
            yr = 0.0F;
            zr = 90.0F;
         }

         size = 1.8F;
      }

      if (transform.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)) {
         if (using) {
            x = 0.5;
            y = 0.4;
            z = -0.4;
            xr = 0.0F;
            yr = -7.0F;
            zr = 90.0F;
         } else {
            x = 0.7;
            y = 0.8;
            z = -1.0;
            xr = 0.0F;
            yr = 0.0F;
            zr = 90.0F;
         }

         size = 1.8F;
      }

      if (transform.equals(ItemDisplayContext.GUI)) {
         x = 0.0;
         y = 1.0;
         z = 4.0;
         xr = 90.0F;
         yr = 45.0F;
         zr = 0.0F;
      }

      if (transform.equals(ItemDisplayContext.GROUND)) {
         x = -0.25;
         y = 1.2;
         z = 0.5;
         xr = 90.0F;
         yr = 45.0F;
         zr = 0.0F;
      }

      RenderGunEntity.renderGun(x, y, z, xr, yr, zr, size, matrix, buffer, getColors(stack));
      matrix.popPose();
   }

   private static float[] getColors(@Nullable ItemStack stack) {
      float[] colors = stack != null ? Utils.getColor(stack) : null;
      return colors == null
         ? new float[]{1.0F, 1.0F, 0.7019608F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F}
         : new float[]{colors[0] / 255.0F, colors[1] / 255.0F, colors[2] / 255.0F, colors[3] / 255.0F, colors[4] / 255.0F, colors[5] / 255.0F};
   }
}
