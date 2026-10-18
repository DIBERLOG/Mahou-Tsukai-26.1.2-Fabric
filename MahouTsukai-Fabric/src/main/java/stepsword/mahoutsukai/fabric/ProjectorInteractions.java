package stepsword.mahoutsukai.fabric;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.Items;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.tile.*;
public final class ProjectorInteractions {
 public static InteractionResult use(Player player,Level level,InteractionHand hand,BlockHitResult hit){
  if(stepsword.mahoutsukai.potion.BoundPotion.boundRightClickBlock(player)||player.isSpectator()||!level.mayInteract(player,hit.getBlockPos()))return InteractionResult.PASS;
  var stack=player.getItemInHand(hand);if(!stack.is(Items.STICK))return InteractionResult.PASS;
  var tile=level.getBlockEntity(hit.getBlockPos());
  if(tile instanceof MahoujinProjectorTileEntity circle){if(!level.isClientSide()){
   if(!player.isDiscrete())stepsword.mahoutsukai.enchant.MahoujinProjectorEnchant.setProjectorNBT(stack,circle,stepsword.mahoutsukai.networking.ProjectorRightClickPacket.ARMOR_ACTION.REPLACE);
   else if(MahoujinProjectorTileEntity.canEdit(player,circle)){var saved=stack.get(DataComponents.PROJECTOR_ENCHANT_COMPONENT);if(saved!=null&&!saved.getPv().isEmpty()){circle.pv=stepsword.mahoutsukai.render.enchant.ProjectorValues.readFromTag(stepsword.mahoutsukai.render.enchant.ProjectorValues.writeToTag(saved.getPv().getFirst()));circle.sendUpdates();}}
  }return InteractionResult.SUCCESS;}
  if(tile instanceof FogProjectorTileEntity fog){if(!level.isClientSide()){
   if(!player.isDiscrete())stack.set(DataComponents.FOG_PROJECTOR_ENCHANT_COMPONENT,new stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant(stepsword.mahoutsukai.util.FogProjectorValues.readFromTag(stepsword.mahoutsukai.util.FogProjectorValues.writeToTag(fog.pv))));
   else{var saved=stack.get(DataComponents.FOG_PROJECTOR_ENCHANT_COMPONENT);if(saved!=null){fog.pv=stepsword.mahoutsukai.util.FogProjectorValues.readFromTag(stepsword.mahoutsukai.util.FogProjectorValues.writeToTag(saved.getPv()));fog.sendUpdates();}}
  }return InteractionResult.SUCCESS;}
  return InteractionResult.PASS;
 }
}
