package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.item.kodoku.KodokuItem;

public class SetKodoku {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         Commands.literal("mahoukodoku")
            .then(
               (Commands.argument("player", EntityArgument.player())
                     .then(
                        Commands.argument("kodoku", IntegerArgumentType.integer(0, 1000000000))
                           .executes(ctx -> run(ctx, IntegerArgumentType.getInteger(ctx, "kodoku")))
                     ))
                  .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
            )
      );
   }

   public static int run(CommandContext<CommandSourceStack> context, int k) throws CommandSyntaxException {
      Player player = EntityArgument.getPlayer(context, "player");
      KodokuItem.setKodoku(player.getMainHandItem(), k);
      return 1;
   }
}
