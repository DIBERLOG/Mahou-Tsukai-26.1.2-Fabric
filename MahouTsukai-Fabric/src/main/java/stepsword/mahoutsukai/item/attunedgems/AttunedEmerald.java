package stepsword.mahoutsukai.item.attunedgems;

import stepsword.mahoutsukai.config.MTConfig;

public class AttunedEmerald extends AttunedGem {
   public static final int GEM_MAX_MANA = 5000;

   public AttunedEmerald() {
      super("emerald", 5000);
   }

   @Override
   public int getMaxMana() {
      return MTConfig.ATTUNED_EMERALD_CAPACITY;
   }
}
