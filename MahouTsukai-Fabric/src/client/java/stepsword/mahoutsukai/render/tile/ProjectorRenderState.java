package stepsword.mahoutsukai.render.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import stepsword.mahoutsukai.render.enchant.ProjectorValues;

public class ProjectorRenderState extends BlockEntityRenderState {
   public ProjectorValues pv = new ProjectorValues();
   public boolean on = true;
   public boolean locator = false;
   public boolean itemRender = false;
   public float partialTicks = 0.0F;
   public BlockPos blockPos = null;
}
