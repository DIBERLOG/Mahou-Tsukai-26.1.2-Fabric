package stepsword.mahoutsukai.item.guide;

import net.minecraft.network.chat.Component;

public class InfoPage extends Page {
   String pageNameLang;
   String pageDescLang;
   String value;

   public InfoPage(String pageName, String pageDesc, String replacevalue) {
      this.pageNameLang = pageName;
      this.pageDescLang = pageDesc;
      this.value = replacevalue;
   }

   @Override
   public String getTitle() {
      return Component.translatable(this.pageNameLang).getString();
   }

   public String getDescription() {
      return this.processString(Component.translatable(this.pageDescLang).getString());
   }

   public String processString(String s) {
      int k = 0;
      s = s.replace("%k", this.value);
      return s.replace("%n", "\n");
   }
}
