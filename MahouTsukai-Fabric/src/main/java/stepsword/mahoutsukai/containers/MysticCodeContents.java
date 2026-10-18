package stepsword.mahoutsukai.containers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
/** Three real component-backed slots; every native container edit is committed to the held item. */
public final class MysticCodeContents extends SimpleContainer {
 private final ItemStack holder;
 public MysticCodeContents(ItemStack holder){super(3);this.holder=holder;var contents=holder.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY);var items=NonNullList.withSize(3,ItemStack.EMPTY);contents.copyInto(items);for(int i=0;i<3;i++)super.setItem(i,items.get(i));}
 @Override public void setChanged(){super.setChanged();if(holder==null)return;var values=new java.util.ArrayList<ItemStack>();for(int i=0;i<3;i++)values.add(getItem(i).copy());holder.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(values));}
}
