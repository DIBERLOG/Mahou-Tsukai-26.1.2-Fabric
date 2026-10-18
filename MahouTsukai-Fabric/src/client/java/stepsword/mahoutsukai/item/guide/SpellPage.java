package stepsword.mahoutsukai.item.guide;

import java.util.ArrayList;
import net.minecraft.network.chat.Component;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.util.UnorderedList;

public class SpellPage extends Page {
   String spellNameLang;
   UnorderedList recipe;
   String spellDescLang;
   ArrayList<String> strings;
   ArrayList<Integer> values;
   String clothLang;

   public SpellPage(String spellName, UnorderedList recipe, String langKey) {
      this.spellNameLang = spellName;
      this.recipe = recipe;
      this.spellDescLang = langKey;
      this.strings = new ArrayList<>();
      this.values = new ArrayList<>();
      if (MahoujinRecipeRegistrar.needsCloth.containsKey(recipe)) {
         if (MahoujinRecipeRegistrar.needsCloth.get(recipe)) {
            this.clothLang = "mahoutsukai.book.cloth_required.name";
         } else {
            this.clothLang = "mahoutsukai.book.cloth_optional.name";
         }
      } else {
         this.clothLang = "mahoutsukai.book.no_cloth.name";
      }
   }

   public SpellPage setValues(Integer... values) {
      for (Integer x : values) {
         this.values.add(x);
      }

      return this;
   }

   @Override
   public String getTitle() {
      return Component.translatable(this.spellNameLang).getString();
   }

   public String getCloth() {
      return Component.translatable(this.clothLang).getString();
   }

   public String getDescription() {
      return this.processString(Component.translatable(this.spellDescLang).getString());
   }

   public SpellPage setStrings(String... strings) {
      for (String s : strings) {
         this.strings.add(s);
      }

      return this;
   }

   public String processString(String s) {
      int k = 0;
      int j = 0;

      for (Integer v : this.values) {
         s = s.replace("%val" + k++, v + "");
      }

      for (String z : this.strings) {
         s = s.replace("%k" + j++, z);
      }

      return s.replace("%n", "\n");
   }

   public UnorderedList getRecipe() {
      return this.recipe;
   }
}
