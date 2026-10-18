package stepsword.mahoutsukai.render.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.Leylines;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;

public class RenderLeyLines {
   public static int indexcounter = 0;

   public static void leyRender(Entity e, PoseStack matrix, float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buffer) {
      if (Leylines.leyDimensionValid(EffectUtil.getDimension(e.level()))) {
         if (e instanceof LivingEntity && EffectUtil.hasBuff((LivingEntity)e, ModEffects.FAY_SIGHT_EYES)) {
            int renderdist = MTConfig.LEY_RENDER_DISTANCE;
            int renderheight = MTConfig.LEY_RENDER_HEIGHT;
            BlockPos epos = e.blockPosition();
            int startx = epos.getX() - renderdist;
            int endx = epos.getX() + renderdist;
            int startz = epos.getZ() - renderdist;
            int endz = epos.getZ() + renderdist;
            indexcounter = 0;

            for (int i = startx; i < endx; i++) {
               for (int j = startz; j < endz; j++) {
                  BlockPos bp = new BlockPos(i, renderheight, j);
                  Leylines.LeyLineShape shape = Leylines.leyOrientation(bp);
                  if (shape != Leylines.LeyLineShape.NONE) {
                     boolean h = shape == Leylines.LeyLineShape.HORIZ || shape == Leylines.LeyLineShape.STAR;
                     boolean v = shape == Leylines.LeyLineShape.VERT || shape == Leylines.LeyLineShape.STAR;
                     boolean ne = shape == Leylines.LeyLineShape.NED || shape == Leylines.LeyLineShape.CROSS || shape == Leylines.LeyLineShape.STAR;
                     boolean nw = shape == Leylines.LeyLineShape.NWD || shape == Leylines.LeyLineShape.CROSS || shape == Leylines.LeyLineShape.STAR;
                     doRender(matrix, i, MTConfig.LEY_RENDER_HEIGHT, j, partialTicks, h, v, ne, nw,buffer);
                  }
               }
            }
         }
      }
   }

   public static void doRender(PoseStack matrix, double x, double y, double z, float partialTicks, boolean rh, boolean rv, boolean rne, boolean rnw,net.minecraft.client.renderer.SubmitNodeCollector buffer) {
      boolean runes = MTConfig.LEY_RUNES;
      Entity entity = Minecraft.getInstance().getCameraEntity();
      Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
      Vec3 proj = renderInfo.position();
      double d0 = proj.x;
      double d1 = proj.y;
      double d2 = proj.z;
      float r = 0.65882355F;
      float g = 1.0F;
      float b = 0.21176471F;
      float a = 1.0F;
      float center = 0.5F;
      d0 -= center;
      d1 -= center;
      d2 -= center;
      matrix.pushPose();
      runes(matrix, buffer, x, y, z, d0, d1, d2, rh, rv, rne, rnw, r, g, b, a, partialTicks);
      matrix.popPose();
   }

   public static void runes(
      PoseStack matrix,
      net.minecraft.client.renderer.SubmitNodeCollector buffer,
      double x,
      double y,
      double z,
      double d0,
      double d1,
      double d2,
      boolean rh,
      boolean rv,
      boolean rne,
      boolean rnw,
      float r,
      float g,
      float b,
      float a,
      float pt
   ) {

      float f = 0.15F;
      int lx = 240;
      int ly = 240;
      float minu = 0.0F;
      float maxu = 0.3F;
      float minv = 0.0F;
      float maxv = 1.0F;
      if (rh) {
         RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.runes, indexcounter, false);
         buffer.submitCustomGeometry(matrix,type,(pose,bb)->{Matrix4f m=pose.pose();
         bb.addVertex(m, (float)x - (float)d0, (float)y - (float)d1, (float)z - (float)d2 - f).setColor(r, g, b, a).setUv(minu, minv).setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0, (float)y - (float)d1, (float)z - (float)d2 + f).setColor(r, g, b, a).setUv(minu, maxv).setUv2(lx, ly);
         bb.addVertex(m, (float)x + 1.0F - (float)d0, (float)y - (float)d1, (float)z - (float)d2 + f).setColor(r, g, b, a).setUv(maxu, maxv).setUv2(lx, ly);
         bb.addVertex(m, (float)x + 1.0F - (float)d0, (float)y - (float)d1, (float)z - (float)d2 - f).setColor(r, g, b, a).setUv(maxu, minv).setUv2(lx, ly);
         });
      }

      if (rv) {
         RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.runes, indexcounter, false);
         buffer.submitCustomGeometry(matrix,type,(pose,bb)->{Matrix4f m=pose.pose();
         bb.addVertex(m, (float)x - (float)d0 + f, (float)y - (float)d1, (float)z - (float)d2).setColor(r, g, b, a).setUv(minu, minv).setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0 - f, (float)y - (float)d1, (float)z - (float)d2).setColor(r, g, b, a).setUv(minu, maxv).setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0 - f, (float)y - (float)d1, (float)z + 1.0F - (float)d2).setColor(r, g, b, a).setUv(maxu, maxv).setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0 + f, (float)y - (float)d1, (float)z + 1.0F - (float)d2).setColor(r, g, b, a).setUv(maxu, minv).setUv2(lx, ly);
         });
      }

      if (rne) {
         RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.runes, indexcounter, false);
         buffer.submitCustomGeometry(matrix,type,(pose,bb)->{Matrix4f m=pose.pose();
         bb.addVertex(m, (float)x + 1.0F - (float)d0 - f - 0.5F, (float)y - (float)d1, (float)z - (float)d2 - f - 0.5F)
            .setColor(r, g, b, a)
            .setUv(minu, minv)
            .setUv2(lx, ly);
         bb.addVertex(m, (float)x + 1.0F - (float)d0 + f - 0.5F, (float)y - (float)d1, (float)z - (float)d2 + f - 0.5F)
            .setColor(r, g, b, a)
            .setUv(minu, maxv)
            .setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0 + f - 0.5F, (float)y - (float)d1, (float)z - (float)d2 + 1.0F + f - 0.5F)
            .setColor(r, g, b, a)
            .setUv(maxu, maxv)
            .setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0 - f - 0.5F, (float)y - (float)d1, (float)z - (float)d2 + 1.0F - f - 0.5F)
            .setColor(r, g, b, a)
            .setUv(maxu, minv)
            .setUv2(lx, ly);
         });
      }

      if (rnw) {
         RenderType type = MahoujinRenderType.createMahoujinRenderType(RenderUtils.runes, indexcounter, false);
         buffer.submitCustomGeometry(matrix,type,(pose,bb)->{Matrix4f m=pose.pose();
         bb.addVertex(m, (float)x - (float)d0 + f, (float)y - (float)d1, (float)z - (float)d2 - f).setColor(r, g, b, a).setUv(minu, minv).setUv2(lx, ly);
         bb.addVertex(m, (float)x - (float)d0 - f, (float)y - (float)d1, (float)z - (float)d2 + f).setColor(r, g, b, a).setUv(minu, maxv).setUv2(lx, ly);
         bb.addVertex(m, (float)x + 1.0F - (float)d0 - f, (float)y - (float)d1, (float)z - (float)d2 + 1.0F + f)
            .setColor(r, g, b, a)
            .setUv(maxu, maxv)
            .setUv2(lx, ly);
         bb.addVertex(m, (float)x + 1.0F - (float)d0 + f, (float)y - (float)d1, (float)z - (float)d2 + 1.0F - f)
            .setColor(r, g, b, a)
            .setUv(maxu, minv)
            .setUv2(lx, ly);
         });
      }

      if (rnw && rne) {
         matrix.translate(x - d0, y - d1, z - d2);
         float rotation = (float)(ClientHandler.clientTickCounter % 360L);
         rotation = rotation - 1.0F + pt;
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
         RenderType ringType = MahoujinRenderType.createRunesPRenderType(RenderUtils.runes, indexcounter);
         RenderUtils.renderRing(matrix, buffer, -0.1, 0.0F, 3.0F, 0.3F, 64, lx, ly, r, g, b, a, ringType);
         RenderUtils.rotateQ(-rotation, 0.0F, 1.0F, 0.0F, matrix);

         RenderUtils.rotateQ(-rotation, 0.0F, 1.0F, 0.0F, matrix);
         ringType = MahoujinRenderType.createRunesPRenderType(RenderUtils.runes, indexcounter);
         RenderUtils.renderRing(matrix, buffer, -0.1, 0.0F, 5.0F, 0.3F, 64, lx, ly, r, g, b, a, ringType);
         RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);

         if (rv && rh) {
            RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
            ringType = MahoujinRenderType.createRunesPRenderType(RenderUtils.runes, indexcounter);
            RenderUtils.renderRing(matrix, buffer, -0.1, 0.0F, 7.0F, 0.3F, 64, lx, ly, r, g, b, a, ringType);
            RenderUtils.rotateQ(-rotation, 0.0F, 1.0F, 0.0F, matrix);
   
         }

         matrix.translate(-x + d0, -y + d1, -z + d2);
      }
   }
}
