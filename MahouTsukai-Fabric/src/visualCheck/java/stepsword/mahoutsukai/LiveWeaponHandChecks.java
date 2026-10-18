package stepsword.mahoutsukai;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;

/** Capture real hand frames, including Iris's solid/translucent passes. */
public final class LiveWeaponHandChecks {
 private static boolean active,oldUse;private static int index,ticks;private static ItemStack main,off,current;
 private static net.minecraft.client.CameraType camera;private static Matrix4f parent,relative;private static float equip;
 private static InteractionHand hand(){return index<3||index==6?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;}
 public static void start(Minecraft mc){
  if(active)throw new IllegalStateException("Live hand check already running");
  main=mc.player.getMainHandItem().copy();off=mc.player.getOffhandItem().copy();oldUse=mc.options.keyUse.isDown();camera=mc.options.getCameraType();active=true;index=0;stage(mc);
 }
 private static void stage(Minecraft mc){
  mc.player.stopUsingItem();mc.player.setItemInHand(InteractionHand.MAIN_HAND,main.copy());mc.player.setItemInHand(InteractionHand.OFF_HAND,off.copy());
  current=new ItemStack(index==6?Items.BOW:stepsword.mahoutsukai.item.ModItems.REGISTERED.get(java.util.List.of("clarent","replica","treasury_projection_gauntlet").get(index%3)));
  mc.player.setItemInHand(hand(),current);mc.player.startUsingItem(hand());mc.options.keyUse.setDown(true);mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);mc.setScreen(null);mc.options.pauseOnLostFocus=false;
  ticks=0;parent=null;relative=null;
 }
 public static void before(InteractionHand renderedHand,ItemStack stack,float equipped,PoseStack pose){
  if(active&&relative==null&&renderedHand==hand()&&stack.getItem()==current.getItem()){parent=new Matrix4f(pose.last().pose());equip=equipped;}
 }
 public static void item(ItemStack stack,ItemDisplayContext display,PoseStack pose){
  if(active&&relative==null&&parent!=null&&display.firstPerson()&&stack.getItem()==current.getItem())relative=new Matrix4f(parent).invert().mul(pose.last().pose());
 }
 private static void finish(Minecraft mc){active=false;mc.options.keyUse.setDown(oldUse);mc.options.setCameraType(camera);mc.player.stopUsingItem();mc.player.setItemInHand(InteractionHand.MAIN_HAND,main);mc.player.setItemInHand(InteractionHand.OFF_HAND,off);}
 public static void tick(Minecraft mc){
  if(!active||mc.player==null)return;
  try{
   if(relative==null){if(++ticks>240)throw new AssertionError("No real hand frame for "+current.getItem()+" "+hand());return;}
   var arm=hand()==InteractionHand.MAIN_HAND?mc.player.getMainArm():mc.player.getMainArm().getOpposite();int sign=arm==HumanoidArm.RIGHT?1:-1;
   var expected=new Matrix4f().translate(sign*.56f,-.52f+equip*-.6f,-.72f);
   if(index<6&&!relative.equals(expected,.0005f))throw new AssertionError("Actual hand frame differs from original base transform: "+current.getItem()+" "+hand()+" "+relative);
   if(index==6&&relative.equals(expected,.0005f))throw new AssertionError("Actual vanilla bow frame lost its use transform");
   System.out.println("PASS: LIVE_HAND_FRAME "+index+" "+current.getItem()+" "+hand());
   if(++index==7){finish(mc);java.nio.file.Files.writeString(java.nio.file.Path.of("weapon-hands-live-check.passed"),"PASS: actual game frames match original Clarent/Replica/Treasury transforms in both hands; native bow animation preserved, including Iris hand rendering passes\n");}else stage(mc);
  }catch(Exception|AssertionError e){if(active)finish(mc);throw new RuntimeException(e);}
 }
}
