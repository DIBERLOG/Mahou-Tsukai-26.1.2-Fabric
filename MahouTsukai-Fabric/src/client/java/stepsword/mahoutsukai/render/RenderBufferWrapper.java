package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public interface RenderBufferWrapper {
   static void emitStrip(Pose pose,RenderType type,VertexConsumer vertices,BiConsumer<Pose,VertexConsumer> consumer){
      if(type.pipeline().getVertexFormatMode()==com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP){
         var isolated=new IsolatedTriangleStrip(vertices);consumer.accept(pose,isolated);isolated.finish();
      }else consumer.accept(pose,vertices);
   }
   void submit(PoseStack var1, RenderType var2, BiConsumer<Pose, VertexConsumer> var3);

   void endBatch(RenderType var1);

   class MultiBufferSourceWrapper implements RenderBufferWrapper {
      private final MultiBufferSource source;

      public MultiBufferSourceWrapper(MultiBufferSource source) {
         this.source = source;
      }

      @Override
      public void submit(PoseStack poseStack, RenderType renderType, BiConsumer<Pose, VertexConsumer> consumer) {
         emitStrip(poseStack.last(),renderType,this.source.getBuffer(renderType),consumer);
      }

      @Override
      public void endBatch(RenderType renderType) {
         if (this.source instanceof BufferSource bufferSource) {
            bufferSource.endBatch(renderType);
         }
      }
   }

   class SubmitNodeCollectorWrapper implements RenderBufferWrapper {
      private final SubmitNodeCollector collector;

      public SubmitNodeCollectorWrapper(SubmitNodeCollector collector) {
         this.collector = collector;
      }

      @Override
      public void submit(PoseStack poseStack, RenderType renderType, BiConsumer<Pose, VertexConsumer> consumer) {
         this.collector.submitCustomGeometry(poseStack, renderType,consumer::accept);
      }

      @Override
      public void endBatch(RenderType renderType) {
      }
   }
}
