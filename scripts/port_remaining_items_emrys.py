from remaining_port_helpers import *
# Original offhand staff mechanics, with native damage/lightning callbacks.
p=copy('item/emrys/StaffEmrys.java');s=p.read_text(encoding='utf-8');s='\n'.join(l for l in s.splitlines() if not l.startswith('import net.neoforged.') and 'import stepsword.mahoutsukai.handlers.ServerHandler' not in l);s=s.replace('ServerHandler.tickCounter','player.level().getGameTime()');a=s.index('   public static void emrysHit(');b=s.index('   public static void turnStickIntoItem',a)
s=s[:a]+'''   public static void emrysHit(Player player,net.minecraft.world.entity.Entity target){if(!player.level().isClientSide() && player.getOffhandItem().getItem()==ModItems.emrysStaff.get() && target instanceof LivingEntity living)zap(living,false,player);}
   public int getRemainingUseDuration(ItemStack stack,LivingEntity entity,int charge){return getUseDuration(stack,entity)-charge;}
   public static float emrysLivingHurt(LivingEntity target,net.minecraft.world.damagesource.DamageSource source,float damage){
      if(source.is(DamageTypes.LIGHTNING_BOLT) && (target.getOffhandItem().getItem()==ModItems.emrysStaff.get() || target.getMainHandItem().getItem()==ModItems.emrysStaff.get()))return 0;
      if(source.getEntity() instanceof Player attacker && attacker.getOffhandItem().getItem()==ModItems.emrysStaff.get())zap(target,false,attacker);
      return damage;
   }
   public static void emrysStruckByLightning(net.minecraft.world.entity.Entity target){if(target instanceof Player player && !player.level().isClientSide() && !MTConfig.DISABLE_EMRYS && player.getOffhandItem().is(Items.STICK) && StrengtheningSpellEffect.getStrengthened(player.getOffhandItem())>0)turnStickIntoItem(player,new ItemStack(ModItems.emrysStaff.get()));}
'''+s[b:];s=s.replace('((MahouTrigger)ModTriggers.EMRYS.get())','ModTriggers.EMRYS');write(p,s)
entity('LIGHTNING','entity/mahoujin/LightningEntity.java','lightning_entity',.1,.1)
p=main/'entity/mahoujin/LightningEntity.java';s=p.read_text().replace('import stepsword.mahoutsukai.handlers.ServerHandler;','').replace('ServerHandler.tickCounter','this.level().getGameTime()').replace('this.target.hurt(this.caster.damageSources().lightningBolt(),','this.target.hurtServer((ServerLevel)this.level(),this.caster.damageSources().lightningBolt(),');write(p,s)
entity('LIGHTNING_BOLT','entity/MahouLightningBoltEntity.java','lightning_bolt_entity',0,0)
p=main/'entity/MahouLightningBoltEntity.java';s=p.read_text().replace('import net.neoforged.neoforge.event.EventHooks;','').replace('if (!EventHooks.onEntityStruckByLightning(entity, fake)) {','{');write(p,s)
copy('potion/ZapCooldownPotion.java')
insert(main/'item/ModItems.java','    static {','    public static final Supplier<Item> emrysStaff=register("staff_emrys",stepsword.mahoutsukai.item.emrys.StaffEmrys::new);\n')
insert(main/'potion/ModEffects.java',' public static void init()',' public static final Holder<MobEffect> ZAP_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","zap_cooldown"),new ZapCooldownPotion());\n')
insert(main/'advancements/ModTriggers.java',' public static void init()',' public static final MahouTrigger EMRYS=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","emrys"),new MahouTrigger());\n')
p=main/'fabric/mixin/IncomingSpellDamageMixin.java';s=p.read_text().replace('  float modified=','  original=stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysLivingHurt(entity,source,original);amount.set(original);\n  float modified=');write(p,s)
write(main/'fabric/mixin/EmrysLightningMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EmrysLightningMixin {
 @Inject(method="thunderHit",at=@At("HEAD")) private void mahoutsukai$emrys(net.minecraft.server.level.ServerLevel world,net.minecraft.world.entity.LightningBolt bolt,CallbackInfo ci){stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysStruckByLightning((net.minecraft.world.entity.Entity)(Object)this);}
}
''')
p=main/'fabric/MahouFabric.java';insert(p,'        stepsword.mahoutsukai.entity.ModEntityDataSerializers.init();','        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->{if(!stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) && (!(target instanceof net.minecraft.world.entity.LivingEntity living) || !stepsword.mahoutsukai.effects.secret.GeasEffect.hasGeas(player,living)))stepsword.mahoutsukai.item.emrys.StaffEmrys.emrysHit(player,target);return net.minecraft.world.InteractionResult.PASS;});\n')
insert(p,'        ServerLifecycleEvents.SERVER_STOPPED.register(server -> stepsword.mahoutsukai.mana.ActiveCircleConfig.clearActiveCircles());','        ServerLifecycleEvents.SERVER_STOPPED.register(server->stepsword.mahoutsukai.item.emrys.StaffEmrys.staffs.clear());\n')
import json
p=res/'mahoutsukai.fabric.mixins.json';d=json.loads(p.read_text());d['mixins'].append('EmrysLightningMixin');write(p,json.dumps(d,indent=2)+'\n')
config()
