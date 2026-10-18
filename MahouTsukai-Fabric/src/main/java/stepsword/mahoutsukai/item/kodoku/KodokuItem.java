package stepsword.mahoutsukai.item.kodoku;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou;
import stepsword.mahoutsukai.entity.kodoku.KodokuEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class KodokuItem extends ItemBase {
   public KodokuItem() {
      super(new Properties().component(DataComponents.KODOKU_COMPONENT, new KodokuMahou(0)));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, context, display, tooltip, flagIn);
      Item item = stack.getItem();
      if (item instanceof KodokuItem) {
         KodokuMahou kodokuMahou = Utils.getKodokuMahou(stack);
         if (kodokuMahou != null) {
            String kodoku = kodokuMahou.getKodoku() + "";
            tooltip.accept(Component.literal(Component.translatable("mahoutsukai.kodoku").getString() + ": " + kodoku));
         }
      }
   }

   public InteractionResult useOn(UseOnContext context) {
      Level worldIn = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Player player = context.getPlayer();
      InteractionHand hand = context.getHand();
      Direction facing = context.getClickedFace();
      if (!worldIn.isClientSide()) {
         setKodoku(player.getMainHandItem(), getKodoku(player.getMainHandItem()));
         KodokuEntity entity = new KodokuEntity(worldIn);
         Vec3 place = EffectUtil.fromBlockPos(pos.relative(facing));
         entity.snapTo(
            new Vec3(place.x + 0.5 + Math.random() * 0.3, place.y, place.z + 0.5 + Math.random() * 0.3), (float)(Math.random() * 360.0), 0.0F
         );
         entity.kodoku = getKodoku(player.getItemInHand(hand));
         worldIn.addFreshEntity(entity);
         player.getItemInHand(hand).shrink(1);
      }

      return InteractionResult.SUCCESS;
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
      if (!target.level().isClientSide()) {
         int kodoku = getKodoku(playerIn.getItemInHand(hand));
         boolean hasworm = false;

         for (int i = 0; i < target.getPassengers().size(); i++) {
            if (target.getPassengers().get(i) instanceof KodokuEntity) {
               hasworm = true;
            }
         }

         if (kodoku <= 0 && !(target instanceof KodokuEntity) && !hasworm) {
            KodokuEntity entity = new KodokuEntity(target.level());
            entity.snapTo(
               new Vec3(target.getX() - target.getDimensions(Pose.STANDING).width() / 2.0F, target.getY() + target.getEyeHeight(), target.getZ()),
               target.getYRot(),
               target.getXRot() + 90.0F
            );
            target.level().addFreshEntity(entity);
            entity.startRiding(target, true, true);
            playerIn.getItemInHand(hand).shrink(1);
         }
      }

      return InteractionResult.SUCCESS;
   }

   public static int getKodoku(ItemStack stack) {
      KodokuMahou mahou = Utils.getKodokuMahou(stack);
      return mahou.getKodoku();
   }

   public static void setKodoku(ItemStack stack, int k) {
      KodokuMahou mahou = Utils.getKodokuMahou(stack);
      mahou.setKodoku(k);
      Utils.setKodokuMahou(stack, mahou);
   }
}
