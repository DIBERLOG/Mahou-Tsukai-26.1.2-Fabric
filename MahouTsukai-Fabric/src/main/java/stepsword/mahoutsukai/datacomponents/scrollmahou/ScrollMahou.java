package stepsword.mahoutsukai.datacomponents.scrollmahou;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class ScrollMahou implements CopyComponent<ScrollMahou> {
   private Optional<UUID> casterUUID = Optional.empty();
   private Optional<CompoundTag> spellStorage = Optional.empty();
   private Optional<String> caster = Optional.empty();

   public ScrollMahou() {
   }

   public ScrollMahou(Optional<UUID> cu, Optional<CompoundTag> s, Optional<String> c) {
      this.casterUUID = cu;
      this.spellStorage = s;
      this.caster = c;
   }

   public Optional<UUID> getCasterUUID() {
      return this.casterUUID;
   }

   public void setCasterUUID(UUID casterUUID) {
      this.casterUUID = Optional.of(casterUUID);
   }

   public Optional<String> getCasterName() {
      return this.caster;
   }

   public void setCasterName(String caster) {
      this.caster = Optional.of(caster);
   }

   public Optional<CompoundTag> getSpellStorage() {
      return this.spellStorage;
   }

   public void setSpellStorage(CompoundTag spellStorage) {
      this.spellStorage = Optional.of(spellStorage);
   }

   public ScrollMahou copy() {
      return new ScrollMahou(this.casterUUID, this.spellStorage.isEmpty() ? Optional.empty() : Optional.of(this.spellStorage.get().copy()), this.caster);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.caster, this.casterUUID, this.spellStorage);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof ScrollMahou b)
         ? false
         : (b.casterUUID.isEmpty() && this.casterUUID.isEmpty() || b.casterUUID.equals(this.casterUUID))
            && (b.caster.isEmpty() && this.caster.isEmpty() || b.caster.equals(this.caster))
            && (b.spellStorage.isEmpty() && this.spellStorage.isEmpty() || b.spellStorage.equals(this.spellStorage));
   }
}
