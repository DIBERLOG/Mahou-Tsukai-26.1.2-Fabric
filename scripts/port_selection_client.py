from pathlib import Path
import re,json
root=Path.cwd();client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
p=client/'render/overlay/RenderSelectedEntity.java';p.parent.mkdir(parents=True,exist_ok=True);s=(orig/'render/overlay/RenderSelectedEntity.java').read_text();remove=['net.neoforged','containers.MysticCode','item.mysticcode','item.spells.familiar','item.spells.projection','secret.ripper']
s='\n'.join(line for line in s.splitlines() if not (line.startswith('import ') and any(term in line for term in remove)))+'\n'
a=s.index('   public static HashSet<Utils.MAHOU_SELECTION> shouldRenderSelected(Player player)');b=s.index('   public static int getDistance',a)
s=s[:a]+'''   public static HashSet<Utils.MAHOU_SELECTION> shouldRenderSelected(Player player) {
      var set=shouldRenderSelected(player,player.getMainHandItem().getItem());set.addAll(shouldRenderSelected(player,player.getOffhandItem().getItem()));return set;
   }
   public static HashSet<Utils.MAHOU_SELECTION> shouldRenderSelected(Player player,Item item) {
      var set=new HashSet<Utils.MAHOU_SELECTION>();
      if(EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT) && !EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN)) set.add(Utils.MAHOU_SELECTION.SELECTIVE_DISPLACEMENT);
      if(item instanceof RetributionSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_RETRIBUTION);
      if(item instanceof ImmunityExchangeSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_IMMUNITY_EXCHANGE);
      if(item instanceof DamageReplicationSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_DAMAGE_REPLICATION);
      if(item instanceof GeasSpellScroll) set.add(Utils.MAHOU_SELECTION.SCROLL_GEAS);
      return set;
   }

'''+s[b:]
s=s.replace('RenderLevelStageEvent event','net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext event').replace('event.getPoseStack()','event.poseStack()').replace('BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();','net.minecraft.client.renderer.SubmitNodeCollector buffer = event.submitNodeCollector();').replace('BufferSource buffer','net.minecraft.client.renderer.SubmitNodeCollector buffer');s=s.replace('player.yRot - player.yRotO','player.getYRot() - player.yRotO').replace('player.xRot - player.xRotO','player.getXRot() - player.xRotO')
s=re.sub(r'MahoujinRenderType.wrapCullLayer\(\s*(MahoujinRenderType.createScopedRunesNoDepthTriangleStripRenderType\([^\n]+\))\s*\)',r'\1',s)
s=re.sub(r'\s*RenderUtils.conditionalBatchEnder\(type, buffer\);','',s)
s=s.replace('      Matrix4f positionMatrix = matrix.last().pose();\n      VertexConsumer bb = buffer.getBuffer(type);','      buffer.submitCustomGeometry(matrix,type,(pose,bb)-> {\n      Matrix4f positionMatrix = pose.pose();').replace('      buffer.endBatch(type);','      });')
s=s.replace('   public static RenderType renderArc(','''   public static void releaseSelection() {
      var client=Minecraft.getInstance();var player=client.player;
      if(player==null) {currentSelected=null;secondSelected=null;selectionKeyPress=0;return;}
      if(!Keybinds.selectiveDisplacement.isDown() && selectionKeyPress>0L && EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT)
          && !EffectUtil.hasBuff(player,ModEffects.SELECTIVE_DISPLACEMENT_COOLDOWN) && currentSelected!=null) {
         int first=currentSelected.getId(),second=secondSelected!=null ? secondSelected.getId() : first;
         if(net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.TYPE))
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.SelectiveDisplacementPacket(first,second));
         selectionKeyPress=0;currentSelected=null;secondSelected=null;
      }
   }
   public static RenderType renderArc(''');p.write_text(s)
p=client/'render/RenderUtils.java';s=p.read_text().replace('    public static final Identifier runes','    public static final Identifier beam=Identifier.fromNamespaceAndPath("mahoutsukai","textures/block/white.png");\n    public static final Identifier runes');p.write_text(s)
p=client/'Keybinds.java';s=p.read_text().replace(' public static KeyMapping drawMahoujin;',' public static KeyMapping drawMahoujin;\n public static KeyMapping selectiveDisplacement;').replace('  drawMahoujin =','  selectiveDisplacement=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.selectiveDisplacement",72,category));\n  drawMahoujin =');p.write_text(s)
p=client/'fabric/MahouFabricClient.java';s=p.read_text().replace('        stepsword.mahoutsukai.Keybinds.init();','''        stepsword.mahoutsukai.Keybinds.init();
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context-> {
            var client=net.minecraft.client.Minecraft.getInstance();
            if(client.player==null || client.screen!=null) return;
            stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.selectedEntity(client.getCameraEntity());
            stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.renderSelectedEntity(context);
        });
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client->stepsword.mahoutsukai.render.overlay.RenderSelectedEntity.releaseSelection());''');p.write_text(s)
# Native icon visibility for the original NoShowEffect interface, without NeoForge client extensions.
(src/'fabric/mixin/EffectIconMixin.java').write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
@Mixin(MobEffectInstance.class)
public abstract class EffectIconMixin {
 @ModifyReturnValue(method="showIcon",at=@At("RETURN"))
 private boolean mahoutsukai$icon(boolean value) {
  var effect=((MobEffectInstance)(Object)this).getEffect().value();
  return value && (!(effect instanceof stepsword.mahoutsukai.potion.NoShowEffect hidden) || hidden.shouldShow());
 }
}
''');p=root/'MahouTsukai-Fabric/src/main/resources/mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['mixins'].append('EffectIconMixin');p.write_text(json.dumps(j,indent=2)+'\n')
# Synchronize the server selection radius; hold duration and visibility remain client preferences.
p=src/'fabric/EconomyConfigPacket.java';s=p.read_text().replace('int clarentDurability) implements','int clarentDurability, int selectiveRange) implements').replace('ByteBufCodecs.INT, EconomyConfigPacket::clarentDurability, EconomyConfigPacket::new','ByteBufCodecs.INT, EconomyConfigPacket::clarentDurability, ByteBufCodecs.INT, EconomyConfigPacket::selectiveRange, EconomyConfigPacket::new').replace('40,1500);','40,1500,40);').replace('ruleBreakerDurability,1500);','ruleBreakerDurability,1500,40);');s=s.replace('    public EconomyConfigPacket {','    public EconomyConfigPacket(int diamond,int emerald,int circuit,int magitech,int ruleBreakerDurability,int clarentDurability) {this(diamond,emerald,circuit,magitech,ruleBreakerDurability,clarentDurability,40);}\n    public EconomyConfigPacket {\n        if(selectiveRange<0 || selectiveRange>100000000) throw new IllegalArgumentException("Selection radius outside original range");');s=s.replace('MTConfig.RULE_BREAKER_DURABILITY, MTConfig.CLARENT_DURABILITY);','MTConfig.RULE_BREAKER_DURABILITY, MTConfig.CLARENT_DURABILITY,MTConfig.SELECTIVE_DISPLACEMENT_RANGE);').replace('        MTConfig.CLARENT_DURABILITY = clarentDurability;','        MTConfig.CLARENT_DURABILITY = clarentDurability;\n        MTConfig.SELECTIVE_DISPLACEMENT_RANGE=selectiveRange;');p.write_text(s)
p=root/'MahouTsukai-Fabric/src/selfCheck/java/stepsword/mahoutsukai/EconomyConfigCheck.java';s=p.read_text().replace('123, 2000)','123, 2000, 70)').replace('wire.readableBytes() == 24, "Four capacities and two weapon durabilities"','wire.readableBytes() == 28, "Four capacities, two weapon durabilities and selection radius"');p.write_text(s)
p=root/'scripts/check_resources.py';s=p.read_text();s=s.replace("    if value['type'] == 'minecraft:composite':","    if value['type'] == 'minecraft:condition':\n        check_item_model(value['on_false']); check_item_model(value['on_true'])\n    elif value['type'] == 'minecraft:composite':");s=s.replace("'rule_breaker', 'mahoujin'}","'rule_breaker', 'mahoujin', 'clarent', 'clarent_blocking'}");p.write_text(s)
print('Original H-key selection and arc geometry adapted to native Fabric submit events')
