from pathlib import Path
p=Path('MahouTsukai-Fabric/src/selfCheck/java/stepsword/mahoutsukai/RemainingFamiliesIntegration.java');s=p.read_text(encoding='utf-8').replace('"Spatial charged AOE launches actual nearby mob"','"Spatial charged AOE launches actual nearby mob: motion="+target.getDeltaMovement()+", location="+storage.location+", circle="+storage.mahoujin.getRotationSpeed()+", nearby="+world.getEntitiesOfClass(Entity.class,new AABB(storage.location,storage.location).inflate(MTConfig.SPATIAL_DISORIENTATION_AOE_RADIUS)).size()');p.write_text(s,encoding='utf-8')
import sys;sys.path.insert(0,'scripts');import remaining_port_helpers as h
h.ritual('item/spells/projection/RealityMarbleSpellScroll.java','block/spells/projection/RealityMarbleMahoujin.java','tile/projection/RealityMarbleMahoujinTileEntity.java','realityMarbleMahoujin')
p=h.copy('effects/projection/RealityMarbleSpellEffect.java');s=p.read_text(encoding='utf-8');s='\n'.join(l for l in s.splitlines() if not l.startswith('import net.neoforged.'));a=s.index('   public static void realityMarbleChunkUnload');b=s.index('   public static void realityMarbleLivingHurt',a);s=s[:a]+'''   public static void realityMarbleChunkLoad(ServerLevel world,LevelChunk chunk){
      if(inMarble(world)){var pos=chunk.getPos();spawnSwords(new BlockPos(pos.getMiddleBlockX(),100,pos.getMiddleBlockZ()),world,chunk,1,MTConfig.MARBLE_DIMENSION_SPAWN_RATE);}
   }
'''+s[b:];s=s.replace('            chunk.addEntity(weapon);','');s=s.replace('EffectUtil.addEnchantment(stack,','addEnchantment(stack,');idx=s.index('   public static Item getItemFromName');s=s[:idx]+'''   private static void addEnchantment(ItemStack stack,net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key,int level,Level world){stack.enchant(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key),level);}
'''+s[idx:];p.write_text(s,encoding='utf-8')
p=h.main/'util/Utils.java';s=p.read_text(encoding='utf-8').replace('enum MAHOU_SELECTION {','enum MAHOU_SELECTION {SCROLL_REALITY_MARBLE,');p.write_text(s,encoding='utf-8')
p=h.main/'fabric/MahouFabric.java';h.insert(p,'        stepsword.mahoutsukai.entity.ModEntityDataSerializers.init();','        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_LOAD.register(stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect::realityMarbleChunkLoad);\n')
p=h.main/'fabric/mixin/IncomingSpellDamageMixin.java';h.insert(p,'  LivingEntity entity=(LivingEntity)(Object)this;','')
s=p.read_text(encoding='utf-8').replace('  LivingEntity entity=(LivingEntity)(Object)this;','  LivingEntity entity=(LivingEntity)(Object)this;\n  if(source.is(net.minecraft.world.damagesource.DamageTypes.FALL) && stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect.inMarble(level)){ci.setReturnValue(false);return;}');s=s.replace('  amount.set(', '  stepsword.mahoutsukai.effects.projection.RealityMarbleSpellEffect.realityMarbleLivingHurt(entity);\n  amount.set(');p.write_text(s,encoding='utf-8')
import shutil
for rel in ['dimension/reality_marble.json','dimension_type/reality_marble.json','worldgen/biome/marble.json']:
 dst=h.res/'data/mahoutsukai'/rel;dst.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(Path('recovered/data/mahoutsukai')/rel,dst)
for f in Path('recovered/data/mahoutsukai/worldgen/density_function').rglob('*.json'):
 dst=h.res/'data/mahoutsukai/worldgen/density_function'/f.relative_to('recovered/data/mahoutsukai/worldgen/density_function');dst.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(f,dst)
h.config()
