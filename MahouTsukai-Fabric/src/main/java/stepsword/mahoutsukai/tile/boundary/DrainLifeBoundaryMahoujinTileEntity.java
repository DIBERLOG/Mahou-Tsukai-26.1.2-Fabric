package stepsword.mahoutsukai.tile.boundary;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class DrainLifeBoundaryMahoujinTileEntity extends BoundaryMahoujinTileEntity implements AboveChecker {
   String whitelistName = null;

   public DrainLifeBoundaryMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.drainLifeBoundary.get(), p, bs);
   }

   @Override
   public int getManaCost() {
      return MTConfig.DRAIN_LIFE_BARRIER_MANA_COST;
   }

   @Override
   public int getManaCycle() {
      return MTConfig.DRAIN_LIFE_BARRIER_MANA_CYCLE;
   }

   @Override
   public int getBarrierCycle() {
      return MTConfig.DRAIN_LIFE_BARRIER_CYCLE;
   }

   @Override
   public int getBarrierRadius() {
      return MTConfig.DRAIN_LIFE_BARRIER_RADIUS;
   }

   @Override
   public void doBarrier(List<LivingEntity> entities) {
      int limit = MTConfig.DRAIN_LIFE_AFFECTED_LIMIT;
      int current = 0;
      Player caster = this.getCaster();
      if (MTConfig.RHO_AIAS_KILL_DEBUG) {
         org.slf4j.LoggerFactory.getLogger("mahoutsukai").debug("Drain Life is running. The list of nearby entities has size {}", entities.size());
      }

      for (LivingEntity e : entities) {
         if (current >= limit) {
            break;
         }

         float tmpHealth = e.getHealth();
         if (!EffectUtil.inEntityBlacklist(e, MTConfig.DRAIN_LIFE_ENTITY_BLACKLIST) && this.drainLife(e) && caster != null) {
            if (this.level instanceof ServerLevel && this.level.getServer() != null) {
               Utils.schedule(this.level, () -> {
                  float newHealth = e.getHealth();
                  if (!(tmpHealth - newHealth >= MTConfig.DRAIN_LIFE_DAMAGE) && e.isAlive()) {
                     EffectUtil.genericAttack(e, (float)(MTConfig.DRAIN_LIFE_DAMAGE * MTConfig.DRAIN_LIFE_PUNISH_RESIST), e.damageSources().generic(), caster);
                     Brain<?> brain = e.getBrain();
                     brain.eraseMemory(MemoryModuleType.IS_PANICKING);
                     brain.eraseMemory(MemoryModuleType.HURT_BY);
                     brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
                     this.giveLife(caster);
                  } else {
                     this.giveLife(caster);
                  }
               });
            }

            current++;
         }
      }
   }

   public boolean drainLife(LivingEntity entityIn) {
      if (!ContractMahoujinTileEntity.isImmuneToSpell(this.level, this.getCasterUUID(), entityIn)
         && (this.whitelistName == null || entityIn.getDisplayName() == null || !this.whitelistName.equals(entityIn.getDisplayName().getString()))) {
         EffectUtil.magicAttack(entityIn, (float)MTConfig.DRAIN_LIFE_DAMAGE, this.getCaster());
         if (MTConfig.DRAIN_LIFE_DAMAGE < entityIn.getHealth()) {
            ((stepsword.mahoutsukai.fabric.mixin.LivingDamageAccessor)entityIn).mahoutsukai$clearLastDamageSource(null);
         }

         Brain<?> brain = entityIn.getBrain();
         brain.eraseMemory(MemoryModuleType.IS_PANICKING);
         brain.eraseMemory(MemoryModuleType.HURT_BY);
         brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
         return true;
      } else {
         return false;
      }
   }

   public void giveLife(Player caster) {
      if (caster.getHealth() >= caster.getMaxHealth()) {
         FoodData casterFS = caster.getFoodData();
         if (casterFS.getFoodLevel() < 20 && !MTConfig.DRAIN_LIFE_IGNORE_HUNGER) {
            casterFS.setFoodLevel(casterFS.getFoodLevel() + 1);
         } else {
            IMahou mahou = Utils.getPlayerMahou(caster);
            mahou.setStoredMana(Math.min(mahou.getMaxMana(), mahou.getStoredMana() + MTConfig.DRAIN_LIFE_MANA_REGEN));
         }
      } else {
         caster.heal((float)MTConfig.DRAIN_LIFE_HEAL_FACTOR);
      }
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_boundary_drain_life"));
   }

   @Override
   public List<Item> checkForItem() {
      return Arrays.asList(Items.NAME_TAG);
   }

   @Override
   public int checkTicks() {
      return MTConfig.DRAIN_LIFE_BARRIER_CYCLE;
   }

   @Override
   public boolean firstEntityOnly() {
      return true;
   }

   @Override
   public boolean preConditions() {
      return !this.level.isClientSide();
   }

   @Override
   public boolean ifItemFoundAbove(ItemEntity item) {
      if (item.getOwner() != null && item.getOwner().getUUID().equals(this.getCasterUUID())) {
         this.whitelistName = item.getItem().getHoverName().getString();
         this.sendUpdates();
      }

      return false;
   }

   public String getWhitelistName() {
      return this.whitelistName;
   }

   @Override
   public void saveAdditional(ValueOutput output) {
      if (this.getWhitelistName() != null) {
         output.putString("whitelist_name", this.whitelistName);
      }

      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.whitelistName = (String)input.getString("whitelist_name").orElse(null);
      super.loadAdditional(input);
   }
}
