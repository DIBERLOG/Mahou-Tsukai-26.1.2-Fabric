package stepsword.mahoutsukai.item.spells.boundary;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;

public class DisplacementBoundarySpellScroll extends BoundarySpellScroll {
   public DisplacementBoundarySpellScroll() {
      super("displacement");
   }

   @Override
   public BlockBase getBoundaryBlock() {
      return (BlockBase)ModBlocks.displacementBoundaryMahoujin.get();
   }
}
