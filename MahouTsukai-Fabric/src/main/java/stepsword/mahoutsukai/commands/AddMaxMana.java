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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.fabric.ManaConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.fabric.MahouNetworking;
import stepsword.mahoutsukai.fabric.MahouHolder;

public class AddMaxMana {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         Commands.literal("addmaxmahou")
            .then(
               Commands.argument("player", EntityArgument.player())
                  .then(
                     (Commands.argument("mana", IntegerArgumentType.integer(0, 1000000000))
                           .executes(ctx -> run(ctx, IntegerArgumentType.getInteger(ctx, "mana"))))
                        .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                  )
            )
      );
   }

   public static int run(CommandContext<CommandSourceStack> context, int mana) throws CommandSyntaxException {
      Player player = EntityArgument.getPlayer(context, "player");
      IMahou mahou = ((MahouHolder) player).mahoutsukai$getMahou();
      if (mahou != null && mana > 0) {
         int current_max = mahou.getMaxMana();
         int new_mana = current_max + mana;
         mahou.setMaxMana(new_mana);
         mahou.setStoredMana(new_mana);
         mahou.setManaToUseCounter(ManaConfig.current.MANA_UP_COUNTER());
         MahouNetworking.sync((ServerPlayer)player);
      }

      return 1;
   }
}

