package stepsword.mahoutsukai;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
public final class VisualCapture implements ClientModInitializer {
 private int pendingBlackChecks;
 private boolean predictionGeometry;
 private static Object countCollector(Object delegate,Class<?> type,java.util.concurrent.atomic.AtomicInteger calls){return java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},(proxy,method,args)->{if(method.getName().startsWith("submitModel"))calls.incrementAndGet();Object result=method.invoke(delegate,args);return result instanceof net.minecraft.client.renderer.OrderedSubmitNodeCollector ordered?countCollector(ordered,net.minecraft.client.renderer.OrderedSubmitNodeCollector.class,calls):result;});}

 public void onInitializeClient() {
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{
   if(!predictionGeometry)return;var mc=net.minecraft.client.Minecraft.getInstance();var calls=new java.util.concurrent.atomic.AtomicInteger();var collector=(net.minecraft.client.renderer.SubmitNodeCollector)countCollector(context.submitNodeCollector(),net.minecraft.client.renderer.SubmitNodeCollector.class,calls);
   stepsword.mahoutsukai.render.overlay.RenderClairvoyance.renderPrediction(context.poseStack(),collector,context.levelState().cameraRenderState,mc.player,0);
   if(calls.get()==0){if(++pendingBlackChecks>300)throw new AssertionError("Clairvoyance received paths but native ghost renderer submitted no model");return;}
   predictionGeometry=false;pendingBlackChecks=0;try{Files.writeString(Path.of("six-prediction-geometry.passed"),"PASS: live path packets produced "+calls.get()+" actual ghost model submissions through native renderer and original alpha wrapper\n");Files.writeString(Path.of("visual-capture.request"),"six-prediction-frame");}catch(java.io.IOException e){throw new RuntimeException(e);}
  });
  ClientTickEvents.END_CLIENT_TICK.register(client -> {
   LiveWeaponHandChecks.tick(client);
   NobuInputChecks.tick(client);
   var request = Path.of("visual-capture.request");
   if (client.level == null || !Files.isRegularFile(request)) return;
   try {
    String name = Files.readString(request).strip();
    Files.delete(request);
    if (!name.matches("[a-z0-9-]+")) throw new IllegalArgumentException("Invalid capture name");
    if(name.equals("battle-respawn")){if(client.player.isDeadOrDying())client.player.respawn();client.setScreen(null);return;}
    if(name.equals("archmage-model-check")){try{ArchmageVisualChecks.run(client);}catch(Exception failure){throw new RuntimeException(failure);}return;}
    if(name.equals("stage-hud-unawakened")){var mana=stepsword.mahoutsukai.util.Utils.getPlayerMahou(client.player);mana.setHasMagic(false);mana.setVisible(true);client.setScreen(null);return;}
    if(name.equals("activation-geometry-check")){try{ActivationVisualChecks.run();}catch(Exception failure){throw new RuntimeException(failure);}return;}
    if(name.equals("handbook-open")){client.setScreen(new stepsword.mahoutsukai.render.gui.guide.HandbookScreen(client.screen));return;}
    if(name.equals("research-open")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.GuideCatalogScreen book))throw new AssertionError("Open server catalogue before research");client.setScreen(new stepsword.mahoutsukai.render.gui.guide.ResearchScreen(book));return;}
    if(name.equals("research-familiar")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.ResearchScreen research))throw new AssertionError("Research screen missing");research.focusNode("family_familiar");return;}
    if(name.equals("research-doom")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.ResearchScreen research))throw new AssertionError("Research screen missing");research.focusNode("doom");return;}
    if(name.equals("research-fireball")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.ResearchScreen research))throw new AssertionError("Research screen missing");research.focusNode("fireball");return;}
    if(name.equals("handbook-fireball")){client.setScreen(new stepsword.mahoutsukai.render.gui.guide.HandbookScreen(client.screen,"fireball"));return;}
    if(name.startsWith("research-focus-")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.ResearchScreen research))throw new AssertionError("Research screen missing");research.focusNode("family_"+name.substring("research-focus-".length()));return;}
    if(name.equals("research-controls-check")){
     if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.ResearchScreen research))throw new AssertionError("Research screen missing");
     var search=(net.minecraft.client.gui.components.EditBox)research.children().stream().filter(w->w instanceof net.minecraft.client.gui.components.EditBox).findFirst().orElseThrow();
     search.setValue(net.minecraft.network.chat.Component.translatable("mahoutsukai.research.family_familiar.title").getString());
     var selectedField=research.getClass().getDeclaredField("selected");selectedField.setAccessible(true);var selected=(com.google.gson.JsonObject)selectedField.get(research);
     if(!selected.get("key").getAsString().equals("family_familiar"))throw new AssertionError("Native search did not find family branch");
     var zoomField=research.getClass().getDeclaredField("zoom");zoomField.setAccessible(true);double before=zoomField.getDouble(research);
     var leftField=research.getClass().getDeclaredField("left");leftField.setAccessible(true);var topField=research.getClass().getDeclaredField("top");topField.setAccessible(true);
     if(!research.mouseScrolled(leftField.getInt(research)+50,topField.getInt(research)+80,0,1)||zoomField.getDouble(research)<=before)throw new AssertionError("Native research zoom failed");
     research.onClose();if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.GuideCatalogScreen book))throw new AssertionError("Back did not restore catalogue");book.filter("scroll_summon_familiar");
     Files.writeString(Path.of(name+".passed"),"PASS: native search widget finds family branch, mouse-wheel zoom and return to original catalogue preserve recipe navigation\n");return;
    }
    if(name.equals("presentation-third-person")){client.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);client.setScreen(null);return;}
    if(name.equals("presentation-audio-check")){
     int individual=0;for(var item:stepsword.mahoutsukai.item.ModItems.REGISTERED.values())if(item instanceof stepsword.mahoutsukai.item.spells.SpellScroll)for(boolean release:new boolean[]{false,true}){
      var sound=net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(stepsword.mahoutsukai.sounds.ModSounds.spellCast(item,release),1,.01F);if(client.getSoundManager().play(sound)==net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED)throw new AssertionError("Individual spell sound unavailable: "+item);client.getSoundManager().stop(sound);individual++;
     }
     for(var family:stepsword.mahoutsukai.fabric.SpellPresentation.Family.values())for(boolean release:new boolean[]{false,true}){
      var sound=net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(stepsword.mahoutsukai.sounds.ModSounds.cast(family.name().toLowerCase(java.util.Locale.ROOT),release),1,.01F);
      if(client.getSoundManager().play(sound)==net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED)throw new AssertionError("Custom spell audio unavailable: "+family+release);client.getSoundManager().stop(sound);
     }
     for(var event:java.util.List.of(stepsword.mahoutsukai.sounds.ModSounds.DOOM_ARRIVAL_HORN.get(),stepsword.mahoutsukai.sounds.ModSounds.DOOM_IMPACT_HORN.get())){
      var sound=net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(event,1,.01F);if(client.getSoundManager().play(sound)==net.minecraft.client.sounds.SoundEngine.PlayResult.NOT_STARTED)throw new AssertionError("DOOM horn unavailable");client.getSoundManager().stop(sound);
     }
     Files.writeString(Path.of(name+".passed"),"PASS: "+individual+" individual spell sounds plus 18 family fallback and two DOOM film events accepted by native sound engine\n");return;
    }
    if(name.startsWith("catalog-search-")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.GuideCatalogScreen book))throw new AssertionError("Server did not open catalog");book.filter(name.substring("catalog-search-".length()));return;}
    if(name.equals("hints-toggle-check")){
     try{var screen=new stepsword.mahoutsukai.render.gui.PlayerSettingsGUI(client.level,client.player.getUUID());client.setScreen(screen);
      var listField=screen.getClass().getDeclaredField("buttons");listField.setAccessible(true);var list=(net.minecraft.client.gui.components.ObjectSelectionList<?>)listField.get(screen);
      var entry=list.children().getFirst();var buttonField=entry.getClass().getDeclaredField("button");buttonField.setAccessible(true);var button=(net.minecraft.client.gui.components.Button)buttonField.get(entry);
      var callbackField=net.minecraft.client.gui.components.Button.class.getDeclaredField("onPress");callbackField.setAccessible(true);var callback=(net.minecraft.client.gui.components.Button.OnPress)callbackField.get(button);
      if(!stepsword.mahoutsukai.config.MTConfig.SHOW_CAST_HINTS)throw new AssertionError("Expected shipped hints on");callback.onPress(button);
      if(stepsword.mahoutsukai.config.MTConfig.SHOW_CAST_HINTS||stepsword.mahoutsukai.render.gui.MahouGui.castHints(null,client.player,0,0,client.font)!=0)throw new AssertionError("Toggle did not hide hints");
      stepsword.mahoutsukai.config.MTConfig.loadClient(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("mahoutsukai-fabric-client.json"));if(stepsword.mahoutsukai.config.MTConfig.SHOW_CAST_HINTS)throw new AssertionError("Disabled choice not persisted");
      callback.onPress(button);if(!stepsword.mahoutsukai.config.MTConfig.SHOW_CAST_HINTS)throw new AssertionError("Re-enable failed");
      Files.writeString(Path.of("hints-toggle-check.passed"),"PASS: actual settings button callback disables HUD, saves false across config reload, and re-enables default-on hints\n");
     }catch(Exception e){throw new RuntimeException(e);}return;
    }
    if(name.equals("progression-hints-settings")){client.setScreen(new stepsword.mahoutsukai.render.gui.PlayerSettingsGUI(client.level,client.player.getUUID()));return;}
    if(name.equals("progression-first-person")){client.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);client.options.keyUse.setDown(false);client.gameMode.releaseUsingItem(client.player);client.setScreen(null);return;}
    if(name.equals("progression-stop-key")){net.minecraft.client.KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.getKey("key.keyboard.x"));return;}
    if(name.equals("progression-hold")){client.options.pauseOnLostFocus=false;client.setScreen(null);client.options.keyUse.setDown(true);client.gameMode.useItem(client.player,net.minecraft.world.InteractionHand.MAIN_HAND);return;}
    if(name.equals("progression-release")){client.options.keyUse.setDown(false);client.gameMode.releaseUsingItem(client.player);return;}
    if(name.equals("nobu-input-check")){NobuInputChecks.start(client);return;}
    if(name.equals("strip-geometry-check")){try{StripGeometryChecks.run();}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("doom-visual-check")){try{DoomVisualChecks.run(client);}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("stage-doom-growth")){DoomVisualChecks.stage(client,70);return;}
    if(name.equals("stage-doom-large")){DoomVisualChecks.stage(client,145);return;}
    if(name.equals("stage-doom-shockwave")){DoomVisualChecks.stage(client,180);return;}
    if(name.equals("clear-doom-fixture")){DoomVisualChecks.clear(client);return;}
    if(name.equals("stage-nobu-guns")){
     client.setScreen(null);client.options.pauseOnLostFocus=false;
     for(int i=0;i<10;i++){double angle=i*Math.PI*2/10;var gun=new stepsword.mahoutsukai.entity.mahoujin.GunEntity(client.level,client.player);gun.setId(-23000-i);gun.addTag("NativeGunVisualFixture");gun.setPos(client.player.getX()+Math.cos(angle)*5,client.player.getY()+5,client.player.getZ()+Math.sin(angle)*5);gun.setCircleSize(2);gun.setRotationPitch((float)(angle*180/Math.PI));gun.setRotationYaw(65);client.level.addEntity(gun);}
     return;
    }
    if(name.equals("clear-nobu-guns")){for(int i=0;i<10;i++){var gun=client.level.getEntity(-23000-i);if(gun instanceof stepsword.mahoutsukai.entity.mahoujin.GunEntity)gun.discard();}return;}
    if(name.equals("weapon-model-poses-check")){try{WeaponPoseChecks.runModels(client);}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("weapon-hands-live-check")){LiveWeaponHandChecks.start(client);return;}
    if(name.equals("shader-compat-check")||name.equals("shaderpack-compat-check")||name.equals("doom-shaderpack-check")){
     if(!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("iris")||!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("sodium"))throw new AssertionError("Compatibility mods not loaded");
     boolean old=stepsword.mahoutsukai.config.MTConfig.NO_SHADERS;stepsword.mahoutsukai.config.MTConfig.NO_SHADERS=false;
     try{
      if(!stepsword.mahoutsukai.render.RenderUtils.turnOffMahouShaders())throw new AssertionError("Original automatic Iris shader fallback missing");
      var base=stepsword.mahoutsukai.render.MahoujinRenderType.createMahoujinRenderType(stepsword.mahoutsukai.render.RenderUtils.mahoujin,0,false);
      if(stepsword.mahoutsukai.render.ClipWrappedRenderLayer.wrap(true,0,0,0,0,0,0,1,base,new org.joml.Matrix4f())!=base)throw new AssertionError("Treasury shader still replaces Iris pipeline");
     if(!name.equals("doom-shaderpack-check")&&stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active.isEmpty())throw new AssertionError("No actual fog particles in compatibility scene");
      if(stepsword.mahoutsukai.render.FogRenderLayer.boundDraws!=0)throw new AssertionError("Custom depth fog still drawn under Iris");
      boolean pack;
      try{var api=Class.forName("net.irisshaders.iris.api.v0.IrisApi");pack=(boolean)api.getMethod("isShaderPackInUse").invoke(api.getMethod("getInstance").invoke(null));}catch(ReflectiveOperationException e){throw new RuntimeException(e);}
     if((name.equals("shaderpack-compat-check")||name.equals("doom-shaderpack-check"))&&!pack)throw new AssertionError("External shader pack did not activate");
      if(pack&&stepsword.mahoutsukai.fabric.IrisCompatibility.registeredCount()==0)throw new AssertionError("No Mahou pipelines registered with actual Iris API");
     Files.writeString(Path.of(name+".passed"),"PASS: actual Iris+Sodium loaded world; original automatic shader fallback active with NO_SHADERS=false, Treasury retains base pipeline, custom depth fog draws="+stepsword.mahoutsukai.render.FogRenderLayer.boundDraws+"; external shader pack active="+pack+"\n");
     }finally{stepsword.mahoutsukai.config.MTConfig.NO_SHADERS=old;}return;
    }
    if(name.equals("weapon-hands-check")){try{WeaponHandChecks.run(client);}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("weapon-poses-check")){try{WeaponPoseChecks.run(client);}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("stage-nobu-pose")||name.equals("stage-clarent-pose")||name.equals("stage-mystic-hand")){
     client.player.stopUsingItem();boolean mystic=name.equals("stage-mystic-hand");
     client.player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.item.ItemStack(stepsword.mahoutsukai.item.ModItems.REGISTERED.get(mystic?"mystic_code":name.equals("stage-nobu-pose")?"nobu":"clarent")));
     client.options.keyUse.setDown(!mystic);
     if(!mystic)client.player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
     client.options.setCameraType(mystic?net.minecraft.client.CameraType.FIRST_PERSON:net.minecraft.client.CameraType.THIRD_PERSON_FRONT);client.options.pauseOnLostFocus=false;client.setScreen(null);return;
    }
    if(name.equals("ritual-overlays-check")){try{RitualVisualChecks.run(client);}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("projector-image-check")){try{ProjectorImageChecks.run(client);}catch(Exception e){throw new RuntimeException(e);}return;}
    if(name.equals("fog-camera-check")){
     var pos=new net.minecraft.core.BlockPos(2,101,3);var tile=(stepsword.mahoutsukai.tile.FogProjectorTileEntity)client.level.getBlockEntity(pos);if(tile==null)throw new AssertionError("Missing real fog fixture");
     var old=tile.pv;var pv=new stepsword.mahoutsukai.util.FogProjectorValues();pv.camera_fog_toggle=true;pv.camera_fog_radius=20;pv.camera_fog_strength=30;pv.camera_fog_color_r=100;pv.camera_fog_color_g=150;pv.camera_fog_color_b=200;tile.pv=pv;
     try{var fog=new net.minecraft.client.renderer.fog.FogData();fog.environmentalStart=2;fog.environmentalEnd=100;fog.color=new org.joml.Vector4f(.1f,.2f,.3f,1);
      double d=client.player.position().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(pos));double attenuation=1-d/20;double scale=Math.pow(1-30*attenuation/60,4);
      stepsword.mahoutsukai.fabric.ProjectorClient.applyFog(client.player,fog);
      if(Math.abs(fog.environmentalEnd-Math.max(4,100*scale))>.001)throw new AssertionError("Camera fog differs from original single attenuation: "+fog.environmentalEnd);
      float expected=(float)((.1+100/255.0*2*attenuation)/(1+2*attenuation));if(Math.abs(fog.color.x-expected)>.0001)throw new AssertionError("Original doubled colour weight changed");
      Files.writeString(Path.of("fog-camera-check.passed"),"PASS: real loaded projector uses original single density attenuation and doubled colour weight\n");
     }finally{tile.pv=old;}return;
    }
    if(name.equals("fog-gpu-check")){
     if(stepsword.mahoutsukai.render.FogRenderLayer.boundDraws==0||stepsword.mahoutsukai.render.FogRenderLayer.depth()==null)throw new AssertionError("Soft fog pipeline did not bind real GPU depth and uniforms");
     Files.writeString(Path.of("fog-gpu-check.passed"),"PASS: corrected fog shader compiled and bound real GPU draws/depth; see matching captured frame\n");return;
    }
    if(name.equals("fog-no-shaders")){
     for(var particle:java.util.List.copyOf(stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active))particle.remove();
     stepsword.mahoutsukai.config.MTConfig.NO_SHADERS=true;client.setScreen(null);return;
    }
    if(name.equals("fog-use-shaders")){stepsword.mahoutsukai.config.MTConfig.NO_SHADERS=false;client.setScreen(null);return;}
    if(name.equals("parity-window")){client.options.pauseOnLostFocus=false;client.getWindow().setWindowed(1280,720);client.options.guiScale().set(0);client.resizeGui();client.setScreen(null);return;}
    if(name.equals("parity-close")){client.setScreen(null);return;}
    if(name.equals("hud-placement-check")){
     try{
      var xm=stepsword.mahoutsukai.render.gui.MahouGui.class.getDeclaredMethod("hudX");xm.setAccessible(true);
      var ym=stepsword.mahoutsukai.render.gui.MahouGui.class.getDeclaredMethod("hudY");ym.setAccessible(true);
      int oldX=stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X,oldY=stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y;
      try{
       stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X=1_000_000;stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y=1_000_000;
       int x=(int)xm.invoke(null),y=(int)ym.invoke(null);
       if(x>client.getWindow().getGuiScaledWidth()||y>client.getWindow().getGuiScaledHeight())throw new AssertionError("HUD clamp allowed an off-screen origin: "+x+","+y);
       Files.writeString(Path.of("hud-placement-check.passed"),"PASS: absurd placement is clamped to the visible screen ("+x+","+y+" of "+client.getWindow().getGuiScaledWidth()+"x"+client.getWindow().getGuiScaledHeight()+")\n");
      }finally{stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X=oldX;stepsword.mahoutsukai.config.MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y=oldY;}
     }catch(ReflectiveOperationException e){throw new RuntimeException(e);}return;
    }
    if(name.equals("catalog-showrecipe-check")){
     if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.GuideCatalogScreen book))throw new AssertionError("Open the server catalogue first");
     try{
      book.showRecipe("mahoutsukai:guidebook");
      var searchField=book.getClass().getDeclaredField("search");searchField.setAccessible(true);var search=(net.minecraft.client.gui.components.EditBox)searchField.get(book);
      var selectedField=book.getClass().getDeclaredField("selected");selectedField.setAccessible(true);var selected=(com.google.gson.JsonObject)selectedField.get(book);
      if(search==null||!search.getValue().isEmpty())throw new AssertionError("Recipe jump left a technical query in the search box");
      if(selected==null||!selected.get("item").getAsString().equals("mahoutsukai:guidebook"))throw new AssertionError("Recipe jump did not select the compendium row");
      Files.writeString(Path.of("catalog-showrecipe-check.passed"),"PASS: research-to-recipe jump clears the search and selects the real compendium item\n");
     }catch(ReflectiveOperationException e){throw new RuntimeException(e);}return;
    }
    if(name.equals("translations-032-check")){
     for(String key:java.util.List.of("mahoutsukai.mystic_code.tooltip","mahoutsukai.mystic_code.slot_restriction","mahoutsukai.hud.hidden_hint","mahoutsukai.command.showmahou.on","mahoutsukai.command.showmahou.off","mahoutsukai.mental.countdown","mahoutsukai.subtitle.doom.rumble","mahoutsukai.subtitle.doom.impact","mahoutsukai.subtitle.mystic.explosion_big","entity.mahoutsukai.reality_marble_boss","mahoutsukai.marble.boss_reward")){
      if(!net.minecraft.client.resources.language.I18n.exists(key))throw new AssertionError("Missing 0.32 translation: "+key);
      if(net.minecraft.client.resources.language.I18n.get(key).equals(key))throw new AssertionError("Untranslated 0.32 key: "+key);
     }
     Files.writeString(Path.of("translations-032-check.passed"),"PASS: all new 0.32 texts resolve in the active language\n");return;
    }
    if(name.equals("compat-quit")){client.stop();return;}
    if(name.equals("confusion-input-check")){
     var keys=new net.minecraft.client.player.KeyboardInput(client.options);boolean old=client.options.keyUp.isDown();boolean jump=client.options.keyJump.isDown();
     try{client.options.keyUp.setDown(true);client.options.keyJump.setDown(true);keys.tick();float forward=keys.getMoveVector().y;if(forward<=0)throw new AssertionError("Normal forward fixture did not move");
      client.player.addEffect(new net.minecraft.world.effect.MobEffectInstance(stepsword.mahoutsukai.potion.ModEffects.CONFUSION,100));keys.tick();
      if(keys.getMoveVector().y>=0||!keys.keyPresses.backward()||keys.keyPresses.forward()||keys.keyPresses.jump())throw new AssertionError("Original confusion did not reverse native movement and block jumping");
      Files.writeString(Path.of("confusion-input-check.passed"),"PASS: native KeyboardInput reverses movement and disables jump under original Confusion\n");
     }finally{client.options.keyUp.setDown(old);client.options.keyJump.setDown(jump);client.player.removeEffect(stepsword.mahoutsukai.potion.ModEffects.CONFUSION);}return;
    }
    if(name.equals("jei-check")){
     var runtime=stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.runtime;
     if(runtime==null)throw new AssertionError("JEI did not activate Mahou plugin");
     var manager=runtime.getRecipeManager();var types=java.util.List.<mezz.jei.api.recipe.types.IRecipeType<?>>of(stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.MAHOUJIN_RECIPES,stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.SCROLL_RECIPES,stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.WEAPON_RECIPES);
     String counts="";for(var type:types){var category=manager.getRecipeCategory(type);long count=manager.createRecipeLookup(type).get().count();if(category==null||count==0)throw new AssertionError("Missing populated original JEI category: "+type);counts+=type+"="+count+" ";}
     runtime.getRecipesGui().showTypes(types);
     Files.writeString(Path.of("jei-check.passed"),"PASS: actual JEI runtime activated all three original Mahou categories with recipes; "+counts+"\n");return;
    }
    if(name.equals("jei-scroll-open")){stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.runtime.getRecipesGui().showTypes(java.util.List.of(stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.SCROLL_RECIPES));return;}
    if(name.equals("jei-weapon-open")){stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.runtime.getRecipesGui().showTypes(java.util.List.of(stepsword.mahoutsukai.integration.jei.MahouJeiPlugin.WEAPON_RECIPES));return;}
    if(name.equals("compat-settings-check")){
     if(stepsword.mahoutsukai.config.MTConfig.LEY_DISTANCE!=777||stepsword.mahoutsukai.config.MTConfig.PROJECTOR_SIZE_CAP!=13)throw new AssertionError("Nondefault server settings not synchronized");
     Files.writeString(Path.of("compat-settings-check.passed"),"PASS: actual remote server synchronized nondefault ley geometry and projector size limit\n");return;
    }
    if(name.equals("projector-settings-open")){
     client.setScreen(new stepsword.mahoutsukai.render.gui.PlayerSettingsGUI(client.level,client.player.getUUID()));
     if(client.screen.children().isEmpty())throw new AssertionError("Settings category list missing");
     Files.writeString(Path.of(name+".passed"),"PASS: original player settings list initialized in native Screen\n");return;
    }
    if(name.equals("projector-color-open")){
     var screen=new stepsword.mahoutsukai.render.gui.SpellSettingGUI(net.minecraft.network.chat.Component.translatable("mahoutsukai.settings.rho_aias"),client.font,stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.RHO_AIAS).createPrimaryColorSetting().createSecondaryColorSetting();client.setScreen(screen);return;
    }
    if(name.equals("projector-color-check")){
     if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.SpellSettingGUI screen))throw new AssertionError("Color screen missing");
     for(int i=0;i<3;i++){screen.rgb[i].setValue(""+(40+i*10));screen.rgb2[i].setValue(""+(70+i*10));}screen.updateSettings();
     stepsword.mahoutsukai.util.Utils.getSettingsMahou(client.player).setIntSettings(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.RHO_AIAS.ordinal(),java.util.List.of(0,0,0,0,0,0));
     Files.writeString(Path.of(name+".passed"),"PASS: native six-field color screen sent update; local optimistic values replaced to require actual server acknowledgement\n");
    }
    if(name.equals("projector-color-sync-check")){
     var actual=stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.getIntSettings(client.player,stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.RHO_AIAS);
     if(!actual.equals(java.util.List.of(40,50,60,70,80,90)))throw new AssertionError("Server did not acknowledge color edit: "+actual);
     Files.writeString(Path.of(name+".passed"),"PASS: edited spell colors returned from the real server and replaced the deliberately reset client state\n");return;
    }
    if(name.equals("projector-fog-clear")){for(var particle:java.util.List.copyOf(stepsword.mahoutsukai.render.particle.ProjectedFogParticle.active))particle.remove();return;}
    if(name.equals("projector-shader-check")){
     if(stepsword.mahoutsukai.render.FogRenderLayer.boundDraws==0||stepsword.mahoutsukai.render.FogRenderLayer.depth()==null)throw new AssertionError("Fog did not bind native GPU uniforms/depth snapshot");
     if(stepsword.mahoutsukai.render.ClipWrappedRenderLayer.boundDraws==0)throw new AssertionError("Treasury did not bind native clipping uniforms");
     Files.writeString(Path.of(name+".passed"),"PASS: actual native GPU draws: fog="+stepsword.mahoutsukai.render.FogRenderLayer.boundDraws+", clipped Treasury="+stepsword.mahoutsukai.render.ClipWrappedRenderLayer.boundDraws+"; sampled a separate depth texture\n");return;
    }
    if(name.equals("projector-abyss-check")){
     if(!stepsword.mahoutsukai.client.ClientHandler.tint||!client.player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.ABYSS))throw new AssertionError("Abyss render state not applied");
     if(!client.level.getBlockState(new net.minecraft.core.BlockPos(0,100,0)).is(net.minecraft.world.level.block.Blocks.STONE))throw new AssertionError("Abyss mutated real block state");
     Files.writeString(Path.of(name+".passed"),"PASS: actual Abyss effect enabled render substitution while client world retains original stone\n");
    }
    if(name.equals("projector-circle-open")||name.equals("projector-fog-open")){
     var pos=name.equals("projector-circle-open")?new net.minecraft.core.BlockPos(0,101,3):new net.minecraft.core.BlockPos(2,101,3);
     client.gameMode.useItemOn(client.player,net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos),net.minecraft.core.Direction.UP,pos,false));return;
    }
    if(name.equals("projector-circle-check")){
     if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.MahoujinProjectorScreen screen))throw new AssertionError("Native projector use did not open circle screen");
     try{var field=screen.getClass().getDeclaredField("sizevalue");field.setAccessible(true);((net.minecraft.client.gui.components.EditBox)field.get(screen)).setValue("4");screen.updateTE();}catch(ReflectiveOperationException e){throw new RuntimeException(e);}
     Files.writeString(Path.of(name+".passed"),"PASS: native block interaction opened original scrollable circle editor; real update payload sent\n");
    }
    if(name.equals("projector-circle-sync-check")){
     if(!(client.level.getBlockEntity(new net.minecraft.core.BlockPos(0,101,3)) instanceof stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity tile)||tile.pv.csize!=4)throw new AssertionError("Edited circle size did not return from server");
     Files.writeString(Path.of(name+".passed"),"PASS: UI edit returned through actual server packet, block update and client block entity\n");return;
    }
    if(name.equals("projector-fog-check")){
     if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.FogProjectorScreen))throw new AssertionError("Native projector use did not open fog screen");
     Files.writeString(Path.of(name+".passed"),"PASS: native block interaction opened original scrollable fog editor\n");
    }
    if(name.equals("projector-models-check")){
     for(var block:java.util.List.of(stepsword.mahoutsukai.block.ModBlocks.projector.get(),stepsword.mahoutsukai.block.ModBlocks.fogProjector.get())){var state=new net.minecraft.client.renderer.item.ItemStackRenderState();client.getItemModelResolver().updateForTopItem(state,new net.minecraft.world.item.ItemStack(block),net.minecraft.world.item.ItemDisplayContext.GUI,client.level,client.player,0);if(state.isEmpty())throw new AssertionError("Empty projector item model");}
     if(client.getModelManager().getModel(stepsword.mahoutsukai.render.RenderBaseItem.rhongomyniad.key).getAll().isEmpty())throw new AssertionError("Rhongomyniad mesh missing");
     Files.writeString(Path.of(name+".passed"),"PASS: both native projector item models and Rhongomyniad mesh resolved\n");return;
    }
    if(name.matches("items-slot-[0-8]")){client.player.getInventory().setSelectedSlot(name.charAt(name.length()-1)-'0');return;}
    if(name.equals("items-mystic-menu-open")){client.player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerInputPacket(new net.minecraft.world.entity.player.Input(false,false,false,false,false,true,false)));client.player.setShiftKeyDown(true);client.gameMode.useItem(client.player,net.minecraft.world.InteractionHand.MAIN_HAND);return;}
    if(name.equals("items-mystic-wheel-open")){client.setScreen(new stepsword.mahoutsukai.render.gui.WheelGui(client.level,client.player.getMainHandItem()));stepsword.mahoutsukai.Keybinds.changeMysticCode.setDown(true);return;}
    if(name.equals("items-replica-wheel-open")){client.setScreen(new stepsword.mahoutsukai.render.gui.ReplicaGui(client.level,client.player.getMainHandItem()));stepsword.mahoutsukai.Keybinds.changeMysticCode.setDown(true);return;}
    if(name.equals("items-screen-close")){stepsword.mahoutsukai.Keybinds.changeMysticCode.setDown(false);client.options.keyShift.setDown(false);client.player.setShiftKeyDown(false);client.player.connection.send(new net.minecraft.network.protocol.game.ServerboundPlayerInputPacket(net.minecraft.world.entity.player.Input.EMPTY));if(client.screen instanceof net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?>)client.player.closeContainer();client.setScreen(null);return;}
    if(name.equals("items-guide-open")){stepsword.mahoutsukai.item.MahouTsukaiGuidebook.openClient.accept(client.player);if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.guide.PageGui gui) || gui.getGuidebook().getPages().size()<53)throw new AssertionError("Full native guide pages missing");Files.writeString(Path.of(name+".passed"),"PASS: native guide screen with "+gui.getGuidebook().getPages().size()+" original pages\n");return;}
    if(name.equals("items-screen-close")){if(client.screen instanceof net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<?>)client.player.closeContainer();client.setScreen(null);return;}
    if(name.equals("items-models-check")){for(var item:java.util.List.of(stepsword.mahoutsukai.item.ModItems.guidebook.get(),stepsword.mahoutsukai.item.ModItems.mysticCode.get(),stepsword.mahoutsukai.item.ModItems.mysticCodeFirstSorcery.get(),stepsword.mahoutsukai.item.ModItems.emrysStaff.get(),stepsword.mahoutsukai.item.ModItems.replica.get(),stepsword.mahoutsukai.item.ModItems.nobu.get(),stepsword.mahoutsukai.item.ModItems.william.get())){var stack=new net.minecraft.world.item.ItemStack(item);var state=new net.minecraft.client.renderer.item.ItemStackRenderState();client.getItemModelResolver().updateForTopItem(state,stack,net.minecraft.world.item.ItemDisplayContext.GUI,client.level,client.player,0);if(state.isEmpty())throw new AssertionError("Empty native model: "+item);}Files.writeString(Path.of(name+".passed"),"PASS: all seven newly ported item models resolve in the native client\n");return;}
    if(name.equals("items-mystic-menu-check")){if(!(client.screen instanceof stepsword.mahoutsukai.render.gui.MysticCodeInventoryGui))throw new AssertionError("Mystic Code native screen did not open");Files.writeString(Path.of(name+".passed"),"PASS: live server menu opened native three-scroll screen\n");}
    if(name.equals("remaining-use-start")){
     client.options.keyUse.setDown(true);client.gameMode.useItem(client.player,net.minecraft.world.InteractionHand.MAIN_HAND);Files.writeString(Path.of(name+".passed"),"Native client useItem dispatched to real local server\n");return;
    }
    if(name.equals("remaining-use-stop")){client.options.keyUse.setDown(false);client.gameMode.releaseUsingItem(client.player);return;}
    if(name.equals("remaining-slot-next")){client.player.getInventory().setSelectedSlot((client.player.getInventory().getSelectedSlot()+1)%9);return;}
    if(name.equals("remaining-mental-check")){
     if(!(client.getCameraEntity() instanceof stepsword.mahoutsukai.entity.MentalDisplacementEntity)){if(++pendingBlackChecks>200)throw new AssertionError("Native mental camera missing");Files.writeString(request,name);return;}
     pendingBlackChecks=0;var input=new net.minecraft.client.player.KeyboardInput(client.options);client.options.keyUp.setDown(true);input.tick();client.options.keyUp.setDown(false);if(input.keyPresses.forward())throw new AssertionError("Mental body input not suppressed");Files.writeString(Path.of(name+".passed"),"PASS: real server soul spawn/owner sync switched native camera and suppressed body input\n");
    }
    if(name.equals("remaining-possession-check")){
     if(client.getCameraEntity()==client.player || !stepsword.mahoutsukai.util.Utils.getPlayerMahou(client.player).isPossessing()){if(++pendingBlackChecks>200)throw new AssertionError("Native possession camera missing");Files.writeString(request,name);return;}
     pendingBlackChecks=0;Files.writeString(Path.of(name+".passed"),"PASS: real server possession packet switched native camera to tracked target\n");
    }
    if(name.equals("remaining-remote-exit")){((stepsword.mahoutsukai.visualcheck.mixin.MinecraftAttackInvoker)client).mahoutsukai$startAttack();if(client.getCameraEntity()!=client.player)throw new AssertionError("Native remote exit failed");Files.writeString(Path.of(name+".passed"),"PASS: actual Minecraft attack restored native player camera\n");return;}
    if(name.equals("six-insight-check")) {
     var hud=stepsword.mahoutsukai.render.overlay.RenderInsightOverlay.class;
     if(!stepsword.mahoutsukai.render.overlay.RenderInsightOverlay.hasTarget || stepsword.mahoutsukai.render.overlay.RenderInsightOverlay.mainhandStack.isEmpty() || stepsword.mahoutsukai.render.overlay.RenderInsightOverlay.effects.isEmpty()) {if(++pendingBlackChecks>200)throw new AssertionError("Live Insight target packet missing");Files.writeString(request,name);return;}
     pendingBlackChecks=0;Files.writeString(Path.of(name+".passed"),"PASS: live server Insight target, equipment and effects reached original native HUD\n");
    }
    if(name.equals("six-presence-check")) {
     if(!client.player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.RIPPER_INVISIBILITY)) {if(++pendingBlackChecks>200)throw new AssertionError("Live concealment flag missing");Files.writeString(request,name);return;}
     var renderer=(net.minecraft.client.renderer.entity.LivingEntityRenderer)client.getEntityRenderDispatcher().getRenderer(client.player);var state=(net.minecraft.client.renderer.entity.state.LivingEntityRenderState)renderer.createRenderState();renderer.extractRenderState(client.player,state,0);
     if(!((stepsword.mahoutsukai.fabric.PresenceRenderState)(Object)state).mahoutsukai$isConcealed())throw new AssertionError("Native concealed player render flag missing");
     pendingBlackChecks=0;Files.writeString(Path.of(name+".passed"),"PASS: live concealment packet and native player renderer state\n");
    }
    if(name.equals("six-knife-input-check")) {
     var target=client.level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,client.player.getBoundingBox().inflate(5),e->e.getName().getString().equals("KnifeVisual")).stream().findFirst().orElseThrow();var previous=client.hitResult;
     try{client.hitResult=new net.minecraft.world.phys.EntityHitResult(target);((stepsword.mahoutsukai.visualcheck.mixin.MinecraftAttackInvoker)client).mahoutsukai$startAttack();Files.writeString(Path.of(name+".passed"),"PASS: native Minecraft.startAttack dispatched actual paired knife attack; verify server health\n");}finally{client.hitResult=previous;}
    }
    if(name.equals("six-gandr-start") || name.equals("six-knife-use")) {client.options.keyUse.setDown(true);client.gameMode.useItem(client.player,net.minecraft.world.InteractionHand.MAIN_HAND);Files.writeString(Path.of(name+".passed"),"PASS: native client item-use dispatched for "+client.player.getMainHandItem().getItem()+"\n");}
    if(name.equals("six-gandr-charge-check")) {
     var entities=client.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class,client.player.getBoundingBox().inflate(20));
     if(entities.stream().noneMatch(e->e.inited && e.getLife()>=50 && !e.getFired())){if(++pendingBlackChecks>200)throw new AssertionError("Native charged Gandr tracking missing");Files.writeString(request,name);return;}
     pendingBlackChecks=0;Files.writeString(Path.of(name+".passed"),"PASS: actual client item use, server charge entity, synchronized life/colors and client swirl initialization\n");
    }
    if(name.equals("six-gandr-release")) {client.options.keyUse.setDown(false);client.gameMode.releaseUsingItem(client.player);Files.writeString(Path.of(name+".passed"),"PASS: native client item release fires Gandr; verify server consumption\n");}
    if(name.equals("six-prediction-check")) {
     if(stepsword.mahoutsukai.potion.ClairvoyanceEyesPotion.predictionMap.isEmpty()){if(++pendingBlackChecks>300)throw new AssertionError("Live native navigation packet missing");Files.writeString(request,name);return;}
     pendingBlackChecks=0;predictionGeometry=true;Files.writeString(Path.of(name+".passed"),"PASS: live native navigation paths received for original ghost rendering\n");
    }
    if(name.equals("power-lake-client-check") || name.equals("power-lake-submerged-check")) {
     boolean submerged=name.equals("power-lake-submerged-check");
     var entity=client.getCameraEntity();
     float distance=stepsword.mahoutsukai.render.overlay.RenderPowerConsolidation.distanceToMurkyWater(entity);
     if(distance<0 || stepsword.mahoutsukai.util.Utils.isInMurkyWater(entity)!=submerged) {
      if(++pendingBlackChecks>200)throw new AssertionError("Lake chunk synchronization/position did not arrive: pos="+entity.position()+", distance="+distance+", submerged="+stepsword.mahoutsukai.util.Utils.isInMurkyWater(entity));
      Files.writeString(request,name);return;
     }
     pendingBlackChecks=0;
     try(var fogRenderer=new net.minecraft.client.renderer.fog.FogRenderer()) {
      double radius=stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_FOG_RADIUS;
      net.minecraft.client.renderer.fog.FogData baseline;
      try {stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_FOG_RADIUS=0;baseline=fogRenderer.setupFog(client.gameRenderer.getMainCamera(),12,client.getDeltaTracker(),0,client.level);}
      finally {stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_FOG_RADIUS=radius;}
      var fog=fogRenderer.setupFog(client.gameRenderer.getMainCamera(),12,client.getDeltaTracker(),0,client.level);
      if(!fog.color.isFinite() || !Float.isFinite(fog.environmentalEnd))throw new AssertionError("Native lake fog must be finite");
      if(submerged && (fog.environmentalStart!=-8 || fog.environmentalEnd!=5 || Math.abs(fog.color.y-60/255f)>0.00001))throw new AssertionError("Submerged original fog color and distances required");
      if(!submerged) {
       double blend=2*(radius-distance)/radius,scale=Math.pow(1-(50-50/radius*distance)/60,4);
       double expectedRed=(baseline.color.x+180/255.0*blend)/(1+blend);
       double boundedEnd=Math.min(baseline.environmentalEnd,6+distance*2);
       double expectedEnd=net.minecraft.util.Mth.clamp((float)(boundedEnd*scale),4,(float)boundedEnd);
       if(Math.abs(fog.color.x-expectedRed)>0.0001 || Math.abs(fog.environmentalEnd-expectedEnd)>0.001)throw new AssertionError("Native original mist formula mismatch: "+fog.environmentalEnd+"/"+expectedEnd+", "+fog.color+"/"+expectedRed);
      }
      Files.writeString(Path.of(name+".passed"),"PASS: native FogRenderer hook, live synchronized lake chunks, submerged="+submerged+", distance="+distance+", fog="+fog.environmentalStart+","+fog.environmentalEnd+", color="+fog.color+"\n");
     }
    }
    if(name.equals("black-fire-matrix-check")) {
     var entity=(net.minecraft.world.entity.LivingEntity)client.getCameraEntity();int oldTime=entity.hurtTime,oldDuration=entity.hurtDuration;
     try {
      entity.hurtTime=0;entity.hurtDuration=0;var pose=new com.mojang.blaze3d.vertex.PoseStack();
      stepsword.mahoutsukai.render.overlay.RenderBlackBurning.hurtCameraEffect(pose,0);
      if(!pose.last().pose().isFinite())throw new AssertionError("Undamaged burning player must not create NaN hand transform");
      Files.writeString(Path.of(name+".passed"),"PASS: zero hurt duration and exact-zero partial tick produce finite native hand transform\n");
     } finally {entity.hurtTime=oldTime;entity.hurtDuration=oldDuration;}
    }
    if(name.equals("black-fire-client-on-check") || name.equals("black-fire-client-off-check")) {
     var target=client.level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,client.player.getBoundingBox().inflate(40),entity->entity.getName().getString().equals("BlackBurnVisual")).stream().findFirst().orElse(null);
     boolean expected=name.equals("black-fire-client-on-check");
     if(target==null || target.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_BURNING)!=expected) {
      if(++pendingBlackChecks>200) throw new AssertionError("Native tracking fixture did not reach expected effect state");
      Files.writeString(request,name);return;
     }
     pendingBlackChecks=0;
     if(target.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BLACK_BURNING)!=expected) throw new AssertionError("Tracking packet must synchronize original black burning flag");
     var renderer=(net.minecraft.client.renderer.entity.LivingEntityRenderer)client.getEntityRenderDispatcher().getRenderer(target);
     var state=(net.minecraft.client.renderer.entity.state.LivingEntityRenderState)renderer.createRenderState();renderer.extractRenderState(target,state,0);
     if(((stepsword.mahoutsukai.fabric.BlackBurningRenderState)(Object)state).mahoutsukai$isBlackBurning()!=expected) throw new AssertionError("Native entity render-state flag must follow effect");
     Files.writeString(Path.of(name+".passed"),"PASS: live tracking packet and native entity render-state black-burning flag="+expected+"\n");
    }
    if(name.equals("proximity-input-check")) {
     if(!(client.player.getMainHandItem().getItem() instanceof stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys)) throw new AssertionError("Actual keys required in main hand");
     var previous=client.hitResult;
     try {
      client.hitResult=net.minecraft.world.phys.BlockHitResult.miss(client.player.getEyePosition().add(client.player.getLookAngle().scale(100)),net.minecraft.core.Direction.UP,client.player.blockPosition());
      Files.writeString(Path.of("proximity-input-before.txt"),client.player.position().toString());
      ((stepsword.mahoutsukai.visualcheck.mixin.MinecraftAttackInvoker)client).mahoutsukai$startAttack();
      Files.writeString(Path.of("proximity-input-check.passed"),"PASS: native Minecraft.startAttack dispatched original proximity selection payload; verify damage/teleport on server\n");
     } finally {client.hitResult=previous;}
    }
    if(name.equals("selective-input-check")) {
     if(!client.player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.SELECTIVE_DISPLACEMENT)) throw new AssertionError("Server must grant original selective ability");
     boolean old=stepsword.mahoutsukai.Keybinds.selectiveDisplacement.isDown();
     try {
      stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.currentSelected=null;
      stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.secondSelected=null;
      stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.selectionKeyPress=0;
      stepsword.mahoutsukai.Keybinds.selectiveDisplacement.setDown(true);
      stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.selectedEntity(client.player);
      var selected=stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.currentSelected;
      if(selected==null) throw new AssertionError("Original client selector must find aimed entity");
      Files.writeString(Path.of("selective-input-target.txt"),selected.position().toString());
      stepsword.mahoutsukai.Keybinds.selectiveDisplacement.setDown(false);
      stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.releaseSelection();
      Files.writeString(Path.of("selective-input-check.passed"),"PASS: original client selection and key release sent native selective payload\n");
     } finally {stepsword.mahoutsukai.Keybinds.selectiveDisplacement.setDown(old);}
    }
    if(name.equals("clarent-blocking-check")) {
     client.player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
     Files.writeString(request,"clarent-blocking-frame");return;
    }
    if(name.equals("bound-input-check")) {
     var keys=new net.minecraft.client.player.KeyboardInput(client.options);
     boolean old=client.options.keyUp.isDown();
     try {
      client.options.keyUp.setDown(true);keys.tick();
      if(keys.getMoveVector().lengthSquared()==0) throw new AssertionError("Unbound movement fixture must move");
      client.player.addEffect(new net.minecraft.world.effect.MobEffectInstance(stepsword.mahoutsukai.potion.ModEffects.BOUND,100));keys.tick();
      if(keys.getMoveVector().lengthSquared()!=0 || keys.keyPresses.forward()) throw new AssertionError("Bound native keyboard input must stop movement");
      Files.writeString(Path.of("bound-input-check.passed"),"PASS: native KeyboardInput mixin blocks movement and forward input while bound\n");
     } finally { client.options.keyUp.setDown(old);client.player.removeEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND); }
    }
    net.minecraft.client.Screenshot.grab(client.gameDirectory, name + ".png", client.getMainRenderTarget(), 1,
     message -> org.slf4j.LoggerFactory.getLogger("mahoutsukai-visualcheck").info("CAPTURE: {}",message.getString()));
   } catch (java.io.IOException | ReflectiveOperationException exception) { throw new RuntimeException(exception); }
  });
 }
}
