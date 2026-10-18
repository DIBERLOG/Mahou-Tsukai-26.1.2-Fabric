package stepsword.mahoutsukai.fabric;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.util.Utils;
public final class RemoteViewActions {
 public static boolean leave(Player player){if(player.level().isClientSide())return false;var mahou=Utils.getPlayerMahou(player);boolean remote=false;if(mahou.isPossessing()){stepsword.mahoutsukai.networking.DonePossessingPacket.handleDonePossess(null,player);remote=true;}if(mahou.getMentalDisplacementPos()!=null){stepsword.mahoutsukai.networking.DoneMentalPacket.onMessage(null,player);remote=true;}return remote;}
}
