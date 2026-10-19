package stepsword.mahoutsukai.containers;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.mysticcode.MysticCode;
import stepsword.mahoutsukai.item.spells.SpellScroll;
public class MysticCodeInventory extends AbstractContainerMenu {
 private final MysticCodeContents contents;
 private final ItemStack code;
 public MysticCodeInventory(int id,Inventory inventory){this(id,inventory,inventory.player.getMainHandItem().getItem() instanceof MysticCode?inventory.player.getMainHandItem():inventory.player.getOffhandItem());}
 public MysticCodeInventory(int id,Inventory inventory,ItemStack code){super(ModContainers.MYSTIC_CODE.get(),id);this.code=code;contents=new MysticCodeContents(code);
  for(int i=0;i<3;i++)addSlot(new Slot(contents,i,44+36*i,20){@Override public boolean mayPlace(ItemStack stack){return stepsword.mahoutsukai.fabric.SpellCasting.accepts(stack) && !hasItem();}@Override public void setChanged(){super.setChanged();save();}});
  for(int row=0;row<3;row++)for(int col=0;col<9;col++)addPlayerSlot(inventory,col+row*9+9,8+col*18,row*18+51);
  for(int col=0;col<9;col++)addPlayerSlot(inventory,col,8+col*18,109);
 }
 private void addPlayerSlot(Inventory inv,int i,int x,int y){addSlot(new Slot(inv,i,x,y){@Override public boolean mayPickup(Player player){return getItem()!=code;}@Override public boolean mayPlace(ItemStack stack){return getItem()!=code && stack!=code;}});}
 private void save(){if(code.getItem() instanceof MysticCode mystic)mystic.saveCapabilityToStack(code);}
 @Override public boolean stillValid(Player player){return !code.isEmpty() && (player.getMainHandItem()==code || player.getOffhandItem()==code);}
 @Override public ItemStack quickMoveStack(Player player,int index){if(index<0 || index>=slots.size())return ItemStack.EMPTY;var slot=slots.get(index);if(!slot.hasItem() || slot.getItem()==code)return ItemStack.EMPTY;var stack=slot.getItem();var result=stack.copy();if(index<3){if(!moveItemStackTo(stack,3,slots.size(),true))return ItemStack.EMPTY;}else if(!stepsword.mahoutsukai.fabric.SpellCasting.accepts(stack) || !moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);save();return result;}
 @Override public void removed(Player player){super.removed(player);save();}
}
