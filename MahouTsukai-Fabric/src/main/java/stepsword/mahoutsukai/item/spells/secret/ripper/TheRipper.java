package stepsword.mahoutsukai.item.spells.secret.ripper;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.effects.exchange.ImmunityExchangeSpellEffect;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.networking.FogPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.potion.RipperInvisPotion;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class TheRipper extends ItemBase {
   public TheRipper() {
      super("theripper", 1, MTConfig.RIPPER_DURABILITY);
   }

   public static net.minecraft.world.item.component.ItemAttributeModifiers attributes(ItemStack stack,net.minecraft.world.item.component.ItemAttributeModifiers original){
      if(!(stack.getItem() instanceof TheRipper))return original;
      var builder=net.minecraft.world.item.component.ItemAttributeModifiers.builder();
      for(var entry:original.modifiers())if(!entry.modifier().id().equals(BASE_ATTACK_DAMAGE_ID) && !entry.modifier().id().equals(BASE_ATTACK_SPEED_ID))builder.add(entry.attribute(),entry.modifier(),entry.slot());
      builder.add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,MTConfig.RIPPER_DAMAGE,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,MTConfig.RIPPER_DAMAGE,Operation.ADD_VALUE),EquipmentSlotGroup.OFFHAND);
      builder.add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,MTConfig.RIPPER_SPEED,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,MTConfig.RIPPER_SPEED,Operation.ADD_VALUE),EquipmentSlotGroup.OFFHAND);
      builder.add(Attributes.MOVEMENT_SPEED,new AttributeModifier(Identifier.fromNamespaceAndPath("mahoutsukai","ripper_move_speed"),MTConfig.RIPPER_MOVEMENT_SPEED,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
      return builder.build();
   }
   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      if (player != null) {
         LivingEntity target = player;
         ItemStack main = player.getItemBySlot(EquipmentSlot.MAINHAND);
         ItemStack off = player.getItemBySlot(EquipmentSlot.OFFHAND);
         if (!player.level().isClientSide() && main.getItem() == off.getItem() && main.getItem() == ModItems.theripper.get() && MTConfig.RIPPER_GIVES_INVIS) {
            int manafog = MTConfig.RIPPER_INVIS_MANA_COST;
            if (PlayerManaManager.drainMana(player, manafog, false, false) == manafog) {
               EffectUtil.buff(player, ModEffects.RIPPER_INVISIBILITY, false, 2000000000, false);
               RipperInvisPotion.notifyIfInvisible(player);
            }
         }

         int mana = MTConfig.RIPPER_FOG_MANA_COST;
         if (!player.level().isClientSide()
            && !EffectUtil.hasBuff(player, ModEffects.RIPPER_COOLDOWN)
            && PlayerManaManager.drainMana(player, mana, false, false) == mana) {
            LivingEntity look = ImmunityExchangeSpellEffect.getEnemy(player);
            if (look != null) {
               target = look;
            }

            EffectUtil.buff(player, ModEffects.RIPPER_COOLDOWN, false, MTConfig.RIPPER_COOLDOWN, false);
            PacketHandler.sendTracking(target, new FogPacket(target.getId(), MTConfig.RIPPER_FOG_RANGE, MTConfig.RIPPER_FOG_DISTANCE));
            if (target instanceof ServerPlayer) {
               PacketHandler.sendTo((ServerPlayer)target, new FogPacket(target.getId(), MTConfig.RIPPER_FOG_RANGE, MTConfig.RIPPER_FOG_DISTANCE));
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   public static float ripperOnHit(LivingEntity victim,DamageSource source,float amount) {
      if (source.getEntity() instanceof LivingEntity living
         && living.getMainHandItem().getItem() == living.getOffhandItem().getItem()
         && living.getOffhandItem().getItem() == ModItems.theripper.get()) {
         if (EffectUtil.hasBuff(living, ModEffects.RIPPER_INVISIBILITY) && MTConfig.RIPPER_GIVES_INVIS) {
            EffectUtil.debuff(living, ModEffects.RIPPER_INVISIBILITY);
            RipperInvisPotion.notifyNotInvisible(living);
            EffectUtil.buff(living, ModEffects.RIPPER_INVIS_COOLDOWN, false, MTConfig.RIPPER_INVIS_COOLDOWN, false);
         }

         if (!EffectUtil.isLookingAtMe(living, victim)) {
            victim.invulnerableTime = 0;
            int mm = 100;
            if (living instanceof Player) {
               IMahou mahou = Utils.getPlayerMahou((Player)living);
               if (mahou != null) {
                  mm = mahou.getMaxMana();
               }
            }

            double d = MTConfig.RIPPER_DAMAGE_BONUS_FROM_BEHIND * (MTConfig.RIPPER_DAMAGE_BONUS_SCALES_WITH_MANA ? mm : 1);
            d = amount + d;
            amount=(float)d;
         }
      }
      return amount;
   }

   public static void ripperLivingTick(LivingEntity living) {
      if (living != null
         && (living.getMainHandItem().getItem() != living.getOffhandItem().getItem() || living.getMainHandItem().getItem() != ModItems.theripper.get())
         && EffectUtil.hasBuff(living, ModEffects.RIPPER_INVISIBILITY)) {
         EffectUtil.debuff(living, ModEffects.RIPPER_INVISIBILITY);
         RipperInvisPotion.notifyNotInvisible(living);
      }
   }

   public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.RIPPER_DURABILITY;
   }

   public static void attack(Player player, Entity target) {
      if (net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.invoker().interact(player,player.level(),InteractionHand.MAIN_HAND,target,null)!=InteractionResult.FAIL) {
         if (!player.cannotAttack(target)) {
            float baseDamage = player.isAutoSpinAttack() ? player.autoSpinAttackDmg : (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            ItemStack attackingItemStack = player.getWeaponItem();
            DamageSource damageSource = player.createAttackSource(attackingItemStack);
            float attackStrengthScale = player.getAttackStrengthScale(0.5F);
            float magicBoost = attackStrengthScale * (player.getEnchantedDamage(target, baseDamage, damageSource) - baseDamage);
            baseDamage *= player.baseDamageScaleFactor();
            if (!player.deflectProjectile(target)) {
               if (baseDamage > 0.0F || magicBoost > 0.0F) {
                  boolean fullStrengthAttack = attackStrengthScale > 0.9F;
                  boolean knockbackAttack;
                  if (player.isSprinting() && fullStrengthAttack) {
                     player.playServerSideSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK);
                     knockbackAttack = true;
                  } else {
                     knockbackAttack = false;
                  }


                  boolean criticalAttack = fullStrengthAttack && player.canCriticalAttack(target);

                  if (criticalAttack) {
                     baseDamage *= 1.5F;
                  }

                  float totalDamage = baseDamage + magicBoost;
                  boolean sweepAttack = player.isSweepAttack(fullStrengthAttack, criticalAttack, knockbackAttack);

                  float oldLivingEntityHealth = 0.0F;
                  if (target instanceof LivingEntity livingTarget) {
                     oldLivingEntityHealth = livingTarget.getHealth();
                  }

                  Vec3 oldMovement = target.getDeltaMovement();
                  boolean wasHurt = target.hurtOrSimulate(damageSource, totalDamage);
                  if (wasHurt) {
                     player.causeExtraKnockback(target, player.getKnockback(target, damageSource) + (knockbackAttack ? 0.5F : 0.0F), oldMovement);
                     if (sweepAttack) {
                        player.doSweepAttack(target, baseDamage, damageSource, attackStrengthScale);
                     }

                     player.attackVisualEffects(target, criticalAttack, sweepAttack, fullStrengthAttack, false, magicBoost);
                     player.setLastHurtMob(target);
                     player.itemAttackInteraction(target, attackingItemStack, damageSource, true);
                     player.damageStatsAndHearts(target, oldLivingEntityHealth);
                     player.causeFoodExhaustion(0.1F);
                  } else {
                     player.playServerSideSound(SoundEvents.PLAYER_ATTACK_NODAMAGE);
                  }
               }

               player.postPiercingAttack();
            }

            player.onAttack();
         }
      }
   }
}
