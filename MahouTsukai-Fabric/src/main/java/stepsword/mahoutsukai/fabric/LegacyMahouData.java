package stepsword.mahoutsukai.fabric;

import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;

/** Reads the original 26.1.2 NeoForge envelope; native saves always take precedence. */
public final class LegacyMahouData {
    private LegacyMahouData() {}

    public static Optional<ValueInput> attachment(ValueInput input, String name) {
        String id = "mahoutsukai:" + name;
        return input.child(id).or(() -> input.child("neoforge:attachments").flatMap(a -> a.child(id)));
    }

    /** Called before Fabric deserializes chunk attachments. Does not touch other mods' data. */
    public static boolean migrateChunk(CompoundTag root) {
        var legacy = root.getCompound("neoforge:attachments")
            .flatMap(a -> a.getCompound("mahoutsukai:chunk_mahou"));
        if (legacy.isEmpty()) return false;
        var fabric = root.getCompound("fabric:attachments").orElseGet(CompoundTag::new);
        if (fabric.contains("mahoutsukai:chunk_mahou")) return false;
        fabric.put("mahoutsukai:chunk_mahou", legacy.get().copy());
        root.put("fabric:attachments", fabric);
        return true;
    }
}
