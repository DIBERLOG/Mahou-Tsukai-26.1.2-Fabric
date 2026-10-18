package stepsword.mahoutsukai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import stepsword.mahoutsukai.networking.PlayerMahouPacket;
import stepsword.mahoutsukai.dataattachments.livingmahou.LivingMahou;
import stepsword.mahoutsukai.dataattachments.livingmahou.LivingMahouStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import stepsword.mahoutsukai.dataattachments.mahou.Mahou;
import stepsword.mahoutsukai.dataattachments.mahou.MahouStorage;
import stepsword.mahoutsukai.fabric.FunRequirement;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.UnorderedList;

public final class PortSelfCheck {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        ManaProgressionCheck.run();
        EconomyConfigCheck.run();
        Mahou original = new Mahou();
        require(original.getStoredMana() == 100 && original.getMaxMana() == 100, "Original defaults");
        original.setStoredMana(53);
        original.setMaxMana(750);
        original.setChargeRate(0.75);
        original.setHasMagic(true);
        original.setProtectiveDisplacement(4);
        original.setEnemy(UUID.fromString("50b48fd0-8020-48ec-a7b7-642aa68dccce"));
        original.setMemorizedEDMTE(new BlockDim(new BlockPos(31, 80, -19), Identifier.parse("minecraft:overworld")));
        original.setGeas(new HashSet<>(List.of("test-oath")));
        HashMap<String, Boolean> fun = new HashMap<>();
        for (FunRequirement req : FunRequirement.values()) fun.put(req.name(), false);
        fun.put(FunRequirement.SHOOT_A_SWORD.name(), true);
        original.setMandatoryFun(fun);
        UnorderedList recipe = new UnorderedList();
        recipe.add("diamond", true);
        recipe.add("iron", true);
        recipe.add("diamond", true);
        original.setLastRecipe(recipe);
        UnorderedList reordered = new UnorderedList();
        reordered.add("diamond", true);
        reordered.add("diamond", true);
        reordered.add("iron", true);
        require(recipe.equals(reordered) && recipe.hashCode() == reordered.hashCode(), "Ritual multiset ordering");
        reordered.remove("diamond");
        require(!recipe.equals(reordered), "Ritual multiplicity");

        ProblemReporter reporter = new ProblemReporter() {
            public ProblemReporter forChild(PathElement path) { return this; }
            public void report(Problem problem) { throw new AssertionError(problem.toString()); }
        };
        TagValueOutput output = TagValueOutput.createWithoutContext(reporter);
        MahouStorage.writeNBT(original, output);
        Mahou loaded = new Mahou();
        MahouStorage.readNBT(loaded, TagValueInput.create(reporter, RegistryAccess.EMPTY, output.buildResult()));
        TagValueOutput reencoded = TagValueOutput.createWithoutContext(reporter);
        MahouStorage.writeNBT(loaded, reencoded);
        require(output.buildResult().equals(reencoded.buildResult()), "Full saved tag round trip");
        require(loaded.getStoredMana() == 53 && loaded.getMaxMana() == 750, "Mana persistence");
        require(loaded.getLastRecipe().equals(recipe), "Ritual recipe persistence");
        require(loaded.getMandatoryFun().get("SHOOT_A_SWORD"), "Progress persistence");
        ByteBuf wire = Unpooled.buffer();
        try {
            PlayerMahouPacket.STREAM_CODEC.encode(wire, new PlayerMahouPacket(original));
            PlayerMahouPacket received = PlayerMahouPacket.STREAM_CODEC.decode(wire);
            require(!wire.isReadable(), "Original packet consumed exactly");
            TagValueOutput packetTag = TagValueOutput.createWithoutContext(reporter);
            MahouStorage.writeNBT(received.state(), packetTag);
            require(output.buildResult().equals(packetTag.buildResult()), "Full original packet round trip");
        } finally {
            wire.release();
        }
        loaded.clearBuffs();
        require(loaded.getProtectiveDisplacement() == 0 && loaded.getStoredMana() == 53, "Buff reset preserves mana");
        LivingMahou living = new LivingMahou();
        living.setKodoku(7);
        living.setAuthorityHit(true);
        living.setActing(3);
        living.setFearPerson(original.getEnemy());
        living.setLastWoundedPos(new Vec3(12, 70, -40));
        living.setActingLoc(new Vec3(-2, 55, 30));
        living.setImmunityDamageType("minecraft:magic");
        TagValueOutput livingTag = TagValueOutput.createWithoutContext(reporter);
        LivingMahouStorage.write(living, livingTag);
        LivingMahou livingReloaded = new LivingMahou();
        LivingMahouStorage.read(livingReloaded, TagValueInput.create(reporter, RegistryAccess.EMPTY, livingTag.buildResult()));
        TagValueOutput livingAgain = TagValueOutput.createWithoutContext(reporter);
        LivingMahouStorage.write(livingReloaded, livingAgain);
        require(livingTag.buildResult().equals(livingAgain.buildResult()), "Full living mahou round trip");
        livingReloaded.clearBuffs();
        require(livingReloaded.getKodoku() == 7 && livingReloaded.getFearPerson() == null, "Original living buff reset");
        System.out.println("PASS: original defaults, ritual multiplicity, NBT round trip, mana/progress preservation and buff reset");
    }
}
