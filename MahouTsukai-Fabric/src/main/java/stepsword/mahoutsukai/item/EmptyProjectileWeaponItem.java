package stepsword.mahoutsukai.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmptyProjectileWeaponItem extends ProjectileWeaponItem {
   public EmptyProjectileWeaponItem(Properties pProperties) {
      super(ItemBase.withRegisteredId(pProperties));
   }

   @NotNull
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return a -> false;
   }

   public int getDefaultProjectileRange() {
      return 0;
   }

   protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {
   }

   @NotNull
   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return a -> false;
   }

   protected int getDurabilityUse(ItemStack pStack) {
      return 0;
   }

   @NotNull
   public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
      return ItemStack.EMPTY;
   }

   @NotNull
   public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
      return arrow;
   }

   @NotNull
   protected Projectile createProjectile(Level pLevel, LivingEntity pShooter, ItemStack pWeapon, ItemStack pAmmo, boolean pIsCrit) {
      return super.createProjectile(pLevel, pShooter, pWeapon, pAmmo, pIsCrit);
   }

   protected void shoot(
      ServerLevel p_346125_,
      LivingEntity p_330728_,
      InteractionHand p_331152_,
      ItemStack p_330646_,
      List<ItemStack> p_331726_,
      float p_331007_,
      float p_331445_,
      boolean p_331107_,
      @Nullable LivingEntity p_331167_
   ) {
   }
}
