package stepsword.mahoutsukai.dataattachments.mahou;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.UnorderedList;

public interface IMahou {
   int getStoredMana();

   void setStoredMana(int var1);

   int getMaxMana();

   void setMaxMana(int var1);

   double getChargeRate();

   void setChargeRate(double var1);

   int getManaToUseCounter();

   void setManaToUseCounter(int var1);

   void setManaFull(boolean var1);

   boolean isManaFull();

   UUID getEnemy();

   void setEnemy(UUID var1);

   BlockPos getPlayerSavedSpawn();

   void setPlayerSavedSpawn(BlockPos var1);

   BlockPos getPlayerMarbleSpawn();

   void setPlayerMarbleSpawn(BlockPos var1);

   String getPlayerOldDimension();

   void setPlayerOldDimension(String var1);

   UUID getLastArrowShot();

   void setLastArrowShot(UUID var1);

   BlockDim getMemorizedEDMTE();

   void setMemorizedEDMTE(BlockDim var1);

   int getProtectiveDisplacement();

   void setProtectiveDisplacement(int var1);

   int getDamageExchangeUses();

   void setDamageExchangeUses(int var1);

   BlockPos getMentalDisplacementPos();

   void setMentalDisplacementPos(BlockPos var1);

   float getDeathCollectionUsesLeft();

   void setDeathCollectionUsesLeft(float var1);

   boolean isPossessing();

   void setPossessing(boolean var1);

   UUID getDamageReplicationTarget();

   void setDamageReplicationTarget(UUID var1);

   boolean hasMagic();

   void setHasMagic(boolean var1);

   float getPlayerPrevPitch();

   void setPlayerPrevPitch(float var1);

   float getPlayerPrevYaw();

   void setPlayerPrevYaw(float var1);

   Vec3 getPlayerPrevEyePos();

   void setPlayerPrevEyePos(Vec3 var1);

   boolean getLastRecipeCloth();

   void setLastRecipeCloth(boolean var1);

   UnorderedList getLastRecipe();

   void setLastRecipe(UnorderedList var1);

   String getImmunityDamageType();

   void setImmunityDamageType(String var1);

   BlockPos getFamiliarInterestingBlock();

   void setFamiliarInterestingBlock(BlockPos var1);

   long getCancelTime();

   void setCancelTime(long var1);

   boolean getVisible();

   void setVisible(boolean var1);

   boolean getFaeSpawn();

   void setFaeSpawn(boolean var1);

   HashSet<String> getGeas();

   void setGeas(HashSet<String> var1);

   void setGuidePage(int var1);

   int getGuidePage();

   HashMap<String, Boolean> getMandatoryFun();

   void setMandatoryFun(HashMap<String, Boolean> var1);

   void copyMahou(IMahou var1);

   void clearBuffs();
}
