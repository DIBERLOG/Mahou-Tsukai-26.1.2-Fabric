package stepsword.mahoutsukai.tile.displacement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.displacement.AscensionSpellEffect;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.ScrollGiver;

public class AscensionMahoujinTileEntity extends MahoujinTickingTileEntity implements ScrollGiver, AboveChecker {
   public AscensionMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.ascension.get(), p, bs);
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_ascension"));
   }

   @Override
   public int checkTicks() {
      return MTConfig.ASCENSION_BLOCK_CYCLE;
   }

   @Override
   public boolean ifEntityFoundAbove(Entity e) {
      AscensionSpellEffect.teleportEntityToSurface(
         this.worldPosition.getX(), this.worldPosition.getZ(), e, this.worldPosition.getY(), this.getCaster(), this.getCasterUUID(), true
      );
      return false;
   }

   @Override
   public boolean preConditions() {
      return !this.level.isClientSide() && ActiveCircleConfig.tryToOperate(this, this.getCasterUUID());
   }

   @Override
   public Class<? extends Entity> checkForEntity() {
      return Entity.class;
   }
}
