package stepsword.mahoutsukai.entity.mahoujin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class MahoujinEntity extends AbstractMahoujinEntity {
   public MahoujinEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }
}
