package stepsword.mahoutsukai.dataattachments.livingmahou;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class LivingMahouStorage {
   private static final String AUTHORITY_HIT = "authority_hit";
   private static final String FEAR_PERSON = "fear_person";
   private static final String WOUNDED = "last_wounded_pos";
   private static final String KODOKU = "kodoku";
   private static final String ACTING = "acting";
   private static final String ACTING_LOC = "acting_loc";
   private static final String IMMUNITY_DAMAGE_TYPE = "immunity_damage_type";

   public static void read(ILivingMahou instance, ValueInput input) {
      instance.clearBuffs();
      instance.setKodoku(input.read("kodoku", Codec.INT).orElse(0));
      instance.setAuthorityHit(input.read("authority_hit", Codec.BOOL).orElse(false));
      instance.setActing(input.read("acting", Codec.INT).orElse(0));
      input.read("fear_person", UUIDUtil.CODEC).ifPresentOrElse(instance::setFearPerson, () -> instance.setFearPerson(null));
      input.read("last_wounded_pos", Vec3.CODEC).ifPresent(instance::setLastWoundedPos);
      input.read("acting_loc", Vec3.CODEC).ifPresent(instance::setActingLoc);
      input.read("immunity_damage_type", Codec.STRING).ifPresent(instance::setImmunityDamageType);
   }

   public static void write(ILivingMahou instance, ValueOutput output) {
      output.store("kodoku", Codec.INT, instance.getKodoku());
      output.store("authority_hit", Codec.BOOL, instance.getAuthorityHit());
      output.store("acting", Codec.INT, instance.getActing());
      if (instance.getFearPerson() != null) {
         output.store("fear_person", UUIDUtil.CODEC, instance.getFearPerson());
      }

      if (instance.getLastWoundedPos() != null) {
         output.store("last_wounded_pos", Vec3.CODEC, instance.getLastWoundedPos());
      }

      if (instance.getActingLoc() != null) {
         output.store("acting_loc", Vec3.CODEC, instance.getActingLoc());
      }

      if (instance.getImmunityDamageType() != null) {
         output.store("immunity_damage_type", Codec.STRING, instance.getImmunityDamageType());
      }
   }
}
