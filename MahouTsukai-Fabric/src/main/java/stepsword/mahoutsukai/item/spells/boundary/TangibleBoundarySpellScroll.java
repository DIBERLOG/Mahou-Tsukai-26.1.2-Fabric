package stepsword.mahoutsukai.item.spells.boundary;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;

public class TangibleBoundarySpellScroll extends BoundarySpellScroll {
   public TangibleBoundarySpellScroll() {
      super("tangible");
   }

   @Override
   public BlockBase getBoundaryBlock() {
      return (BlockBase)ModBlocks.tangibleBoundaryMahoujin.get();
   }
}
