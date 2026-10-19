package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.doom.DoomEntity;
import stepsword.mahoutsukai.render.MahoujinRenderType;

/** Dark core, animated accretion rings and a spherical shockwave; native geometry, no new texture dependency. */
public final class DoomRenderer extends EntityRenderer<DoomEntity, DoomRenderState> {
 private static final RenderType TYPE = MahoujinRenderType.createMahoujinRenderType(
  Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png"), 0, false);
 public DoomRenderer(EntityRendererProvider.Context context) { super(context); }
 public DoomRenderState createRenderState() { return new DoomRenderState(); }
 public void extractRenderState(DoomEntity entity, DoomRenderState state, float partial) {
  super.extractRenderState(entity, state, partial);
  state.age = entity.age() + partial;
 }
 @Override public boolean shouldRender(DoomEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
  return entity.shouldRenderAtSqrDistance(entity.distanceToSqr(x, y, z));
 }
 public void submit(DoomRenderState state, PoseStack matrix, SubmitNodeCollector collector, CameraRenderState camera) {
  matrix.pushPose();
  float age = state.age;
  if (age < DoomEntity.PREPARATION_TICKS) {
   float radius = DoomEntity.visualRadius(age);
   float grow = radius / DoomEntity.VISUAL_RADIUS;
   collector.submitCustomGeometry(matrix, TYPE, (pose, buffer) -> {
    sphere(buffer, pose.pose(), radius, 0.012F, 0.003F, 0.025F, 1);
   });
   for (int disk = 0; disk < 2; disk++) {
    matrix.pushPose();
    matrix.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(disk == 0 ? 18 : -24));
    matrix.mulPose(com.mojang.math.Axis.XP.rotationDegrees(disk == 0 ? 23 : 65));
    matrix.mulPose(com.mojang.math.Axis.YP.rotationDegrees(age * (disk == 0 ? 2.6F : -1.8F)));
    int diskIndex = disk;
    collector.submitCustomGeometry(matrix, TYPE, (pose, buffer) -> {
     // Thin bright event horizon, surrounded by broad, counter-rotating accretion disks.
     ring(buffer, pose.pose(), radius * 1.012F, radius * 1.045F, age, 0);
     for (int layer = 0; layer < 8; layer++) {
      float inner = radius * 1.04F + layer * 3.5F * grow;
      ring(buffer, pose.pose(), inner, inner + 3.8F * grow, age * (diskIndex == 0 ? 1 : -1), layer);
     }
    });
    matrix.popPose();
   }
  } else if (age < DoomEntity.PREPARATION_TICKS + 60) {
   float progress = (age - DoomEntity.PREPARATION_TICKS) / 60F;
   float radius = (float)Math.pow(progress,.65)*110;
   collector.submitCustomGeometry(matrix, TYPE, (pose, buffer) -> {
    ring(buffer, pose.pose(), radius, radius + 3 * (1 - progress), age, 0);
    ring(buffer, pose.pose(), radius * 0.8F, radius * 0.8F + 1.5F, -age, 1);
    sphere(buffer, pose.pose(), Math.max(0.1F, radius), 0.6F, 0.16F, 1, 0.13F * (1 - progress));
    if(progress < 0.25F)sphere(buffer, pose.pose(), DoomEntity.VISUAL_RADIUS * (1 - progress * 4), 0.9F, 0.72F, 1, 1 - progress * 4);
    // Short white-violet starburst, followed by a transparent shock shell.
    float flash=(float)Math.pow(Math.max(0,1-progress*3),2);
    if(flash>0)for(int i=0;i<48;i++){
     double angle=i*Math.PI/24,dx=Math.cos(angle),dz=Math.sin(angle),rise=Math.sin(angle*7)*.65;
     double outer=8+radius*1.2,inner=outer*.25,w=(.4+flash)*2;
     vertex(buffer,pose.pose(),dx*inner-dz*w,rise*inner,dz*inner+dx*w,1,.9F,1,flash);
     vertex(buffer,pose.pose(),dx*outer,rise*outer,dz*outer,.75F,.3F,1,0);
     vertex(buffer,pose.pose(),dx*outer,rise*outer+.2,dz*outer,.75F,.3F,1,0);
     vertex(buffer,pose.pose(),dx*inner+dz*w,rise*inner,dz*inner-dx*w,1,.9F,1,flash);
    }
   });
   for(int band=0;band<3;band++){
    float p=(progress-band*.07F);if(p<=0)continue;
    float r=(float)Math.pow(p,.7)*110;
    matrix.pushPose();matrix.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(band*60));matrix.mulPose(com.mojang.math.Axis.XP.rotationDegrees(30+band*35));
    collector.submitCustomGeometry(matrix,TYPE,(pose,buffer)->{ring(buffer,pose.pose(),r,r+Math.max(.2F,5*(1-progress)),age,2);});matrix.popPose();
   }
  }
  matrix.popPose();
 }
 private static void sphere(VertexConsumer buffer, Matrix4f pose, float radius, float r, float g, float b, float alpha) {
  for (int lat = 0; lat < 20; lat++) for (int lon = 0; lon < 48; lon++) {
   double a = Math.PI * lat / 20, a2 = Math.PI * (lat + 1) / 20;
   double t = Math.PI * 2 * lon / 48, t2 = Math.PI * 2 * (lon + 1) / 48;
   vertex(buffer, pose, radius*Math.sin(a)*Math.cos(t), radius*Math.cos(a), radius*Math.sin(a)*Math.sin(t), r,g,b,alpha);
   vertex(buffer, pose, radius*Math.sin(a2)*Math.cos(t), radius*Math.cos(a2), radius*Math.sin(a2)*Math.sin(t), r,g,b,alpha);
   vertex(buffer, pose, radius*Math.sin(a2)*Math.cos(t2), radius*Math.cos(a2), radius*Math.sin(a2)*Math.sin(t2), r,g,b,alpha);
   vertex(buffer, pose, radius*Math.sin(a)*Math.cos(t2), radius*Math.cos(a), radius*Math.sin(a)*Math.sin(t2), r,g,b,alpha);
  }
 }
 private static void ring(VertexConsumer buffer, Matrix4f pose, float inner, float outer, float age, int layer) {
  for (int index = 0; index < 128; index++) {
   double a = Math.PI * 2 * index / 128, b = Math.PI * 2 * (index + 1) / 128;
   float pulse = (float)(0.5 + 0.5 * Math.sin(a * 6 + layer * 1.7 + age * 0.12));
   float alpha = (1 - layer / 8F) * (0.25F + pulse * 0.6F);
   vertex(buffer,pose,inner*Math.cos(a),0,inner*Math.sin(a),0.6F,0.12F,1,alpha);
   vertex(buffer,pose,outer*Math.cos(a),0,outer*Math.sin(a),0.25F,0.015F,0.65F,0);
   vertex(buffer,pose,outer*Math.cos(b),0,outer*Math.sin(b),0.25F,0.015F,0.65F,0);
   vertex(buffer,pose,inner*Math.cos(b),0,inner*Math.sin(b),0.6F,0.12F,1,alpha);
  }
 }
 private static void vertex(VertexConsumer buffer, Matrix4f pose, double x, double y, double z, float r, float g, float b, float a) {
  buffer.addVertex(pose,(float)x,(float)y,(float)z).setColor(r,g,b,a).setUv(0.5F,0.5F).setUv2(240,240);
 }
}
