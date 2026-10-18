from remaining_port_helpers import *
copy('util/PlayerHelp.java')
p=main/'config/MandatoryFun.java';original=(orig/'config/MandatoryFun.java').read_text();a=original.index('   public static boolean meetsAllFunReqs');b=original.index('   public enum FUN_REQS',a);insert(p,'   public enum FUN_REQS',original[a:b])
# The quest book uses the native book reader, preserving original translated pages and live progress.
write(main/'item/MahouTsukaiFunbook.java','''package stepsword.mahoutsukai.item;
import net.minecraft.world.item.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.server.network.Filterable;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.config.*;
public class MahouTsukaiFunbook extends WrittenBookItem {
 public MahouTsukaiFunbook(){super(ItemBase.withRegisteredId(new Properties().stacksTo(1)));}
 @Override public InteractionResult use(Level world,Player player,InteractionHand hand){if(!world.isClientSide()){var pages=new java.util.ArrayList<Filterable<Component>>();pages.add(Filterable.passThrough(Component.translatable("mahoutsukai.book.fun1.name").append("\\n\\n").append(Component.translatable("mahoutsukai.book.fun1.desc"))));pages.add(Filterable.passThrough(Component.translatable("mahoutsukai.book.fun2.name").append("\\n\\n").append(Component.translatable("mahoutsukai.book.fun2.desc"))));for(var req:MandatoryFun.FUN_REQS.values())if(MTConfig.CALIBURN_MANDATORY_FUN_REQUIREMENTS.contains(req.name())){String key="mahoutsukai.book."+req.name().toLowerCase(java.util.Locale.ENGLISH);pages.add(Filterable.passThrough(Component.translatable(key+".name").append("\\n\\n").append(Component.translatable(MandatoryFun.meetsFunReq(player,req)?"mahoutsukai.book.fun_req_met.desc":"mahoutsukai.book.fun_req_not_met.desc")).append("\\n\\n").append(Component.translatable(key+".desc"))));}player.getItemInHand(hand).set(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT,new WrittenBookContent(Filterable.passThrough("Mandatory Fun"),"stepsword",0,pages,true));}return super.use(world,player,hand);}
}
''')
insert(main/'item/ModItems.java','    static {','    public static final Supplier<Item> funbook=register("funbook",stepsword.mahoutsukai.item.MahouTsukaiFunbook::new);\n')
p=copy('effects/projection/PowerConsolidationSpellEffect.java');s=p.read_text();s=re.sub(r'import net.neoforged[^;]+;\n','',s);s=s.replace('import stepsword.mahoutsukai.MahouTsukaiMod;','').replace('import stepsword.mahoutsukai.handlers.ServerHandler;','').replace('import stepsword.mahoutsukai.networking.ChunkMahouRequestPacket;','').replace('import stepsword.mahoutsukai.networking.SweepAttackPacket;','')
a=s.index('   public static void caliburnLeftClickSpace');s=s[:a]+'}\n';s=s.replace('final Post event','final ServerLevel world').replace('event.getLevel()','world').replace('ServerHandler.tickCounter','world.getGameTime()').replace('((ServerLevel)world).getEntities().getAll()','world.getAllEntities()').replace('ArrayList<String> list = (ArrayList<String>)MTConfig.POWER_CONSOLIDATION_BLACKLIST','List<? extends String> list = MTConfig.POWER_CONSOLIDATION_BLACKLIST');s=re.sub(r'                                 if \(MTConfig.POWER_CONSOLIDATION_UNSOCKETS_APOTH_GEMS\) \{.*?\n                                 }\n','',s,flags=re.S);s=re.sub(r'\s*MahouTsukaiMod.jousting.powerConsolidation[^;]+;','',s)
s=s.replace('ResourceHandler<ItemResource> inventory = Utils.getInventory(player);','var inventory = player.getInventory();').replace('int sizeInventory = inventory.size();','int sizeInventory = inventory.getContainerSize();').replace('((ItemResource)inventory.getResource(i)).toStack()','inventory.getItem(i)');s=re.sub(r'ModTriggers\.(\w+)\.get\(\)',r'ModTriggers.\1',s);s=s.replace('Utils.err(e.toString());','org.slf4j.LoggerFactory.getLogger("mahoutsukai").error("Lake sword conversion failed",e);');write(p,s)
append(main/'util/Utils.java',''' public static net.minecraft.resources.Identifier getRegistryKey(net.minecraft.world.item.Item item,Level world){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);}
''')
p=main/'fabric/MahouFabric.java';s=p.read_text();line='            stepsword.mahoutsukai.effects.projection.PowerConsolidationSpellEffect.powerConsolidationWorldTick(world);';s=s.replace('        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK.register(world -> {','        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK.register(world -> {\n'+line) if line not in s else s;write(p,s)
config()
