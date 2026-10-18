from remaining_port_helpers import *
import json
p=client/'render/particle/ParticleSphere.java';s=p.read_text(encoding='utf-8').replace('public class ParticleSphere extends Particle {','public class ParticleSphere extends Particle {\n public static final java.util.Set<ParticleSphere> active=java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());').replace('      this.grow = grow;','      this.grow = grow;active.add(this);').replace('      this.oRoll = this.roll;','      this.oRoll = this.roll;if(!isAlive())active.remove(this);').replace('float partialTicks) {','float partialTicks,net.minecraft.client.renderer.SubmitNodeCollector buf) {').replace('         BufferSource buf = Minecraft.getInstance().renderBuffers().bufferSource();','').replace('MahoujinRenderType.wrapCullLayer(','(').replace('         buf.endBatch(sphere);','');write(p,s)
p=main/'datacomponents/fiction/FictionMahou.java';s=p.read_text(encoding='utf-8').replace('this.caster.equals(MahouIds.FAE_UUID)','java.util.Objects.equals(b.caster,this.caster)');write(p,s)
# Reading progress accepts actual held book, original timing, and server-known text.
s=(orig/'networking/TurnedPagePacket.java').read_text(encoding='utf-8');s='\n'.join(l for l in s.splitlines() if not l.startswith('import net.neoforged.'));s=s.replace('IPayloadContext context','ServerPlayer player').replace('      ServerPlayer player = (ServerPlayer)context.player();\n','').replace('openedBook(RightClickItem event)','openedBook(net.minecraft.world.entity.player.Player player,ItemStack stack)').replace('      ItemStack stack = event.getItemStack();\n','').replace('event.getEntity()','player').replace('public class TurnedPagePacket implements CustomPacketPayload {','public class TurnedPagePacket implements CustomPacketPayload {\n public static final Type<TurnedPagePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","turned_page_packet"));').replace('return MahouPackets.TURNED_PAGE_TYPE;','return TYPE;').replace('      MahouSavedData mahou = Utils.getWorldMahou(player.level());','''      if(message.page<0 || message.page>100)return;
      ItemStack held=player.getMainHandItem().getItem() instanceof WrittenBookItem?player.getMainHandItem():player.getOffhandItem();
      if(!(held.getItem() instanceof WrittenBookItem))return;
      var book=held.get(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT);if(book==null || message.page>=book.pages().size())return;
      if(!lastOpened.containsKey(player.getUUID()) || !lastOpened.get(player.getUUID()).text.equals(WrittenEvaluation.getBookText(held)))return;
      MahouSavedData mahou = Utils.getWorldMahou(player.level());''');write(main/'networking/TurnedPagePacket.java',s)
write(main/'fabric/mixin/WilliamTradeMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.inventory.MerchantResultSlot.class)
public abstract class WilliamTradeMixin {
 @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final private net.minecraft.world.inventory.MerchantContainer slots;
 @org.spongepowered.asm.mixin.injection.Inject(method="onTake",at=@org.spongepowered.asm.mixin.injection.At("HEAD"))
 private void mahoutsukai$fame(net.minecraft.world.entity.player.Player player,net.minecraft.world.item.ItemStack stack,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){if(!player.level().isClientSide() && slots.getActiveOffer()!=null)stepsword.mahoutsukai.item.william.William.williamBookTraded(player,slots.getActiveOffer());}
}
''')
write(client/'fabric/mixin/WilliamBookPageMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.gui.screens.inventory.BookViewScreen.class)
public abstract class WilliamBookPageMixin {
 @org.spongepowered.asm.mixin.Shadow private int currentPage;
 @org.spongepowered.asm.mixin.injection.Inject(method={"pageForward","pageBack"},at=@org.spongepowered.asm.mixin.injection.At("TAIL"))
 private void mahoutsukai$page(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.TurnedPagePacket(currentPage));}
 @org.spongepowered.asm.mixin.injection.Inject(method="setPage",at=@org.spongepowered.asm.mixin.injection.At("RETURN"))
 private void mahoutsukai$link(int page,org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> ci){if(ci.getReturnValueZ())net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.TurnedPagePacket(currentPage));}
}
''')
p=main/'fabric/MahouNetworking.java';s=p.read_text(encoding='utf-8').replace('    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.TurnedPagePacket.TYPE,stepsword.mahoutsukai.networking.TurnedPagePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.TurnedPagePacket.TYPE,(packet,context)->stepsword.mahoutsukai.networking.TurnedPagePacket.handle(packet,context.player()));
''');write(p,s)
p=main/'fabric/MahouFabric.java';s=p.read_text(encoding='utf-8').replace('    public void onInitialize() {','''    public void onInitialize() {
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)->{if(!world.isClientSide() && !player.isSpectator())stepsword.mahoutsukai.networking.TurnedPagePacket.openedBook(player,player.getItemInHand(hand));return net.minecraft.world.InteractionResult.PASS;});
''');write(p,s)
p=res/'mahoutsukai.fabric.mixins.json';data=json.loads(p.read_text(encoding='utf-8'));data['mixins'].append('WilliamTradeMixin');data['client'].append('WilliamBookPageMixin');write(p,json.dumps(data,indent=2)+'\n')
