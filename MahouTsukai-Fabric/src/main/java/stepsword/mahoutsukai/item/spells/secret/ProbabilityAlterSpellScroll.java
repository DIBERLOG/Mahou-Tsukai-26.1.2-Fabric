package stepsword.mahoutsukai.item.spells.secret;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.rulebreaker.RuleBreaker;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class ProbabilityAlterSpellScroll extends SpellScroll {
   public ProbabilityAlterSpellScroll() {
      super("probability_alter");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.RULE_BREAKER_MANA_COST;
   }

   @Override
   public void useAction(ItemStack stack, Level level, LivingEntity entity, boolean consume) {
      if (!(!level.isClientSide() && entity instanceof Player player) || findTilesInRangeWithItemOnTop(player, false) >= 2) {
         super.useAction(stack, level, entity, consume);
      }
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack s) {
      if (scrollMahou != null) {
         int x = findTilesInRangeWithItemOnTop(user);
         if (x > 1) {
            ItemStack stack = new ItemStack((ItemLike)ModItems.ruleBreaker.get());
            RuleBreaker.setModulus(stack, x);
            RuleBreaker.setDesignation(stack, 0);
            stack.setCount(1);
            Utils.giveOrDrop(user, stack);
            return true;
         }
      }

      return false;
   }

   public static int findTilesInRangeWithItemOnTop(Player player) {
      return findTilesInRangeWithItemOnTop(player, true);
   }

   private static int findTilesInRangeWithItemOnTop(Player player, boolean consume) {
      ArrayList<BlockPos> lst = Utils.findTilesInRange(player, MTConfig.RULE_BREAKER_RANGE, a -> a instanceof ProbabilityAlterMahoujinTileEntity);
      ArrayList<ItemEntity> toRemove = new ArrayList<>();

      for (BlockPos p : lst) {
         BlockEntity te = player.level().getBlockEntity(p);
         if (te instanceof ProbabilityAlterMahoujinTileEntity) {
            for (ItemEntity item : player.level()
               .getEntitiesOfClass(ItemEntity.class, new AABB(p.getX(), p.getY(), p.getZ(), p.getX() + 1, p.getY() + 1, p.getZ() + 1))) {
               if (!toRemove.contains(item) && EffectUtil.inItemBlacklist(item.getItem().getItem(), MTConfig.RULE_BREAKER_ITEM_WHITELIST, player.level())) {
                  toRemove.add(item);
                  break;
               }
            }
         }
      }

      if (toRemove.size() < 2) {
         return 0;
      }

      if (consume) {
         toRemove.forEach(e -> e.getItem().shrink(1));
      }

      return toRemove.size();
   }
}
