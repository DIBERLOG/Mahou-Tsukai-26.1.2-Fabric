package stepsword.mahoutsukai.item.guide;

import java.util.ArrayList;

public class ContentsPage extends Page {
   public ArrayList<String> pageNames = new ArrayList<>();
   public int start;
   public int end;

   public ContentsPage(ArrayList<Page> pages, int start, int stop) {
      for (int z = start; z < pages.size() && z < stop; z++) {
         this.pageNames.add(pages.get(z).getTitle());
      }

      this.start = start;
      this.end = stop;
   }

   @Override
   public String getTitle() {
      return "mahoutsukai.contents.name";
   }
}
