package stepsword.mahoutsukai.integration.jei;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Predicate;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.block.spells.SingleUseMahoujinBlockTileEntity;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.projection.ClarentEffect;
import stepsword.mahoutsukai.effects.projection.PowerConsolidationSpellEffect;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.ScrollGiver;
import stepsword.mahoutsukai.util.UnorderedList;
import stepsword.mahoutsukai.util.Utils;

@JeiPlugin
public class MahouJeiPlugin implements IModPlugin {
   public static mezz.jei.api.runtime.IJeiRuntime runtime;
   @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime value) { runtime=value; }
   @Override public void onRuntimeUnavailable() { runtime=null; }
   public static final IIngredientType<MahoujinJeiIngredient> MAHOUJIN_INGREDIENT = () -> MahoujinJeiIngredient.class;
   public static final IIngredientType<EntityJeiIngredient> ENTITY_INGREDIENT = () -> EntityJeiIngredient.class;
   public static final IRecipeType<MahouCircleJeiRecipe> MAHOUJIN_RECIPES = IRecipeType.create("mahoutsukai", "mahoujin", MahouCircleJeiRecipe.class);
   public static final IRecipeType<MahouScrollJeiRecipe> SCROLL_RECIPES = IRecipeType.create("mahoutsukai", "scrolls", MahouScrollJeiRecipe.class);
   public static final IRecipeType<MahouWeaponJeiRecipe> WEAPON_RECIPES = IRecipeType.create("mahoutsukai", "weapons", MahouWeaponJeiRecipe.class);

   public Identifier getPluginUid() {
      return Identifier.fromNamespaceAndPath("mahoutsukai", "mahou_jei");
   }

   public void registerIngredients(IModIngredientRegistration registration) {
      ensureMahoujinRecipesInitialized();
      registration.register(
         MAHOUJIN_INGREDIENT,
         createMahoujinIngredientCatalog(),
         new MahoujinJeiIngredientHelper(MAHOUJIN_INGREDIENT),
         new MahoujinJeiIngredientRenderer(),
         MahoujinJeiIngredient.CODEC
      );
      registration.register(
         ENTITY_INGREDIENT,
         createEntityIngredientCatalog(),
         new EntityJeiIngredientHelper(ENTITY_INGREDIENT),
         new EntityJeiIngredientRenderer(),
         EntityJeiIngredient.CODEC
      );
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new MahouCircleJeiCategory(gui.createDrawableItemLike(ModBlocks.mahoujin.get().asItem())),
            new MahouScrollJeiCategory(gui.createDrawableItemLike((ItemLike)ModItems.REGISTERED.get("scroll_boundary_alarm"))),
            new MahouWeaponJeiCategory(gui.createDrawableItemLike((ItemLike)ModItems.theripper.get()))
         }
      );
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addCraftingStation(MAHOUJIN_RECIPES, new ItemLike[]{ModBlocks.mahoujin.get().asItem()});
      registration.addCraftingStation(SCROLL_RECIPES, new ItemLike[]{ModBlocks.mahoujin.get().asItem()});
      registration.addCraftingStation(WEAPON_RECIPES, new ItemLike[]{(ItemLike)ModItems.REGISTERED.get("scroll_boundary_alarm")});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      ensureMahoujinRecipesInitialized();
      List<MahouCircleJeiRecipe> mahoujinRecipes = new ArrayList<>();
      List<MahouScrollJeiRecipe> scrollRecipes = new ArrayList<>();
      List<MahouWeaponJeiRecipe> weaponRecipes = new ArrayList<>();

      for (Entry<UnorderedList, BlockBase> entry : MahoujinRecipeRegistrar.recipes.entrySet()) {
         UnorderedList catalysts = entry.getKey();
         BlockBase block = entry.getValue();
         if (MahoujinRecipeRegistrar.needsCloth.getOrDefault(catalysts, false)) {
            createScrollRecipe(catalysts, block).ifPresent(scrollRecipes::add);
         } else {
            mahoujinRecipes.add(createMahoujinRecipe(catalysts, block));
         }
      }

      addWeaponRecipe(weaponRecipes, ModItems.presenceConcealmentScroll, ModItems.theripper);
      addWeaponRecipe(weaponRecipes, ModItems.treasuryProjectionScroll, ModItems.treasuryProjectionGauntlet);
      addWeaponRecipe(weaponRecipes, ModItems.spatialDisorientationScroll, ModItems.spatialStaff);
      addWeaponRecipe(weaponRecipes, ModItems.explosionStaffScroll, ModItems.explosionStaff);
      addWeaponRecipe(weaponRecipes, (() -> ModItems.REGISTERED.get("scroll_proximity_projection")), (() -> ModItems.REGISTERED.get("proximity_projection_keys")));
      addWeaponRecipe(weaponRecipes, (() -> ModItems.REGISTERED.get("scroll_probability_alter")), ModItems.ruleBreaker);
      addWeaponRecipe(weaponRecipes, ModItems.weaponShooterScroll, ModItems.weaponShooterBow);
      scrollRecipes.add(createCaliburnRecipe());
      scrollRecipes.add(createMorganRecipe());
      scrollRecipes.add(createReplicaRecipe());
      scrollRecipes.add(createClarentRecipe());
      scrollRecipes.add(createEmrysRecipe());
      registration.addRecipes(MAHOUJIN_RECIPES, mahoujinRecipes);
      registration.addRecipes(SCROLL_RECIPES, scrollRecipes);
      registration.addRecipes(WEAPON_RECIPES, weaponRecipes);
   }

   private static MahouCircleJeiRecipe createMahoujinRecipe(UnorderedList catalysts, BlockBase block) {
      Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
      return new MahouCircleJeiRecipe(
         Identifier.fromNamespaceAndPath("mahoutsukai", "mahoujin/" + blockId.getPath()),
         catalystStacks(catalysts),
         MahoujinJeiIngredient.create(blockId, catalysts, false)
      );
   }

   private static Optional<ScrollFromMahoujinJeiRecipe> createScrollRecipe(UnorderedList catalysts, BlockBase block) {
      if (block instanceof SingleUseMahoujinBlockTileEntity<?> singleUse) {
         MahoujinTileEntity tile = singleUse.createTileEntity(BlockPos.ZERO, block.defaultBlockState(), null);
         if (tile instanceof ScrollGiver scrollGiver) {
            ItemStack scroll = scrollGiver.getItemToGive();
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
            Identifier scrollId = BuiltInRegistries.ITEM.getKey(scroll.getItem());
            return Optional.of(
               new ScrollFromMahoujinJeiRecipe(
                  Identifier.fromNamespaceAndPath("mahoutsukai", "scrolls/" + scrollId.getPath()),
                  catalystStacks(catalysts),
                  MahoujinJeiIngredient.create(blockId, catalysts, true),
                  scroll
               )
            );
         } else {
            org.slf4j.LoggerFactory.getLogger("mahoutsukai")
               .warn("JEI skipped scroll recipe for {} because its tile does not implement ScrollGiver", BuiltInRegistries.BLOCK.getKey(block));
            return Optional.empty();
         }
      } else {
         org.slf4j.LoggerFactory.getLogger("mahoutsukai").warn("JEI skipped scroll recipe for {} because it is not a single-use Mahoujin", BuiltInRegistries.BLOCK.getKey(block));
         return Optional.empty();
      }
   }

   private static void addWeaponRecipe(List<MahouWeaponJeiRecipe> target, java.util.function.Supplier<Item> scroll, java.util.function.Supplier<Item> weapon) {
      Identifier weaponId = BuiltInRegistries.ITEM.getKey((Item)weapon.get());
      target.add(
         new MahouWeaponJeiRecipe(
            Identifier.fromNamespaceAndPath("mahoutsukai", "weapons/" + weaponId.getPath()),
            new ItemStack((ItemLike)scroll.get()),
            new ItemStack((ItemLike)weapon.get())
         )
      );
   }

   private static CaliburnJeiRecipe createCaliburnRecipe() {
      List<ItemStack> swords = itemsFromTag(ItemTags.SWORDS, stack -> PowerConsolidationSpellEffect.isItemAllowed(stack, Minecraft.getInstance().level));
      if (swords.isEmpty()) {
         swords.add(new ItemStack(Items.DIAMOND_SWORD));
      }

      UnorderedList catalysts = MahoujinRecipeRegistrar.makeRecipe(
         MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_EMERALD
      );
      BlockBase block = MahoujinRecipeRegistrar.recipes.get(catalysts);
      return new CaliburnJeiRecipe(
         id("special/caliburn"),
         swords,
         MahoujinJeiIngredient.create(BuiltInRegistries.BLOCK.getKey(block), catalysts, false),
         new ItemStack(Items.ENCHANTED_BOOK),
         new ItemStack((ItemLike)ModItems.caliburn.get())
      );
   }

   private static MorganJeiRecipe createMorganRecipe() {
      return new MorganJeiRecipe(
         id("special/morgan"),
         new ItemStack((ItemLike)ModItems.caliburn.get()),
         createMorganEntityIngredients(),
         new ItemStack((ItemLike)ModItems.morgan.get())
      );
   }

   private static ReplicaJeiRecipe createReplicaRecipe() {
      return new ReplicaJeiRecipe(
         id("special/replica"),
         List.of(
            new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_strengthening")),
            new ItemStack(Items.SHIELD),
            new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_damage_exchange")),
            new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_immunity_exchange")),
            new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_death_collection"))
         ),
         new ItemStack((ItemLike)ModItems.replica.get())
      );
   }

   private static ClarentJeiRecipe createClarentRecipe() {
      List<ItemStack> swords = itemsFromTag(ItemTags.SWORDS, stack -> !ClarentEffect.blacklisted(stack, Minecraft.getInstance().level));
      if (swords.isEmpty()) {
         swords.add(new ItemStack(Items.DIAMOND_SWORD));
      }

      return new ClarentJeiRecipe(
         id("special/clarent"),
         new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_strengthening")),
         swords,
         dragonBreathIngredient(),
         new ItemStack((ItemLike)ModItems.REGISTERED.get("clarent"))
      );
   }

   private static EmrysJeiRecipe createEmrysRecipe() {
      return new EmrysJeiRecipe(
         id("special/emrys"),
         new ItemStack((ItemLike)ModItems.REGISTERED.get("scroll_strengthening")),
         new ItemStack(Items.STICK),
         lightningIngredient(),
         new ItemStack((ItemLike)ModItems.emrysStaff.get())
      );
   }

   private static List<MahoujinJeiIngredient> createMahoujinIngredientCatalog() {
      Map<Identifier, MahoujinJeiIngredient> catalog = new LinkedHashMap<>();

      for (Entry<UnorderedList, BlockBase> entry : MahoujinRecipeRegistrar.recipes.entrySet()) {
         if (!MahoujinRecipeRegistrar.needsCloth.getOrDefault(entry.getKey(), false)) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(entry.getValue());
            MahoujinJeiIngredient ingredient = MahoujinJeiIngredient.create(blockId, entry.getKey(), false);
            catalog.put(ingredient.id(), ingredient);
         }
      }

      UnorderedList powerConsolidation = MahoujinRecipeRegistrar.makeRecipe(
         MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_EMERALD
      );
      BlockBase powerConsolidationBlock = MahoujinRecipeRegistrar.recipes.get(powerConsolidation);
      MahoujinJeiIngredient powerConsolidationIngredient = MahoujinJeiIngredient.create(
         BuiltInRegistries.BLOCK.getKey(powerConsolidationBlock), powerConsolidation, false
      );
      catalog.put(powerConsolidationIngredient.id(), powerConsolidationIngredient);
      return List.copyOf(catalog.values());
   }

   private static List<EntityJeiIngredient> createEntityIngredientCatalog() {
      return List.of();
   }

   private static List<EntityJeiIngredient> createMorganEntityIngredients() {
      List<EntityJeiIngredient> entities = new ArrayList<>();

      for (String configuredId : MTConfig.MORGAN_TRANSFORM_ENTITY_WHITELIST) {
         Identifier entityId = Identifier.tryParse(configuredId);
         if (entityId != null && BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
            entities.add(
               new EntityJeiIngredient(
                  Identifier.fromNamespaceAndPath("mahoutsukai", "entity/" + entityId.getNamespace() + "/" + entityId.getPath()),
                  entityId,
                  EntityJeiIngredient.Kind.NORMAL
               )
            );
         }
      }

      if (entities.isEmpty()) {
         Identifier villager = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VILLAGER);
         entities.add(new EntityJeiIngredient(id("entity/minecraft/villager"), villager, EntityJeiIngredient.Kind.NORMAL));
      }

      return entities;
   }

   private static EntityJeiIngredient dragonBreathIngredient() {
      return new EntityJeiIngredient(
         id("entity/dragon_breath"), BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.AREA_EFFECT_CLOUD), EntityJeiIngredient.Kind.DRAGON_BREATH
      );
   }

   private static EntityJeiIngredient lightningIngredient() {
      return new EntityJeiIngredient(
         id("entity/lightning"), BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.LIGHTNING_BOLT), EntityJeiIngredient.Kind.LIGHTNING
      );
   }

   private static List<ItemStack> catalystStacks(UnorderedList catalysts) {
      List<ItemStack> stacks = new ArrayList<>();

      for (String catalyst : catalysts.getOrder()) {
         Item item = MahoujinRecipeRegistrar.ingredients.get(catalyst);
         if (item == null) {
            throw new IllegalStateException("Unknown Mahoujin catalyst: " + catalyst);
         }

         stacks.add(new ItemStack(item));
      }

      return stacks;
   }

   private static List<ItemStack> itemsFromTag(TagKey<Item> tag, Predicate<ItemStack> filter) {
      List<ItemStack> stacks = new ArrayList<>();

      for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
         ItemStack stack = new ItemStack((ItemLike)holder.value());
         if (filter.test(stack)) {
            stacks.add(stack);
         }
      }

      return stacks;
   }

   private static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("mahoutsukai", path);
   }

   private static void ensureMahoujinRecipesInitialized() {
      if (MahoujinRecipeRegistrar.recipes.isEmpty()) {
         MahoujinRecipeRegistrar.init();
      }
   }
}
