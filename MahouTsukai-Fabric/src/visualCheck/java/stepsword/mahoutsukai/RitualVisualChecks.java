package stepsword.mahoutsukai;

import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.render.tile.MahoujinRenderer;
import stepsword.mahoutsukai.tile.boundary.AlarmBoundaryMahoujinTileEntity;
import stepsword.mahoutsukai.tile.mystic.CupOfHeavenMahoujinTileEntity;

public final class RitualVisualChecks {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void run(Minecraft mc) throws Exception {
  boolean cupSetting=MTConfig.HEAVENS_CUP_CLIENT_CIRCLE,soundSetting=MTConfig.MYSTIC_STAFF_EXPLOSION_SOUND;
  var master=mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER);var ui=mc.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.UI);double oldMaster=master.get(),oldUi=ui.get();
  var geometry=new AtomicInteger();var labels=new AtomicInteger();
  var collector=(net.minecraft.client.renderer.SubmitNodeCollector)java.lang.reflect.Proxy.newProxyInstance(RitualVisualChecks.class.getClassLoader(),new Class[]{net.minecraft.client.renderer.SubmitNodeCollector.class},(proxy,method,args)->{
   if(method.getName().equals("submitCustomGeometry")){require(((com.mojang.blaze3d.vertex.PoseStack)args[0]).last().pose().isFinite(),"Ritual geometry has a non-finite transform");geometry.incrementAndGet();}
   if(method.getName().equals("submitText"))labels.incrementAndGet();return null;
  });
  try {
   var cup=new CupOfHeavenMahoujinTileEntity(new BlockPos(0,1,0),ModBlocks.cupOfHeavenMahoujin.get().defaultBlockState());cup.setLevel(mc.level);
   var renderer=(MahoujinRenderer)(Object)mc.getBlockEntityRenderDispatcher().getRenderer(cup);
   require(stepsword.mahoutsukai.render.MahoujinRenderType.createScopedRunesRenderType("cup_check",stepsword.mahoutsukai.render.RenderUtils.runes,0).pipeline().getSamplers().contains("Sampler2"),"Ritual ring pipeline omits the shader lightmap sampler");
   var state=renderer.createRenderState();var pose=new com.mojang.blaze3d.vertex.PoseStack();
   var camera=new net.minecraft.client.renderer.state.level.CameraRenderState();
   MTConfig.HEAVENS_CUP_CLIENT_CIRCLE=false;renderer.extractRenderState(cup,state,0,mc.player.position(),null);require(!state.cup,"Cup visibility preference ignored");renderer.submit(state,pose,collector,camera);int plain=geometry.get();geometry.set(0);
   MTConfig.HEAVENS_CUP_CLIENT_CIRCLE=true;renderer.extractRenderState(cup,state,0,mc.player.position(),null);require(state.cup,"Cup overlay was not extracted");renderer.submit(state,pose,collector,camera);require(geometry.get()==plain+2,"Cup circle and rune ring were not submitted");
   var alarm=new AlarmBoundaryMahoujinTileEntity(BlockPos.ZERO,ModBlocks.alarmBoundaryMahoujin.get().defaultBlockState());alarm.setLevel(mc.level);alarm.alarmName="Ritual visual test";alarm.setCasterUUID(mc.player.getUUID());
   renderer.extractRenderState(alarm,state,0,mc.player.position(),null);require(alarm.alarmName.equals(state.display),"Owner alarm label missing");renderer.submit(state,pose,collector,camera);require(labels.get()==1,"Native text submission missing");
   alarm.setCasterUUID(java.util.UUID.randomUUID());renderer.extractRenderState(alarm,state,0,mc.player.position(),null);require(state.display==null && !state.cup,"Owner-only label or previous cup state leaked");
   var sound=net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(stepsword.mahoutsukai.sounds.ModSounds.EXPLOSION.get(),1);
   master.set(.01);ui.set(1.0);
   MTConfig.MYSTIC_STAFF_EXPLOSION_SOUND=false;require(mc.getSoundManager().play(sound)==net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED,"Original explosion mute preference ignored");
   MTConfig.MYSTIC_STAFF_EXPLOSION_SOUND=true;require(mc.getSoundManager().play(sound)!=net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED,"Explosion remained muted after preference restored");mc.getSoundManager().stop(sound);
   Files.writeString(Path.of("ritual-overlays-check.passed"),"PASS: native registered ritual renderer submits Cup circle/ring, finite low-height transform, owner-only alarm text and resets reused state; real SoundEngine obeys explosion mute and restores playback\n");
  }finally{MTConfig.HEAVENS_CUP_CLIENT_CIRCLE=cupSetting;MTConfig.MYSTIC_STAFF_EXPLOSION_SOUND=soundSetting;master.set(oldMaster);ui.set(oldUi);}
 }
}
