package stepsword.mahoutsukai.tile.displacement;

import java.util.HashSet;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;

public class OrderedDisplacementMahoujinTileEntity extends MahoujinTickingTileEntity implements AboveChecker {
   private int receivingTickCounter = 0;
   public static String TELEPORTER_ID_TAG = "MAHOUTSUKAI_TELEPORTER_ID";
   private long teleporter_id;

   public OrderedDisplacementMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.orderedDisplacement.get(), p, bs);
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      output.putLong(TELEPORTER_ID_TAG, this.teleporter_id);
      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.teleporter_id = input.getLongOr(TELEPORTER_ID_TAG, 0L);
      super.loadAdditional(input);
   }

   public long getTeleporterId() {
      return this.teleporter_id;
   }

   public void setTeleporterId(long t) {
      this.teleporter_id = t;
   }

   public void setReceivingTickCounter(int t) {
      this.receivingTickCounter = t;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (!level.isClientSide() && this.receivingTickCounter > 0) {
         this.receivingTickCounter--;
      }
   }

   @Override
   public int checkTicks() {
      return MTConfig.ORDERED_DISPLACEMENT_BLOCK_CYCLE;
   }

   @Override
   public boolean ifEntityFoundAbove(Entity e) {
      BlockPos target = this.getTarget();
      if (target != null) {
         Player caster = this.getCaster();
         if (!e.isDiscrete()) {
            boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
               this.worldPosition, this.level, this.getCasterUUID(), MTConfig.ORDERED_DISPLACEMENT_MANA_COST
            );
            if (nearbyBatteriesGaveMana
               || caster != null
                  && PlayerManaManager.drainMana(caster, MTConfig.ORDERED_DISPLACEMENT_MANA_COST, false, false) == MTConfig.ORDERED_DISPLACEMENT_MANA_COST) {
               MahouTsukaiTeleporter.teleport(e, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, EffectUtil.getDimension(e.level()));
               if (this.level.getBlockEntity(target) instanceof OrderedDisplacementMahoujinTileEntity mte) {
                  mte.setReceivingTickCounter(MTConfig.ORDERED_DISPLACEMENT_RECEIVE_COOLDOWN);
               }
            }
         }
      }

      return false;
   }

   @Override
   public boolean preConditions() {
      return !this.level.isClientSide() && this.receivingTickCounter <= 0 && ActiveCircleConfig.tryToOperate(this, this.getCasterUUID());
   }

   @Override
   public Class<? extends Entity> checkForEntity() {
      return Entity.class;
   }

   @Override
   public boolean firstEntityOnly() {
      return true;
   }

   public BlockPos getTarget() {
      int radius = MTConfig.ORDERED_DISPLACEMENT_RADIUS;
      ChunkPos cpa = ChunkPos.containing(this.worldPosition.offset(-radius, -radius, -radius));
      ChunkPos cpb = ChunkPos.containing(this.worldPosition.offset(radius, radius, radius));
      HashSet<BlockPos> poss = new HashSet<>();

      for (int i = cpa.x(); i < cpb.x() + 1; i++) {
         for (int j = cpa.z(); j < cpb.z() + 1; j++) {
            LevelChunk c = this.level.getChunk(i, j);
            poss.addAll(
               c.getBlockEntities()
                  .entrySet()
                  .stream()
                  .filter(a -> a.getValue() instanceof OrderedDisplacementMahoujinTileEntity)
                  .collect(Collectors.toMap(x -> (BlockPos)x.getKey(), x -> (BlockEntity)x.getValue()))
                  .keySet()
            );
         }
      }

      BlockPos target = null;
      long targetID = -1L;
      BlockPos backupTarget = null;
      long lowestID = -1L;

      for (BlockPos z : poss) {
         BlockEntity te = this.level.getBlockEntity(z);
         if (te instanceof OrderedDisplacementMahoujinTileEntity && !this.worldPosition.equals(z)) {
            OrderedDisplacementMahoujinTileEntity mte = (OrderedDisplacementMahoujinTileEntity)te;
            if (mte.getCasterUUID().equals(this.getCasterUUID())
               && radius >= Math.abs(z.getX() - this.worldPosition.getX())
               && radius >= Math.abs(z.getZ() - this.worldPosition.getZ())) {
               if ((targetID == -1L || mte.getTeleporterId() < targetID) && mte.getTeleporterId() > this.getTeleporterId()) {
                  targetID = mte.getTeleporterId();
                  target = z;
               }

               if (mte.getTeleporterId() < lowestID || lowestID == -1L) {
                  lowestID = mte.getTeleporterId();
                  backupTarget = z;
               }
            }
         }
      }

      return targetID < this.getTeleporterId() ? backupTarget : target;
   }
}
