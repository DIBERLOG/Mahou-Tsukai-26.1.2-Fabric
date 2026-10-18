package stepsword.mahoutsukai.fabric;

import net.minecraft.world.item.ItemStack;

/** Original optional Apotheosis API, also implemented by the Adventure Fabric port. */
public final class ApotheosisCompatibility {
 public static ItemStack unsocket(ItemStack stack){
  if(!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("apotheosis"))return stack;
  try{
   var gems=Class.forName("dev.shadowsoffire.apotheosis.socket.SocketedGems");
   var affixes=Class.forName("dev.shadowsoffire.apotheosis.affix.ItemAffixes");
   Class.forName("dev.shadowsoffire.apotheosis.socket.SocketHelper").getMethod("setGems",ItemStack.class,gems).invoke(null,stack,gems.getField("EMPTY").get(null));
   Class.forName("dev.shadowsoffire.apotheosis.affix.AffixHelper").getMethod("setAffixes",ItemStack.class,affixes).invoke(null,stack,affixes.getField("EMPTY").get(null));
   return stack;
  }catch(ReflectiveOperationException e){throw new IllegalStateException("Installed Apotheosis does not expose its expected gem/affix API",e);}
 }
}
