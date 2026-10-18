from pathlib import Path
import re,json,shutil
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources';client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai'
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
def copy(rel):
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'),encoding='utf-8');return p
for group,cls,field,name,cats in [('mystic','DamageReplication','damageReplication','damage_replication',['GOLD','GOLD','EMERALD']),('secret','SelectiveDisplacement','selectiveDisplacement','selective_displacement',['EYE','ENDER','EMERALD'])]:
 copy(f'block/spells/{group}/{cls}Mahoujin.java');p=copy(f'tile/{group}/{cls}MahoujinTileEntity.java');edit(p,f'ModItems.{field}Scroll.get()',f'ModItems.REGISTERED.get("scroll_{name}")');copy(f'item/spells/{group}/{cls}SpellScroll.java')
 edit(src/'item/ModItems.java','    static {',f'    static {{\n        register("scroll_{name}",stepsword.mahoutsukai.item.spells.{group}.{cls}SpellScroll::new);')
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {field}Mahoujin=plain("mahoujin_{name}",new stepsword.mahoutsukai.block.spells.{group}.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(src/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity>> {field}=register("mahoujin_{name}",stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init()')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {field}=makeRecipe('+','.join('POWDERED_'+x for x in cats)+f');recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},true);')
 edit(client/'fabric/MahouFabricClient.java','        stepsword.mahoutsukai.Keybinds.init();',f'        stepsword.mahoutsukai.Keybinds.init();\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{field}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 for folder in ['items','models/item']:shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/scroll_{name}.json',res/f'assets/mahoutsukai/{folder}/scroll_{name}.json')
 shutil.copyfile(root/f'recovered/assets/mahoutsukai/blockstates/mahoujin_{name}.json',res/f'assets/mahoutsukai/blockstates/mahoujin_{name}.json')
 for tex in json.loads((res/f'assets/mahoutsukai/models/item/scroll_{name}.json').read_text()).get('textures',{}).values():
  ns,path=tex.split(':');q=res/f'assets/{ns}/textures/{path}.png';q.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f'recovered/assets/{ns}/textures/{path}.png',q)
for cls,name in [('Cooldown',''),('SelectiveDisplacement','selective_displacement'),('SelectiveDisplacementCooldown','selective_displacement_cooldown')]:
 copy(f'potion/{cls}Potion.java')
 if name:
  edit(src/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {name.upper()}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{name}"),new {cls}Potion());\n public static void init()')
  # Invisible cooldown icons are not supplied by the original.
  q=root/f'recovered/assets/mahoutsukai/textures/mob_effect/{name}.png'
  if q.exists():shutil.copyfile(q,res/f'assets/mahoutsukai/textures/mob_effect/{name}.png')
p=copy('effects/mystic/DamageReplicationSpellEffect.java');s=p.read_text();s=re.sub(r'import net.neoforged[^\n]+\n','',s);a=s.index('         a -> Utils.getPredicate');b=s.index('\n      );',a);s=s[:a]+'         a -> a instanceof LivingEntity, true'+s[b:];s=s.replace('damageReplicationLivingDamage(LivingIncomingDamageEvent event)','damageReplicationLivingDamage(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,float amount)').replace('event.getEntity()','victim').replace('target.hurt(event.getSource(), event.getAmount());','((LivingEntity)target).hurtServer((net.minecraft.server.level.ServerLevel)victim.level(),source,amount);');s=s.replace('damageReplicationPotionAdded(Added event)','damageReplicationPotionAdded(LivingEntity victim,net.minecraft.world.effect.MobEffectInstance effect)').replace('event.getEffectInstance()','effect');p.write_text(s)
# The original five-argument buff overload includes amplifier; retain it.
edit(src/'util/EffectUtil.java',' public static boolean hasBuff',' public static float toDegrees(float radians) {return radians*180.0F/(float)Math.PI;}\n public static void buff(LivingEntity entity,Holder<MobEffect> effect,boolean additive,int ticks,int amplifier) {buff(entity,effect,additive,ticks,true,amplifier);}\n public static boolean hasBuff')
p=copy('networking/SelectiveDisplacementPacket.java');s=p.read_text();s=re.sub(r'import net.neoforged[^\n]+\n','',s);a=s.index('   public static void handle(');b=s.index('   public static void handleAbilitySelectiveDisplacement',a);s=s[:a]+s[b:];s=s.replace('Object e2;','Entity e2;').replace('((Entity)e2).yRot = EffectUtil.toDegrees(-yaw);','e2.setYRot(EffectUtil.toDegrees(-yaw));').replace('((Entity)e2).xRot = EffectUtil.toDegrees(pitch);','e2.setXRot(EffectUtil.toDegrees(pitch));').replace('e.yRot = EffectUtil.toDegrees(-yaw2);','e.setYRot(EffectUtil.toDegrees(-yaw2));').replace('e.xRot = EffectUtil.toDegrees(pitch2);','e.setXRot(EffectUtil.toDegrees(pitch2));').replace('((MahouTrigger)ModTriggers.BOOGIE_WOOGIE.get()).trigger(player);','ModTriggers.BOOGIE_WOOGIE.trigger(player);').replace('return MahouPackets.SELECTIVE_DISPLACEMENT_TYPE;','return TYPE;');s=s.replace('   int id1 = 0;','   public static final Type<SelectiveDisplacementPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","selective_displacement_packet"));\n   public int id1 = 0;').replace('   int id2 = 0;','   public int id2 = 0;')
s=s.replace('         if (e != null && e2 != null && e != e2 &&', '''         // The client supplies runtime entity IDs: enforce the original selection AABB on the server.
         var allowed=player.getBoundingBox().inflate(MTConfig.SELECTIVE_DISPLACEMENT_RANGE);
         if(e==null || e2==null || !e.isAlive() || !e2.isAlive() || !allowed.intersects(e.getBoundingBox()) || !allowed.intersects(e2.getBoundingBox())) return;
         if (e != null && e2 != null && e != e2 &&''');p.write_text(s)
edit(src/'fabric/MahouNetworking.java','    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.TYPE,stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.SelectiveDisplacementPacket.handleAbilitySelectiveDisplacement(context.player(),packet.id1,packet.id2));''')
edit(src/'fabric/mixin/IncomingSpellDamageMixin.java','  stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingAttack','  stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationLivingDamage(entity,source,modified);\n  stepsword.mahoutsukai.effects.projection.ClarentEffect.clarentLivingAttack')
edit(src/'fabric/MahouFabric.java','            stepsword.mahoutsukai.effects.Kodoku.kodokuEntityDeath(entity,source);return true;','            stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationLivingDeath(entity);\n            stepsword.mahoutsukai.effects.Kodoku.kodokuEntityDeath(entity,source);return true;')
(src/'fabric/mixin/ReplicationEffectMixin.java').write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public abstract class ReplicationEffectMixin {
 @Inject(method="addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",at=@At("RETURN"))
 private void mahoutsukai$added(MobEffectInstance effect,Entity source,CallbackInfoReturnable<Boolean> ci) {
  if(ci.getReturnValue()) stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationPotionAdded((LivingEntity)(Object)this,effect);
 }
 @Inject(method="forceAddEffect",at=@At("TAIL"))
 private void mahoutsukai$forced(MobEffectInstance effect,Entity source,CallbackInfo ci) {
  stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect.damageReplicationPotionAdded((LivingEntity)(Object)this,effect);
 }
}
''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['mixins'].append('ReplicationEffectMixin');p.write_text(json.dumps(j,indent=2)+'\n')
edit(src/'advancements/ModTriggers.java',' public static void init()', ' public static final MahouTrigger BOOGIE_WOOGIE=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","boogie_woogie"),new MahouTrigger());\n public static void init()');shutil.copyfile(root/'recovered/data/mahoutsukai/advancement/boogie_woogie.json',res/'data/mahoutsukai/advancement/boogie_woogie.json')
# Only selectors whose gameplay exists in this port are exposed.
edit(src/'util/Utils.java','    public static void debug(String message)', '''    public enum MAHOU_SELECTION {SELECTIVE_DISPLACEMENT,SCROLL_RETRIBUTION,SCROLL_IMMUNITY_EXCHANGE,SCROLL_DAMAGE_REPLICATION,SCROLL_GEAS}
    public static boolean getPredicate(java.util.HashSet<MAHOU_SELECTION> set,Entity entity,Player player) {
        if(set.contains(MAHOU_SELECTION.SELECTIVE_DISPLACEMENT)) return entity!=player;
        if(set.contains(MAHOU_SELECTION.SCROLL_GEAS)) return entity!=player && !EffectUtil.inEntityBlacklist(entity,stepsword.mahoutsukai.config.MTConfig.GEAS_BLACKLIST) && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity);
        return !set.isEmpty() && entity!=player && entity instanceof net.minecraft.world.entity.LivingEntity;
    }
    public static boolean nearAllowed(java.util.HashSet<MAHOU_SELECTION> set) {return set.contains(MAHOU_SELECTION.SELECTIVE_DISPLACEMENT) || set.contains(MAHOU_SELECTION.SCROLL_RETRIBUTION) || set.contains(MAHOU_SELECTION.SCROLL_DAMAGE_REPLICATION);}
    public static void debug(String message)''')
# Exact original scalar defaults/ranges for newly connected consumers.
original=(orig/'config/MTConfig.java').read_text();p=src/'config/MTConfig.java';existing=set(re.findall(r'public static [\w.<>? ]+ (\w+) =',p.read_text()));needed={'SHOW_SELECTED_ENTITY','SELECTIVE_DISPLACEMENT_HOLD_TIME','SELECTIVE_DISPLACEMENT_RANGE'}
for rel in ['effects/mystic/DamageReplicationSpellEffect.java','item/spells/mystic/DamageReplicationSpellScroll.java','item/spells/secret/SelectiveDisplacementSpellScroll.java','networking/SelectiveDisplacementPacket.java']:needed.update(re.findall(r'MTConfig\.(\w+)',(src/rel).read_text()))
fields=[]
for name in sorted(needed-existing):
 match=re.search(r'   public static (int|double|boolean) '+name+r' = (.*?);',original,re.S);assert match,name
 typ,val=match.groups();default=re.search(r'"'+name+r'",\s*(-?[\d.E]+|true|false)(?:,|\s*\))',original)
 if default:val=default.group(1)
 fields.append(f'    public static {typ} {name} = {val};\n')
edit(p,'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES');s=p.read_text();rows=[]
for name in re.findall(r'public static (?:int|double) (\w+) =',s):
 m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',original);assert m,name
 _,low,high=m.groups();rows.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}})')
a=s.index('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES');b=s.index('    private static final com.google.gson.Gson JSON',a);p.write_text(s[:a]+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+',\n'.join(rows)+'\n    );\n'+s[b:])
print('Damage replication and selective displacement server mechanics connected; original client selection overlay next')
