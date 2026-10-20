package stepsword.mahoutsukai.dataattachments.worldsave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

/** Persistent pointer to each arena's boss; recovery loads only its last saved chunk. */
public final class RealityMarbleEncounters extends SavedData {
 public record Encounter(BlockPos arena, UUID boss, ChunkPos chunk) {
  private static final Codec<Encounter> CODEC=RecordCodecBuilder.create(instance->instance.group(
   BlockPos.CODEC.fieldOf("arena").forGetter(Encounter::arena),
   UUIDUtil.CODEC.fieldOf("boss").forGetter(Encounter::boss),
   ChunkPos.CODEC.fieldOf("chunk").forGetter(Encounter::chunk)
  ).apply(instance,Encounter::new));
 }
 public static final Codec<RealityMarbleEncounters> CODEC=Encounter.CODEC.listOf().xmap(RealityMarbleEncounters::new,data->new ArrayList<>(data.encounters.values()));
 public static final SavedDataType<RealityMarbleEncounters> TYPE=new SavedDataType<>(
  Identifier.fromNamespaceAndPath("mahoutsukai","reality_marble_encounters"),RealityMarbleEncounters::new,CODEC,null);
 private final Map<BlockPos,Encounter> encounters=new HashMap<>();

 private RealityMarbleEncounters(){this(List.of());}
 private RealityMarbleEncounters(List<Encounter> records){
  for(var record:records){if(near(record.arena())==null)encounters.put(record.arena(),record);else setDirty();}
 }
 public static RealityMarbleEncounters get(ServerLevel level){return level.getDataStorage().computeIfAbsent(TYPE);}
 @Nullable public Encounter near(BlockPos arena){
  var exact=encounters.get(arena);if(exact!=null)return exact;
  for(var entry:encounters.values())if(entry.arena().distSqr(arena)<=64L*64L)return entry;
  return null;
 }
 @Nullable public Encounter byBoss(UUID id){
  for(var entry:encounters.values())if(entry.boss().equals(id))return entry;
  return null;
 }
 /** First claim wins: loading an old duplicate must never replace this arena's opponent. */
 public boolean remember(BlockPos arena,UUID id,ChunkPos chunk){
  var previous=near(arena);
  if(previous!=null&&!previous.boss().equals(id))return false;
  var next=new Encounter(previous==null?arena.immutable():previous.arena(),id,chunk);
  if(!next.equals(encounters.put(next.arena(),next)))setDirty();
  return true;
 }
 public void forget(BlockPos arena,UUID id){
  var previous=near(arena);
  if(previous!=null&&previous.boss().equals(id)){encounters.remove(previous.arena());setDirty();}
 }
}
