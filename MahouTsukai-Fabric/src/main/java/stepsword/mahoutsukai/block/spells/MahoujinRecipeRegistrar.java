package stepsword.mahoutsukai.block.spells;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import stepsword.mahoutsukai.block.BlockBase;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.catalysts.PowderedCatalyst;
import stepsword.mahoutsukai.util.UnorderedList;

public class MahoujinRecipeRegistrar {
   public static HashMap<UnorderedList, BlockBase> recipes = new HashMap<>();
   public static HashMap<UnorderedList, Boolean> needsCloth = new HashMap<>();
   public static HashMap<String, ItemBase> ingredients = new HashMap<>();
   public static String POWDERED_IRON = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_iron")).getCatalystName();
   public static String POWDERED_QUARTZ = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_quartz")).getCatalystName();
   public static String POWDERED_EMERALD = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_emerald")).getCatalystName();
   public static String POWDERED_DIAMOND = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_diamond")).getCatalystName();
   public static String POWDERED_GOLD = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_gold")).getCatalystName();
   public static String POWDERED_ENDER = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_ender")).getCatalystName();
   public static String POWDERED_EYE = ((PowderedCatalyst)ModItems.REGISTERED.get("powdered_eye")).getCatalystName();

   public static void init() {
      var scryingRecipe=makeRecipe(POWDERED_ENDER, POWDERED_ENDER, POWDERED_EYE);recipes.put(scryingRecipe,(BlockBase)ModBlocks.scryingMahoujin.get());needsCloth.put(scryingRecipe,false);
      var butterflyEffectRecipe=makeRecipe(POWDERED_QUARTZ, POWDERED_QUARTZ, POWDERED_DIAMOND);recipes.put(butterflyEffectRecipe,(BlockBase)ModBlocks.butterflyEffectMahoujin.get());needsCloth.put(butterflyEffectRecipe,true);
      var mysticStaffRecipe=makeRecipe(POWDERED_GOLD, POWDERED_GOLD, POWDERED_DIAMOND);recipes.put(mysticStaffRecipe,(BlockBase)ModBlocks.mysticStaffMahoujin.get());needsCloth.put(mysticStaffRecipe,true);
      var realityMarbleRecipe=makeRecipe(POWDERED_DIAMOND, POWDERED_DIAMOND, POWDERED_DIAMOND);recipes.put(realityMarbleRecipe,(BlockBase)ModBlocks.realityMarbleMahoujin.get());needsCloth.put(realityMarbleRecipe,true);
      var treasuryProjectionRecipe=makeRecipe(POWDERED_DIAMOND, POWDERED_DIAMOND, POWDERED_EYE);recipes.put(treasuryProjectionRecipe,(BlockBase)ModBlocks.treasuryProjectionMahoujin.get());needsCloth.put(treasuryProjectionRecipe,true);
      var weaponShooterRecipe=makeRecipe(POWDERED_DIAMOND, POWDERED_DIAMOND, POWDERED_GOLD);recipes.put(weaponShooterRecipe,(BlockBase)ModBlocks.weaponShooterMahoujin.get());needsCloth.put(weaponShooterRecipe,true);
      var spatialDisorientationRecipe=makeRecipe(POWDERED_GOLD, POWDERED_GOLD, POWDERED_ENDER);recipes.put(spatialDisorientationRecipe,(BlockBase)ModBlocks.spatialDisorientationMahoujin.get());needsCloth.put(spatialDisorientationRecipe,true);
      var possessEntityRecipe=makeRecipe(POWDERED_QUARTZ, POWDERED_QUARTZ, POWDERED_GOLD);recipes.put(possessEntityRecipe,(BlockBase)ModBlocks.possessEntityMahoujin.get());needsCloth.put(possessEntityRecipe,true);
      var mentalDisplacementRecipe=makeRecipe(POWDERED_ENDER, POWDERED_ENDER, POWDERED_QUARTZ);recipes.put(mentalDisplacementRecipe,(BlockBase)ModBlocks.mentalDisplacementMahoujin.get());needsCloth.put(mentalDisplacementRecipe,true);
      var borrowedAuthorityRecipe=makeRecipe(POWDERED_GOLD, POWDERED_GOLD, POWDERED_GOLD);recipes.put(borrowedAuthorityRecipe,(BlockBase)ModBlocks.borrowedAuthorityMahoujin.get());needsCloth.put(borrowedAuthorityRecipe,true);
      var fallenDownRecipe=makeRecipe(POWDERED_GOLD, POWDERED_ENDER, POWDERED_IRON);recipes.put(fallenDownRecipe,(BlockBase)ModBlocks.fallenDownMahoujin.get());needsCloth.put(fallenDownRecipe,true);
      var rhoAiasRecipe=makeRecipe(POWDERED_GOLD, POWDERED_GOLD, POWDERED_IRON);recipes.put(rhoAiasRecipe,(BlockBase)ModBlocks.rhoAiasMahoujin.get());needsCloth.put(rhoAiasRecipe,true);
      var gandrRecipe=makeRecipe(POWDERED_GOLD, POWDERED_EMERALD, POWDERED_DIAMOND);recipes.put(gandrRecipe,(BlockBase)ModBlocks.gandrMahoujin.get());needsCloth.put(gandrRecipe,true);
      var presenceConcealmentRecipe=makeRecipe(POWDERED_ENDER, POWDERED_IRON, POWDERED_DIAMOND);recipes.put(presenceConcealmentRecipe,(BlockBase)ModBlocks.presenceConcealmentMahoujin.get());needsCloth.put(presenceConcealmentRecipe,true);
      var reversionEyesRecipe=makeRecipe(POWDERED_EYE, POWDERED_EYE, POWDERED_EMERALD);recipes.put(reversionEyesRecipe,(BlockBase)ModBlocks.reversionEyesMahoujin.get());needsCloth.put(reversionEyesRecipe,true);
      var cupOfHeavenRecipe=makeRecipe(POWDERED_GOLD, POWDERED_GOLD, POWDERED_QUARTZ);recipes.put(cupOfHeavenRecipe,(BlockBase)ModBlocks.cupOfHeavenMahoujin.get());needsCloth.put(cupOfHeavenRecipe,false);
      var predictionRecipe=makeRecipe(POWDERED_EYE, POWDERED_EYE, POWDERED_QUARTZ);recipes.put(predictionRecipe,(BlockBase)ModBlocks.predictionMahoujin.get());needsCloth.put(predictionRecipe,true);
      var insightRecipe=makeRecipe(POWDERED_EYE, POWDERED_EYE, POWDERED_ENDER);recipes.put(insightRecipe,(BlockBase)ModBlocks.insightEyesMahoujin.get());needsCloth.put(insightRecipe,true);
      var lake=makeRecipe(POWDERED_DIAMOND,POWDERED_DIAMOND,POWDERED_EMERALD);recipes.put(lake,(BlockBase)ModBlocks.powerConsolidationMahoujin.get());needsCloth.put(lake,false);
      var selectiveDisplacement=makeRecipe(POWDERED_EYE,POWDERED_ENDER,POWDERED_EMERALD);recipes.put(selectiveDisplacement,(BlockBase)ModBlocks.selectiveDisplacementMahoujin.get());needsCloth.put(selectiveDisplacement,true);
      var damageReplication=makeRecipe(POWDERED_GOLD,POWDERED_GOLD,POWDERED_EMERALD);recipes.put(damageReplication,(BlockBase)ModBlocks.damageReplicationMahoujin.get());needsCloth.put(damageReplication,true);
      var strengthening=makeRecipe(POWDERED_DIAMOND,POWDERED_DIAMOND,POWDERED_IRON);recipes.put(strengthening,(BlockBase)ModBlocks.strengtheningMahoujin.get());needsCloth.put(strengthening,true);
      var deathCollection = makeRecipe(POWDERED_EYE,POWDERED_EYE,POWDERED_IRON);
      recipes.put(deathCollection,(BlockBase)ModBlocks.deathCollectionMahoujin.get());needsCloth.put(deathCollection,true);
      var projectileDisplacement = makeRecipe(POWDERED_ENDER,POWDERED_ENDER,POWDERED_DIAMOND);
      recipes.put(projectileDisplacement,(BlockBase)ModBlocks.projectileDisplacementMahoujin.get());needsCloth.put(projectileDisplacement,true);
      var retribution = makeRecipe(POWDERED_EMERALD,POWDERED_IRON,POWDERED_DIAMOND);
      recipes.put(retribution,(BlockBase)ModBlocks.retributionMahoujin.get());needsCloth.put(retribution,true);
      var mysticEyes = makeRecipe(POWDERED_EYE,POWDERED_EYE,POWDERED_GOLD);
      recipes.put(mysticEyes,(BlockBase)ModBlocks.mysticEyesMahoujin.get());needsCloth.put(mysticEyes,true);
      var equivalentDisplacement = makeRecipe(POWDERED_ENDER,POWDERED_ENDER,POWDERED_EMERALD);
      recipes.put(equivalentDisplacement,(BlockBase)ModBlocks.equivalentDisplacementMahoujin.get());needsCloth.put(equivalentDisplacement,false);
      var orderedDisplacement = makeRecipe(POWDERED_ENDER,POWDERED_ENDER,POWDERED_ENDER);
      recipes.put(orderedDisplacement,(BlockBase)ModBlocks.orderedDisplacementMahoujin.get());needsCloth.put(orderedDisplacement,false);
      var contract = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_ENDER);
      recipes.put(contract,(BlockBase)ModBlocks.contractMahoujin.get());needsCloth.put(contract,false);
      var tangibleBoundary = makeRecipe(POWDERED_IRON,POWDERED_IRON,POWDERED_DIAMOND);
      recipes.put(tangibleBoundary,(BlockBase)ModBlocks.tangibleBoundaryMahoujin.get());needsCloth.put(tangibleBoundary,false);
      var raiseEnclosureBoundary = makeRecipe(POWDERED_IRON,POWDERED_IRON,POWDERED_IRON);
      recipes.put(raiseEnclosureBoundary,(BlockBase)ModBlocks.raiseEnclosureBoundaryMahoujin.get());needsCloth.put(raiseEnclosureBoundary,false);
      var drainLifeBoundary = makeRecipe(POWDERED_IRON,POWDERED_IRON,POWDERED_EMERALD);
      recipes.put(drainLifeBoundary,(BlockBase)ModBlocks.drainLifeBoundaryMahoujin.get());needsCloth.put(drainLifeBoundary,false);
      var displacementBoundary = makeRecipe(POWDERED_IRON,POWDERED_IRON,POWDERED_ENDER);
      recipes.put(displacementBoundary,(BlockBase)ModBlocks.displacementBoundaryMahoujin.get());needsCloth.put(displacementBoundary,false);
      var gravityBoundary = makeRecipe(POWDERED_IRON,POWDERED_IRON,POWDERED_GOLD);
      recipes.put(gravityBoundary,(BlockBase)ModBlocks.gravityBoundaryMahoujin.get());needsCloth.put(gravityBoundary,false);
      var alarmBoundary = makeRecipe(POWDERED_IRON,POWDERED_IRON,POWDERED_QUARTZ);
      recipes.put(alarmBoundary,(BlockBase)ModBlocks.alarmBoundaryMahoujin.get());needsCloth.put(alarmBoundary,false);
      var durabilityExchange = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_DIAMOND);
      recipes.put(durabilityExchange,(BlockBase)ModBlocks.durabilityExchangeMahoujin.get());needsCloth.put(durabilityExchange,false);
      var alchemicalExchange = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_GOLD);
      recipes.put(alchemicalExchange,(BlockBase)ModBlocks.alchemicalExchangeMahoujin.get());needsCloth.put(alchemicalExchange,false);
      var catalystExchange = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_EMERALD);
      recipes.put(catalystExchange,(BlockBase)ModBlocks.catalystExchangeMahoujin.get());needsCloth.put(catalystExchange,false);
      var chronalExchange = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_QUARTZ);
      recipes.put(chronalExchange,(BlockBase)ModBlocks.chronalExchangeMahoujin.get());needsCloth.put(chronalExchange,false);
      var protectiveDisplacement = makeRecipe(POWDERED_ENDER,POWDERED_ENDER,POWDERED_IRON);
      recipes.put(protectiveDisplacement,(BlockBase)ModBlocks.protectiveDisplacementMahoujin.get());needsCloth.put(protectiveDisplacement,true);
      var immunityExchange = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_EYE);
      recipes.put(immunityExchange,(BlockBase)ModBlocks.immunityExchangeMahoujin.get());needsCloth.put(immunityExchange,true);
      var damageExchange = makeRecipe(POWDERED_EMERALD,POWDERED_EMERALD,POWDERED_IRON);
      recipes.put(damageExchange,(BlockBase)ModBlocks.damageExchangeMahoujin.get());needsCloth.put(damageExchange,true);
      var projection=makeRecipe(POWDERED_DIAMOND,POWDERED_DIAMOND,POWDERED_QUARTZ);
      recipes.put(projection,(BlockBase)ModBlocks.projectionMahoujin.get());needsCloth.put(projection,true);
      var proximityProjection=makeRecipe(POWDERED_DIAMOND,POWDERED_DIAMOND,POWDERED_ENDER);
      recipes.put(proximityProjection,(BlockBase)ModBlocks.proximityProjectionMahoujin.get());needsCloth.put(proximityProjection,true);
      var summonFamiliar=makeRecipe(POWDERED_QUARTZ,POWDERED_QUARTZ,POWDERED_QUARTZ);
      recipes.put(summonFamiliar,(BlockBase)ModBlocks.summonFamiliarMahoujin.get());needsCloth.put(summonFamiliar,true);
      var recallFamiliar=makeRecipe(POWDERED_QUARTZ,POWDERED_QUARTZ,POWDERED_ENDER);
      recipes.put(recallFamiliar,(BlockBase)ModBlocks.recallFamiliarMahoujin.get());needsCloth.put(recallFamiliar,true);
      var swapFamiliar=makeRecipe(POWDERED_QUARTZ,POWDERED_QUARTZ,POWDERED_EMERALD);
      recipes.put(swapFamiliar,(BlockBase)ModBlocks.swapFamiliarMahoujin.get());needsCloth.put(swapFamiliar,true);
      var familiarsGarden=makeRecipe(POWDERED_QUARTZ,POWDERED_QUARTZ,POWDERED_IRON);
      recipes.put(familiarsGarden,(BlockBase)ModBlocks.familiarsGardenMahoujin.get());needsCloth.put(familiarsGarden,true);
      var blackFlame=makeRecipe(POWDERED_EYE,POWDERED_EYE,POWDERED_DIAMOND);
      recipes.put(blackFlame,(BlockBase)ModBlocks.blackFlameMahoujin.get());needsCloth.put(blackFlame,true);
      var geas = makeRecipe(POWDERED_GOLD, POWDERED_EMERALD, POWDERED_IRON);
      recipes.put(geas,(BlockBase)ModBlocks.geasMahoujin.get());needsCloth.put(geas,true);
      var sight = makeRecipe(POWDERED_EYE, POWDERED_EYE, POWDERED_EYE);
      var ascent = makeRecipe(POWDERED_ENDER, POWDERED_ENDER, POWDERED_GOLD);
      var probability = makeRecipe(POWDERED_GOLD, POWDERED_DIAMOND, POWDERED_QUARTZ);
      recipes.put(sight, (BlockBase)ModBlocks.faySightEyesMahoujin.get()); needsCloth.put(sight, true);
      recipes.put(ascent, (BlockBase)ModBlocks.ascensionMahoujin.get()); needsCloth.put(ascent, false);
      recipes.put(probability, (BlockBase)ModBlocks.probabilityAlterMahoujin.get()); needsCloth.put(probability, false);
      for (var item : ModItems.REGISTERED.values()) if (item instanceof PowderedCatalyst catalyst)
         ingredients.put(catalyst.getCatalystName(), catalyst);
   }

   public static UnorderedList makeRecipe(String itemA, String itemB, String itemC) {
      return new UnorderedList(new ArrayList<>(Arrays.asList(itemA, itemB, itemC)));
   }
}
