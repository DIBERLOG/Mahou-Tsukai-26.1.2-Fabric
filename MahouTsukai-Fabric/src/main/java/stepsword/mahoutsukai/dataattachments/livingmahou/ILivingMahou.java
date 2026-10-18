package stepsword.mahoutsukai.dataattachments.livingmahou;

import java.util.UUID;
import net.minecraft.world.phys.Vec3;

public interface ILivingMahou {
   boolean getAuthorityHit();

   int getKodoku();

   void setKodoku(int var1);

   void setAuthorityHit(boolean var1);

   UUID getFearPerson();

   void setFearPerson(UUID var1);

   Vec3 getLastWoundedPos();

   void setLastWoundedPos(Vec3 var1);

   void setActing(int var1);

   int getActing();

   void setActingLoc(Vec3 var1);

   Vec3 getActingLoc();

   String getImmunityDamageType();

   void setImmunityDamageType(String var1);

   void copyMahou(ILivingMahou var1);

   void clearBuffs();
}
