package stepsword.mahoutsukai;
import java.nio.file.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import stepsword.mahoutsukai.render.entity.archmage.ArchmageModel;
/** Actual native model bake and animation curves; never packaged in the distributable. */
public final class ArchmageVisualChecks {
 public static void heldItem(Minecraft mc)throws Exception{
  stepsword.mahoutsukai.entity.RealityMarbleBossEntity boss=null;
  for(var entity:mc.level.entitiesForRendering())if(entity instanceof stepsword.mahoutsukai.entity.RealityMarbleBossEntity candidate&&candidate.distanceToSqr(mc.player)<32*32){boss=candidate;break;}
  require(boss!=null,"A real synchronized Archmage must be visible");
  var renderer=(stepsword.mahoutsukai.render.entity.RenderRealityMarbleBoss)mc.getEntityRenderDispatcher().getRenderer(boss);var state=renderer.createRenderState();renderer.extractRenderState(boss,state,0);
  require(state.rightHandItemStack.is(stepsword.mahoutsukai.item.ModItems.emrysStaff.get())&&!state.rightHandItemState.isEmpty(),"Renderer extracts the real equipped Emrys stack");
  var bounds=state.rightHandItemState.getModelBoundingBox();require(bounds.getXsize()>.5&&bounds.getYsize()>.5,"Emrys has real generated shaft geometry instead of an empty special model");
  var model=new ArchmageModel(ArchmageModel.createBodyLayer().bakeRoot());var idle=new stepsword.mahoutsukai.render.entity.RenderRealityMarbleBoss.State();idle.walkAnimationSpeed=.7F;idle.walkAnimationPos=4;model.setupAnim(idle);var pose=new com.mojang.blaze3d.vertex.PoseStack();model.translateToHand(idle,net.minecraft.world.entity.HumanoidArm.RIGHT,pose);pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(135));
  var axis=new org.joml.Vector3f(-1,1,0).mulDirection(pose.last().pose()).normalize();require(axis.y<-.999F,"The actual Emrys sprite shaft is vertical in the animated hand");
  Files.writeString(Path.of("archmage-held-check.passed"),"PASS: real synchronized Archmage equipment, native living item extraction, nonempty Emrys shaft geometry and upright composed held-item transform\n");
 }
 private static void require(boolean ok,String text){if(!ok)throw new AssertionError(text);}
 private static String signature(List<ModelPart> parts){var out=new StringBuilder();for(var p:parts){for(float n:new float[]{p.x,p.y,p.z,p.xRot,p.yRot,p.zRot}){require(Float.isFinite(n),"Nonfinite bone transform");out.append(Math.round(n*10000)).append(',');}}return out.toString();}
 public static void run(Minecraft mc)throws Exception{
  var root=ArchmageModel.createBodyLayer().bakeRoot();var model=new ArchmageModel(root);var parts=root.getAllParts();
  var cubesField=ModelPart.class.getDeclaredField("cubes");cubesField.setAccessible(true);int cubes=0;for(var p:parts)cubes+=((java.util.List<?>)cubesField.get(p)).size();require(cubes==37,"All 37 body cuboids baked by Minecraft; old thirteen-cuboid staff is removed");
  require(root.getChild("rig").getChild("root_motion").getChild("body").getChild("right_arm").hasChild("staff"),"Staff follows right-arm pivot");
  var signatures=new HashSet<String>();int samples=0;
  for(String name:List.of("idle","walk","cast_spell","staff_attack","levitation","summon_pillars","area_attack","death","golden_volley","summon_vex","arrive","hurt_reaction","cast_recovery","combat_idle")){
   var unique=new HashSet<String>();float duration=switch(name){case "idle"->4;case "walk"->1.2F;case "cast_spell"->1.4F;case "staff_attack"->.8F;case "levitation"->3;case "summon_pillars","golden_volley","summon_vex"->1.6F;case "area_attack"->1.5F;case "arrive"->2.5F;case "hurt_reaction"->.35F;case "cast_recovery"->.6F;case "combat_idle"->3.2F;default->1;};
   for(int i=0;i<=40;i++){parts.forEach(ModelPart::resetPose);model.pose(name,duration*i/40,1);unique.add(signature(parts));samples++;}
   require(unique.size()>5,"Animation must actually move bones: "+name);
   parts.forEach(ModelPart::resetPose);model.pose(name,duration*.35F,1);signatures.add(signature(parts));
   if(List.of("idle","walk","levitation","combat_idle").contains(name)){parts.forEach(ModelPart::resetPose);model.pose(name,0,1);String first=signature(parts);parts.forEach(ModelPart::resetPose);model.pose(name,duration,1);require(first.equals(signature(parts)),"Continuous loop: "+name);}
  }
  require(signatures.size()==14,"Fourteen distinct animation poses");
  var rig=root.getChild("rig");var motion=rig.getChild("root_motion");var body=motion.getChild("body");var arm=body.getChild("right_arm");var staff=arm.getChild("staff");
  require(((java.util.List<?>)cubesField.get(staff)).isEmpty(),"The hand locator must have no baked staff geometry");
  int upright=0;var tiltedKinds=new HashSet<Integer>();
  for(int kind=0;kind<=6;kind++)for(int tick=1;tick<=32;tick++)for(boolean flying:new boolean[]{false,true}){
   var state=new stepsword.mahoutsukai.render.entity.RenderRealityMarbleBoss.State();state.ageInTicks=43+tick;state.attackKind=kind;state.attackTicks=kind==0?0:tick;state.walkAnimationPos=tick*.6F;state.walkAnimationSpeed=.7F;state.levitating=flying;model.setupAnim(state);
   var pose=new com.mojang.blaze3d.vertex.PoseStack();for(var bone:List.of(root,rig,motion,body,arm,staff))bone.translateAndRotate(pose);
   var axis=new org.joml.Vector3f(0,1,0).mulDirection(pose.last().pose()).normalize();
   require(Float.isFinite(axis.x)&&Float.isFinite(axis.y)&&Float.isFinite(axis.z),"Finite composed staff orientation");
   if(kind==0){require(Math.abs(axis.y)>.9999F,"Staff remains vertical in idle/walk/levitation: tick="+tick+" axis="+axis);require(arm.xRot<0&&body.getChild("left_arm").xRot<0,"Both walking arms stay forward instead of behind the body");upright++;}
   else if(Math.abs(axis.y)<.99F)tiltedKinds.add(kind);
   signature(parts);
   var cloak=body.getChild("cloak");require(cloak.z>=5&&cloak.xRot>0&&cloak.yRot==0,"Cloak always trails behind the body");
  }
  require(tiltedKinds.size()==6,"All six spell poses allow deliberate staff tilts");
  var blended=new HashSet<String>();
  for(int tick=0;tick<=50;tick++){
   var state=new stepsword.mahoutsukai.render.entity.RenderRealityMarbleBoss.State();state.ageInTicks=tick;state.healthRatio=.25F;state.arrivalTicks=50-tick;model.setupAnim(state);blended.add(signature(parts));
  }
  require(blended.size()>30,"Arrival actually animates through native render state");
  for(int tick=1;tick<=12;tick++){var state=new stepsword.mahoutsukai.render.entity.RenderRealityMarbleBoss.State();state.recoveryTicks=tick;state.hurtTime=tick%10;state.healthRatio=.25F;model.setupAnim(state);signature(parts);}
  for(var sound:List.of(stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_CHARGE.get(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_RELEASE.get(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_SHOCKWAVE.get(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_ARRIVAL.get(),stepsword.mahoutsukai.sounds.ModSounds.ARCHMAGE_DEFEAT_LAUGH.get())){
   var instance=net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound,1,.01F);require(mc.getSoundManager().play(instance)!=net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED,"New audio decodes: "+sound);mc.getSoundManager().stop(instance);
  }
  Files.writeString(Path.of("archmage-model-check.passed"),"PASS: 37 native body cuboids, empty held-item locator, forward walking arms, trailing cloak, 14 moving animations / "+samples+" finite samples, 4 seamless loops, "+upright+" upright hand poses, 6 deliberately tilted casts, native arrival/recovery/hurt states, 5 Archmage OGGs accepted by native sound engine\n");
 }
}
