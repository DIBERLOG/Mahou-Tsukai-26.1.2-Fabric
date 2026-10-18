package stepsword.mahoutsukai.render.item;

import java.util.ArrayList;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.spells.Mahoujin;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.util.UnorderedList;

public class MahoujinItemStackRenderer extends BlockEntityItemStackRenderer<MahoujinTileEntity> {
   public static final String JEI_CATALYSTS_TAG = "MahouJeiCatalysts";
   public static final String JEI_CLOTH_TAG = "MahouJeiCloth";

   public MahoujinItemStackRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
      super(blockEntityRenderDispatcher, entityModelSet);
   }

   @Override
   protected boolean shouldRender(ItemStack stack) {
      return Block.byItem(stack.getItem()) instanceof Mahoujin;
   }

   protected MahoujinTileEntity createBlockEntity() {
      return new MahoujinTileEntity(new BlockPos(0, 0, 0), ((Mahoujin)ModBlocks.mahoujin.get()).defaultBlockState());
   }

   @Override
   public MagicalItemRenderState extractArgument(ItemStack stack) {
      MagicalItemRenderState state = super.extractArgument(stack);
      CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      if (customData == null) {
         return state;
      }

      CompoundTag tag = customData.copyTag();
      ArrayList<String> catalysts = new ArrayList<>();
      tag.getList("MahouJeiCatalysts").ifPresent(list -> readCatalysts(list, catalysts));
      state.mahoujinCatalysts = new UnorderedList(catalysts);
      state.mahoujinCloth = tag.getBooleanOr("MahouJeiCloth", false);
      return state;
   }

   protected void configureBlockEntity(MahoujinTileEntity blockEntity, MagicalItemRenderState state) {
      if (state.mahoujinCatalysts != null) {
         blockEntity.setCatalysts(state.mahoujinCatalysts, false);
      }

      blockEntity.setCloth(state.mahoujinCloth);
   }

   private static void readCatalysts(ListTag list, ArrayList<String> catalysts) {
      for (int i = 0; i < list.size(); i++) {
         String catalyst = list.getStringOr(i, "");
         if (!catalyst.isEmpty()) {
            catalysts.add(catalyst);
         }
      }
   }
}
