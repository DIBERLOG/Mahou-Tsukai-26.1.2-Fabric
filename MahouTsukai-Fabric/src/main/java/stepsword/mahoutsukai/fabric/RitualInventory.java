package stepsword.mahoutsukai.fabric;

import java.util.ArrayList;
import java.util.HashMap;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.item.catalysts.PowderedCatalyst;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.util.UnorderedList;

/** Original repeat-recipe inventory walk, using Fabric item storage instead of NeoForge handlers. */
public final class RitualInventory {
 public static boolean repeat(Player player, MahoujinTileEntity circle, UnorderedList recipe, boolean cloth) {
  var slots = ContainerStorage.of(player.getInventory(), null);
  var views = new ArrayList<StorageView<ItemVariant>>();
  for (int index=0; index<player.getInventory().getContainerSize(); index++) {
   var slot = slots.getSlot(index);
   views.add(slot);
   var stack = player.getInventory().getItem(index);
   if (!(stack.getItem() instanceof PowderedCatalyst)) {
    var nested = ItemStorage.ITEM.find(stack, ContainerItemContext.ofPlayerSlot(player,slot));
    if (nested != null) for (var view : nested) views.add(view);
   }
  }
  var counts = new HashMap<String,Long>();
  Item clothItem = Item.byBlock(ModBlocks.spellClothBlock.get());
  boolean hasCloth = false;
  for (var view : views) {
   if (view.getResource().getItem() instanceof PowderedCatalyst catalyst)
    counts.merge(catalyst.getCatalystName(), view.getAmount(), Long::sum);
   else if (view.getResource().getItem() == clothItem && view.getAmount()>0) hasCloth = true;
  }
  if (cloth && !hasCloth) return false;
  for (String key : recipe.getKeys()) if (counts.getOrDefault(key,0L)<recipe.get(key)) return false;
  var remaining = new HashMap<String,Integer>();
  for (String key : recipe.getKeys()) remaining.put(key,recipe.get(key));
  boolean tookCloth = !cloth;
  try (var tx = Transaction.openOuter()) {
   for (var view : views) {
    var variant = view.getResource();
    if (!tookCloth && variant.getItem()==clothItem && view.extract(variant,1,tx)==1) tookCloth=true;
    else if (variant.getItem() instanceof PowderedCatalyst catalyst) {
     var name=catalyst.getCatalystName();
     int wanted=remaining.getOrDefault(name,0);
     if (wanted>0) remaining.put(name,wanted-(int)view.extract(variant,wanted,tx));
    }
   }
   if (!tookCloth || remaining.values().stream().anyMatch(count->count>0)) return false;
   tx.commit();
  }
  if (cloth) circle.setCloth(true);
  circle.setCatalysts(recipe);
  return true;
 }
}
