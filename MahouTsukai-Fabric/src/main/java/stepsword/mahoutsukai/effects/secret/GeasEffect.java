package stepsword.mahoutsukai.effects.secret;

import java.util.HashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.util.Utils;

public class GeasEffect {
   public static String BREAK = "==MAHOU==";

   public static boolean geasEffect(Player caster) {
      Entity e = Utils.selectGeasTarget(caster);
      if (e instanceof LivingEntity) {
         return addGeas(caster, (LivingEntity)e);
      }

      if (e instanceof ItemEntity) {
         return addGeas(caster, (ItemEntity)e);
      }

      MutableComponent tt = Component.translatable("mahoutsukai.geas.geasa");
      tt.append(Component.literal(" " + getGeasString(caster)));
      if (caster instanceof ServerPlayer) {
         caster.sendSystemMessage(Component.literal(ChatFormatting.AQUA + tt.getString()));
      }

      return false;
   }

   public static boolean geasItem(Player player, ItemEntity item) {
      boolean ret = hasGeas(player, item.getItem());
      if (ret) {
         MandatoryFun.completeFunReq(player, MandatoryFun.FUN_REQS.GEAS_PICKUP_CANCEL);
      }

      return ret;
   }

   public static boolean geasLivingHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource source) {
      return source.getEntity() instanceof Player player && hasGeas(player,target);
   }

   public static boolean addGeas(Player player, LivingEntity target) {
      boolean r = true;
      String geas;
      String message;
      if (target instanceof Player) {
         geas = target.getDisplayName().getString() + BREAK + "minecraft:player";
         message = target.getDisplayName().getString();
      } else {
         geas = Utils.getRegistryName(target);
         message = target.getDisplayName().getString();
         if (target.getCustomName() != null && !player.isDiscrete()) {
            message = target.getCustomName().getString();
            geas = target.getCustomName().getString() + BREAK + geas;
         }
      }

      return add(player, message, ChatFormatting.stripFormatting(geas));
   }

   public static boolean add(Player player, String message, String geas) {
      boolean r = true;
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         HashSet<String> geasList = mahou.getGeas();
         if (geasList == null) {
            geasList = new HashSet<>();
            mahou.setGeas(geasList);
         }

         String added = "mahoutsukai.geas.added";
         if (geasList.contains(geas)) {
            added = "mahoutsukai.geas.removed";
            geasList.remove(geas);
            r = false;
         } else {
            geasList.add(geas);
         }

         MutableComponent tt = Component.translatable(added);
         tt.append(Component.literal(" " + message));
         if (player instanceof ServerPlayer sp) {
            sp.sendOverlayMessage(tt);
         }
      }

      return r;
   }

   public static boolean addGeas(Player player, ItemEntity item) {
      boolean r = true;
      String s = "Item:" + item.getItem().getItem().getName(item.getItem()).getString();
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         HashSet<String> geasList = mahou.getGeas();
         if (geasList == null) {
            geasList = new HashSet<>();
            mahou.setGeas(geasList);
         }

         r = add(player, s, ChatFormatting.stripFormatting(s));
      }

      return r;
   }

   public static boolean hasGeas(Player player, ItemStack item) {
      String s = "Item:" + item.getItem().getName(item).getString();
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         HashSet<String> geasList = mahou.getGeas();
         if (geasList == null) {
            geasList = new HashSet<>();
            mahou.setGeas(geasList);
         }

         for (String g : geasList) {
            if (g.equals(s)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasGeas(Player player, LivingEntity target) {
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         HashSet<String> geasList = mahou.getGeas();
         if (geasList == null) {
            geasList = new HashSet<>();
            mahou.setGeas(geasList);
         }

         String nameDisplay = "";
         if (target.getCustomName() != null) {
            nameDisplay = target.getCustomName().getString();
         }

         if (target instanceof Player) {
            nameDisplay = target.getDisplayName().getString();
         }

         String nameType = Utils.getRegistryName(target);

         for (String g : geasList) {
            if (g.contains(BREAK)) {
               String[] lst = g.split(BREAK);
               if (lst.length >= 2 && lst[0].equals(nameDisplay) && lst[1].equals(nameType)) {
                  return true;
               }
            } else if (nameType.equals(g)) {
               return true;
            }
         }
      }

      return false;
   }

   public static String getGeasString(Player player) {
      String geasa = "";
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         HashSet<String> geasList = mahou.getGeas();
         if (geasList == null) {
            geasList = new HashSet<>();
            mahou.setGeas(geasList);
         }

         for (String g : geasList) {
            String[] lst = g.split(BREAK);
            if (!geasa.equals("")) {
               geasa = geasa + ", ";
            }

            if (lst.length > 1) {
               geasa = geasa + regToName(lst[1]);
               geasa = geasa + " - " + lst[0];
            } else {
               geasa = geasa + regToName(lst[0]);
            }
         }
      }

      return geasa;
   }

   public static int getUniqueGeasCount(Player player) {
      int geasa = 0;
      IMahou mahou = Utils.getPlayerMahou(player);
      if (mahou != null) {
         HashSet<String> geasList = mahou.getGeas();
         if (geasList == null) {
            geasList = new HashSet<>();
            mahou.setGeas(geasList);
         }

         for (String g : geasList) {
            String[] lst = g.split(BREAK);
            if (lst.length == 1 && !lst[0].startsWith("Item:")) {
               geasa++;
            }
         }
      }

      return geasa;
   }

   public static String regToName(String res) {
      if (!res.startsWith("Item:")) {
         Identifier loc = Identifier.parse(res);
         Reference<EntityType<?>> type = (Reference<EntityType<?>>)BuiltInRegistries.ENTITY_TYPE.get(loc).orElse(null);
         return type != null ? ((EntityType)type.value()).getDescription().getString() : res;
      } else {
         return res;
      }
   }
}
