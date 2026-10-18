package stepsword.mahoutsukai.block.circuits;

import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.mahoutsukai.block.BlockTileEntity;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.fabric.MahouIds;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class ManaCircuitBlock extends BlockTileEntity<ManaCircuitTileEntity> {
   public static final AABB boundingBox = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   public static final AABB boundingBoxE = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

   public ManaCircuitBlock() {
      super(Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), "mana_circuit");
   }

   @Override
   public Class<ManaCircuitTileEntity> getTileEntityClass() {
      return ManaCircuitTileEntity.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new ManaCircuitTileEntity(blockPos, blockState);
   }

   public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult res) {
      return (InteractionResult)(chargeCircuitBlock(worldIn, pos, playerIn) ? InteractionResult.SUCCESS : InteractionResult.FAIL);
   }

   public static boolean chargeCircuitBlock(Level worldIn, BlockPos pos, Player playerIn) {
      if (!worldIn.isClientSide() && worldIn.getBlockEntity(pos) instanceof ManaCircuitTileEntity te) {
         if (te.getOwnerUUID() == null || te.getOwnerUUID().equals(MahouIds.FAE_UUID)) {
            te.setOwnerUUID(playerIn.getUUID());
         }

         if (playerIn.isDiscrete()) {
            te.setOn(!te.isOn());
            te.sendUpdates();
         } else if (playerIn != null) {
            if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(playerIn.getMainHandItem().getItem()).equals(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "fae_essence"))) {
               if (playerIn.getUUID().equals(te.getOwnerUUID())) {
                  te.setFae(!te.getFae());
                  te.sendUpdates();
               }
            } else {
               IMahou mahou = Utils.getPlayerMahou(playerIn);
               if (mahou != null) {
                  double chargeRate = mahou.getChargeRate();
                  int playerMaxMana = mahou.getMaxMana();
                  int manaDifference = (int)Math.ceil(playerMaxMana * chargeRate);
                  int storedMana = te.getMana();
                  int MAX_MANA = te.getMaxMana();
                  if (storedMana < MAX_MANA) {
                     manaDifference = Math.min(manaDifference, MAX_MANA - storedMana);
                     manaDifference = PlayerManaManager.chargeCircuit(playerIn, manaDifference);
                     te.setMana(storedMana + manaDifference);
                  }

                  storedMana = te.getMana();
                  if (storedMana >= MAX_MANA) {
                     te.setMana(MAX_MANA);
                  }

                  te.sendUpdates();
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }
}

