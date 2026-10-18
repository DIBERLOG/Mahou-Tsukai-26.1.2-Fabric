from pathlib import Path
import re,shutil,json
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a);p.write_text(s.replace(a,b))
def copy(rel):
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);s=(orig/rel).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable').replace('javax.annotation.Nonnull','org.jetbrains.annotations.NotNull').replace('@Nonnull','@NotNull');p.write_text(s);return p
# Original global save codec keeps all reality-marble and fiction data, even while their gameplay is still pending.
for rel in ['dataattachments/worldsave/WorldMahouStorage.java','dataattachments/worldsave/FictionData.java','dataattachments/worldsave/WorldMahou.java']:copy(rel)
s=(orig/'dataattachments/worldsave/MahouSavedData.java').read_text();start=s.index('   public void changePlayerSpawn');header=s[:s.index('   public int addFameIfExists')];header='\n'.join(line for line in header.splitlines() if not any(x in line for x in ['import stepsword.mahoutsukai.config.','import stepsword.mahoutsukai.dataattachments.settingsmahou.','import stepsword.mahoutsukai.datacomponents.','import stepsword.mahoutsukai.item.','import stepsword.mahoutsukai.mana.','import stepsword.mahoutsukai.util.']))+'\n';(src/'dataattachments/worldsave/MahouSavedData.java').write_text(header+s[start:])
p=copy('tile/exchange/ContractMahoujinTileEntity.java');s=p.read_text();s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('import stepsword.mahoutsukai.entity.MentalDisplacementEntity;\n','');s=re.sub(r'      if \(e instanceof MentalDisplacementEntity.*?\n      }\n','',s,flags=re.S);s=s.replace('breakContract(LivingIncomingDamageEvent event)','breakContract(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.damagesource.DamageSource source)').replace('event.getEntity()','entity').replace('event.getSource();','').replace('event.getSource()','source');s=s.replace('for (Player entityIn : targets)','for (Player entityIn : targets)');# incoming method local 'player' is fine
p.write_text(s)
p=copy('block/spells/exchange/ContractMahoujin.java')
edit(src/'util/Utils.java','    public static void debug(String message)', '''    public static stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData getWorldMahou(Level world) {
        return world != null && world.getServer()!=null ? new stepsword.mahoutsukai.dataattachments.worldsave.WorldMahou(world.getServer().overworld()).data : null;
    }
    public static BlockPos toBlockPos(double x,double y,double z) { return BlockPos.containing(x,y,z); }
    public static void schedule(Level world,Runnable task) { if(world.getServer()!=null) world.getServer().execute(task); }
    public static void debug(String message)''')
edit(src/'fabric/mixin/IncomingSpellDamageMixin.java','  if(entity instanceof net.minecraft.world.entity.player.Player', '  stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.breakContract(entity,source);\n  if(entity instanceof net.minecraft.world.entity.player.Player')
copy('util/PlayerHelp.java');copy('tile/boundary/BoundaryMahoujinTileEntity.java');copy('block/spells/BoundaryBlockTileEntity.java');copy('item/spells/boundary/BoundarySpellScroll.java')
families=[('AlarmBoundary','AlarmBoundary','alarmBoundary','alarm_barrier','boundary_alarm',['IRON','IRON','QUARTZ']),('GravityBoundary','GravityBoundary','gravityBoundary','gravity_barrier','boundary_gravity',['IRON','IRON','GOLD']),('DisplacementBoundary','DisplacementBoundary','displacementBoundary','displacement_barrier','boundary_displacement',['IRON','IRON','ENDER']),('DrainLifeBoundary','DrainLifeBoundary','drainLifeBoundary','drain_life_barrier','boundary_drain_life',['IRON','IRON','EMERALD']),('RaiseEnclosureBoundary','RaiseEnclosureBarrier','raiseEnclosureBoundary','raise_enclosure_barrier','boundary_raise_enclosure',['IRON']*3),('TangibleBoundary','TangibleBoundary','tangibleBoundary','tangible_barrier','boundary_tangible',['IRON','IRON','DIAMOND'])]
for blockcls,tilecls,field,name,item,cats in families+[('Contract','Contract','contract','contract',None,['EMERALD','EMERALD','ENDER'])]:
 group='boundary' if item else 'exchange'
 copy(f'block/spells/{group}/{blockcls}Mahoujin.java')
 p=copy(f'tile/{group}/{tilecls}MahoujinTileEntity.java') if item else src/'tile/exchange/ContractMahoujinTileEntity.java'
 if item:
  s=p.read_text().replace(f'ModItems.{field.replace("Boundary","") if False else "NO"}Scroll.get()','unused')
  s=re.sub(r'ModItems\.boundary\w+Scroll.get\(\)',f'ModItems.REGISTERED.get("scroll_{item}")',s)
  s=s.replace('item.thrower != null && item.thrower.equals(this.getCasterUUID())','item.getOwner() != null && item.getOwner().getUUID().equals(this.getCasterUUID())')
  s=s.replace('MahouTsukaiMod.logger','org.slf4j.LoggerFactory.getLogger("mahoutsukai")').replace('import stepsword.mahoutsukai.MahouTsukaiMod;\n','')
  s=s.replace('entityIn.lastDamageSource = null;','((stepsword.mahoutsukai.fabric.mixin.LivingDamageAccessor)entityIn).mahoutsukai$clearLastDamageSource(null);')
  # Java decompiler's collector cast is not source-compatible; same set operation.
  s=re.sub(r'entities.stream\(\)\s*\.map\(Entity::getUUID\)\s*\.collect\(\(Collector<.*?Collectors.toCollection\(HashSet::new\)\)', 'entities.stream().map(Entity::getUUID).collect(Collectors.toSet())',s)
  p.write_text(s)
  copy(f'item/spells/boundary/{blockcls}SpellScroll.java')
  edit(src/'item/ModItems.java','    static {',f'    static {{\n        register("scroll_{item}",stepsword.mahoutsukai.item.spells.boundary.{blockcls}SpellScroll::new);')
  for folder in ['items','models/item']:
   q=res/f'assets/mahoutsukai/{folder}/scroll_{item}.json';shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/scroll_{item}.json',q)
  model=json.loads((res/f'assets/mahoutsukai/models/item/scroll_{item}.json').read_text())
  for tex in model.get('textures',{}).values():
   ns,path=tex.split(':');q=res/f'assets/{ns}/textures/{path}.png';q.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f'recovered/assets/{ns}/textures/{path}.png',q)
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {field}Mahoujin = plain("mahoujin_{name}",new stepsword.mahoutsukai.block.spells.{group}.{blockcls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(src/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.{group}.{tilecls}MahoujinTileEntity>> {field} = register("mahoujin_{name}",stepsword.mahoutsukai.tile.{group}.{tilecls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init()')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {field} = makeRecipe('+','.join('POWDERED_'+c for c in cats)+f');\n      recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},false);')
 client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/MahouFabricClient.java';edit(client,'        stepsword.mahoutsukai.Keybinds.init();',f'        stepsword.mahoutsukai.Keybinds.init();\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{field}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 shutil.copyfile(root/f'recovered/assets/mahoutsukai/blockstates/mahoujin_{name}.json',res/f'assets/mahoutsukai/blockstates/mahoujin_{name}.json')
# Invisible boundary surfaces preserve their original collision and persistence logic.
copy('block/spells/boundary/InvisibleBarrierBlock.java');copy('tile/boundary/InvisibleBarrierBlockTileEntity.java')
edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain','    public static final Supplier<Block> invisibleBarrierBlock = plain("invisible_barrier_block",new stepsword.mahoutsukai.block.spells.boundary.InvisibleBarrierBlock(),false);\n    private static Supplier<Block> plain')
edit(src/'tile/ModTileEntities.java','    public static void init()','    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.boundary.InvisibleBarrierBlockTileEntity>> invisibleBarrier = register("invisible_barrier_block",stepsword.mahoutsukai.tile.boundary.InvisibleBarrierBlockTileEntity::new,ModBlocks.invisibleBarrierBlock.get());\n    public static void init()')
p=src/'tile/boundary/BoundaryMahoujinTileEntity.java';s=p.read_text();s=re.sub(r'entities.stream\(\)\s*\.map\(Entity::getUUID\)\s*\.collect\(\(Collector<.*?Collectors.toCollection\(HashSet::new\)\)', 'entities.stream().map(Entity::getUUID).collect(Collectors.toSet())',s);p.write_text(s)
# Copy exact original settings used by the newly connected code; effective builder defaults override static initializer values.
original=(orig/'config/MTConfig.java').read_text();config=src/'config/MTConfig.java';existing=set(re.findall(r'public static [\w.<>? ]+ (\w+) =',config.read_text()));needed=set()
for d in [src/'tile/boundary',src/'tile/exchange/ContractMahoujinTileEntity.java']:
 for f in d.rglob('*.java') if d.is_dir() else [d]: needed.update(re.findall(r'MTConfig\.(\w+)',f.read_text()))
fields=[]
for name in sorted(needed-existing):
 m=re.search(r'   public static (int|double|boolean|List<\? extends String>) '+name+r' = (.*?);',original,re.S);assert m,name
 typ,value=m.groups();value=value.replace('new ArrayList<>()','new java.util.ArrayList<>()');typ=typ.replace('List<','java.util.List<')
 if typ in ['int','double','boolean']:
  default=re.search(r'"'+name+r'",\s*(-?[\d.E]+|true|false)(?:,|\s*\))',original)
  if default:value=default.group(1)
 fields.append(f'    public static {typ} {name} = {value};\n')
edit(config,'    private static final com.google.gson.Gson JSON',''.join(fields)+'    private static final com.google.gson.Gson JSON')
edit(src/'util/EffectUtil.java',' public static boolean hasBuff',''' public static void magicAttack(LivingEntity entity,float damage,LivingEntity caster) { genericAttack(entity,damage,entity.damageSources().magic(),caster); }
 public static void genericAttack(LivingEntity entity,float damage,net.minecraft.world.damagesource.DamageSource source,LivingEntity caster) {
  if(caster instanceof net.minecraft.world.entity.player.Player player) entity.setLastHurtByPlayer(player,100);
  if(entity.level() instanceof net.minecraft.server.level.ServerLevel world) entity.hurtServer(world,source,damage);
 }
 public static boolean hasBuff''')
(src/'fabric/mixin/LivingDamageAccessor.java').write_text('''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
@Mixin(LivingEntity.class)
public interface LivingDamageAccessor { @Accessor("lastDamageSource") void mahoutsukai$clearLastDamageSource(DamageSource source); }
''');p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['mixins'].append('LivingDamageAccessor');p.write_text(json.dumps(j,indent=2)+'\n')
print('Connected original contracts and six boundary families')
