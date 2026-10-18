package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.util.Utils;

public class ClearFictions {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(Commands.literal("clearfictions").requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)).executes(ClearFictions::run));
   }

   public static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      MahouSavedData data = Utils.getWorldMahou(((CommandSourceStack)context.getSource()).getLevel());
      data.playerCreations.clear();
      data.setDirty();
      return 1;
   }
}
