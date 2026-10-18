package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import stepsword.mahoutsukai.item.william.William;
import stepsword.mahoutsukai.render.IEntityAwareRenderer;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.Point;
import stepsword.mahoutsukai.render.RenderBufferWrapper;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.entity.RenderWilliamEntity;
import stepsword.mahoutsukai.util.Utils;

public class WilliamRenderer implements IEntityAwareRenderer, SpecialModelRenderer<MagicalItemRenderState> {
   public static float[] william_cover = new float[]{0.76862746F, 0.6666667F, 0.58431375F, 1.0F};
   public static float[] william_pages = new float[]{0.27058825F, 0.019607844F, 0.67058825F, 1.0F};
   public static float[] william_text = new float[]{0.39215687F, 0.19215687F, 0.70980394F, 1.0F};
   public static float[] william_circle = new float[]{0.6784314F, 0.47843137F, 1.0F, 1.0F};
   public static float[] rlyeh_cover = new float[]{0.0F, 0.2F, 0.25F, 1.0F};
   public static float[] rlyeh_pages = new float[]{0.3529412F, 0.3529412F, 0.3529412F, 1.0F};
   public static float[] rlyeh_text = new float[]{0.39215687F, 1.0F, 1.0F, 1.0F};
   public static float[] rlyeh_circle = new float[]{0.39215687F, 1.0F, 1.0F, 1.0F};
   private static RenderType[] runeRenderTypes;
   private final double renderX;
   private final double renderY;
   private final double renderZ;
   private final float renderXR;
   private final float renderYR;
   private final float renderZR;
   private final float renderSize;
   private final boolean renderText;

   public WilliamRenderer() {
      this(0.0, 1.0, 0.5, 90.0F, 45.0F, 0.0F, 0.4F, false);
   }

   public WilliamRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
      this();
   }

   public WilliamRenderer(double x, double y, double z, float xr, float yr, float zr, float size) {
      this(x, y, z, xr, yr, zr, size, false);
   }

   public WilliamRenderer(double x, double y, double z, float xr, float yr, float zr, float size, boolean renderText) {
      this.renderX = x;
      this.renderY = y;
      this.renderZ = z;
      this.renderXR = xr;
      this.renderYR = yr;
      this.renderZR = zr;
      this.renderSize = size;
      this.renderText = renderText;
   }

   public void submit(
      @Nullable MagicalItemRenderState state,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (state != null && state.render) {
         float[][] colorset = getColorSet(
            state.stack, !state.stack.has(DataComponents.FICTION_COMPONENT) || ((FictionMahou)state.stack.get(DataComponents.FICTION_COMPONENT)).style >= 0
         );
         renderBook(
            this.renderX,
            this.renderY,
            this.renderZ,
            this.renderXR,
            this.renderYR,
            this.renderZR,
            this.renderSize,
            poseStack,
            new RenderBufferWrapper.SubmitNodeCollectorWrapper(submitNodeCollector),
            createRenderBook(this.renderText),
            colorset[0],
            colorset[1],
            colorset[2]
         );
      }
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof William;
      state.stack = stack.copyWithCount(1);
      return state;
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
      float size = 0.4F;
      if (transform.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
         x = 0.5;
         y = 0.65;
         z = 0.5;
         xr = 180.0F;
         yr = 0.0F;
         zr = 0.0F;
      }

      if (transform.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)) {
         x = 0.5;
         y = 0.65;
         z = 0.5;
         xr = 180.0F;
         yr = 0.0F;
         zr = 0.0F;
      }

      if (transform.equals(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)) {
         x = 0.4;
         y = 0.85;
         z = 0.5;
         xr = 180.0F;
         yr = 90.0F;
         zr = 0.0F;
         size = 0.7F;
      }

      if (transform.equals(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)) {
         x = 0.2;
         y = 0.85;
         z = 0.5;
         xr = 180.0F;
         yr = 90.0F;
         zr = 0.0F;
         size = 0.7F;
      }

      if (transform.equals(ItemDisplayContext.GUI)) {
         x = 0.25;
         y = 0.2;
         z = 0.5;
         xr = 30.0F;
         yr = 60.0F;
         zr = 0.0F;
         size = 0.6F;
      }

      if (transform.equals(ItemDisplayContext.GROUND)) {
         x = 0.5;
         y = 0.85;
         z = 0.7;
         xr = 180.0F;
         yr = 0.0F;
         zr = 0.0F;
         size = 0.4F;
      }

      float[][] colorset = getColorSet(
         stack, stack == null || !stack.has(DataComponents.FICTION_COMPONENT) || ((FictionMahou)stack.get(DataComponents.FICTION_COMPONENT)).style >= 0
      );
      renderBook(x, y, z, xr, yr, zr, size, matrix, buffer, createRenderBook(false), colorset[0], colorset[1], colorset[2]);
      matrix.popPose();
   }

   private static BookRender createRenderBook(boolean renderText) {
      BookRender render = new BookRender();
      render.renderingChars = renderText;
      render.calculatePoints();
      return render;
   }

   public static float[][] getColorSet(ItemStack stack, boolean w) {
      float[][] colors = new float[][]{rlyeh_pages, rlyeh_cover, rlyeh_text, rlyeh_circle};
      if (w) {
         colors = new float[][]{william_pages, william_cover, william_text, william_circle};
      }

      float[] cs = Utils.getColor(stack, false);
      float[] cs2 = Utils.getColor(stack, true);
      if (cs != null && cs2 != null) {
         colors = new float[][]{
            {cs[0] / 255.0F, cs[1] / 255.0F, cs[2] / 255.0F, 1.0F},
            {cs[3] / 255.0F, cs[4] / 255.0F, cs[5] / 255.0F, 1.0F},
            {cs2[0] / 255.0F, cs2[1] / 255.0F, cs2[2] / 255.0F, 1.0F},
            {cs2[3] / 255.0F, cs2[4] / 255.0F, cs2[5] / 255.0F, 1.0F}
         };
      }

      return colors;
   }

   public static void renderBook(
      double x,
      double y,
      double z,
      float yRot,
      float pRot,
      float dRot,
      float size,
      PoseStack matrix,
      MultiBufferSource buffer,
      BookRender br,
      float[] cover,
      float[] pages,
      float[] text
   ) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ(dRot, 0.0F, 0.0F, 1.0F, matrix);
      RenderUtils.rotateQ(yRot, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pRot, 0.0F, 1.0F, 0.0F, matrix);
      matrix.scale(size, size, size);
      MultiBufferSource actualBuffer = (MultiBufferSource)(buffer != null ? buffer : Minecraft.getInstance().renderBuffers().bufferSource());
      RenderType type = MahoujinRenderType.createWilliamRenderType(RuleBreakerRenderer.white, 0);
      Matrix4f m = matrix.last().pose();
      VertexConsumer buf = actualBuffer.getBuffer(type);
      renderPageStack(br.frontPageStack, m, buf, pages);
      renderPageStack(br.backPageStack, m, buf, pages);
      renderCover(br.frontCover, m, buf, cover);
      renderCover(br.backCover, m, buf, cover);
      renderBinding(br, m, buf, cover);

      for (ArrayList<Point> points : br.turningPages) {
         renderPageFlip(points, m, buf, pages);
      }

      RenderUtils.conditionalBatchEnder(type, actualBuffer);
      if (br.renderingChars) {
         renderFrontText(br, m, actualBuffer, text);
         renderBackText(br, m, actualBuffer, text);
         renderPageFlipText(br, m, actualBuffer, text);
      }

      matrix.popPose();
   }

   public static void renderBook(
      double x,
      double y,
      double z,
      float yRot,
      float pRot,
      float dRot,
      float size,
      PoseStack matrix,
      RenderBufferWrapper buffer,
      BookRender br,
      float[] cover,
      float[] pages,
      float[] text
   ) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ(dRot, 0.0F, 0.0F, 1.0F, matrix);
      RenderUtils.rotateQ(yRot, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pRot, 0.0F, 1.0F, 0.0F, matrix);
      matrix.scale(size, size, size);
      RenderType type = MahoujinRenderType.createWilliamRenderType(RuleBreakerRenderer.white, 0);
      buffer.submit(matrix, type, (pose, vertexConsumer) -> {
         Matrix4f mx = pose.pose();
         renderPageStack(br.frontPageStack, mx, vertexConsumer, pages);
         renderPageStack(br.backPageStack, mx, vertexConsumer, pages);
         renderCover(br.frontCover, mx, vertexConsumer, cover);
         renderCover(br.backCover, mx, vertexConsumer, cover);
         renderBinding(br, mx, vertexConsumer, cover);

         for (ArrayList<Point> points : br.turningPages) {
            renderPageFlip(points, mx, vertexConsumer, pages);
         }
      });
      if (br.renderingChars) {
         Matrix4f m = matrix.last().pose();
         renderFrontText(br, m, matrix, buffer, text);
         renderBackText(br, m, matrix, buffer, text);
         renderPageFlipText(br, m, matrix, buffer, text);
      }

      matrix.popPose();
   }

   public static void renderFrontText(BookRender br, Matrix4f m, MultiBufferSource buffer, float[] rgba) {
      renderText(br.frontCharIndices, br.frontCharacterLocations, m, buffer, rgba, true);
   }

   public static void renderBackText(BookRender br, Matrix4f m, MultiBufferSource buffer, float[] rgba) {
      renderText(br.backCharIndices, br.backCharacterLocations, m, buffer, rgba, true);
   }

   public static void renderPageFlipText(BookRender br, Matrix4f m, MultiBufferSource buffer, float[] rgba) {
      if (br.frontPageFlipCharacterLocations != null) {
         for (int x = 0; x < br.frontPageFlipCharacterLocations.size(); x++) {
            if (x < br.turningPageChars.size()) {
               renderText(br.turningPageChars.get(x), br.frontPageFlipCharacterLocations.get(x), m, buffer, rgba, true);
            }
         }
      }

      if (br.backPageFlipCharacterLocations != null) {
         for (int x = 0; x < br.backPageFlipCharacterLocations.size(); x++) {
            if (x < br.turningPageChars.size()) {
               renderText(br.turningPageChars.get(x), br.backPageFlipCharacterLocations.get(x), m, buffer, rgba, true);
            }
         }
      }
   }

   public static void renderFrontText(BookRender br, Matrix4f m, PoseStack poseStack, RenderBufferWrapper buffer, float[] rgba) {
      renderText(br.frontCharIndices, br.frontCharacterLocations, m, poseStack, buffer, rgba, true);
   }

   public static void renderBackText(BookRender br, Matrix4f m, PoseStack poseStack, RenderBufferWrapper buffer, float[] rgba) {
      renderText(br.backCharIndices, br.backCharacterLocations, m, poseStack, buffer, rgba, true);
   }

   public static void renderPageFlipText(BookRender br, Matrix4f m, PoseStack poseStack, RenderBufferWrapper buffer, float[] rgba) {
      if (br.frontPageFlipCharacterLocations != null) {
         for (int x = 0; x < br.frontPageFlipCharacterLocations.size(); x++) {
            if (x < br.turningPageChars.size()) {
               renderText(br.turningPageChars.get(x), br.frontPageFlipCharacterLocations.get(x), m, poseStack, buffer, rgba, true);
            }
         }
      }

      if (br.backPageFlipCharacterLocations != null) {
         for (int x = 0; x < br.backPageFlipCharacterLocations.size(); x++) {
            if (x < br.turningPageChars.size()) {
               renderText(br.turningPageChars.get(x), br.backPageFlipCharacterLocations.get(x), m, poseStack, buffer, rgba, true);
            }
         }
      }
   }

   public static void renderText(ArrayList<Integer> indices, ArrayList<Point> points, Matrix4f m, MultiBufferSource buffer, float[] rgba, boolean flip) {
      if (indices != null && !indices.isEmpty() && points != null && !points.isEmpty()) {
         ensureRuneRenderTypes();
         ArrayList<Integer>[] glyphBuckets = collectGlyphBuckets(indices, points);

         for (int i = 0; i < glyphBuckets.length; i++) {
            ArrayList<Integer> glyphs = glyphBuckets[i];
            if (!glyphs.isEmpty()) {
               RenderType type = runeRenderTypes[i];
               VertexConsumer buf = buffer.getBuffer(type);

               for (int pointIndex : glyphs) {
                  emitGlyph(buf, m, points, pointIndex, rgba, flip);
               }

               RenderUtils.conditionalBatchEnder(type, buffer);
            }
         }
      }
   }

   public static void renderText(
      ArrayList<Integer> indices, ArrayList<Point> points, Matrix4f m, PoseStack poseStack, RenderBufferWrapper buffer, float[] rgba, boolean flip
   ) {
      if (indices != null && !indices.isEmpty() && points != null && !points.isEmpty()) {
         ensureRuneRenderTypes();
         ArrayList<Integer>[] glyphBuckets = collectGlyphBuckets(indices, points);

         for (int i = 0; i < glyphBuckets.length; i++) {
            ArrayList<Integer> glyphs = glyphBuckets[i];
            if (!glyphs.isEmpty()) {
               RenderType type = runeRenderTypes[i];
               buffer.submit(poseStack, type, (pose, vertexConsumer) -> {
                  for (int pointIndex : glyphs) {
                     emitGlyph(vertexConsumer, m, points, pointIndex, rgba, flip);
                  }
               });
            }
         }
      }
   }

   private static void ensureRuneRenderTypes() {
      RenderWilliamEntity.ensureRuneLocations();
      if (runeRenderTypes == null || runeRenderTypes.length != RenderWilliamEntity.rune_loc.size()) {
         runeRenderTypes = new RenderType[RenderWilliamEntity.rune_loc.size()];

         for (int i = 0; i < RenderWilliamEntity.rune_loc.size(); i++) {
            runeRenderTypes[i] = MahoujinRenderType.createCharacterRenderType(RenderWilliamEntity.rune_loc.get(i), 0);
         }
      }
   }

   private static ArrayList<Integer>[] collectGlyphBuckets(ArrayList<Integer> indices, ArrayList<Point> points) {
      ArrayList<Integer>[] glyphBuckets = new ArrayList[runeRenderTypes.length];
      Arrays.setAll(glyphBuckets, ignored -> new ArrayList<>());
      int glyph = 0;

      for (int pointIndex = 0; pointIndex < points.size(); pointIndex += 4) {
         int runeIndex = indices.get(glyph % indices.size()) % runeRenderTypes.length;
         glyphBuckets[runeIndex].add(pointIndex);
         glyph++;
      }

      return glyphBuckets;
   }

   private static void emitGlyph(VertexConsumer buf, Matrix4f m, ArrayList<Point> points, int pointIndex, float[] rgba, boolean flip) {
      if (flip) {
         Point.addPointAt(buf, m, points, pointIndex + 3, rgba).setUv(0.0F, 0.0F).setUv2(240, 240);
         Point.addPointAt(buf, m, points, pointIndex + 2, rgba).setUv(0.0F, 1.0F).setUv2(240, 240);
         Point.addPointAt(buf, m, points, pointIndex + 1, rgba).setUv(1.0F, 1.0F).setUv2(240, 240);
         Point.addPointAt(buf, m, points, pointIndex + 0, rgba).setUv(1.0F, 0.0F).setUv2(240, 240);
      } else {
         Point.addPointAt(buf, m, points, pointIndex + 0, rgba).setUv(0.0F, 0.0F).setUv2(240, 240);
         Point.addPointAt(buf, m, points, pointIndex + 1, rgba).setUv(0.0F, 1.0F).setUv2(240, 240);
         Point.addPointAt(buf, m, points, pointIndex + 2, rgba).setUv(1.0F, 1.0F).setUv2(240, 240);
         Point.addPointAt(buf, m, points, pointIndex + 3, rgba).setUv(1.0F, 0.0F).setUv2(240, 240);
      }
   }

   public static void renderCover(ArrayList<Point> cover, Matrix4f m, VertexConsumer buf, float[] colors) {
      int lx = 240;
      int ly = 240;
      float[] rgba = colors;
      float[] darken = BookRender.darken(rgba);
      Point.addPointAt(buf, m, cover, 0, darken).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 0, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 0, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 4, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 1, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 5, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 2, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 6, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 3, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 7, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 0, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 4, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 0, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 1, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 3, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 2, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 4, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 5, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 7, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 6, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, cover, 6, rgba).setUv(1.0F, 1.0F).setUv2(lx, ly);
   }

   public static void renderBinding(BookRender br, Matrix4f m, VertexConsumer buf, float[] colors) {
      int lx = 240;
      int ly = 240;
      float[] rgba = colors;
      float[] darken = BookRender.darken(rgba);
      float[] darken1 = BookRender.darken(rgba, 0.9F);
      float[] darken2 = BookRender.darken(darken, 0.9F);
      Point.addPointAt(buf, m, br.binding, 6, darken).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 6, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 7, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);

      for (int k = 0; k <= br.binding_quality; k++) {
         Point.addPointAt(buf, m, br.binding, 9 + 4 * k, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, br.binding, 11 + 4 * k, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      }

      Point.addPointAt(buf, m, br.binding, 2, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 3, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 3, darken).setUv(1.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 4, darken).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 4, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 5, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);

      for (int k = 0; k <= br.binding_quality; k++) {
         Point.addPointAt(buf, m, br.binding, 8 + 4 * k, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, br.binding, 10 + 4 * k, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      }

      Point.addPointAt(buf, m, br.binding, 0, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 1, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 1, darken).setUv(1.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 4, darken2).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 4, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 6, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);

      for (int k = 0; k <= br.binding_quality; k++) {
         Point.addPointAt(buf, m, br.binding, 8 + 4 * k, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, br.binding, 9 + 4 * k, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      }

      Point.addPointAt(buf, m, br.binding, 0, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 2, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 2, darken2).setUv(1.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 5, darken1).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 5, darken1).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 7, darken1).setUv(0.0F, 1.0F).setUv2(lx, ly);

      for (int k = 0; k <= br.binding_quality; k++) {
         Point.addPointAt(buf, m, br.binding, 10 + 4 * k, darken1).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, br.binding, 11 + 4 * k, darken1).setUv(0.0F, 1.0F).setUv2(lx, ly);
      }

      Point.addPointAt(buf, m, br.binding, 1, darken1).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 3, darken1).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, br.binding, 3, darken1).setUv(1.0F, 1.0F).setUv2(lx, ly);
   }

   public static void renderPageStack(ArrayList<Point> stack, Matrix4f m, VertexConsumer buf, float[] colors) {
      int lx = 240;
      int ly = 240;
      float[] rgba = colors;
      float[] darken = BookRender.darken(rgba, 0.8F);
      float[] darken2 = BookRender.darken(darken, 0.8F);
      Point.addPointAt(buf, m, stack, 0, darken).setUv(0.0F, 0.0F).setUv2(lx, ly);

      for (int k = 0; k <= stack.size() - 8; k += 4) {
         Point.addPointAt(buf, m, stack, k, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 4, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 1, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 5, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 2, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 6, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 3, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 7, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 4, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      }

      Point.addPointAt(buf, m, stack, 0, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, stack, 1, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, stack, 3, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, stack, 2, darken).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, stack, 2, darken).setUv(1.0F, 1.0F).setUv2(lx, ly);
   }

   public static void renderPageFlip(ArrayList<Point> stack, Matrix4f m, VertexConsumer buf, float[] colors) {
      int lx = 240;
      int ly = 240;
      float[] rgba = colors;
      float[] darken = BookRender.darken(rgba, 0.8F);
      float[] darken2 = BookRender.darken(darken, 0.8F);
      Point.addPointAt(buf, m, stack, 0, rgba).setUv(0.0F, 0.0F).setUv2(lx, ly);

      for (int k = 0; k < stack.size(); k += 2) {
         Point.addPointAt(buf, m, stack, k + 0, rgba).setUv(0.0F, 0.5F).setUv2(lx, ly);
         Point.addPointAt(buf, m, stack, k + 1, darken2).setUv(0.0F, 1.0F).setUv2(lx, ly);
      }

      Point.addPointAt(buf, m, stack, stack.size() - 1, rgba).setUv(1.0F, 1.0F).setUv2(lx, ly);
   }

   public static void renderCircle(ArrayList<Point> circle, Matrix4f m, VertexConsumer buf, float[] colors) {
      int lx = 240;
      int ly = 240;
      float[] rgba = colors;
      Point.addPointAt(buf, m, circle, 0, rgba).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, circle, 0, rgba).setUv(0.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, circle, 1, rgba).setUv(0.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, circle, 3, rgba).setUv(1.0F, 0.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, circle, 2, rgba).setUv(1.0F, 1.0F).setUv2(lx, ly);
      Point.addPointAt(buf, m, circle, 2, rgba).setUv(1.0F, 1.0F).setUv2(lx, ly);
   }
}
