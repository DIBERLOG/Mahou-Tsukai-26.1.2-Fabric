package stepsword.mahoutsukai.dataattachments.settingsmahou;

import java.util.List;
import java.util.Set;

public interface ISettingsMahou {
   List<Integer> getIntSettings(int var1);

   void setIntSettings(int var1, List<Integer> var2);

   Set<Integer> getIntSettingsKeySet();

   void copyMahou(ISettingsMahou var1);
}
