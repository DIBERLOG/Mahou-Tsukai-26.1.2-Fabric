package stepsword.mahoutsukai;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

/** Read-only verification of the actual disposable server test save. */
public final class SavedPlayerCheck {
    public static void main(String[] args) throws Exception {
        CompoundTag player = NbtIo.readCompressed(Path.of(args[0]), NbtAccounter.unlimitedHeap());
        CompoundTag mahou = player.getCompound("mahoutsukai:player_mahou").orElseThrow();
        int expectedMana = args.length > 1 ? Integer.parseInt(args[1]) : 100;
        if (mahou.getIntOr("stored_mana", -1) != expectedMana || mahou.getIntOr("max_mana", -1) != expectedMana) {
            throw new AssertionError("Player mana missing from real disk save");
        }
        Map<String, Integer> items = new HashMap<>();
        for (Tag tag : player.getListOrEmpty("Inventory")) {
            CompoundTag stack = (CompoundTag) tag;
            items.merge(stack.getStringOr("id", ""), stack.getIntOr("count", 0), Integer::sum);
        }
        if (!items.equals(Map.of("mahoutsukai:hammer", 1, "mahoutsukai:powdered_diamond", 2, "minecraft:diamond", 1))) {
            throw new AssertionError("Unexpected post-craft saved inventory: " + items);
        }
        System.out.println("PASS: actual disk player save contains original mana fields and exact post-craft inventory " + items);
    }
}
