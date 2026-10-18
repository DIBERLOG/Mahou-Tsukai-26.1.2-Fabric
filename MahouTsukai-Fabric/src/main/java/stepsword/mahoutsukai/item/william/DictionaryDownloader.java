package stepsword.mahoutsukai.item.william;
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
  future.whenComplete((value,error)->{if(error!=null)downloads.remove(address,future);else consumer.accept(value);});
 }
 public static class Dictionary {public final DictionarySearcher dictionary;public final boolean complete;public Dictionary(DictionarySearcher dictionary,boolean complete){this.dictionary=dictionary;this.complete=complete;}}
}
