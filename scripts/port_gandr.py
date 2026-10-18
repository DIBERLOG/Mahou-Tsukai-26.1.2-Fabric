"""Historical one-shot Gandr server/common dependency adaptation."""
from pathlib import Path
import re,json,shutil
base=Path('MahouTsukai-Fabric/src');orig=Path('recovered/stepsword/mahoutsukai');main=base/'main/java/stepsword/mahoutsukai';client=base/'client/java/stepsword/mahoutsukai';res=base/'main/resources'
def put(rel,s):
 p=main/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8');return p
def copy(rel):return put(rel,(orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'))
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
for rel in ['dataattachments/mahoujinentity/MahoujinData.java','dataattachments/mahoujinentity/MahoujinDataStorage.java','entity/mahoujin/IMahoujinEntity.java','entity/mahoujin/AbstractMahoujinEntity.java','entity/mahoujin/MahoujinEntity.java','entity/mahoujin/GandrEntity.java','item/spells/secret/GandrSpellScroll.java','block/spells/secret/GandrMahoujin.java','tile/secret/GandrMahoujinTileEntity.java','networking/MagicCircleBoomParticlePacket.java','dataattachments/settingsmahou/SettingsMahou.java','dataattachments/settingsmahou/ISettingsMahou.java','dataattachments/settingsmahou/SettingsMahouStorage.java','dataattachments/settingsmahou/MahouSettings.java','networking/SettingsMahouPacket.java']:copy(rel)
put('entity/ModEntityDataSerializers.java','''package stepsword.mahoutsukai.entity;
import net.minecraft.network.syncher.*;
import stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData;
public final class ModEntityDataSerializers {
 public static final EntityDataSerializer<MahoujinData> MAHOUJIN=EntityDataSerializer.forValueType(MahoujinData.STREAM_CODEC);
 static {EntityDataSerializers.registerSerializer(MAHOUJIN);}
 public static void init(){}
}
''')
p=main/'entity/mahoujin/IMahoujinEntity.java';s=p.read_text(encoding='utf-8').replace('import stepsword.mahoutsukai.dataattachments.DataAttachments;','');s=s.replace('return (MahoujinData)this.asEntity().getData(DataAttachments.MAHOUJIN_ENTITY_ATTACHMENTS);','return this.asEntity().getEntityData().get(AbstractMahoujinEntity.MAHOUJIN_DATA);');s=s.replace('this.asEntity().setData(DataAttachments.MAHOUJIN_ENTITY_ATTACHMENTS, data);','this.asEntity().getEntityData().set(AbstractMahoujinEntity.MAHOUJIN_DATA,data);');s=s.replace('   default void defineSync(Builder builder) {\n   }','   default void defineSync(Builder builder) {builder.define(AbstractMahoujinEntity.MAHOUJIN_DATA,new MahoujinData());\n   }');s=s.replace('   default void read(ValueInput input) {\n   }','''   default void read(ValueInput input) {
      MahoujinData data=new MahoujinData();stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinDataStorage.read(data,input);data.setFired(input.getBooleanOr("MAHOUTSUKAI_FIRED",false));this.asEntity().getEntityData().set(AbstractMahoujinEntity.MAHOUJIN_DATA,data);
   }''').replace('   default void add(ValueOutput output) {\n   }','   default void add(ValueOutput output) {stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinDataStorage.write(getMahoujinData(),output);output.putBoolean("MAHOUTSUKAI_FIRED",getFired());\n   }');p.write_text(s,encoding='utf-8')
p=main/'entity/mahoujin/AbstractMahoujinEntity.java';s=p.read_text(encoding='utf-8').replace(' implements IMahoujinEntity {',' implements IMahoujinEntity {\n   public static final net.minecraft.network.syncher.EntityDataAccessor<stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData> MAHOUJIN_DATA=net.minecraft.network.syncher.SynchedEntityData.defineId(AbstractMahoujinEntity.class,stepsword.mahoutsukai.entity.ModEntityDataSerializers.MAHOUJIN);');p.write_text(s,encoding='utf-8')
p=main/'entity/mahoujin/GandrEntity.java';s=p.read_text(encoding='utf-8').replace('import stepsword.mahoutsukai.MahouTsukaiMod;','').replace('import stepsword.mahoutsukai.item.mysticcode.MysticCode;','').replace(' && !(player.getUseItem().getItem() instanceof MysticCode)','').replace('MahouTsukaiMod.proxy.spawnGandrParticle(this);','clientParticles.accept(this);').replace('public class GandrEntity extends MahoujinEntity {','public class GandrEntity extends MahoujinEntity {\n   public static java.util.function.Consumer<GandrEntity> clientParticles=entity->{};').replace('ModTriggers.GANDR.get()','ModTriggers.GANDR');s=s.replace('      this.damage = input.getFloatOr','      this.effects = new ArrayList<>(input.read("MAHOUTSUKAI_GANDR_EFFECTS",MobEffectInstance.CODEC.listOf()).orElse(java.util.List.of()));\n      this.damage = input.getFloatOr',1);s=s.replace('      output.putFloat("MAHOUTSUKAI_DAMAGE"','      output.store("MAHOUTSUKAI_GANDR_EFFECTS",MobEffectInstance.CODEC.listOf(),this.effects);\n      output.putFloat("MAHOUTSUKAI_DAMAGE"',1);p.write_text(s,encoding='utf-8')
# Native settings persistence and isolated copies retain original colors and ordinals.
p=main/'dataattachments/settingsmahou/SettingsMahou.java';s=p.read_text(encoding='utf-8').replace('this.intsettings.put(spell, lst);','this.intsettings.put(spell, new java.util.ArrayList<>(lst));');p.write_text(s,encoding='utf-8')
p=main/'dataattachments/settingsmahou/MahouSettings.java';s=p.read_text(encoding='utf-8').replace('List<Integer> ret = mahou.getIntSettings(spell.ordinal());','List<Integer> ret = new java.util.ArrayList<>(mahou.getIntSettings(spell.ordinal()));');p.write_text(s,encoding='utf-8')
put('fabric/SettingsHolder.java','''package stepsword.mahoutsukai.fabric;
public interface SettingsHolder {stepsword.mahoutsukai.dataattachments.settingsmahou.SettingsMahou mahoutsukai$getSettings();}
''')
put('fabric/mixin/PlayerSettingsMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.*;
import stepsword.mahoutsukai.dataattachments.settingsmahou.*;
@Mixin(Player.class)
public abstract class PlayerSettingsMixin implements stepsword.mahoutsukai.fabric.SettingsHolder {
 @Unique private final SettingsMahou mahoutsukai$settings=new SettingsMahou();
 public SettingsMahou mahoutsukai$getSettings(){return mahoutsukai$settings;}
 @Inject(method="readAdditionalSaveData",at=@At("TAIL"))
 private void mahoutsukai$readSettings(ValueInput input,CallbackInfo ci){input.child("mahoutsukai:settings_mahou").ifPresent(child->SettingsMahouStorage.read(mahoutsukai$settings,child));}
 @Inject(method="addAdditionalSaveData",at=@At("TAIL"))
 private void mahoutsukai$writeSettings(ValueOutput output,CallbackInfo ci){SettingsMahouStorage.write(mahoutsukai$settings,output.child("mahoutsukai:settings_mahou"));}
}
''')
for name,id in [('MagicCircleBoomParticlePacket','magic_circle_boom_particle'),('SettingsMahouPacket','settings_mahou')]:
 p=main/f'networking/{name}.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'   public static void handle\(.*?\n   }\n','',s,flags=re.S);s=s.replace('implements CustomPacketPayload {',f'implements CustomPacketPayload {{\n   public static final Type<{name}> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","{id}"));');s=re.sub(r'return MahouPackets\.\w+;', 'return TYPE;',s);s=s.replace('   ISettingsMahou mahou;','   public ISettingsMahou mahou;');p.write_text(s,encoding='utf-8')
p=main/'util/Utils.java';s=p.read_text(encoding='utf-8');a=s.rfind('}');s=s[:a]+'''
 public static stepsword.mahoutsukai.dataattachments.settingsmahou.SettingsMahou getSettingsMahou(Player player){return ((stepsword.mahoutsukai.fabric.SettingsHolder)player).mahoutsukai$getSettings();}
 public static String getRegistryName(net.minecraft.world.effect.MobEffect effect,net.minecraft.world.level.Level world){return net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect).toString();}
 public static java.util.List<Integer> readIntListFromBuffer(io.netty.buffer.ByteBuf buffer){int n=buffer.readInt();if(n<0 || n>1024)throw new IllegalArgumentException("Invalid settings list length");var values=new java.util.ArrayList<Integer>();for(int i=0;i<n;i++)values.add(buffer.readInt());return values;}
 public static void writeIntListToBuffer(io.netty.buffer.ByteBuf buffer,java.util.List<Integer> values){buffer.writeInt(values.size());for(int value:values)buffer.writeInt(value);}
 public static float colorConvert(int color){return color/255.0f;}
'''+s[a:];p.write_text(s,encoding='utf-8')
edit(main/'entity/ModEntities.java',' public static void init() {',''' private static final Identifier GANDR_ID=Identifier.fromNamespaceAndPath("mahoutsukai","gandr_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.GandrEntity> GANDR_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,GANDR_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.GandrEntity>of(stepsword.mahoutsukai.entity.mahoujin.GandrEntity::new,MobCategory.MISC).sized(1,1).clientTrackingRange(64).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,GANDR_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.GandrEntity>> GANDR=()->GANDR_TYPE;
 public static void init() {''')
edit(main/'item/ModItems.java','    static {','    public static final Supplier<Item> gandrScroll=register("scroll_gandr",stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll::new);\n    static {')
edit(main/'block/ModBlocks.java','    private static Supplier<Block> plain','    public static final Supplier<Block> gandrMahoujin=plain("mahoujin_gandr",new stepsword.mahoutsukai.block.spells.secret.GandrMahoujin(),false);\n    private static Supplier<Block> plain')
edit(main/'tile/ModTileEntities.java','    public static void init()','    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.secret.GandrMahoujinTileEntity>> gandr=register("mahoujin_gandr",stepsword.mahoutsukai.tile.secret.GandrMahoujinTileEntity::new,ModBlocks.gandrMahoujin.get());\n    public static void init()')
text=(orig/'block/spells/MahoujinRecipeRegistrar.java').read_text(encoding='utf-8');recipe=re.search(r'recipes.put\((\w+), \(BlockBase\)ModBlocks\.gandrMahoujin',text)[1];cats=re.search(r'UnorderedList '+recipe+r' = makeRecipe\(([^)]+)\)',text)[1];edit(main/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {recipe}=makeRecipe({cats});recipes.put({recipe},(BlockBase)ModBlocks.gandrMahoujin.get());needsCloth.put({recipe},true);')
edit(main/'advancements/ModTriggers.java',' public static void init()',' public static final MahouTrigger GANDR=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","gandr"),new MahouTrigger());\n public static void init()')
edit(main/'fabric/MahouFabric.java','        stepsword.mahoutsukai.tickets.ModTickets.init();','        stepsword.mahoutsukai.entity.ModEntityDataSerializers.init();\n        stepsword.mahoutsukai.tickets.ModTickets.init();')
edit(main/'fabric/MahouFabric.java','                if(tick%10==0){','                if(tick%100==0)stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.updateClientMahou(player);\n                if(tick%10==0){')
edit(main/'fabric/MahouFabric.java','        MahouNetworking.register();','''        ServerPlayerEvents.JOIN.register(stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings::updateClientMahou);
        ServerPlayerEvents.COPY_FROM.register((oldPlayer,newPlayer,alive)->stepsword.mahoutsukai.util.Utils.getSettingsMahou(newPlayer).copyMahou(stepsword.mahoutsukai.util.Utils.getSettingsMahou(oldPlayer)));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer,newPlayer,alive)->stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.updateClientMahou(newPlayer));
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll.gandrs.clear());
        MahouNetworking.register();''')
edit(main/'fabric/MahouNetworking.java','    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.SettingsMahouPacket.TYPE,stepsword.mahoutsukai.networking.SettingsMahouPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket.TYPE,stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket.STREAM_CODEC);''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text(encoding='utf-8'));j['mixins']+=['PlayerSettingsMixin'];p.write_text(json.dumps(j,indent=2)+'\n',encoding='utf-8')
# Effective defaults from the original builder, not stale static declarations.
config=(orig/'config/MTConfig.java').read_text(encoding='utf-8');p=main/'config/MTConfig.java';s=p.read_text(encoding='utf-8');needed=set()
for f in main.rglob('*.java'):
 if f.name!='MTConfig.java':needed.update(re.findall(r'MTConfig\.(\w+)',f.read_text(encoding='utf-8')))
fields=[];ranges=[]
for name in sorted(needed):
 if re.search(r'public static [^\n]+\b'+name+r'\s*=',s) or name=='load':continue
 typ=re.search(r'   public static (int|double|boolean|List<\? extends String>) '+name+r' =',config)
 if not typ:continue
 if typ[1].startswith('List'):
  m=re.search('"'+name+r'",\s*new String\[\]\{([^}]*)\}',config)
  if m is None and re.search('"'+name+r'",\s*new String\[0\]',config):m=[None,'']
  assert m,name;fields.append(f'    public static java.util.List<? extends String> {name}=java.util.List.of({m[1]});\n');continue
 if typ[1]=='boolean':
  m=re.search('"'+name+r'",\s*(true|false)',config);assert m,name;fields.append(f'    public static boolean {name}={m[1]};\n');continue
 m=re.search('"'+name+r'",\s*([\d.EeFf+-]+)\s*,\s*([\d.EeFf+-]+)\s*,\s*([\d.EeFf+-]+)',config);assert m,name;val,low,high=m.groups();fields.append(f'    public static {typ[1]} {name}={val};\n');ranges.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}}),\n')
s=s.replace('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES').replace('ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+''.join(ranges));p.write_text(s,encoding='utf-8')
for f in ['assets/mahoutsukai/items/scroll_gandr.json','assets/mahoutsukai/models/item/scroll_gandr.json','assets/mahoutsukai/blockstates/mahoujin_gandr.json']:
 target=res/f;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(Path('recovered')/f,target)
