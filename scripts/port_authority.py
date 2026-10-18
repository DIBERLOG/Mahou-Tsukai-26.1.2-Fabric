from remaining_port_helpers import *
import json
ritual('item/spells/mystic/BorrowedAuthoritySpellScroll.java','block/spells/mystic/BorrowedAuthorityMahoujin.java','tile/mystic/BorrowedAuthorityMahoujinTileEntity.java','borrowedAuthorityMahoujin')
for cls,id in [('BorrowedAuthorityPotion','borrowed_authority'),('BorrowedAuthorityCooldownPotion','ba_cooldown'),('AuthorityJumpPotion','authority_jump'),('WallStickPotion','wall_stick')]:
 copy('potion/'+cls+'.java');field={'BorrowedAuthorityPotion':'BORROWED_AUTHORITY','BorrowedAuthorityCooldownPotion':'BA_COOLDOWN','AuthorityJumpPotion':'AUTHORITY_JUMP','WallStickPotion':'WALL_STICK'}[cls]
 insert(main/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {field}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),new {cls}());\n')
 f=Path('recovered/assets/mahoutsukai/textures/mob_effect')/(id+'.png')
 if f.exists():shutil.copyfile(f,res/'assets/mahoutsukai/textures/mob_effect'/f.name)
p=main/'potion/BorrowedAuthorityPotion.java';s=p.read_text().replace('bs.getBlock().getExplosionResistance(bs, entity.level(), p, null)','bs.getBlock().getExplosionResistance()').replace('bs.getBlock().getExplosionResistance(bs, world, p, null)','bs.getBlock().getExplosionResistance()').replace('target.hurt(player.damageSources().playerAttack(player), (float)damage);','target.hurtServer((ServerLevel)player.level(),player.damageSources().playerAttack(player), (float)damage);').replace('player.hurt(player.damageSources().playerAttack(player), (float)damageToSelf);','player.hurtServer((ServerLevel)player.level(),player.damageSources().playerAttack(player), (float)damageToSelf);')
s=re.sub(r'EffectUtil.tryFakeExplosion\(\s*EffectUtil.createFakeExplosion\([^\n]+\),\s*affected,\s*(entity|target),\s*null,\s*"borrowed_authority",\s*MTConfig.AUTHORITY_DROPS_BLOCKS,\s*new ArrayList<>\(\)\s*\);',r'breakBlocks(affected,\1);',s)
s=s.replace('&& target != null) {','&& target instanceof LivingEntity && target != player && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpell(player.level(),player.getUUID(),target)) {')
# Native callback guards prevent client->server echo on tracking updates.
s=s.replace('      notifyNotAuthority(livingEntity);','      if(!livingEntity.level().isClientSide()){livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedauthority);notifyNotAuthority(livingEntity);}')
s=s.replace('      notifyIfAuthority(livingEntity);','      if(!livingEntity.level().isClientSide())notifyIfAuthority(livingEntity);')
# Same-position attacks cannot produce NaN velocities.
s=s.replace('      Vec3 motion = entity.getDeltaMovement();','      if(f<1.0E-6F)return;\n      Vec3 motion = entity.getDeltaMovement();')
write(p,s)
append(p,''' private static void breakBlocks(List<BlockPos> affected,Entity target){
  Player owner=target instanceof Player p?p:target instanceof LivingEntity living?living.getLastHurtByPlayer():null;
  EffectUtil.applySpellBlockChanges(affected,target,owner,MTConfig.AUTHORITY_DROPS_BLOCKS,new ArrayList<>(),target.position(),-1,(state,e,drop,pos)->{if(state.isAir() || e.level().getBlockEntity(pos)!=null)return false;if(drop)net.minecraft.world.level.block.Block.dropResources(state,e.level(),pos);return e.level().removeBlock(pos,false);});
 }
''')
append(main/'util/EffectUtil.java',' public static boolean noMoreSoaring(LivingEntity player){return player.onGround() || player.isFallFlying() || player.isUnderWater();}\n')
# Original payload formats with native transport. Security stays on the server.
for cls,id,direction in [('AuthorityPacket','authority','clientbound'),('AuthoritySmokeRingPacket','authority_smoke_ring','clientbound'),('AuthorityJumpPacket','authority_jump','serverbound'),('AuthorityStickPacket','authority_stick','serverbound'),('AuthorityAttackPacket','authority_attack','serverbound')]:
 p=copy('networking/'+cls+'.java');s=p.read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'   public static void handle\(.*?\n   }\n','',s,flags=re.S);s=re.sub(r'return MahouPackets\.\w+;', 'return TYPE;',s)
 anchor='implements CustomPacketPayload {';s=s.replace(anchor,anchor+f'\n   public static final Type<{cls}> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","{id}"));')
 for typ,name in [('int','entityids'),('boolean','authority')]:s=s.replace('   '+typ+' '+name+';','   public '+typ+' '+name+';')
 if cls=='AuthorityAttackPacket':
  s=s.replace('         && entity.isAlive())','         && entity.isAlive() && !player.isSpectator() && !stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) && player.distanceToSqr(entity)<16 && player.hasLineOfSight(entity) && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpell(player.level(),player.getUUID(),entity))')
  s=s.replace('         player.attack(entity);','         stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityAttackFromPlayer(player,entity);player.attack(entity);')
 if cls=='AuthorityJumpPacket':
  s=s.replace('      if (EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY) && WallStickPotion.onWallOrGround(player)) {','      if (packet.held>0 && packet.held<=12000 && Double.isFinite(packet.lookX) && Double.isFinite(packet.lookY) && Double.isFinite(packet.lookZ) && EffectUtil.hasBuff(player, ModEffects.BORROWED_AUTHORITY) && !stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) && WallStickPotion.onWallOrGround(player)) {')
  s=s.replace('         boop(player, Math.min((float)packet.held / 4.0F, 5.0F), -packet.lookX, -packet.lookY, -packet.lookZ);','         var look=player.getLookAngle();boop(player, Math.min((float)packet.held / 4.0F, 5.0F), -look.x, -look.y, -look.z);')
 write(p,s)
 p=main/'fabric/MahouNetworking.java';text=p.read_text();line=f'        PayloadTypeRegistry.{direction}Play().register(stepsword.mahoutsukai.networking.{cls}.TYPE,stepsword.mahoutsukai.networking.{cls}.STREAM_CODEC);';text=text.replace('    public static void register() {','    public static void register() {\n'+line) if line not in text else text
 if direction=='serverbound':
  method={'AuthorityJumpPacket':'authorityJump(packet,context.player())','AuthorityStickPacket':'authorityStick(packet,context.player())','AuthorityAttackPacket':'doAttack(context.player(),context.player().level().getEntity(packet.entityId))'}[cls];line=f'        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.{cls}.TYPE,(packet,context)->stepsword.mahoutsukai.networking.{cls}.{method});';text=text.replace('    public static void register() {','    public static void register() {\n'+line) if line not in text else text
 write(p,text)
p=main/'fabric/mixin/LivingMahouMixin.java';s=p.read_text();line='        stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityLivingUpdate((LivingEntity)(Object)this);stepsword.mahoutsukai.potion.AuthorityJumpPotion.authorityJumpLivingTick((LivingEntity)(Object)this);stepsword.mahoutsukai.potion.WallStickPotion.wallStickLivingTick((LivingEntity)(Object)this);';s=s.replace('        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick',line+'\n        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick') if line not in s else s;write(p,s)
p=main/'fabric/MahouFabric.java';s=p.read_text();line='                stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityPlayerTick(player);';s=s.replace('                if(tick%100==0)',line+'\n                if(tick%100==0)') if line not in s else s
# Run after existing permission callbacks, preserving original attack-event ordering.
line='        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->{stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityAttackFromPlayer(player,target);return net.minecraft.world.InteractionResult.PASS;});'
s=s.replace('        MahouNetworking.register();',line+'\n        MahouNetworking.register();') if line not in s else s
line='            if(entity instanceof net.minecraft.world.entity.LivingEntity authority && authority.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BORROWED_AUTHORITY))stepsword.mahoutsukai.networking.PacketHandler.sendTo(player,new stepsword.mahoutsukai.networking.AuthorityPacket(authority.getId(),true));'
s=s.replace('            if(entity instanceof net.minecraft.world.entity.LivingEntity invisible',line+'\n            if(entity instanceof net.minecraft.world.entity.LivingEntity invisible') if line not in s else s;write(p,s)
config()
