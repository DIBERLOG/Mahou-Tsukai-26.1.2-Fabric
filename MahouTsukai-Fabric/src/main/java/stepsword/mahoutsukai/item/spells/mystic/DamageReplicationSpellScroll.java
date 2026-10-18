package stepsword.mahoutsukai.item.spells.mystic;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.mystic.DamageReplicationSpellEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;

public class DamageReplicationSpellScroll extends SpellScroll {
   public DamageReplicationSpellScroll() {
      super("damage_replication");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.DAMAGE_REPLICATION_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return scrollMahou != null ? DamageReplicationSpellEffect.damageReplication(user) : false;
   }
}
