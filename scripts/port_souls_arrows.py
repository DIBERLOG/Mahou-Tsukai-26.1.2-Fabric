from pathlib import Path
import re,shutil,json
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a);p.write_text(s.replace(a,b))
def copy(rel):
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'));return p
for group,cls,field,tilefield,itemfield,name,cats in [('displacement','ProjectileDisplacement','projectileDisplacement','projectileDisplacement','projectileDisplacement','projectile_displacement',['ENDER','ENDER','DIAMOND']),('eyes','DeathCollectionEyes','deathCollection','deathCollectionEyes','deathCollectionEyes','death_collection',['EYE','EYE','IRON'])]:
 copy(f'block/spells/{group}/{cls}Mahoujin.java');p=copy(f'tile/{group}/{cls}MahoujinTileEntity.java');p.write_text(p.read_text().replace(f'ModItems.{itemfield}Scroll.get()',f'ModItems.REGISTERED.get("scroll_{name}")'))
 copy(f'item/spells/{group}/{cls}SpellScroll.java');edit(src/'item/ModItems.java','    static {',f'    static {{\n        register("scroll_{name}",stepsword.mahoutsukai.item.spells.{group}.{cls}SpellScroll::new);')
 for folder in ['items','models/item']:shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/scroll_{name}.json',res/f'assets/mahoutsukai/{folder}/scroll_{name}.json')
 model=json.loads((res/f'assets/mahoutsukai/models/item/scroll_{name}.json').read_text())
 for tex in model.get('textures',{}).values():
  ns,path=tex.split(':');q=res/f'assets/{ns}/textures/{path}.png';q.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f'recovered/assets/{ns}/textures/{path}.png',q)
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {field}Mahoujin = plain("mahoujin_{name}",new stepsword.mahoutsukai.block.spells.{group}.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(src/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity>> {tilefield} = register("mahoujin_{name}",stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init()')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {field} = makeRecipe('+','.join('POWDERED_'+c for c in cats)+f');\n      recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},true);')
 client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/MahouFabricClient.java';edit(client,'        stepsword.mahoutsukai.Keybinds.init();',f'        stepsword.mahoutsukai.Keybinds.init();\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{tilefield}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 shutil.copyfile(root/f'recovered/assets/mahoutsukai/blockstates/mahoujin_{name}.json',res/f'assets/mahoutsukai/blockstates/mahoujin_{name}.json')
p=copy('potion/DeathCollectionEyesPotion.java');s=p.read_text().replace('Utils.damageSourceAny(source, DamageTypes.GENERIC_KILL, DamageTypes.FELL_OUT_OF_WORLD)','(source.is(DamageTypes.GENERIC_KILL) || source.is(DamageTypes.FELL_OUT_OF_WORLD))');p.write_text(s)
edit(src/'potion/ModEffects.java',' public static void init()', ' public static final Holder<MobEffect> DEATH_COLLECTION_EYES = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","death_collection_eyes"),new DeathCollectionEyesPotion());\n public static void init()');shutil.copyfile(root/'recovered/assets/mahoutsukai/textures/mob_effect/death_collection_eyes.png',res/'assets/mahoutsukai/textures/mob_effect/death_collection_eyes.png')
p=copy('effects/displacement/ProjectileDisplacementSpellEffect.java');s=p.read_text().replace('import stepsword.mahoutsukai.proxy.CommonProxy;\n','').replace('HashSet<Entity> entities = CommonProxy.getAllEntities((ServerLevel)caster.level());','HashSet<Entity> entities = new HashSet<>();\n            ((ServerLevel)caster.level()).getAllEntities().forEach(entities::add);');p.write_text(s)
copy('item/Faecake.java');edit(src/'item/ModItems.java','    static {','    static {\n        register("faecake",Faecake::new);')
for folder in ['items','models/item']:shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/faecake.json',res/f'assets/mahoutsukai/{folder}/faecake.json')
shutil.copyfile(root/'recovered/assets/mahoutsukai/textures/item/faecake.png',res/'assets/mahoutsukai/textures/item/faecake.png');shutil.copyfile(root/'recovered/data/mahoutsukai/recipe/faecake.json',res/'data/mahoutsukai/recipe/faecake.json')
p=src/'fabric/MahouFabric.java';edit(p,'        stepsword.mahoutsukai.advancements.ModTriggers.init();','''        net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.ALLOW_DEATH.register((entity,source,amount)->
            !stepsword.mahoutsukai.potion.DeathCollectionEyesPotion.deathCollectionEyesLivingDeath(entity,source));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity,world)->
            stepsword.mahoutsukai.effects.displacement.ProjectileDisplacementSpellEffect.projectileDisplacementJoinWorld(entity));
        stepsword.mahoutsukai.advancements.ModTriggers.init();''')
original=(orig/'config/MTConfig.java').read_text();p=src/'config/MTConfig.java';needed=set()
for rel in ['potion/DeathCollectionEyesPotion.java','effects/displacement/ProjectileDisplacementSpellEffect.java','item/spells/eyes/DeathCollectionEyesSpellScroll.java']:needed.update(re.findall(r'MTConfig\.(\w+)',(src/rel).read_text()))
existing=set(re.findall(r'public static [\w.<>? ]+ (\w+) =',p.read_text()));fields=[]
for name in sorted(needed-existing):
 m=re.search(r'   public static (int|double|boolean) '+name+r' = (.*?);',original,re.S);assert m,name
 typ,val=m.groups();d=re.search(r'"'+name+r'",\s*(-?[\d.E]+|true|false)(?:,|\s*\))',original)
 if d:val=d.group(1)
 fields.append(f'    public static {typ} {name} = {val};\n')
edit(p,'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES')
s=p.read_text();rows=[]
for name in re.findall(r'public static (?:int|double) (\w+) =',s):
 m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',original);assert m,name
 default,low,high=m.groups();rows.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}})')
a=s.index('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES');b=s.index('    private static final com.google.gson.Gson JSON',a);s=s[:a]+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+',\n'.join(rows)+'\n    );\n'+s[b:];p.write_text(s)
print('Adapted projectile displacement, death collection and faecake')
