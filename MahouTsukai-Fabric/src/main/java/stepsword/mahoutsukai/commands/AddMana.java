package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;


public class AddMana {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         Commands.literal("addmahou")
            .then(
               Commands.argument("player", EntityArgument.player())
                  .then(
                     Commands.argument("mana", IntegerArgumentType.integer(-1000000000, 1000000000))
                        .then(
                           (Commands.argument("overDrainHurts", FloatArgumentType.floatArg(0.0F, 1.0E9F))
                                 .then(
                                    Commands.argument("damagetype", IntegerArgumentType.integer(0, 2))
                                       .executes(
                                          ctx -> run(
                                             ctx,
                                             IntegerArgumentType.getInteger(ctx, "mana"),
                                             FloatArgumentType.getFloat(ctx, "overDrainHurts"),
                                             IntegerArgumentType.getInteger(ctx, "damagetype")
                                          )
                                       )
                                 ))
                              .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        )
                  )
            )
      );
   }

   public static int run(CommandContext<CommandSourceStack> context, int mana, float overhurts, int damagetype) throws CommandSyntaxException {
      Player player = EntityArgument.getPlayer(context, "player");
      IMahou mahou = ((stepsword.mahoutsukai.fabric.MahouHolder)player).mahoutsukai$getMahou();
      if (mahou != null) {
         if (mana < 0) {
            mana = -mana;
            int spent = PlayerManaManager.drainMana(player, mana, false, false, false, false, true);

            DamageSource type = switch (damagetype) {
               case 0 -> player.damageSources().generic();
               case 1 -> player.damageSources().magic();
               case 2 -> player.damageSources().fellOutOfWorld();
               default -> player.damageSources().magic();
            };
            if (spent < mana && overhurts > 0.0F) {
               float dmg = overhurts * (mana - spent);
               player.hurtServer((net.minecraft.server.level.ServerLevel)player.level(), type, dmg);
            }
         } else {
            mahou.setStoredMana(Math.min(mahou.getMaxMana(), mahou.getStoredMana() + mana));
         }

         PlayerManaManager.updateClientMahou((ServerPlayer)player, mahou);
      }

      return 1;
   }
}

