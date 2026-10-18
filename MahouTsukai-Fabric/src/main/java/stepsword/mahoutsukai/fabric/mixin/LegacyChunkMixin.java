package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stepsword.mahoutsukai.fabric.LegacyMahouData;

@Mixin(SerializableChunkData.class)
public abstract class LegacyChunkMixin {
    @Unique private boolean mahoutsukai$imported;
    @Inject(method = "parse", at = @At("HEAD"))
    private static void mahoutsukai$legacyChunk(net.minecraft.world.level.LevelHeightAccessor height, net.minecraft.world.level.chunk.PalettedContainerFactory factory, CompoundTag tag,
            CallbackInfoReturnable<SerializableChunkData> cir) {
        if(LegacyMahouData.migrateChunk(tag))tag.putBoolean("mahoutsukai:legacy_imported",true);
    }
    @Inject(method="parse",at=@At("RETURN"))
    private static void mahoutsukai$rememberImport(net.minecraft.world.level.LevelHeightAccessor height,net.minecraft.world.level.chunk.PalettedContainerFactory factory,CompoundTag tag,CallbackInfoReturnable<SerializableChunkData> cir){
        if(cir.getReturnValue()!=null && tag.getBooleanOr("mahoutsukai:legacy_imported",false))((LegacyChunkMixin)(Object)cir.getReturnValue()).mahoutsukai$imported=true;
    }
    @Inject(method="read",at=@At("RETURN"))
    private void mahoutsukai$saveImport(net.minecraft.server.level.ServerLevel level,net.minecraft.world.entity.ai.village.poi.PoiManager poi,net.minecraft.world.level.chunk.storage.RegionStorageInfo info,net.minecraft.world.level.ChunkPos pos,CallbackInfoReturnable<net.minecraft.world.level.chunk.ProtoChunk> cir){
        if(mahoutsukai$imported && cir.getReturnValue()!=null)cir.getReturnValue().markUnsaved();
    }
}
