package stepsword.mahoutsukai.render.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;


public class FogShaderInstance {
   public static Identifier FOG_TEX = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/particle/depth.png");
   public static Identifier DEPTH_TEX = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/particle/depth.png");
   public static boolean doDepth = true;
   public final RenderPipeline pipeline;
   public final Matrix4f modelViewMatrix = new Matrix4f();
   public final Matrix4f projectionMatrix = new Matrix4f();
   public final Vector4f smokeColor = new Vector4f(1.0F, 1.0F, 1.0F, 0.7F);
   public final Vector2f nearFar = new Vector2f(0.07F, 400.0F);
   public final Vector2f invertedViewport = new Vector2f(1.0F, 1.0F);
   public float smokeSoftness = 0.06F;

   public FogShaderInstance(RenderPipeline pipeline) {
      this.pipeline = pipeline;
   }

   public void smoke(float f) {
      this.smokeSoftness = f;
   }

   public void nearfar(float n, float f) {
      this.nearFar.set(n, f);
   }

   public void color(float r, float g, float b, float a) {
      this.smokeColor.set(r, g, b, a);
   }

   public void resize(float w, float h) {
      this.invertedViewport.set(1.0F / w, 1.0F / h);
   }

   public void initFog(int w, int h) {
   }

   public void finishFog() {
   }

   public void depth(int w, int h) {
      doDepth = false;
   }

   public void printGLERROR(String s) {
   }

   public void apply() {
   }

   public void close() {
   }
}
