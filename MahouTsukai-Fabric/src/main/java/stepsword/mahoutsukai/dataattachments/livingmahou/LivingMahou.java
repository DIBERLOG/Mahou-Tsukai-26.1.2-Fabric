package stepsword.mahoutsukai.dataattachments.livingmahou;

import java.util.UUID;
import net.minecraft.world.phys.Vec3;

public class LivingMahou implements ILivingMahou {
   private int kodoku = 0;
   private boolean authorityHit = false;
   private UUID fearPerson = null;
   private Vec3 lastWoundedPos = null;
   private int acting = 0;
   private Vec3 actingLoc = null;
   private String immunityDamageType = null;

   @Override
   public boolean getAuthorityHit() {
      return this.authorityHit;
   }

   @Override
   public void setAuthorityHit(boolean b) {
      this.authorityHit = b;
   }

   @Override
   public int getKodoku() {
      return this.kodoku;
   }

   @Override
   public void setKodoku(int u) {
      this.kodoku = u;
   }

   @Override
   public UUID getFearPerson() {
      return this.fearPerson;
   }

   @Override
   public void setFearPerson(UUID uuid) {
      this.fearPerson = uuid;
   }

   @Override
   public Vec3 getLastWoundedPos() {
      return this.lastWoundedPos;
   }

   @Override
   public void setLastWoundedPos(Vec3 v) {
      this.lastWoundedPos = v;
   }

   @Override
   public void setActing(int a) {
      this.acting = a;
   }

   @Override
   public int getActing() {
      return this.acting;
   }

   @Override
   public void setActingLoc(Vec3 v) {
      this.actingLoc = v;
   }

   @Override
   public Vec3 getActingLoc() {
      return this.actingLoc;
   }

   @Override
   public String getImmunityDamageType() {
      return this.immunityDamageType;
   }

   @Override
   public void setImmunityDamageType(String damageType) {
      this.immunityDamageType = damageType;
   }

   @Override
   public void copyMahou(ILivingMahou m) {
      if (m != null) {
         this.setFearPerson(m.getFearPerson());
         this.setKodoku(m.getKodoku());
         this.setActing(m.getActing());
         this.setActingLoc(m.getActingLoc());
         this.setImmunityDamageType(m.getImmunityDamageType());
      }
   }

   @Override
   public void clearBuffs() {
      this.setFearPerson(null);
      this.setImmunityDamageType(null);
   }
}
