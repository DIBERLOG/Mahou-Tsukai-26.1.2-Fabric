package stepsword.jousting.client;
public final class JoustingClient implements net.fabricmc.api.ClientModInitializer {
 public void onInitializeClient(){
  stepsword.jousting.JoustingMod.stopUsing=p->net.minecraft.client.Minecraft.getInstance().gameMode.releaseUsingItem(p);
  var original=stepsword.jousting.LanceSettings.json();
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(stepsword.jousting.LanceSettingsPacket.TYPE,(packet,context)->stepsword.jousting.LanceSettings.apply(com.google.gson.JsonParser.parseString(packet.json()).getAsJsonObject()));
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->stepsword.jousting.LanceSettings.apply(original));
 }
}
