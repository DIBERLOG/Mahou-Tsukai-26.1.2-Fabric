package stepsword.mahoutsukai.render;

import net.minecraft.resources.Identifier;

/** Original circuit texture identifiers; other RenderUtils routines remain pending. */
public final class RenderUtils {
 private static final boolean SHADER_MOD_PRESENT=net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("iris") || net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("oculus");
 public static float calculateOverlayFade(int duration,int interval,int fadeTicks,float maxAlpha){int phase=duration%interval;return phase>interval-fadeTicks?(phase-(interval-fadeTicks))/(float)fadeTicks*maxAlpha:0;}
 public static void renderTextureOverlay(net.minecraft.client.gui.GuiGraphicsExtractor graphics,String texture,int r,int g,int b,float alpha,float x,float y,float width,float height,float z){
  int color=net.minecraft.util.ARGB.colorFromFloat(Math.clamp(alpha,0,1),r/255f,g/255f,b/255f);int w=Math.round(width),h=Math.round(height);
  graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,Identifier.parse(texture),Math.round(x),Math.round(y),0,0,w,h,w,h,w,h,color);
 }

 public static boolean turnOffMahouShaders(){return stepsword.mahoutsukai.config.MTConfig.NO_SHADERS || SHADER_MOD_PRESENT;}
 public static void billboard(com.mojang.blaze3d.vertex.PoseStack pose,float rotation,float partial){
  var camera=net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera();pose.mulPose(camera.rotation());rotateQ(90,1,0,0,pose);rotateQ(rotation,0,1,0,pose);
 }

    public static void rotateQ(float degrees, float x, float y, float z, com.mojang.blaze3d.vertex.PoseStack matrix) {
        float radians = degrees * (float)(Math.PI / 180.0);
        float sin = org.joml.Math.sin(radians / 2);
        matrix.mulPose(new org.joml.Quaternionf(x*sin, y*sin, z*sin, org.joml.Math.cos(radians / 2)));
    }
    public static void conditionalBatchEnder(net.minecraft.client.renderer.rendertype.RenderType type, net.minecraft.client.renderer.MultiBufferSource buffer) {
        if (buffer instanceof net.minecraft.client.renderer.MultiBufferSource.BufferSource source) source.endBatch(type);
    }
    public static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
    public static final Identifier beam=Identifier.fromNamespaceAndPath("mahoutsukai","textures/block/white.png");
    public static final Identifier runes = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/runes.png");

 public static net.minecraft.client.renderer.rendertype.RenderType renderRing(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,double cy,float flatness,float radius,float height,int segments,int lx,int ly,float r,float g,float b,float a,net.minecraft.client.renderer.rendertype.RenderType type){return renderRing(matrix,collector,cy,flatness,radius,height,segments,lx,ly,r,g,b,a,type,true);}
 public static void renderSphere(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,float radius,int segments,int lx,int ly,float r,float g,float b,float a,net.minecraft.client.renderer.rendertype.RenderType type){renderSphere(matrix,collector,radius,segments,lx,ly,r,g,b,a,type,1,true);}
   public static net.minecraft.client.renderer.rendertype.RenderType renderRing(
      com.mojang.blaze3d.vertex.PoseStack matrix,
      net.minecraft.client.renderer.SubmitNodeCollector buffer,
      double cy,
      float flatness,
      float radius,
      float height,
      int num_segments,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      net.minecraft.client.renderer.rendertype.RenderType type,
      boolean isolateStrip
   ) {
      matrix.pushPose();
      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{
      org.joml.Matrix4f positionMatrix=pose.pose();
      double theta = 6.2831852 / num_segments;
      double q = height * Math.sin(stepsword.mahoutsukai.util.EffectUtil.toRad(flatness));
      double p = radius + height * Math.cos(stepsword.mahoutsukai.util.EffectUtil.toRad(flatness));
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      double xb = 0.0;
      double yb = 0.0;
      double zb = 0.0;
      float squeeze = 3.0F;
      float texx = 0.0F;

      for (int i = 0; i < num_segments + 1; i++) {
         texx += squeeze * 1.0F / (num_segments + 1);
         if (texx >= 1.0F) {
            texx = squeeze * 1.0F / (num_segments + 1);
            bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
            bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         }

         double tt = i * theta;
         x = -radius * Math.sin(tt);
         y = cy;
         z = radius * Math.cos(tt);
         xb = -p * Math.sin(tt);
         yb = cy - q;
         zb = p * Math.cos(tt);
         if (isolateStrip && i == 0) {
            bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly);
            bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly);
         }

         bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly);
         bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly);
      }

      if (isolateStrip) {
         bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly);
      }

      bb.addVertex(positionMatrix,(float)xb,(float)yb,(float)zb).setColor(r,g,b,a).setUv(texx,1).setUv2(lx,ly);
      });
      matrix.popPose();
      return type;
   }
   public static void renderSphere(
      com.mojang.blaze3d.vertex.PoseStack matrix,
      net.minecraft.client.renderer.SubmitNodeCollector buf,
      float radius,
      int gradation,
      int lx,
      int ly,
      float r,
      float g,
      float b,
      float a,
      net.minecraft.client.renderer.rendertype.RenderType type,
      float percentage,
      boolean isolateStrip
   ) {
      float PI = 3.141592F;
      buf.submitCustomGeometry(matrix,type,(pose,bb)->{
      org.joml.Matrix4f m=pose.pose();
      float alpha = 0.0F;

      while (alpha < 3.141592F) {
         float lastX = 0.0F;
         float lastY = 0.0F;
         float lastZ = 0.0F;
         boolean first = true;

         for (float beta = 0.0F; beta < 6.31459997177124 * percentage; beta += 3.141592F / gradation) {
            float x = (float)(radius * Math.cos(beta) * Math.sin(alpha));
            float y = (float)(radius * Math.sin(beta) * Math.sin(alpha));
            float z = (float)(radius * Math.cos(alpha));
            if (isolateStrip && first) {
               bb.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
               bb.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
               first = false;
            }

            bb.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
            x = (float)(radius * Math.cos(beta) * Math.sin(alpha + 3.141592F / gradation));
            y = (float)(radius * Math.sin(beta) * Math.sin(alpha + 3.141592F / gradation));
            z = (float)(radius * Math.cos(alpha + 3.141592F / gradation));
            bb.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
            lastX = x;
            lastY = y;
            lastZ = z;
         }

         if (isolateStrip && !first) {
            bb.addVertex(m, lastX, lastY, lastZ).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
         }

         alpha += 3.141592F / gradation;
      }
         });
   }
 public static net.minecraft.client.renderer.rendertype.RenderType renderRing(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,double cy,float flatness,float radius,float height,int segments,int lx,int ly,float r,float g,float b,float a,int index){return renderRing(matrix,collector,cy,flatness,radius,height,segments,lx,ly,r,g,b,a,MahoujinRenderType.createScopedRunesRenderType("render_utils_ring",runes,index));}

 public static net.minecraft.client.renderer.rendertype.RenderType renderRingCull(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,double cy,float flatness,float radius,float height,int segments,int lx,int ly,float r,float g,float b,float a,int index){return renderRing(matrix,collector,cy,flatness,radius,height,segments,lx,ly,r,g,b,a,MahoujinRenderType.createScopedRunesRenderType("render_utils_ring_cull",runes,index));}

 public static net.minecraft.client.renderer.rendertype.RenderType renderRing(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,double cy,float flatness,float radius,float height,int segments,int lx,int ly,float r,float g,float b,float a,int index,Identifier texture){return renderRing(matrix,collector,cy,flatness,radius,height,segments,lx,ly,r,g,b,a,MahoujinRenderType.createScopedRunesRenderType("render_utils_ring",texture,index));}

 public static org.joml.Quaternionf getQuaternion(org.joml.Vector3f axis,float angle,boolean degrees){return new org.joml.Quaternionf().rotationAxis(degrees?(float)Math.toRadians(angle):angle,axis.x,axis.y,axis.z);}

 public static void renderSphere(com.mojang.blaze3d.vertex.PoseStack m,net.minecraft.client.renderer.SubmitNodeCollector c,float radius,int segments,int lx,int ly,float r,float g,float b,float a,net.minecraft.client.renderer.rendertype.RenderType type,float percentage){renderSphere(m,c,radius,segments,lx,ly,r,g,b,a,type,percentage,true);}

 public static com.mojang.blaze3d.vertex.VertexConsumer addPoint(com.mojang.blaze3d.vertex.VertexConsumer vertices,org.joml.Matrix4f matrix,double x,double y,double z,float[] color){return vertices.addVertex(matrix,(float)x,(float)y,(float)z).setColor(color[0],color[1],color[2],color.length>3?color[3]:1);}

}
