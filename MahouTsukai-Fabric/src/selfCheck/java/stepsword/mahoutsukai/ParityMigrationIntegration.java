package stepsword.mahoutsukai;

import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.*;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import stepsword.mahoutsukai.fabric.*;
import stepsword.mahoutsukai.util.Utils;

/** Golden fixtures produced by loading the unmodified original JAR on NeoForge. */
public final class ParityMigrationIntegration {
    private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    private static final Path DIR=Path.of("../../../analysis/original-fixtures");
    private static CompoundTag read(String name) throws Exception {return NbtIo.readCompressed(DIR.resolve(name+".nbt"),NbtAccounter.unlimitedHeap());}
    public static void run(MinecraftServer server) throws Exception {
        require(Files.exists(DIR.resolve("probe.passed")),"Generate original NeoForge fixtures before running parity checks");
        var level=server.overworld();var input=read("player");
        var original=input.getCompound("neoforge:attachments").orElseThrow();
        var player=new ServerPlayer(server,level,new com.mojang.authlib.GameProfile(UUID.randomUUID(),"Migrated"),ClientInformation.createDefault());
        player.load(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),input));
        require(Utils.getPlayerMahou(player).getStoredMana()==6789 && Utils.getPlayerMahou(player).getMaxMana()==12345,"Original player progression migrated through native Player.load");
        require(Utils.getSettingsMahou(player).getIntSettings(0).equals(List.of(10,20,30,40,50,60)),"Original settings attachment migrated");
        require(Utils.getLivingMahou(player).getFearPerson()!=null,"Original living attachment migrated");
        var saved=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());player.saveWithoutId(saved);
        for(String name:List.of("player_mahou","living_mahou","settings_mahou")) {
            var expected=original.getCompound("mahoutsukai:"+name).orElseThrow();
            var actual=saved.buildResult().getCompound("mahoutsukai:"+name).orElseThrow();
            require(actual.equals(expected),"All original fields preserved for "+name+": expected "+expected+" actual "+actual);
        }
        var nativeWins=input.copy();nativeWins.put("mahoutsukai:player_mahou",saved.buildResult().getCompound("mahoutsukai:player_mahou").orElseThrow().copy());
        nativeWins.getCompound("mahoutsukai:player_mahou").orElseThrow().putInt("stored_mana",99);
        player.load(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),nativeWins));
        require(Utils.getPlayerMahou(player).getStoredMana()==99,"Native data takes precedence over stale legacy envelope");
        var circle=new stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity(level,player,new net.minecraft.world.phys.Vec3(0,0,1),net.minecraft.world.phys.Vec3.ZERO,1,1,1,1,1);
        circle.load(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),read("circle")));
        require(circle.getLife()==7 && circle.getCircleSize()==3 && circle.getMahoujinData().getColorR()==.2f,"Original synchronized circle attachment migrated");
        var chunkTag=read("chunk");require(LegacyMahouData.migrateChunk(chunkTag),"Legacy chunk conversion runs once");require(!LegacyMahouData.migrateChunk(chunkTag),"Chunk conversion is idempotent");
        var chunk=SerializableChunkData.parse(level,level.palettedContainerFactory(),read("chunk")).read(level,level.getPoiManager(),new net.minecraft.world.level.chunk.storage.RegionStorageInfo("test",level.dimension(),"chunk"),new net.minecraft.world.level.ChunkPos(0,0));
        var rewritten=SerializableChunkData.copyOf(level,chunk instanceof net.minecraft.world.level.chunk.ImposterProtoChunk proxy ? proxy.getWrapped() : chunk).write();require(rewritten.getCompound("fabric:attachments").flatMap(a->a.getCompound("mahoutsukai:chunk_mahou")).isPresent(),"Migrated native chunk writes the Fabric envelope");
        var data=chunk.getAttachedOrThrow(stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou.TYPE);
        require(data.lakeBlocks.contains(new net.minecraft.core.BlockPos(8,280,8)) && data.fogProjectors.contains(new net.minecraft.core.BlockPos(9,280,8)) && !data.needsRetroFix,"Original chunk lists survive actual native chunk deserialization");
        var ops=level.registryAccess().createSerializationContext(NbtOps.INSTANCE);int items=0;
        for(var tag:read("items").getList("items").orElseThrow()) {
            var stack=ItemStack.CODEC.parse(ops,tag).getOrThrow();
            require(!stack.isEmpty(),"Original item stack must decode");
            var encoded=ItemStack.CODEC.encodeStart(ops,stack).getOrThrow();
            require(encoded.equals(tag),"Original persistent item patch preserved: "+stack);
            require(ItemStack.CODEC.parse(ops,encoded).getOrThrow().getItem()==stack.getItem(),"Migrated original item survives save/reload");items++;
            if(((CompoundTag)tag).getCompound("components").orElseGet(CompoundTag::new).contains("mahoutsukai:gem_mahou"))require(Utils.getGemMahou(stack).getStoredMana()==4321,"Original gem mana component preserved");
            if(((CompoundTag)tag).getCompound("components").orElseGet(CompoundTag::new).contains("mahoutsukai:sword_mahou"))require(Utils.getSwordMahou(stack).getAttackDamage()==23.5f && Utils.getSwordMahou(stack).getInnateCap()==9000,"Original sword damage and cap preserved");
        }
        int entities=0;
        for(var tag:read("entities").getList("entities").orElseThrow()) {
            var entity=EntityType.loadEntityRecursive((CompoundTag)tag,level,EntitySpawnReason.LOAD,e->e);
            require(entity!=null,"Original entity ID must load: "+((CompoundTag)tag).getString("id"));
            var output=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());entity.saveWithoutId(output);
            var again=entity.getType().create(level,EntitySpawnReason.LOAD);require(again!=null,"Migrated entity can be recreated");
            again.load(TagValueInput.create(ProblemReporter.DISCARDING,level.registryAccess(),output.buildResult()));entities++;
        }
        var registries=JsonParser.parseString(Files.readString(DIR.resolve("registries.json"))).getAsJsonObject();
        var missing=new JsonObject();
        for(var registry:List.of(BuiltInRegistries.ITEM,BuiltInRegistries.BLOCK,BuiltInRegistries.ENTITY_TYPE,BuiltInRegistries.MOB_EFFECT,BuiltInRegistries.BLOCK_ENTITY_TYPE,BuiltInRegistries.DATA_COMPONENT_TYPE,BuiltInRegistries.FLUID)) {
            var absent=new JsonArray();for(var id:registries.getAsJsonArray(registry.key().identifier().toString()))if(!registry.containsKey(net.minecraft.resources.Identifier.parse(id.getAsString())))absent.add(id);
            if(!absent.isEmpty())missing.add(registry.key().identifier().toString(),absent);
        }
        var report=new JsonObject();report.add("missing_original_registry_ids",missing);report.addProperty("original_items_loaded",items);report.addProperty("original_entities_loaded",entities);
        var originalConfig=JsonParser.parseString(Files.readString(DIR.resolve("config.json"))).getAsJsonObject();
        var current=stepsword.mahoutsukai.config.MTConfig.defaultSettings();Files.writeString(Path.of("../../../analysis/phase018-native-config-defaults.json"),new GsonBuilder().setPrettyPrinting().create().toJson(current));var configDiff=new JsonObject();
        for(var e:originalConfig.entrySet())if(current.has(e.getKey())&&!current.get(e.getKey()).equals(e.getValue())){var diff=new JsonObject();diff.add("original",e.getValue());diff.add("fabric",current.get(e.getKey()));configDiff.add(e.getKey(),diff);}
        report.add("current_config_differences",configDiff);
        Files.writeString(Path.of("../../../analysis/phase018-parity-report.json"),new GsonBuilder().setPrettyPrinting().create().toJson(report)+"\n");
        require(missing.isEmpty(),"Missing original registered IDs: "+missing);
        var intended=java.util.Map.of("PROXIMITY_PROJECTION_DURABILITY",130.0,"POWER_CONSOLIDATION_FOG_RADIUS",24.0,"NOBU_DISTANCE_REQUIREMENT",100.0,"CLARENT_ATTACK_CAP",1600.0);
        var unexplained=configDiff.deepCopy();
        intended.forEach((name,value)->{require(configDiff.has(name)&&configDiff.getAsJsonObject(name).get("fabric").getAsDouble()==value,"Missing requested progression default: "+name);unexplained.remove(name);});
        require(unexplained.isEmpty(),"Unrequested configured-default differences: "+unexplained);
        System.out.println("PASS: ORIGINAL_MIGRATION_PARITY original player/living/colors/chunk/circle + "+items+" item stacks + seven registries");
    }
}

