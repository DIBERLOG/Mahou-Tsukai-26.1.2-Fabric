package stepsword.mahoutsukai.dataattachments.chunks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
/** Original chunk fields. Mutations are flushed once per four ticks, as in the original mod. */
public final class ChunkMahou {
 public final Set<BlockPos> lakeBlocks=new HashSet<>(),fogProjectors=new HashSet<>();
 public boolean needsRetroFix=true;
 public ChunkMahou(){}
 private ChunkMahou(List<BlockPos> lake,List<BlockPos> fog,boolean retro){lakeBlocks.addAll(lake);fogProjectors.addAll(fog);needsRetroFix=retro;}
 public ChunkMahou copy(){return new ChunkMahou(List.copyOf(lakeBlocks),List.copyOf(fogProjectors),needsRetroFix);}
 public static final Codec<ChunkMahou> CODEC=RecordCodecBuilder.create(i->i.group(
  BlockPos.CODEC.listOf().optionalFieldOf("lake_blocks",List.of()).forGetter((ChunkMahou c)->List.copyOf(c.lakeBlocks)),
  BlockPos.CODEC.listOf().optionalFieldOf("fog_projectors",List.of()).forGetter((ChunkMahou c)->List.copyOf(c.fogProjectors)),
  Codec.BOOL.optionalFieldOf("needs_retro_fix_lake",false).forGetter((ChunkMahou c)->c.needsRetroFix)
 ).apply(i,ChunkMahou::new));
 public static final StreamCodec<RegistryFriendlyByteBuf,ChunkMahou> STREAM_CODEC=ByteBufCodecs.fromCodecWithRegistries(CODEC);
 public static final AttachmentType<ChunkMahou> TYPE=AttachmentRegistry.create(Identifier.fromNamespaceAndPath("mahoutsukai","chunk_mahou"),builder->builder.initializer(ChunkMahou::new).persistent(CODEC).syncWith(STREAM_CODEC,AttachmentSyncPredicate.all()));
 public static void init(){}
}
