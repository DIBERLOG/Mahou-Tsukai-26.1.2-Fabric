package stepsword.mahoutsukai.client.itemmodel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import stepsword.mahoutsukai.item.mysticcode.MysticCode;

public record SlotSelectProperty() implements SelectItemModelProperty<Integer> {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mahoutsukai", "slot");
   public static final MapCodec<SlotSelectProperty> MAP_CODEC = MapCodec.unit(new SlotSelectProperty());
   public static final Codec<Integer> VALUE_CODEC = Codec.INT;
   public static final Type<SlotSelectProperty, Integer> TYPE = Type.create(MAP_CODEC, VALUE_CODEC);

   public @Nullable Integer get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
      return itemStack.getItem() instanceof MysticCode ? MysticCode.getSlot(itemStack) : null;
   }

   public Codec<Integer> valueCodec() {
      return VALUE_CODEC;
   }

   public Type<SlotSelectProperty, Integer> type() {
      return TYPE;
   }
}
