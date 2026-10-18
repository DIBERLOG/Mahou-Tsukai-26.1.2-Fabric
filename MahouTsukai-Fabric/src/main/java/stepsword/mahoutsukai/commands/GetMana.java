package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.fabric.MahouHolder;

public class GetMana {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         Commands.literal("getmahou")
            .then(
               (Commands.argument("player", EntityArgument.player()).executes(GetMana::run))
                  .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
            )
      );
   }

   public static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      Player player = EntityArgument.getPlayer(context, "player");
      IMahou mahou = ((MahouHolder) player).mahoutsukai$getMahou();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal(mahou.getStoredMana() + ""), false);
      return mahou.getStoredMana();
   }
}


