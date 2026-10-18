package stepsword.mahoutsukai.integration.jei;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public record EntityJeiIngredient(Identifier id, Identifier entityTypeId, EntityJeiIngredient.Kind kind) {
   private static final Codec<EntityJeiIngredient.Kind> KIND_CODEC = Codec.STRING.xmap(EntityJeiIngredient.Kind::valueOf, Enum::name);
   public static final Codec<EntityJeiIngredient> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(EntityJeiIngredient::id),
            Identifier.CODEC.fieldOf("entity_type").forGetter(EntityJeiIngredient::entityTypeId),
            KIND_CODEC.fieldOf("kind").forGetter(EntityJeiIngredient::kind)
         )
         .apply(instance, EntityJeiIngredient::new)
   );

   public Component getName() {
      return (Component)(switch (this.kind) {
         case NORMAL -> (Component)BuiltInRegistries.ENTITY_TYPE
            .getOptional(this.entityTypeId)
            .map(entityType -> entityType.getDescription())
            .orElseGet(() -> Component.literal(this.entityTypeId.toString()));
         case DRAGON_BREATH -> Component.literal("Dragon's Breath Cloud");
         case LIGHTNING -> Component.literal("Lightning Bolt");
      });
   }

   public enum Kind {
      NORMAL,
      DRAGON_BREATH,
      LIGHTNING;
   }
}
