package stepsword.mahoutsukai.effects.projection;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.clarent.Clarent;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class ClarentEffect {
   public static void clarentOnRightClick(Player player) {
      ItemStack s = player.getMainHandItem();
      if (player.level() != null
         && !player.level().isClientSide()
         && Utils.isWeapon(s.getItem())
         && StrengtheningSpellEffect.getStrengthened(s) > 0
         && !blacklisted(s, player.level())) {
         List<AreaEffectCloud> list = player.level()
            .getEntitiesOfClass(
               AreaEffectCloud.class, player.getBoundingBox().inflate(2.0), e -> e != null && e.isAlive() && e.getOwner() instanceof EnderDragon
            );
         if (!list.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = list.get(0);
            areaeffectcloudentity.setRadius(areaeffectcloudentity.getRadius() - 0.5F);
            ItemStack clarent = new ItemStack((ItemLike)ModItems.REGISTERED.get("clarent"));
            SwordMahou mahou = Utils.getSwordMahou(clarent);
            mahou.setAttackDamage(0.0F);
            Utils.setSwordMahou(clarent, mahou);
            if (MTConfig.CLARENT_UNBREAKABLE) {
               clarent.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            }

            turnSwordIntoItem(player, clarent);
            ModTriggers.CLARENT.trigger((ServerPlayer)player);
         }
      }
   }

   public static void turnSwordIntoItem(Player player, ItemStack change) {
      player.getInventory().removeItemNoUpdate(player.getInventory().getSelectedSlot());
      if (!player.getInventory().add(player.getInventory().getSelectedSlot(), change)) {
         player.drop(change, false);
      }
   }

   public static float clarentLivingHurt(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,float amount) {
      Entity e = source.getEntity();
      LivingEntity target = victim;
      if (e instanceof Player && !e.level().isClientSide() && target != null && amount > 0.0F) {
         Player player = (Player)e;
         if (player.getMainHandItem().getItem() instanceof Clarent) {
            float d1 = Clarent.calculateDamageFromWeapon(target.getMainHandItem());
            float d2 = Clarent.calculateDamageFromWeapon(target.getOffhandItem());
            d1 = Math.max(d1, d2);
            SwordMahou mahou = Utils.getSwordMahou(player.getMainHandItem());
            if (mahou != null) {
               int mana = getManaCostClarent(player.getMainHandItem(), player);
               if (mana <= 0) {
                  d1 += mahou.getAttackDamage();
               } else if (PlayerManaManager.drainMana(player, mana, false, false) == mana) {
                  d1 = (float)(d1 + getDamageFromManaClarent(mana));
               }

               mahou.setAttackDamage(0.0F);
            }

            Utils.setSwordMahou(player.getMainHandItem(), mahou);
            amount=d1;
            EffectUtil.buff(target, ModEffects.WOUNDED, false, MTConfig.CLARENT_WOUND_TICKS);
            if (target instanceof ServerPlayer) {
               ((ServerPlayer)target).sendOverlayMessage(Component.translatable("mahoutsukai.clarent.wounded"));
            }
         }
      }
         return amount;
   }

   public static int getManaCostClarent(ItemStack stack, Player player) {
      SwordMahou mahou = Utils.getSwordMahou(stack);
      int mana = 0;
      mana = (int)(MTConfig.CLARENT_MANA_PER_DAMAGE * mahou.getAttackDamage());
      return Math.min(mana, PlayerManaManager.countMana(player));
   }

   public static double getDamageFromManaClarent(int mana) {
      return mana / MTConfig.CLARENT_MANA_PER_DAMAGE;
   }

   public static void clarentLivingAttack(LivingEntity victim,net.minecraft.world.damagesource.DamageSource source,float amount) {
      LivingEntity target = victim;
      if (!target.level().isClientSide() && target.isUsingItem() && target.getUseItem().getItem() instanceof Clarent) {
         target.getUseItem().hurtAndBreak(1, target, EquipmentSlot.MAINHAND);
         SwordMahou mahou = Utils.getSwordMahou(target.getUseItem());
         mahou.setAttackDamage(Math.min(MTConfig.CLARENT_ATTACK_CAP, mahou.getAttackDamage() + amount * (float)MTConfig.CLARENT_STORED_FACTOR));
         Utils.setSwordMahou(target.getUseItem(), mahou);
      }

      Entity e = source.getEntity();
      if (e instanceof LivingEntity && ((LivingEntity)e).getMainHandItem().getItem() == ModItems.REGISTERED.get("clarent") && target.isBlocking()) {
         SwordMahou mahou = Utils.getSwordMahou(((LivingEntity)e).getMainHandItem());
         mahou.setAttackDamage(0.0F);
         Utils.setSwordMahou(((LivingEntity)e).getMainHandItem(), mahou);
      }
   }

   public static boolean blacklisted(ItemStack s, Level level) {
      List<? extends String> blacklist = MTConfig.CLARENT_SWORD_BLACKLIST;
      boolean bl = MTConfig.CLARENT_BLACKLIST_TOGGLE;
      boolean in = EffectUtil.inItemBlacklist(s.getItem(), blacklist, level);
      return bl ? in : !in;
   }
}
