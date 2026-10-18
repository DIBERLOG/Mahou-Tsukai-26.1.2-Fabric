package stepsword.mahoutsukai.util;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
/** Native LivingEntity.hurtServer runs real combat; only health storage is isolated from the world. */
public class FakeSkeleton extends Skeleton {
 public float h=5000000;
 public FakeSkeleton(Level world){super(EntityType.SKELETON,world);}
 @Override protected void actuallyHurt(ServerLevel world,DamageSource source,float damage){if(!isInvulnerable()){damage=getDamageAfterArmorAbsorb(source,damage);damage=getDamageAfterMagicAbsorb(source,damage);float absorbed=Math.min(getAbsorptionAmount(),damage);setAbsorptionAmount(Math.max(0,getAbsorptionAmount()-absorbed));h-=Math.max(0,damage-absorbed);}}
}
