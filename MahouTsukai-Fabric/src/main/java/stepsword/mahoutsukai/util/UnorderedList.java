package stepsword.mahoutsukai.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UnorderedList {
   private HashMap<String, Integer> coll = new HashMap<>();
   private ArrayList<String> list;
   public static final Codec<UnorderedList> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.STRING.listOf().xmap(ArrayList::new, value -> value).fieldOf("list").forGetter(UnorderedList::getOrder)).apply(instance, UnorderedList::new)
   );

   public UnorderedList() {
      this.list = new ArrayList<>();
   }

   public UnorderedList(ArrayList<String> lst) {
      for (int i = 0; i < lst.size(); i++) {
         this.add(lst.get(i), false);
      }

      this.list = lst;
   }

   public void add(String key, boolean addToList) {
      if (this.coll.containsKey(key.toString())) {
         this.coll.put(key, this.coll.get(key) + 1);
         if (addToList) {
            this.list.add(key);
         }
      } else {
         this.coll.put(key, 1);
         if (addToList) {
            this.list.add(key);
         }
      }
   }

   public String remove(String key) {
      if (this.coll.containsKey(key)) {
         this.coll.put(key, this.coll.get(key) - 1);
         if (this.coll.get(key) <= 0) {
            this.coll.remove(key);
         }

         this.list.remove(key);
         return key;
      } else {
         return null;
      }
   }

   public Integer get(String key) {
      return this.coll.get(key);
   }

   public Set<String> getKeys() {
      return this.coll.keySet();
   }

   public ArrayList<String> getOrder() {
      return this.list;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof UnorderedList cmp)) {
         return false;
      } else {
         Set<String> cmpKeys = cmp.getKeys();
         if (!cmpKeys.equals(this.getKeys())) {
            return false;
         }

         for (String tmp : cmpKeys) {
            if (!cmp.get(tmp).equals(this.get(tmp))) {
               return false;
            }
         }

         return true;
      }
   }

   public int size() {
      int count = 0;

      for (String tmp : this.getKeys()) {
         count += this.get(tmp);
      }

      return count;
   }

   @Override
   public int hashCode() {
      int hc = 0;

      for (String tmp : this.list) {
         hc += tmp.hashCode();
      }

      return hc;
   }
}
