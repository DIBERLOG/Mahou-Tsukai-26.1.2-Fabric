package stepsword.mahoutsukai.dataattachments.settingsmahou;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsMahou implements ISettingsMahou {
   public ConcurrentHashMap<Integer, List<Integer>> intsettings = MahouSettings.getIntDefaults();

   @Override
   public void setIntSettings(int spell, List<Integer> lst) {
      this.intsettings.put(spell, new java.util.ArrayList<>(lst));
   }

   @Override
   public void copyMahou(ISettingsMahou mahou) {
      this.intsettings = MahouSettings.getIntDefaults();
      if (mahou != null) {
         for (Integer i : this.intsettings.keySet()) {
            this.setIntSettings(i, mahou.getIntSettings(i));
         }
      }
   }

   @Override
   public List<Integer> getIntSettings(int spell) {
      return this.intsettings.get(spell);
   }

   @Override
   public Set<Integer> getIntSettingsKeySet() {
      return this.intsettings.keySet();
   }
}
