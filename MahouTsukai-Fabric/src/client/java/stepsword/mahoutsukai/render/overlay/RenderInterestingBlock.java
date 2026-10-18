package stepsword.mahoutsukai.render.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.FamiliarEntity;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.render.RenderUtils;

public class RenderInterestingBlock {
   private static final String BOX_QUAD_SCOPE = "interesting_block_box_quad";
   private static final Identifier WHITE = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final int FAMILIAR_SEARCH_RADIUS = 64;
   private static final int RESCAN_INTERVAL = 20;
   private static final int RESCAN_MOVE_DISTANCE = 4;
   private static final List<BlockPos> interestingBlocks = new ArrayList<>();
   private static ClientLevel cachedLevel;
   private static BlockPos lastScanCenter;
   private static int cachedFamiliarId = -1;
   private static long nextScanTick;

   public static void updateInterestingBlocks() {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;
      if (level != null && minecraft.player != null) {
         if (cachedLevel != level) {
            clearInterestingBlocks();
            cachedLevel = level;
         }

         FamiliarEntity familiar = findFamiliar(level);
         if (familiar == null) {
            interestingBlocks.clear();
            lastScanCenter = null;
            cachedFamiliarId = -1;
         } else {
            BlockPos center = familiar.blockPosition();
            boolean movedFarEnough = lastScanCenter == null || center.distSqr(lastScanCenter) >= 16.0;
            if (movedFarEnough || level.getGameTime() >= nextScanTick) {
               scanInterestingBlocks(level, center);
               lastScanCenter = center.immutable();
               nextScanTick = level.getGameTime() + 20L;
            }
         }
      } else {
         clearInterestingBlocks();
      }
   }

   public static void clearInterestingBlocks() {
      interestingBlocks.clear();
      cachedLevel = null;
      lastScanCenter = null;
      cachedFamiliarId = -1;
      nextScanTick = 0L;
   }

   private static FamiliarEntity findFamiliar(ClientLevel level) {
      if (cachedFamiliarId >= 0 && level.getEntity(cachedFamiliarId) instanceof FamiliarEntity familiar && isOwnedByLocalPlayer(familiar)) {
         return familiar;
      }

      AABB searchArea = Minecraft.getInstance().player.getBoundingBox().inflate(64.0);
      List<FamiliarEntity> familiars = level.getEntitiesOfClass(FamiliarEntity.class, searchArea, RenderInterestingBlock::isOwnedByLocalPlayer);
      if (familiars.isEmpty()) {
         return null;
      }

      FamiliarEntity closest = familiars.getFirst();
      double closestDistance = closest.distanceToSqr(Minecraft.getInstance().player);

      for (int i = 1; i < familiars.size(); i++) {
         FamiliarEntity candidate = familiars.get(i);
         double distance = candidate.distanceToSqr(Minecraft.getInstance().player);
         if (distance < closestDistance) {
            closest = candidate;
            closestDistance = distance;
         }
      }

      cachedFamiliarId = closest.getId();
      return closest;
   }

   private static boolean isOwnedByLocalPlayer(FamiliarEntity familiar) {
      return familiar.getOwnerReference() != null
         && Minecraft.getInstance().player != null
         && familiar.getOwnerReference().getUUID().equals(Minecraft.getInstance().player.getUUID());
   }

   private static void scanInterestingBlocks(ClientLevel level, BlockPos center) {
      List<BlockPos> found = new ArrayList<>();
      int searchRadius = MTConfig.FAMILIAR_INTERESTING_BLOCK_RENDER_RANGE;
      int minX = center.getX() - searchRadius;
      int maxX = center.getX() + searchRadius;
      int minY = Math.max(level.getMinY(), center.getY() - searchRadius);
      int maxY = Math.min(level.getMaxY(), center.getY() + searchRadius);
      int minZ = center.getZ() - searchRadius;
      int maxZ = center.getZ() + searchRadius;
      int radiusSquared = searchRadius * searchRadius;

      for (int chunkX = SectionPos.blockToSectionCoord(minX); chunkX <= SectionPos.blockToSectionCoord(maxX); chunkX++) {
         for (int chunkZ = SectionPos.blockToSectionCoord(minZ); chunkZ <= SectionPos.blockToSectionCoord(maxZ); chunkZ++) {
            if (level.hasChunk(chunkX, chunkZ)) {
               LevelChunk chunk = level.getChunk(chunkX, chunkZ);
               LevelChunkSection[] sections = chunk.getSections();

               for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
                  int sectionMinY = SectionPos.sectionToBlockCoord(chunk.getSectionYFromSectionIndex(sectionIndex));
                  int sectionMaxY = sectionMinY + 15;
                  if (sectionMaxY >= minY && sectionMinY <= maxY) {
                     LevelChunkSection section = sections[sectionIndex];
                     if (section.maybeHas(FamiliarEntity::isInterestingBlock)) {
                        int sectionMinX = chunkX << 4;
                        int sectionMinZ = chunkZ << 4;
                        int fromX = Math.max(minX, sectionMinX);
                        int toX = Math.min(maxX, sectionMinX + 15);
                        int fromY = Math.max(minY, sectionMinY);
                        int toY = Math.min(maxY, sectionMaxY);
                        int fromZ = Math.max(minZ, sectionMinZ);
                        int toZ = Math.min(maxZ, sectionMinZ + 15);

                        for (int y = fromY; y <= toY; y++) {
                           for (int z = fromZ; z <= toZ; z++) {
                              for (int x = fromX; x <= toX; x++) {
                                 int dx = x - center.getX();
                                 int dy = y - center.getY();
                                 int dz = z - center.getZ();
                                 if (dx * dx + dy * dy + dz * dz <= radiusSquared
                                    && FamiliarEntity.isInterestingBlock(section.getBlockState(x & 15, y & 15, z & 15))) {
                                    found.add(new BlockPos(x, y, z));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      interestingBlocks.clear();
      interestingBlocks.addAll(found);
   }

   /** Original ore scanner, submitted through Fabric's native level render collector. */
   public static void interestingBlockOverlay(net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext context) {
      if(interestingBlocks.isEmpty()) return;
      var matrix=context.poseStack();var camera=Minecraft.getInstance().gameRenderer.getMainCamera().position();
      matrix.pushPose();matrix.translate(-camera.x,-camera.y,-camera.z);
      var positions=List.copyOf(interestingBlocks);
      float width=Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.appropriateLineWidth;
      context.submitNodeCollector().submitCustomGeometry(matrix,MahoujinRenderType.createLinesNoDepthRenderType(),(pose,buffer)-> {
         var stack=new PoseStack();stack.last().set(pose);
         for(var pos:positions) ShapeRenderer.renderShape(stack,buffer,Shapes.block(),pos.getX(),pos.getY(),pos.getZ(),-10759684,width);
      });
      matrix.popPose();
   }
}
