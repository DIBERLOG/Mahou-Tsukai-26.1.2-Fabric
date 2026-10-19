package stepsword.mahoutsukai.item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
/** Unbound, single-use dungeon loot; increases persistent maximum and current mana equally. */
public final class ManaCapacityScroll extends ItemBase {
 private final int amount;
 public ManaCapacityScroll(int amount){super(new net.minecraft.world.item.Item.Properties().stacksTo(16).rarity(amount>=1000?net.minecraft.world.item.Rarity.EPIC:amount>=250?net.minecraft.world.item.Rarity.RARE:net.minecraft.world.item.Rarity.UNCOMMON));this.amount=amount;}
 public int capacity(){return amount;}
 @Override public InteractionResult use(Level world,Player player,InteractionHand hand){
  if(world.isClientSide())return InteractionResult.SUCCESS;
  var state=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player);int old=state.getMaxMana();
  int next=(int)Math.min(stepsword.mahoutsukai.fabric.ManaConfig.current.MAX_MANA_CAP(),(long)old+amount);
  if(next<=old){player.sendOverlayMessage(Component.translatable("mahoutsukai.progression.cap"));return InteractionResult.FAIL;}
  state.setHasMagic(true);state.setMaxMana(next);state.setStoredMana((int)Math.min(next,(long)state.getStoredMana()+next-old));
  if(!player.isCreative())player.getItemInHand(hand).shrink(1);
  stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou((net.minecraft.server.level.ServerPlayer)player,state);
  stepsword.mahoutsukai.fabric.SpellCasting.completed(player,this);
  player.sendOverlayMessage(Component.translatable("mahoutsukai.progression.gained",next-old,stepsword.mahoutsukai.fabric.MageProgression.level(next)));
  return InteractionResult.SUCCESS;
 }
 @Override public void appendHoverText(ItemStack stack,net.minecraft.world.item.Item.TooltipContext context,net.minecraft.world.item.component.TooltipDisplay display,java.util.function.Consumer<Component> text,net.minecraft.world.item.TooltipFlag flag){super.appendHoverText(stack,context,display,text,flag);text.accept(Component.translatable("mahoutsukai.progression.scroll",amount));}
}
