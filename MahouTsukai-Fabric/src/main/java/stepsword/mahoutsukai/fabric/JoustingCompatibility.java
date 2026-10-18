package stepsword.mahoutsukai.fabric;
import java.util.*;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.jousting.item.ItemLance;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.lance.LanceMahou;
import stepsword.mahoutsukai.effects.projection.PowerConsolidationSpellEffect;
import stepsword.mahoutsukai.entity.WeaponProjectileEntity;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.lance.Rhongomyniad;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.*;
public final class JoustingCompatibility {
 public static boolean loaded(){return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("jousting");}
 public static boolean isLance(net.minecraft.world.item.Item item){return loaded() && item instanceof ItemLance;}
 public static float rotation(net.minecraft.world.item.Item item){return isLance(item)?item instanceof Rhongomyniad?-180f:-90f:0;}
   public static void powerConsolidation(net.minecraft.server.level.ServerLevel world, ItemEntity item, List<ItemEntity> toDelete, List<WeaponProjectileEntity> toSpawn, int manaCost) {
      if (item.getItem().getItem() instanceof ItemLance && PowerConsolidationSpellEffect.isItemAllowed(item.getItem(), world)) {
         boolean inMurkyWater = Utils.isInMurkyWater(item);
         boolean isAllowed = PowerConsolidationSpellEffect.isItemAllowed(item.getItem(), world);
         boolean isEnchanted = item.getItem().isEnchanted();
         boolean hasOwner = item.getOwner() instanceof Player;
         double rhongomyniadNerf = MTConfig.RHONGOMYNIAD_NERF_FACTOR;
         if (!hasOwner && inMurkyWater) {
            PlayerHelp.sendHelpMessageNearby(item.blockPosition(), item.level(), PlayerHelp.Message.RHONGOMYNIAD_NO_OWNER);
         }

         if (hasOwner && inMurkyWater) {
            Player player = (Player)item.getOwner();
            if (isAllowed) {
               if (isEnchanted) {
                  ConcurrentSet<BlockPos> connected = new ConcurrentSet();
                  boolean lt = PowerConsolidationSpellEffect.lakeThreshhold(item.blockPosition(), connected, world);
                  if (lt) {
                     if (PlayerManaManager.hasMana(player, manaCost)) {
                        BlockPos p = PowerConsolidationSpellEffect.findCenter(connected);
                        if (!Utils.isBlockAir(world, p)) {
                           p = p.below();
                        }

                        ItemStack stack = new ItemStack(ModItems.REGISTERED.get("rhongomyniad"));
                        LanceMahou mahou = Utils.getLanceMahou(stack);
                        float damage = Rhongomyniad.simulateHit(item.getItem(), world);
                        ItemStack stackCopy = item.getItem().copy();
                        net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(stackCopy,enchantments->enchantments.removeIf(e->true));
                        float damageWithoutEnchant = Rhongomyniad.simulateHit(stackCopy, world);
                        damage = (float)(damage - rhongomyniadNerf * (damage - damageWithoutEnchant));
                        mahou.setAttackDamage(--damage);
                        mahou.setWeight((float)(((ItemLance)item.getItem().getItem()).getWeight(item.getItem()) - 0.02F));
                        Utils.setLanceMahou(stack, mahou);
                        Utils.debug("Advancement Time");
                        WeaponProjectileEntity wpe = new WeaponProjectileEntity(world, p.getX(), p.getY(), p.getZ(), stack);
                        wpe.setDeltaMovement(0.0, -1.0, 0.0);
                        wpe.setOwner(player);
                        toSpawn.add(wpe);
                        toDelete.add(item);
                     } else {
                        PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.NOT_ENOUGH_MANA);
                     }
                  } else {
                     PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.CALIBURN_LAKE_TOO_SMALL);
                  }
               } else {
                  PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.RHONGOMYNIAD_NOT_ENCHANTED);
               }
            } else {
               PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.RHONGOMYNIAD_LANCE_BANNED);
            }
         }
      }
   }

}
