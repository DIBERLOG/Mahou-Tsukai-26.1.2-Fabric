from pathlib import Path
r=Path('MahouTsukai-Fabric/src'); main=r/'main/java/stepsword/mahoutsukai'; client=r/'client/java/stepsword/mahoutsukai'
p=main/'networking/SweepAttackPacket.java'
s=Path('recovered/stepsword/mahoutsukai/networking/SweepAttackPacket.java').read_text(encoding='utf-8')
s=s.replace('import net.neoforged.neoforge.network.handling.IPayloadContext;','')
a=s.index('   public static void handle(');b=s.index('   public static void doAttack',a)
s=s[:a]+'''   public static final Type<SweepAttackPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","sweep_attack"));
   public static boolean canAttack(Player player,Entity target){
      if(player==null || !(target instanceof LivingEntity living) || !target.isAlive() || target==player || target.level()!=player.level() || player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND) || GeasEffect.hasGeas(player,living))return false;
      var item=player.getMainHandItem().getItem();
      if(!(item instanceof Caliburn) && !(item instanceof Morgan))return false;
      int radius=stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_SWEEP_RADIUS;
      return player.getBoundingBox().inflate(radius).intersects(target.getBoundingBox())
         && stepsword.mahoutsukai.util.EffectUtil.isLookingAtMe(target,player)
         && Math.abs(stepsword.mahoutsukai.util.EffectUtil.pointToLineDistance(target.position(),player.getEyePosition(1),player.getEyePosition(1).add(player.getLookAngle())))<stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_SWEEP_LOOK_RADIUS;
   }
''' +s[b:]
s=s.replace('if (entity != null && player != null) {','if (canAttack(player,entity) && !player.level().isClientSide()) {').replace('return MahouPackets.SWEEP_ATTACK_TYPE;','return TYPE;')
p.write_text(s,encoding='utf-8')
p=main/'fabric/MahouNetworking.java';s=p.read_text(encoding='utf-8');pos=s.index('        PayloadTypeRegistry.serverboundPlay()');s=s[:pos]+'''        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SweepAttackPacket.TYPE,stepsword.mahoutsukai.networking.SweepAttackPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SweepAttackPacket.TYPE,(packet,context)->{var p=context.player();if(!stepsword.mahoutsukai.fabric.RemoteViewActions.leave(p))stepsword.mahoutsukai.networking.SweepAttackPacket.doAttack(p,p.level().getEntity(packet.entityId));});
'''+s[pos:];p.write_text(s,encoding='utf-8')
p=client/'fabric/mixin/RemoteViewActionMixin.java';s=p.read_text(encoding='utf-8');s=s.replace('if(mahoutsukai$leaveRemote())ci.setReturnValue(false);','''if(mahoutsukai$leaveRemote()){ci.setReturnValue(false);return;}
  var mc=Minecraft.getInstance();var p=mc.player;
  if(p!=null && mc.hitResult!=null && mc.hitResult.getType()!=net.minecraft.world.phys.HitResult.Type.ENTITY){
   int radius=stepsword.mahoutsukai.config.MTConfig.POWER_CONSOLIDATION_SWEEP_RADIUS;
   for(var target:p.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class,p.getBoundingBox().inflate(radius)))
    if(stepsword.mahoutsukai.networking.SweepAttackPacket.canAttack(p,target))net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.SweepAttackPacket(target));
  }''');p.write_text(s,encoding='utf-8')
p=main/'fabric/mixin/RuleBreakerDurabilityMixin.java';s=p.read_text(encoding='utf-8');pos=s.index(' @Inject(');s=s[:pos]+''' @Inject(method="isDamageableItem",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$weaponBow(CallbackInfoReturnable<Boolean> result){if(((ItemStack)(Object)this).getItem() instanceof stepsword.mahoutsukai.item.spells.projection.WeaponProjectile.WeaponProjectileBow)result.setReturnValue(false);}
'''+s[pos:];p.write_text(s,encoding='utf-8')
