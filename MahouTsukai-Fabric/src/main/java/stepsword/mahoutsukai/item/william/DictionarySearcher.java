package stepsword.mahoutsukai.item.william;

import java.util.HashMap;
import java.util.HashSet;

public class DictionarySearcher {
   public HashSet<String> words = new HashSet<>();
   public HashMap<Character, Integer> analysis = new HashMap<>();
   public int totalLetters = 0;

   public boolean isWord(String s) {
      return this.words.contains(s);
   }

   public static DictionarySearcher initializeDictionarySearcher(String s) {
      DictionarySearcher ds = new DictionarySearcher();
      StringBuilder curr = new StringBuilder();

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         ds.analysis.put(c, ds.analysis.getOrDefault(c, 0) + 1);
         if (i == s.length() - 1) {
            curr.append(c);
         }

         if (c != ',' && i != s.length() - 1) {
            curr.append(c);
         } else {
            ds.words.add(curr.toString());
            curr = new StringBuilder();
         }
      }

      ds.totalLetters = 0;

      for (Character c : ds.analysis.keySet()) {
         ds.totalLetters = ds.totalLetters + ds.analysis.get(c);
      }

      return ds;
   }
}
