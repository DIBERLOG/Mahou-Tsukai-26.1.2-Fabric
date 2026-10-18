package stepsword.mahoutsukai.entity.mahoujin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;


import stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class FallenDownBeamEntity extends MahoujinEntity {
   Player player;
   public ArrayList<String> waterbased;
   public static final String entityName = "fallen_down_beam_entity";

   public FallenDownBeamEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public FallenDownBeamEntity(Level world) {
      super((EntityType<?>)ModEntities.FALLEN_DOWN_BEAM.get(), world);
   }

   public FallenDownBeamEntity(Level world, LivingEntity caster) {
      this(world);
      this.setCasterUUID(caster.getUUID());
      this.waterbased = new ArrayList<>(
         Arrays.asList(
            Utils.getRegistryName(Blocks.SNOW, this.level()),
            Utils.getRegistryName(Blocks.WATER, this.level()),
            Utils.getRegistryName((Block)ModBlocks.murkyFluid.get(), this.level())
         )
      );
   }

   @Override
   protected void readAdditionalSaveData(ValueInput input) {
      super.readAdditionalSaveData(input);
   }

   @Override
   protected void addAdditionalSaveData(ValueOutput output) {
      super.addAdditionalSaveData(output);
   }

   public float getGrowingLife() {
      return 50.0F;
   }

   public double getCurrentRadius() {
      return Math.max(0.0, Math.min(MTConfig.FALLEN_DOWN_RADIUS, (this.getLife() - this.getGrowingLife()) * MTConfig.FALLEN_DOWN_BEAM_GROWTH));
   }

   @Override
   public void tick() {
      super.tick();
      double radius = this.getCurrentRadius();
      if (!this.level().isClientSide() && this.level().getGameTime() % 4L == 0L) {
         Player caster = null;
         if (this.level().getServer() != null) {
            caster = this.level().getServer().getPlayerList().getPlayer(this.getCasterUUID());
         }

         int mana = MTConfig.FALLEN_DOWN_MANA_PER_TICK;
         if (caster == null || !stepsword.mahoutsukai.item.mysticcode.MysticCode.isChanneling(caster,FallenDownSpellScroll.class)) {
            this.discard();return;
         }

         if (caster == null) {
            this.discard();
            return;
         }

         if (PlayerManaManager.drainMana(caster, mana, false, false, true, false) == mana && this.getLife() > 20) {
            AABB aabb = new AABB(
               this.blockPosition().getX() - radius,
               this.level().getMinY(),
               this.blockPosition().getZ() - radius,
               this.blockPosition().getX() + radius,
               this.level().getMaxY(),
               this.blockPosition().getZ() + radius
            );
            List<Entity> lst = this.level()
               .getEntities(this, aabb, a -> a instanceof LivingEntity && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), a));
            float damage = (float)MTConfig.FALLEN_DOWN_BEAM_DAMAGE;
            IMahou mahou = Utils.getPlayerMahou(caster);
            if (mahou != null) {
               damage = (float)(damage + mahou.getMaxMana() * MTConfig.FALLEN_DOWN_BEAM_MANA_SCALED_DAMAGE);
            }

            for (Entity e : lst) {
               float extra = 0.0F;
               if (e instanceof LivingEntity) {
                  extra = (float)(((LivingEntity)e).getHealth() * MTConfig.FALLEN_DOWN_BEAM_TARGET_HEALTH_PERCENTAGE_DAMAGE);
               }

               e.hurtServer((ServerLevel)this.level(),e.damageSources().onFire(), damage + extra);
            }

            List<BlockPos> blocksBurn = new ArrayList<>();
            if (radius > 0.0) {
               for (double i = -radius - 2.0; i < radius + 2.0; i++) {
                  for (double j = -radius - 2.0; j < radius + 2.0; j++) {
                     for (int y = this.level().getMinY(); y < this.level().getMaxY(); y++) {
                        if (i * i + j * j < (radius + 1.0) * (radius + 1.0)) {
                           BlockPos pos = this.blockPosition().offset((int)i, y, (int)j).offset(0, -this.blockPosition().getY(), 0);
                           BlockState bs = this.level().getBlockState(pos);
                           if (((net.minecraft.world.level.block.FireBlock)Blocks.FIRE).canBurn(bs)
                              || this.getBlocksTransform(Utils.getRegistryKey(bs.getBlock(), this.level())) != null) {
                              blocksBurn.add(pos);
                           }
                        }
                     }
                  }
               }
            }

            this.fallenDown(blocksBurn, caster, (ServerLevel)this.level(), "", false, false);
         }
      }
   }

   @Override
   public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
      return false;
   }

   public void fallenDown(List<BlockPos> affected, Player caster, ServerLevel world, String name, boolean drop, boolean fall) {
      if (caster == null && this.player == null) {
         this.player = SafeFakePlayer.createFakePlayerIfNull(world);
      } else if (caster != null) {
         this.player = caster;
      }

      Collections.shuffle(affected);
      EffectUtil.applySpellBlockChanges(
         affected,
         this.player,
         this.player,
         drop,
         new ArrayList<>(),
         this.player.position(),
         MTConfig.FALLEN_DOWN_BLOCK_BREAK_PER_TICK,
         (bs, e, d, p) -> {
            boolean ret = false;
            if (okToBreak(bs, this.player.level(), p)) {
               Block b = this.getBlocksTransform(Utils.getRegistryKey(bs.getBlock(), this.player.level()));
               if (b != null) {
                  world.setBlockAndUpdate(p, b.defaultBlockState());
               }

               if (Math.random() < 0.04 && world.isEmptyBlock(p.above())) {
                  world.setBlockAndUpdate(p.above(), Blocks.FIRE.defaultBlockState());
               }

               if (this.waterbased == null) {
                  this.waterbased = new ArrayList<>(
                     Arrays.asList(
                        Utils.getRegistryName(Blocks.SNOW, this.level()),
                        Utils.getRegistryName(Blocks.WATER, this.level()),
                        Utils.getRegistryName((Block)ModBlocks.murkyFluid.get(), this.level())
                     )
                  );
               }

               if ((this.waterbased.contains(Utils.getRegistryName(bs.getBlock(), this.level())) || world.getBlockState(p).getFluidState().isSource())
                  && world.getRandom().nextFloat() > 0.8F) {
                  world.playSound(
                     null,
                     p,
                     SoundEvents.LAVA_EXTINGUISH,
                     SoundSource.BLOCKS,
                     0.5F,
                     2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F
                  );
               }

               ret = true;
            }

            return ret;
         }
      );
   }

   public Block getBlocksTransform(Identifier loc) {
      List<? extends String> inblocks = MTConfig.FALLEN_DOWN_BEAM_IN;
      List<? extends String> outblocks = MTConfig.FALLEN_DOWN_BEAM_OUT;
      String m = loc.toString();
      int index = inblocks.indexOf(m);
      Block b = null;
      if (index >= 0 && index < outblocks.size()) {
         b = Utils.getBlockFromRegistryName(outblocks.get(index));
      } else {
         index = inblocks.indexOf(loc.getNamespace());
         if (index >= 0 && index < outblocks.size()) {
            b = Utils.getBlockFromRegistryName(outblocks.get(index));
         }
      }

      return b != null && Utils.getRegistryKey(b, this.level()) != null && Utils.getRegistryKey(b, this.level()).equals(loc) ? null : b;
   }

   public static boolean okToBreak(BlockState bs, Level w, BlockPos p) {
      return !EffectUtil.unchangableBlock(bs.getBlock()) && !bs.isAir() && w.getBlockEntity(p) == null;
   }
}
