package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.lance.LanceMahou;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.item.clarent.Clarent;

import stepsword.mahoutsukai.item.morgan.Morgan;
import stepsword.mahoutsukai.item.rulebreaker.RuleBreaker;
import stepsword.mahoutsukai.util.Utils;

public class SetMahouX {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         ((((((((((Commands.literal(
                                             "mahouset"
                                          )
                                          .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)))
                                       .then(
                                          Commands.literal("souls")
                                             .then(
                                                Commands.argument("player", EntityArgument.player())
                                                   .then(
                                                      Commands.argument("count", IntegerArgumentType.integer())
                                                         .executes(
                                                            ctx -> setSouls(
                                                               EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "count")
                                                            )
                                                         )
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("morgan")
                                          .then(
                                             Commands.argument("player", EntityArgument.player())
                                                .then(
                                                   Commands.argument("damage", IntegerArgumentType.integer())
                                                      .executes(
                                                         ctx -> setMorgan(
                                                            EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "damage"), false
                                                         )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("caliburn")
                                       .then(
                                          Commands.argument("player", EntityArgument.player())
                                             .then(
                                                Commands.argument("damage", IntegerArgumentType.integer())
                                                   .executes(
                                                      ctx -> setCaliburn(
                                                         EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "damage"), false
                                                      )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("clarent")
                                    .then(
                                       Commands.argument("player", EntityArgument.player())
                                          .then(
                                             Commands.argument("damage", IntegerArgumentType.integer())
                                                .executes(
                                                   ctx -> setClarent(
                                                      EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "damage"), false
                                                   )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("morgancap")
                                 .then(
                                    Commands.argument("player", EntityArgument.player())
                                       .then(
                                          Commands.argument("damage_limit", IntegerArgumentType.integer())
                                             .executes(
                                                ctx -> setMorgan(
                                                   EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "damage_limit"), true
                                                )
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("caliburncap")
                              .then(
                                 Commands.argument("player", EntityArgument.player())
                                    .then(
                                       Commands.argument("damage_limit", IntegerArgumentType.integer())
                                          .executes(
                                             ctx -> setCaliburn(
                                                EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "damage_limit"), true
                                             )
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("rulebreakerd")
                           .then(
                              Commands.argument("player", EntityArgument.player())
                                 .then(
                                    Commands.argument("d", IntegerArgumentType.integer())
                                       .executes(ctx -> setRuleBreaker(EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "d"), true))
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("rulebreakerm")
                        .then(
                           Commands.argument("player", EntityArgument.player())
                              .then(
                                 Commands.argument("m", IntegerArgumentType.integer())
                                    .executes(ctx -> setRuleBreaker(EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "m"), false))
                              )
                        )
                  ))
               .then(
                  Commands.literal("rhongomyniad")
                     .then(
                        Commands.argument("player", EntityArgument.player())
                           .then(
                              Commands.argument("damage", IntegerArgumentType.integer())
                                 .executes(
                                    ctx -> setRhongomyniad(EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "damage"), false)
                                 )
                           )
                     )
               ))
            .then(
               Commands.literal("rhongomyniadweight")
                  .then(
                     Commands.argument("player", EntityArgument.player())
                        .then(
                           Commands.argument("weight", IntegerArgumentType.integer())
                              .executes(ctx -> setRhongomyniad(EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "weight"), true))
                        )
                  )
            )
      );
   }

   public static int setMorgan(Player player, int x, boolean cap) throws CommandSyntaxException {
      if (player.getMainHandItem().getItem() instanceof Morgan) {
         SwordMahou mahou = Utils.getSwordMahou(player.getMainHandItem());
         if (!cap) {
            mahou.setAttackDamage((float)Math.min(mahou.getInnateCap(), x));
         } else {
            mahou.setInnateCap(x);
         }

         Utils.setSwordMahou(player.getMainHandItem(), mahou);
         Utils.updateHeldItem(player);
      }

      return 1;
   }

   public static int setCaliburn(Player player, int x, boolean cap) throws CommandSyntaxException {
      if (player.getMainHandItem().getItem() instanceof Caliburn) {
         SwordMahou mahou = Utils.getSwordMahou(player.getMainHandItem());
         if (mahou != null) {
            if (!cap) {
               mahou.setAttackDamage((float)Math.min(mahou.getInnateCap(), x));
            } else {
               mahou.setInnateCap(x);
            }

            Utils.setSwordMahou(player.getMainHandItem(), mahou);
            Utils.updateHeldItem(player);
         }
      }

      return 1;
   }

   public static int setClarent(Player player, int x, boolean cap) throws CommandSyntaxException {
      if (player.getMainHandItem().getItem() instanceof Clarent) {
         SwordMahou mahou = Utils.getSwordMahou(player.getMainHandItem());
         if (mahou != null) {
            if (!cap) {
               mahou.setAttackDamage(Math.min(MTConfig.CLARENT_ATTACK_CAP, x));
            } else {
               mahou.setInnateCap(x);
            }

            Utils.setSwordMahou(player.getMainHandItem(), mahou);
            Utils.updateHeldItem(player);
         }
      }

      return 1;
   }

   public static int setRuleBreaker(Player player, int x, boolean d) throws CommandSyntaxException {
      if (player.getMainHandItem().getItem() instanceof RuleBreaker) {
         if (d) {
            RuleBreaker.setDesignation(player.getMainHandItem(), x);
         } else {
            RuleBreaker.setModulus(player.getMainHandItem(), x);
         }

         Utils.updateHeldItem(player);
      }

      return 1;
   }

   public static int setRhongomyniad(Player player, int x, boolean weight) throws CommandSyntaxException {
      if (player.getMainHandItem().has(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT)) {
         LanceMahou mahou = Utils.getLanceMahou(player.getMainHandItem());
         if (mahou == null) {
            mahou = new LanceMahou();
         }

         if (!weight) {
            mahou.setAttackDamage(x);
         } else {
            mahou.setWeight(x);
         }

         Utils.setLanceMahou(player.getMainHandItem(), mahou);
         Utils.updateHeldItem(player);
      }

      return 1;
   }

   public static int setSouls(Player player, int x) throws CommandSyntaxException {
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         mahou.setDeathCollectionUsesLeft(x);
      }

      return 1;
   }
}
