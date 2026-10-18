package stepsword.mahoutsukai.fabric;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.reflect.Method;

/** Optional public Iris API; keep Iris absent from the mod's required dependencies. */
public final class IrisCompatibility {
 private static final boolean PRESENT=net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("iris");
 private static final java.util.Set<RenderPipeline> registered=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
 private static Object api;private static Class<?> programType,shadowType;private static Method assign,assignShadow;
 public static int registeredCount(){return registered.size();}
 @SuppressWarnings({"unchecked","rawtypes"})
 public static void register(RenderPipeline pipeline){
  if(!PRESENT || !pipeline.getLocation().getNamespace().equals("mahoutsukai") || registered.contains(pipeline))return;
  var format=pipeline.getVertexFormat();
  // Iris exposes its extended glyph/entity format only while drawing the level.
  // Register here rather than during model construction so its API chooses that format.
  if(format==DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP || format==DefaultVertexFormat.ENTITY)return;
  try{
   if(api==null){var type=Class.forName("net.irisshaders.iris.api.v0.IrisApi");api=type.getMethod("getInstance").invoke(null);programType=Class.forName("net.irisshaders.iris.api.v0.IrisProgram");shadowType=Class.forName("net.irisshaders.iris.api.v0.IrisShadowProgram");assign=type.getMethod("assignPipeline",RenderPipeline.class,programType);assignShadow=type.getMethod("assignPipelineShadow",RenderPipeline.class,shadowType);}
   boolean textured=format.getElementAttributeNames().contains("UV0");
   String program=pipeline.getVertexFormatMode()==VertexFormat.Mode.LINES?"LINES":textured?"ENTITIES_TRANSLUCENT":"BASIC";
   assign.invoke(api,pipeline,Enum.valueOf((Class)programType,program));
   if(textured)assignShadow.invoke(api,pipeline,Enum.valueOf((Class)shadowType,"SHADOW_ENTITIES"));
   registered.add(pipeline);
  }catch(ReflectiveOperationException e){throw new IllegalStateException("Cannot register Mahou pipeline with the installed Iris public API: "+pipeline.getLocation(),e);}
 }
}
