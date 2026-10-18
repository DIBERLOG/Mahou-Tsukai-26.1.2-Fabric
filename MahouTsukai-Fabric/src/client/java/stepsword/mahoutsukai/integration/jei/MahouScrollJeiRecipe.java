package stepsword.mahoutsukai.integration.jei;

import net.minecraft.resources.Identifier;

public sealed interface MahouScrollJeiRecipe
   permits ScrollFromMahoujinJeiRecipe,
   CaliburnJeiRecipe,
   MorganJeiRecipe,
   ReplicaJeiRecipe,
   ClarentJeiRecipe,
   EmrysJeiRecipe {
   Identifier id();
}
