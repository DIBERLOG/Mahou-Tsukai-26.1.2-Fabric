package stepsword.jousting.item;

import com.google.common.collect.Multimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class ItemBase extends Item {
   public Multimap<Attribute, AttributeModifier> attributes;
   private static final ThreadLocal<Identifier> REGISTERING_ID = new ThreadLocal<>();

   public static void pushRegistrationId(Identifier id) {
      REGISTERING_ID.set(id);
   }

   public static void popRegistrationId() {
      REGISTERING_ID.remove();
   }

   public static Properties withRegisteredId(Properties properties) {
      Identifier id = REGISTERING_ID.get();
      if (id == null) {
         throw new IllegalStateException("No registering item id available for " + properties);
      } else {
         return properties.setId(ResourceKey.create(Registries.ITEM, id));
      }
   }

   public static Properties withRegisteredId(Properties properties, String fallbackName) {
      Identifier id = REGISTERING_ID.get();
      if (id == null) {
         id = Identifier.fromNamespaceAndPath("jousting", fallbackName);
      }

      return properties.setId(ResourceKey.create(Registries.ITEM, id));
   }

   public ItemBase(int maxStack, int damage) {
      super(withRegisteredId(new Properties().stacksTo(maxStack).durability(damage)));
   }

   public static ItemAttributeModifiers createAttributes(int pAttackDamage) {
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, pAttackDamage, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

   public ItemBase(int maxStack) {
      super(withRegisteredId(new Properties().stacksTo(maxStack)));
   }

   public ItemBase() {
      super(withRegisteredId(new Properties().stacksTo(64)));
   }

   public ItemBase(Properties z) {
      super(withRegisteredId(z));
   }

   public ItemBase(String name, Item container) {
      super(withRegisteredId(new Properties().stacksTo(64).craftRemainder(container), name));
   }
}
