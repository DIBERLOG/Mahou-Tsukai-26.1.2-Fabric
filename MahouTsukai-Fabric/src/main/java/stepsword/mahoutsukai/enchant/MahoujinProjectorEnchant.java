package stepsword.mahoutsukai.enchant;

import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant;
import stepsword.mahoutsukai.networking.ProjectorRightClickPacket;
import stepsword.mahoutsukai.render.enchant.ProjectorValues;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;
import stepsword.mahoutsukai.util.Utils;

public class MahoujinProjectorEnchant {
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   public static String tag = "mahoujin_projector";

   public static List<ProjectorValues> getProjectorValues(ItemStack stack) {
      ProjectorEnchant e = Utils.getProjectorEnchant(stack);
      return e != null && !e.getPv().isEmpty() ? e.getPv() : null;
   }

   public static void setProjectorNBT(ItemStack stack, MahoujinProjectorTileEntity mte, ProjectorRightClickPacket.ARMOR_ACTION action) {
      if (mte != null) {
         ProjectorEnchant e = Utils.getProjectorEnchant(stack);
         if (e == null) {
            e = new ProjectorEnchant();
         } else {
            e = e.copy();
         }

         if ((action == ProjectorRightClickPacket.ARMOR_ACTION.REMOVE || action == ProjectorRightClickPacket.ARMOR_ACTION.REPLACE) && !e.getPv().isEmpty()) {
            e.getPv().removeLast();
         }

         if (action == ProjectorRightClickPacket.ARMOR_ACTION.REPLACE || action == ProjectorRightClickPacket.ARMOR_ACTION.ADD) {
            if(e.getPv().size()>=64)return;
            e.getPv().addLast(ProjectorValues.readFromTag(ProjectorValues.writeToTag(mte.pv)));
         }

         Utils.setProjectorEnchant(stack, e);
      }
   }
}
