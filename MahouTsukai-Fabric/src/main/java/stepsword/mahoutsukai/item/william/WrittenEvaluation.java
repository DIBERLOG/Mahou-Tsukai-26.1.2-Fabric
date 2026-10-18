package stepsword.mahoutsukai.item.william;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.worldsave.FictionData;

public class WrittenEvaluation {
 private static final org.slf4j.Logger LOGGER=org.slf4j.LoggerFactory.getLogger("mahoutsukai");


   public static float plagiarismEvaluation(HashMap<String, Integer> newText, HashMap<String, Integer> oldText) {
      HashSet<String> keys = new HashSet<>(newText.keySet());
      keys.addAll(oldText.keySet());
      double newWords = (1.0F - (float)(keys.size() - oldText.size()) / newText.size()) * MTConfig.PLAGIARISM_NEW_WORD_WEIGHT;
      double similarWordCount = MTConfig.PLAGIARISM_SIMILAR_WORD_COUNT_WEIGHT;
      float common = 0.0F;
      float sumdiff = 0.0F;

      for (String k : keys) {
         if (newText.containsKey(k) && oldText.containsKey(k)) {
            int n = newText.get(k);
            int o = oldText.get(k);
            sumdiff += closeness(n, o);
            common++;
         } else {
            sumdiff++;
            common++;
         }
      }

      if (common > 0.0F) {
         sumdiff /= common;
      }

      similarWordCount *= 1.0F - sumdiff;
      return (float)(newWords + similarWordCount);
   }

   public static int evaluateString(String s, ConcurrentHashMap<FictionData, UUID> existing) {
      WrittenEvaluation.BreakDown analysis = analysis(s);
      float plagiarism = 0.0F;

      for (FictionData other : existing.keySet()) {
         WrittenEvaluation.BreakDown a = analysis(other.text);
         float p = plagiarismEvaluation(analysis.countsOfWords, a.countsOfWords);
         if (plagiarism < p) {
            plagiarism = p;
         }
      }

      double bestScore = -2.1474836E9F;
      double worstScore = 2.147483647E9;
      String bd = null;
      String wd = null;

      for (String dict : DictionaryDownloader.dictionaries.keySet()) {
         DictionarySearcher ds = DictionaryDownloader.dictionaries.get(dict).dictionary;
         double cscore = characterScore(analysis, ds);
         double wscore = wordScore(analysis, ds);
         double pscore = punctuationScore(analysis, ds);
         double sscore = spaceScore(analysis, ds);
         double score = Math.abs(cscore) * MTConfig.CHARACTER_SCORE_FACTOR
            + Math.abs(wscore) * MTConfig.WORD_SCORE_FACTOR
            + Math.abs(pscore) * MTConfig.PUNCTUATION_SCORE_FACTOR
            + Math.abs(sscore) * MTConfig.SPACE_SCORE_FACTOR;
         if (MTConfig.RHO_AIAS_KILL_DEBUG) {
            LOGGER.info("word score: {}", wscore);
            LOGGER.info("char score: {}", cscore);
            LOGGER.info("punctuation score: {}", pscore);
            LOGGER.info("space score: {}", sscore);
            LOGGER.info("weighted word score: {}", Math.abs(wscore) * MTConfig.WORD_SCORE_FACTOR);
            LOGGER.info("weighted char score: {}", Math.abs(cscore) * MTConfig.CHARACTER_SCORE_FACTOR);
            LOGGER.info("weighted punctuation score: {}", Math.abs(pscore) * MTConfig.PUNCTUATION_SCORE_FACTOR);
            LOGGER.info("weighted space score: {}", Math.abs(sscore) * MTConfig.SPACE_SCORE_FACTOR);
         }

         if (cscore < 0.0 || wscore < 0.0) {
            score *= -1.0;
         }

         if (score > bestScore) {
            bestScore = score;
            bd = dict;
         }

         if (score < worstScore) {
            worstScore = score;
         }
      }

      double chosenScore = bestScore;
      LOGGER.debug("MahouTsukai fiction creation:");
      LOGGER.debug("Best Score: {}", bestScore);
      LOGGER.debug("Best Matching Dictionary: {}", bd);
      LOGGER.debug("Worst Score: {}", worstScore);
      LOGGER.debug("Plagiarism: {}", plagiarism);
      if (plagiarism > MTConfig.ACCEPTABLE_PLAGIARISM) {
         plagiarism = 1.0F;
      }

      return (int)Math.floor(chosenScore * (1.0F - plagiarism));
   }

   public static WrittenEvaluation.BreakDown analysis(String s) {
      HashMap<String, Integer> countsOfWords = new HashMap<>();
      HashMap<Character, Integer> countsOfChars = new HashMap<>();
      HashMap<Character, Integer> countsOfSpaces = new HashMap<>();
      HashMap<Character, Integer> countsOfPuncts = new HashMap<>();
      StringBuilder currentWord = new StringBuilder();
      int ws = 0;
      int cs = 0;

      for (int p = 0; p < s.length(); p++) {
         char c = s.charAt(p);
         if (Character.isWhitespace(c)) {
            String curr = currentWord.toString().toLowerCase(Locale.ENGLISH);
            curr = trimPunct(curr);
            if (!curr.isEmpty()) {
               countsOfWords.put(curr, countsOfWords.getOrDefault(curr, 0) + 1);
               ws++;
               currentWord = new StringBuilder();
            }

            countsOfSpaces.put(c, countsOfSpaces.getOrDefault(c, 0) + 1);
         } else {
            boolean punct = isPunct(s.substring(p, p + 1));
            Character pct = s.charAt(p);
            if (!currentWord.isEmpty() || !punct) {
               currentWord.append(pct);
               cs++;
               countsOfChars.put(c, countsOfChars.getOrDefault(c, 0) + 1);
            }

            if (punct) {
               countsOfPuncts.put(pct, countsOfPuncts.getOrDefault(pct, 0) + 1);
            }
         }
      }

      WrittenEvaluation.BreakDown ret = new WrittenEvaluation.BreakDown();
      ret.countsOfChars = countsOfChars;
      ret.countsOfWords = countsOfWords;
      ret.countsOfPuncts = countsOfPuncts;
      ret.countsOfSpaces = countsOfSpaces;
      ret.charCount = cs;
      ret.wordCount = ws;
      return ret;
   }

   public static String trimPunct(String s) {
      int i = 0;

      while (i < s.length() && isPunct(s.substring(i, i + 1))) {
         i++;
      }

      int j = s.length();

      while (j > 0 && isPunct(s.substring(j - 1, j))) {
         j--;
      }

      return s.substring(i, j);
   }

   public static boolean isPunct(String s) {
      return Pattern.matches("[\\p{Punct}\\p{IsPunctuation}]", s);
   }

   public static boolean isSpace(Character c) {
      return Character.isWhitespace(c);
   }

   public static String getBookText(ItemStack stack) {
      if (!stack.has(DataComponents.WRITTEN_BOOK_CONTENT)) {
         return "";
      }

      WrittenBookContent writtenbookcontent = (WrittenBookContent)stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
      List<Component> filtered = writtenbookcontent.getPages(true);
      List<Component> notfiltered = writtenbookcontent.getPages(false);
      StringBuilder bookText = new StringBuilder();

      for (int m = 0; m < MTConfig.WILLIAM_MAX_PAGES && m < filtered.size(); m++) {
         String s = filtered.get(m).getString();
         bookText.append(s).append("\n");
      }

      return bookText.toString();
   }

   public static float closeness(int a1, int a2) {
      return (float)Math.abs(a1 - a2) / Math.max(a1, a2);
   }

   public static int characterScore(WrittenEvaluation.BreakDown b, DictionarySearcher dictionary) {
      int m = 0;

      for (Character c : b.countsOfChars.keySet()) {
         if (!isPunct(c + "")) {
            m += b.countsOfChars.get(c);
         }

         if (dictionary.analysis.containsKey(c)) {
            m = (int)(m + (1.0F - closeness(dictionary.analysis.get(c), b.countsOfChars.get(c))) * MTConfig.CHARACTER_RATIO_POINTS);
         } else if (!isPunct(c + "")) {
            m -= MTConfig.CHARACTER_RATIO_POINTS;
         }
      }

      return m * b.countsOfChars.keySet().size();
   }

   public static int punctuationScore(WrittenEvaluation.BreakDown b, DictionarySearcher dictionary) {
      int m = 0;

      for (Character c : b.countsOfPuncts.keySet()) {
         if (isPunct(c + "")) {
            m += MTConfig.PUNCTUATION_RATIO_POINTS;
         }
      }

      return m;
   }

   public static int spaceScore(WrittenEvaluation.BreakDown b, DictionarySearcher dictionary) {
      int m = 0;

      for (Character c : b.countsOfSpaces.keySet()) {
         if (isSpace(c)) {
            m += MTConfig.SPACE_RATIO_POINTS;
         }
      }

      return m;
   }

   public static int wordScore(WrittenEvaluation.BreakDown b, DictionarySearcher dictionary) {
      int totalWords = 0;
      int realWordValue = 0;

      for (String s : b.countsOfWords.keySet()) {
         totalWords += b.countsOfWords.get(s);
         if (dictionary.isWord(s)) {
            realWordValue += MTConfig.REAL_WORD_POINTS * s.length();
         } else {
            realWordValue -= MTConfig.REAL_WORD_POINTS * s.length();
         }
      }

      return totalWords * b.countsOfWords.keySet().size() * realWordValue;
   }

   public static class BreakDown {
      HashMap<String, Integer> countsOfWords = new HashMap<>();
      HashMap<Character, Integer> countsOfChars = new HashMap<>();
      HashMap<Character, Integer> countsOfPuncts = new HashMap<>();
      HashMap<Character, Integer> countsOfSpaces = new HashMap<>();
      int wordCount = 0;
      int charCount = 0;
   }
}
