package stepsword.mahoutsukai;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import com.mojang.blaze3d.platform.InputConstants;
import stepsword.mahoutsukai.item.nobu.Nobu;
import net.minecraft.world.entity.LivingEntity;
import stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity;
import stepsword.mahoutsukai.visualcheck.mixin.MinecraftAttackInvoker;

/** Native mouse queue -> handleKeybinds while using Nobu, not a direct packet test. */
public final class NobuInputChecks {
 private static int ticks=-1,shotTick;private static boolean oldUse,oldPause;
 private static LivingEntity target;private static float health;
 public static void start(Minecraft mc){
  if(!(mc.player.getMainHandItem().getItem() instanceof Nobu)||stepsword.mahoutsukai.util.Utils.getMode(mc.player.getMainHandItem())!=0)throw new AssertionError("Hold Auto Nobu for the input fixture");
  if(!Keybinds.fireNobu.isDefault())throw new AssertionError("Fixture requires default left mouse Nobu binding");
  oldUse=mc.options.keyUse.isDown();oldPause=mc.options.pauseOnLostFocus;
  while(Keybinds.fireNobu.consumeClick()){}while(mc.options.keyAttack.consumeClick()){}
  mc.options.pauseOnLostFocus=false;mc.setScreen(null);ticks=0;shotTick=0;
  mc.gameMode.releaseUsingItem(mc.player);
  KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(0));
  ((MinecraftAttackInvoker)mc).mahoutsukai$handleKeybinds();
  if(Keybinds.fireNobu.consumeClick()){finish(mc);throw new AssertionError("Ordinary left click leaked into the next Nobu aim session");}
 }
 private static void finish(Minecraft mc){ticks=-1;mc.options.keyUse.setDown(oldUse);mc.options.pauseOnLostFocus=oldPause;mc.gameMode.releaseUsingItem(mc.player);}
 public static void tick(Minecraft mc){
  if(ticks<0||mc.player==null)return;
  try{
   ++ticks;
   // Wait for the native level-loaded acknowledgement before sending item use.
   if(ticks==40){mc.options.keyUse.setDown(true);mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
   if(ticks<70)return;
   if(shotTick==0){
   if(!mc.player.isUsingItem())throw new AssertionError("Native Nobu right mouse use stopped before trigger");
   var beams=mc.level.getEntitiesOfClass(GunBeamProjectileEntity.class,mc.player.getBoundingBox().inflate(30),b->mc.player.getUUID().equals(b.getCasterUUID())&&!b.getFired());
   if(beams.isEmpty()){if(ticks<200)return;throw new AssertionError("Server did not acknowledge native Nobu targeting with a real beam");}
   var entity=mc.level.getEntity(beams.getFirst().getTarget());
   if(!(entity instanceof LivingEntity living))throw new AssertionError("Native Nobu beam has no living target");
   target=living;health=target.getHealth();
   KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(0));
   ((MinecraftAttackInvoker)mc).mahoutsukai$handleKeybinds();
   if(Keybinds.fireNobu.consumeClick())throw new AssertionError("Default Nobu left click was never handled while right click was held");
   shotTick=ticks;return;
   }
   if(ticks-shotTick<30)return;
   if(target.getHealth()>=health)throw new AssertionError("Native Nobu trigger did not damage its actual server target");
   java.nio.file.Files.writeString(java.nio.file.Path.of("nobu-input-check.passed"),"PASS: native left mouse queue while holding right mouse -> acknowledged server beam -> real synchronized target health "+health+" -> "+target.getHealth()+"\n");
   finish(mc);
  }catch(Exception|AssertionError e){finish(mc);throw new RuntimeException(e);}
 }
}

