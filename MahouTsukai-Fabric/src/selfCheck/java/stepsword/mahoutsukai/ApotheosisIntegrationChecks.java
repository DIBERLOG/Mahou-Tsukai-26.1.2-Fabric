package stepsword.mahoutsukai;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import java.util.List;

/** Uses registered components and API from the real optional Apotheosis Fabric JAR. */
public final class ApotheosisIntegrationChecks {
 public static void run(){
  var source=new ItemStack(Items.DIAMOND_SWORD);
  var gems=dev.shadowsoffire.apotheosis.socket.gem.GemRegistry.INSTANCE.getValues();if(gems.isEmpty())throw new AssertionError("Actual Apotheosis gems did not load");
  var gem=dev.shadowsoffire.apotheosis.socket.gem.GemItem.createStack(gems.iterator().next(),dev.shadowsoffire.apotheosis.socket.gem.Purity.values()[0]);
  source.set(Apoth.Components.SOCKETED_GEMS,ItemContainerContents.fromItems(List.of(gem)));
  var ids=AffixRegistry.INSTANCE.getKeys();if(ids.isEmpty())throw new AssertionError("Actual Apotheosis affix registry did not load");
  AffixHelper.setAffixes(source,ItemAffixes.EMPTY.toBuilder().put(AffixRegistry.INSTANCE.holder(ids.iterator().next()),.5f).build());
  var copy=source.copy();stepsword.mahoutsukai.fabric.ApotheosisCompatibility.unsocket(copy);
  if(!copy.get(Apoth.Components.SOCKETED_GEMS).allItemsCopyStream().allMatch(ItemStack::isEmpty)||!copy.get(Apoth.Components.AFFIXES).isEmpty())throw new AssertionError("Original unsocket API did not clear real gem/affix components");
  if(source.get(Apoth.Components.SOCKETED_GEMS).allItemsCopyStream().allMatch(ItemStack::isEmpty)||source.get(Apoth.Components.AFFIXES).isEmpty())throw new AssertionError("Unsocket modified the original offering rather than its simulation copy");
  System.out.println("PASS: APOTHEOSIS_INTEGRATION: actual Fabric Adventure mod/registered affix and socket components, original gem/affix clearing API, source stack preserved");
 }
}

