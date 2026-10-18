package stepsword.mahoutsukai.item.guide;

import java.util.ArrayList;
import net.minecraft.network.chat.Component;

public class DoubleInfoPage extends Page {
   String pageNameLang;
   String pageDescLang;
   String value;
   String pageNameLang2;
   String pageDescLang2;
   String value2;
   ArrayList<String> values = new ArrayList<>();

   public DoubleInfoPage(String pageName, String pageDesc, String replacevalue, String pageName2, String pageDesc2, String replacevalue2) {
      this.pageNameLang = pageName;
      this.pageDescLang = pageDesc;
      this.value = replacevalue;
      this.pageNameLang2 = pageName2;
      this.pageDescLang2 = pageDesc2;
      this.value2 = replacevalue2;
   }

   public DoubleInfoPage setValues(ArrayList<String> v) {
      this.values = v;
      return this;
   }

   @Override
   public String getTitle() {
      return Component.translatable(this.pageNameLang).getString();
   }

   public String getTitle2() {
      return Component.translatable(this.pageNameLang2).getString();
   }

   public String getDescription() {
      return this.processString(Component.translatable(this.pageDescLang).getString(), this.value);
   }

   public String getDescription2() {
      return this.processString(Component.translatable(this.pageDescLang2).getString(), this.value2);
   }

   public String processString(String s, String v) {
      int k = 0;
      if (this.values.isEmpty()) {
         s = s.replace("%k", v);
      } else {
         for (String value : this.values) {
            s = s.replace("%k" + k, value);
            k++;
         }
      }

      return s.replace("%n", "\n");
   }
}
