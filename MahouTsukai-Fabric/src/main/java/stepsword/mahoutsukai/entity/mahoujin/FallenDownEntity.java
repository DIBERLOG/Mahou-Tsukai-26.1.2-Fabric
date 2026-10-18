package stepsword.mahoutsukai.entity.mahoujin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;


import stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class FallenDownEntity extends MahoujinEntity {
   Player player;
   public static final String entityName = "fallen_down_entity";

   public FallenDownEntity(EntityType entityEntityType, Level world) {
      super(entityEntityType, world);
   }

   public FallenDownEntity(Level world) {
      super((EntityType<?>)ModEntities.FALLEN_DOWN.get(), world);
   }

   public FallenDownEntity(Level world, LivingEntity caster) {
      this(world);
      this.setCasterUUID(caster.getUUID());
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

   @Override
   public void tick() {
      super.tick();
      int radius = Math.min(this.getLife() / 20 + 1, 30);
      radius = MTConfig.FALLEN_DOWN_RADIUS;
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
            ((ServerLevel)this.level()).rainLevel = Math.min(this.getLife() / 40.0F, 4.8F);
            AABB aabb = new AABB(
               this.blockPosition().getX() - radius,
               this.level().getMinY(),
               this.blockPosition().getZ() - radius,
               this.blockPosition().getX() + radius,
               this.level().getMaxY(),
               this.blockPosition().getZ() + radius
            );
            List<Entity> lst = this.level()
               .getEntities(
                  this,
                  aabb,
                  a -> a instanceof LivingEntity && !ContractMahoujinTileEntity.isImmuneToSpell(this.level(), this.getCasterUUID(), a) && a.onGround()
               );
            List<BlockPos> blocksBreak = new ArrayList<>();
            List<BlockPos> blocksFall = new ArrayList<>();

            for (Entity e : lst) {
               BlockPos ep = e.blockPosition();
               int break_radius = 3;
               int depth = 4;

               for (int i = -break_radius - 1; i <= break_radius; i++) {
                  for (int j = -break_radius - 1; j <= break_radius; j++) {
                     int m = 0;
                     if (i * i + j * j < break_radius * break_radius) {
                        for (int k = ep.getY() - depth; k < ep.getY(); k++) {
                           BlockPos tmp = new BlockPos(ep.getX() + i, k, ep.getZ() + j);
                           if (okToBreak(this.level().getBlockState(tmp), this.level(), tmp)) {
                              if (m >= depth / 2) {
                                 blocksFall.add(tmp);
                              } else {
                                 blocksBreak.add(tmp);
                              }

                              m++;
                           }
                        }

                        for (int k = (int)(ep.getY() + e.getBbHeight()); k < ep.getY() + e.getBbHeight() + depth / 2; k++) {
                           BlockPos tmp = new BlockPos(ep.getX() + i, k, ep.getZ() + j);
                           blocksFall.add(tmp);
                        }
                     }
                  }
               }
            }

            this.fallenDown(blocksBreak, caster, (ServerLevel)this.level(), "", false, false);
            this.fallenDown(blocksFall, caster, (ServerLevel)this.level(), "", false, true);
         }
      }
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
         false,
         new ArrayList<>(),
         this.player.getPosition(0.0F),
         MTConfig.FALLEN_DOWN_BLOCK_BREAK_PER_TICK,
         (bs, e, d, p) -> {
            if (okToBreak(bs, this.player.level(), p)) {
               if (!fall) {
                  if (drop) {
                     Block.dropResources(bs, world, p, world.getBlockEntity(p));
                  }

                  this.player.level().removeBlock(p, false);
                  this.player.level().setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                  return true;
               }

               if (world.isEmptyBlock(p.below()) || FallingBlock.isFree(world.getBlockState(p.below())) && p.getY() >= 0) {
                  FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(
                     world, Utils.toBlockPos(p.getX() + 0.5, p.getY(), p.getZ() + 0.5), world.getBlockState(p)
                  );

                  return true;
               }
            }

            return false;
         }
      );
   }

   public static boolean okToBreak(BlockState bs, Level w, BlockPos p) {
      return !EffectUtil.unchangableBlock(bs.getBlock()) && !bs.isAir() && w.getBlockEntity(p) == null;
   }
}
