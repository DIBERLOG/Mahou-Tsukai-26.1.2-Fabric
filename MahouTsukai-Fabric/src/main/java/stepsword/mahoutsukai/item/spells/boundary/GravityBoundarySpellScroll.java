package stepsword.mahoutsukai.item.spells.boundary;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;

public class GravityBoundarySpellScroll extends BoundarySpellScroll {
   public GravityBoundarySpellScroll() {
      super("gravity");
   }

   @Override
   public BlockBase getBoundaryBlock() {
      return (BlockBase)ModBlocks.gravityBoundaryMahoujin.get();
   }
}
