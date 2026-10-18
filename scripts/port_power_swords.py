from remaining_port_helpers import *
# Sword classes and native combat simulation used by all three weapon ritual families.
for rel in ['item/ModeItem.java','item/caliburn/Caliburn.java','item/morgan/Morgan.java','potion/CaliburnMorganCooldownPotion.java','potion/RagePotion.java','effects/projection/MorganEffect.java']:copy(rel)
for field,id,cls in [('caliburn','caliburn','item.caliburn.Caliburn'),('morgan','morgan','item.morgan.Morgan')]:insert(main/'item/ModItems.java','    static {',f'    public static final Supplier<Item> {field}=register("{id}",stepsword.mahoutsukai.{cls}::new);\n')
entity('SMITE','entity/mahoujin/SmiteEntity.java','smite_entity');entity('MORGAN_BALL','entity/mahoujin/MorganBallEntity.java','morgan_ball_entity')
for cls in ['SmiteEntity','MorganBallEntity']:
 p=main/('entity/mahoujin/'+cls+'.java');s=p.read_text().replace('import stepsword.mahoutsukai.MahouTsukaiMod;','');a=s.find('   @Nullable\n   public AABB getBoundingBox()');
 if a>=0:s=s[:a]+s[s.index('\n   }',a)+5:]
 if cls=='SmiteEntity':
  s=s.replace('public class SmiteEntity extends MahoujinEntity {','public class SmiteEntity extends MahoujinEntity {\n   public record Smoke(SmiteEntity entity,float y,float size,float r,float g,float b){}\n   public static java.util.function.Consumer<Smoke> clientSmoke=value->{};')
  s=s.replace('MahouTsukaiMod.proxy.smiteSmokeRing(this, this.maxlen - this.client_beam_len, ringSize, rgb[0], rgb[1], rgb[2]);','clientSmoke.accept(new Smoke(this,this.maxlen-this.client_beam_len,ringSize,rgb[0],rgb[1],rgb[2]));')
 write(p,s)
insert(main/'entity/ModEntityDataSerializers.java',' public static void init()', ''' public static final EntityDataSerializer<java.util.List<Integer>> INT_LIST=EntityDataSerializer.forValueType(net.minecraft.network.codec.ByteBufCodecs.VAR_INT.apply(net.minecraft.network.codec.ByteBufCodecs.list()));
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","int_list"),INT_LIST);}
''')
write(main/'util/FakeSkeleton.java','''package stepsword.mahoutsukai.util;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
/** Native LivingEntity.hurtServer runs real combat; only health storage is isolated from the world. */
public class FakeSkeleton extends Skeleton {
 public float h=5000000;
 public FakeSkeleton(Level world){super(EntityType.SKELETON,world);}
 @Override protected void actuallyHurt(ServerLevel world,DamageSource source,float damage){if(!isInvulnerable()){damage=getDamageAfterArmorAbsorb(source,damage);damage=getDamageAfterMagicAbsorb(source,damage);float absorbed=Math.min(getAbsorptionAmount(),damage);setAbsorptionAmount(Math.max(0,getAbsorptionAmount()-absorbed));h-=Math.max(0,damage-absorbed);}}
}
''')
p=main/'item/caliburn/Caliburn.java';s=p.read_text().replace('import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;','').replace('List<String> arr','List<? extends String> arr').replace('public static void upgradeDamageCap(AttackEntityEvent event)','public static void upgradeDamageCap()');write(p,s)
p=main/'effects/projection/MorganEffect.java';s=p.read_text().replace('import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;','').replace('morganLivingDeath(LivingDeathEvent event)','morganLivingDeath(LivingEntity target,net.minecraft.world.damagesource.DamageSource source)').replace('event.getSource()','source').replace('         LivingEntity target = event.getEntity();','');s=re.sub(r'\(\(MahouTrigger\)ModTriggers\.(\w+)\.get\(\)\)',r'((MahouTrigger)ModTriggers.\1)',s);write(p,s)
for field,id,cls in [('CM_COOLDOWN','cm_cooldown','CaliburnMorganCooldownPotion'),('RAGE','rage','RagePotion')]:insert(main/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {field}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),new {cls}());\n')
for name in ['MORGAN','BREADCRUMBS','SECRET_CALIBUR','SWORD_IN_THE_LAKE']:
 insert(main/'advancements/ModTriggers.java',' public static void init()',f' public static final MahouTrigger {name}=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","{name.lower()}"),new MahouTrigger());\n')
p=copy('networking/ItemAbilityPacket.java');s=p.read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'import stepsword.mahoutsukai.(entity.mahoujin.ReplicaTeleportMahoujinEntity|item.replica.Replica);\n','',s);a=s.index('   public static void handle(ItemAbilityPacket');b=s.index('   public static void handleAbilityMorgan',a);s=s[:a]+'''   public static void handleNative(ItemAbilityPacket packet,ServerPlayer player){if(stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) || player.isSpectator())return;if(packet.ability==ABILITY.CALIBURN)Caliburn.handleAbilityCaliburn(player);else if(packet.ability==ABILITY.MORGAN)handleAbilityMorgan(player);}
'''+s[b:];a=s.index('   public static void handleAbilityReplica');b=s.index('   public static void cooldownMessage',a);s=s[:a]+s[b:];s=s.replace('      if (n < ItemAbilityPacket.ABILITY.values().length) {','      if (n >=0 && n < ItemAbilityPacket.ABILITY.values().length) {');s=s.replace('implements CustomPacketPayload {','implements CustomPacketPayload {\n   public static final Type<ItemAbilityPacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","item_ability"));').replace('return MahouPackets.ITEM_ABILITY_TYPE;','return TYPE;');write(p,s)
p=copy('networking/RagePacket.java');s=p.read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'   public static void handle\(.*?\n   }\n','',s,flags=re.S);s=s.replace('implements CustomPacketPayload {','implements CustomPacketPayload {\n   public static final Type<RagePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","rage"));').replace('return MahouPackets.RAGE_TYPE;','return TYPE;').replace('   int entityids;','   public int entityids;').replace('   boolean rage;','   public boolean rage;');write(p,s)
p=main/'fabric/MahouNetworking.java';s=p.read_text();line='''        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.RagePacket.TYPE,stepsword.mahoutsukai.networking.RagePacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ItemAbilityPacket.TYPE,stepsword.mahoutsukai.networking.ItemAbilityPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ItemAbilityPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.ItemAbilityPacket.handleNative(packet,context.player()));''';s=s.replace('    public static void register() {','    public static void register() {\n'+line) if line not in s else s;write(p,s)
append(main/'util/Utils.java',''' public static net.minecraft.resources.Identifier getRegistryKey(Entity entity,Level world){return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());}
''')
p=res/'mahoutsukai.accesswidener';s=p.read_text();line='accessible field net/minecraft/world/entity/ai/attributes/RangedAttribute maxValue D';write(p,s+'\n'+line+'\n') if line not in s else None
# Explicit native hooks; sword stat changes share the existing GrowSwordItem modifier path.
p=main/'fabric/MahouFabric.java';s=p.read_text();line='            stepsword.mahoutsukai.effects.projection.MorganEffect.morganLivingDeath(entity,source);';s=s.replace('            stepsword.mahoutsukai.effects.Kodoku.kodokuEntityDeath',line+'\n            stepsword.mahoutsukai.effects.Kodoku.kodokuEntityDeath') if line not in s else s
line='                stepsword.mahoutsukai.effects.projection.MorganEffect.secretCalibur(player);';s=s.replace('                if(tick%100==0)',line+'\n                if(tick%100==0)') if line not in s else s
line='        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->{stepsword.mahoutsukai.item.caliburn.Caliburn.upgradeDamageCap();return net.minecraft.world.InteractionResult.PASS;});';s=s.replace('        MahouNetworking.register();',line+'\n        MahouNetworking.register();') if line not in s else s;write(p,s)
config()
