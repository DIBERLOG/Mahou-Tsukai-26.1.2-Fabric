package stepsword.mahoutsukai.dataattachments.settingsmahou;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SettingsMahouStorage {
   public static void read(ISettingsMahou instance, ValueInput input) {
      for (SettingsMahouStorage.SettingsEntry entry : input.read("entries", SettingsMahouStorage.SettingsEntry.CODEC.listOf()).orElse(List.of())) {
         instance.setIntSettings(entry.key(), entry.values());
      }
   }

   public static void write(ISettingsMahou instance, ValueOutput output) {
      List<SettingsMahouStorage.SettingsEntry> entries = new ArrayList<>();

      for (Integer key : instance.getIntSettingsKeySet()) {
         try {
            List<Integer> values = instance.getIntSettings(key);
            entries.add(new SettingsMahouStorage.SettingsEntry(key, values));
         } catch (Exception var6) {
         }
      }

      output.store("entries", SettingsMahouStorage.SettingsEntry.CODEC.listOf(), entries);
   }

   public record SettingsEntry(int key, List<Integer> values) {
      public static final Codec<SettingsMahouStorage.SettingsEntry> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.INT.fieldOf("key").forGetter(SettingsMahouStorage.SettingsEntry::key),
               Codec.INT.listOf().fieldOf("values").forGetter(SettingsMahouStorage.SettingsEntry::values)
            )
            .apply(instance, SettingsMahouStorage.SettingsEntry::new)
      );
   }
}
