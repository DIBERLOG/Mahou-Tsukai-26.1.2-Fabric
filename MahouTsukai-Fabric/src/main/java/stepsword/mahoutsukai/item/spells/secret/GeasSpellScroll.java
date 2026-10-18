package stepsword.mahoutsukai.item.spells.secret;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.effects.secret.GeasEffect;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class GeasSpellScroll extends SpellScroll {
   public GeasSpellScroll() {
      super("geas");
   }

   @Override
   public int getInitialManaCost() {
      return MTConfig.GEAS_MANA_COST;
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      return GeasEffect.geasEffect(user);
   }

   public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
      // Native ItemStack.onDestroyed invokes the vanilla base callback separately.
      if (damageSource.is(DamageTypes.ON_FIRE) || damageSource.is(DamageTypes.IN_FIRE) || damageSource.is(DamageTypes.LAVA)) {
         Entity e = itemEntity.getOwner();
         if (e instanceof ServerPlayer) {
            IMahou mahou = Utils.getPlayerMahou((Player)e);
            mahou.setGeas(null);
         }
      }
   }
}
