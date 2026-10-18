package stepsword.mahoutsukai.datacomponents.replica;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.datacomponents.CopyComponent;

public class ReplicaMahou implements CopyComponent<ReplicaMahou> {
   Optional<Vec3> loc = Optional.empty();
   Optional<String> dim = Optional.empty();
   Boolean friendly = true;
   int slot = 0;
   ArrayList<String> types = new ArrayList<>();

   public ReplicaMahou() {
   }

   public ReplicaMahou(Optional<Vec3> l, Optional<String> d, Boolean f, int s, List<String> t) {
      this.loc = l;
      this.dim = d;
      this.friendly = f;
      this.slot = s;
      this.types = new ArrayList<>(t);
   }

   public ArrayList<String> getTypes() {
      return this.types;
   }

   public Boolean getFriendly() {
      return this.friendly;
   }

   public int getSlot() {
      return this.slot;
   }

   public Optional<String> getDim() {
      return this.dim;
   }

   public Optional<Vec3> getLoc() {
      return this.loc;
   }

   public void setDim(String dim) {
      this.dim = Optional.of(dim);
   }

   public void setFriendly(Boolean friendly) {
      this.friendly = friendly;
   }

   public void setLoc(Vec3 loc) {
      this.loc = Optional.of(loc);
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public void setTypes(ArrayList<String> types) {
      this.types = types;
   }

   public ReplicaMahou copy() {
      ArrayList<String> ts = new ArrayList<>();
      if (this.types != null) {
         ts = new ArrayList<>(this.types);
      }

      return new ReplicaMahou(this.loc, this.dim, this.friendly, this.slot, ts);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.loc, this.dim, this.friendly, this.slot, this.types);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof ReplicaMahou b)
         ? false
         : (b.loc.isEmpty() && this.loc.isEmpty() || b.loc.equals(this.loc))
            && b.friendly == this.friendly
            && (this.dim.isEmpty() && b.dim.isEmpty() || this.dim.equals(b.dim))
            && this.slot == b.slot
            && (this.types == b.types || this.types.equals(b.types));
   }
}
