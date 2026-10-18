package stepsword.mahoutsukai.tile.displacement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tickets.ModTickets;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;

public class EquivalentDisplacementMahoujinTileEntity extends MahoujinTickingTileEntity implements AboveChecker {
   private static final int TICKET_RADIUS = 1;
   private int receivingTickCounter = 0;
   public boolean startLoadingPartner = false;
   private BlockDim target = null;
   public static String TELEPORTER_BOUND_TAG = "MAHOUTSUKAI_BOUND_TAG";

   public EquivalentDisplacementMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.equivalentDisplacement.get(), p, bs);
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      if (this.target != null) {
         output.store(TELEPORTER_BOUND_TAG, BlockDim.CODEC, this.target);
      }

      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.target = (BlockDim)input.read(TELEPORTER_BOUND_TAG, BlockDim.CODEC).orElse(null);
      super.loadAdditional(input);
   }

   public BlockPos getTargetLocation() {
      return this.target == null ? null : this.target.getPos();
   }

   public Identifier getTargetDimension() {
      return this.target == null ? null : this.target.getDim();
   }

   public void setTarget(BlockDim bd) {
      this.target = bd;
   }

   public Identifier getDimension() {
      return this.level != null ? EffectUtil.getDimension(this.level) : null;
   }

   public void setReceivingTickCounter(int t) {
      this.receivingTickCounter = t;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (!level.isClientSide() && this.receivingTickCounter > 0) {
         this.receivingTickCounter--;
      }

      this.loadChunks();
   }

   @Override
   public int checkTicks() {
      return MTConfig.EQUIVALENT_DISPLACEMENT_BLOCK_CYCLE;
   }

   @Override
   public void noEntityFoundAbove() {
      this.startLoadingPartner = false;
   }

   @Override
   public boolean ifEntityFoundAbove(Entity e) {
      this.startLoadingPartner = true;
      BlockPos target = this.getTargetLocation();
      Identifier d = this.getDimension();
      EquivalentDisplacementMahoujinTileEntity tte = this.getTargetTE(target, this.getTargetDimension());
      Entity here = e;
      if (tte != null) {
         List<? extends Entity> theres = tte.checkAbove(tte, true);
         Entity there = !theres.isEmpty() ? theres.getFirst() : null;
         boolean iTeleport = doITeleport(this.worldPosition, target, d, this.getTargetDimension());
         if (iTeleport && here != null && there != null && !here.isDiscrete() && !there.isDiscrete()) {
            Player caster = this.getCaster();
            boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
               this.worldPosition, this.level, this.getCasterUUID(), MTConfig.EQUIVALENT_DISPLACEMENT_MANA_COST
            );
            if (nearbyBatteriesGaveMana
               || caster != null
                  && PlayerManaManager.drainMana(caster, MTConfig.EQUIVALENT_DISPLACEMENT_MANA_COST, false, false)
                     == MTConfig.EQUIVALENT_DISPLACEMENT_MANA_COST) {
               MahouTsukaiTeleporter.teleport(here, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, this.getTargetDimension());
               MahouTsukaiTeleporter.teleport(there, this.worldPosition.getX() + 0.5, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5, d);
               this.setReceivingTickCounter(MTConfig.EQUIVALENT_DISPLACEMENT_RECEIVE_COOLDOWN);
               tte.setReceivingTickCounter(MTConfig.EQUIVALENT_DISPLACEMENT_RECEIVE_COOLDOWN);
            }
         }
      }

      return false;
   }

   @Override
   public boolean firstEntityOnly() {
      return true;
   }

   @Override
   public boolean preConditions() {
      BlockPos target = this.getTargetLocation();
      Identifier d = this.getDimension();
      EquivalentDisplacementMahoujinTileEntity tte = this.getTargetTE(target, this.getTargetDimension());
      if (this.startLoadingPartner && tte != null) {
         tte.loadChunks();
      }

      return !this.level.isClientSide()
         && tte != null
         && this.receivingTickCounter <= 0
         && ActiveCircleConfig.tryToOperate(this, this.getCasterUUID())
         && target != null
         && (!target.equals(this.worldPosition) || d != this.getTargetDimension())
         && tte.getCasterUUID().equals(this.getCasterUUID());
   }

   @Override
   public Class<? extends Entity> checkForEntity() {
      return Entity.class;
   }

   public void loadChunks() {
      ChunkPos c = ChunkPos.containing(this.worldPosition);
      this.level.getChunk(c.x(), c.z(), ChunkStatus.FULL, true);
      if (!this.level.isClientSide()) {
         ChunkPos pos = ChunkPos.containing(this.getBlockPos());
         ((ServerLevel)this.level).getChunkSource().addTicketWithRadius((TicketType)ModTickets.EQUIV_DISP_TICKET.get(), pos, 1);
      }
   }

   public void releaseChunks() {
      if (!this.level.isClientSide()) {
         ((ServerLevel)this.level)
            .getChunkSource()
            .removeTicketWithRadius((TicketType)ModTickets.EQUIV_DISP_TICKET.get(), ChunkPos.containing(this.getBlockPos()), 1);
      }
   }

   public String serializeTicket() {
      String ret = "mahoutsukai_";
      ret = ret + this.worldPosition.getX() + "_" + this.worldPosition.getY() + "_" + this.worldPosition.getZ() + "_";
      if (this.getDimension() != null) {
         ret = ret + this.getDimension().toString();
      } else {
         ret = ret + "overworld";
      }

      return ret;
   }

   public static boolean doITeleport(BlockPos me, BlockPos target, Identifier md, Identifier td) {
      if (md != td && !MTConfig.EQUIVALENT_DISPLACEMENT_DIMENSIONAL_TRAVEL) {
         return false;
      } else {
         double maxd = MTConfig.EQUIVALENT_DISPLACEMENT_MAX_DISTANCE;
         if (maxd > 0.0 && me.distSqr(target) > maxd * maxd) {
            return false;
         } else if (me.getX() != target.getX()) {
            return me.getX() < target.getX();
         } else if (me.getY() != target.getY()) {
            return me.getY() < target.getY();
         } else {
            return me.getZ() != target.getZ() ? me.getZ() < target.getZ() : md.hashCode() < td.hashCode();
         }
      }
   }

   public EquivalentDisplacementMahoujinTileEntity getTargetTE(BlockPos pos, Identifier dimension) {
      if (dimension != null && pos != null) {
         ServerLevel worldServer = EffectUtil.getNewDimensionByName((ServerLevel)this.level, dimension);
         if (worldServer != null && worldServer.getBlockEntity(pos) instanceof EquivalentDisplacementMahoujinTileEntity edmte) {
            return edmte;
         }
      }

      return null;
   }
}
