package stepsword.mahoutsukai.render.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

import org.joml.Matrix3x2fStack;
import stepsword.mahoutsukai.Keybinds;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.render.overlay.RenderBoundOverlay;
import stepsword.mahoutsukai.render.overlay.RenderInsightOverlay;
import stepsword.mahoutsukai.render.overlay.RenderRevertedOverlay;
import stepsword.mahoutsukai.tile.MahoujinProjectorTileEntity;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class MahouGui  {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mahoutsukai", "overlay");

   public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
      if (!Minecraft.getInstance().options.hideGui) {
         RenderInsightOverlay.insightOverlay(guiGraphics);
         RenderBoundOverlay.boundOverlay(guiGraphics);
         RenderRevertedOverlay.revertedOverlay(guiGraphics);
         manaText(guiGraphics);
         mahoujinOverlay(guiGraphics);
      }
   }

   public static void manaText(GuiGraphicsExtractor graphics) {
      Matrix3x2fStack matrix = graphics.pose();
      Font fr = Minecraft.getInstance().font;
      String preText = Component.translatable("mahoutsukai.manabar").getString();
      int color = -2236929;
      matrix.pushMatrix();
      matrix.translate(0.0F, 0.0F);
      IMahou mahou = Utils.getPlayerMahou(Minecraft.getInstance().player);
      if (mahou != null && mahou.hasMagic() && mahou.getVisible()) {
         int storedMana = mahou.getStoredMana();
         int maxMana = mahou.getMaxMana();
         graphics.text(fr, preText + " " + storedMana + " / " + maxMana, MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X, MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y, color);
         infoOverlay(graphics, mahou, 0, fr, color);
      }

      circuitOverlay(graphics, fr, preText, color);
      projectorOverlay(graphics, fr);
      matrix.popMatrix();
   }

   public static void infoOverlay(GuiGraphicsExtractor graphics, IMahou mahou, int dist, Font fr, int color) {
      if (mahou.getDeathCollectionUsesLeft() > 0.0F) {
         dist += 12;
         String souls = I18n.get("mahoutsukai.souls.collected", new Object[0]);
         String drain = I18n.get("mahoutsukai.souls.drain", new Object[0]);
         double mdrain = mahou.getDeathCollectionUsesLeft() * MTConfig.DEATH_COLLECTION_DRAIN_PER_SOUL;
         String manadrain = String.format("%.2f", mdrain);
         graphics.text(
            fr,
            souls + " " + mahou.getDeathCollectionUsesLeft() + " (-" + manadrain + " " + drain + ")",
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X,
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y + dist,
            color
         );
      }

      if (mahou.getDamageExchangeUses() > 0) {
         dist += 12;
         String damageExchange = I18n.get("mahoutsukai.damage_exchange.count", new Object[0]);
         graphics.text(
            fr,
            damageExchange + " " + mahou.getDamageExchangeUses(),
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X,
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y + dist,
            color
         );
      }

      if (mahou.getImmunityDamageType() != null && EffectUtil.hasBuff(Minecraft.getInstance().player, ModEffects.IMMUNITY_EXCHANGE)) {
         dist += 12;
         String immunityExchange = I18n.get("mahoutsukai.immunity_exchange.last", new Object[0]);
         graphics.text(
            fr,
            immunityExchange + " " + mahou.getImmunityDamageType(),
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X,
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y + dist,
            color
         );
      }
   }

   public static void mahoujinOverlay(GuiGraphicsExtractor graphics) {
      Player player = Minecraft.getInstance().player;
      HitResult result = Minecraft.getInstance().hitResult;
      if (result != null && player != null && result.getType() == Type.BLOCK) {
         BlockHitResult bresult = (BlockHitResult)result;
         BlockPos pos = bresult.getBlockPos();
         if (pos != null
            && player.level().getBlockEntity(pos) instanceof MahoujinTileEntity mte
            && (mte.getCatalysts() == null || mte.getCatalysts().size() == 0)
            && player.getMainHandItem().isEmpty()) {
            MahoujinHUD.renderHUD(graphics, Minecraft.getInstance());
         }
      }
   }

   public static void circuitOverlay(GuiGraphicsExtractor graphics, Font fr, String preText, int color) {
      Player player = Minecraft.getInstance().player;
      HitResult result = Minecraft.getInstance().hitResult;
      if (result != null && player != null && result.getType() == Type.BLOCK) {
         BlockHitResult bresult = (BlockHitResult)result;
         BlockPos pos = bresult.getBlockPos();
         if (pos != null && player.level().getBlockEntity(pos) instanceof ManaCircuitTileEntity mte) {
            int xc = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
            int yc = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + 8;
            if (mte.isRedstoneOn()) {
               String s = Component.translatable("mahoutsukai.circuit.poweredon").getString();
               graphics.text(fr, s, xc - 30, yc, color);
            } else {
               String s = Component.translatable("mahoutsukai.circuit.poweredoff").getString();
               graphics.text(fr, s, xc - 30, yc, color);
            }

            graphics.text(fr, preText + " " + mte.getMana() + " / " + mte.getMaxMana(), xc - 30, yc + 14, color);
            if (player.getUUID().equals(mte.getOwnerUUID())) {
               String s = Component.translatable("mahoutsukai.circuit.yours").getString();
               graphics.text(fr, s, xc - 30, yc + 28, color);
            } else {
               String s = Component.translatable("mahoutsukai.circuit.notyours").getString();
               graphics.text(fr, s, xc - 30, yc + 28, color);
            }

            if (mte.getFae()) {
               String s = Component.translatable("mahoutsukai.circuit.fae_open").getString();
               graphics.text(fr, s, xc - 30, yc + 42, color);
            } else {
               String s = Component.translatable("mahoutsukai.circuit.fae_closed").getString();
               graphics.text(fr, s, xc - 30, yc + 42, color);
            }
         }
      }
   }

   public static void projectorOverlay(GuiGraphicsExtractor graphics, Font fr) {
      Player player = Minecraft.getInstance().player;
      HitResult result = Minecraft.getInstance().hitResult;
      int active = -2236929;
      int inactive = -12303258;
      if (result != null && player != null && result.getType() == Type.BLOCK) {
         BlockHitResult bresult = (BlockHitResult)result;
         BlockPos pos = bresult.getBlockPos();
         if (pos != null) {
            BlockEntity te = player.level().getBlockEntity(pos);
            if (te instanceof MahoujinProjectorTileEntity && Utils.isArmor(player.getMainHandItem())) {
               int xc = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2;
               int yc = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + 8;
               String s = Component.translatable("mahoutsukai.projector_overlay.armor_replace").getString();
               String s2 = Component.translatable("mahoutsukai.projector_overlay.armor_add").getString();
               String s3 = Component.translatable("mahoutsukai.projector_overlay.armor_remove").getString();
               s2 = "[" + Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase() + "]" + s2;
               s3 = "[" + Keybinds.drawMahoujin.getTranslatedKeyMessage().getString().toUpperCase() + "]" + s3;
               graphics.text(fr, s, xc - 30, yc + 14, active);
               graphics.text(fr, s2, xc - 30, yc + 28, Keybinds.changeMysticCode.isDown() ? active : inactive);
               graphics.text(fr, s3, xc - 30, yc + 42, !Keybinds.changeMysticCode.isDown() && Keybinds.drawMahoujin.isDown() ? active : inactive);
            }
         }
      }
   }
}
