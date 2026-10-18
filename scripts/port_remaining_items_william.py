from remaining_port_helpers import *
# Match declarations, not call sites, when transplanting complete methods.
def replace_method(s,name,new):
 m=re.search(r'   (?:public|private|protected) (?:static )?[^\n{;]*\b'+name+r'\([^\n]*\)\s*\{',s);assert m,name
 a=s.index('{',m.start());i=a+1;depth=1
 while depth:
  if s[i]=='{':depth+=1
  elif s[i]=='}':depth-=1
  i+=1
 return s[:m.start()]+new+s[i:]
for rel in ['item/william/WrittenEvaluation.java','item/william/DictionarySearcher.java','item/william/BookReadingTracker.java','item/william/WilliamCloningRecipe.java','util/AltChecker.java','entity/mahoujin/WilliamEntity.java','item/william/William.java','potion/ActingPotion.java','potion/AbyssPotion.java']:
 copy(rel)
p=main/'item/william/WrittenEvaluation.java';s=p.read_text(encoding='utf-8').replace('import stepsword.mahoutsukai.MahouTsukaiMod;','').replace('MahouTsukaiMod.logger','LOGGER').replace('public class WrittenEvaluation {','public class WrittenEvaluation {\n private static final org.slf4j.Logger LOGGER=org.slf4j.LoggerFactory.getLogger("mahoutsukai");');write(p,s)
p=main/'item/william/DictionarySearcher.java';s=p.read_text(encoding='utf-8').replace('c == s.length() - 1','i == s.length() - 1').replace("c != ',' && c != s.length() - 1","c != ',' && i != s.length() - 1");write(p,s)
# Original scoring and dictionaries, with bounded network waits and atomic cache writes.
write(main/'item/william/DictionaryDownloader.java','''package stepsword.mahoutsukai.item.william;
import java.nio.file.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.function.Consumer;
import stepsword.mahoutsukai.config.MTConfig;
public class DictionaryDownloader {
 public static Thread dictionaryDownloader;
 public String url;
 public static final ConcurrentHashMap<String,Dictionary> dictionaries=new ConcurrentHashMap<>();
 private static final ConcurrentHashMap<String,CompletableFuture<DictionarySearcher>> downloads=new ConcurrentHashMap<>();
 public DictionaryDownloader(String url){this.url=url;}
 public static boolean allDictionariesFinished(){return !MTConfig.WILLIAM_DICTIONARIES.isEmpty() && MTConfig.WILLIAM_DICTIONARIES.stream().allMatch(s->{var d=dictionaries.get(address(s));return d!=null && d.complete;});}
 private static String address(String name){return name.startsWith("https://")?name:"https://raw.githubusercontent.com/stepsword/all-words-in-all-languages/refs/heads/main/"+name+"/"+name+".txt";}
 public static void startAllDownloaders(Consumer<DictionarySearcher> consumer){for(String name:MTConfig.WILLIAM_DICTIONARIES)startDictionaryDownloader(name,consumer);}
 public static void startDictionaryDownloader(String name,Consumer<DictionarySearcher> consumer){new DictionaryDownloader(name).getDictionary(address(name),consumer);}
 public void getDictionary(String address,Consumer<DictionarySearcher> consumer){
  var cached=dictionaries.get(address);if(cached!=null && cached.complete){consumer.accept(cached.dictionary);return;}
  var future=downloads.computeIfAbsent(address,key->{
   var result=new CompletableFuture<DictionarySearcher>();
   dictionaryDownloader=new Thread(()->{HttpURLConnection connection=null;Path temporary=null;try{
    String filename=java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(key.getBytes(StandardCharsets.UTF_8)))+".txt";
    Path cache=net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().resolve("mahoutsukai/dictionaries").resolve(filename);
    Files.createDirectories(cache.getParent());
    if(!Files.exists(cache)){
     connection=(HttpURLConnection)URI.create(key).toURL().openConnection();connection.setConnectTimeout(15000);connection.setReadTimeout(30000);connection.setRequestProperty("Accept","text/*");
     if(connection.getResponseCode()/100!=2)throw new java.io.IOException("Dictionary HTTP "+connection.getResponseCode());
     temporary=Files.createTempFile(cache.getParent(),"dictionary-",".tmp");
     try(var in=connection.getInputStream()){Files.copy(in,temporary,StandardCopyOption.REPLACE_EXISTING);}Files.move(temporary,cache,StandardCopyOption.REPLACE_EXISTING);
    }
    var searcher=DictionarySearcher.initializeDictionarySearcher(Files.readString(cache,StandardCharsets.UTF_8));
    if(searcher.words.isEmpty())throw new java.io.IOException("Empty dictionary");
    dictionaries.put(key,new Dictionary(searcher,true));result.complete(searcher);
   }catch(Exception error){result.completeExceptionally(error);org.slf4j.LoggerFactory.getLogger("mahoutsukai").error("Could not load William dictionary {}",key,error);}
   finally{if(connection!=null)connection.disconnect();if(temporary!=null)try{Files.deleteIfExists(temporary);}catch(java.io.IOException ignored){}}
   },"Mahou Dictionary Downloader");dictionaryDownloader.setDaemon(true);dictionaryDownloader.start();return result;
  });
  future.thenAccept(consumer);if(future.isCompletedExceptionally())downloads.remove(address,future);
 }
 public static class Dictionary {public final DictionarySearcher dictionary;public final boolean complete;public Dictionary(DictionarySearcher dictionary,boolean complete){this.dictionary=dictionary;this.complete=complete;}}
}
''')
p=main/'item/william/William.java';s=(orig/'item/william/William.java').read_text(encoding='utf-8');s='\n'.join(l for l in s.splitlines() if not l.startswith('import net.neoforged.'));s=s.replace('stack.is(ModItems.william)','stack.is(ModItems.william.get())')
s=s.replace('williamBookTraded(TradeWithVillagerEvent event)','williamBookTraded(Player player,MerchantOffer offer)').replace('      MerchantOffer offer = event.getMerchantOffer();\n','').replace('      Player player = event.getEntity();\n','').replace('event.getEntity().level()','player.level()')
s=s.replace('createWilliam(RightClickBlock event)','createWilliam(Player player,ItemStack stack,BlockPos p)').replace('      BlockPos p = event.getHitVec().getBlockPos();\n','').replace('event.getLevel()','player.level()').replace('      ItemStack stack = event.getItemStack();\n','')
s=replace_method(s,'onDestroyed','');write(p,s)
p=main/'entity/mahoujin/WilliamEntity.java';s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;','').replace('import stepsword.mahoutsukai.render.item.BookRender;','')
s=s.replace('   public BookRender last_br;\n   public BookRender br = new BookRender();','   public static java.util.function.Consumer<WilliamEntity> clientTick=e->{};\n   public Object last_br,br;')
s=s.replace('         this.last_br = this.br.clone();\n         this.br.animateTick(this.clientTicks);','         clientTick.accept(this);')
a=s.index('      } else {\n         float open = (Float)this.entityData.get(COVER_OPENNESS);');b=s.index('\n   public void plethora',a);s=s[:a]+'      }\n   }\n'+s[b:]
s=s.replace('williamRightClick(EntityInteract event)','williamRightClick(Player player,Entity e,InteractionHand hand)').replace('      Entity e = event.getTarget();\n','').replace('event.getHand()','hand').replace('event.getEntity() instanceof Player p','player instanceof Player p')
s=s.replace('if (player instanceof Player p && p.isDiscrete() && p.getUUID().equals(w.getCasterUUID()))','if (player.isDiscrete() && player.getUUID().equals(w.getCasterUUID()))').replace('PlayerHelp.sendHelpMessage(p,','PlayerHelp.sendHelpMessage(player,')
s=re.sub(r'   public AABB getBoundingBox\(\) \{.*?\n   }\n','',s,flags=re.S).replace('new ItemStack(ModItems.rhongomyniad)','new ItemStack(ModItems.rhongomyniad.get())');s=s.replace('      v = this.collide(v);','      v = this.collide(v);')
write(p,s);append(p,''' public float coverOpenness(){return entityData.get(COVER_OPENNESS);} public float pageOpenness(){return entityData.get(PAGE_OPENNESS);}
''')
entity('WILLIAM','entity/mahoujin/WilliamEntity.java','william_entity',1.5,1)
insert(main/'item/ModItems.java','    static {','    public static final Supplier<Item> william=register("william",stepsword.mahoutsukai.item.william.William::new);\n')
p=main/'dataattachments/worldsave/MahouSavedData.java';s=p.read_text(encoding='utf-8');o=(orig/'dataattachments/worldsave/MahouSavedData.java').read_text(encoding='utf-8');a=o.index('   public int addFameIfExists');b=o.index('   public void changePlayerSpawn',a);s=s.replace('   public void changePlayerSpawn',o[a:b].replace('new ItemStack(ModItems.william)','new ItemStack(ModItems.william.get())')+'   public void changePlayerSpawn');imports=o[o.index('import stepsword.mahoutsukai.config'):o.index('public class MahouSavedData')];s=s.replace('public class MahouSavedData',imports+'public class MahouSavedData');s=s.replace('import java.util.UUID;','import java.util.UUID;\nimport java.util.List;');write(p,s)
write(main/'MahouRecipes.java','''package stepsword.mahoutsukai;
import stepsword.mahoutsukai.item.william.WilliamCloningRecipe;
public final class MahouRecipes {
 private static final net.minecraft.world.item.crafting.RecipeSerializer<WilliamCloningRecipe> CLONING=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.RECIPE_SERIALIZER,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","william_bookcloning"),new net.minecraft.world.item.crafting.RecipeSerializer<>(com.mojang.serialization.MapCodec.unit(WilliamCloningRecipe::new),net.minecraft.network.codec.StreamCodec.of((output,recipe)->{},input->new WilliamCloningRecipe())));
 public static final java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeSerializer<WilliamCloningRecipe>> WILLIAM_CLONING_RECIPE=()->CLONING;
 public static void init(){}
}
''')
p=main/'item/william/WilliamCloningRecipe.java';write(p,p.read_text(encoding='utf-8').replace('itemstack1.is(ModItems.william)','itemstack1.is(ModItems.william.get())'))
copy('networking/ActingPacket.java');p=main/'networking/ActingPacket.java';s=p.read_text(encoding='utf-8').replace('import net.neoforged.neoforge.network.handling.IPayloadContext;','');s=replace_method(s,'handle','');s=s.replace('public class ActingPacket implements CustomPacketPayload {','public class ActingPacket implements CustomPacketPayload {\n public static final Type<ActingPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","acting"));').replace('return MahouPackets.ACTING_TYPE;','return TYPE;');write(p,s)
for field,cls,id in [('ACTING','ActingPotion','acting'),('ABYSS','AbyssPotion','abyss'),('MANA_TAKEN_COOLDOWN','ManaTakenCooldownPotion','mana_taken_cooldown')]:
 copy('potion/'+cls+'.java');p=main/'potion/ModEffects.java';s=p.read_text(encoding='utf-8')
 if f' {field}=' not in s and f' {field} =' not in s:insert(p,' public static int getColorNumber',f' public static final Holder<MobEffect> {field}=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{id}"),new {cls}());\n')
p=main/'fabric/MahouFabric.java';s=p.read_text(encoding='utf-8').replace('        stepsword.mahoutsukai.entity.ModEntities.init();','        stepsword.mahoutsukai.entity.ModEntities.init();\n        stepsword.mahoutsukai.MahouRecipes.init();').replace('                stepsword.mahoutsukai.item.nobu.Nobu.nobuPlayerTick(player);','                stepsword.mahoutsukai.item.nobu.Nobu.nobuPlayerTick(player);\n                stepsword.mahoutsukai.item.william.William.createWilliamCheck(player);');s=s.replace('    public void onInitialize() {','''    public void onInitialize() {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,world,hand,hit)->{if(!world.isClientSide() && !player.isSpectator())stepsword.mahoutsukai.item.william.William.createWilliam(player,player.getItemInHand(hand),hit.getBlockPos());return net.minecraft.world.InteractionResult.PASS;});
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player,world,hand,target,hit)->{if(target instanceof stepsword.mahoutsukai.entity.mahoujin.WilliamEntity){if(!player.isSpectator())stepsword.mahoutsukai.entity.mahoujin.WilliamEntity.williamRightClick(player,target,hand);return net.minecraft.world.InteractionResult.SUCCESS;}return net.minecraft.world.InteractionResult.PASS;});
''');write(p,s)
p=main/'fabric/MahouNetworking.java';s=p.read_text(encoding='utf-8').replace('    public static void register() {','    public static void register() {\n        PayloadTypeRegistry.clientboundPlay().register(stepsword.mahoutsukai.networking.ActingPacket.TYPE,stepsword.mahoutsukai.networking.ActingPacket.STREAM_CODEC);');write(p,s)
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace('ModContainers.MYSTIC_CODE,','ModContainers.MYSTIC_CODE.get(),');write(p,s)
config()
