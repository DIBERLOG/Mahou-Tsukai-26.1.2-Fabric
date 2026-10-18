package stepsword.mahoutsukai.item;

import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.item.lance.Rhongomyniad;

/** Loaded only when the optional Jousting mod is present. */
final class JoustingItems {
    static void register() {
        ModItems.register("rhongomyniad", () -> {
            stepsword.jousting.item.ItemBase.pushRegistrationId(Identifier.fromNamespaceAndPath("mahoutsukai", "rhongomyniad"));
            try { return new Rhongomyniad(); }
            finally { stepsword.jousting.item.ItemBase.popRegistrationId(); }
        });
    }
}
