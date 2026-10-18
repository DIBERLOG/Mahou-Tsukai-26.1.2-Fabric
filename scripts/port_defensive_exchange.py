from pathlib import Path
import re, shutil, json
root=Path.cwd(); src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'; orig=root/'recovered/stepsword/mahoutsukai'; res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text(); assert a in s,(p,a);p.write_text(s.replace(a,b),encoding='utf-8')
families=[('exchange','DamageExchange','damageExchange','damage_exchange',['EMERALD','EMERALD','IRON']),('exchange','ImmunityExchange','immunityExchange','immunity_exchange',['EMERALD','EMERALD','EYE']),('displacement','ProtectiveDisplacement','protectiveDisplacement','protective_displacement',['ENDER','ENDER','IRON'])]
for group,cls,field,name,cats in families:
 for rel in [f'item/spells/{group}/{cls}SpellScroll.java',f'block/spells/{group}/{cls}Mahoujin.java',f'tile/{group}/{cls}MahoujinTileEntity.java']:
  text=(orig/rel).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable').replace(f'ModItems.{field}Scroll.get()',f'ModItems.REGISTERED.get("scroll_{name}")')
  p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text)
 edit(src/'item/ModItems.java','    static {',f'    static {{\n        register("scroll_{name}",stepsword.mahoutsukai.item.spells.{group}.{cls}SpellScroll::new);')
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {field}Mahoujin = plain("mahoujin_{name}",new stepsword.mahoutsukai.block.spells.{group}.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(src/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity>> {field} = register("mahoujin_{name}",stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init()')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {field} = makeRecipe('+','.join('POWDERED_'+c for c in cats)+f');\n      recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},true);')
 client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/MahouFabricClient.java'
 edit(client,'        stepsword.mahoutsukai.Keybinds.init();',f'        stepsword.mahoutsukai.Keybinds.init();\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{field}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 for folder,filename in [('items',f'scroll_{name}.json'),('models/item',f'scroll_{name}.json'),('blockstates',f'mahoujin_{name}.json')]:
  p=res/f'assets/mahoutsukai/{folder}/{filename}';p.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/{filename}',p)
 model=json.loads((res/f'assets/mahoutsukai/models/item/scroll_{name}.json').read_text())
 for tex in model.get('textures',{}).values():
  ns,path=tex.split(':');p=res/f'assets/{ns}/textures/{path}.png';p.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f'recovered/assets/{ns}/textures/{path}.png',p)
for rel in ['effects/exchange/DamageExchangeSpellEffect.java','effects/displacement/ProtectiveDisplacementSpellEffect.java','potion/ScrollPotion.java','potion/ImmunityExchangePotion.java']:
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);text=(orig/rel).read_text();text=re.sub(r'import net.neoforged[^\n]+\n','',text);p.write_text(text)
p=src/'effects/exchange/DamageExchangeSpellEffect.java'
edit(p,'public static void damageExchangeLivingHurt(LivingIncomingDamageEvent event) {','public static float damageExchangeLivingHurt(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.damagesource.DamageSource source, float amount) {')
s=p.read_text().replace('event.getEntity()','entity').replace('event.getSource()','source').replace('event.getAmount()','amount').replace('event.setAmount((float)MTConfig.DAMAGE_EXCHANGE_REDUCE_TO);','amount = (float)MTConfig.DAMAGE_EXCHANGE_REDUCE_TO;');s=s.replace('Utils.damageSourceAny(source, DamageTypes.FELL_OUT_OF_WORLD, DamageTypes.GENERIC_KILL)','(source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL))');s=s.rsplit('   }',1)[0]+'      return amount;\n   }\n}\n';p.write_text(s)
p=src/'potion/ImmunityExchangePotion.java';s=p.read_text();s=s[:s.index('   public static boolean immunityExchangeLivingHurt')]+'''   /** NaN means cancel the incoming damage event, as in the original highest-priority handler. */
   public static float immunityExchangeLivingHurt(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source, float amount) {
      if (!EffectUtil.hasBuff(entity,ModEffects.IMMUNITY_EXCHANGE) || source == entity.damageSources().fellOutOfWorld()) return amount;
      IMahou playerMahou = entity instanceof Player player ? Utils.getPlayerMahou(player) : null;
      ILivingMahou livingMahou = entity instanceof Player ? null : Utils.getLivingMahou(entity);
      if (playerMahou == null && livingMahou == null) return amount;
      String last=playerMahou != null ? playerMahou.getImmunityDamageType() : livingMahou.getImmunityDamageType();
      boolean cancel=last != null && !last.isEmpty() && last.equals(source.getMsgId());
      if(last != null && !last.isEmpty() && !cancel) amount *= 2;
      if(playerMahou != null) playerMahou.setImmunityDamageType(source.getMsgId());
      else livingMahou.setImmunityDamageType(source.getMsgId());
      if(entity instanceof ServerPlayer player) PlayerManaManager.updateClientMahou(player,playerMahou);
      return cancel ? Float.NaN : amount;
   }
}
''';p.write_text(s)
p=src/'effects/exchange/ImmunityExchangeSpellEffect.java';p.write_text('''package stepsword.mahoutsukai.effects.exchange;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;
public class ImmunityExchangeSpellEffect {
 public static LivingEntity getEnemy(Player player) { return (LivingEntity)Utils.selectStrictTarget(player,e->e instanceof LivingEntity); }
 public static boolean buffImmunityExchange(Player user) {
  LivingEntity target=getEnemy(user);
  EffectUtil.buff(target != null ? target : user,ModEffects.IMMUNITY_EXCHANGE,true,MTConfig.IMMUNITY_EXCHANGE_TIME);
  return true;
 }
}
''')
p=src/'effects/displacement/ProtectiveDisplacementSpellEffect.java';s=p.read_text().replace('protectiveDisplacementProjectileImpact(ProjectileImpactEvent event)','protectiveDisplacementProjectileImpact(net.minecraft.world.entity.projectile.Projectile projectile, net.minecraft.world.phys.HitResult hit)').replace('event.getEntity()','projectile').replace('event.getRayTraceResult()','hit');p.write_text(s)
p=src/'potion/ModEffects.java';edit(p,' public static void init()', ' public static final Holder<MobEffect> IMMUNITY_EXCHANGE = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","immunity_exchange"),new ImmunityExchangePotion());\n public static void init()')
p=src/'util/Utils.java';edit(p,'        double distance=stepsword.mahoutsukai.config.MTConfig.GLOBAL_LOOK_RANGE;','''        return selectStrictTarget(player,entity -> !EffectUtil.inEntityBlacklist(entity,stepsword.mahoutsukai.config.MTConfig.GEAS_BLACKLIST)
            && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity));
    }
    /** Original strict cursor branch, shared by Geas and Immunity Exchange. */
    public static Entity selectStrictTarget(Player player,java.util.function.Predicate<Entity> predicate) {
        double distance=stepsword.mahoutsukai.config.MTConfig.GLOBAL_LOOK_RANGE;''');edit(p,'''entity ->
            !EffectUtil.inEntityBlacklist(entity,stepsword.mahoutsukai.config.MTConfig.GEAS_BLACKLIST)
             && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity)''','predicate')
p=src/'config/MTConfig.java';edit(p,'    private static final com.google.gson.Gson JSON', '''    public static int DAMAGE_EXCHANGE_MANA_COST = 40;
    public static int DAMAGE_EXCHANGE_MANA_GAIN = 20;
    public static double DAMAGE_EXCHANGE_REDUCE_TO = 1.0;
    public static int DAMAGE_EXCHANGE_CAP = 5;
    public static int IMMUNITY_EXCHANGE_MANA_COST = 400;
    public static int IMMUNITY_EXCHANGE_TIME = 1200;
    public static int PROTECTIVE_DISPLACEMENT_MANA_COST = 50;
    private static final com.google.gson.Gson JSON''');edit(p,'case "WILLIAM_PERSONAL_MANA_FACTOR" -> 10000;','case "WILLIAM_PERSONAL_MANA_FACTOR" -> 10000;\n                        case "DAMAGE_EXCHANGE_CAP" -> 1000000000;')
p=src/'fabric/MahouFabric.java';s=p.read_text();a=s.index('        net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.ALLOW_DAMAGE');b=s.index('        stepsword.mahoutsukai.advancements.ModTriggers.init();',a);p.write_text(s[:a]+s[b:])
p=src/'fabric/mixin/IncomingSpellDamageMixin.java';p.write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
@Mixin(LivingEntity.class)
public abstract class IncomingSpellDamageMixin {
 @Inject(method="hurtServer",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"),cancellable=true)
 private void mahoutsukai$incoming(ServerLevel level,DamageSource source,float original,CallbackInfoReturnable<Boolean> ci,@Local(argsOnly=true) LocalFloatRef amount) {
  LivingEntity entity=(LivingEntity)(Object)this;
  float modified=stepsword.mahoutsukai.potion.ImmunityExchangePotion.immunityExchangeLivingHurt(entity,source,amount.get());
  if(Float.isNaN(modified) || stepsword.mahoutsukai.effects.secret.GeasEffect.geasLivingHurt(entity,source)) { ci.setReturnValue(false); return; }
  if(entity instanceof net.minecraft.world.entity.player.Player && modified>0 && !stepsword.mahoutsukai.config.MTConfig.BLEEDING_FROM_DAGGER_ONLY)
   stepsword.mahoutsukai.util.EffectUtil.buff(entity,stepsword.mahoutsukai.potion.ModEffects.BLEEDING,false,100);
  amount.set(stepsword.mahoutsukai.effects.exchange.DamageExchangeSpellEffect.damageExchangeLivingHurt(entity,source,modified));
 }
}
''')
(src/'fabric/mixin/ProtectiveProjectileMixin.java').write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Projectile.class)
public abstract class ProtectiveProjectileMixin {
 @Inject(method="hitTargetOrDeflectSelf",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$protect(HitResult hit,CallbackInfoReturnable<ProjectileDeflection> ci) {
  if(stepsword.mahoutsukai.effects.displacement.ProtectiveDisplacementSpellEffect.protectiveDisplacementProjectileImpact((Projectile)(Object)this,hit))
   ci.setReturnValue(ProjectileDeflection.NONE);
 }
}
''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['mixins']+=['IncomingSpellDamageMixin','ProtectiveProjectileMixin'];p.write_text(json.dumps(j,indent=2)+'\n')
# Reuse original effect icon.
p=res/'assets/mahoutsukai/textures/mob_effect/immunity_exchange.png';p.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/'recovered/assets/mahoutsukai/textures/mob_effect/immunity_exchange.png',p)
print('Adapted 3 original scroll/ritual/effect systems and native combat/projectile hooks')
