package local.mahouparity;

import java.nio.file.*;
import java.util.*;
import com.mojang.authlib.GameProfile;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.server.level.*;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.storage.TagValueOutput;
import stepsword.mahoutsukai.dataattachments.DataAttachments;

/** Test-only mod, loaded alongside the unmodified original JAR. */
@Mod("mahou_parity_probe")
public final class OriginalProbe {
    public OriginalProbe() { NeoForge.EVENT_BUS.addListener(this::started); }

    private void started(ServerStartedEvent event) {
        try {
            var server=event.getServer(); var level=server.overworld();
            Path out=Path.of("../../analysis/original-fixtures"); Files.createDirectories(out);
            var player=new ServerPlayer(server,level,new GameProfile(UUID.fromString("d8c362cb-78c5-407d-a993-112211221122"),"OriginalProbe"),ClientInformation.createDefault());
            player.setPos(8,280,8);
            var mahou=player.getData(DataAttachments.PLAYER_ATTACHMENTS);
            mahou.setMaxMana(12345);mahou.setStoredMana(6789);mahou.setChargeRate(2.5);
            mahou.setDamageExchangeUses(7);mahou.setDeathCollectionUsesLeft(13.5f);mahou.setHasMagic(true);mahou.setGuidePage(9);
            player.getData(DataAttachments.SETTINGS_ATTACHMENTS).setIntSettings(0,List.of(10,20,30,40,50,60));
            var living=player.getData(DataAttachments.LIVING_ATTACHMENTS);living.setFearPerson(player.getUUID());
            var playerOut=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());player.saveWithoutId(playerOut);
            NbtIo.writeCompressed(playerOut.buildResult(),out.resolve("player.nbt"));
            var chunk=level.getChunkAt(new BlockPos(8,280,8));
            var chunkData=chunk.getData(DataAttachments.CHUNK_ATTACHMENTS);
            chunkData.getLakeBlocks().add(new BlockPos(8,280,8));chunkData.getFogProjectors().add(new BlockPos(9,280,8));
            chunkData.setNeedsRetroFixLake(false);chunk.markUnsaved();
            NbtIo.writeCompressed(SerializableChunkData.copyOf(level,chunk).write(),out.resolve("chunk.nbt"));
            var circle=new stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity(level,player,new net.minecraft.world.phys.Vec3(0,0,1),player.position(),.2f,.3f,.4f,1,3);
            circle.setLife(7);var circleOut=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());circle.saveWithoutId(circleOut);
            NbtIo.writeCompressed(circleOut.buildResult(),out.resolve("circle.nbt"));
            var ops=level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            CompoundTag samples=new CompoundTag(); ListTag stacks=new ListTag();
            for(var id:BuiltInRegistries.ITEM.keySet().stream().filter(id->id.getNamespace().equals("mahoutsukai")).sorted().toList()) {
                stacks.add(ItemStack.CODEC.encodeStart(ops,new ItemStack(BuiltInRegistries.ITEM.getValue(id))).getOrThrow());
            }
            var gem=new ItemStack(stepsword.mahoutsukai.item.ModItems.attunedDiamond.get());
            gem.set(stepsword.mahoutsukai.datacomponents.DataComponents.GEM_COMPONENT.get(),new stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou(4321));
            stacks.add(ItemStack.CODEC.encodeStart(ops,gem).getOrThrow());
            var sword=new ItemStack(stepsword.mahoutsukai.item.ModItems.caliburn.get());
            sword.set(stepsword.mahoutsukai.datacomponents.DataComponents.SWORD_COMPONENT.get(),new stepsword.mahoutsukai.datacomponents.sword.SwordMahou(23.5f,9000));
            stacks.add(ItemStack.CODEC.encodeStart(ops,sword).getOrThrow());
            var projected=new ItemStack(Items.DIAMOND_SWORD);
            projected.set(stepsword.mahoutsukai.datacomponents.DataComponents.SHOULD_RENDER_COMPONENT.get(),false);
            stacks.add(ItemStack.CODEC.encodeStart(ops,projected).getOrThrow());
            samples.put("items",stacks);NbtIo.writeCompressed(samples,out.resolve("items.nbt"));
            ListTag entities=new ListTag();
            for(var id:BuiltInRegistries.ENTITY_TYPE.keySet().stream().filter(id->id.getNamespace().equals("mahoutsukai")).sorted().toList()){
                var entity=BuiltInRegistries.ENTITY_TYPE.getValue(id).create(level,EntitySpawnReason.LOAD);
                if(entity==null)throw new IllegalStateException("Cannot create original entity "+id);
                var encoded=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,level.registryAccess());entity.saveWithoutId(encoded);
                var tag=encoded.buildResult();tag.putString("id",id.toString());entities.add(tag);
            }
            CompoundTag allEntities=new CompoundTag();allEntities.put("entities",entities);NbtIo.writeCompressed(allEntities,out.resolve("entities.nbt"));
            var json=new com.google.gson.JsonObject();
            for(var registry:List.of(BuiltInRegistries.ITEM,BuiltInRegistries.BLOCK,BuiltInRegistries.ENTITY_TYPE,BuiltInRegistries.MOB_EFFECT,BuiltInRegistries.BLOCK_ENTITY_TYPE,BuiltInRegistries.DATA_COMPONENT_TYPE,BuiltInRegistries.FLUID)) {
                var ids=new com.google.gson.JsonArray();registry.keySet().stream().filter(id->id.getNamespace().equals("mahoutsukai")).sorted().forEach(id->ids.add(id.toString()));json.add(registry.key().identifier().toString(),ids);
            }
            Files.writeString(out.resolve("registries.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(json));
            var config=new com.google.gson.JsonObject();
            for(var field:stepsword.mahoutsukai.config.MTConfig.class.getFields()) {
                Object value=field.get(null);
                if(value instanceof Number || value instanceof Boolean || value instanceof String || value instanceof List<?>)config.add(field.getName(),new com.google.gson.Gson().toJsonTree(value));
            }
            // A dedicated server does not load the CLIENT config: use its declared defaults.
            for(var field:stepsword.mahoutsukai.config.MTConfig.Client.class.getFields()){
                var value=(net.neoforged.neoforge.common.ModConfigSpec.ConfigValue<?>)field.get(stepsword.mahoutsukai.config.MTConfig.CLIENT);
                config.add(field.getName(),new com.google.gson.Gson().toJsonTree(value.getDefault()));
            }
            Files.writeString(out.resolve("config.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(config));
            var configNames=new com.google.gson.JsonObject();
            for(var field:stepsword.mahoutsukai.config.MTConfig.Server.class.getFields()){
                var value=(net.neoforged.neoforge.common.ModConfigSpec.ConfigValue<?>)field.get(stepsword.mahoutsukai.config.MTConfig.SERVER);
                configNames.addProperty(value.getPath().getLast(),field.getName());
            }
            Files.writeString(out.resolve("config-names.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(configNames));
            Files.writeString(out.resolve("probe.passed"),"PASS: unmodified NeoForge original emitted player, living, settings, chunk, circle, item and registry fixtures\n");
            System.out.println("PASS: ORIGINAL_PARITY_PROBE"); server.halt(false);
        } catch(Exception | AssertionError failure) {throw new IllegalStateException("Original parity probe failed",failure);}
    }
}
