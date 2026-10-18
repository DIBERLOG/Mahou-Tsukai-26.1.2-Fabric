from remaining_port_helpers import *
import json
ritual('item/spells/familiar/PossessEntitySpellScroll.java','block/spells/familiar/PossessEntityMahoujin.java','tile/familiar/PossessEntityMahoujinTileEntity.java','possessEntityMahoujin')
copy('effects/familiar/PossessEntityEffect.java')
p=main/'effects/familiar/PossessEntityEffect.java';s=p.read_text().replace('net.minecraft.world.entity.monster.Creeper','net.minecraft.world.entity.monster.creeper.Creeper');s=s.replace('      if (target != null) {','      if (target instanceof LivingEntity && target!=player && player.level()==target.level() && target.isAlive()) {');s=s.replace('               mahou.setPossessing(true);','               mahou.setPossessing(true);\n               PlayerManaManager.updateClientMahou((ServerPlayer)player,mahou);');s=s.replace('import stepsword.mahoutsukai.util.Utils;','import stepsword.mahoutsukai.util.Utils;\nimport stepsword.mahoutsukai.mana.PlayerManaManager;');write(p,s)
for cls,id in [('PossessEntityPacket','possess_entity'),('DonePossessingPacket','done_possessing')]:
 p=copy('networking/'+cls+'.java');s=p.read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=re.sub(r'   public static void handle\(.*?\n   }\n','',s,flags=re.S);s=re.sub(r'return MahouPackets\.\w+;', 'return TYPE;',s);s=s.replace('implements CustomPacketPayload {',f'implements CustomPacketPayload {{\n   public static final Type<{cls}> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","{id}"));')
 for field in ['entityId','chunkx','chunkz']:s=s.replace('   int '+field,'   public int '+field)
 if cls=='DonePossessingPacket':s=s.replace('            mahou.setPossessing(false);','            mahou.setPossessing(false);\n            if(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(serverPlayer,mahou);')
 write(p,s)
p=main/'util/Utils.java';s=p.read_text().replace('enum MAHOU_SELECTION {','enum MAHOU_SELECTION {SCROLL_POSSESS_ENTITY,');s=s.replace('return set.contains(MAHOU_SELECTION.WEAPON) ||','return set.contains(MAHOU_SELECTION.SCROLL_POSSESS_ENTITY) || set.contains(MAHOU_SELECTION.WEAPON) ||');write(p,s)
p=main/'fabric/MahouNetworking.java';s=p.read_text();line='''        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.PossessEntityPacket.TYPE,stepsword.mahoutsukai.networking.PossessEntityPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.DonePossessingPacket.TYPE,stepsword.mahoutsukai.networking.DonePossessingPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.DonePossessingPacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.DonePossessingPacket.handleDonePossess(packet,context.player()));''';s=s.replace('    public static void register() {','    public static void register() {\n'+line) if line not in s else s;write(p,s)
p=client/'client/PossessEntityClientEffect.java';s=(orig/'client/PossessEntityClientEffect.java').read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);a=s.index('   public static boolean possessEntityDisallowedAction(PlayerInteractEvent');s=s[:a]+'}\n';s=s.replace('PacketHandler.sendToServer(','net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(').replace('k.release();','k.setDown(false);');write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text();line='''  ClientPlayNetworking.registerGlobalReceiver(PossessEntityPacket.TYPE,(packet,context)->{var entity=context.client().level.getEntity(packet.entityId);if(entity!=null){context.client().setCameraEntity(entity);stepsword.mahoutsukai.util.Utils.getPlayerMahou(context.player()).setPossessing(true);stepsword.mahoutsukai.client.PossessEntityClientEffect.possessionHold=entity;}});
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.player==null)return;if(stepsword.mahoutsukai.client.PossessEntityClientEffect.possessionHold!=null && stepsword.mahoutsukai.client.PossessEntityClientEffect.possessionHold.isRemoved()){mc.setCameraEntity(mc.player);ClientPlayNetworking.send(new DonePossessingPacket());stepsword.mahoutsukai.client.PossessEntityClientEffect.possessionHold=null;}stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityMouseInput();});
''';s=s.replace(' public static void init(){',' public static void init(){\n'+line) if line not in s else s;write(p,s)
p=client/'fabric/mixin/BoundInputMixin.java';s=p.read_text();line='  if(player!=null)stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityInputUpdate(this,player);';s=s.replace('  if(player!=null && stepsword',line+'\n  if(player!=null && stepsword',1) if line not in s else s;write(p,s)
# One action returns the player to the body; it must not also mine, attack or use an item.
write(client/'fabric/mixin/RemoteViewActionMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.Minecraft;
@Mixin(Minecraft.class)
public abstract class RemoteViewActionMixin {
 private boolean mahoutsukai$leaveRemote(){var player=Minecraft.getInstance().player;return player!=null && (stepsword.mahoutsukai.client.MentalDisplacementClientEffect.mentalDisplacementDisallowedAction(player) || stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityDisallowedAction(player));}
 @Inject(method="startAttack",at=@At("HEAD"),cancellable=true) private void mahoutsukai$attack(CallbackInfoReturnable<Boolean> ci){if(mahoutsukai$leaveRemote())ci.setReturnValue(false);}
 @Inject(method="startUseItem",at=@At("HEAD"),cancellable=true) private void mahoutsukai$use(CallbackInfo ci){if(mahoutsukai$leaveRemote())ci.cancel();}
}
''')
write(client/'fabric/mixin/RemoteViewHandMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.player.LocalPlayer;
import com.mojang.blaze3d.vertex.PoseStack;
@Mixin(ItemInHandRenderer.class)
public abstract class RemoteViewHandMixin {
 @Inject(method="renderHandsWithItems",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$hide(float partial,PoseStack pose,SubmitNodeCollector collector,LocalPlayer player,int light,CallbackInfo ci){if(player!=null && (stepsword.mahoutsukai.client.MentalDisplacementClientEffect.mentalDisplacementRenderHand() || stepsword.mahoutsukai.client.PossessEntityClientEffect.possessEntityRenderHand()))ci.cancel();}
}
''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['client']+=[x for x in ['RemoteViewActionMixin','RemoteViewHandMixin'] if x not in j['client']];write(p,json.dumps(j,indent=2)+'\n')
config()
