package stepsword.mahoutsukai;
import java.nio.file.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.ModItems;

public final class WeaponPoseChecks {
 private static void near(float actual,float expected,String message){if(Math.abs(actual-expected)>.0002)throw new AssertionError(message+": "+actual+" != "+expected);}
 public static void run(Minecraft mc) throws Exception {
  runModels(mc);handChecks(mc);
  Files.writeString(Path.of("weapon-poses-check.passed"),"PASS: original Nobu/Clarent pose coordinates and vanilla bob ordering, left/right dominant arms, main/offhand rifle, avatar and armor models, real avatar extraction and stop-use reset; native Mystic Code/First Sorcery hand calls submit real arm geometry and preserve pose stack\n");
 }
 public static void runModels(Minecraft mc) throws Exception {
  for(var layer:java.util.List.of(net.minecraft.client.model.geom.ModelLayers.PLAYER,net.minecraft.client.model.geom.ModelLayers.PLAYER_ARMOR.chest())){
   var model=new HumanoidModel<HumanoidRenderState>(mc.getEntityModels().bakeLayer(layer));
   for(var main:HumanoidArm.values())for(var hand:InteractionHand.values()){
    var state=new HumanoidRenderState();state.mainArm=main;state.attackArm=main;state.useItemHand=hand;state.isUsingItem=true;state.speedValue=1;state.ageInTicks=37;
    state.rightArmPose=state.leftArmPose=HumanoidModel.ArmPose.ITEM;
    state.rightHandItemStack=state.leftHandItemStack=ItemStack.EMPTY;
    var arm=hand==InteractionHand.MAIN_HAND?main:main.getOpposite();var item=new ItemStack(ModItems.nobu.get());
    if(arm==HumanoidArm.RIGHT)state.rightHandItemStack=item;else state.leftHandItemStack=item;
    model.setupAnim(state);
    float xBob=(float)Math.sin(37*.067f)*.05f;
    near(model.rightArm.yRot,-.1f+model.head.yRot,"Rifle right yaw");near(model.leftArm.yRot,.5f+model.head.yRot,"Rifle support yaw");
    near(model.rightArm.xRot,-(float)Math.PI/2+model.head.xRot+(arm==HumanoidArm.LEFT?xBob:0),"Original rifle right bob cancellation");
    near(model.leftArm.xRot,-(float)Math.PI/2+model.head.xRot-(arm==HumanoidArm.LEFT?xBob:0),"Original rifle support bob cancellation");
    if(hand==InteractionHand.MAIN_HAND){
     item=new ItemStack(ModItems.REGISTERED.get("clarent"));if(arm==HumanoidArm.RIGHT)state.rightHandItemStack=item;else state.leftHandItemStack=item;
     state.rightArmPose=state.leftArmPose=HumanoidModel.ArmPose.ITEM;model.setupAnim(state);var part=model.getArm(arm);int sign=arm==HumanoidArm.RIGHT?1:-1;
     near(part.x,-8*sign,"Clarent arm position");near(part.y,2,"Clarent arm height");near(part.yRot,(float)Math.PI*sign,"Clarent blocking yaw");
     near(part.xRot,(float)Math.toRadians(150)+sign*xBob,"Clarent blocking pitch with vanilla bob");
    }
   }
  }
  var player=mc.player;var saved=player.getMainHandItem().copy();
  try{
   player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(ModItems.nobu.get()));player.startUsingItem(InteractionHand.MAIN_HAND);
   var renderer=mc.getEntityRenderDispatcher().getPlayerRenderer(player);var state=renderer.createRenderState();renderer.extractRenderState(player,state,0);renderer.getModel().setupAnim(state);
   if(!state.isUsingItem || state.getMainHandItemStack().getItem()!=ModItems.nobu.get())throw new AssertionError("Real avatar state did not carry active Nobu");
   player.stopUsingItem();renderer.extractRenderState(player,state,0);renderer.getModel().setupAnim(state);
   if(state.isUsingItem || state.rightArmPose==HumanoidModel.ArmPose.CROSSBOW_HOLD || state.leftArmPose==HumanoidModel.ArmPose.CROSSBOW_HOLD)throw new AssertionError("Rifle pose leaked after native use stopped");
  }finally{player.stopUsingItem();player.setItemInHand(InteractionHand.MAIN_HAND,saved);}
  Files.writeString(Path.of("weapon-model-poses-check.passed"),"PASS: original Nobu/Clarent model poses and bob ordering, both dominant arms, both rifle hands, avatar and armor models, real avatar extraction and stop-use reset\n");
 }
 private static void handChecks(Minecraft mc) throws Exception {
  var calls=new java.util.concurrent.atomic.AtomicInteger();var itemGeometry=new java.util.concurrent.atomic.AtomicInteger();
  var collector=(net.minecraft.client.renderer.SubmitNodeCollector)java.lang.reflect.Proxy.newProxyInstance(WeaponPoseChecks.class.getClassLoader(),new Class[]{net.minecraft.client.renderer.SubmitNodeCollector.class},(proxy,method,args)->{
   if(method.getName().startsWith("submitModel"))calls.incrementAndGet();
   if(method.getName().equals("submitCustomGeometry"))itemGeometry.incrementAndGet();
   return method.getReturnType().isInstance(proxy)?proxy:null;
  });
  var render=net.minecraft.client.renderer.ItemInHandRenderer.class.getDeclaredMethod("renderArmWithItem",net.minecraft.client.player.AbstractClientPlayer.class,float.class,float.class,InteractionHand.class,float.class,ItemStack.class,float.class,com.mojang.blaze3d.vertex.PoseStack.class,net.minecraft.client.renderer.SubmitNodeCollector.class,int.class);render.setAccessible(true);
  var savedMain=mc.player.getMainHandItem().copy();var savedOff=mc.player.getOffhandItem().copy();
  try{for(var item:java.util.List.of(ModItems.mysticCode.get(),ModItems.mysticCodeFirstSorcery.get()))for(var hand:InteractionHand.values()){
   mc.player.setItemInHand(hand,new ItemStack(item));
   calls.set(0);itemGeometry.set(0);var pose=new com.mojang.blaze3d.vertex.PoseStack();var initial=new org.joml.Matrix4f(pose.last().pose());
   var rendered=hand==InteractionHand.MAIN_HAND?new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD):new ItemStack(item);
   render.invoke(mc.gameRenderer.itemInHandRenderer,mc.player,0f,0f,hand,.5f,rendered,.7f,pose,collector,15728880);
   if(calls.get()==0 || !pose.last().pose().equals(initial))throw new AssertionError("Mystic Code did not submit a real arm or left its transform on the stack");
   if(hand==InteractionHand.MAIN_HAND && itemGeometry.get()!=0)throw new AssertionError("Equip transition rendered the main-hand item despite original cancellation");
  }}finally{mc.player.setItemInHand(InteractionHand.MAIN_HAND,savedMain);mc.player.setItemInHand(InteractionHand.OFF_HAND,savedOff);}
 }
}
