package stepsword.mahoutsukai.effects.familiar;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.entity.FamiliarEntity;
import stepsword.mahoutsukai.util.Utils;

public class SummonFamiliarSpellEffect {
   public static HashMap<UUID, FamiliarEntity> familiarMap = new HashMap<>();

   public static void summonFamiliar(Player caster) {
      if (!caster.level().isClientSide()) {
         FamiliarEntity familiar = new FamiliarEntity(caster.level());
         Vec3 casterPos = new Vec3(caster.getX(), caster.getY(), caster.getZ());
         casterPos = casterPos.add(0.0, caster.getEyeHeight(), 0.0);
         Vec3 familiarPos = casterPos.add(caster.getLookAngle().normalize().scale(1.0));
         familiar.setPos(familiarPos.x, familiarPos.y, familiarPos.z);
         familiar.setFamiliarId(UUID.randomUUID());
         familiar.tame(caster);
         if (familiarMap.containsKey(caster.getUUID())) {
            FamiliarEntity old = familiarMap.get(caster.getUUID());
            if (old != null && old.isAlive()) {
               old.discard();
            }
         }

         familiarMap.put(caster.getUUID(), familiar);
         caster.level().addFreshEntity(familiar);
      }
   }

   public static void familiarRightClick(Player player, net.minecraft.world.entity.Entity target, InteractionHand hand) {
      if (target instanceof FamiliarEntity && !target.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
         FamiliarEntity fe = (FamiliarEntity)target;
         if (fe.isOwnedBy(player)) {
            if (player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
               fe.setSearchBlock(Utils.getRegistryName(blockItem.getBlock(), player.level()));
            } else {
               fe.toggleHoliday();
               fe.setSearchBlock(null);
            }
         }
      }
   }
}
