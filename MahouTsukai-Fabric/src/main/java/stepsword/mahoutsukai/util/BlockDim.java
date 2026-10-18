package stepsword.mahoutsukai.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class BlockDim {
   public BlockPos pos;
   public Identifier dim;
   public static final Codec<BlockDim> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(BlockDim::getPos), Identifier.CODEC.fieldOf("dim").forGetter(BlockDim::getDim))
         .apply(instance, BlockDim::new)
   );

   public BlockDim(BlockPos p, Identifier d) {
      this.pos = p;
      this.dim = d;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Identifier getDim() {
      return this.dim;
   }
}
