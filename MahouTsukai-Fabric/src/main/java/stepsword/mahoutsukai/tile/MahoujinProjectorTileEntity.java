package stepsword.mahoutsukai.tile;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.render.enchant.ProjectorValues;
import stepsword.mahoutsukai.tile.exchange.ContractMahoujinTileEntity;

public class MahoujinProjectorTileEntity extends TickingTileEntity {
   public ProjectorValues pv = new ProjectorValues();
   public UUID caster = null;

   public MahoujinProjectorTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType<?>)ModTileEntities.projector.get(), p, bs);
   }

   protected void loadAdditional(ValueInput input) {
      input.read("pv", ProjectorValues.CODEC).ifPresent(values -> this.pv = values);
      input.read("caster", UUIDUtil.CODEC).ifPresent(uuid -> this.caster = uuid);
      super.loadAdditional(input);
   }

   protected void saveAdditional(ValueOutput output) {
      output.store("pv", ProjectorValues.CODEC, this.pv);
      if (this.caster != null) {
         output.store("caster", UUIDUtil.CODEC, this.caster);
      }

      super.saveAdditional(output);
   }

   protected void applyImplicitComponents(DataComponentGetter input) {
      super.applyImplicitComponents(input);
      this.pv = ProjectorValues.readFromTag(ProjectorValues.writeToTag(input.getOrDefault(DataComponents.PROJECTOR_VALUES_COMPONENT, new ProjectorValues())));
   }

   protected void collectImplicitComponents(Builder builder) {
      builder.set(DataComponents.PROJECTOR_VALUES_COMPONENT, this.pv == null ? new ProjectorValues() : ProjectorValues.readFromTag(ProjectorValues.writeToTag(this.pv)));
   }

   @Override
   public void tick(Level level, BlockPos blockPos, BlockState blockState, TickingTileEntity blockEntity) {
      if (this.pv.cspeed == 0.0F) {
         this.pv.crotation = 0.0F;
      }

      if (this.pv.corbitspeed == 0.0F) {
         this.pv.corbit = 0.0F;
      }

      if (MTConfig.PROJECTOR_CONTINUES_ROTATE_WHEN_OFF || level != null && !level.hasNeighborSignal(this.worldPosition)) {
         this.pv.crotation = this.pv.crotation + this.pv.cspeed;
         this.pv.crotation %= 360.0F;
         this.pv.cpitch = this.pv.cpitch + this.pv.cpitspeed;
         this.pv.cpitch %= 360.0F;
         this.pv.cyaw = this.pv.cyaw + this.pv.cyawspeed;
         this.pv.cyaw %= 360.0F;
         this.pv.corbit = this.pv.corbit + this.pv.corbitspeed;
         this.pv.corbit %= 360.0F;
         this.pv.gif_frame = this.pv.gif_frame + this.pv.gif_speed;
         this.pv.gif_frame %= 100000.0F;
      }
   }

   public static boolean canEdit(Player player, MahoujinProjectorTileEntity projector) {
      return projector.caster == null
         || player.getUUID().equals(projector.caster)
         || ContractMahoujinTileEntity.isImmuneToSpell(player.level(), projector.caster, player)
         || player.isCreative();
   }
}
