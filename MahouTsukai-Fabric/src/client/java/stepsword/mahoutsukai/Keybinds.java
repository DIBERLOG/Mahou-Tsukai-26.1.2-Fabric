package stepsword.mahoutsukai;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
public final class Keybinds {
 public static KeyMapping settingsGUI,stopMagic;
 public static KeyMapping gunUp,gunDown,fireNobu;
 public static KeyMapping drawMahoujin;
 public static KeyMapping selectiveDisplacement;
 public static KeyMapping changeMysticCode;
 public static void init() {
  var category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("mahoutsukai","controls"));
  stopMagic=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.stopMagic",88,category));
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.player==null||mc.screen!=null)return;while(stopMagic.consumeClick())net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.StopMagicPacket());});
  settingsGUI=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.settingsGUI",46,category));
  fireNobu=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.fireNobu",com.mojang.blaze3d.platform.InputConstants.Type.MOUSE,0,category));
  gunUp=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.gunUp",265,category));
  gunDown=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.gunDown",264,category));
  changeMysticCode=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.changeMysticCode",89,category));
  selectiveDisplacement=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.selectiveDisplacement",72,category));
  drawMahoujin = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.drawMahoujin",77,category));
 }
}

