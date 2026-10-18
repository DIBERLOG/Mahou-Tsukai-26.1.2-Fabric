package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import stepsword.mahoutsukai.dataattachments.worldsave.FictionData;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.util.Utils;

public class ClearPlayerFictions {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clearplayerfictions").executes(ctx -> run(ctx))).requires(cs -> true)
      );
   }

   public static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      var player=context.getSource().getPlayerOrException();
      MahouSavedData data=Utils.getWorldMahou(player.level());
      data.playerCreations.entrySet().removeIf(entry -> player.getUUID().equals(entry.getValue()));
      data.setDirty();
      return 1;
   }
}
