package stepsword.mahoutsukai.item.attunedgems;

import stepsword.mahoutsukai.config.MTConfig;

public class AttunedDiamond extends AttunedGem {
   public static int GEM_MAX_MANA = 10000;

   public AttunedDiamond() {
      super("diamond", GEM_MAX_MANA);
   }

   @Override
   public int getMaxMana() {
      return MTConfig.ATTUNED_DIAMOND_CAPACITY;
   }
}
