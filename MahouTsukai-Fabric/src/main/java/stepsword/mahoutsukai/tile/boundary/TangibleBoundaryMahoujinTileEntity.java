package stepsword.mahoutsukai.tile.boundary;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.spells.boundary.InvisibleBarrierBlock;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.Utils;

public class TangibleBoundaryMahoujinTileEntity extends BoundaryMahoujinTileEntity {
   public List<BlockPos> lst = null;

   public TangibleBoundaryMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.tangibleBoundary.get(), p, bs);
   }

   @Override
   public int getManaCost() {
      return MTConfig.TANGIBLE_BARRIER_MANA_COST;
   }

   @Override
   public int getManaCycle() {
      return MTConfig.TANGIBLE_BARRIER_MANA_CYCLE;
   }

   @Override
   public int getBarrierCycle() {
      return MTConfig.TANGIBLE_BARRIER_CYCLE;
   }

   @Override
   public int getBarrierRadius() {
      return MTConfig.TANGIBLE_BARRIER_RADIUS;
   }

   @Override
   public void doBarrier(List<LivingEntity> entities) {
      if (this.lst == null) {
         this.lst = getBox(this.worldPosition, this.getBarrierRadius())
            .map(a -> new BlockPos(a))
            .filter(
               a -> a.getX() == this.worldPosition.getX() - this.getBarrierRadius()
                  || a.getX() == this.worldPosition.getX() + this.getBarrierRadius()
                  || a.getY() == this.worldPosition.getY() - this.getBarrierRadius()
                  || a.getY() == this.worldPosition.getY() + this.getBarrierRadius()
                  || a.getZ() == this.worldPosition.getZ() - this.getBarrierRadius()
                  || a.getZ() == this.worldPosition.getZ() + this.getBarrierRadius()
            )
            .collect(Collectors.toList());
      }

      this.placeAllBarriers(this.lst);
   }

   public static Stream<BlockPos> getBox(BlockPos pos, int radius) {
      return BlockPos.betweenClosedStream(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius));
   }

   public static void removeOwnedBarriers(BlockPos pos, int radius, Level world) {
      ChunkPos chunkBottom = ChunkPos.containing(pos.offset(-radius, -radius, -radius));
      ChunkPos chunkTop = ChunkPos.containing(pos.offset(radius, radius, radius));
      HashSet<BlockPos> toremove = new HashSet<>();

      for (int i = chunkBottom.x(); i <= chunkTop.x(); i++) {
         for (int j = chunkBottom.z(); j <= chunkTop.z(); j++) {
            LevelChunk c = world.getChunk(i, j);

            for (Entry<BlockPos, BlockEntity> entry : c.getBlockEntities().entrySet()) {
               if (entry.getValue() instanceof InvisibleBarrierBlockTileEntity barrier && pos.equals(barrier.boundaryCircle)) {
                  toremove.add(entry.getKey());
               }
            }
         }
      }

      for (BlockPos p : toremove) {
         world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
      }
   }

   @Override
   public void doNotBarrier() {
      removeOwnedBarriers(this.worldPosition, this.getBarrierRadius(), this.level);
   }

   public void placeAllBarriers(List<BlockPos> box) {
      HashSet<UUID> wl = this.getWhitelist();
      box.forEach(
         tmp -> {
            if (tmp.getX() == this.worldPosition.getX() - this.getBarrierRadius()
               || tmp.getX() == this.worldPosition.getX() + this.getBarrierRadius()
               || tmp.getY() == this.worldPosition.getY() - this.getBarrierRadius()
               || tmp.getY() == this.worldPosition.getY() + this.getBarrierRadius()
               || tmp.getZ() == this.worldPosition.getZ() - this.getBarrierRadius()
               || tmp.getZ() == this.worldPosition.getZ() + this.getBarrierRadius()) {
               Block b = this.level.getBlockState(tmp).getBlock();
               BlockEntity te = this.level.getBlockEntity(tmp);
               if (b instanceof AirBlock || b instanceof TallGrassBlock || b instanceof FlowerBlock) {
                  this.level.setBlockAndUpdate(tmp, ((InvisibleBarrierBlock)ModBlocks.invisibleBarrierBlock.get()).defaultBlockState());
                  te = this.level.getBlockEntity(tmp);
                  if (te != null) {
                     ((InvisibleBarrierBlockTileEntity)te).setCasterUUID(this.getCasterUUID());
                     ((InvisibleBarrierBlockTileEntity)te).setBoundaryCircle(this.worldPosition);
                     ((InvisibleBarrierBlockTileEntity)te).whitelist = wl;
                  }
               } else if (te instanceof InvisibleBarrierBlockTileEntity && !wl.equals(((InvisibleBarrierBlockTileEntity)te).whitelist)) {
                  ((InvisibleBarrierBlockTileEntity)te).whitelist = wl;
                  ((InvisibleBarrierBlockTileEntity)te).sendUpdates();
               }
            }
         }
      );
   }

   public HashSet<UUID> getWhitelist() {
      MahouSavedData save = Utils.getWorldMahou(this.level);
      HashSet<UUID> ret = new HashSet<>();
      if (save != null && save.playerContracts != null && save.playerContracts.containsKey(this.getCasterUUID())) {
         ret = new HashSet<>(save.playerContracts.get(this.getCasterUUID()));
      }

      return ret;
   }

   @Override
   public ItemStack getItemToGive() {
      return new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_boundary_tangible"));
   }
}
