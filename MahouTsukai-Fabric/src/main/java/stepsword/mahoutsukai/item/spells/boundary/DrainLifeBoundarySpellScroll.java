package stepsword.mahoutsukai.item.spells.boundary;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;

public class DrainLifeBoundarySpellScroll extends BoundarySpellScroll {
   public DrainLifeBoundarySpellScroll() {
      super("drain_life");
   }

   @Override
   public BlockBase getBoundaryBlock() {
      return (BlockBase)ModBlocks.drainLifeBoundaryMahoujin.get();
   }
}
