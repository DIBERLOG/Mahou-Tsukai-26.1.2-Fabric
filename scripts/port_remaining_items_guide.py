from remaining_port_helpers import *
p=main/'util/EffectUtil.java'
append(p,''' public static net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> toHolder(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key,Level level){return level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(key);}
 public static int getEnchantmentLevel(net.minecraft.world.item.ItemStack stack,net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment){return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(enchantment,stack);}
 public static boolean hasEnchantment(net.minecraft.world.item.ItemStack stack,net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key,Level level){return getEnchantmentLevel(stack,toHolder(key,level))>0;}
''')
p=main/'util/Utils.java';s=(orig/'util/Utils.java').read_text(encoding='utf-8');a=s.index('   public static Vec3 vecFromPitchYaw');b=s.index('   public static Identifier holderToKey',a);append(p,s[a:b])
for folder in ['item/guide','render/gui/guide']:
 for f in (orig/folder).glob('*.java'):write(client/f.relative_to(orig),f.read_text(encoding='utf-8'))
for rel in ['render/gui/MahouImageButton.java','render/gui/MysticCodeInventoryGui.java','render/gui/WheelGui.java','render/gui/WheelSectorRenderer.java']:
 write(client/rel,(orig/rel).read_text(encoding='utf-8').replace('PacketHandler.sendToServer(', 'net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send('))
p=client/'render/gui/WheelGui.java';s=p.read_text(encoding='utf-8').replace('!InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), Keybinds.changeMysticCode.getKey().getValue())','!Keybinds.changeMysticCode.isDown()');write(p,s)
p=client/'render/gui/guide/PageGui.java';s=p.read_text(encoding='utf-8').replace('PacketHandler.sendToServer(', 'net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(');write(p,s)
p=main/'item/MahouTsukaiGuidebook.java';write(p,'''package stepsword.mahoutsukai.item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
public class MahouTsukaiGuidebook extends ItemBase {
 public static java.util.function.Consumer<Player> openClient=player->{};
 public MahouTsukaiGuidebook(){super("guidebook");}
 @Override public InteractionResult use(Level world,Player player,InteractionHand hand){if(world.isClientSide())openClient.accept(player);return InteractionResult.SUCCESS;}
}
''')
insert(main/'item/ModItems.java','    static {','    public static final Supplier<Item> guidebook=register("guidebook",MahouTsukaiGuidebook::new);\n')
write(main/'networking/SavePagePacket.java','''package stepsword.mahoutsukai.networking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
public record SavePagePacket(int page) implements CustomPacketPayload {
 public static final Type<SavePagePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","save_page"));
 public static final net.minecraft.network.codec.StreamCodec<io.netty.buffer.ByteBuf,SavePagePacket> STREAM_CODEC=net.minecraft.network.codec.StreamCodec.composite(net.minecraft.network.codec.ByteBufCodecs.INT,SavePagePacket::page,SavePagePacket::new);
 public Type<SavePagePacket> type(){return TYPE;}
}
''')
insert(main/'fabric/MahouNetworking.java','    public static void register() {','') if False else None
p=main/'fabric/MahouNetworking.java';s=p.read_text(encoding='utf-8').replace('    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.SavePagePacket.TYPE,stepsword.mahoutsukai.networking.SavePagePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.SavePagePacket.TYPE,(packet,context)->{if(packet.page()<0 || packet.page()>4096)return;var mahou=stepsword.mahoutsukai.util.Utils.getPlayerMahou(context.player());mahou.setGuidePage(packet.page());stepsword.mahoutsukai.mana.PlayerManaManager.updateClientMahou(context.player(),mahou);});
''');write(p,s)
p=client/'Keybinds.java';s=p.read_text(encoding='utf-8').replace(' public static KeyMapping drawMahoujin;',' public static KeyMapping gunUp,gunDown;\n public static KeyMapping drawMahoujin;').replace('  changeMysticCode=','  gunUp=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.gunUp",265,category));\n  gunDown=KeyMappingHelper.registerKeyMapping(new KeyMapping("key.gunDown",264,category));\n  changeMysticCode=');write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',''' public static void init(){
  net.minecraft.client.gui.screens.MenuScreens.register(stepsword.mahoutsukai.containers.ModContainers.MYSTIC_CODE,stepsword.mahoutsukai.render.gui.MysticCodeInventoryGui::new);
  stepsword.mahoutsukai.item.MahouTsukaiGuidebook.openClient=player->{var book=new stepsword.mahoutsukai.item.guide.GuidebookInstance();int page=stepsword.mahoutsukai.util.Utils.getPlayerMahou(player).getGuidePage();book.setCurrentPage(Math.clamp(page,0,book.getPages().size()-1));var gui=new stepsword.mahoutsukai.render.gui.guide.PageGui(book);net.minecraft.client.Minecraft.getInstance().setScreen(gui);gui.navigateToPage(Math.clamp(page,0,book.getPages().size()-1));};
''').replace('if(item instanceof stepsword.mahoutsukai.item.caliburn.Caliburn)','if(item instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode)mc.setScreen(new stepsword.mahoutsukai.render.gui.WheelGui(mc.level,mc.player.getMainHandItem()));else if(item instanceof stepsword.mahoutsukai.item.replica.Replica)ClientPlayNetworking.send(new ItemAbilityPacket(ItemAbilityPacket.ABILITY.REPLICA));else if(item instanceof stepsword.mahoutsukai.item.caliburn.Caliburn)');write(p,s)
config()
