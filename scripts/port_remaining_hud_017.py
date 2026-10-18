from pathlib import Path
import re
root=Path(__file__).resolve().parents[1];o=root/'recovered/stepsword/mahoutsukai';c=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai';m=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
def put(base,name,s):
 p=base/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def edit(base,name,a,b):
 p=base/name;s=p.read_text();assert a in s,(name,a);put(base,name,s.replace(a,b))
for name in ['render/gui/MahouGui.java','render/overlay/RenderBoundOverlay.java','render/overlay/RenderRevertedOverlay.java']:
 s=(o/name).read_text().replace('import net.neoforged.neoforge.client.gui.GuiLayer;','').replace('implements GuiLayer','')
 s=s.replace('Utils.isArmor(player.getMainHandItem().getItem())','Utils.isArmor(player.getMainHandItem())')
 put(c,name,s)
s=(o/'render/gui/MahoujinHUD.java').read_text();s=re.sub(r'import net\.neoforged[^;]+;\n','',s)
a=s.index('                     ResourceHandler<ItemResource> inv =');b=s.index('                  if (stack.getItem() ==',a)
s=s[:a]+'''                     for(ItemStack st2:Utils.snapshotNestedItems(stack)){
                        if(st2.getItem() instanceof PowderedCatalyst catalyst && hs.contains(catalyst.getCatalystName()))counts.merge(catalyst.getCatalystName(),st2.getCount(),Integer::sum);
                        if(st2.is(ModBlocks.spellClothBlock.get().asItem()))hasClothInInventory=true;
                     }
                  }

'''+s[b:]
s=s.replace('else if (Utils.getInventory(stack) != null)', 'else if (!Utils.snapshotNestedItems(stack).isEmpty())')
put(c,'render/gui/MahoujinHUD.java',s)
edit(c,'render/RenderUtils.java','public final class RenderUtils {','''public final class RenderUtils {
 public static float calculateOverlayFade(int duration,int interval,int fadeTicks,float maxAlpha){int phase=duration%interval;return phase>interval-fadeTicks?(phase-(interval-fadeTicks))/(float)fadeTicks*maxAlpha:0;}
 public static void renderTextureOverlay(net.minecraft.client.gui.GuiGraphicsExtractor graphics,String texture,int r,int g,int b,float alpha,float x,float y,float width,float height,float z){
  int color=net.minecraft.util.ARGB.colorFromFloat(Math.clamp(alpha,0,1),r/255f,g/255f,b/255f);int w=Math.round(width),h=Math.round(height);
  graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,Identifier.parse(texture),Math.round(x),Math.round(y),0,0,w,h,w,h,w,h,color);
 }
''')
edit(m,'util/EffectUtil.java','public final class EffectUtil {','''public final class EffectUtil {
 public static int getEffectDuration(net.minecraft.world.entity.LivingEntity entity,net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect){var instance=entity.getEffect(effect);return instance==null?0:instance.getDuration();}
''')
edit(m,'config/MTConfig.java','public final class MTConfig {','''public final class MTConfig {
 public static int CLIENT_GUI_MAHOU_PLACEMENT_X=10;
 public static int CLIENT_GUI_MAHOU_PLACEMENT_Y=20;
''')
edit(m,'config/MTConfig.java','ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n        java.util.Map.entry("CLIENT_GUI_MAHOU_PLACEMENT_X",new double[]{0,100000000}),\n        java.util.Map.entry("CLIENT_GUI_MAHOU_PLACEMENT_Y",new double[]{0,100000000}),')
edit(c,'fabric/SixFamilyClient.java','RenderInsightOverlay.insightOverlay(graphics);','new stepsword.mahoutsukai.render.gui.MahouGui().render(graphics,delta);')
print('Connected original mana/soul/exchange/circuit/projector HUD, ritual recipe hint and Bound/Reverted overlays')
