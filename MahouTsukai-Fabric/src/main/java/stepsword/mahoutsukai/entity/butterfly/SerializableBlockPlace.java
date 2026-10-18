package stepsword.mahoutsukai.entity.butterfly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SerializableBlockPlace {
   public BlockPos loc;
   public BlockState state;
   public ItemStack stack;
   public static Codec<SerializableBlockPlace> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BlockPos.CODEC.fieldOf("loc").forGetter(SerializableBlockPlace::getLoc),
            BlockState.CODEC.fieldOf("state").forGetter(SerializableBlockPlace::getState),
            ItemStack.CODEC.fieldOf("stack").forGetter(SerializableBlockPlace::getStack)
         )
         .apply(instance, SerializableBlockPlace::new)
   );

   public SerializableBlockPlace() {
   }

   public SerializableBlockPlace(BlockPos loc, BlockState state, ItemStack stack) {
      this.loc = loc;
      this.state = state;
      this.stack = stack;
   }

   public BlockPos getLoc() {
      return this.loc;
   }

   public void setLoc(BlockPos loc) {
      this.loc = loc;
   }

   public BlockState getState() {
      return this.state;
   }

   public void setState(BlockState state) {
      this.state = state;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public void setStack(ItemStack stack) {
      this.stack = stack;
   }
}
