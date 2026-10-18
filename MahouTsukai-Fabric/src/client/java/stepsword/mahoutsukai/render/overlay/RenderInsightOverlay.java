package stepsword.mahoutsukai.render.overlay;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.joml.Matrix3x2fStack;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.EffectUtil;

public class RenderInsightOverlay {
   public static ArrayList<ItemStack> inventory = new ArrayList<>();
   public static ArrayList<MobEffectInstance> effects = new ArrayList<>();
   public static ItemStack mainhandStack = ItemStack.EMPTY;
   public static int mainhandindex = -1;
   public static Identifier insight = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/gui/insight.png");
   public static int invprogress = 0;
   public static int maxinvprogress = 10;
   public static int effectsprogress = 0;
   public static int maxeffectsprogress = 10;
   public static int mainhandprogress = 0;
   public static int maxmainhandprogress = 10;
   public static int entityId = -1;
   public static boolean hasTarget = false;
   public static boolean block = false;
   public static int lastinvwidth = 0;
   public static int lastinvheight = 0;
   public static int lasteffectswidth = 0;
   public static int lasteffectsheight = 0;
   public static int lastmainhandwidth = 0;
   public static int lastmainhandheight = 0;

   public static void insightCounters() {
      boolean hasInsight = EffectUtil.hasBuff(Minecraft.getInstance().player, ModEffects.INSIGHT);
      if (hasInsight && hasTarget && inventory.size() > 0) {
         if (invprogress < maxinvprogress) {
            invprogress++;
         }
      } else if (invprogress > 0) {
         invprogress--;
      }

      if (hasInsight && hasTarget && effects.size() > 0 && !block) {
         if (effectsprogress < maxeffectsprogress) {
            effectsprogress++;
         }
      } else if (effectsprogress > 0) {
         effectsprogress--;
      }

      if (hasInsight && hasTarget && !mainhandStack.isEmpty() && !block) {
         if (mainhandprogress < maxmainhandprogress) {
            mainhandprogress++;
         }
      } else if (mainhandprogress > 0) {
         mainhandprogress--;
      }
   }

   public static void insightOverlay(GuiGraphicsExtractor graphics) {
      boolean hasInsight = EffectUtil.hasBuff(Minecraft.getInstance().player, ModEffects.INSIGHT);
      graphics.pose().pushMatrix();

      try {
         renderInventoryTab(graphics, hasInsight);
         renderEffectsTab(graphics, hasInsight);
         renderMainHandTab(graphics, hasInsight);
      } catch (Exception var3) {
      }

      graphics.pose().popMatrix();
   }

   public static void renderMainHandTab(GuiGraphicsExtractor graphics, boolean hasInsight) {
      int y = getScaledHeight() / 2 + 32 + MTConfig.INSIGHT_BOTTOM_Y;
      int x = getScaledWidth() / 2 + MTConfig.INSIGHT_BOTTOM_X;
      int w = x / 2;
      int hinv = 0;
      int panelX = x - lastmainhandwidth / 2;
      int panelY = y + mainhandprogress;
      int yoff = 9;
      int xbuffer = 4;
      int ybuffer = 2;
      ArrayList<String> enchanted = new ArrayList<>();
      String name = null;
      if (mainhandprogress > 0 && !block) {
         if (!mainhandStack.isEmpty()) {
            ItemStack stack = mainhandStack;
            name = stack.getHoverName().getString();
            ItemEnchantments enchants = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            int maxw = -1;
            int h = 0;

            for (Holder<Enchantment> e : enchants.keySet()) {
               String n = Enchantment.getFullname(e, enchants.getLevel(e)).getString();
               maxw = Math.max(Minecraft.getInstance().font.width(n), maxw);
               enchanted.add(n);
               h++;
            }

            maxw = Math.max(Minecraft.getInstance().font.width(name), maxw);
            w = maxw + 8 * xbuffer;
            hinv = (h + 1) * (yoff + ybuffer + 2) + ybuffer * 12;
            lastmainhandheight = hinv;
            lastmainhandwidth = w;
            panelX = x - w / 2;
            panelY = y + mainhandprogress;
            drawTexturedModalRect(graphics, panelX, panelY, 130, 326, 252, 186, w, hinv, 1.0F, 1.0F, 1.0F, mainhandprogress / (1.3F * maxmainhandprogress));
         } else {
            drawTexturedModalRect(
               graphics,
               panelX,
               panelY,
               130,
               326,
               252,
               186,
               lastmainhandwidth,
               lastmainhandheight,
               1.0F,
               1.0F,
               1.0F,
               mainhandprogress / (1.3F * maxmainhandprogress)
            );
         }
      }

      if (hasInsight && mainhandprogress > 0 && name != null) {
         int sz = Minecraft.getInstance().font.width(name);
         int var10003 = panelX + lastmainhandwidth / 2 - sz / 2;
         graphics.text(Minecraft.getInstance().font, name, var10003, panelY + ybuffer, -1, true);
         int m = 1;

         for (String s : enchanted) {
            sz = Minecraft.getInstance().font.width(s);
            var10003 = panelX + lastmainhandwidth / 2 - sz / 2;
            graphics.text(Minecraft.getInstance().font, s, var10003, panelY + ybuffer + (yoff + ybuffer) * m++, -4473925, true);
         }
      }
   }

   public static void renderEffectsTab(GuiGraphicsExtractor graphics, boolean hasInsight) {
      Matrix3x2fStack matrix = graphics.pose();
      int y = getScaledHeight() / 2 + MTConfig.INSIGHT_LEFT_Y;
      int x = getScaledWidth() / 2 + MTConfig.INSIGHT_LEFT_X;
      int w = x / 2;
      int hinv = 0;
      int xoff = 16;
      int yoff = 16;
      int rowlen = 9;
      int xbuffer = 2;
      int ybuffer = 2;
      int numrows = (int)Math.ceil((float)effects.size() / rowlen);
      if (effectsprogress > 0 && !block) {
         if (effects.size() > 0) {
            w = Math.min(effects.size(), rowlen) * (xoff + xbuffer) + xbuffer * 6;
            hinv = numrows * (yoff + ybuffer) + ybuffer * 2;
            lasteffectsheight = hinv;
            lasteffectswidth = w;
            drawTexturedModalRect(
               graphics, x - w - effectsprogress, y - hinv / 2, 0, 166, 256, 180, w, hinv, 1.0F, 1.0F, 1.0F, effectsprogress / (1.3F * maxeffectsprogress)
            );
         } else {
            drawTexturedModalRect(
               graphics,
               x - lasteffectswidth - effectsprogress,
               y - lasteffectsheight / 2,
               0,
               166,
               256,
               180,
               lasteffectswidth,
               lasteffectsheight,
               1.0F,
               1.0F,
               1.0F,
               effectsprogress / (1.3F * maxeffectsprogress)
            );
         }
      }

      if (hasInsight && effectsprogress == maxeffectsprogress) {
         for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).showIcon()) {
               int iconX = x - w - effectsprogress + xbuffer * 3 + i % rowlen * (xoff + xbuffer);
               int iconY = y - hinv / 2 + ybuffer + i / rowlen * (yoff + ybuffer);
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effects.get(i).getEffect()), iconX, iconY, 18, 18);
            }
         }
      }
   }

   public static void renderInventoryTab(GuiGraphicsExtractor graphics, boolean hasInsight) {
      int y = getScaledHeight() / 2 + MTConfig.INSIGHT_RIGHT_Y;
      int x = getScaledWidth() / 2 + MTConfig.INSIGHT_RIGHT_X;
      int w = x / 2;
      int hinv = 0;
      int xoff = 16;
      int yoff = 16;
      int rowlen = 9;
      int xbuffer = 2;
      int ybuffer = 2;
      int numrows = (int)Math.ceil((float)inventory.size() / rowlen);
      if (invprogress > 0) {
         if (inventory.size() > 0) {
            w = Math.min(inventory.size(), rowlen) * (xoff + xbuffer) + xbuffer * 8;
            hinv = numrows * (yoff + ybuffer) + ybuffer * 2;
            lastinvheight = hinv;
            lastinvwidth = w;
            drawTexturedModalRect(graphics, x + invprogress, y - hinv / 2, 256, 166, 256, 180, w, hinv, 1.0F, 1.0F, 1.0F, invprogress / (1.3F * maxinvprogress));
         } else {
            drawTexturedModalRect(
               graphics,
               x + invprogress,
               y - lastinvheight / 2,
               256,
               166,
               256,
               180,
               lastinvwidth,
               lastinvheight,
               1.0F,
               1.0F,
               1.0F,
               invprogress / (1.3F * maxinvprogress)
            );
         }
      }

      if (hasInsight && invprogress == maxinvprogress) {
         for (int i = 0; i < inventory.size(); i++) {
            int itemX = x + invprogress + xoff * (i % rowlen) + xbuffer;
            int itemY = y - hinv / 2 + i / rowlen * yoff + ybuffer;
            ItemStack stack = inventory.get(i);
            graphics.fakeItem(stack, itemX, itemY);
            graphics.itemDecorations(Minecraft.getInstance().font, stack, itemX, itemY);
         }
      }
   }

   public static void drawTexturedModalRect(
      GuiGraphicsExtractor graphics, int x, int y, int textureX, int textureY, int tx2, int ty2, int width, int height, float r, float g, float b, float a
   ) {
      int color = ARGB.colorFromFloat(Math.max(0.0F, Math.min(1.0F, a)), r, g, b);
      graphics.blit(RenderPipelines.GUI_TEXTURED, insight, x, y, textureX, textureY, width, height, tx2, ty2, 512, 512, color);
   }

   public static int getScaledHeight() {
      return Minecraft.getInstance().getWindow().getGuiScaledHeight();
   }

   public static int getScaledWidth() {
      return Minecraft.getInstance().getWindow().getGuiScaledWidth();
   }
}
