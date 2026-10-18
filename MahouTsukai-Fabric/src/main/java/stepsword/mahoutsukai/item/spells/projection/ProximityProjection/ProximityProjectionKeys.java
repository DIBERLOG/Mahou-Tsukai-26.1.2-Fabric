package stepsword.mahoutsukai.item.spells.projection.ProximityProjection;

import com.google.common.base.Predicate;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.item.GrowSwordItem;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.MahouTsukaiTeleporter;

public class ProximityProjectionKeys extends ItemBase {
   public ProximityProjectionKeys() {
      super(
         "proximity_projection_keys",
         new Properties()
            .enchantable(ToolMaterial.IRON.enchantmentValue())
            .attributes(GrowSwordItem.createAttributes((int)(3.0F + ToolMaterial.IRON.attackDamageBonus()), -2.4F))
            .stacksTo(1)
            .durability(MTConfig.PROXIMITY_PROJECTION_DURABILITY)
      );
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return true;
   }

   public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
      return enchantment.value().isPrimaryItem(new ItemStack(Items.IRON_SWORD));
   }

   public float getDestroySpeed(ItemStack stack, BlockState state) {
      Block block = state.getBlock();
      return block == Blocks.COBWEB ? 15.0F : 1.0F;
   }

   public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      if (!attacker.level().isClientSide() && attacker instanceof Player) {
         BlockPos pos = target.blockPosition();
         attacker.stopRiding();
         ((ServerPlayer)attacker).connection.teleport(pos.getX(), pos.getY(), pos.getZ(), attacker.getYRot(), attacker.getXRot());
         MahouTsukaiTeleporter.teleport(attacker, pos.getX(), pos.getY(), pos.getZ(), EffectUtil.getDimension(attacker.level()));
      }
   }

   public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
      if (state.getDestroySpeed(worldIn, pos) != 0.0) {
         stack.hurtAndBreak(2, entityLiving, EquipmentSlot.MAINHAND);
      }

      return true;
   }

   public boolean isCombineRepairable(ItemStack stack) {
      return false;
   }

   public static Predicate<Entity> getProximityLookPredicate() {
      return new Predicate<Entity>() {
         public boolean apply(@Nullable Entity e) {
            return e instanceof LivingEntity;
         }
      };
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.PROXIMITY_PROJECTION_DURABILITY;
   }
}
