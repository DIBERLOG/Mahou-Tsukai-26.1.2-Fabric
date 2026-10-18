package stepsword.mahoutsukai.item.lance;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import stepsword.jousting.item.ItemLance;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.lance.LanceMahou;
import stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer;
import stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity;
import stepsword.mahoutsukai.item.caliburn.Caliburn;
import stepsword.mahoutsukai.util.FakeSkeleton;
import stepsword.mahoutsukai.util.Utils;

public class Rhongomyniad extends ItemLance {
   public static final Identifier speedlance = Identifier.fromNamespaceAndPath("mahoutsukai", "rhongomyniad_speed");
   public static final String MT_ATTACK_DAMAGE = "mahoutsukai_rhongomyniad_attack";
   public static final String MT_WEIGHT = "mahoutsukai_rhongomyniad_weight";

   public Rhongomyniad() {
      super(() -> MTConfig.POWER_CONSOLIDATION_DURABILITY, () -> 10.0, () -> 2.0);
   }

   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      if (player.isDiscrete() && hand == InteractionHand.MAIN_HAND) {
         if (!world.isClientSide()) {
            BlockHitResult result = getPlayerPOVHitResult(world, player, Fluid.SOURCE_ONLY);
            ItemStack placedStack = player.getMainHandItem().copy();
            RhongomyniadEntity placed = new RhongomyniadEntity(world, player, 1.0F, placedStack);
            BlockPos position = result.getBlockPos().relative(result.getDirection());
            placed.setPos(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
            world.addFreshEntity(placed);
            player.getMainHandItem().shrink(1);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.use(world, player, hand);
      }
   }

   public int getMaxDamage(ItemStack stack) {
      return MTConfig.POWER_CONSOLIDATION_DURABILITY;
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      if (stack.getItem() instanceof Rhongomyniad && player.getVehicle() != null && player.getVehicle() instanceof LivingEntity) {
         LivingEntity riding = (LivingEntity)player.getVehicle();
         if (!player.level()
            .getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(MTConfig.RHONGOMYNIAD_RANGE), b -> Caliburn.specialTarget(b))
            .isEmpty()) {
            addspeed(riding, true);
         }
      }

      super.onUseTick(level, player, stack, count);
   }

   public static float simulateHit(ItemStack s, Level world) {
      float ret = 3.0F;
      int health = 5000000;
      if (s != null && world instanceof ServerLevel serverLevel) {
         ItemStack copy = s.copy();
         FakeSkeleton target = new FakeSkeleton(world);
         target.h = health;
         Player fakePlayer = SafeFakePlayer.createFakePlayerIfNull(serverLevel);
         Caliburn.equipForSimulation(copy, fakePlayer);
         fakePlayer.attackStrengthTicker = 1000;
         ((ItemLance)copy.getItem()).attack(fakePlayer, target);
         ret = health - target.h;
      }

      return ret;
   }

   public static void addspeed(LivingEntity living, boolean add) {
      AttributeModifier a = new AttributeModifier(speedlance, MTConfig.RHONGOMYNIAD_SPEED_FACTOR, Operation.ADD_MULTIPLIED_TOTAL);
      AttributeInstance attr = living.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attr != null) {
         boolean hasSpeed = attr.hasModifier(speedlance);
         if (add) {
            if (!hasSpeed) {
               attr.addTransientModifier(a);
            }
         } else if (hasSpeed) {
            attr.removeModifier(a);
         }
      }
   }

   public boolean releaseUsing(ItemStack stack, Level world, LivingEntity player, int timeLeft) {
      if (player.getVehicle() instanceof LivingEntity mount) {
         addspeed(mount, false);
      }

      return super.releaseUsing(stack, world, player, timeLeft);
   }

   public double getAttackDamage(ItemStack stack) {
      LanceMahou mahou = Utils.getLanceMahou(stack);
      return mahou == null ? 1.0 : 1.0F + mahou.getAttackDamage();
   }

   public double getWeight(ItemStack stack) {
      LanceMahou mahou = Utils.getLanceMahou(stack);
      return mahou == null ? 2.0 : mahou.getWeight();
   }
}
