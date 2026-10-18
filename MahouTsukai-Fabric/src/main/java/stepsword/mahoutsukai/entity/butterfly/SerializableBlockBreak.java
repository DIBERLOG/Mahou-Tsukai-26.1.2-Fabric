package stepsword.mahoutsukai.entity.butterfly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SerializableBlockBreak {
   public BlockPos loc;
   public BlockState state;
   public static Codec<SerializableBlockBreak> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BlockPos.CODEC.fieldOf("loc").forGetter(SerializableBlockBreak::getLoc),
            BlockState.CODEC.fieldOf("state").forGetter(SerializableBlockBreak::getState)
         )
         .apply(instance, SerializableBlockBreak::new)
   );

   public SerializableBlockBreak(BlockPos loc, BlockState state) {
      this.loc = loc;
      this.state = state;
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
}
