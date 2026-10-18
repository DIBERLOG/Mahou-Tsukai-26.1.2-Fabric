package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.datacomponents.lance.LanceMahou;
import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
import stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity;
import stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity;
import stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity;
import stepsword.mahoutsukai.entity.mahoujin.SmiteEntity;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.util.Utils;

public class MahouSummon {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext cbctx) {
      dispatcher.register(
         (((((Commands.literal(
                              "mahousummon"
                           )
                           .requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)))
                        .then(
                           Commands.literal("treasury")
                              .then(
                                 Commands.argument("stack", ItemArgument.item(cbctx))
                                    .then(
                                       Commands.argument("pos", Vec3Argument.vec3())
                                          .then(
                                             Commands.argument("anim_speed", FloatArgumentType.floatArg())
                                                .then(
                                                   Commands.argument("proj_speed", FloatArgumentType.floatArg())
                                                      .then(
                                                         Commands.argument("acc", FloatArgumentType.floatArg())
                                                            .then(
                                                               Commands.argument("r", IntegerArgumentType.integer())
                                                                  .then(
                                                                     Commands.argument("g", IntegerArgumentType.integer())
                                                                        .then(
                                                                           (Commands.argument("b", IntegerArgumentType.integer())
                                                                                 .then(
                                                                                    (Commands.argument("aim", Vec3Argument.vec3())
                                                                                          .then(
                                                                                             Commands.argument("entity", EntityArgument.entity())
                                                                                                .executes(
                                                                                                   ctx -> spawnTreasury(
                                                                                                      EntityArgument.getEntity(ctx, "entity"),
                                                                                                      ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                                                      ItemArgument.getItem(ctx, "stack"),
                                                                                                      Vec3Argument.getVec3(ctx, "pos"),
                                                                                                      FloatArgumentType.getFloat(ctx, "anim_speed"),
                                                                                                      FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                                                      FloatArgumentType.getFloat(ctx, "acc"),
                                                                                                      IntegerArgumentType.getInteger(ctx, "r"),
                                                                                                      IntegerArgumentType.getInteger(ctx, "g"),
                                                                                                      IntegerArgumentType.getInteger(ctx, "b"),
                                                                                                      Vec3Argument.getVec3(ctx, "aim")
                                                                                                   )
                                                                                                )
                                                                                          ))
                                                                                       .executes(
                                                                                          ctx -> spawnTreasury(
                                                                                             null,
                                                                                             ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                                             ItemArgument.getItem(ctx, "stack"),
                                                                                             Vec3Argument.getVec3(ctx, "pos"),
                                                                                             FloatArgumentType.getFloat(ctx, "anim_speed"),
                                                                                             FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                                             FloatArgumentType.getFloat(ctx, "acc"),
                                                                                             IntegerArgumentType.getInteger(ctx, "r"),
                                                                                             IntegerArgumentType.getInteger(ctx, "g"),
                                                                                             IntegerArgumentType.getInteger(ctx, "b"),
                                                                                             Vec3Argument.getVec3(ctx, "aim")
                                                                                          )
                                                                                       )
                                                                                 ))
                                                                              .then(
                                                                                 (Commands.argument("target", EntityArgument.entity())
                                                                                       .then(
                                                                                          Commands.argument("entity", EntityArgument.entity())
                                                                                             .executes(
                                                                                                ctx -> spawnTreasury(
                                                                                                   EntityArgument.getEntity(ctx, "entity"),
                                                                                                   ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                                                   ItemArgument.getItem(ctx, "stack"),
                                                                                                   Vec3Argument.getVec3(ctx, "pos"),
                                                                                                   FloatArgumentType.getFloat(ctx, "anim_speed"),
                                                                                                   FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                                                   FloatArgumentType.getFloat(ctx, "acc"),
                                                                                                   IntegerArgumentType.getInteger(ctx, "r"),
                                                                                                   IntegerArgumentType.getInteger(ctx, "g"),
                                                                                                   IntegerArgumentType.getInteger(ctx, "b"),
                                                                                                   EntityArgument.getEntity(ctx, "target")
                                                                                                )
                                                                                             )
                                                                                       ))
                                                                                    .executes(
                                                                                       ctx -> spawnTreasury(
                                                                                          null,
                                                                                          ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                                          ItemArgument.getItem(ctx, "stack"),
                                                                                          Vec3Argument.getVec3(ctx, "pos"),
                                                                                          FloatArgumentType.getFloat(ctx, "anim_speed"),
                                                                                          FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                                          FloatArgumentType.getFloat(ctx, "acc"),
                                                                                          IntegerArgumentType.getInteger(ctx, "r"),
                                                                                          IntegerArgumentType.getInteger(ctx, "g"),
                                                                                          IntegerArgumentType.getInteger(ctx, "b"),
                                                                                          EntityArgument.getEntity(ctx, "target")
                                                                                       )
                                                                                    )
                                                                              )
                                                                        )
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("wepproj")
                           .then(
                              Commands.argument("stack", CompoundTagArgument.compoundTag())
                                 .then(
                                    Commands.argument("pos", Vec3Argument.vec3())
                                       .then(
                                          Commands.argument("proj_speed", FloatArgumentType.floatArg())
                                             .then(
                                                (Commands.argument("acc", FloatArgumentType.floatArg())
                                                      .then(
                                                         (Commands.argument("aim", Vec3Argument.vec3())
                                                               .then(
                                                                  Commands.argument("entity", EntityArgument.entity())
                                                                     .executes(
                                                                        ctx -> spawnWeaponProjectile(
                                                                           EntityArgument.getEntity(ctx, "entity"),
                                                                           ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                           ItemArgument.getItem(ctx, "stack"),
                                                                           Vec3Argument.getVec3(ctx, "pos"),
                                                                           FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                           FloatArgumentType.getFloat(ctx, "acc"),
                                                                           Vec3Argument.getVec3(ctx, "aim")
                                                                        )
                                                                     )
                                                               ))
                                                            .executes(
                                                               ctx -> spawnWeaponProjectile(
                                                                  null,
                                                                  ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                  ItemArgument.getItem(ctx, "stack"),
                                                                  Vec3Argument.getVec3(ctx, "pos"),
                                                                  FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                  FloatArgumentType.getFloat(ctx, "acc"),
                                                                  Vec3Argument.getVec3(ctx, "aim")
                                                               )
                                                            )
                                                      ))
                                                   .then(
                                                      (Commands.argument("target", EntityArgument.entity())
                                                            .then(
                                                               Commands.argument("entity", EntityArgument.entity())
                                                                  .executes(
                                                                     ctx -> spawnWeaponProjectile(
                                                                        EntityArgument.getEntity(ctx, "entity"),
                                                                        ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                        ItemArgument.getItem(ctx, "stack"),
                                                                        Vec3Argument.getVec3(ctx, "pos"),
                                                                        FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                                        FloatArgumentType.getFloat(ctx, "acc"),
                                                                        EntityArgument.getEntity(ctx, "target")
                                                                     )
                                                                  )
                                                            ))
                                                         .executes(
                                                            ctx -> spawnWeaponProjectile(
                                                               null,
                                                               ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                               ItemArgument.getItem(ctx, "stack"),
                                                               Vec3Argument.getVec3(ctx, "pos"),
                                                               FloatArgumentType.getFloat(ctx, "proj_speed"),
                                                               FloatArgumentType.getFloat(ctx, "acc"),
                                                               EntityArgument.getEntity(ctx, "target")
                                                            )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("smite")
                        .then(
                           Commands.argument("target", EntityArgument.entities())
                              .then(
                                 Commands.argument("r", IntegerArgumentType.integer())
                                    .then(
                                       Commands.argument("g", IntegerArgumentType.integer())
                                          .then(
                                             Commands.argument("b", IntegerArgumentType.integer())
                                                .then(
                                                   Commands.argument("size", FloatArgumentType.floatArg())
                                                      .then(
                                                         Commands.argument("damage", FloatArgumentType.floatArg())
                                                            .executes(
                                                               ctx -> spawnSmite(
                                                                  EntityArgument.getEntities(ctx, "target"),
                                                                  IntegerArgumentType.getInteger(ctx, "r"),
                                                                  IntegerArgumentType.getInteger(ctx, "g"),
                                                                  IntegerArgumentType.getInteger(ctx, "b"),
                                                                  FloatArgumentType.getFloat(ctx, "size"),
                                                                  FloatArgumentType.getFloat(ctx, "damage")
                                                               )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("rhongomyniad")
                     .then(
                        Commands.argument("pos", BlockPosArgument.blockPos())
                           .then(
                              Commands.argument("damage", FloatArgumentType.floatArg())
                                 .executes(
                                    ctx -> spawnRhongomyniad(
                                       ((CommandSourceStack)ctx.getSource()).getLevel(),
                                       BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                       FloatArgumentType.getFloat(ctx, "damage")
                                    )
                                 )
                           )
                     )
               ))
            .then(
               Commands.literal("morganball")
                  .then(
                     Commands.argument("pos", Vec3Argument.vec3())
                        .then(
                           Commands.argument("aim", Vec3Argument.vec3())
                              .then(
                                 Commands.argument("speed", FloatArgumentType.floatArg())
                                    .then(
                                       Commands.argument("damage", FloatArgumentType.floatArg())
                                          .then(
                                             Commands.argument("r", IntegerArgumentType.integer())
                                                .then(
                                                   Commands.argument("g", IntegerArgumentType.integer())
                                                      .then(
                                                         Commands.argument("b", IntegerArgumentType.integer())
                                                            .then(
                                                               Commands.argument("r2", IntegerArgumentType.integer())
                                                                  .then(
                                                                     Commands.argument("g2", IntegerArgumentType.integer())
                                                                        .then(
                                                                           Commands.argument("b2", IntegerArgumentType.integer())
                                                                              .executes(
                                                                                 ctx -> spawnMorganBall(
                                                                                    ((CommandSourceStack)ctx.getSource()).getLevel(),
                                                                                    Vec3Argument.getVec3(ctx, "pos"),
                                                                                    Vec3Argument.getVec3(ctx, "aim"),
                                                                                    FloatArgumentType.getFloat(ctx, "speed"),
                                                                                    FloatArgumentType.getFloat(ctx, "damage"),
                                                                                    IntegerArgumentType.getInteger(ctx, "r"),
                                                                                    IntegerArgumentType.getInteger(ctx, "g"),
                                                                                    IntegerArgumentType.getInteger(ctx, "b"),
                                                                                    IntegerArgumentType.getInteger(ctx, "r2"),
                                                                                    IntegerArgumentType.getInteger(ctx, "g2"),
                                                                                    IntegerArgumentType.getInteger(ctx, "b2")
                                                                                 )
                                                                              )
                                                                        )
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   public static int spawnTreasury(Entity e, Level level, ItemInput item, Vec3 pos, float speed, float speed2, float acc, int r, int g, int b, Vec3 aim) throws CommandSyntaxException {
      ItemStack stack = item.createItemStack(1);
      stack = stack.isEmpty() ? new ItemStack(Items.IRON_SWORD) : stack;
      GateWeaponProjectileEntity weapon = new GateWeaponProjectileEntity(level, pos.x, pos.y, pos.z, stack.copy(), e, speed);
      weapon.setColor(Utils.colorConvert(r), Utils.colorConvert(g), Utils.colorConvert(b), 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
      weapon.shoot(aim.x - 0.5, aim.y, aim.z - 0.5, speed2, acc);
      level.addFreshEntity(weapon);
      return 1;
   }

   public static int spawnTreasury(Entity e, Level level, ItemInput item, Vec3 pos, float speed, float speed2, float acc, int r, int g, int b, Entity target) throws CommandSyntaxException {
      Vec3 aim = pos.subtract(target.position()).scale(-1.0);
      spawnTreasury(e, level, item, pos, speed, speed2, acc, r, g, b, aim.add(0.5, 0.0, 0.5));
      return 1;
   }

   public static int spawnWeaponProjectile(Entity e, Level level, ItemInput item, Vec3 p, float speed, float acc, Vec3 aim) throws CommandSyntaxException {
      ItemStack stack = item.createItemStack(1);
      stack = stack.isEmpty() ? new ItemStack(Items.IRON_SWORD) : stack;
      WeaponProjectileEntity wpe = new WeaponProjectileEntity(level, p.x, p.y, p.z, stack);
      wpe.shoot(aim.x - 0.5, aim.y, aim.z - 0.5, speed, acc);
      wpe.setOwner(e);
      if (e instanceof LivingEntity) {
         stack.hurtAndBreak(1, (LivingEntity)e, EquipmentSlot.MAINHAND);
      }

      level.addFreshEntity(wpe);
      return 1;
   }

   public static int spawnWeaponProjectile(Entity e, Level level, ItemInput item, Vec3 pos, float speed, float acc, Entity target) throws CommandSyntaxException {
      Vec3 aim = pos.subtract(target.position()).scale(-1.0);
      spawnWeaponProjectile(e, level, item, pos, speed, acc, aim.add(0.5, 0.0, 0.5));
      return 1;
   }

   public static int spawnSmite(Collection<? extends Entity> targets, int r, int g, int b, float size, float damage) throws CommandSyntaxException {
      for (Entity target : targets) {
         if (target instanceof LivingEntity) {
            SmiteEntity smite = new SmiteEntity(target.level(), (LivingEntity)target, r / 255.0F, g / 255.0F, b / 255.0F, 1.0F, size, damage);
            smite.setPos(target.getX(), target.getY(), target.getZ());
            target.level().addFreshEntity(smite);
         }
      }

      return 1;
   }

   public static int spawnRhongomyniad(Level level, BlockPos pos, float damage) throws CommandSyntaxException {
      var item=net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","rhongomyniad"));
      if(item.isEmpty())throw new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(net.minecraft.network.chat.Component.literal("Rhongomyniad item requires the unavailable Jousting integration")).create();
      ItemStack stack = new ItemStack(item.get());
      LanceMahou lance = Utils.getLanceMahou(stack);
      lance.setAttackDamage(damage);
      Utils.setLanceMahou(stack, lance);
      RhongomyniadEntity rhongomyniad = new RhongomyniadEntity(level, 1.0F, stack);
      BlockPos p = pos.relative(Direction.UP);
      rhongomyniad.setPos(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
      level.addFreshEntity(rhongomyniad);
      return 1;
   }

   public static int spawnMorganBall(Level level, Vec3 pos, Vec3 aim, float speed, float damage, int r, int g, int b, int r2, int g2, int b2) throws CommandSyntaxException {
      MorganBallEntity ball = new MorganBallEntity(
         level,
         null,
         Utils.colorConvert(r),
         Utils.colorConvert(g),
         Utils.colorConvert(b),
         0.3F,
         Utils.colorConvert(r2),
         Utils.colorConvert(g2),
         Utils.colorConvert(b2),
         0.0F,
         damage
      );
      Vec3 eye = pos;
      ball.setPos(eye.x, eye.y, eye.z);
      ball.origPos = new Vec3(eye.x, eye.y, eye.z);
      ball.setDeltaMovement(aim.normalize().scale(speed));
      level.addFreshEntity(ball);
      return 1;
   }
}
