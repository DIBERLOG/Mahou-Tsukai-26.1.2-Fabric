package stepsword.mahoutsukai.client;
import java.util.ArrayList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.nobu.Nobu;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.networking.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.render.*;
import stepsword.mahoutsukai.render.item.RuleBreakerRenderer;
import stepsword.mahoutsukai.render.overlay.RenderSelectedEntity;
public final class NobuClientEffect {
 public static ArrayList<Entity> targets;
 public static boolean targetsHasChanged;
   public static boolean nobuAimFireHandler(Player player, int button) {
      boolean ret = false;

      if (button == 0 && player.isUsingItem()) {
         ItemStack stack = player.getUseItem();
         if (stack.getItem() instanceof Nobu) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new NobuBeamCreatePacket(new ArrayList<>(), NobuBeamCreatePacket.NobuFirePacket.FIRE_GUN));
            if (targets != null && !targets.isEmpty() && Utils.getMode(stack) == 0) {
               net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new NobuBeamCreatePacket(new ArrayList<>(), NobuBeamCreatePacket.NobuFirePacket.FIRE_BEAM));
               ret = true;
            }
         }

         if (stack.getItem() instanceof Nobu
            && (Utils.getMode(stack) == 1 || Utils.getMode(stack) == 2 || Utils.getMode(stack) == 3)
            && targets != null
            && !targets.isEmpty()) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new NobuBeamCreatePacket(new ArrayList<>(), NobuBeamCreatePacket.NobuFirePacket.FIRE_GUN));
            ret = true;
         }
      }

      if (button == 1) {
         ItemStack stack = player.getMainHandItem();
         if (stack.getItem() instanceof Nobu && (Utils.getMode(stack) == 1 || Utils.getMode(stack) == 2) && targets != null && !targets.isEmpty()) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new NobuSetTargetPacket(targets.getFirst().getId()));
         }
      }

      return ret;
   }

   public static void nobuAutoTargetMode(Player player) {
      int MAX_NOBU_AUTO_TARGETS = 8;
      double NOBU_DISTANCE = 50.0;
      double NOBU_LOOK_ANGLE = 80.0;
      if (player != null) {
         if (player.isUsingItem()) {
            ItemStack stack = player.getUseItem();
            if (stack.getItem() instanceof Nobu && Utils.getMode(stack) == 0) {
               if (targets == null) {
                  targets = new ArrayList<>();
               }

               if (targets.size() < MAX_NOBU_AUTO_TARGETS) {
                  ArrayList<Entity> select = Utils.selectEntitiesNearCursor(
                     MAX_NOBU_AUTO_TARGETS, player, NOBU_DISTANCE, NOBU_LOOK_ANGLE, player.level(), ex -> isNobuTargetable(player.level(), player, ex), true
                  );
                  ArrayList<Integer> createBeam = new ArrayList<>();
                  if (!select.isEmpty()) {
                     for (Entity e : select.subList(0, Math.max(0, Math.min(select.size(), Math.min(select.size(), MAX_NOBU_AUTO_TARGETS - targets.size()))))) {
                        targets.add(e);
                        targetsHasChanged = true;
                        createBeam.add(e.getId());
                     }
                  }

                  if (targetsHasChanged) {
                     net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new NobuBeamCreatePacket(createBeam, NobuBeamCreatePacket.NobuFirePacket.CREATE_BEAM));
                     targetsHasChanged = false;
                  }
               }
            }
         } else {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof Nobu) {
               if (Utils.getMode(stack) != 1 && Utils.getMode(stack) != 2) {
                  clearTargets();
               } else if (Nobu.getSelectedTarget(stack) <= 0) {
                  targets = new ArrayList<>();
                  ArrayList<Entity> select = Utils.selectEntitiesNearCursor(
                     1, player, NOBU_DISTANCE, NOBU_LOOK_ANGLE, player.level(), ex -> isNobuTargetable(player.level(), player, ex), true
                  );
                  targets = select;
               }
            } else {
               clearTargets();
            }
         }
      }
   }

   public static void clearTargets() {
      if (targets != null && !targets.isEmpty()) {
         targets = null;
      }
   }

 public static boolean isNobuTargetable(Level level,Player player,Entity target){return target instanceof LivingEntity && target!=player && target.isAlive() && (targets==null || !targets.contains(target)) && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpellClient(player,target);}
   public static void renderSelection(PoseStack matrix, SubmitNodeCollector buffer, float partialTicks, Entity e, Player player) {
      Vec3 proj = Minecraft.getInstance().gameRenderer.getMainCamera().position();
      double d3 = proj.x;
      double d4 = proj.y;
      double d5 = proj.z;
      Vec3 center = e.getBoundingBox().getCenter();
      Vec3 diff = center.subtract(e.position());
      Vec3 prev = new Vec3(e.xo, e.yo, e.zo);
      prev = prev.add(diff);
      double d0 = prev.x + (center.x - prev.x) * partialTicks;
      double d1 = prev.y + (center.y - prev.y) * partialTicks;
      double d2 = prev.z + (center.z - prev.z) * partialTicks;
      matrix.pushPose();
      matrix.translate(d0 - d3, d1 - d4, d2 - d5);
      if (player != null) {
         float yaw = player.yRotO + (player.yRot - player.yRotO) * partialTicks;
         float pitch = player.xRotO + (player.xRot - player.xRotO) * partialTicks;
         RenderUtils.rotateQ(360.0F - yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, matrix);
         float r = 0.4F;
         float g = 0.8F;
         float b = 0.6F;
         float a = 0.6F;
         renderCrossHairLine(r, g, b, a, 1.0F, 1.42F, 0.04F, 240, 240, 0.0F, matrix, buffer);
         renderCrossHairLine(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.4F, 0.02F, 240, 240, 0.01F, matrix, buffer);
         RenderUtils.rotateQ(90.0F, 0.0F, 1.0F, 0.0F, matrix);
         renderCrossHairLine(r, g, b, a, 1.0F, 1.42F, 0.04F, 240, 240, 0.02F, matrix, buffer);
         renderCrossHairLine(1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.4F, 0.02F, 240, 240, 0.03F, matrix, buffer);
         matrix.scale(0.5F, 0.5F, 0.5F);
         int space = 20;
         int separators = 8;
         int degrees = 360 / separators;
         RenderUtils.rotateQ(-5.0F, 0.0F, 1.0F, 0.0F, matrix);

         for (int i = 1; i < separators + 1; i++) {
            RenderUtils.rotateQ(degrees, 0.0F, 1.0F, 0.0F, matrix);
            RenderType type = (
               (MahoujinRenderType.createRunesNoDepthRenderType(RenderUtils.beam, 0))
            );
            RenderSelectedEntity.renderArc(matrix, buffer, 0.0, 0.0F, 0.8F, 0.06F, 32, 240, 240, r, g, b, a, type, degrees - space + 5, 1.0F);
            type = ((MahoujinRenderType.createRunesNoDepthRenderType(RenderUtils.beam, 0)));
            RenderSelectedEntity.renderArc(matrix, buffer, 0.01F, 0.0F, 0.8F, 0.04F, 32, 240, 240, 1.0F, 1.0F, 1.0F, 1.0F, type, degrees - space, 0.0F);
         }

         r = 1.0F;
         g = 1.0F;
         b = 0.6F;
         RenderUtils.rotateQ((float)(player.level().getGameTime() % 360L * 2L), 0.0F, 1.0F, 0.0F, matrix);
         RenderType type = (
            (MahoujinRenderType.createRunesRenderType(RenderUtils.runes, 15))
         );
         RenderUtils.renderRing(matrix, buffer, 0.0, 0.0F, 0.4F, 0.2F, 32, 240, 240, r, g, b, 1.0F, type);
         RenderUtils.rotateQ((float)(-(player.level().getGameTime() % 360L) * 4L), 0.0F, 1.0F, 0.0F, matrix);
         type = ((MahoujinRenderType.createRunesRenderType(RenderUtils.runes, 15)));
         RenderUtils.renderRing(matrix, buffer, 0.0, 0.0F, 1.0F, 0.2F, 32, 240, 240, r, g, b, 1.0F, type);
      }

      matrix.popPose();
   }

   public static void renderCrossHairLine(
      float r, float g, float b, float a, float size, float len, float height, int lx, int ly, float zindex, PoseStack matrix, SubmitNodeCollector buf
   ) {
      RenderType type = (
         (MahoujinRenderType.createBoxRenderType(RuleBreakerRenderer.white, 0))
      );
      matrix.pushPose();
      matrix.scale(height, size, len);
      matrix.pushPose();
      buf.submitCustomGeometry(matrix,type,(nativePose,bb)->{
      bb.addVertex(nativePose.pose(), -0.5F, zindex, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 0.5F, zindex, 0.5F).setColor(r, g, b, a).setUv(1.0F, 1.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), 0.5F, zindex, -0.5F).setColor(r, g, b, a).setUv(1.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -0.5F, zindex, -0.5F).setColor(r, g, b, a).setUv(0.0F, 0.0F).setUv2(lx, ly);
      bb.addVertex(nativePose.pose(), -0.5F, zindex, 0.5F).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lx, ly);
      });
      matrix.popPose();
      matrix.popPose();
   }
}
