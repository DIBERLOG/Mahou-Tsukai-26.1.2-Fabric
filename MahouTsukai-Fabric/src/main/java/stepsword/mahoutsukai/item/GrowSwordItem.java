package stepsword.mahoutsukai.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class GrowSwordItem extends Item {
   public GrowSwordItem(ToolMaterial pTier, Properties pProperties) {
      super(
         ItemBase.withRegisteredId(pProperties)
            .sword(pTier, 3.0F, -2.4F)
            .enchantable(pTier.enchantmentValue())
            .component((DataComponentType)DataComponents.SWORD_COMPONENT, new SwordMahou(3.0F, 10000.0))
      );
   }

   public GrowSwordItem(ToolMaterial pTier, float aspd, int admg, int dura) {
      super(
         ItemBase.withRegisteredId(new Properties())
            .sword(pTier, admg, aspd)
            .enchantable(pTier.enchantmentValue())
            .durability(dura)
            .component((DataComponentType)DataComponents.SWORD_COMPONENT, new SwordMahou(admg, 10000.0))
      );
   }

   public float getAttack(ItemStack stack) {
      if (stack != null) {
         SwordMahou mahou = Utils.getSwordMahou(stack);
         return mahou.getAttackDamage();
      } else {
         return 3.0F;
      }
   }

   public float getCappedAttack(ItemStack stack) {
      if (stack != null) {
         SwordMahou sm = Utils.getSwordMahou(stack);
         if (sm != null) {
            float damage = sm.getAttackDamage();
            double cap = sm.getInnateCap();
            if (MTConfig.POWER_CONSOLIDATION_TIERS.isEmpty()) {
               float capped = Math.min(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP, damage);
               sm.setAttackDamage(capped);
               Utils.setSwordMahou(stack, sm);
               return capped;
            }

            float capped = (float)Math.min(MTConfig.POWER_CONSOLIDATION_ATTACK_CAP, cap);
            sm.setInnateCap(capped);
            capped = (float)Math.min(sm.getInnateCap(), damage);
            sm.setAttackDamage(capped);
            Utils.setSwordMahou(stack, sm);
            return capped;
         }
      }

      return 3.0F;
   }

   /** Original attribute replacement at Fabric's native modifier enumeration point. */
   public static ItemAttributeModifiers attributes(ItemStack stack,ItemAttributeModifiers base) {
      if(!(stack.getItem() instanceof GrowSwordItem sword)) return base;
      String name=net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
      float damage=(name.equals("caliburn") || name.equals("morgan")) ? sword.getCappedAttack(stack) : sword.getAttack(stack);
      return base.withModifierAdded(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage,Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND);
   }


   public static ItemAttributeModifiers createAttributes(int pAttackDamage, float pAttackSpeed) {
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, pAttackDamage, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, pAttackSpeed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .build();
   }

}
