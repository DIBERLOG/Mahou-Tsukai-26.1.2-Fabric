package stepsword.mahoutsukai.dataattachments.mahou;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.UnorderedList;

public class Mahou implements IMahou {
   private int storedMana = 100;
   private int maxMana = 100;
   private double chargeRate = 0.1;
   private int manaToUseCounter = 100;
   private boolean manaFull = true;
   private UUID enemy = null;
   private BlockPos playerPos = null;
   private BlockPos marbleSpawn = null;
   private UUID arrowUUID = null;
   private String dimension = null;
   private BlockDim blockDim = null;
   private int protectiveDisplacementUses = 0;
   private BlockPos mentalDisplacementPos;
   private int damageExchangeUses = 0;
   private float deathCollectionUsesLeft = 0.0F;
   private UUID damageReplication = null;
   private boolean possessing = false;
   private boolean hasMagic = false;
   private float prevYaw = 0.0F;
   private float prevPitch = 0.0F;
   private Vec3 prevEye = null;
   private boolean lastRecipeCloth = false;
   private UnorderedList lastRecipe = null;
   private BlockPos interestingBlockPos = null;
   private long cancelTime = 0L;
   private String immunityDamageType = null;
   private boolean visible = true;
   private boolean faespawn = true;
   private HashSet<String> geas = null;
   private int page = 0;
   private HashMap<String, Boolean> mandatoryFun = null;

   @Override
   public int getStoredMana() {
      return this.storedMana;
   }

   @Override
   public void setStoredMana(int storedMana) {
      this.storedMana = storedMana;
   }

   @Override
   public int getMaxMana() {
      return this.maxMana;
   }

   @Override
   public void setMaxMana(int maxMana) {
      this.maxMana = maxMana;
   }

   @Override
   public double getChargeRate() {
      return this.chargeRate;
   }

   @Override
   public void setChargeRate(double chargeRate) {
      this.chargeRate = chargeRate;
   }

   @Override
   public int getManaToUseCounter() {
      return this.manaToUseCounter;
   }

   @Override
   public void setManaToUseCounter(int manaToUseCounter) {
      this.manaToUseCounter = manaToUseCounter;
   }

   @Override
   public void setManaFull(boolean full) {
      this.manaFull = full;
   }

   @Override
   public boolean isManaFull() {
      return this.manaFull;
   }

   @Override
   public UUID getEnemy() {
      return this.enemy;
   }

   @Override
   public void setEnemy(UUID enemyUUID) {
      this.enemy = enemyUUID;
   }

   @Override
   public BlockPos getPlayerSavedSpawn() {
      return this.playerPos;
   }

   @Override
   public void setPlayerSavedSpawn(BlockPos pos) {
      this.playerPos = pos;
   }

   @Override
   public BlockPos getPlayerMarbleSpawn() {
      return this.marbleSpawn;
   }

   @Override
   public void setPlayerMarbleSpawn(BlockPos pos) {
      this.marbleSpawn = pos;
   }

   @Override
   public String getPlayerOldDimension() {
      return this.dimension;
   }

   @Override
   public void setPlayerOldDimension(String d) {
      this.dimension = d;
   }

   @Override
   public UUID getLastArrowShot() {
      return this.arrowUUID;
   }

   @Override
   public void setLastArrowShot(UUID arrowUUID) {
      this.arrowUUID = arrowUUID;
   }

   @Override
   public BlockDim getMemorizedEDMTE() {
      return this.blockDim;
   }

   @Override
   public void setMemorizedEDMTE(BlockDim loc) {
      this.blockDim = loc;
   }

   @Override
   public int getProtectiveDisplacement() {
      return this.protectiveDisplacementUses;
   }

   @Override
   public void setProtectiveDisplacement(int u) {
      this.protectiveDisplacementUses = u;
   }

   @Override
   public int getDamageExchangeUses() {
      return this.damageExchangeUses;
   }

   @Override
   public void setDamageExchangeUses(int u) {
      this.damageExchangeUses = u;
   }

   @Override
   public BlockPos getMentalDisplacementPos() {
      return this.mentalDisplacementPos;
   }

   @Override
   public void setMentalDisplacementPos(BlockPos pos) {
      this.mentalDisplacementPos = pos;
   }

   @Override
   public float getDeathCollectionUsesLeft() {
      return this.deathCollectionUsesLeft;
   }

   @Override
   public void setDeathCollectionUsesLeft(float b) {
      this.deathCollectionUsesLeft = b;
   }

   @Override
   public boolean isPossessing() {
      return this.possessing;
   }

   @Override
   public void setPossessing(boolean b) {
      this.possessing = b;
   }

   @Override
   public UUID getDamageReplicationTarget() {
      return this.damageReplication;
   }

   @Override
   public void setDamageReplicationTarget(UUID uuid) {
      this.damageReplication = uuid;
   }

   @Override
   public boolean hasMagic() {
      return this.hasMagic;
   }

   @Override
   public void setHasMagic(boolean b) {
      this.hasMagic = b;
   }

   @Override
   public float getPlayerPrevPitch() {
      return this.prevPitch;
   }

   @Override
   public void setPlayerPrevPitch(float p) {
      this.prevPitch = p;
   }

   @Override
   public float getPlayerPrevYaw() {
      return this.prevYaw;
   }

   @Override
   public void setPlayerPrevYaw(float y) {
      this.prevYaw = y;
   }

   @Override
   public Vec3 getPlayerPrevEyePos() {
      return this.prevEye;
   }

   @Override
   public void setPlayerPrevEyePos(Vec3 v) {
      this.prevEye = v;
   }

   @Override
   public boolean getLastRecipeCloth() {
      return this.lastRecipeCloth;
   }

   @Override
   public void setLastRecipeCloth(boolean b) {
      this.lastRecipeCloth = b;
   }

   @Override
   public UnorderedList getLastRecipe() {
      return this.lastRecipe;
   }

   @Override
   public void setLastRecipe(UnorderedList u) {
      this.lastRecipe = u;
   }

   @Override
   public BlockPos getFamiliarInterestingBlock() {
      return this.interestingBlockPos;
   }

   @Override
   public void setFamiliarInterestingBlock(BlockPos r) {
      this.interestingBlockPos = r;
   }

   @Override
   public String getImmunityDamageType() {
      return this.immunityDamageType;
   }

   @Override
   public void setImmunityDamageType(String s) {
      this.immunityDamageType = s;
   }

   @Override
   public long getCancelTime() {
      return this.cancelTime;
   }

   @Override
   public void setCancelTime(long c) {
      this.cancelTime = c;
   }

   @Override
   public boolean getVisible() {
      return this.visible;
   }

   @Override
   public void setVisible(boolean v) {
      this.visible = v;
   }

   @Override
   public boolean getFaeSpawn() {
      return this.faespawn;
   }

   @Override
   public void setFaeSpawn(boolean v) {
      this.faespawn = v;
   }

   @Override
   public HashSet<String> getGeas() {
      return this.geas;
   }

   @Override
   public void setGeas(HashSet<String> g) {
      this.geas = g;
   }

   @Override
   public void setGuidePage(int p) {
      this.page = p;
   }

   @Override
   public int getGuidePage() {
      return this.page;
   }

   @Override
   public HashMap<String, Boolean> getMandatoryFun() {
      return this.mandatoryFun;
   }

   @Override
   public void setMandatoryFun(HashMap<String, Boolean> fun) {
      this.mandatoryFun = fun;
   }

   @Override
   public void copyMahou(IMahou m) {
      if (m != null) {
         this.setStoredMana(m.getStoredMana());
         this.setChargeRate(m.getChargeRate());
         this.setManaFull(m.isManaFull());
         this.setManaToUseCounter(m.getManaToUseCounter());
         this.setMaxMana(m.getMaxMana());
         this.setEnemy(m.getEnemy());
         this.setPlayerSavedSpawn(m.getPlayerSavedSpawn());
         this.setPlayerMarbleSpawn(m.getPlayerMarbleSpawn());
         this.setPlayerOldDimension(m.getPlayerOldDimension());
         this.setLastArrowShot(m.getLastArrowShot());
         this.setMemorizedEDMTE(m.getMemorizedEDMTE());
         this.setMentalDisplacementPos(m.getMentalDisplacementPos());
         this.setProtectiveDisplacement(m.getProtectiveDisplacement());
         this.setDamageExchangeUses(m.getDamageExchangeUses());
         this.setDamageReplicationTarget(m.getDamageReplicationTarget());
         this.setDeathCollectionUsesLeft(m.getDeathCollectionUsesLeft());
         this.setPossessing(m.isPossessing());
         this.setHasMagic(m.hasMagic());
         this.setPlayerPrevPitch(m.getPlayerPrevPitch());
         this.setPlayerPrevYaw(m.getPlayerPrevYaw());
         this.setPlayerPrevEyePos(m.getPlayerPrevEyePos());
         this.setLastRecipe(m.getLastRecipe());
         this.setLastRecipeCloth(m.getLastRecipeCloth());
         this.setFamiliarInterestingBlock(m.getFamiliarInterestingBlock());
         this.setImmunityDamageType(m.getImmunityDamageType());
         this.setVisible(m.getVisible());
         this.setFaeSpawn(m.getFaeSpawn());
         this.setGeas(m.getGeas());
         this.setMandatoryFun(m.getMandatoryFun());
         this.setGuidePage(m.getGuidePage());
      }
   }

   @Override
   public void clearBuffs() {
      this.setPlayerPrevYaw(0.0F);
      this.setPlayerPrevPitch(0.0F);
      this.setPlayerPrevEyePos(null);
      this.setProtectiveDisplacement(0);
      this.setDamageExchangeUses(0);
      this.setFamiliarInterestingBlock(null);
      this.setDamageReplicationTarget(null);
      this.setPossessing(false);
      this.setDeathCollectionUsesLeft(0.0F);
      this.setImmunityDamageType(null);
   }
}
