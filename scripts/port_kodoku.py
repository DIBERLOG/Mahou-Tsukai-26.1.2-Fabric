from pathlib import Path
import re,shutil,json
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a);p.write_text(s.replace(a,b))
def copy(rel):
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'));return p
copy('datacomponents/kodoku/KodokuMahou.java');p=copy('entity/kodoku/KodokuEntity.java');s=p.read_text().replace('import stepsword.mahoutsukai.handlers.ServerHandler;\n','').replace('ServerHandler.tickCounter % 20L','this.level().getGameTime() % 20L').replace('riding.hurt(this.damageSources().mobAttack(this), 2.0F);','riding.hurtServer((ServerLevel)this.level(),this.damageSources().mobAttack(this), 2.0F);').replace('ModItems.kodoku.get()','ModItems.REGISTERED.get("kodoku")');p.write_text(s)
p=copy('item/kodoku/KodokuItem.java');p.write_text(p.read_text().replace('target.yRot','target.getYRot()').replace('target.xRot','target.getXRot()'))
p=copy('effects/Kodoku.java');s=p.read_text();s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('kodokuEntityDeath(LivingDeathEvent event)','kodokuEntityDeath(LivingEntity victim, net.minecraft.world.damagesource.DamageSource source)').replace('event.getSource()','source').replace('event.getEntity()','victim').replace('Utils.damageSourceAny(source, DamageTypes.ON_FIRE, DamageTypes.IN_FIRE, DamageTypes.LAVA)','(source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA))');p.write_text(s)
copy('commands/SetKodoku.java')
p=src/'datacomponents/DataComponents.java';edit(p,'import com.mojang.serialization.Codec;','import com.mojang.serialization.Codec;\nimport stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou;');edit(p,'    public static void init() {','''   public static final Codec<KodokuMahou> KODOKU_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.fieldOf("kodoku").forGetter(KodokuMahou::getKodoku)).apply(instance,KodokuMahou::new));
   public static final StreamCodec<ByteBuf,KodokuMahou> KODOKU_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,KodokuMahou::getKodoku,KodokuMahou::new);
   public static final DataComponentType<KodokuMahou> KODOKU_COMPONENT=DataComponentType.<KodokuMahou>builder().persistent(KODOKU_CODEC).networkSynchronized(KODOKU_STREAM_CODEC).build();
    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","kodoku_mahou"),KODOKU_COMPONENT);''')
p=src/'util/Utils.java';edit(p,'    public static void debug(String message)', '''    public static stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou getKodokuMahou(ItemStack stack) {
        var component=stepsword.mahoutsukai.datacomponents.DataComponents.KODOKU_COMPONENT;
        var value=stack.get(component);
        if(value==null) {value=new stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou();stack.set(component,value);}
        return value.copy();
    }
    public static void setKodokuMahou(ItemStack stack,stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou value) { stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.KODOKU_COMPONENT,value); }
    public static void debug(String message)''')
edit(src/'util/EffectUtil.java',' public static boolean hasBuff',' public static net.minecraft.world.phys.Vec3 fromBlockPos(net.minecraft.core.BlockPos pos) { return new net.minecraft.world.phys.Vec3(pos.getX(),pos.getY(),pos.getZ()); }\n public static boolean hasBuff')
edit(src/'item/ModItems.java','    static {','    static {\n        register("kodoku",stepsword.mahoutsukai.item.kodoku.KodokuItem::new);')
p=src/'entity/ModEntities.java';edit(p,' public static void init() {',''' private static final Identifier KODOKU_ID=Identifier.fromNamespaceAndPath("mahoutsukai","kodoku_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.kodoku.KodokuEntity> KODOKU_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,KODOKU_ID,
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.<stepsword.mahoutsukai.entity.kodoku.KodokuEntity>create(MobCategory.MISC,stepsword.mahoutsukai.entity.kodoku.KodokuEntity::new)
   .dimensions(net.minecraft.world.entity.EntityDimensions.fixed(0.4F,0.3F)).trackable(64,3,false).build(ResourceKey.create(Registries.ENTITY_TYPE,KODOKU_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.kodoku.KodokuEntity>> KODOKU=()->KODOKU_TYPE;
 public static void init() {
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(KODOKU_TYPE,stepsword.mahoutsukai.entity.kodoku.KodokuEntity.registerAttributes());''')
p=src/'fabric/MahouFabric.java';edit(p,'''net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.ALLOW_DEATH.register((entity,source,amount)->
            !stepsword.mahoutsukai.potion.DeathCollectionEyesPotion.deathCollectionEyesLivingDeath(entity,source));''','''net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.ALLOW_DEATH.register((entity,source,amount)-> {
            if(stepsword.mahoutsukai.potion.DeathCollectionEyesPotion.deathCollectionEyesLivingDeath(entity,source)) return false;
            stepsword.mahoutsukai.effects.Kodoku.kodokuEntityDeath(entity,source);return true;
        });''');edit(p,'            GetMana.register(dispatcher);','            stepsword.mahoutsukai.commands.SetKodoku.register(dispatcher);\n            GetMana.register(dispatcher);')
for rel in ['render/entity/kodoku/ModelKodoku.java','render/entity/kodoku/RenderKodoku.java']:
 p=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text())
p=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/MahouFabricClient.java';edit(p,'        stepsword.mahoutsukai.Keybinds.init();','''        stepsword.mahoutsukai.Keybinds.init();
        net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.registerModelLayer(stepsword.mahoutsukai.render.entity.kodoku.ModelKodoku.KODOKU_LAYER,stepsword.mahoutsukai.render.entity.kodoku.ModelKodoku::createBodyLayer);
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.KODOKU.get(),stepsword.mahoutsukai.render.entity.kodoku.RenderKodoku::new);''')
for folder in ['items','models/item']:shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/kodoku.json',res/f'assets/mahoutsukai/{folder}/kodoku.json')
for path in ['item/kodoku','entity/kodoku']:shutil.copyfile(root/f'recovered/assets/mahoutsukai/textures/{path}.png',res/f'assets/mahoutsukai/textures/{path}.png')
shutil.copyfile(root/'recovered/data/mahoutsukai/recipe/kodoku.json',res/'data/mahoutsukai/recipe/kodoku.json')
original=(orig/'config/MTConfig.java').read_text();p=src/'config/MTConfig.java';fields=[]
for name in re.findall(r'MTConfig\.(KODOKU_\w+)',(src/'effects/Kodoku.java').read_text()):
 if name in ''.join(fields):continue
 m=re.search(r'"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',original);assert m,name
 fields.append(f'    public static double {name} = {m.group(1)};\n')
edit(p,'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES')
s=p.read_text();rows=[]
for name in re.findall(r'public static (?:int|double) (\w+) =',s):
 m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',original);assert m,name
 _,low,high=m.groups();rows.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}})')
a=s.index('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES');b=s.index('    private static final com.google.gson.Gson JSON',a);p.write_text(s[:a]+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+',\n'.join(rows)+'\n    );\n'+s[b:])
print('Adapted Kodoku entity/item/component/kill transfer/battery charging/model/animation/crafting')
