package stepsword.mahoutsukai.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.Utils;

public class SetMahouVisible {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(Commands.literal("showmahou").executes(SetMahouVisible::run));
   }

   public static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      if (((CommandSourceStack)context.getSource()).getEntity() instanceof Player) {
         Player player = (Player)((CommandSourceStack)context.getSource()).getEntity();
         IMahou mahou = Utils.getPlayerMahou(player);
         if (!player.level().isClientSide() && mahou != null) {
            mahou.setHasMagic(true);
            mahou.setVisible(!mahou.getVisible());
            PlayerManaManager.updateClientMahou((ServerPlayer)player, mahou);
         }
      }

      return 1;
   }
}
