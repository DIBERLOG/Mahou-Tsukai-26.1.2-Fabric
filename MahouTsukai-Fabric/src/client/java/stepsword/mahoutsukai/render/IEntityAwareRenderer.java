package stepsword.mahoutsukai.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IEntityAwareRenderer {
   void render(LivingEntity var1, ItemStack var2, ItemDisplayContext var3, PoseStack var4, MultiBufferSource var5, int var6, int var7);
}
