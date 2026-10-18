package stepsword.mahoutsukai.item.spells.secret;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.entity.mahoujin.GandrEntity;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.MagicCircleBoomParticlePacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.util.Utils;

public class GandrSpellScroll extends SpellScroll {
   public static HashMap<UUID, GandrSpellScroll.GandrUserStorage> gandrs = new HashMap<>();

   public GandrSpellScroll() {
      super("gandr");
   }

   @Override
   public int getInitialManaCost() {
      return 0;
   }

   public int getManaCostScaling(IMahou mahou) {
      boolean manascales = MTConfig.GANDR_MANA_SCALES;
      double factor = MTConfig.GANDR_MANA_COST_FACTOR;
      return manascales ? (int)Math.floor(factor * mahou.getMaxMana()) : (int)Math.floor(factor);
   }

   public double getDamage(IMahou mahou) {
      boolean scales = MTConfig.GANDR_DAMAGE_SCALES;
      double factor = MTConfig.GANDR_DAMAGE_FACTOR;
      double mindamage = MTConfig.GANDR_MIN_DAMAGE;
      double maxdamage = MTConfig.GANDR_MAX_DAMAGE;
      double ret = 0.0;
      if (mahou != null) {
         if (scales) {
            ret = mahou.getMaxMana() * factor;
         } else {
            ret = factor;
         }

         ret = Math.max(mindamage, Math.min(ret, maxdamage));
      }

      return ret;
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      if (!player.level().isClientSide()) {
         if (gandrs.containsKey(player.getUUID())) {
            GandrSpellScroll.GandrUserStorage storage = gandrs.get(player.getUUID());
         } else {
            GandrSpellScroll.GandrUserStorage storage = new GandrSpellScroll.GandrUserStorage();
            gandrs.put(player.getUUID(), storage);
            if (player instanceof Player) {
               List<Integer> settings = MahouSettings.getIntSettings((Player)player, MahouSettings.Spell.GANDR);
               storage.gandr = new GandrEntity(
                  player.level(),
                  player,
                  Utils.colorConvert(settings.get(0)),
                  Utils.colorConvert(settings.get(1)),
                  Utils.colorConvert(settings.get(2)),
                  1.0F,
                  Utils.colorConvert(settings.get(3)),
                  Utils.colorConvert(settings.get(4)),
                  Utils.colorConvert(settings.get(5))
               );
               storage.gandr.setLookPos(player.level().isClientSide(), player, storage.gandr);
               player.level().addFreshEntity(storage.gandr);
            }
         }
      }
   }

   @Override
   public void useAction(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean consume) {
      if (!worldIn.isClientSide()) {
         ScrollMahou scrollMahou = Utils.getScrollMahou(stack);
         if (entityLiving instanceof Player user) {
            int cost = this.getManaCostScaling(Utils.getPlayerMahou((Player)entityLiving));
            if (matchCaster(user, scrollMahou)) {
               if (PlayerManaManager.drainMana(user, cost, false, false) == cost) {
                  if (this.shouldConsume(user, scrollMahou, consume, stack)) {
                     stack.shrink(1);
                  }
               } else if (gandrs.containsKey(user.getUUID())) {
                  GandrSpellScroll.GandrUserStorage storage = gandrs.get(user.getUUID());
                  storage.gandr.discard();
                  gandrs.remove(user.getUUID());
               }
            }
         }
      }
   }

   @Override
   public boolean doSpell(Player user, ScrollMahou scrollMahou, ItemStack stack) {
      double speed = MTConfig.GANDR_SPEED;
      int launch_particles = MTConfig.GANDR_RED_PARTICLES_LAUNCH;
      if (!gandrs.containsKey(user.getUUID())) {
         return false;
      }

      GandrSpellScroll.GandrUserStorage storage = gandrs.get(user.getUUID());
      if (!(storage.gandr.getLife() > storage.gandr.getGrowingLife())) {
         storage.gandr.discard();
         gandrs.remove(user.getUUID());
         return false;
      }

      storage.gandr.setFired(true);
      storage.gandr.effects = new ArrayList<>(user.getActiveEffects());
      storage.gandr.damage = (float)this.getDamage(Utils.getPlayerMahou(user));
      Iterator<MobEffectInstance> iter = user.getActiveEffects().iterator();
      ArrayList<Holder<MobEffect>> lst = new ArrayList<>();

      while (iter.hasNext()) {
         MobEffectInstance ei = iter.next();
         if (GandrEntity.validEffect(ei, user.level())) {
            lst.add(ei.getEffect());
         }
      }

      for (Holder<MobEffect> e : lst) {
         user.removeEffect(e);
      }

      Vec3 v = user.getLookAngle().normalize().scale(speed);
      storage.gandr.setDeltaMovement(v.x, v.y, v.z);
      Vec3 look = storage.gandr.getViewVector(1.0F);
      float[] rgbs = storage.gandr.getColors();
      PacketHandler.sendTracking(
         storage.gandr,
         new MagicCircleBoomParticlePacket(
            storage.gandr.getX(),
            storage.gandr.getY(),
            storage.gandr.getZ(),
            look.x,
            look.y,
            look.z,
            0.2F,
            0,
            0,
            launch_particles,
            false,
            false,
            rgbs[0],
            rgbs[1],
            rgbs[2],
            rgbs[4],
            rgbs[5],
            rgbs[6]
         )
      );
      PacketHandler.sendTracking(
         storage.gandr,
         new MagicCircleBoomParticlePacket(
            storage.gandr.getX(),
            storage.gandr.getY(),
            storage.gandr.getZ(),
            look.x,
            look.y,
            look.z,
            0.3F,
            1,
            0,
            launch_particles,
            false,
            false,
            rgbs[0],
            rgbs[1],
            rgbs[2],
            rgbs[4],
            rgbs[5],
            rgbs[6]
         )
      );
      gandrs.remove(user.getUUID());
      return true;
   }

   public class GandrUserStorage {
      GandrEntity gandr;
   }
}
