from remaining_port_helpers import *
p=main/'networking/NobuBeamCreatePacket.java';write(p,p.read_text(encoding='utf-8').replace('isImmuneToSpell(player,e)','isImmuneToSpell(player.level(),player.getUUID(),e)'))
# Native Nobu aiming, selection and fire input.
s=(orig/'client/NobuClientEffect.java').read_text(encoding='utf-8');a=s.index('   public static boolean nobuAimFireHandler');b=s.index('   public static void displayAutoTargetsCrosshairs',a);logic=s[a:b];logic=logic.replace('Keybinds.fireNobu.getKey().getValue()','0');a=logic.index('      if (0 == 0) {');b=logic.index('\n      if (button == 0',a);logic=logic[:a]+logic[b:];logic=logic.replace('PacketHandler.sendToServer(','net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(')
imports='''package stepsword.mahoutsukai.client;
import java.util.ArrayList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.nobu.Nobu;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.networking.*;
public final class NobuClientEffect {
 public static ArrayList<Entity> targets;
 public static boolean targetsHasChanged;
''';write(client/'client/NobuClientEffect.java',imports+logic+''' public static boolean isNobuTargetable(Level level,Player player,Entity target){return target instanceof LivingEntity && target!=player && target.isAlive() && (targets==null || !targets.contains(target)) && !stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity.isImmuneToSpellClient(player,target);}
}
''')
p=client/'fabric/mixin/RemoteViewActionMixin.java';s=p.read_text(encoding='utf-8').replace('  var mc=Minecraft.getInstance();var p=mc.player;','  var mc=Minecraft.getInstance();var p=mc.player;\n  if(p!=null && stepsword.mahoutsukai.client.NobuClientEffect.nobuAimFireHandler(p,0)){ci.setReturnValue(false);return;}').replace('if(mahoutsukai$leaveRemote())ci.cancel();','if(mahoutsukai$leaveRemote()){ci.cancel();return;}var p=Minecraft.getInstance().player;if(p!=null)stepsword.mahoutsukai.client.NobuClientEffect.nobuAimFireHandler(p,1);');write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',''' public static void init(){
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.player==null || mc.screen!=null)return;stepsword.mahoutsukai.client.NobuClientEffect.nobuAutoTargetMode(mc.player);if(mc.player.getMainHandItem().getItem() instanceof stepsword.mahoutsukai.item.nobu.Nobu){if(stepsword.mahoutsukai.Keybinds.gunUp.consumeClick())ClientPlayNetworking.send(new NobuBeamCreatePacket(java.util.List.of(),NobuBeamCreatePacket.NobuFirePacket.RAISE_ANGLE));if(stepsword.mahoutsukai.Keybinds.gunDown.consumeClick())ClientPlayNetworking.send(new NobuBeamCreatePacket(java.util.List.of(),NobuBeamCreatePacket.NobuFirePacket.LOWER_ANGLE));}});
''');write(p,s)
config()
