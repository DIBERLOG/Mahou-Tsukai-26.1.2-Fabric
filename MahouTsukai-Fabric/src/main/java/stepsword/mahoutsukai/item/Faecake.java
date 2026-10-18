package stepsword.mahoutsukai.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.util.Utils;

public class Faecake extends ItemBase {
   public Faecake() {
      super("faecake", new Properties().food(new Builder().nutrition(3).saturationModifier(0.6F).build()));
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity living) {
      if (living instanceof Player) {
         IMahou mahou = Utils.getPlayerMahou((Player)living);
         mahou.setFaeSpawn(false);
      }

      return super.finishUsingItem(stack, world, living);
   }
}
