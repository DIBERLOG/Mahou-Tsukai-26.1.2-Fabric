package stepsword.mahoutsukai.item.spells.boundary;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;

public class RaiseEnclosureBoundarySpellScroll extends BoundarySpellScroll {
   public RaiseEnclosureBoundarySpellScroll() {
      super("raise_enclosure");
   }

   @Override
   public BlockBase getBoundaryBlock() {
      return (BlockBase)ModBlocks.raiseEnclosureBoundaryMahoujin.get();
   }
}
