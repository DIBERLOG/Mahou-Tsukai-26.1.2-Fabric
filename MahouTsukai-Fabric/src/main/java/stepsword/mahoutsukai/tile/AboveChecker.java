package stepsword.mahoutsukai.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public interface AboveChecker {
   default List<Item> checkForItem() {
      return new ArrayList<>();
   }

   default List<Class<? extends Item>> checkForItemClass() {
      return new ArrayList<>();
   }

   default boolean allowedItem(Item item) {
      for (Class<? extends Item> c : this.checkForItemClass()) {
         if (c.isAssignableFrom(item.getClass())) {
            return true;
         }
      }

      return false;
   }

   default Class<? extends Entity> checkForEntity() {
      return Entity.class;
   }

   default int checkTicks() {
      return 10;
   }

   default boolean firstEntityOnly() {
      return false;
   }

   default boolean preConditions() {
      return true;
   }

   default List<? extends Entity> checkAbove(BlockEntity be, boolean checkOnly) {
      BlockPos worldPosition = be.getBlockPos();
      double x = worldPosition.getX();
      double y = worldPosition.getY();
      double z = worldPosition.getZ();
      List<? extends Entity> lst = be.getLevel().getEntitiesOfClass(this.checkForEntity(), new AABB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
      if (!checkOnly) {
         HashSet<Item> items = new HashSet<>(this.checkForItem());
         if (lst.isEmpty()) {
            this.noEntityFoundAbove();
         }

         for (Entity e : lst) {
            if (e instanceof ItemEntity ie && (items.contains(ie.getItem().getItem()) || this.allowedItem(ie.getItem().getItem())) && this.ifItemFoundAbove(ie)
               || this.ifEntityFoundAbove(e)
               || this.firstEntityOnly()) {
               break;
            }
         }
      }

      return lst;
   }

   default void noEntityFoundAbove() {
   }

   default List<? extends Entity> checkAbove(BlockEntity be) {
      return this.checkAbove(be, false);
   }

   default boolean ifItemFoundAbove(ItemEntity item) {
      return false;
   }

   default boolean ifEntityFoundAbove(Entity e) {
      return false;
   }

   default void tickAC(Level level, BlockEntity be) {
      if (level.getGameTime() % this.checkTicks() == 0L && this.preConditions()) {
         this.checkAbove(be);
      }
   }
}
