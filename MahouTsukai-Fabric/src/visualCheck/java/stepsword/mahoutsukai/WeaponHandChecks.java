package stepsword.mahoutsukai;
import java.nio.file.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import org.joml.Matrix4f;

public final class WeaponHandChecks {
 public static boolean capturing;public static Matrix4f matrix;
 public static void run(Minecraft mc) throws Exception {
  var player=mc.player;var savedMain=player.getMainHandItem().copy();var savedOff=player.getOffhandItem().copy();
  var renderer=net.minecraft.client.renderer.ItemInHandRenderer.class.getDeclaredMethod("renderArmWithItem",net.minecraft.client.player.AbstractClientPlayer.class,float.class,float.class,InteractionHand.class,float.class,ItemStack.class,float.class,com.mojang.blaze3d.vertex.PoseStack.class,net.minecraft.client.renderer.SubmitNodeCollector.class,int.class);renderer.setAccessible(true);
  var collector=(net.minecraft.client.renderer.SubmitNodeCollector)java.lang.reflect.Proxy.newProxyInstance(WeaponHandChecks.class.getClassLoader(),new Class[]{net.minecraft.client.renderer.SubmitNodeCollector.class},(proxy,method,args)->method.getReturnType().isInstance(proxy)?proxy:null);
  try {
   for(var hand:InteractionHand.values())for(String name:java.util.List.of("clarent","replica","treasury_projection_gauntlet")){
    player.stopUsingItem();var item=stepsword.mahoutsukai.item.ModItems.REGISTERED.get(name);if(item==null)throw new AssertionError("Missing fixture: "+name);
    var stack=new ItemStack(item);player.setItemInHand(hand,stack);player.startUsingItem(hand);
    if(!player.isUsingItem() || player.getUseItemRemainingTicks()<=0)throw new AssertionError("Native item use fixture did not start");
    matrix=null;capturing=true;renderer.invoke(mc.gameRenderer.itemInHandRenderer,player,0f,0f,hand,.2f,stack,.25f,new com.mojang.blaze3d.vertex.PoseStack(),collector,15728880);capturing=false;
    var arm=hand==InteractionHand.MAIN_HAND?player.getMainArm():player.getMainArm().getOpposite();int sign=arm==HumanoidArm.RIGHT?1:-1;
    var expected=new Matrix4f().translate(sign*.56f,-.52f+.25f*-.6f,-.72f);
    if(matrix==null || !matrix.equals(expected,.0001f))throw new AssertionError("First-person "+name+" "+hand+" differs from original Forge base transform: "+matrix);
    if(stack.getUseAnimation()!=ItemUseAnimation.BLOCK)throw new AssertionError("Render hook changed the item's gameplay animation");
   }
   player.stopUsingItem();var bow=new ItemStack(Items.BOW);player.setItemInHand(InteractionHand.MAIN_HAND,bow);player.startUsingItem(InteractionHand.MAIN_HAND);
   matrix=null;capturing=true;renderer.invoke(mc.gameRenderer.itemInHandRenderer,player,0f,0f,InteractionHand.MAIN_HAND,.2f,bow,.25f,new com.mojang.blaze3d.vertex.PoseStack(),collector,15728880);capturing=false;
   int sign=player.getMainArm()==HumanoidArm.RIGHT?1:-1;
   if(matrix==null || matrix.equals(new Matrix4f().translate(sign*.56f,-.67f,-.72f),.0001f))throw new AssertionError("Vanilla bow animation was suppressed");
   Files.writeString(Path.of("weapon-hands-check.passed"),"PASS: real first-person matrices match original Clarent/Replica/Treasury transforms in both hands; item BLOCK gameplay animation and vanilla bow transform preserved\n");
  }finally{capturing=false;player.stopUsingItem();player.setItemInHand(InteractionHand.MAIN_HAND,savedMain);player.setItemInHand(InteractionHand.OFF_HAND,savedOff);}
 }
}
