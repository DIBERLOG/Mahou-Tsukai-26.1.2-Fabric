package stepsword.mahoutsukai.tile.exchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.catalysts.PowderedCatalyst;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;

public class CatalystExchangeMahoujinTileEntity extends MahoujinTickingTileEntity implements AboveChecker {
   public CatalystExchangeMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.catalystExchange.get(), p, bs);
   }

   @Override
   public boolean preConditions() {
      return ActiveCircleConfig.tryToOperate(this, this.getCasterUUID());
   }

   @Override
   public boolean ifItemFoundAbove(ItemEntity item) {
      Set<String> ing = MahoujinRecipeRegistrar.ingredients.keySet();
      List<String> ings = new ArrayList<>(ing);
      if (item.getItem().getItem() instanceof PowderedCatalyst) {
         for (int i = 0; i < item.getItem().getCount(); i++) {
            boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
               this.worldPosition, this.level, this.getCasterUUID(), MTConfig.CATALYST_EXCHANGE_MANA_COST
            );
            if (!nearbyBatteriesGaveMana
               && (
                  this.getCaster() == null
                     || PlayerManaManager.drainMana(this.getCaster(), MTConfig.CATALYST_EXCHANGE_MANA_COST, false, false)
                        != MTConfig.CATALYST_EXCHANGE_MANA_COST
               )) {
               return true;
            }

            ItemBase z = MahoujinRecipeRegistrar.ingredients.get(ings.get(this.level.getRandom().nextInt(ings.size())));
            ItemStack stack = new ItemStack(z);
            stack.setCount(1);
            this.level.addFreshEntity(new ItemEntity(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack));
            item.getItem().shrink(1);
            if (item.getItem().getCount() == 0) {
               item.discard();
            }
         }
      }

      return false;
   }

   @Override
   public int checkTicks() {
      return MTConfig.CATALYST_EXCHANGE_BLOCK_CYCLE;
   }

   @Override
   public List<Class<? extends Item>> checkForItemClass() {
      return Arrays.asList(PowderedCatalyst.class);
   }

   @Override
   public boolean firstEntityOnly() {
      return false;
   }
}
