package stepsword.mahoutsukai.checks.mixin;
import java.util.*;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(PlayerList.class)
public interface PlayerListAccessor {
 @Accessor("playersByUUID") Map<UUID,ServerPlayer> mahoutsukai$playersByUUID();
}
