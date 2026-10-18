package stepsword.mahoutsukai.render.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import stepsword.mahoutsukai.Keybinds;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.spells.exchange.ImmunityExchangeSpellScroll;
import stepsword.mahoutsukai.item.spells.mystic.DamageReplicationSpellScroll;
import stepsword.mahoutsukai.item.spells.secret.GeasSpellScroll;
import stepsword.mahoutsukai.item.spells.secret.RetributionSpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class RenderSelectedEntity {
   static Identifier loc = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/particle/white_magic_particle1.png");
   public static Entity currentSelected = null;
   public static Entity secondSelected = null;
   public static long selectionKeyPress = 0L;

   public static void selectedEntity(Entity camera) {
      if (camera instanceof Player player) {
         HashSet<Utils.MAHOU_SELECTION> set = shouldRenderSelected(player);
         if (set.size() > 0) {
            if (set.contains(Utils.MAHOU_SELECTION.SELECTIVE_DISPLACEMENT) && Keybinds.selectiveDisplacement.isDown()) {
               selectionKeyPress++;
               if (currentSelected == null) {
                  currentSelected = Utils.selectEntityNearCursor(
                     player, getDistance(set), player.level(), a -> Utils.getPredicate(set, a, player), Utils.nearAllowed(set)
                  );
               } else if (selectionKeyPress > MTConfig.SELECTIVE_DISPLACEMENT_HOLD_TIME) {
                  secondSelected = Utils.selectEntityNearCursor(
                     player, getDistance(set), player.level(), a -> Utils.getPredicate(set, a, player), Utils.nearAllowed(set)
                  );
               }
            } else {
               currentSelected = Utils.selectEntityNearCursor(
                  player, getDistance(set), player.level(), a -> Utils.getPredicate(set, a, player), Utils.nearAllowed(set)
               );
            }
         } else {
            currentSelected = null;
            secondSelected = null;
         }
      }
   }

   public static void renderSelectedEntity(net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext event) {
      PoseStack matrix = event.poseStack();
      Entity camera = Minecraft.getInstance().getCameraEntity();
      float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
      net.minecraft.client.renderer.SubmitNodeCollector buffer = event.submitNodeCollector();
      if (MTConfig.SHOW_SELECTED_ENTITY && camera instanceof Player player) {
         if (currentSelected != null && !currentSelected.isInvisibleTo(player)) {
            renderSelection(matrix, buffer, partialTicks, currentSelected, player);
         }

         if (secondSelected != null && !secondSelected.isInvisibleTo(player)) {
            renderSelection(matrix, buffer, partialTicks, secondSelected, player);
         }
      }
   }

   public static void renderSelection(PoseStack matrix, net.minecraft.client.renderer.SubmitNodeCollector buffer, float partialTicks, Entity e, Player player) {
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
         float yaw = player.yRotO + (player.getYRot() - player.yRotO) * partialTicks;
         float pitch = player.xRotO + (player.getXRot() - player.xRotO) * partialTicks;
         RenderUtils.rotateQ(360.0F - yaw, 0.0F, 1.0F, 0.0F, matrix);
         RenderUtils.rotateQ(pitch + 90.0F, 1.0F, 0.0F, 0.0F, matrix);
         matrix.scale(0.5F, 0.5F, 0.5F);
         int space = 20;
         int separators = 4;
         int degrees = 360 / separators;
         RenderUtils.rotateQ((float)(player.level().getGameTime() % 360L * 2L), 0.0F, 1.0F, 0.0F, matrix);

         for (int i = 1; i < separators + 1; i++) {
            RenderUtils.rotateQ(degrees, 0.0F, 1.0F, 0.0F, matrix);
            RenderType type = MahoujinRenderType.createScopedRunesNoDepthTriangleStripRenderType("selected_entity", RenderUtils.beam, 0);
            renderArc(matrix, buffer, 0.0, 0.0F, 1.0F, 0.16F, 128, 240, 240, 1.0F, 0.8F, 0.7F, 0.6F, type, degrees - space, 0.0F);
            type = MahoujinRenderType.createScopedRunesNoDepthTriangleStripRenderType("selected_entity_inner", RenderUtils.beam, 0);
            renderArc(matrix, buffer, 0.0, 0.0F, 1.0F, 0.12F, 128, 240, 240, 1.0F, 1.0F, 1.0F, 1.0F, type, degrees - space, 0.0F);
         }
      }

      matrix.popPose();
   }

   public static HashSet<Utils.MAHOU_SELECTION> shouldRenderSelected(Player player) {
      var set=shouldRenderSelected(player,player.getMainHandItem().getItem());set.addAll(shouldRenderSelected(player,player.getOffhandItem().getItem()));return set;
   }
   public static HashSet<Utils.MAHOU_SELECTION> shouldRenderSelected(Player player,Item item) {
      var set=new HashSet<Utils.MAHOU_SELECTION>();
      if(EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT) && !EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN)) set.add(Utils.MAHOU_SELECTION.SELECTIVE_DISPLACEMENT);
      if(item instanceof stepsword.mahoutsukai.item.spells.projection.ProjectionSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_PROJECTION);
      if(item instanceof stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys) set.add(Utils.MAHOU_SELECTION.WEAPON);
      if(item instanceof RetributionSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_RETRIBUTION);
      if(item instanceof ImmunityExchangeSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_IMMUNITY_EXCHANGE);
      if(item instanceof DamageReplicationSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_DAMAGE_REPLICATION);
      if(item instanceof GeasSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_GEAS);
      return set;
   }

   public static int getDistance(HashSet<Utils.MAHOU_SELECTION> set) {
      return set.contains(Utils.MAHOU_SELECTION.SELECTIVE_DISPLACEMENT) ? MTConfig.SELECTIVE_DISPLACEMENT_RANGE : MTConfig.GLOBAL_LOOK_RANGE;
   }

   public static void releaseSelection() {
      var client=Minecraft.getInstance();var player=client.player;
      if(player==null) {currentSelected=null;secondSelected=null;selectionKeyPress=0;return;}
      if(!Keybinds.selectiveDisplacement.isDown() && selectionKeyPress>0L && EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT)
          && !EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN) && currentSelected!=null) {
         int first=currentSelected.getId(),second=secondSelected!=null ? secondSelected.getId() : first;
         if(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.TYPE))
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.SelectiveDisplacementPacket(first,second));
         selectionKeyPress=0;currentSelected=null;secondSelected=null;
      }
   }
   public static RenderType renderArc(
      PoseStack matrix,
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
      RenderType type,
      float arcfinish,
      float anglestart
   ) {
      matrix.pushPose();
      buffer.submitCustomGeometry(matrix,type,(pose,bb)-> {
      Matrix4f positionMatrix = pose.pose();
      double theta = 6.2831852 / num_segments;
      double q = height * Math.sin(EffectUtil.toRad(flatness));
      double p = radius + height * Math.cos(EffectUtil.toRad(flatness));
      double stop = EffectUtil.toRad(arcfinish);
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      double xb = 0.0;
      double yb = 0.0;
      double zb = 0.0;
      float circle_b = 0.0F;
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
         // SubmitNodeCollector batches strips: duplicate endpoints to keep original separate arcs.
         if(i==0) bb.addVertex(positionMatrix,(float)x,(float)y,(float)z).setColor(r,g,b,a).setUv(texx,0.0F).setUv2(lx,ly);
         bb.addVertex(positionMatrix, (float)x, (float)y, (float)z).setColor(r, g, b, a).setUv(texx, 0.0F).setUv2(lx, ly);
         bb.addVertex(positionMatrix, (float)xb, (float)yb, (float)zb).setColor(r, g, b, a).setUv(texx, 1.0F).setUv2(lx, ly);
         if (tt > stop) {
            break;
         }
      }

      bb.addVertex(positionMatrix,(float)xb,(float)yb,(float)zb).setColor(r,g,b,a).setUv(texx,1.0F).setUv2(lx,ly);
      });
      matrix.popPose();
      return type;
   }
}
