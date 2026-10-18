"""Historical one-shot Presence Concealment/native dual knives adapter."""
from pathlib import Path
import re,json,shutil
base=Path('MahouTsukai-Fabric/src');orig=Path('recovered/stepsword/mahoutsukai');main=base/'main/java/stepsword/mahoutsukai';client=base/'client/java/stepsword/mahoutsukai';res=base/'main/resources'
def put(rel,s,cl=False):
 p=(client if cl else main)/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8');return p
def copy(rel):return put(rel,(orig/rel).read_text(encoding='utf-8').replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'))
def edit(p,a,b):
 s=p.read_text(encoding='utf-8');assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
for rel in ['item/spells/secret/ripper/TheRipper.java','item/spells/secret/PresenceConcealmentSpellScroll.java','tile/secret/PresenceConcealmentMahoujinTileEntity.java','block/spells/secret/PresenceConcealmentMahoujin.java','potion/RipperInvisPotion.java','potion/RipperCooldownPotion.java','potion/RipperInvisCooldownPotion.java','networking/FogPacket.java','networking/RipperInvisiblePacket.java','networking/RipperUseEntityPacket.java']:copy(rel)
p=main/'item/spells/secret/ripper/TheRipper.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s)
a=s.index('   public static void adjustAttributeModifiers(');b=s.index('   public InteractionResult use(',a)
s=s[:a]+'''   public static net.minecraft.world.item.component.ItemAttributeModifiers attributes(ItemStack stack,net.minecraft.world.item.component.ItemAttributeModifiers original){
      if(!(stack.getItem() instanceof TheRipper))return original;
      var builder=net.minecraft.world.item.component.ItemAttributeModifiers.builder();
      for(var entry:original.modifiers())if(!entry.modifier().id().equals(BASE_ATTACK_DAMAGE_ID) && !entry.modifier().id().equals(BASE_ATTACK_SPEED_ID))builder.add(entry.attribute(),entry.modifier(),entry.slot());
      builder.add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,MTConfig.RIPPER_DAMAGE,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,MTConfig.RIPPER_DAMAGE,Operation.ADD_VALUE),EquipmentSlotGroup.OFFHAND);
      builder.add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,MTConfig.RIPPER_SPEED,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,MTConfig.RIPPER_SPEED,Operation.ADD_VALUE),EquipmentSlotGroup.OFFHAND);
      builder.add(Attributes.MOVEMENT_SPEED,new AttributeModifier(Identifier.fromNamespaceAndPath("mahoutsukai","ripper_move_speed"),MTConfig.RIPPER_MOVEMENT_SPEED,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
      return builder.build();
   }
'''+s[b:]
s=s.replace('public static void ripperOnHit(LivingIncomingDamageEvent event) {','public static float ripperOnHit(LivingEntity victim,DamageSource source,float amount) {').replace('event.getSource()','source').replace('event.getEntity()','victim').replace('event.getAmount()','amount').replace('event.setAmount((float)d);','amount=(float)d;')
s=s.replace('   public static void ripperLivingTick(', '   public static void ripperLivingTick(')
a=s.index('   public static float ripperOnHit(');b=s.index('   public static void ripperLivingTick(',a);section=s[a:b];i=section.rfind('   }');section=section[:i]+'      return amount;\n'+section[i:];s=s[:a]+section+s[b:]
s=s.replace('CommonHooks.onPlayerAttackTarget(player, target)','net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.invoker().interact(player,player.level(),InteractionHand.MAIN_HAND,target,null)!=InteractionResult.FAIL')
s=s.replace('                  baseDamage += attackingItemStack.getItem().getAttackDamageBonus(target, baseDamage, damageSource);','')
s=s.replace('                  CriticalHitEvent critEvent = CommonHooks.fireCriticalHit(player, target, criticalAttack, criticalAttack ? 1.5F : 1.0F);\n                  criticalAttack = critEvent.isCriticalHit();','').replace('critEvent.getDamageMultiplier()','1.5F').replace('critEvent.isCriticalHit() && critEvent.disableSweep()','criticalAttack')
s=s.replace('                  SweepAttackEvent sweepEvent = CommonHooks.fireSweepAttack(player, target, sweepAttack);\n                  sweepAttack = sweepEvent.isSweeping();','').replace('                        AABB sweepHitBox = attackingItemStack.getSweepHitBox(player, target);\n                        player.doSweepAttack(target, baseDamage, damageSource, attackStrengthScale, sweepHitBox);','                        player.doSweepAttack(target, baseDamage, damageSource, attackStrengthScale);')
p.write_text(s,encoding='utf-8')
p=main/'potion/RipperInvisPotion.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s);a=s.index('   public static boolean presenceConcealmentNoSound(');s=s[:a]+'''   public static boolean presenceConcealmentNoSound(Entity source){return source instanceof LivingEntity living && EffectUtil.hasBuff(living,ModEffects.RIPPER_INVISIBILITY);}
}
''';s=s.replace('      notifyNotInvisible(livingEntity);','      if(livingEntity.level().isClientSide())return;\n      notifyNotInvisible(livingEntity);',1).replace('      notifyIfInvisible(livingEntity);','      if(livingEntity.level().isClientSide())return;\n      notifyIfInvisible(livingEntity);',1);p.write_text(s,encoding='utf-8')
p=main/'potion/RipperInvisCooldownPotion.java';s=p.read_text(encoding='utf-8').replace('if (livingEntity instanceof Player player','if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player');p.write_text(s,encoding='utf-8')
for name,id in [('FogPacket','fog'),('RipperInvisiblePacket','ripper_invisible')]:
 p=main/f'networking/{name}.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'   public static void handle\(.*?\n   }\n','',s,flags=re.S);s=s.replace('implements CustomPacketPayload {',f'implements CustomPacketPayload {{\n   public static final Type<{name}> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","{id}"));');s=re.sub(r'return MahouPackets\.\w+;', 'return TYPE;',s);s=re.sub(r'(?m)^   (int|boolean) (\w+);',r'   public \1 \2;',s);p.write_text(s,encoding='utf-8')
p=main/'networking/RipperUseEntityPacket.java';s=p.read_text(encoding='utf-8');s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=s.replace('implements CustomPacketPayload {','implements CustomPacketPayload {\n   public static final Type<RipperUseEntityPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","ripper_use_entity"));');s=s.replace('   InteractionHand hand','   public InteractionHand hand').replace('   int id','   public int id');a=s.index('   public static void handle(');b=s.index('   public Type<RipperUseEntityPacket>',a);s=s[:a]+'''   public static boolean handle(ServerPlayer player,int id,InteractionHand hand){
      if(!(player.getMainHandItem().getItem() instanceof TheRipper) || !(player.getOffhandItem().getItem() instanceof TheRipper) || player.isSpectator() || stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player))return false;
      Entity target=player.level().getEntity(id);
      if(!(target instanceof LivingEntity living) || !living.isAlive() || target==player || stepsword.mahoutsukai.effects.secret.GeasEffect.hasGeas(player,living))return false;
      double max=player.hasLineOfSight(target)?36:9;
      if(player.distanceToSqr(target)>=max)return false;
      player.resetLastActionTime();TheRipper.attack(player,target);return true;
   }

'''+s[b:];s=s.replace('MahouPackets.RIPPER_USE_ENTITY_TYPE','TYPE');p.write_text(s,encoding='utf-8')
# Exact original registry names and acquisition.
edit(main/'item/ModItems.java','    static {','    public static final Supplier<Item> theripper=register("theripper",stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper::new);\n    public static final Supplier<Item> presenceConcealmentScroll=register("scroll_presence_concealment",stepsword.mahoutsukai.item.spells.secret.PresenceConcealmentSpellScroll::new);\n    static {')
edit(main/'block/ModBlocks.java','    private static Supplier<Block> plain','    public static final Supplier<Block> presenceConcealmentMahoujin=plain("mahoujin_presence_concealment",new stepsword.mahoutsukai.block.spells.secret.PresenceConcealmentMahoujin(),false);\n    private static Supplier<Block> plain')
edit(main/'tile/ModTileEntities.java','    public static void init()','    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.secret.PresenceConcealmentMahoujinTileEntity>> presenceConcealment=register("mahoujin_presence_concealment",stepsword.mahoutsukai.tile.secret.PresenceConcealmentMahoujinTileEntity::new,ModBlocks.presenceConcealmentMahoujin.get());\n    public static void init()')
text=(orig/'block/spells/MahoujinRecipeRegistrar.java').read_text(encoding='utf-8');recipe=re.search(r'recipes.put\((\w+), \(BlockBase\)ModBlocks\.presenceConcealmentMahoujin',text)[1];cats=re.search(r'UnorderedList '+recipe+r' = makeRecipe\(([^)]+)\)',text)[1];edit(main/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {recipe}=makeRecipe({cats});recipes.put({recipe},(BlockBase)ModBlocks.presenceConcealmentMahoujin.get());needsCloth.put({recipe},true);')
for name,cls,id in [('RIPPER_INVISIBILITY','RipperInvisPotion','ripper_invis'),('RIPPER_INVIS_COOLDOWN','RipperInvisCooldownPotion','ripper_invis_cooldown'),('RIPPER_COOLDOWN','RipperCooldownPotion','ripper_cooldown')]:edit(main/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {name}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),new {cls}());\n public static void init()')
edit(main/'fabric/mixin/StrengthenedItemMixin.java','stepsword.mahoutsukai.item.GrowSwordItem.attributes((ItemStack)(Object)this,modifiers)','stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.attributes((ItemStack)(Object)this,stepsword.mahoutsukai.item.GrowSwordItem.attributes((ItemStack)(Object)this,modifiers))')
edit(main/'fabric/mixin/RuleBreakerDurabilityMixin.java','  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent)','  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper)result.setReturnValue(stepsword.mahoutsukai.config.MTConfig.RIPPER_DURABILITY);\n  else if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.clarent.Clarent)')
# The original incoming-damage event ran before the vanilla invulnerability cooldown test.
edit(main/'fabric/mixin/LivingMahouMixin.java','        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick','        if(!((LivingEntity)(Object)this).level().isClientSide()){stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.ripperLivingTick((LivingEntity)(Object)this);stepsword.mahoutsukai.potion.RipperInvisPotion.presenceConcealmentLivingTick((LivingEntity)(Object)this);}\n        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick')
# Native variable modification at entry preserves original backstab cooldown reset and damage order.
put('fabric/mixin/RipperDamageMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
@Mixin(LivingEntity.class)
public abstract class RipperDamageMixin {
 @ModifyVariable(method="hurtServer",at=@At("HEAD"),argsOnly=true)
 private float mahoutsukai$backstab(float amount,ServerLevel world,DamageSource source,float incoming){return stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.ripperOnHit((LivingEntity)(Object)this,source,amount);}
}
''')
put('fabric/mixin/RipperGameEventMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
@Mixin(ServerLevel.class)
public abstract class RipperGameEventMixin {
 @Inject(method="gameEvent",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$silence(Holder<GameEvent> event,Vec3 position,GameEvent.Context context,CallbackInfo ci){if(stepsword.mahoutsukai.potion.RipperInvisPotion.presenceConcealmentNoSound(context.sourceEntity()))ci.cancel();}
}
''')
edit(main/'fabric/MahouFabric.java','            if(entity instanceof net.minecraft.world.entity.LivingEntity reverted','            if(entity instanceof net.minecraft.world.entity.LivingEntity invisible && invisible.hasEffect(stepsword.mahoutsukai.potion.ModEffects.RIPPER_INVISIBILITY))stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.RipperInvisiblePacket(invisible.getId(),true));\n            if(entity instanceof net.minecraft.world.entity.LivingEntity reverted')
edit(main/'fabric/MahouNetworking.java','    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.RipperInvisiblePacket.TYPE,stepsword.mahoutsukai.networking.RipperInvisiblePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.FogPacket.TYPE,stepsword.mahoutsukai.networking.FogPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.RipperUseEntityPacket.TYPE,stepsword.mahoutsukai.networking.RipperUseEntityPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.RipperUseEntityPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.RipperUseEntityPacket.handle(context.player(),packet.id,packet.hand));''')
# Native attack implementation uses the original vanilla default critical/sweep decisions.
aw=res/'mahoutsukai.accesswidener';s=aw.read_text(encoding='utf-8');methods={'createAttackSource':'(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/damagesource/DamageSource;','cannotAttack':'(Lnet/minecraft/world/entity/Entity;)Z','deflectProjectile':'(Lnet/minecraft/world/entity/Entity;)Z','canCriticalAttack':'(Lnet/minecraft/world/entity/Entity;)Z','isSweepAttack':'(ZZZ)Z','attackVisualEffects':'(Lnet/minecraft/world/entity/Entity;ZZZZF)V','damageStatsAndHearts':'(Lnet/minecraft/world/entity/Entity;F)V','itemAttackInteraction':'(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/damagesource/DamageSource;Z)V','doSweepAttack':'(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;F)V','getEnchantedDamage':'(Lnet/minecraft/world/entity/Entity;FLnet/minecraft/world/damagesource/DamageSource;)F'}
for name,desc in methods.items():s+=f'accessible method net/minecraft/world/entity/player/Player {name} {desc}\n'
s+='accessible field net/minecraft/world/entity/LivingEntity autoSpinAttackDmg F\n';aw.write_text(s,encoding='utf-8')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text(encoding='utf-8'));j['mixins']+=['RipperDamageMixin','RipperGameEventMixin'];p.write_text(json.dumps(j,indent=2)+'\n',encoding='utf-8')
# Config defaults and bounds from the original builder, including false/negative values.
config=(orig/'config/MTConfig.java').read_text(encoding='utf-8');p=main/'config/MTConfig.java';s=p.read_text(encoding='utf-8');needed=set()
for parent in [main]:
 for f in parent.rglob('*.java'):
  if f.name!='MTConfig.java':needed.update(re.findall(r'MTConfig\.(\w+)',f.read_text(encoding='utf-8')))
fields=[];ranges=[]
for name in sorted(needed):
 if re.search(r'public static [^\n]+\b'+name+r'\s*=',s) or name=='load':continue
 typ=re.search(r'   public static (int|double|boolean) '+name+r' =',config)
 if not typ:continue
 if typ[1]=='boolean':
  m=re.search('"'+name+r'",\s*(true|false)',config);assert m,name;fields.append(f'    public static boolean {name}={m[1]};\n');continue
 m=re.search('"'+name+r'",\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)\s*,\s*([\d.Ee+-]+)',config);assert m,name;val,low,high=m.groups();fields.append(f'    public static {typ[1]} {name}={val};\n');ranges.append(f'        java.util.Map.entry("{name}",new double[]{{{low},{high}}}),\n')
s=s.replace('    private static final java.util.Map<String,double[]> ORIGINAL_RANGES',''.join(fields)+'    private static final java.util.Map<String,double[]> ORIGINAL_RANGES').replace('ORIGINAL_RANGES = java.util.Map.ofEntries(','ORIGINAL_RANGES = java.util.Map.ofEntries(\n'+''.join(ranges));p.write_text(s,encoding='utf-8')
for name in ['scroll_presence_concealment','theripper']:
 for f in [f'assets/mahoutsukai/items/{name}.json',f'assets/mahoutsukai/models/item/{name}.json']:
  target=res/f;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(Path('recovered')/f,target)
 # models may use a different sprite name; copy all explicit local texture references.
 model=json.loads((res/f'assets/mahoutsukai/models/item/{name}.json').read_text(encoding='utf-8'))
 for texture in model.get('textures',{}).values():
  if texture.startswith('mahoutsukai:'):
   f='assets/mahoutsukai/textures/'+texture.split(':')[1]+'.png';target=res/f;target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(Path('recovered')/f,target)
f='assets/mahoutsukai/blockstates/mahoujin_presence_concealment.json';shutil.copyfile(Path('recovered')/f,res/f)
for name in ['ripper_invis','ripper_invis_cooldown','ripper_cooldown']:
 f=f'assets/mahoutsukai/textures/mob_effect/{name}.png'
 if (Path('recovered')/f).exists():shutil.copyfile(Path('recovered')/f,res/f)
