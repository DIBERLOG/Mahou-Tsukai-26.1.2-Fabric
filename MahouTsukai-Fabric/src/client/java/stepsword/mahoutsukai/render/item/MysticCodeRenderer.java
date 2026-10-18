package stepsword.mahoutsukai.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.client.ClientHandler;
import stepsword.mahoutsukai.client.ItemUseRenderBridge;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.mysticcode.MysticCode;
import stepsword.mahoutsukai.render.MahoujinRenderType;
import stepsword.mahoutsukai.render.NewRenderUtils;
import stepsword.mahoutsukai.render.RenderBaseItem;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.render.geometry.MahouGeometry;
import stepsword.mahoutsukai.render.geometry.RingGeometry;

public class MysticCodeRenderer implements SpecialModelRenderer<MagicalItemRenderState> {
   private static final String RING_SCOPE_1 = "mystic_code_renderer_1";
   private static final String RING_SCOPE_2 = "mystic_code_renderer_2";
   private final RenderBaseItem.ModelKeyPair model;
   private static final Identifier mahoujin = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/block/mahoujin_casting.png");

   public MysticCodeRenderer() {
      this(RenderBaseItem.mysticCode);
   }

   public MysticCodeRenderer(RenderBaseItem.ModelKeyPair model) {
      this.model = model;
   }

   public static void renderMysticCode(MagicalItemRenderState state, PoseStack matrix, SubmitNodeCollector submitNodeCollector) {
      float offsetx = 0.0F;
      float offsety = 0.0F;
      float offsetz = 0.0F;
      float r = state.r;
      float g = state.g;
      float b = state.b;
      float a = state.a;
      float rotation = 0.0F;
      float yaw = 0.0F;
      float pitch = 90.0F;
      float speed = (float)MTConfig.MYSTIC_CODE_RING_SPEED;
      float ring = 90.0F;
      float size = 1.0F;
      matrix.pushPose();
      float pull = (float)state.time;
      rotation += pull * speed;
      float secondrotation = 360.0F - pull * speed % 360.0F;
      rotation %= 360.0F;
      secondrotation %= 360.0F;
      matrix.translate(offsetx + 0.5, offsety + 0.5, offsetz);
      RenderUtils.rotateQ(yaw, 0.0F, 1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(pitch, 1.0F, 0.0F, 0.0F, matrix);
      RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
      int lightmapX = 240;
      int lightmapY = 240;
      r /= 255.0F;
      g /= 255.0F;
      b /= 255.0F;
      float scale = 0.9F;
      if (state.colorIndex != 8) {
         RenderType t1 = MahoujinRenderType.createMahoujinRenderType(mahoujin, 0, false);
         float finalScale = size * scale;
         float finalR = r;
         float finalG = g;
         float finalB = b;
         submitNodeCollector.submitCustomGeometry(matrix, t1, (pose, buffer) -> {
            NewRenderUtils.addVertex(pose, buffer, finalR, finalG, finalB, a, -0.5F * finalScale, 0.0F, -0.5F * finalScale, 0.0F, 0.0F, lightmapX, lightmapY);
            NewRenderUtils.addVertex(pose, buffer, finalR, finalG, finalB, a, -0.5F * finalScale, 0.0F, 0.5F * finalScale, 0.0F, 1.0F, lightmapX, lightmapY);
            NewRenderUtils.addVertex(pose, buffer, finalR, finalG, finalB, a, 0.5F * finalScale, 0.0F, 0.5F * finalScale, 1.0F, 1.0F, lightmapX, lightmapY);
            NewRenderUtils.addVertex(pose, buffer, finalR, finalG, finalB, a, 0.5F * finalScale, 0.0F, -0.5F * finalScale, 1.0F, 0.0F, lightmapX, lightmapY);
         });
      }

      RenderUtils.rotateQ(rotation, 0.0F, -1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(secondrotation, 0.0F, 1.0F, 0.0F, matrix);
      float[] colors = new float[]{r, g, b, a};
      MahouGeometry ring1 = new RingGeometry(size / 5.0F, 0.08F)
         .setStartingY(0.5)
         .setFlatness(ring)
         .setColor(colors)
         .setLx(lightmapX)
         .setLy(lightmapY)
         .setType(MahoujinRenderType.createScopedRunesRenderType("mystic_code_renderer_1", RenderUtils.runes, 0));
      ring1.render(matrix, submitNodeCollector);
      RenderUtils.rotateQ(secondrotation, 0.0F, -1.0F, 0.0F, matrix);
      RenderUtils.rotateQ(rotation, 0.0F, 1.0F, 0.0F, matrix);
      MahouGeometry ring2 = new RingGeometry(size / 5.0F, 0.08F)
         .setStartingY(0.7F)
         .setFlatness(ring)
         .setColor(colors)
         .setLx(lightmapX)
         .setLy(lightmapY)
         .setType(MahoujinRenderType.createScopedRunesRenderType("mystic_code_renderer_2", RenderUtils.runes, 0));
      ring2.render(matrix, submitNodeCollector);
      matrix.popPose();
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public @Nullable MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = new MagicalItemRenderState();
      state.render = stack.getItem() instanceof MysticCode;
      state.time = Minecraft.getInstance().level != null ? ClientHandler.clientTickCounter : 0L;
      state.model = (QuadCollection)Minecraft.getInstance().getModelManager().getModel(this.model.key);
      state.stack = stack;
      ItemDisplayContext transform = ItemUseRenderBridge.getDisplayContext(stack);
      state.transform = transform != null ? transform : ItemDisplayContext.NONE;
      state.useTicks = ItemUseRenderBridge.getUseTicks(stack);
      state.r = 230.0F;
      state.g = 40.0F;
      state.b = 40.0F;
      state.a = 1.0F;
      if (stack.getItem() instanceof MysticCode mcode) {
         int color = mcode.getColor(stack);
         switch (color) {
            case 1:
            case 2:
            case 10:
               state.r = 255.0F;
               state.g = 82.0F;
               state.b = 168.0F;
               break;
            case 3:
               state.r = 96.0F;
               state.g = 240.0F;
               state.b = 222.0F;
               break;
            case 4:
               state.r = 100.0F;
               state.g = 227.0F;
               state.b = 100.0F;
               break;
            case 5:
               state.r = 255.0F;
               state.g = 199.0F;
               state.b = 60.0F;
               break;
            case 6:
               state.r = 125.0F;
               state.g = 92.0F;
               state.b = 247.0F;
               break;
            case 7:
               state.r = 255.0F;
               state.g = 109.0F;
               state.b = 36.0F;
            case 8:
            default:
               break;
            case 9:
               state.r = 76.5F;
               state.g = 204.0F;
               state.b = 255.0F;
         }

         state.colorIndex = color;
      }

      return state;
   }

   public void submit(
      @Nullable MagicalItemRenderState state,
      PoseStack matrix,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (state != null && state.render) {
         matrix.pushPose();
         RenderType type = null;
         RenderBaseItem.render(state.model, matrix, submitNodeCollector, type, true);
         if (state.useTicks > 0.0F) {
            renderMysticCode(state, matrix, submitNodeCollector);
         }

         matrix.popPose();
      }
   }
}
