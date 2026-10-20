package stepsword.mahoutsukai;

import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import stepsword.mahoutsukai.render.gui.PauseMenuBranding;

/** Runs against real initialized native menus, including resize and compact GUI scales. */
public final class PauseBranding041Checks {
 public static void resourcePack(Minecraft client){
  var repository=client.getResourcePackRepository();repository.reload();
  var previous=java.util.List.copyOf(repository.getSelectedIds());
  String id="file/MahouTsukai-pause-menu-resourcepack-0.41.zip";
  var selection=new java.util.ArrayList<>(previous);selection.add(id);repository.setSelected(selection);
  if(!repository.getSelectedIds().contains(id))throw new AssertionError("Native pack repository did not accept the supplied ZIP");
  client.reloadResourcePacks().whenComplete((result,failure)->client.execute(()->{
   String error=failure==null?null:failure.toString();
   if(error==null){
    var resource=client.getResourceManager().getResource(PauseMenuBranding.LOGO).orElseThrow();
    if(!resource.sourcePackId().equals(id))error="Logo did not resolve from the selected resource pack: "+resource.sourcePackId();
   }
   String problem=error;repository.setSelected(previous);
   client.reloadResourcePacks().whenComplete((restored,restoreFailure)->client.execute(()->{
    try{
     boolean restoredIds=java.util.Set.copyOf(repository.getSelectedIds()).equals(java.util.Set.copyOf(previous));
     if(problem!=null||restoreFailure!=null||!restoredIds){Files.writeString(Path.of("pause-branding-pack-check.failed"),String.valueOf(problem)+" / "+restoreFailure+" / restored selection="+restoredIds+"\n");return;}
     Files.writeString(Path.of("pause-branding-pack-check.passed"),"PASS: native resource-pack ZIP discovery and reload; pause logo resolved from "+id+"; previous selection restored and reloaded\n");
    }catch(java.io.IOException failureToWrite){throw new RuntimeException(failureToWrite);}
   }));
  }));
 }
 public static void run(Minecraft client)throws java.io.IOException{
  var previous=client.screen;int oldScale=client.options.guiScale().get();
  int checked=0;
  try{
   for(int scale:new int[]{1,2,3,4}){
    client.options.guiScale().set(scale);client.resizeGui();
    var screen=new PauseScreen(true);client.setScreen(screen);
    int buttons=(int)screen.children().stream().filter(Button.class::isInstance).count();
    if(buttons<7)throw new AssertionError("Pause buttons were lost at scale "+scale);
    for(int resize=0;resize<2;resize++){
     if(resize>0)screen.resize(screen.width,screen.height);
     if(screen.children().stream().anyMatch(e->e instanceof StringWidget w&&w.visible&&w.getMessage().equals(screen.getTitle())))throw new AssertionError("The original menu heading is still visible");
     var box=PauseMenuBranding.bounds(screen);
     if(box.left()<0||box.top()<0||box.left()+box.width()>screen.width)throw new AssertionError("Logo escaped the screen at scale "+scale);
     for(var child:screen.children())if(child instanceof Button button&&button.visible){
      if(box.top()+box.height()>button.getY())throw new AssertionError("Logo overlaps a real pause button at scale "+scale);
      if(button.getMessage().getString().isBlank())throw new AssertionError("A pause control lost its label");
     }
     checked++;
    }
   }
   var loadingPause=new PauseScreen(false);client.setScreen(loadingPause);
   if(loadingPause.children().stream().noneMatch(e->e instanceof StringWidget w&&w.visible))throw new AssertionError("The compact loading pause heading must remain native");
   try(var stream=client.getResourceManager().open(PauseMenuBranding.LOGO);var png=com.mojang.blaze3d.platform.NativeImage.read(stream)){
    if(png.getWidth()!=2100||png.getHeight()!=749)throw new AssertionError("Unexpected shipped logo dimensions");
    int transparent=0,opaque=0;
    for(int y=0;y<png.getHeight();y+=7)for(int x=0;x<png.getWidth();x+=7){int alpha=png.getPixel(x,y)>>>24;if(alpha==0)transparent++;if(alpha==255)opaque++;}
    if(transparent<100||opaque<100)throw new AssertionError("The real logo needs transparent exterior and opaque lettering");
   }
   Files.writeString(Path.of("pause-branding-check.passed"),"PASS: "+checked+" native menu/resize layouts at GUI scales 1-4; original heading hidden; all buttons and labels retained; no logo/button overlap; loading pause preserved; actual RGBA PNG loaded through the resource manager\n");
  }finally{
   client.options.guiScale().set(oldScale);client.resizeGui();client.setScreen(previous);
  }
 }
}
