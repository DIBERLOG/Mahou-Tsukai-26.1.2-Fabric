package stepsword.mahoutsukai.item;

import com.google.common.collect.Multimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

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
         id = Identifier.fromNamespaceAndPath("mahoutsukai", fallbackName);
      }

      return properties.setId(ResourceKey.create(Registries.ITEM, id));
   }

   public ItemBase(String name, int maxStack, int damage) {
      super(withRegisteredId(new Properties().stacksTo(maxStack).durability(damage), name));
   }

   public ItemBase(String name, int maxStack) {
      super(withRegisteredId(new Properties().stacksTo(maxStack), name));
   }

   public ItemBase(String name) {
      super(withRegisteredId(new Properties().stacksTo(64), name));
   }

   public ItemBase(String name, Properties z) {
      super(withRegisteredId(z, name));
   }

   public ItemBase(Properties z, int n) {
      super(withRegisteredId(z).stacksTo(n));
   }

   public ItemBase(Properties z) {
      super(withRegisteredId(z).stacksTo(64));
   }

   public ItemBase(String name, Item container) {
      super(withRegisteredId(new Properties().stacksTo(1).craftRemainder(container), name));
   }
}
