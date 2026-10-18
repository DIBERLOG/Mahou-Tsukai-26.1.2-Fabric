package stepsword.mahoutsukai;

import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou;

public final class GemComponentCheck {
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        GemMahou first = new GemMahou(4321);
        require(new GemMahou().getStoredMana() == 0, "Original zero default");
        GemMahou second = first.copy();
        GemMahou changed = second.copy();
        changed.setStoredMana(1234);
        require(first.getStoredMana() == 4321 && second.getStoredMana() == 4321, "Copies do not alias mutable mana");
        require(changed.getStoredMana() == 1234, "Copy can change independently");
        var encoded = GemMahou.CODEC.encodeStart(JsonOps.INSTANCE, first).getOrThrow();
        require(encoded.getAsJsonObject().get("storedMana").getAsInt() == 4321, "Original persistent field name");
        require(GemMahou.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow().equals(first), "Persistent codec round trip");
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            GemMahou.STREAM_CODEC.encode(buffer, first);
            require(buffer.readableBytes() == 4, "Original int wire format");
            require(GemMahou.STREAM_CODEC.decode(buffer).getStoredMana() == 4321 && !buffer.isReadable(), "Wire round trip");
        } finally {
            buffer.release();
        }
        System.out.println("PASS: original gem zero default, copy isolation, persistent field and wire format; stack/registry integration requires Fabric runtime");
    }
}
