package stepsword.mahoutsukai.fabric.mixin;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.*;
import stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect;
@Mixin(ServerExplosion.class)
public abstract class RhoExplosionMixin {
 @Shadow @Final private ServerLevel level;
 @ModifyReturnValue(method="calculateExplodedPositions",at=@At("RETURN"))
 private List<BlockPos> mahoutsukai$shieldBlocks(List<BlockPos> blocks){return RhoAiasSpellEffect.protectBlocks(level,blocks);}
 @ModifyExpressionValue(method="hurtEntities",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
 private List<Entity> mahoutsukai$shieldEntities(List<Entity> entities){return RhoAiasSpellEffect.protectEntities(level,entities);}
}
