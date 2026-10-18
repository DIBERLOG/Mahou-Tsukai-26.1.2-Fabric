package stepsword.mahoutsukai.client;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
public final class ItemUseRenderBridge {
 private record Context(ItemStack stack,ItemDisplayContext display,float ticks){}
 private static final ThreadLocal<java.util.ArrayDeque<Context>> contexts=ThreadLocal.withInitial(java.util.ArrayDeque::new);
 public static void push(LivingEntity entity,ItemStack stack,ItemDisplayContext display){contexts.get().push(new Context(stack,display,entity!=null && entity.isUsingItem() && ItemStack.matches(entity.getUseItem(),stack)?entity.getTicksUsingItem():0));}
 public static void popContext(){var values=contexts.get();values.pop();if(values.isEmpty())contexts.remove();}
 public static float getUseTicks(ItemStack stack){var values=contexts.get();return values.isEmpty()?0:values.peek().ticks();}
 public static ItemDisplayContext getDisplayContext(ItemStack stack){var values=contexts.get();return values.isEmpty()?ItemDisplayContext.NONE:values.peek().display();}
}
