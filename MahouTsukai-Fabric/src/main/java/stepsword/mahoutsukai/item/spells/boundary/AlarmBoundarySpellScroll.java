package stepsword.mahoutsukai.item.spells.boundary;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;

public class AlarmBoundarySpellScroll extends BoundarySpellScroll {
   public AlarmBoundarySpellScroll() {
      super("alarm");
   }

   @Override
   public BlockBase getBoundaryBlock() {
      return (BlockBase)ModBlocks.alarmBoundaryMahoujin.get();
   }
}
