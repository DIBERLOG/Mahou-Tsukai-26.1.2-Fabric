from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
original = root / 'recovered/stepsword/mahoutsukai'
main = root / 'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
client = root / 'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'

def put(base, name, text):
    p = base / name
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(text, encoding='utf-8')

def copy(base, name):
    text = (original / name).read_text(encoding='utf-8')
    put(base, name, text)
    return text

def edit(base, name, old, new):
    p = base / name
    text = p.read_text(encoding='utf-8')
    assert old in text, (name, old)
    put(base, name, text.replace(old, new))

# Original Authority circle, with a persistent owner rather than a transient entity ID.
s = copy(main, 'entity/mahoujin/AuthorityMahoujinEntity.java')
s = s.replace('import javax.annotation.Nullable;', '') .replace('   @Nullable\n', '')
s = s.replace('      this.casterUUID = caster.getId();', '      this.casterUUID = caster.getId();\n      this.setCasterUUID(caster.getUUID());')
s = s.replace('Entity caster = this.level().getEntity(this.casterUUID);', 'Entity caster = this.getCasterUUID() == null ? this.level().getEntity(this.casterUUID) : ((ServerLevel)this.level()).getEntity(this.getCasterUUID());')
put(main, 'entity/mahoujin/AuthorityMahoujinEntity.java', s)

copy(main, 'datacomponents/lance/LanceMahou.java')
s = copy(main, 'entity/mahoujin/RhongomyniadEntity.java')
s = re.sub(r'import net\.neoforged[^;]+;\n', '', s)
a = s.index('   public static void rhongomyniadRightClick(')
b = s.index('   @Override\n   public void tick()', a)
s = s[:a] + '''   @Override
   public net.minecraft.world.InteractionResult interact(Player player, net.minecraft.world.InteractionHand hand) {
      if (this.nocaster || !player.getUUID().equals(this.getCasterUUID())) return net.minecraft.world.InteractionResult.PASS;
      if (!this.level().isClientSide() && this.isAlive()) {
         if (!this.getStack().isEmpty()) this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getStack().copy()));
         this.setStack(ItemStack.EMPTY);
         this.discard();
      }
      return net.minecraft.world.InteractionResult.SUCCESS;
   }

''' + s[b:]
s = s.replace('public static void rhongomyniadLivingHurt(LivingIncomingDamageEvent event) {\n      LivingEntity target = event.getEntity();\n      Entity source = event.getSource().getEntity();', 'public static void rhongomyniadLivingHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource damageSource) {\n      Entity source = damageSource.getEntity();')
s = s.replace('.orElse(ModItems.rhongomyniad == null ? ItemStack.EMPTY : new ItemStack(ModItems.rhongomyniad))', '.orElse(ItemStack.EMPTY)')
s = s.replace('      stack.setCount(1);', '      this.nocaster = input.getBooleanOr("MAHOUTSUKAI_NOCASTER", false);\n      if (!stack.isEmpty()) stack.setCount(1);')
s = s.replace('      if (!this.getStack().isEmpty()) {', '      output.putBoolean("MAHOUTSUKAI_NOCASTER", this.nocaster);\n      if (!this.getStack().isEmpty()) {')
s = s.replace('Utils.getLanceMahou(rhongo.getStack())', 'rhongo.getStack().getOrDefault(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT, new LanceMahou())')
put(main, 'entity/mahoujin/RhongomyniadEntity.java', s)

for field, cls, name in [('AUTHORITY','AuthorityMahoujinEntity','authority_mahoujin_entity'), ('RHONGOMYNIAD','RhongomyniadEntity','rhongomyniad_entity')]:
    entry = f''' private static final Identifier {field}_ID=Identifier.fromNamespaceAndPath("mahoutsukai","{name}");
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.{cls}>> {field}=()->{field}_TYPE;
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.{cls}> {field}_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,{field}_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.{cls}>of(stepsword.mahoutsukai.entity.mahoujin.{cls}::new,MobCategory.MISC).sized(1f,1f).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,{field}_ID)));
'''
    edit(main, 'entity/ModEntities.java', ' public static void init()', entry+' public static void init()')

edit(main, 'fabric/mixin/IncomingSpellDamageMixin.java', '  stepsword.mahoutsukai.item.nobu.Nobu.nobuCreate(source);', '  stepsword.mahoutsukai.item.nobu.Nobu.nobuCreate(source);\n  stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity.rhongomyniadLivingHurt(entity,source);')

# Reuse original serializable projector settings and scrollable screens.
copy(main, 'render/enchant/ProjectorValues.java')
copy(main, 'util/FogProjectorValues.java')
for name in ['MahoujinProjectorScreen','FogProjectorScreen','ProjectorScreenScrollLayout','ProjectorScreenLabels','MahouEditBox']:
    s = copy(client, f'render/gui/{name}.java')
    s = s.replace('PacketHandler.sendToServer(', 'net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(')
    put(client, f'render/gui/{name}.java', s)

s = copy(main, 'tile/MahoujinProjectorTileEntity.java')
put(main, 'tile/MahoujinProjectorTileEntity.java', s)
s = copy(main, 'tile/FogProjectorTileEntity.java')
s = s.replace('import stepsword.mahoutsukai.MahouTsukaiMod;', '')
a = s.index('   @Override\n   public void tick(')
b = s.index('   public float get_particle_fog_size()', a)
s = s[:a] + '   // Client particle emission is registered in ProjectorClient.\n\n' + s[b:]
put(main, 'tile/FogProjectorTileEntity.java', s)

for cls, tile, field, id in [('MahoujinProjector','MahoujinProjectorTileEntity','projector','mahoujin_projector'), ('FogProjector','FogProjectorTileEntity','fogProjector','fog_projector')]:
    put(main, f'block/{cls}.java', f'''package stepsword.mahoutsukai.block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.Blocks;
import stepsword.mahoutsukai.tile.{tile};
public class {cls} extends BlockTileEntity<{tile}> {{
 public {cls}() {{super(Properties.ofFullCopy(Blocks.GLASS).noOcclusion(),"{id}");}}
 public Class<{tile}> getTileEntityClass() {{return {tile}.class;}}
 public {tile} newBlockEntity(BlockPos pos,BlockState state) {{return new {tile}(pos,state);}}
 {'public void setPlacedBy(net.minecraft.world.level.Level level,BlockPos pos,BlockState state,net.minecraft.world.entity.LivingEntity placer,net.minecraft.world.item.ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(placer instanceof net.minecraft.world.entity.player.Player player && level.getBlockEntity(pos) instanceof '+tile+' tile){tile.caster=player.getUUID();tile.sendUpdates();}}' if field == 'projector' else ''}
}}
''')
    edit(main,'block/ModBlocks.java','public final class ModBlocks {',f'public final class ModBlocks {{\n    public static final Supplier<Block> {field}=plain("{id}",new {cls}(),true);')
    edit(main,'tile/ModTileEntities.java','public final class ModTileEntities {',f'public final class ModTileEntities {{\n    public static final Supplier<BlockEntityType<{tile}>> {field}=register("{id}",{tile}::new,ModBlocks.{field}.get());')

# Components retain the original IDs and NBT codecs.
components = [('PROJECTOR_VALUES_COMPONENT','stepsword.mahoutsukai.render.enchant.ProjectorValues','projector_values'),('FOG_PROJECTOR_VALUES_COMPONENT','stepsword.mahoutsukai.util.FogProjectorValues','fog_projector_values')]
for field, cls, id in components:
    edit(main,'datacomponents/DataComponents.java','public final class DataComponents {',f'public final class DataComponents {{\n public static final DataComponentType<{cls}> {field}=DataComponentType.<{cls}>builder().persistent({cls}.CODEC).networkSynchronized(ByteBufCodecs.fromCodec({cls}.CODEC)).build();')
    edit(main,'datacomponents/DataComponents.java','    public static void init() {',f'    public static void init() {{\n        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),{field});')
edit(main,'datacomponents/DataComponents.java','public final class DataComponents {','''public final class DataComponents {
 public static final Codec<stepsword.mahoutsukai.datacomponents.lance.LanceMahou> LANCE_CODEC=RecordCodecBuilder.create(i->i.group(Codec.FLOAT.fieldOf("attackDamage").forGetter(stepsword.mahoutsukai.datacomponents.lance.LanceMahou::getAttackDamage),Codec.FLOAT.fieldOf("weight").forGetter(stepsword.mahoutsukai.datacomponents.lance.LanceMahou::getWeight)).apply(i,stepsword.mahoutsukai.datacomponents.lance.LanceMahou::new));
 public static final DataComponentType<stepsword.mahoutsukai.datacomponents.lance.LanceMahou> LANCE_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.lance.LanceMahou>builder().persistent(LANCE_CODEC).networkSynchronized(ByteBufCodecs.fromCodec(LANCE_CODEC)).build();''')
edit(main,'datacomponents/DataComponents.java','    public static void init() {','    public static void init() {\n        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","lance_mahou"),LANCE_COMPONENT);')

# Keep packet formats, but validate sender, range, loaded chunk and finite bounded input server-side.
for cls,id in [('MahoujinProjectorUpdatePacket','mahoujin_projector_update'),('FogProjectorUpdatePacket','fog_projector_update')]:
    s = (original/f'networking/{cls}.java').read_text()
    s = re.sub(r'import net\.neoforged[^;]+;\n','',s)
    a = s.index('   public static void handle(')
    b = s.index('   public static void updateTileEntity',a)
    s = s[:a] + f'''   public static final Type<{cls}> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","{id}"));
   public static void handle({cls} message, ServerPlayer player) {{
      if (!valid(message, player)) return;
      updateTileEntity(message,player.level(){',player' if cls.startswith('Mahoujin') else ''});
   }}
   private static boolean valid({cls} message, ServerPlayer player) {{
      if (player.isSpectator() || message.pos == null || !player.level().hasChunkAt(message.pos) || player.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(message.pos))>64 || !player.level().mayInteract(player,message.pos)) return false;
      return stepsword.mahoutsukai.fabric.ProjectorValidation.valid(message);
   }}

''' + s[b:]
    s = re.sub(r'MahouPackets\.\w+_TYPE','TYPE',s)
    put(main,f'networking/{cls}.java',s)

put(main,'fabric/ProjectorValidation.java','''package stepsword.mahoutsukai.fabric;
public final class ProjectorValidation {
 private ProjectorValidation() {}
 public static boolean valid(Object packet) {
  try {
   for(var field:packet.getClass().getDeclaredFields()) {
    if(java.lang.reflect.Modifier.isStatic(field.getModifiers()))continue;
    field.setAccessible(true);String name=field.getName();
    if(field.getType()==float.class){float v=field.getFloat(packet);if(!Float.isFinite(v))return false;
     float cap=(name.equals("x")||name.equals("y")||name.equals("z"))?(float)stepsword.mahoutsukai.config.MTConfig.PROJECTOR_OFFSET_CAP:name.equals("size")?(float)stepsword.mahoutsukai.config.MTConfig.PROJECTOR_SIZE_CAP:name.equals("camera_fog_radius")?(float)stepsword.mahoutsukai.config.MTConfig.FOG_PROJECTOR_MAX_RADIUS:360;
     if(Math.abs(v)>cap)return false;
     if((name.equals("size")||name.equals("camera_fog_radius")||name.equals("particle_fog_size")||name.equals("particle_fog_thickness")||name.equals("camera_fog_strength"))&&v<0)return false;
    }
    if(field.getType()==int.class){int v=field.getInt(packet);if(name.equals("image")||name.equals("runes")){if(v < -1 || v>255)return false;}
     else if(name.equals("particle_fog_frequency_ticks")){if(v<1||v>12000)return false;}
     else if(name.equals("particle_fog_frequency_num")){if(v<0||v>100)return false;}
     else if(v<0||v>255)return false;
    }
   }
   return true;
  }catch(ReflectiveOperationException e){throw new IllegalStateException(e);}
 }
}
''')

cfg = (main/'config/MTConfig.java').read_text()
cfg = cfg.replace('public static int RHONGOMYNIAD_MANA_COST=300;', 'public static int RHONGOMYNIAD_MANA_COST=40;')
fields = '''
 public static int RHONGOMYNIAD_MAX_SMITES=10;
 public static int RHONGOMYNIAD_RANGE=20;
 public static boolean RHONGOMYNIAD_RESPECT_IMMUNE=false;
 public static double PROJECTOR_DEFAULT_ROTATION_SPEED=1;
 public static boolean PROJECTOR_CONTINUES_ROTATE_WHEN_OFF=true;
 public static double PROJECTOR_OFFSET_CAP=60;
 public static double PROJECTOR_SIZE_CAP=60;
 public static double FOG_PROJECTOR_MAX_RADIUS=30;
'''
cfg=cfg.replace('public final class MTConfig {','public final class MTConfig {'+fields)
if 'public static int RHONGOMYNIAD_RANGE' not in cfg:
    cfg=cfg.replace('public class MTConfig {','public class MTConfig {'+fields)
put(main,'config/MTConfig.java',cfg)

edit(main,'fabric/MahouFabric.java','    public void onInitialize() {','''    public void onInitialize() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.TYPE,stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.TYPE,stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.STREAM_CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.MahoujinProjectorUpdatePacket.handle(packet,context.player()));
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.FogProjectorUpdatePacket.handle(packet,context.player()));''')
print('Ported remaining entities, projector blocks/data/screens and validated network updates')
