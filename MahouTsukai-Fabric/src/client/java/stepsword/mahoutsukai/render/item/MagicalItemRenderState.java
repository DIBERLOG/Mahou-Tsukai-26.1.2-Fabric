package stepsword.mahoutsukai.render.item;

import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.util.UnorderedList;

public class MagicalItemRenderState {
   boolean render = true;
   long time = 0L;
   QuadCollection model;
   ItemStack stack = ItemStack.EMPTY;
   float r = 255.0F;
   float g = 255.0F;
   float b = 255.0F;
   float a = 1.0F;
   float progress = 0.0F;
   float useTicks = 0.0F;
   int colorIndex = 0;
   ItemDisplayContext transform = ItemDisplayContext.NONE;
   UnorderedList mahoujinCatalysts;
   boolean mahoujinCloth;
}
