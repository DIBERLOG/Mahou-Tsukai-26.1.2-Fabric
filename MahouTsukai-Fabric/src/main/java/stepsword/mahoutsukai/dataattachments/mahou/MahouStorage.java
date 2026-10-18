package stepsword.mahoutsukai.dataattachments.mahou;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.fabric.FunRequirement;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.Codecs;
import stepsword.mahoutsukai.util.UnorderedList;

public class MahouStorage {
   private static final String STORED_MANA = "stored_mana";
   private static final String MAX_MANA = "max_mana";
   private static final String CHARGE_RATE = "charge_rate";
   private static final String MANA_TO_USE_COUNTER = "mana_to_use_counter";
   private static final String MANA_FULL = "mana_full";
   private static final String ENEMY = "enemy";
   private static final String SAVED_SPAWN = "saved_spawn";
   private static final String MARBLE_SPAWN = "marble_spawn";
   private static final String OLD_DIMENSION = "old_dimension";
   private static final String MEMORIZED_EDMTE = "memorized_edmte";
   private static final String LAST_ARROW = "last_arrow";
   private static final String MENTAL_DISPLACEMENT = "mental_displacement";
   private static final String PROTECTIVE_DISPLACEMENT_USES = "protective_displacement_uses";
   private static final String DAMAGE_EXCHANGE_USES = "damage_exchange_uses";
   private static final String DEATH_COLLECTION_USES_LEFT = "death_collection_uses_left";
   private static final String IS_POSSESSING = "is_possessing";
   private static final String DAMAGE_REPLICATION_TARGET = "damage_replication_target";
   private static final String HAS_MAGIC = "has_magic";
   private static final String PREV_YAW = "prev_yaw";
   private static final String PREV_PITCH = "prev_pitch";
   private static final String LAST_RECIPE_CLOTH = "last_recipe_cloth";
   private static final String LAST_RECIPE = "last_recipe";
   private static final String PREV_EYE = "prev_eye";
   private static final String INTERESTING = "interesting";
   private static final String CANCEL_TIME = "cancel_time";
   private static final String IMMUNITY_DAMAGE_TYPE = "immunity_damage_type";
   private static final String VISIBLE = "visible";
   private static final String FAE_SPAWN = "fae_spawn";
   private static final String GEAS = "geas";
   private static final String PAGE = "page";
   private static final String MANDATORY_FUN = "mandatory_fun";

   public static void readNBT(IMahou instance, ValueInput input) {
      instance.clearBuffs();
      instance.setStoredMana(input.read("stored_mana", Codec.INT).orElse(0));
      instance.setMaxMana(input.read("max_mana", Codec.INT).orElse(100));
      instance.setChargeRate(input.read("charge_rate", Codec.DOUBLE).orElse(1.0));
      instance.setManaToUseCounter(input.read("mana_to_use_counter", Codec.INT).orElse(0));
      instance.setManaFull(input.read("mana_full", Codec.BOOL).orElse(false));
      input.read("immunity_damage_type", Codec.STRING).ifPresent(instance::setImmunityDamageType);
      input.read("enemy", UUIDUtil.CODEC).ifPresent(instance::setEnemy);
      input.read("saved_spawn", BlockPos.CODEC).ifPresent(instance::setPlayerSavedSpawn);
      input.read("marble_spawn", BlockPos.CODEC).ifPresent(instance::setPlayerMarbleSpawn);
      input.read("old_dimension", Codec.STRING).ifPresent(instance::setPlayerOldDimension);
      input.read("memorized_edmte", BlockDim.CODEC).ifPresent(instance::setMemorizedEDMTE);
      input.read("last_arrow", UUIDUtil.CODEC).ifPresent(instance::setLastArrowShot);
      input.read("mental_displacement", BlockPos.CODEC).ifPresent(instance::setMentalDisplacementPos);
      input.read("prev_eye", Vec3.CODEC).ifPresent(instance::setPlayerPrevEyePos);
      input.read("damage_replication_target", UUIDUtil.CODEC).ifPresent(instance::setDamageReplicationTarget);
      instance.setProtectiveDisplacement(input.read("protective_displacement_uses", Codec.INT).orElse(0));
      instance.setDamageExchangeUses(input.read("damage_exchange_uses", Codec.INT).orElse(0));
      instance.setDeathCollectionUsesLeft(input.read("death_collection_uses_left", Codec.FLOAT).orElse(0.0F));
      instance.setPossessing(input.read("is_possessing", Codec.BOOL).orElse(false));
      instance.setHasMagic(input.read("has_magic", Codec.BOOL).orElse(false));
      instance.setPlayerPrevPitch(input.read("prev_pitch", Codec.FLOAT).orElse(0.0F));
      instance.setPlayerPrevYaw(input.read("prev_yaw", Codec.FLOAT).orElse(0.0F));
      instance.setLastRecipeCloth(input.read("last_recipe_cloth", Codec.BOOL).orElse(false));
      instance.setCancelTime(input.read("cancel_time", Codec.LONG).orElse(0L));
      instance.setGuidePage(input.read("page", Codec.INT).orElse(0));
      input.read("last_recipe", UnorderedList.CODEC).ifPresent(instance::setLastRecipe);
      input.read("interesting", BlockPos.CODEC).ifPresent(instance::setFamiliarInterestingBlock);
      input.read("geas", Codecs.STRING_HASHSET_CODEC).ifPresent(instance::setGeas);
      input.read("mandatory_fun", Codecs.HASHMAP_STRING_BOOL_CODEC).ifPresent(fun -> {
         for (FunRequirement req : FunRequirement.values()) {
            if (!fun.containsKey(req.name())) {
               fun.put(req.name(), false);
            }
         }

         instance.setMandatoryFun((HashMap<String, Boolean>)fun);
      });
      instance.setVisible(input.read("visible", Codec.BOOL).orElse(true));
      instance.setFaeSpawn(input.read("fae_spawn", Codec.BOOL).orElse(true));
   }

   public static void writeNBT(IMahou instance, ValueOutput output) {
      output.store("stored_mana", Codec.INT, instance.getStoredMana());
      output.store("max_mana", Codec.INT, instance.getMaxMana());
      output.store("charge_rate", Codec.DOUBLE, instance.getChargeRate());
      output.store("mana_to_use_counter", Codec.INT, instance.getManaToUseCounter());
      output.store("mana_full", Codec.BOOL, instance.isManaFull());
      output.store("protective_displacement_uses", Codec.INT, instance.getProtectiveDisplacement());
      output.store("damage_exchange_uses", Codec.INT, instance.getDamageExchangeUses());
      output.store("death_collection_uses_left", Codec.FLOAT, instance.getDeathCollectionUsesLeft());
      output.store("is_possessing", Codec.BOOL, instance.isPossessing());
      output.store("has_magic", Codec.BOOL, instance.hasMagic());
      output.store("prev_pitch", Codec.FLOAT, instance.getPlayerPrevPitch());
      output.store("prev_yaw", Codec.FLOAT, instance.getPlayerPrevYaw());
      output.store("last_recipe_cloth", Codec.BOOL, instance.getLastRecipeCloth());
      output.store("cancel_time", Codec.LONG, instance.getCancelTime());
      output.store("visible", Codec.BOOL, instance.getVisible());
      output.store("fae_spawn", Codec.BOOL, instance.getFaeSpawn());
      output.store("page", Codec.INT, instance.getGuidePage());
      if (instance.getImmunityDamageType() != null) {
         output.store("immunity_damage_type", Codec.STRING, instance.getImmunityDamageType());
      }

      if (instance.getEnemy() != null) {
         output.store("enemy", UUIDUtil.CODEC, instance.getEnemy());
      }

      if (instance.getPlayerSavedSpawn() != null) {
         output.store("saved_spawn", BlockPos.CODEC, instance.getPlayerSavedSpawn());
      }

      if (instance.getPlayerMarbleSpawn() != null) {
         output.store("marble_spawn", BlockPos.CODEC, instance.getPlayerMarbleSpawn());
      }

      if (instance.getPlayerOldDimension() != null) {
         output.store("old_dimension", Codec.STRING, instance.getPlayerOldDimension());
      }

      if (instance.getMemorizedEDMTE() != null) {
         output.store("memorized_edmte", BlockDim.CODEC, instance.getMemorizedEDMTE());
      }

      if (instance.getLastArrowShot() != null) {
         output.store("last_arrow", UUIDUtil.CODEC, instance.getLastArrowShot());
      }

      if (instance.getMentalDisplacementPos() != null) {
         output.store("mental_displacement", BlockPos.CODEC, instance.getMentalDisplacementPos());
      }

      if (instance.getPlayerPrevEyePos() != null) {
         output.store("prev_eye", Vec3.CODEC, instance.getPlayerPrevEyePos());
      }

      if (instance.getDamageReplicationTarget() != null) {
         output.store("damage_replication_target", UUIDUtil.CODEC, instance.getDamageReplicationTarget());
      }

      if (instance.getFamiliarInterestingBlock() != null) {
         output.store("interesting", BlockPos.CODEC, instance.getFamiliarInterestingBlock());
      }

      if (instance.getLastRecipe() != null) {
         output.store("last_recipe", UnorderedList.CODEC, instance.getLastRecipe());
      }

      if (instance.getGeas() != null && !instance.getGeas().isEmpty()) {
         output.store("geas", Codecs.STRING_HASHSET_CODEC, instance.getGeas());
      }

      if (instance.getMandatoryFun() != null) {
         output.store("mandatory_fun", Codecs.HASHMAP_STRING_BOOL_CODEC, instance.getMandatoryFun());
      }
   }
}

