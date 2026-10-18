from pathlib import Path
import re
root=Path(__file__).resolve().parents[1];o=root/'recovered/stepsword/mahoutsukai';c=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai';m=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
def put(base,name,s):
 p=base/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def edit(base,name,a,b):
 s=(base/name).read_text(encoding='utf-8');assert a in s,(name,a);put(base,name,s.replace(a,b))
for cls in ['PlayerSettingsGUI','SpellSettingGUI','ButtonList','RenderStrengthening','RenderPowerConsolidationInnateCap','RenderRuleBreakerModulus']:
 s=(o/f'render/gui/{cls}.java').read_text(encoding='utf-8').replace('import stepsword.mahoutsukai.MahouTsukaiMod;','')
 s=s.replace('PacketHandler.sendToServer(', 'net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(')
 s=s.replace('MahouTsukaiMod.proxy.openSettingsGUI(Minecraft.getInstance().level, Minecraft.getInstance().player.getUUID());','Minecraft.getInstance().setScreen(new PlayerSettingsGUI(Minecraft.getInstance().level,Minecraft.getInstance().player.getUUID()));')
 put(c,f'render/gui/{cls}.java',s)
s=(o/'networking/SettingsUpdatePacket.java').read_text(encoding='utf-8');s=re.sub(r'import net\.neoforged[^;]+;\n','',s)
s=s.replace('      this.valuesInt = new ArrayList<>();','      if(sz<0||sz>6)throw new IllegalArgumentException("Invalid settings list length");\n      this.valuesInt = new ArrayList<>();')
a=s.index('   public static void handle(');b=s.index('   public Type<SettingsUpdatePacket> type()',a)
s=s[:a]+'''   public static final Type<SettingsUpdatePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","settings_update"));
   public static void handle(SettingsUpdatePacket message,net.minecraft.server.level.ServerPlayer player){
      var spells=stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.values();
      if(message.spell<0||message.spell>=spells.length||message.valuesInt==null)return;
      var spell=spells[message.spell];int count=(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.hasPrimary(spell)?3:0)+(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.hasSecondary(spell)?3:0);
      if(message.valuesInt.size()!=count||message.valuesInt.stream().anyMatch(v->v==null||v<0||v>255))return;
      var values=new java.util.ArrayList<>(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.getIntDefaults().get(message.spell));for(int i=0;i<count;i++)values.set(i,message.valuesInt.get(i));
      Utils.getSettingsMahou(player).setIntSettings(message.spell,values);
      stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.updateClientMahou(player);
   }

'''+s[b:];s=s.replace('MahouPackets.SETTINGS_UPDATE_TYPE','TYPE');put(m,'networking/SettingsUpdatePacket.java',s)
edit(m,'fabric/MahouFabric.java','    public void onInitialize() {','''    public void onInitialize() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SettingsUpdatePacket.TYPE,stepsword.mahoutsukai.networking.SettingsUpdatePacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SettingsUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.SettingsUpdatePacket.handle(packet,context.player()));''')
edit(c,'Keybinds.java',' public static KeyMapping gunUp,gunDown,fireNobu;',' public static KeyMapping settingsGUI;\n public static KeyMapping gunUp,gunDown,fireNobu;')
edit(c,'Keybinds.java','  fireNobu=','  settingsGUI=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.settingsGUI",46,category));\n  fireNobu=')
edit(c,'fabric/ProjectorClient.java',' public static void init(){',''' public static void init(){
  net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack,context,type,lines)->{stepsword.mahoutsukai.render.gui.RenderStrengthening.strengthenTooltip(stack,lines);stepsword.mahoutsukai.render.gui.RenderPowerConsolidationInnateCap.innateCapTooltip(stack,lines);stepsword.mahoutsukai.render.gui.RenderRuleBreakerModulus.modulusTooltip(stack,lines);});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.player!=null&&mc.screen==null&&stepsword.mahoutsukai.Keybinds.settingsGUI.consumeClick())mc.setScreen(new stepsword.mahoutsukai.render.gui.PlayerSettingsGUI(mc.level,mc.player.getUUID()));});
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{var mc=Minecraft.getInstance();if(mc.getCameraEntity()!=null)stepsword.mahoutsukai.render.overlay.RenderLeyLines.leyRender(mc.getCameraEntity(),context.poseStack(),mc.getDeltaTracker().getGameTimeDeltaPartialTick(false),context.submitNodeCollector());});''')
s=(o/'render/overlay/RenderLeyLines.java').read_text(encoding='utf-8')
s=s.replace('float partialTicks) {','float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buffer) {')
s=s.replace('doRender(matrix, i, MTConfig.LEY_RENDER_HEIGHT, j, partialTicks, h, v, ne, nw);','doRender(matrix, i, MTConfig.LEY_RENDER_HEIGHT, j, partialTicks, h, v, ne, nw,buffer);')
s=s.replace('boolean rnw) {','boolean rnw,net.minecraft.client.renderer.SubmitNodeCollector buffer) {')
s=s.replace('runes(matrix, Minecraft.getInstance().renderBuffers().bufferSource(),','runes(matrix, buffer,')
s=s.replace('      BufferSource buffer,','      net.minecraft.client.renderer.SubmitNodeCollector buffer,')
s=s.replace('      Matrix4f m = matrix.last().pose();','')
s=re.sub(r'MahoujinRenderType.wrapCullLayer\((MahoujinRenderType.create[^\n]+)\)',r'\1',s)
s=s.replace('         VertexConsumer bb = buffer.getBuffer(type);','         buffer.submitCustomGeometry(matrix,type,(pose,bb)->{Matrix4f m=pose.pose();')
s=s.replace('         buffer.endBatch(type);','         });').replace('         buffer.endBatch(ringType);','')
put(c,'render/overlay/RenderLeyLines.java',s)
edit(m,'config/MTConfig.java','public final class MTConfig {','''public final class MTConfig {
 public static int LEY_RENDER_DISTANCE=100;
 public static int LEY_RENDER_HEIGHT=70;
 public static boolean LEY_RUNES=true;
''')
edit(m,'config/MTConfig.java','ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n        java.util.Map.entry("LEY_RENDER_DISTANCE",new double[]{0,100000000}),\n        java.util.Map.entry("LEY_RENDER_HEIGHT",new double[]{-100000000,100000000}),')
print('Connected original color settings screens/key/network validation, remaining tooltips and Fay Sight ley-line visuals')
