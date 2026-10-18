package stepsword.mahoutsukai.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.networking.InsightPacket;
import stepsword.mahoutsukai.networking.PacketHandler;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class InsightEyesPotion extends EyesPotion {
   public static Map<Player, Boolean> hadTargets = new HashMap<>();

   protected InsightEyesPotion() {
      super(ModEffects.getColorNumber(204, 204, 140));
   }

   public static void insightEyesPlayerTick(Player player) {
      InsightPacket packet=inspect(player);
      if(packet!=null && player instanceof ServerPlayer serverPlayer)PacketHandler.sendTo(serverPlayer,packet);
   }
   public static InsightPacket inspect(Player player) {
      if (!player.level().isClientSide() && EffectUtil.hasBuff(player, ModEffects.INSIGHT)) {
         Entity e = Utils.selectEntityNearCursor(
            player,
            MTConfig.GLOBAL_LOOK_RANGE,
            player.level(),
            a -> Utils.getPredicate(new java.util.HashSet<>(java.util.Set.of(Utils.MAHOU_SELECTION.INSIGHT)), a, player),
            false
         );
         HitResult result = Utils.rayTraceEyes(player, MTConfig.GLOBAL_LOOK_RANGE);
         if (e != null && result instanceof BlockHitResult) {
            double d1 = player.position().distanceToSqr(e.position());
            double d2 = player.position().distanceToSqr(EffectUtil.fromBlockPos(((BlockHitResult)result).getBlockPos()));
            if (!(d1 > d2) && e instanceof LivingEntity) {
               result = null;
            } else {
               e = null;
            }
         }

         if (e != null && e instanceof LivingEntity) {
            java.util.List<ItemStack> inventory = entityInventory((LivingEntity)e);
            ItemStack s = ((LivingEntity)e).getMainHandItem();
            int indexmain = -1;
            int r = 0;
            if (inventory != null) {
               for (int z = 0; z < inventory.size(); z++) {
                  if (!inventory.get(z).isEmpty()) {
                     if (ItemStack.matches(inventory.get(z),s)) {
                        indexmain = r;
                     }

                     r++;
                  }
               }
            }

            hadTargets.put(player,true);
            return new InsightPacket(
                  writeInventory(inventory, player.level()),
                  writeEffects(((LivingEntity)e).getActiveEffects(), player.level()),
                  writeItem(s, player.level()),
                  true,
                  false,
                  e.getId(),
                  indexmain
               );
         }

         if (result instanceof BlockHitResult) {
            BlockPos pos = ((BlockHitResult)result).getBlockPos();
            java.util.List<ItemStack> inventory = blockInventory(player.level(),pos);
            if (inventory != null) {
               hadTargets.put(player,true);
            return new InsightPacket(
                     writeInventory(inventory, player.level()),
                     writeEffects(null, player.level()),
                     writeItem(ItemStack.EMPTY, player.level()),
                     true,
                     true,
                     -1,
                     -1
                  );
            }
         }
      }

      if (hadTargets.containsKey(player) && hadTargets.get(player)) {
         hadTargets.remove(player);
         return new InsightPacket(
               writeInventory(null, player.level()), writeEffects(null, player.level()), writeItem(ItemStack.EMPTY, player.level()), false, false, -1, -1
            );
      }
      return null;
   }

   public static java.util.List<ItemStack> entityInventory(LivingEntity entity) {
      net.minecraft.world.Container container=entity instanceof Player p ? p.getInventory() : entity instanceof net.minecraft.world.entity.npc.InventoryCarrier carrier ? carrier.getInventory() : entity instanceof net.minecraft.world.Container c ? c : null;
      var result=new ArrayList<ItemStack>();
      if(container!=null)for(int i=0;i<container.getContainerSize();i++)result.add(container.getItem(i).copy());
      else for(var slot:net.minecraft.world.entity.EquipmentSlot.VALUES)result.add(entity.getItemBySlot(slot).copy());
      return result;
   }
   public static java.util.List<ItemStack> blockInventory(Level world,BlockPos pos) {
      var storage=net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(world,pos,net.minecraft.core.Direction.NORTH);
      if(storage==null)return null;
      var result=new ArrayList<ItemStack>();
      for(var view:storage){if(result.size()>=54)break;result.add(view.isResourceBlank()?ItemStack.EMPTY:view.getResource().toStack((int)Math.min(Integer.MAX_VALUE,view.getAmount())));}
      return result;
   }
   public static CompoundTag writeInventory(java.util.List<ItemStack> inventory, Level level) {
      CompoundTag list = new CompoundTag();
      RegistryOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      int m = 0;
      if (inventory != null) {
         for (int i = 0; i < Math.min(54, inventory.size()); i++) {
            ItemStack resource = inventory.get(i);
            if (!resource.isEmpty()) {
               ItemStack stack = resource.copy();
               list.store("index" + m++, ItemStack.CODEC, ops, stack);
            }
         }
      }

      return list;
   }

   public static ArrayList<ItemStack> readInventory(CompoundTag nbt, Level level) {
      ArrayList<ItemStack> lst = new ArrayList<>();
      RegistryOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      int i = 0;

      while (nbt.contains("index" + i)) {
         Optional<ItemStack> stack = nbt.read("index" + i++, ItemStack.CODEC, ops);
         stack.filter(s -> !s.isEmpty()).ifPresent(lst::add);
      }

      return lst;
   }

   public static CompoundTag writeItem(ItemStack stack, Level level) {
      CompoundTag tag = new CompoundTag();
      if (stack != null && !stack.isEmpty()) {
         RegistryOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
         tag.store("stack", ItemStack.CODEC, ops, stack);
      }

      return tag;
   }

   public static ItemStack readItem(CompoundTag nbt, Level level) {
      RegistryOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      return nbt.read("stack", ItemStack.CODEC, ops).orElse(ItemStack.EMPTY);
   }

   public static CompoundTag writeEffects(Collection<MobEffectInstance> es, Level level) {
      CompoundTag ret = new CompoundTag();
      RegistryOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      if (es != null) {
         ArrayList<MobEffectInstance> effects = new ArrayList<>(es);
         int m = 0;

         for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).getDuration() > 0) {
               ret.store("effect" + m++, MobEffectInstance.CODEC, ops, effects.get(i));
            }
         }
      }

      return ret;
   }

   public static ArrayList<MobEffectInstance> readEffects(CompoundTag nbt, Level level) {
      ArrayList<MobEffectInstance> lst = new ArrayList<>();
      RegistryOps<Tag> ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      int i = 0;

      while (nbt.contains("effect" + i)) {
         nbt.read("effect" + i++, MobEffectInstance.CODEC, ops).ifPresent(lst::add);
      }

      return lst;
   }
}
