package stepsword.mahoutsukai.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class MahouTrigger extends SimpleCriterionTrigger<MahouTrigger.TriggerInstance> {
   public void trigger(ServerPlayer pPlayer) {
      this.trigger(pPlayer, p_222625_ -> true);
   }

   public Codec<MahouTrigger.TriggerInstance> codec() {
      return MahouTrigger.TriggerInstance.CODEC;
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
      public static final Codec<MahouTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         p_337390_ -> p_337390_.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("mahou").forGetter(MahouTrigger.TriggerInstance::player))
            .apply(p_337390_, MahouTrigger.TriggerInstance::new)
      );

      public static Criterion<MahouTrigger.TriggerInstance> mahoutsukai() {
         return ((MahouTrigger)ModTriggers.MAHOUTSUKAI.get()).createCriterion(new MahouTrigger.TriggerInstance(Optional.empty()));
      }
   }
}
