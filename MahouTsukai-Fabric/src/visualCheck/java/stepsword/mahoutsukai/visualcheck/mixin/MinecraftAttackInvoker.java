package stepsword.mahoutsukai.visualcheck.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Test-only access to the same native method called by a real left click. */
@Mixin(Minecraft.class)
public interface MinecraftAttackInvoker {
    @Invoker("startAttack") boolean mahoutsukai$startAttack();
    @Invoker("handleKeybinds") void mahoutsukai$handleKeybinds();
}
