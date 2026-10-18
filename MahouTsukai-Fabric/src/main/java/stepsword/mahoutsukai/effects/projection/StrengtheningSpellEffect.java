package stepsword.mahoutsukai.effects.projection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.stream.Streams;
import org.apache.commons.lang3.tuple.Pair;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class StrengtheningSpellEffect {
   public static final String STRENGTHENED_TAG = "mahoutsukai_strengthened";
   public static final String UNBREAKABLE_TAG = "mahoutsukai_old_unbreakable";
   public static final int STRENGTHENED_USES = 5;
   public static final float STRENGTHENED_DAMAGE_BUFF = 4.0F;
   static Map<Pair<TagKey<Block>, TagKey<Block>>, Boolean> CACHE = new HashMap<>();
   static ToolMaterial[] VANILLA_TOOL_MATERIALS = new ToolMaterial[]{
      ToolMaterial.WOOD, ToolMaterial.STONE, ToolMaterial.COPPER, ToolMaterial.IRON, ToolMaterial.DIAMOND, ToolMaterial.NETHERITE
   };

   public static float strengtheningLivingHurt(LivingEntity entity, DamageSource source,float amount) {
      if (!entity.level().isClientSide()) {
         ItemStack weapon = null;
         if (source.getMsgId() != "arrow"
            && source.is(DamageTypes.PLAYER_ATTACK)
            && source.getEntity() instanceof LivingEntity
            && source.getEntity() instanceof LivingEntity attacker) {
            weapon = attacker.getMainHandItem();
         }

         if (weapon != null && getStrengthened(weapon) > 0) {
            amount += 4.0F;
            reduceStrengthened(weapon, 1);
         }
      }
         return amount;
   }





   static boolean isSubset(TagKey<Block> tag1, TagKey<Block> tag2) {
      return CACHE.computeIfAbsent(Pair.of(tag1, tag2), pair -> {
         Iterable<Holder<Block>> list1 = BuiltInRegistries.BLOCK.getTagOrEmpty((TagKey)pair.getLeft());
         Iterable<Holder<Block>> list2 = BuiltInRegistries.BLOCK.getTagOrEmpty((TagKey)pair.getRight());
         Set<Holder<Block>> set1 = Streams.of(list1).collect(Collectors.toSet());
         Set<Holder<Block>> set2 = Streams.of(list2).collect(Collectors.toSet());
         return set2.size() > set1.size() && set2.containsAll(set1);
      });
   }

   static ToolMaterial getNextMiningMaterial(ItemStack stack) {
      Tool tool = (Tool)stack.get(DataComponents.TOOL);
      if (tool == null) {
         return ToolMaterial.WOOD;
      }

      TagKey<Block> incorrectBlocks = getIncorrectBlocksForDrops(tool);
      if (incorrectBlocks == null) {
         return ToolMaterial.WOOD;
      }

      if (incorrectBlocks.equals(ToolMaterial.GOLD.incorrectBlocksForDrops())) {
         return ToolMaterial.STONE;
      }

      for (int i = 0; i < VANILLA_TOOL_MATERIALS.length; i++) {
         ToolMaterial material = VANILLA_TOOL_MATERIALS[i];
         if (incorrectBlocks.equals(material.incorrectBlocksForDrops())) {
            return VANILLA_TOOL_MATERIALS[Math.min(i + 1, VANILLA_TOOL_MATERIALS.length - 1)];
         }
      }

      return ToolMaterial.WOOD;
   }

   static TagKey<Block> getIncorrectBlocksForDrops(Tool tool) {
      for (Rule rule : tool.rules()) {
         if (rule.correctForDrops().isPresent() && !(Boolean)rule.correctForDrops().get()) {
            return (TagKey<Block>)rule.blocks().unwrapKey().orElse(null);
         }
      }

      return null;
   }

   public static boolean strengtheningHarvestCheck(ItemStack s,BlockState target,boolean canHarvest) {
      if (getStrengthened(s) > 0) {
         ItemStack woodPickaxe = new ItemStack(Items.WOODEN_PICKAXE);
         boolean wood = woodPickaxe.isCorrectToolForDrops(target);
         if (s.has(DataComponents.TOOL)) {
            ToolMaterial nextMaterial = getNextMiningMaterial(s);
            if (!target.is(nextMaterial.incorrectBlocksForDrops())) {
               canHarvest=true;
            }
         } else if (wood) {
            canHarvest=true;
         }
      }
         return canHarvest;
   }

   public static void strengtheningArmorEvent(LivingEntity entity) {
      LivingEntity living = entity;

      for (EquipmentSlot slot : EquipmentSlot.VALUES) {
         if (slot.isArmor()) {
            ItemStack s = living.getItemBySlot(slot);
            if (getStrengthened(s) > 0) {
               reduceStrengthened(s, 1);
            }
         }
      }
   }

   public static void strengtheningBreakEvent(Player player) {
      if (!player.level().isClientSide()) {
         ItemStack s = player.getMainHandItem();
         if (getStrengthened(s) > 0 && !Utils.isTool(s)) {
            reduceStrengthened(s, 1);
         }
      }
   }

   public static float strengtheningBreakSpeed(Player player,float speed) {
      if (player != null) {
         ItemStack s = player.getMainHandItem();
         if (getStrengthened(s) > 0) {
            speed += 3.0F;
         }
      }
         return speed;
   }

   public static void reduceStrengthened(ItemStack stack, int reduce) {
      if (stack != null && !stack.isEmpty()) {
         StrengtheningMahou mahou = stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT);
         if (mahou != null) {
            mahou=mahou.copy();
            mahou.setUses(mahou.getUses() - reduce);
            Utils.setStrengtheningMahou(stack, mahou);
            if (mahou.getUses() <= 0) {
               clearStrengthened(stack, mahou);
            }
         }
      }
   }

   private static void clearStrengthened(ItemStack stack, StrengtheningMahou mahou) {
      if (mahou.unbreakable) {
         stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
      } else {
         stack.remove(DataComponents.UNBREAKABLE);
      }

      stack.remove(stepsword.mahoutsukai.datacomponents.DataComponents.STRENGTHENING_COMPONENT);
   }

   public static boolean canStrengthen(ItemStack stack, Level world) {
      return MTConfig.STRENGTHENING_CAP > 0
         && stack != null
         && !stack.isEmpty()
         && !EffectUtil.inItemBlacklist(stack.getItem(), MTConfig.STRENGTHENING_ITEM_BLACKLIST, world);
   }

   public static boolean setStrengthened(ItemStack stack, Level world) {
      if (!world.isClientSide() && canStrengthen(stack, world)) {
         StrengtheningMahou mahou = Utils.getStrengtheningMahou(stack);
         int alreadyStrengthened = getStrengthened(stack);
         if (alreadyStrengthened == 0 && stack.has(DataComponents.UNBREAKABLE)) {
            mahou.setUnbreakable(true);
         }

         stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
         mahou.setUses(Math.min(MTConfig.STRENGTHENING_CAP, alreadyStrengthened + 5));
         Utils.setStrengtheningMahou(stack, mahou);
         return true;
      } else {
         return false;
      }
   }

   public static int getStrengthened(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && Utils.hasStrengtheningMahou(stack)) {
         StrengtheningMahou mahou = Utils.getStrengtheningMahou(stack);
         if (mahou.uses <= 0) {
            clearStrengthened(stack, mahou);
            return 0;
         } else {
            return mahou.uses;
         }
      } else {
         return 0;
      }
   }

   public static ItemStack findStackToStrengthen(Player p) {
      ItemStack stack = null;
      if (p != null) {
         stack = p.getOffhandItem();
         if (stack == null || stack.isEmpty()) {
            for (int i = 0; i < 9; i++) {
               stack = p.getInventory().getItem(i);
               if (stack != null && !stack.isEmpty() && !(stack.getItem() instanceof SpellScroll)) {
                  break;
               }
            }
         }
      }

      return stack;
   }
}
