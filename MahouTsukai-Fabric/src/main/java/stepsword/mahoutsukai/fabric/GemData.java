package stepsword.mahoutsukai.fabric;

import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou;

/** Same copy-on-read behavior as original Utils.getDefaultMahou/getGemMahou. */
public final class GemData {
    public static GemMahou get(ItemStack stack) {
        GemMahou value = stack.get(DataComponents.GEM_COMPONENT);
        if (value == null) {
            value = new GemMahou();
            stack.set(DataComponents.GEM_COMPONENT, value);
            return value;
        }
        return value.copy();
    }

    public static GemMahou set(ItemStack stack, GemMahou value) {
        return stack.set(DataComponents.GEM_COMPONENT, value);
    }
}
