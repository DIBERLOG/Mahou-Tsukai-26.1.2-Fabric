package stepsword.mahoutsukai.render.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.potion.ClairvoyanceEyesPotion;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.ColorSubmitNodeCollector;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;

public class RenderClairvoyance {
   static Identifier loc = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/particle/white_magic_particle1.png");

   public static void renderPrediction(
      PoseStack matrix, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, Entity camera, float partialTicks
   ) {
      if (camera instanceof Player player && EffectUtil.hasBuff(player, ModEffects.CLAIRVOYANCE)) {
         SubmitNodeCollector predictionCollector = ColorSubmitNodeCollector.wrap(submitNodeCollector, 1.0F, 1.0F, 1.0F, 0.25F);
         ClairvoyanceEyesPotion.iterator = ClairvoyanceEyesPotion.predictionMap.keySet().iterator();

         while (ClairvoyanceEyesPotion.iterator.hasNext()) {
            PathfinderMob e = ClairvoyanceEyesPotion.iterator.next();
            Vec3 a = e.position();
            Vec3 b = camera.position();
            if (a.distanceTo(b) < MTConfig.CLAIRVOYANCE_RANGE && e.isAlive()) {
               Path path = ClairvoyanceEyesPotion.predictionMap.get(e);
               new CompoundTag();
               CompoundTag nbt;
               if (!ClairvoyanceEyesPotion.entityNBTMap.containsKey(e)) {
                  TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, e.registryAccess());
                  e.saveWithoutId(output);
                  nbt = output.buildResult();
                  nbt.putString("id", EntityType.getKey(e.getType()).toString());
                  ClairvoyanceEyesPotion.entityNBTMap.put(e, nbt);
               } else {
                  nbt = ClairvoyanceEyesPotion.entityNBTMap.get(e);
               }

               try {
                  Entity tmp = EntityType.loadEntityRecursive(nbt, e.level(), EntitySpawnReason.LOAD, entity -> entity);
                  PathfinderMob pred = null;
                  if (tmp instanceof PathfinderMob) {
                     pred = (PathfinderMob)tmp;
                  }

                  if (path != null && pred != null && !path.isDone() && path.getNodeCount() > path.getNextNodeIndex() + 1) {
                     int plength = path.getNodeCount();

                     for (int i = path.getNextNodeIndex(); i < plength; i++) {
                        Node point = path.getNode(i);
                        snapPredictionPose(pred, e, path, i, point.x, point.y, point.z, partialTicks);
                        if (a.distanceTo(pred.position()) < 1.0) {
                           path.setNextNodeIndex(i);
                        }

                        if (!path.isDone() && path.getNodeCount() > path.getNextNodeIndex() + 1) {
                           renderEntityStatic(pred, point.x, point.y, point.z, partialTicks, matrix, predictionCollector, cameraRenderState, 240);
                        }
                     }
                  }
               } catch (Exception var17) {
               }
            } else {
               ClairvoyanceEyesPotion.iterator.remove();
               ClairvoyanceEyesPotion.entityNBTMap.remove(e);
            }
         }

         ClairvoyanceEyesPotion.iterator = null;
      }
   }

   private static void snapPredictionPose(PathfinderMob pred, PathfinderMob source, Path path, int nodeIndex, double x, double y, double z, float partialTicks) {
      float yaw = getPredictionYaw(source, path, nodeIndex, x, z, partialTicks);
      float pitch = source.getXRot(partialTicks);
      pred.snapTo(x, y, z, yaw, pitch);
      pred.xo = pred.xOld = x;
      pred.yo = pred.yOld = y;
      pred.zo = pred.zOld = z;
      pred.yRotO = yaw;
      pred.xRotO = pitch;
      pred.setYBodyRot(yaw);
      pred.yBodyRotO = yaw;
      pred.setYHeadRot(yaw);
      pred.yHeadRotO = yaw;
   }

   private static float getPredictionYaw(PathfinderMob source, Path path, int nodeIndex, double x, double z, float partialTicks) {
      if (nodeIndex > 0) {
         Float yaw = yawBetween(path.getNode(nodeIndex - 1), x, z);
         if (yaw != null) {
            return yaw;
         }
      }

      if (nodeIndex + 1 < path.getNodeCount()) {
         Node next = path.getNode(nodeIndex + 1);
         Float yaw = yawFromDelta(next.x - x, next.z - z);
         if (yaw != null) {
            return yaw;
         }
      }

      return source.getYRot(partialTicks);
   }

   private static Float yawBetween(Node previous, double x, double z) {
      return yawFromDelta(x - previous.x, z - previous.z);
   }

   private static Float yawFromDelta(double xDiff, double zDiff) {
      return xDiff * xDiff + zDiff * zDiff < 1.0E-6 ? null : (float)(-Mth.atan2(xDiff, zDiff) * 180.0F / (float)Math.PI);
   }

   public static <E extends Entity> void renderEntityStatic(
      E entityIn,
      double xIn,
      double yIn,
      double zIn,
      float partialTicks,
      PoseStack matrixStackIn,
      SubmitNodeCollector submitNodeCollector,
      CameraRenderState cameraRenderState,
      int packedLightIn
   ) {
      try {
         Vec3 v = Minecraft.getInstance().gameRenderer.getMainCamera().position();
         EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
         EntityRenderState renderState = dispatcher.extractEntity(entityIn, partialTicks);
         renderState.lightCoords = packedLightIn;
         renderState.nameTag = null;
         renderState.scoreText = null;
         renderState.shadowRadius = 0.0F;
         dispatcher.submit(renderState, cameraRenderState, xIn - v.x, yIn - v.y, zIn - v.z, matrixStackIn, submitNodeCollector);
      } catch (Throwable var15) {
      }
   }
}
