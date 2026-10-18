package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.entity.mahoujin.WilliamEntity;
import stepsword.mahoutsukai.item.william.William;
import stepsword.mahoutsukai.render.DummyEntityRenderState;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.item.BookRender;
import stepsword.mahoutsukai.render.item.WilliamRenderer;

public class RenderWilliamEntity extends EntityRenderer<WilliamEntity, DummyEntityRenderState> {
   private static final String BUILTIN_TEXT_SCOPE = "william_entity_circle";
   public static ArrayList<Identifier> rune_loc = null;

   public RenderWilliamEntity(Context renderManager) {
      super(renderManager);
   }

   public DummyEntityRenderState createRenderState() {
      return new DummyEntityRenderState();
   }

   public void submit(DummyEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public static void renderWilliam(WilliamEntity entity, double x, double y, double z, float roll, PoseStack matrix, float partialTicks,SubmitNodeCollector buffer) {
      matrix.pushPose();
      matrix.translate(x, y, z);
      float pitch = entity.getRotationPitch();
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);

      BookRender br = entity.br instanceof BookRender value ? value : new BookRender();
      BookRender last = entity.last_br instanceof BookRender value ? value : br;
      ensureRuneLocations();
      float[][] colors = WilliamRenderer.getColorSet(entity.getStack(), !William.isRlyeh(entity.getStack()));
      if (entity.page_colors == null) {
         entity.page_colors = colors[0];
      }

      if (entity.cover_colors == null) {
         entity.cover_colors = colors[1];
      }

      if (entity.text_colors == null) {
         entity.text_colors = colors[2];
      }

      if (entity.circle_colors == null) {
         entity.circle_colors = colors[3];
      }

      BookRender brr = br.partial(last, partialTicks);
      WilliamRenderer.renderBook(0.0, 0.0, 0.0, 0.0F, 0.0F, 1.0F, 0.5F, matrix, new stepsword.mahoutsukai.render.RenderBufferWrapper.SubmitNodeCollectorWrapper(buffer), brr, entity.page_colors, entity.cover_colors, entity.text_colors);
      matrix.popPose();
      renderCircles(br, matrix, x, y, z, buffer, roll, pitch, partialTicks, entity.clientTicks, entity.circle_colors);
   }

   public static void ensureRuneLocations() {
      if (rune_loc == null) {
         rune_loc = new ArrayList<>();

         for (int i = 0; i < 24; i++) {
            rune_loc.add(Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/runefont/" + i + ".png"));
         }
      }
   }

   public static void renderCircles(
      BookRender br,
      PoseStack matrix,
      double x,
      double y,
      double z,
      SubmitNodeCollector buffer,
      float roll,
      float pitch,
      float partial,
      long ticks,
      float[] circle_color
   ) {
      RenderType circle = MahoujinRenderType.createBuiltInTextMahoujinRenderType("william_entity_circle", RenderUtils.mahoujin, 0, true);
      matrix.pushPose();
      matrix.translate(x, y, z);
      RenderUtils.rotateQ(roll, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, matrix);
      matrix.scale(0.5F, 0.5F, 0.5F);
      matrix.translate(0.0, -0.4, -br.cover_height / 2.0);
      RenderUtils.rotateQ(((float)ticks - (1.0F - partial)) * 3.0F, 0.0F, 1.0F, 0.0F, matrix);
      buffer.submitCustomGeometry(matrix,circle,(pose,vertices)->{
       var m=pose.pose();
       RenderUtils.addPoint(vertices,m,-1,0,-1,circle_color).setUv(0,0).setUv2(240,240);
       RenderUtils.addPoint(vertices,m,-1,0,1,circle_color).setUv(0,1).setUv2(240,240);
       RenderUtils.addPoint(vertices,m,1,0,1,circle_color).setUv(1,1).setUv2(240,240);
       RenderUtils.addPoint(vertices,m,1,0,-1,circle_color).setUv(1,0).setUv2(240,240);
      });
      matrix.popPose();
   }
}
