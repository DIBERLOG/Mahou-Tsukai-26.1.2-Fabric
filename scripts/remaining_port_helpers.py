"""Reusable local source migration helpers. Copies never overwrite adapted files."""
from pathlib import Path
import re,shutil
orig=Path('recovered/stepsword/mahoutsukai'); main=Path('MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'); client=Path('MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'); res=Path('MahouTsukai-Fabric/src/main/resources')
def write(p,s):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def copy(rel):
 p=main/rel
 if not p.exists():write(p,(orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable').replace('javax.annotation.Nonnull','org.jetbrains.annotations.NotNull').replace('@Nonnull','@NotNull'))
 return p

def insert(p,anchor,text):
 s=p.read_text(encoding='utf-8')
 if text.strip() in s:return
 assert anchor in s,(p,anchor);write(p,s.replace(anchor,text+anchor,1))
def append(p,text):
 s=p.read_text(encoding='utf-8')
 if text.strip() not in s:write(p,s[:s.rfind('}')]+text+'\n}\n')
def ritual(scrollrel,blockrel,tilerel,blockfield):
 for r in [scrollrel,blockrel,tilerel]:copy(r)
 scroll=(orig/scrollrel).read_text();block=(orig/blockrel).read_text();tile=(orig/tilerel).read_text()
 fq=lambda rel:'stepsword.mahoutsukai.'+rel.removesuffix('.java').replace('/','.')
 id=re.search(r'super\("([^"]+)"',scroll)[1];bid=re.search(r'super\("([^"]+)"',block)[1];sf=re.search(r'ModItems\.(\w+)\.get',tile)[1];tf=re.search(r'ModTileEntities\.(\w+)\.get',tile)[1]
 insert(main/'item/ModItems.java','    static {',f'    public static final Supplier<Item> {sf}=register("scroll_{id}",{fq(scrollrel)}::new);\n')
 insert(main/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {blockfield}=plain("{bid}",new {fq(blockrel)}(),false);\n')
 insert(main/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<{fq(tilerel)}>> {tf}=register("{bid}",{fq(tilerel)}::new,ModBlocks.{blockfield}.get());\n')
 p=main/'block/spells/MahoujinRecipeRegistrar.java';s=p.read_text();text=(orig/'block/spells/MahoujinRecipeRegistrar.java').read_text();recipe=re.search(r'recipes.put\((\w+), \(BlockBase\)ModBlocks\.'+blockfield,text)[1];cats=re.search(r'UnorderedList '+recipe+r' = makeRecipe\(([^)]+)',text)[1];cloth=re.search(r'needsCloth.put\('+recipe+r', (true|false)',text)[1]
 if f'ModBlocks.{blockfield}.get()' not in s:write(p,s.replace('   public static void init() {',f'   public static void init() {{\n      var {recipe}=makeRecipe({cats});recipes.put({recipe},(BlockBase)ModBlocks.{blockfield}.get());needsCloth.put({recipe},{cloth});'))
 for rel in [f'assets/mahoutsukai/items/scroll_{id}.json',f'assets/mahoutsukai/models/item/scroll_{id}.json',f'assets/mahoutsukai/blockstates/{bid}.json']:
  target=res/rel;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(Path('recovered')/rel,target)
 insert(client/'fabric/SixFamilyClient.java',' public static void init(){', '') if False else None
 p=client/'fabric/SixFamilyClient.java';s=p.read_text();line=f'  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{tf}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);'
 if line not in s:write(p,s.replace(' public static void init(){',' public static void init(){\n'+line))
def entity(field,rel,id,width=1,height=1):
 copy(rel);cls='stepsword.mahoutsukai.'+rel.removesuffix('.java').replace('/','.')
 insert(main/'entity/ModEntities.java',' public static void init() {',f''' private static final Identifier {field}_ID=Identifier.fromNamespaceAndPath("mahoutsukai","{id}");
 private static final EntityType<{cls}> {field}_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,{field}_ID,EntityType.Builder.<{cls}>of({cls}::new,MobCategory.MISC).sized({width}f,{height}f).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,{field}_ID)));
 public static final Supplier<EntityType<{cls}>> {field}=()->{field}_TYPE;
''')
def config():
 original=(orig/'config/MTConfig.java').read_text();p=main/'config/MTConfig.java';s=p.read_text();needed=set()
 for f in [*main.rglob('*.java'),*client.rglob('*.java')]:
  if f!=p:needed.update(re.findall(r'MTConfig\.(\w+)',f.read_text(encoding='utf-8')))
 fields=[];ranges=[]
 for name in sorted(needed):
  if re.search(r'public static [^\n]+\b'+name+r'\s*=',s) or name=='load':continue
  typ=re.search(r'   public static (int|double|boolean|List<\? extends String>) '+name+r'\s*(?:=|;)',original)
  if not typ:continue
  if typ[1].startswith('List'):
   m=re.search('"'+name+r'",\s*new String\[\]\{([^}]*)\}',original)
   if m is None and re.search('"'+name+r'",\s*new String\[0\]',original):value=''
   else:assert m,name;value=m[1]
   fields.append(f'    public static java.util.List<? extends String> {name}=java.util.List.of({value});\n');continue
  if typ[1]=='boolean':
   m=re.search('"'+name+r'",\s*(true|false)',original);assert m,name;fields.append(f'    public static boolean {name}={m[1]};\n');continue
  m=re.search('"'+({'FALLEN_DOWN_MANA_PER_TICK':'FALLEN_DOWN_MANA_COST'}.get(name,name))+r'",\s*([\d.EeFf+-]+)\s*,\s*([\d.EeFf+-]+)\s*,\s*([\d.EeFf+-]+)',original);assert m,name;val,low,high=m.groups();fields.append(f'    public static {typ[1]} {name}={val};\n');ranges.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}}),\n')
 s=s.replace('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES').replace('ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+''.join(ranges));write(p,s)
