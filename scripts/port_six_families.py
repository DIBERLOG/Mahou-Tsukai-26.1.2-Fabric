"""Historical one-shot: original Insight, Clairvoyance and Cup foundation. Do not rerun over native fixes."""
from pathlib import Path
import re,json,shutil
root=Path.cwd();base=root/'MahouTsukai-Fabric/src';orig=root/'recovered/stepsword/mahoutsukai';main=base/'main/java/stepsword/mahoutsukai';client=base/'client/java/stepsword/mahoutsukai';res=base/'main/resources'
def put(rel,s,cl=False):
 p=(client if cl else main)/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8');return p
def copy(rel,cl=False):return put(rel,(orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'),cl)
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');
 if b in s:return
 assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
families=[('eyes','InsightEyes','insight','insightEyesMahoujin','insightEyes','insightEyesScroll'),('eyes','ClairvoyanceEyes','prediction','predictionMahoujin','clairvoyanceEyes','clairvoyanceEyesScroll'),('mystic','CupOfHeaven','cup_of_heaven','cupOfHeavenMahoujin','cupOfHeaven','cupOfHeavenScroll')]
for category,cls,id,block,tile,item in families:
 for rel in [f'block/spells/{category}/{cls}Mahoujin.java',f'tile/{category}/{cls}MahoujinTileEntity.java',f'item/spells/{category}/{cls}SpellScroll.java']:copy(rel)
 edit(main/'item/ModItems.java','    static {',f'    public static final Supplier<Item> {item}=register("scroll_{id}",stepsword.mahoutsukai.item.spells.{category}.{cls}SpellScroll::new);\n    static {{')
 edit(main/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {block}=plain("mahoujin_{id}",new stepsword.mahoutsukai.block.spells.{category}.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(main/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.{category}.{cls}MahoujinTileEntity>> {tile}=register("mahoujin_{id}",stepsword.mahoutsukai.tile.{category}.{cls}MahoujinTileEntity::new,ModBlocks.{block}.get());\n    public static void init()')
 text=(orig/'block/spells/MahoujinRecipeRegistrar.java').read_text(encoding='utf-8');recipe=re.search(r'recipes.put\((\w+), \(BlockBase\)ModBlocks\.'+block,text)[1];cats=re.search(r'UnorderedList '+recipe+r' = makeRecipe\(([^)]+)\)',text)[1];cloth=re.search(r'needsCloth.put\('+recipe+r', (true|false)\)',text)
 edit(main/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {recipe}=makeRecipe({cats});recipes.put({recipe},(BlockBase)ModBlocks.{block}.get());needsCloth.put({recipe},{cloth[1] if cloth else "false"});')
 edit(client/'fabric/MahouFabricClient.java','    public void onInitializeClient() {',f'    public void onInitializeClient() {{\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{tile}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 for f in [f'assets/mahoutsukai/items/scroll_{id}.json',f'assets/mahoutsukai/models/item/scroll_{id}.json',f'assets/mahoutsukai/blockstates/mahoujin_{id}.json',f'assets/mahoutsukai/textures/item/scroll_{id}.png']:
  source=root/'recovered'/f
  if source.exists():target=res/f;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
for rel in ['util/NodeNetworkUtil.java','util/JobType.java','potion/ClairvoyanceEyesPotion.java','potion/InsightEyesPotion.java']:copy(rel)
# Keep the original traversal and per-owner batching, fixing the original requeue/removal races.
put('util/MahouJobs.java','''package stepsword.mahoutsukai.util;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
public final class MahouJobs {
 public static final Map<UUID,JobType> running=new LinkedHashMap<>();
 public static void addJob(UUID owner,JobType job){running.putIfAbsent(owner,job);}
 public static void runJobs(){var pending=new LinkedHashMap<>(running);running.clear();for(var job:pending.values())job.run();}
 public static void clear(){running.clear();}
}
''')
# A continuation must not enqueue its already-explored starting node again; original condition already enforces this.
# Native packet transport, only to connections that negotiated these payloads.
put('networking/PacketHandler.java','''package stepsword.mahoutsukai.networking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
public final class PacketHandler {
 public static void sendTo(ServerPlayer player,CustomPacketPayload packet){if(player.connection!=null && ServerPlayNetworking.canSend(player,packet.type()))ServerPlayNetworking.send(player,packet);}
 public static void sendTracking(Entity entity,CustomPacketPayload packet){for(var player:PlayerLookup.tracking(entity))sendTo(player,packet);}
}
''')
for rel,id in [('networking/InsightPacket.java','insight'),('networking/EntityPathPacket.java','entity_path')]:
 p=copy(rel);s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'   public static void handle\([^\n]+\n.*?\n   }\n','',s,flags=re.S)
 s=re.sub(r'return MahouPackets\.\w+;', 'return TYPE;',s)
 s=s.replace(' implements CustomPacketPayload {',f' implements CustomPacketPayload {{\n   public static final Type<{p.stem}> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","{id}"));')
 s=re.sub(r'(?m)^   (CompoundTag|ArrayList<Integer>|ArrayList<Path>|int|boolean) (\w+);',r'   public \1 \2;',s);p.write_text(s,encoding='utf-8')
p=main/'potion/InsightEyesPotion.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=s.replace('Utils.getPredicate(Utils.MAHOU_SELECTION.INSIGHT, a, player)','Utils.getPredicate(new java.util.HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.INSIGHT)), a, player)').replace('Utils.nearAllowed(Utils.MAHOU_SELECTION.INSIGHT)','false')
s=s.replace('ResourceHandler<ItemResource> inventory = Utils.getInventory((LivingEntity)e);','java.util.List<ItemStack> inventory = entityInventory((LivingEntity)e);').replace('ResourceHandler<ItemResource> inventory = Utils.getInventory(player.level(), pos);','java.util.List<ItemStack> inventory = blockInventory(player.level(),pos);').replace('ResourceHandler<ItemResource> inventory','java.util.List<ItemStack> inventory').replace('!((ItemResource)inventory.getResource(z)).isEmpty()','!inventory.get(z).isEmpty()').replace('((ItemResource)inventory.getResource(z)).test(s::equals)','ItemStack.matches(inventory.get(z),s)')
s=s.replace('ItemResource resource = (ItemResource)inventory.getResource(i);','ItemStack resource = inventory.get(i);').replace('ItemStack stack = new ItemStack(resource.typeHolder(), inventory.getAmountAsInt(i), resource.getComponentsPatch());','ItemStack stack = resource.copy();')
# Return the payload from the same targeting path so integration checks exercise live behavior without a client.
s=s.replace('   public static void insightEyesPlayerTick(Player player) {','''   public static void insightEyesPlayerTick(Player player) {
      InsightPacket packet=inspect(player);
      if(packet!=null && player instanceof ServerPlayer serverPlayer)PacketHandler.sendTo(serverPlayer,packet);
   }
   public static InsightPacket inspect(Player player) {''')
s=re.sub(r'            PacketHandler.sendTo\(\s*\(ServerPlayer\)player,\s*(new InsightPacket\(.*?\))\s*\);\s*hadTargets.put\(player, true\);\s*return;',r'            hadTargets.put(player,true);\n            return \1;',s,flags=re.S)
s=re.sub(r'               PacketHandler.sendTo\(\s*\(ServerPlayer\)player,\s*(new InsightPacket\(.*?\))\s*\);\s*hadTargets.put\(player, true\);\s*return;',r'               hadTargets.put(player,true);\n               return \1;',s,flags=re.S)
s=re.sub(r'         PacketHandler.sendTo\(\s*\(ServerPlayer\)player,\s*(new InsightPacket\(.*?\))\s*\);',r'         return \1;',s,flags=re.S)
s=s.replace('   public static CompoundTag writeInventory','''   public static java.util.List<ItemStack> entityInventory(LivingEntity entity) {
      net.minecraft.world.Container container=entity instanceof Player p ? p.getInventory() : entity instanceof net.minecraft.world.entity.npc.InventoryCarrier carrier ? carrier.getInventory() : entity instanceof net.minecraft.world.Container c ? c : null;
      var result=new ArrayList<ItemStack>();
      if(container!=null)for(int i=0;i<container.getContainerSize();i++)result.add(container.getItem(i).copy());
      else for(var slot:net.minecraft.world.entity.EquipmentSlot.VALUES)result.add(entity.getItemBySlot(slot).copy());
      return result;
   }
   public static java.util.List<ItemStack> blockInventory(Level world,BlockPos pos) {
      var storage=net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(world,pos,net.minecraft.core.Direction.NORTH);
      if(storage==null)return null;
      var result=new ArrayList<ItemStack>();
      for(var view:storage){if(result.size()>=54)break;result.add(view.isResourceBlank()?ItemStack.EMPTY:view.getResource().toStack((int)Math.min(Integer.MAX_VALUE,view.getAmount())));}
      return result;
   }
   public static CompoundTag writeInventory''')
# append missing no-target return at end of inspect
s=s.replace('      }\n   }\n\n   public static java.util.List<ItemStack> entityInventory','      }\n      return null;\n   }\n\n   public static java.util.List<ItemStack> entityInventory',1)
p.write_text(s,encoding='utf-8')
p=main/'potion/ClairvoyanceEyesPotion.java';s=p.read_text(encoding='utf-8').replace('   public static void clairvoyanceEyesPlayerTick(Player player) {','''   public static void clairvoyanceEyesPlayerTick(Player player) {if(player instanceof ServerPlayer sp && EffectUtil.hasBuff(player,ModEffects.CLAIRVOYANCE))PacketHandler.sendTo(sp,predict(player));}
   public static EntityPathPacket predict(Player player) {''').replace('         PacketHandler.sendTo((ServerPlayer)player, new EntityPathPacket(entities, paths));','         return new EntityPathPacket(entities, paths);');s=s.replace('      }\n   }\n}', '      }\n      return new EntityPathPacket(new ArrayList<>(),new ArrayList<>());\n   }\n}');p.write_text(s,encoding='utf-8')
for name,cls in [('INSIGHT','InsightEyesPotion'),('CLAIRVOYANCE','ClairvoyanceEyesPotion')]:edit(main/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {name}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{name.lower()}"),new {cls}());\n public static void init()')
# Original native overlay and translucent prediction collector.
for rel in ['render/overlay/RenderInsightOverlay.java','render/overlay/RenderClairvoyance.java','render/ColorSubmitNodeCollector.java','render/ColorRenderTypeBuffer.java','render/ColorWrappedRenderLayer.java','render/MahouWrappedRenderTypes.java']:copy(rel,True)
for name in ['AccessorRenderType','AccessorRenderSetup','AccessorRenderSetupBuilder']:
 s=(orig/f'mixin/{name}.java').read_text(encoding='utf-8').replace('package stepsword.mahoutsukai.mixin;','package stepsword.mahoutsukai.fabric.mixin;');put(f'fabric/mixin/{name}.java',s,True)
p=client/'render/MahouWrappedRenderTypes.java';s=p.read_text(encoding='utf-8').replace('stepsword.mahoutsukai.mixin.','stepsword.mahoutsukai.fabric.mixin.');a=s.index('      Builder builder = RenderPipeline.builder(',s.index('private static RenderPipeline clonePipeline'));b=s.index('      mutator.accept(builder);',a);s=s[:a]+'''      Builder builder = PipelineCopies.copy(source).withLocation(Identifier.fromNamespaceAndPath(source.getLocation().getNamespace(),source.getLocation().getPath()+suffix));
'''+s[b:];p.write_text(s,encoding='utf-8')
p=client/'render/overlay/RenderClairvoyance.java';s=p.read_text(encoding='utf-8');a=s.index('   public static void renderPathPoint(');b=s.index('   private static void snapPredictionPose(',a);s=s[:a]+s[b:];p.write_text(s,encoding='utf-8')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text(encoding='utf-8'));j['client']+=['AccessorRenderType','AccessorRenderSetup','AccessorRenderSetupBuilder'];p.write_text(json.dumps(j,indent=2)+'\n',encoding='utf-8')
# native helpers referenced by preserved algorithms
p=main/'util/Utils.java';s=p.read_text(encoding='utf-8');s=s.replace('public enum MAHOU_SELECTION {','public enum MAHOU_SELECTION {INSIGHT,');a=s.rfind('}');source=(orig/'util/Utils.java').read_text(encoding='utf-8');primes=re.search(r'   public static int\[\] primes = new int\[\]\{.*?\n   };',source,re.S)[0];s=s[:a]+primes+'''
 public static net.minecraft.world.phys.HitResult rayTraceEyes(LivingEntity entity,double length){var start=entity.getEyePosition(1);return entity.level().clip(new net.minecraft.world.level.ClipContext(start,start.add(entity.getLookAngle().scale(length)),net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY,entity));}
'''+s[a:];p.write_text(s,encoding='utf-8')
p=main/'item/spells/mystic/CupOfHeavenSpellScroll.java';s=p.read_text(encoding='utf-8').replace('ModTriggers.GREATER_GRAIL.get()','ModTriggers.GREATER_GRAIL').replace('ModTriggers.LESSER_GRAIL.get()','ModTriggers.LESSER_GRAIL');p.write_text(s,encoding='utf-8')
for name in ['GREATER_GRAIL','LESSER_GRAIL']:edit(main/'advancements/ModTriggers.java',' public static void init()',f' public static final MahouTrigger {name}=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","{name.lower()}"),new MahouTrigger());\n public static void init()')
# Exact original config builder defaults and ranges.
config=(orig/'config/MTConfig.java').read_text(encoding='utf-8');p=main/'config/MTConfig.java';s=p.read_text(encoding='utf-8');needed=set()
for parent in [main,client]:
 for f in parent.rglob('*.java'):
  if f.name!='MTConfig.java':needed.update(re.findall(r'MTConfig\.(\w+)',f.read_text(encoding='utf-8')))
fields=[];ranges=[]
for name in sorted(needed):
 if re.search(r'public static [^\n]+\b'+name+r'\s*=',s) or name in ['load']:continue
 typ=re.search(r'   public static (int|double|boolean|List<\? extends String>) '+name+r' =',config)
 if not typ:continue
 if name=='HEAVENS_CUP_EFFECTS':fields.append('    public static java.util.List<? extends String> HEAVENS_CUP_EFFECTS=java.util.List.of("poison","weakness","blindness","slowness","wither","levitation");\n');continue
 m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',config);assert m,name;val,low,high=m.groups();fields.append(f'    public static {typ[1]} {name}={val};\n');ranges.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}}),\n')
s=s.replace('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES').replace('ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+''.join(ranges));p.write_text(s,encoding='utf-8')
for f in ['assets/mahoutsukai/textures/gui/insight.png','assets/mahoutsukai/textures/mob_effect/insight.png','assets/mahoutsukai/textures/mob_effect/clairvoyance.png']:
 source=root/'recovered'/f
 if source.exists():target=res/f;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,target)
# Lifecycle, native input-independent GUI and world collectors.
p=main/'fabric/MahouFabric.java';s=p.read_text(encoding='utf-8').replace('            long tick = world.getGameTime()', '            long tick = world.getGameTime()');s=s.replace('                stepsword.mahoutsukai.mana.PlayerManaManager.onPlayerTick(player, tick);','''                if(tick%10==0){stepsword.mahoutsukai.potion.InsightEyesPotion.insightEyesPlayerTick(player);stepsword.mahoutsukai.potion.ClairvoyanceEyesPotion.clairvoyanceEyesPlayerTick(player);}
                stepsword.mahoutsukai.mana.PlayerManaManager.onPlayerTick(player, tick);''');s=s.replace('        MahouNetworking.register();','''        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server->stepsword.mahoutsukai.util.MahouJobs.runJobs());
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler,server)->stepsword.mahoutsukai.potion.InsightEyesPotion.hadTargets.remove(handler.player));
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{stepsword.mahoutsukai.util.MahouJobs.clear();stepsword.mahoutsukai.potion.InsightEyesPotion.hadTargets.clear();});
        MahouNetworking.register();''');p.write_text(s,encoding='utf-8')
p=main/'fabric/MahouNetworking.java';s=p.read_text(encoding='utf-8').replace('    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.InsightPacket.TYPE,stepsword.mahoutsukai.networking.InsightPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.EntityPathPacket.TYPE,stepsword.mahoutsukai.networking.EntityPathPacket.STREAM_CODEC);''');p.write_text(s,encoding='utf-8')
p=client/'fabric/MahouFabricClient.java';s=p.read_text(encoding='utf-8').replace('    public void onInitializeClient() {','''    public void onInitializeClient() {
        SixFamilyClient.init();''');p.write_text(s,encoding='utf-8')
put('fabric/SixFamilyClient.java','''package stepsword.mahoutsukai.fabric;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import stepsword.mahoutsukai.networking.*;
import stepsword.mahoutsukai.potion.*;
import stepsword.mahoutsukai.render.overlay.*;
public final class SixFamilyClient {
 public static void init(){
  ClientPlayNetworking.registerGlobalReceiver(InsightPacket.TYPE,(packet,context)->{
   var world=context.client().level;
   RenderInsightOverlay.inventory=InsightEyesPotion.readInventory(packet.inventory,world);RenderInsightOverlay.effects=InsightEyesPotion.readEffects(packet.effects,world);
   RenderInsightOverlay.mainhandStack=InsightEyesPotion.readItem(packet.mainhand,world);RenderInsightOverlay.mainhandindex=packet.mainhandindex;RenderInsightOverlay.entityId=packet.id;RenderInsightOverlay.hasTarget=packet.hasTarget;RenderInsightOverlay.block=packet.block;
  });
  ClientPlayNetworking.registerGlobalReceiver(EntityPathPacket.TYPE,(packet,context)->{
   var world=context.client().level;var received=new java.util.HashSet<net.minecraft.world.entity.PathfinderMob>();
   for(int i=0;i<packet.entities.size() && i<packet.paths.size();i++)if(world.getEntity(packet.entities.get(i)) instanceof net.minecraft.world.entity.PathfinderMob mob){
    if(context.player().distanceTo(mob)>stepsword.mahoutsukai.config.MTConfig.CLAIRVOYANCE_RANGE)continue;
    received.add(mob);var old=ClairvoyanceEyesPotion.predictionMap.get(mob);var next=packet.paths.get(i);
    if(old==null || old.getNodeCount()==0 || !old.getEndNode().equals(next.getEndNode()))ClairvoyanceEyesPotion.predictionMap.put(mob,next);
   }
   ClairvoyanceEyesPotion.predictionMap.keySet().removeIf(mob->!received.contains(mob));ClairvoyanceEyesPotion.entityNBTMap.keySet().retainAll(received);
  });
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client->{if(client.player!=null)RenderInsightOverlay.insightCounters();});
  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","insight"),(graphics,delta)->{if(net.minecraft.client.Minecraft.getInstance().player!=null)RenderInsightOverlay.insightOverlay(graphics);});
  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context->{var mc=net.minecraft.client.Minecraft.getInstance();if(mc.player!=null)RenderClairvoyance.renderPrediction(context.poseStack(),context.submitNodeCollector(),context.levelState().cameraRenderState,mc.getCameraEntity(),mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));});
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{ClairvoyanceEyesPotion.predictionMap.clear();ClairvoyanceEyesPotion.entityNBTMap.clear();RenderInsightOverlay.hasTarget=false;RenderInsightOverlay.inventory.clear();RenderInsightOverlay.effects.clear();RenderInsightOverlay.mainhandStack=net.minecraft.world.item.ItemStack.EMPTY;});
 }
}
''',True)
print('Copied original Cup, Insight and Clairvoyance algorithms, native registries, packets and renderers')
