package stepsword.mahoutsukai.item.mysticcode;

import java.util.ArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import stepsword.mahoutsukai.config.MTConfig;

public class MysticCodeFirstSorcery extends MysticCode implements MenuProvider {
   public MysticCodeFirstSorcery() {
      super("mystic_code_first_sorcery", new Properties().stacksTo(1).durability(MTConfig.FIRST_SORCERY_DURABILITY));
      this.addPropertyOverrides();
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return false;
   }

   @Override
   public boolean consumesScroll() {
      return false;
   }

   @Override
   public boolean requiresDura() {
      return true;
   }

   @Override
   public java.util.List<? extends String> getBlacklist() {
      return MTConfig.MYSTIC_CODE_FIRST_SORCERY_BLACKLIST;
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.FIRST_SORCERY_DURABILITY;
   }
}
