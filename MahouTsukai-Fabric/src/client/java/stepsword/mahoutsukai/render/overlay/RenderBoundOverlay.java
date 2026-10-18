package stepsword.mahoutsukai.render.overlay;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.RenderUtils;
import stepsword.mahoutsukai.util.EffectUtil;

public class RenderBoundOverlay {
   public static void boundOverlay(GuiGraphicsExtractor graphics) {
      Entity e = Minecraft.getInstance().getCameraEntity();
      if (e instanceof LivingEntity && EffectUtil.hasBuff((LivingEntity)e, ModEffects.BOUND)) {
         Window sr = Minecraft.getInstance().getWindow();
         float alpha = RenderUtils.calculateOverlayFade(EffectUtil.getEffectDuration((LivingEntity)e, ModEffects.BOUND), 100, 20, 0.07F);
         if (alpha > 0.0F) {
            RenderUtils.renderTextureOverlay(
               graphics,
               "mahoutsukai:textures/mob_effect/bound.png",
               255,
               255,
               255,
               alpha,
               sr.getGuiScaledWidth() / 2.0F - sr.getGuiScaledWidth() / 1.5F,
               sr.getGuiScaledHeight() / 2.0F - sr.getGuiScaledWidth() / 1.5F,
               sr.getGuiScaledWidth() / 0.75F,
               sr.getGuiScaledWidth() / 0.75F,
               -999.0F
            );
         }

         RenderUtils.renderTextureOverlay(
            graphics,
            "mahoutsukai:textures/mob_effect/bound.png",
            255,
            255,
            255,
            0.5F,
            sr.getGuiScaledWidth() / 2.0F - sr.getGuiScaledWidth() / 10.0F,
            sr.getGuiScaledHeight() / 2.0F - sr.getGuiScaledWidth() / 10.0F,
            sr.getGuiScaledWidth() / 5.0F,
            sr.getGuiScaledWidth() / 5.0F,
            -999.0F
         );
      }
   }
}
