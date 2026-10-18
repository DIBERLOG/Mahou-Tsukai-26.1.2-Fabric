package stepsword.mahoutsukai.integration.jei;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityJeiIngredientRenderer implements IIngredientRenderer<EntityJeiIngredient> {
   private final Map<EntityJeiIngredientRenderer.IdentifierAndLevel, Entity> entities = new HashMap<>();
   private Object currentLevel;

   public void render(GuiGraphicsExtractor graphics, EntityJeiIngredient ingredient) {
      if (ingredient.kind() == EntityJeiIngredient.Kind.DRAGON_BREATH) {
         graphics.fakeItem(new ItemStack(Items.DRAGON_BREATH), 0, 0);
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.level != null) {
            if (this.currentLevel != minecraft.level) {
               this.entities.clear();
               this.currentLevel = minecraft.level;
            }

            EntityJeiIngredientRenderer.IdentifierAndLevel key = new EntityJeiIngredientRenderer.IdentifierAndLevel(ingredient.id(), minecraft.level);
            Entity entity = this.entities.computeIfAbsent(key, ignored -> createEntity(ingredient));
            if (entity != null) {
                  EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
                  EntityRenderState state = dispatcher.extractEntity(entity, 1.0F);
                  state.shadowPieces.clear();
                  state.outlineColor = 0;
                  float largestDimension = Math.max(0.75F, Math.max(state.boundingBoxWidth, state.boundingBoxHeight));
                  float scale = 12.0F / largestDimension;
                  Vector3f translation = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F, 0.0F);
                  Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).rotateY((float)Math.toRadians(-35.0));
                  graphics.entity(state, scale, translation, rotation, null, 0, 0, 16, 16);
            }
         }
      }
   }

   private static Entity createEntity(EntityJeiIngredient ingredient) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level == null) {
         return null;
      }

      EntityType<?> type = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.getOptional(ingredient.entityTypeId()).orElse(null);
      if (type == null) {
         return null;
      }

      Entity entity = type.create(minecraft.level, EntitySpawnReason.LOAD);
      if (entity instanceof AreaEffectCloud cloud && ingredient.kind() == EntityJeiIngredient.Kind.DRAGON_BREATH) {
         cloud.setCustomParticle(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F));
         cloud.setRadius(3.0F);
         cloud.setDuration(600);
         cloud.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 1));
      }

      return entity;
   }

   public List<Component> getTooltip(EntityJeiIngredient ingredient, TooltipFlag tooltipFlag) {
      return List.of(ingredient.getName());
   }

   public int getWidth() {
      return 16;
   }

   public int getHeight() {
      return 16;
   }

   private record IdentifierAndLevel(Identifier id, Object level) {
   }
}
