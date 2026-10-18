"""Historical one-shot familiar adaptation; subsequent native corrections are maintained in source."""
from pathlib import Path
import re,json,shutil
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources';client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
def copy(rel,cl=False):
 p=(client if cl else src)/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'),encoding='utf-8');return p
families=[('SummonFamiliar','summonFamiliar','summon_familiar','quartz'),('RecallFamiliar','recallFamiliar','recall_familiar','ender'),('SwapFamiliar','swapFamiliar','swap_familiar','emerald'),('FamiliarsGarden','familiarsGarden','familiars_garden','gold')]
for cls,field,id,last in families:
 for rel in [f'item/spells/familiar/{cls}SpellScroll.java',f'block/spells/familiar/{cls}Mahoujin.java',f'tile/familiar/{cls}MahoujinTileEntity.java']:copy(rel)
 p=src/f'tile/familiar/{cls}MahoujinTileEntity.java';s=p.read_text(encoding='utf-8');s=re.sub(r'ModItems\.\w+\.get\(\)',f'ModItems.REGISTERED.get("scroll_{id}")',s);p.write_text(s,encoding='utf-8')
 edit(src/'item/ModItems.java','        register("scroll_projection",',f'        register("scroll_{id}",stepsword.mahoutsukai.item.spells.familiar.{cls}SpellScroll::new);\n        register("scroll_projection",')
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain(',f'    public static final Supplier<Block> {field}Mahoujin = plain("mahoujin_{id}",new stepsword.mahoutsukai.block.spells.familiar.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain(')
 edit(src/'tile/ModTileEntities.java','    public static void init() {}',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.familiar.{cls}MahoujinTileEntity>> {field}=register("mahoujin_{id}",stepsword.mahoutsukai.tile.familiar.{cls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init() {{}}')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','      var geas =',f'      var {field}=makeRecipe(POWDERED_QUARTZ,POWDERED_QUARTZ,POWDERED_{last.upper()});\n      recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},true);\n      var geas =')
 edit(client/'fabric/MahouFabricClient.java','    public void onInitializeClient() {',f'    public void onInitializeClient() {{\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{field}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 for folder,name in [('items','scroll_'+id),('models/item','scroll_'+id),('blockstates','mahoujin_'+id)]:
  rel=f'assets/mahoutsukai/{folder}/{name}.json';target=res/rel;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/'recovered'/rel,target)
for rel in ['entity/FamiliarEntity.java','effects/familiar/SummonFamiliarSpellEffect.java','effects/familiar/RecallFamiliarEffect.java','effects/familiar/FamiliarsGardenEffect.java','potion/GardenPeacePotion.java','potion/InterestingBlockPotion.java']:copy(rel)
p=src/'effects/familiar/SummonFamiliarSpellEffect.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('familiarRightClick(EntityInteract event)','familiarRightClick(Player player, net.minecraft.world.entity.Entity target, InteractionHand hand)').replace('event.getTarget()','target').replace('event.getHand()','hand').replace('event.getEntity()','player').replace('event.getLevel()','player.level()');p.write_text(s,encoding='utf-8')
p=src/'effects/familiar/FamiliarsGardenEffect.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^\n]+\n','',s).replace('familiarsGardenLivingDamage(LivingIncomingDamageEvent event)','familiarsGardenLivingDamage(LivingEntity target, DamageSource src)').replace('      DamageSource src = event.getSource();\n','').replace('         LivingEntity target = event.getEntity();\n','');p.write_text(s,encoding='utf-8')
p=src/'entity/FamiliarEntity.java';s=p.read_text(encoding='utf-8').replace('import stepsword.mahoutsukai.MahouTsukaiMod;\n','').replace('import stepsword.mahoutsukai.handlers.ServerHandler;\n','').replace('MahouTsukaiMod.logger','org.slf4j.LoggerFactory.getLogger("mahoutsukai")').replace('ServerHandler.tickCounter','(this.level().getGameTime() % 50000L)').replace('PacketHandler.sendTo((ServerPlayer)Player,','net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send((ServerPlayer)Player,');s=s.replace('import stepsword.mahoutsukai.networking.PacketHandler;\n','')
# Both native entity factories (spawn and disk load) must have the same flight controller.
s=s.replace('      super((EntityType)ModEntities.FAMILIAR.get(), worldIn);\n      this.moveControl = new FlyingMoveControl(this, 10, false);\n      this.setPersistenceRequired();','      this(ModEntities.FAMILIAR.get(),worldIn);').replace('      super(entityEntityType, world);','      super(entityEntityType, world);\n      this.moveControl = new FlyingMoveControl(this, 10, false);\n      this.setPersistenceRequired();');p.write_text(s,encoding='utf-8')
p=copy('networking/GardenPacket.java');s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.network.handling.IPayloadContext;\n','');a=s.index('   public static void handle(');b=s.index('   public Type<GardenPacket>',a);s=s[:a]+s[b:];s=s.replace('public class GardenPacket implements CustomPacketPayload {','public class GardenPacket implements CustomPacketPayload {\n   public static final Type<GardenPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","garden_packet"));').replace('MahouPackets.GARDEN_TYPE','TYPE');p.write_text(s,encoding='utf-8')
p=src/'render/particle/ModParticles.java';p.parent.mkdir(parents=True,exist_ok=True);p.write_text('''package stepsword.mahoutsukai.render.particle;
public final class ModParticles {
 private static final net.minecraft.core.particles.SimpleParticleType PETAL_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","particle_petal"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> PETAL=()->PETAL_TYPE;
 public static void init() {}
}
''',encoding='utf-8')
copy('render/particle/ParticlePetal.java',True);p=copy('render/particle/PetalParticleType.java',True)
# The factory is client-only; the actual particle type is registered in common code.
s=p.read_text(encoding='utf-8').replace('public class PetalParticleType extends SimpleParticleType {\n   public PetalParticleType() {\n      super(true);\n   }','public class PetalParticleType {');p.write_text(s,encoding='utf-8')
for rel in ['render/entity/familiar/RenderFamiliarEntity.java','render/entity/familiar/ModelFamiliar.java','render/entity/familiar/FamiliarRenderState.java']:copy(rel,True)
p=client/'render/entity/familiar/RenderFamiliarEntity.java';s=p.read_text(encoding='utf-8').replace('import stepsword.mahoutsukai.client.ActingClientEffect;\n','');s=s.replace('      if (state.getRenderData(ActingClientEffect.ACTING_RENDER_TARGET) != null) {\n         super.submit(state, poseStack, submitNodeCollector, camera);\n      } else {','      {');p.write_text(s,encoding='utf-8')
edit(src/'entity/ModEntities.java',' public static void init() {',''' private static final Identifier FAMILIAR_ID=Identifier.fromNamespaceAndPath("mahoutsukai","familiar_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.FamiliarEntity> FAMILIAR_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,FAMILIAR_ID,
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.<stepsword.mahoutsukai.entity.FamiliarEntity>create(MobCategory.CREATURE,stepsword.mahoutsukai.entity.FamiliarEntity::new)
   .dimensions(net.minecraft.world.entity.EntityDimensions.fixed(0.5F,0.9F)).trackable(200,3,false).build(ResourceKey.create(Registries.ENTITY_TYPE,FAMILIAR_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.FamiliarEntity>> FAMILIAR=()->FAMILIAR_TYPE;
 public static void init() {
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(FAMILIAR_TYPE,stepsword.mahoutsukai.entity.FamiliarEntity.registerAttributes());''')
edit(src/'tickets/ModTickets.java',' public static void init()', ' public static final Supplier<TicketType> FAMILIAR_TICKET=register("familiar_ticket");\n public static void init()')
for field,cls,id in [('PEACEFUL_GARDEN','GardenPeacePotion','peaceful_garden'),('INTERESTING_BLOCK','InterestingBlockPotion','interesting_block')]:
 edit(src/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {field}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),new {cls}());\n public static void init()')
edit(src/'fabric/MahouNetworking.java','    public static void register() {','    public static void register() {\n        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.GardenPacket.TYPE,stepsword.mahoutsukai.networking.GardenPacket.STREAM_CODEC);')
edit(client/'fabric/MahouFabricClient.java','    public void onInitializeClient() {','''    public void onInitializeClient() {
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(stepsword.mahoutsukai.render.particle.ModParticles.PETAL.get(),stepsword.mahoutsukai.render.particle.PetalParticleType.PetalFactory::new);
        net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.FAMILIAR.get(),stepsword.mahoutsukai.render.entity.familiar.RenderFamiliarEntity::new);
        ClientPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.GardenPacket.TYPE,(packet,context)->stepsword.mahoutsukai.entity.FamiliarEntity.gardenEffectB(stepsword.mahoutsukai.util.Utils.toBlockPos(packet.posX,packet.posY,packet.posZ),context.client().level));''')
edit(src/'fabric/MahouFabric.java','        stepsword.mahoutsukai.tickets.ModTickets.init();','''        stepsword.mahoutsukai.tickets.ModTickets.init();
        stepsword.mahoutsukai.render.particle.ModParticles.init();
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player,world,hand,target,hit)-> {
            if(!stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player)) stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarRightClick(player,target,hand);
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect.familiarsGardenAttackEntity(target,player) ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.ENTITY_LOAD.register((entity,world)-> {
            if(entity instanceof stepsword.mahoutsukai.entity.FamiliarEntity familiar && familiar.getOwnerReference()!=null) {
                var owner=familiar.getOwnerReference().getUUID();
                var old=stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.get(owner);
                if(old==null || !old.isAlive()) stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.put(owner,familiar);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect.familiarMap.clear());''')
edit(src/'fabric/mixin/IncomingSpellDamageMixin.java','  float modified=','  if(stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect.familiarsGardenLivingDamage(entity,source)) {ci.setReturnValue(false);return;}\n  float modified=')
edit(src/'fabric/mixin/LivingMahouMixin.java','        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick','        if(!((LivingEntity)(Object)this).level().isClientSide()) stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect.familiarsGardenLivingUpdate((LivingEntity)(Object)this);\n        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick')
# Exact original defaults/ranges of connected settings, including non-default static initializer differences.
p=src/'config/MTConfig.java';existing=set(re.findall(r'public static [\w.<>? ]+ (\w+)\s*=',p.read_text(encoding='utf-8')));original=(orig/'config/MTConfig.java').read_text(encoding='utf-8');needed={'FAMILIAR_INTERESTING_BLOCK_RENDER_RANGE'}
for file in list((src/'effects/familiar').glob('*.java'))+list((src/'item/spells/familiar').glob('*.java'))+[src/'entity/FamiliarEntity.java']:needed.update(re.findall(r'MTConfig\.(\w+)',file.read_text(encoding='utf-8')))
fields=[];ranges=[]
for name in sorted(needed-existing):
 match=re.search(r'   public static (int|double|boolean) '+name+r' = (.*?);',original,re.S);assert match,name
 typ,val=match.groups();default=re.search(r'"'+name+r'",\s*(-?[\d.E]+|true|false)(?:,|\s*\))',original);assert default,name;val=default.group(1);fields.append(f'    public static {typ} {name} = {val};\n')
 if typ!='boolean':
  m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',original);assert m,name;_,low,high=m.groups();ranges.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}}),\n')
edit(p,'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES');edit(p,'ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+''.join(ranges))
for rel in ['assets/mahoutsukai/textures/item/familiar-texture1.png','assets/mahoutsukai/textures/item/familiar-texture2.png','assets/mahoutsukai/textures/particle/petal.png','assets/mahoutsukai/particles/particle_petal.json']:
 target=res/rel;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/'recovered'/rel,target)
print('Familiar creature, four rituals, garden effects/particle, native registry and renderer connected')
