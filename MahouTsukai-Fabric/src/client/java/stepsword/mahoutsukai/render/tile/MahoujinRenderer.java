package stepsword.mahoutsukai.render.tile;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import java.util.ArrayList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
/** Original shared circle geometry. Unported spell-specific overlays remain in recovered/. */
public class MahoujinRenderer implements BlockEntityRenderer<MahoujinTileEntity,MahoujinState> {
   public static Identifier location = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_changeable.png");
   public static Identifier spellClothLocation = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/spell_cloth.png");
   private static final Identifier white = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/white.png");
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");
   private static final Identifier circle = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/blood-circle.png");
   private static final Identifier circle2 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/blood-circle2.png");
   private static final Identifier circle3 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/blood-circle3.png");
   private static final Identifier square2 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/blood-square2.png");
   private static final Identifier fcircle = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/fay-circle.png");
   private static final Identifier fcircle2 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/fay-circle2.png");
   private static final Identifier fcircle3 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/fay-circle3.png");
   private static final Identifier fsquare2 = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/fay-square2.png");


 private static final BlockDisplayContext SCRY_BLOCK_DISPLAY_CONTEXT=BlockDisplayContext.create();
 private final BlockModelResolver blockModelResolver;
 public MahoujinRenderer(Context context){this.blockModelResolver=context.blockModelResolver();}
 public boolean shouldRenderOffScreen(){return true;}
 public MahoujinState createRenderState() { return new MahoujinState(); }
 public void extractRenderState(MahoujinTileEntity entity, MahoujinState state, float partialTicks, Vec3 camera, CrumblingOverlay breaking) {
  BlockEntityRenderer.super.extractRenderState(entity,state,partialTicks,camera,breaking);
  state.scry=null;state.scryBlocks.clear();state.partialTicks=partialTicks;
  state.cup=entity instanceof stepsword.mahoutsukai.tile.mystic.CupOfHeavenMahoujinTileEntity && stepsword.mahoutsukai.config.MTConfig.HEAVENS_CUP_CLIENT_CIRCLE;
  state.cupY=entity.getBlockPos().getY();state.display=null;
  var player=Minecraft.getInstance().player;
  if(player!=null && player.getUUID().equals(entity.getCasterUUID())){
   if(entity instanceof stepsword.mahoutsukai.tile.boundary.AlarmBoundaryMahoujinTileEntity alarm)state.display=alarm.getAlarmName();
   if(entity instanceof stepsword.mahoutsukai.tile.boundary.DrainLifeBoundaryMahoujinTileEntity drain)state.display=drain.getWhitelistName();
  }
  if(entity instanceof stepsword.mahoutsukai.tile.displacement.ScryingMahoujinTileEntity circle){state.scry=circle.scryTarget;state.scryTargetPos=circle.targetPos;if(circle.lst!=null)for(var info:circle.lst)state.scryBlocks.add(info==null?null:info.state);}
  state.fay=entity.isFay(); state.cloth=entity.hasCloth(); state.itemPreview=!entity.hasLevel();
  state.on=!state.cloth && (entity instanceof stepsword.mahoutsukai.tile.displacement.AscensionMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.exchange.CatalystExchangeMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.exchange.DurabilityExchangeMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.exchange.AlchemicalExchangeMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.boundary.BoundaryMahoujinTileEntity boundary && boundary.shouldIPerformBarrier()
    || entity instanceof stepsword.mahoutsukai.tile.boundary.RaiseEnclosureBarrierMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.projection.PowerConsolidationMahoujinTileEntity
    || entity instanceof stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity);
  state.time=ClientHandler.clientTickCounter;
  state.order=new ArrayList<>(entity.getCatalysts().getOrder());
  if(state.fay) state.order.add("fay");
 }
 public void submit(MahoujinState state, PoseStack matrix, SubmitNodeCollector collector, CameraRenderState camera) {
  if(state.scry!=null)scryingRender(state,matrix,collector,camera,blockModelResolver);
  circleRender(state.time,state.on,state.cloth,state.fay,state.order,matrix,collector,camera,state.itemPreview);
  if(state.cup)cupRender(state.time,state.cupY,matrix,collector);
  if(state.display!=null && !state.display.isEmpty())renderStringAboveCircle(matrix,collector,state.display);
 }
   public static void cupRender(float time, float yv, PoseStack matrix, SubmitNodeCollector submitter) {
      matrix.pushPose();
      if (yv <= 0.0F) {
         yv = 1.0F;
      }

      float offsetx = 0.0F;
      float offsety = 120.0F - yv;
      float offsetz = 0.0F;
      if (time > 18000.0F) {
         time -= 24000.0F;
      }

      float timeColor = Math.abs(time - 6000.0F) / 12000.0F;
      float r1 = 0.19215687F;
      float g1 = 0.007843138F;
      float b1 = 0.8901961F;
      float a1 = 1.0F;
      float r2 = 0.47058824F;
      float g2 = 0.83137256F;
      float b2 = 1.0F;
      float a2 = 1.0F;
      float r = r1 + (r2 - r1) * timeColor;
      float g = g1 + (g2 - g1) * timeColor;
      float b = b1 + (b2 - b1) * timeColor;
      float a = a1 + (a2 - a1) * timeColor;
      float rotation = (float)ClientHandler.clientTickCounter;
      float yaw = 0.0F;
      float pitch = 0.0F;
      int twopower = 0;
      float ring = 0.0F;
      rotation--;
      float size;
      if (yv > 80.0F) {
         size = 2.0F;
      } else {
         while (yv > 2.0F) {
            yv /= 2.0F;
            twopower++;
         }

         size = 20.0F / Math.max(1, twopower);
      }

      if (size < 0.4F) {
         size = 0.4F;
      }

      matrix.translate(offsetx + 0.5, offsety, offsetz + 0.5);
      matrix.scale(size, size, size);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
      int lx = 240;
      int ly = 240;
      RenderType type = MahoujinRenderType.createBuiltInTextMahoujinRenderType("heavens_cup", mahoujin, 0, true);
      float finalSize = size;
      submitter.submitCustomGeometry(matrix, type, (pose, buffer) -> {
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, -0.5F * finalSize, 0.0F, -0.5F * finalSize, 0.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, -0.5F * finalSize, 0.0F, 0.5F * finalSize, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, 0.5F * finalSize, 0.0F, 0.5F * finalSize, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, r, g, b, a, 0.5F * finalSize, 0.0F, -0.5F * finalSize, 1.0F, 0.0F, lx, ly);
      });
      NewRenderUtils.renderRing("mahoujin_renderer_ring", matrix, submitter, 0.0, ring, size / 2.0F + 0.125F, 0.125F, 128, lx, ly, r, g, b, a);
      matrix.popPose();
   }

   public static void renderStringAboveCircle(PoseStack pose,SubmitNodeCollector collector,String label){
      var mc=Minecraft.getInstance();var text=net.minecraft.network.chat.Component.literal(label);
      pose.pushPose();pose.translate(.5,.5,.5);pose.mulPose(mc.gameRenderer.getMainCamera().rotation());
      RenderUtils.rotateQ(180,0,1,0,pose);pose.scale(-.025f,-.025f,.025f);
      collector.submitText(pose,-mc.font.width(text)/2f,0,text.getVisualOrderText(),false,net.minecraft.client.gui.Font.DisplayMode.NORMAL,15728880,0xffddddff,0,0);
      pose.popPose();
   }

   public static void circleRender(
      long time, boolean on, boolean cloth, boolean fay, ArrayList<String> order, PoseStack matrix, SubmitNodeCollector submitter, CameraRenderState camera
   ) {
      circleRender(time, on, cloth, fay, order, matrix, submitter, camera, false);
   }

   private static void circleRender(
      long time,
      boolean on,
      boolean cloth,
      boolean fay,
      ArrayList<String> order,
      PoseStack matrix,
      SubmitNodeCollector submitter,
      CameraRenderState camera,
      boolean itemPreview
   ) {
      float rr = 1.0F;
      float gg = 1.0F;
      float bb = 1.0F;
      float aa = 1.0F;
      Color[] colors = getColors(order);
      float r1 = colors[0].getRed() / 255.0F;
      float g1 = colors[0].getGreen() / 255.0F;
      float b1 = colors[0].getBlue() / 255.0F;
      float a1 = colors[0].getAlpha() / 255.0F;
      float r2 = colors[1].getRed() / 255.0F;
      float g2 = colors[1].getGreen() / 255.0F;
      float b2 = colors[1].getBlue() / 255.0F;
      float a2 = colors[1].getAlpha() / 255.0F;
      float r3 = colors[2].getRed() / 255.0F;
      float g3 = colors[2].getGreen() / 255.0F;
      float b3 = colors[2].getBlue() / 255.0F;
      float a3 = colors[2].getAlpha() / 255.0F;
      int lx = 240;
      int ly = 240;
      float oscparam = 0.1F;
      long t = time;
      long t2 = t * 5L;
      float oscillate = (float)(oscparam / 2.0F + oscparam / 2.0F * Math.sin((float)t2 / 100.0F));
      float oscillate1 = (float)(oscparam / 2.0F + oscparam / 2.0F * Math.sin((float)t2 / 90.0F));
      float oscillate2 = (float)(oscparam / 2.0F + oscparam / 2.0F * Math.sin((float)t2 / 80.0F));
      float oscillate3 = (float)(oscparam / 2.0F + oscparam / 2.0F * Math.sin((float)t2 / 70.0F));
      float oscillate4 = (float)(oscparam / 2.0F + oscparam / 2.0F * Math.sin((float)t2 / 60.0F));
      float oscillate5 = (float)(oscparam / 2.0F + oscparam / 2.0F * Math.sin((float)t2 / 50.0F));
      if (!on) {
         t2 = 0L;
         t = 0L;
         oscillate = 0.0F;
         oscillate1 = 0.0F;
         oscillate2 = 0.0F;
         oscillate3 = 0.0F;
         oscillate4 = 0.0F;
         oscillate5 = 0.0F;
      }

      matrix.pushPose();
      matrix.translate(0.5F, 0.0F, 0.5F);
      matrix.pushPose();
      renderCloth(matrix, submitter, cloth, itemPreview);
      matrix.popPose();
      RenderType c;
      RenderType c2;
      RenderType c3;
      RenderType s2;
      if (fay) {
         c = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_circle_fay", fcircle, 1, false);
         c2 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_circle_fay_2", fcircle2, 1, false);
         c3 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_circle_fay_3", fcircle3, 1, false);
         s2 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_square_fay", fsquare2, 1, false);
      } else {
         c = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_circle_blood", circle, 1, false);
         c2 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_circle_blood_2", circle2, 1, false);
         c3 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_circle_blood_3", circle3, 1, false);
         s2 = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_square_blood", square2, 1, false);
      }

      RenderType lines = MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_line_blood", white, 1, false);
      moveTex(matrix, 0.0F, 1.0E-4F + oscillate, 0.0F, 1.0F, 0.0F, false);
      renderTex(c, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 1.0E-4F + oscillate1, 0.0F, 0.69F, 0.0F, false);
      renderTex(c2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 1.0E-4F + oscillate2, 0.0F, 0.43F, 0.0F, false);
      renderTex(c3, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 1.0E-4F + oscillate1, 0.0F, 0.19F, (float)(-t) * 1.0F, false);
      drawTriangle(lines, submitter, matrix, r1, g1, b1, a1);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 2.0E-4F + oscillate1, 0.0F, 0.19F, 40.0F - (float)t * 2.0F, false);
      drawTriangle(lines, submitter, matrix, r2, g2, b2, a2);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 3.0E-4F + oscillate1, 0.0F, 0.19F, 80.0F - (float)t * 3.0F, false);
      drawTriangle(lines, submitter, matrix, r3, g3, b3, a3);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 5.0E-5F + oscillate, 0.0F, 1.2F, (float)(0L + t), false);
      renderTex(s2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 5.0E-5F + oscillate1, 0.0F, 1.2F, 0.0F + (float)t * 2.0F, false);
      renderTex(s2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 5.0E-5F + oscillate2, 0.0F, 1.2F, (float)(30L + t), false);
      renderTex(s2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 5.0E-5F + oscillate3, 0.0F, 1.2F, (float)(30L + t * 2L), false);
      renderTex(s2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 5.0E-5F + oscillate4, 0.0F, 1.2F, (float)(60L + t), false);
      renderTex(s2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      moveTex(matrix, 0.0F, 5.0E-5F + oscillate5, 0.0F, 1.2F, (float)(60L + t * 2L), false);
      renderTex(s2, submitter, matrix, rr, gg, bb, aa, lx, ly);
      moveTex(matrix, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, true);
      matrix.popPose();
   }

   public static void drawTriangle(RenderType type, SubmitNodeCollector submitter, PoseStack matrix, float rr, float gg, float bb, float aa) {
      float wide = 0.86602527F;
      float high = 0.5000002F;
      float r = -1.0F;
      float width = 0.8F;
      float wide2 = width * wide;
      float high2 = width * high;
      float r2 = r * width;
      int lx = 240;
      int ly = 240;
      submitter.submitCustomGeometry(matrix, type, (pose, buffer) -> {
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, 0.0F, 0.06875F, r, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, wide, 0.06875F, high, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, wide2, 0.06875F, high2, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, 0.0F, 0.06875F, r2, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, 0.0F, 0.06875F, r, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, -wide, 0.06875F, high, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, -wide2, 0.06875F, high2, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, 0.0F, 0.06875F, r2, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, -wide, 0.06875F, high, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, wide, 0.06875F, high, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, wide2, 0.06875F, high2, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, -wide2, 0.06875F, high2, 0.0F, 1.0F, lx, ly);
      });
   }

   public static void renderTex(RenderType type, SubmitNodeCollector submitter, PoseStack matrix, float rr, float gg, float bb, float aa, int lx, int ly) {
      submitter.submitCustomGeometry(matrix, type, (pose, buffer) -> {
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, -0.5F, 0.0625F, 0.5F, 0.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, 0.5F, 0.0625F, 0.5F, 1.0F, 1.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, 0.5F, 0.0625F, -0.5F, 1.0F, 0.0F, lx, ly);
         NewRenderUtils.addVertex(pose, buffer, rr, gg, bb, aa, -0.5F, 0.0625F, -0.5F, 0.0F, 0.0F, lx, ly);
      });
   }

   public static void moveTex(PoseStack m, float x, float y, float z, float s, float degrees, boolean reverse) {
      if (reverse) {
         m.popPose();
      } else {
         m.pushPose();
         m.translate(x, y, z);
         RenderUtils.rotateQ(degrees, 0.0F, 1.0F, 0.0F, m);
         m.scale(s, 1.0F, s);
      }
   }

   public static void renderCloth(PoseStack stack, SubmitNodeCollector submitter, boolean renderCloth) {
      renderCloth(stack, submitter, renderCloth, false);
   }

   private static void renderCloth(PoseStack stack, SubmitNodeCollector submitter, boolean renderCloth, boolean itemPreview) {
      if (renderCloth) {
         RenderType type = itemPreview
            ? MahoujinRenderType.createBuiltInTextMahoujinRenderType("mahoujin_item_cloth", spellClothLocation, 0, false)
            : MahoujinRenderType.createShadedRenderType(spellClothLocation, 0, false);
         float rr = 1.0F;
         float gg = 1.0F;
         float bb = 1.0F;
         float aa = 1.0F;
         float rr2 = 0.6F;
         float gg2 = 0.6F;
         float bb2 = 0.6F;
         int lx = 240;
         int ly = 240;
         submitter.submitCustomGeometry(stack, type, (pose, buffer) -> {
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, -0.5F, 0.0625F, 0.5F, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.0625F, 0.5F, 1.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.0625F, -0.5F, 1.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, -0.5F, 0.0625F, -0.5F, 0.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, -0.5F, 0.0F, 0.5F, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, -0.5F, 0.0F, -0.5F, 0.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.0F, 0.5F, 1.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0625F, -0.5F, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0625F, -0.5F, 1.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0F, -0.5F, 0.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0625F, 0.5F, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0625F, 0.5F, 1.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0F, 0.5F, 1.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0F, 0.5F, 0.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0625F, -0.5F, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0625F, 0.5F, 1.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0F, 0.5F, 1.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, 0.5F, 0.0F, -0.5F, 0.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0625F, 0.5F, 0.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0625F, -0.5F, 1.0F, 1.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 240, 240);
            NewRenderUtils.addVertex(pose, buffer, 0.6F, 0.6F, 0.6F, 1.0F, -0.5F, 0.0F, 0.5F, 0.0F, 0.0F, 240, 240);
         });
      }
   }

   public static Color[] getColors(ArrayList<String> order) {
      Color newFirst = new Color(0, 0, 0, 0);
      Color newSecond = new Color(0, 0, 0, 0);
      Color newThird = new Color(0, 0, 0, 0);
      if (!order.isEmpty()) {
         if (!order.get(0).equals("fay")) {
            newFirst = ModItems.catalystMap.get(order.get(0)).COLOR;
         }

         if (order.size() > 1) {
            if (!order.get(1).equals("fay")) {
               newSecond = ModItems.catalystMap.get(order.get(1)).COLOR;
            }

            if (order.size() > 2 && !order.get(2).equals("fay")) {
               newThird = ModItems.catalystMap.get(order.get(2)).COLOR;
            }
         }
      }

      return new Color[]{newFirst, newSecond, newThird};
   }

   public static void scryingRender(
      MahoujinState state, PoseStack stack, SubmitNodeCollector submitter, CameraRenderState camera, BlockModelResolver blockModelResolver
   ) {
      Entity e = state.scry;
      stack.pushPose();
      float scale = 0.2F;
      double invscaleall = 1.0 / scale;
      stack.translate(0.5F, 0.5F, 0.5F);
      stack.scale(scale, scale, scale);

      try {
         EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
         EntityRenderState renderState = dispatcher.extractEntity(e, state.partialTicks);
         renderState.lightCoords = state.lightCoords;
         renderState.nameTag = null;
         renderState.scoreText = null;
         renderState.shadowRadius = 0.0F;
         renderState.shadowPieces.clear();
         dispatcher.submit(renderState, camera, 0.0, 0.0, 0.0, stack, submitter);
      } catch (Throwable var13) {
      }

      stack.translate(invscaleall * -0.125, 0.0, invscaleall * -0.125);
      if (state.scryTargetPos != null) {
         stack.translate(
            -0.5 * (state.scryTargetPos.x - Math.floor(state.scryTargetPos.x)),
            -0.5 * (state.scryTargetPos.y - Math.floor(state.scryTargetPos.y)),
            -0.5 * (state.scryTargetPos.z - Math.floor(state.scryTargetPos.z))
         );
      }

      if (!state.scryBlocks.isEmpty()) {
         int index = 0;

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               for (int k = -1; k <= 2; k++) {
                  if (index >= state.scryBlocks.size()) {
                     stack.popPose();
                     return;
                  }

                  renderScryBlock(state.scryBlocks.get(index), i, k, j, stack, submitter, blockModelResolver, state.lightCoords);
                  index++;
               }
            }
         }
      }

      stack.popPose();
   }

   private static void renderScryBlock(
      BlockState blockState, int x, int y, int z, PoseStack stack, SubmitNodeCollector submitter, BlockModelResolver blockModelResolver, int lightCoords
   ) {
      if (blockState != null && !blockState.isAir() && blockState.getBlock() != Blocks.CAVE_AIR && blockState.getBlock() != Blocks.VOID_AIR) {
         stack.pushPose();
         stack.translate(x, y, z);

         try {
            BlockModelRenderState blockRenderState = new BlockModelRenderState();
            blockModelResolver.update(blockRenderState, blockState, SCRY_BLOCK_DISPLAY_CONTEXT);
            blockRenderState.submit(stack, submitter, lightCoords, OverlayTexture.NO_OVERLAY, 0);
         } catch (Throwable var9) {
         }

         stack.popPose();
      }
   }

}
