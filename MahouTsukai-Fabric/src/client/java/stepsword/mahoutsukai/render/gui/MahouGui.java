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

   public static int doomTicks;

   public static final Identifier ID = Identifier.fromNamespaceAndPath("mahoutsukai", "overlay");



   public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {

      if (!Minecraft.getInstance().options.hideGui) {

         // Each overlay is isolated: one broken effect must never remove the mana panel.
         section("doom_sky", () -> stepsword.mahoutsukai.client.DoomSkyEffect.render(guiGraphics));

         section("insight", () -> RenderInsightOverlay.insightOverlay(guiGraphics));

         section("bound", () -> RenderBoundOverlay.boundOverlay(guiGraphics));

         section("reverted", () -> RenderRevertedOverlay.revertedOverlay(guiGraphics));

         section("mana", () -> manaText(guiGraphics));

         section("mahoujin", () -> mahoujinOverlay(guiGraphics));

      }

   }

   private static final java.util.Set<String> REPORTED = java.util.concurrent.ConcurrentHashMap.newKeySet();

   private static void section(String name, Runnable body) {
      try {
         body.run();
      } catch (RuntimeException | LinkageError error) {
         if (REPORTED.add(name)) {
            org.slf4j.LoggerFactory.getLogger("mahoutsukai").error("HUD section {} failed and was skipped", name, error);
         }
      }
   }



   public static void manaText(GuiGraphicsExtractor graphics) {

      Matrix3x2fStack matrix = graphics.pose();

      Font fr = Minecraft.getInstance().font;

      String preText = Component.translatable("mahoutsukai.manabar").getString();

      int color = -2236929;

      matrix.pushMatrix();
      float hudScale=.82F;
      // Effective placement is clamped to the current screen so a stale or absurd config value can
      // never push the mana HUD off-screen (one real way for the HUD to "disappear").
      int baseX=hudX(),baseY=hudY();
      matrix.translate(baseX*(1-hudScale),baseY*(1-hudScale));
      matrix.scale(hudScale,hudScale);
      try {

      matrix.translate(0.0F, 0.0F);

      IMahou mahou = Utils.getPlayerMahou(Minecraft.getInstance().player);

      if (mahou != null && mahou.getVisible()) {

         int storedMana = mahou.getStoredMana();

         int maxMana = mahou.getMaxMana();

         int panelX=baseX,panelY=baseY;

         int panelWidth=Math.min(220,Math.max(80,Minecraft.getInstance().getWindow().getGuiScaledWidth()-panelX-8));

         graphics.fill(panelX-4,panelY-4,panelX+panelWidth,panelY+24,0x8A15121C);

         graphics.fill(panelX-4,panelY-4,panelX-2,panelY+24,0xFF9A79BC);

         graphics.fill(panelX,panelY+22,panelX+(int)((panelWidth-4)*Math.clamp(storedMana/(double)Math.max(1,maxMana),0,1)),panelY+23,0xFF9071B4);

         graphics.text(fr, preText + " " + storedMana + " / " + maxMana, baseX, baseY, color);

         int x=baseX,y=baseY;

         graphics.text(fr,Component.translatable("mahoutsukai.progression.hud",stepsword.mahoutsukai.fabric.MageProgression.level(maxMana),stepsword.mahoutsukai.fabric.MageProgression.nextLevelMana(maxMana)).getString(),x,y+12,0xFFD6A1FF);

         var player=Minecraft.getInstance().player;

         int hintHeight=castHints(graphics,player,x,y+29,fr);

         int extras=y+31+hintHeight;

         if(doomTicks>0){int seconds=(doomTicks+19)/20;graphics.text(fr,Component.translatable("mahoutsukai.cast.doom_ready",String.format(java.util.Locale.ROOT,"%d:%02d",seconds/60,seconds%60)).getString(),x,extras,0xFFCE9DFF);extras+=12;}

         int mental=stepsword.mahoutsukai.client.MentalDisplacementClientEffect.secondsLeft();

         if(mental>=0){graphics.text(fr,Component.translatable("mahoutsukai.mental.countdown",mental).getString(),x,extras,0xFF9FE0FF);extras+=12;}

         if(MTConfig.SHOW_CAST_HINTS&&EffectUtil.hasBuff(player,ModEffects.BLACK_FLAME_EYES)){graphics.text(fr,Component.translatable("mahoutsukai.black_flame.stop_hint",Keybinds.stopMagic.getTranslatedKeyMessage().getString()).getString(),x,extras,0xFFB4DE91);extras+=12;}

         infoOverlay(graphics, mahou, extras-y, fr, color);

      }



      if(mahou==null||!mahou.getVisible())castHints(graphics,Minecraft.getInstance().player,baseX,baseY,fr);

      } finally { matrix.popMatrix(); }
      circuitOverlay(graphics, fr, preText, color);

      projectorOverlay(graphics, fr);

   }



   private static String fit(Font font,String text,int width){return font.width(text)<=width?text:font.plainSubstrByWidth(text,Math.max(0,width-font.width("...")))+"...";}

   /** Effective HUD origin: configured placement clamped to the current GUI screen. */
   private static int hudX(){return Math.clamp(MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X,0,Math.max(0,Minecraft.getInstance().getWindow().getGuiScaledWidth()-60));}
   private static int hudY(){return Math.clamp(MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_Y,0,Math.max(0,Minecraft.getInstance().getWindow().getGuiScaledHeight()-60));}

   /** Total cooldown length in ticks for the spells that actually register one. */
   private static int cooldownTicks(net.minecraft.world.item.ItemStack stack){
      if(stack.getItem() instanceof stepsword.mahoutsukai.fireball.FireballScroll)return stepsword.mahoutsukai.fireball.FireballScroll.COOLDOWN_TICKS;
      if(stack.getItem() instanceof stepsword.mahoutsukai.doom.DoomScroll)return stepsword.mahoutsukai.doom.DoomScroll.COOLDOWN;
      return 0;
   }



   public static int castHints(GuiGraphicsExtractor graphics,Player player,int x,int y,Font font){

      if(!MTConfig.SHOW_CAST_HINTS)return 0;

      var held=player.isUsingItem()?player.getUseItem():player.getMainHandItem();

      if(!(held.getItem() instanceof stepsword.mahoutsukai.item.spells.SpellScroll)&&!(held.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode)&&!(held.getItem() instanceof stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff)){if(player.isUsingItem())return 0;held=player.getOffhandItem();}

      boolean code=held.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode;

      var spell=stepsword.mahoutsukai.fabric.SpellCasting.active(held);

      boolean staff=spell.getItem() instanceof stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff;
      if(!code&&!staff&&!(spell.getItem() instanceof stepsword.mahoutsukai.item.spells.SpellScroll))return 0;

      int width=Math.min(220,Math.max(80,Minecraft.getInstance().getWindow().getGuiScaledWidth()-x-8)),height=code?63:48;

      graphics.fill(x-4,y-3,x+width,y+height-3,0xA015121C);

      graphics.fill(x-4,y-3,x-2,y+height-3,0xFF766289);

      if(code){

         var inventory=Utils.getMCInventory(held);int selected=stepsword.mahoutsukai.item.mysticcode.MysticCode.getSlot(held),cell=(width-4)/3;

         for(int i=0;i<3;i++){int left=x+i*cell;boolean active=i==selected;graphics.fill(left,y,left+cell-2,y+12,active?0xAA665077:0x55302B36);

            String title=(i+1)+" "+(inventory.getItem(i).isEmpty()?"—":inventory.getItem(i).getHoverName().getString());

            graphics.text(font,fit(font,title,cell-6),left+2,y+2,active?0xFFFFFFFF:0xFFB3ACBA);

         }

         y+=15;

      }

      if(spell.isEmpty()){graphics.text(font,Component.translatable("mahoutsukai.hints.empty").getString(),x,y,0xFFCAC2D1);return height;}

      String title=staff?Component.translatable("mahoutsukai.hints.staff",Component.translatable(switch(Utils.getMode(spell)){case 0->"mahoutsukai.mysticstaff.big";case 1->"mahoutsukai.mysticstaff.aoe";default->"mahoutsukai.mysticstaff.beam";})).getString():spell.getHoverName().getString();
      graphics.text(font,fit(font,title,width-4),x,y,0xFFE2D5ED);

      if(stepsword.mahoutsukai.fabric.SpellCasting.summonsWeapon(spell)&&code){graphics.text(font,fit(font,Component.translatable("mahoutsukai.cast.weapon_code").getString(),width-4),x,y+13,0xFFFFB394);return height;}

      if(!spell.isEmpty()&&player.getCooldowns().isOnCooldown(spell)){
         float cooldown=player.getCooldowns().getCooldownPercent(spell,0);
         int total=cooldownTicks(spell);
         String label=total>0?Component.translatable("mahoutsukai.cast.cooldown",String.format(java.util.Locale.ROOT,"%.1f",cooldown*total/20.0)).getString():Component.translatable("mahoutsukai.cast.cooldown_unknown").getString();
         graphics.text(font,fit(font,label,width-4),x,y+13,0xFFFFB16B);
         int hold=stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(spell);
         graphics.text(font,fit(font,Component.translatable("mahoutsukai.hints.hold",String.format(java.util.Locale.ROOT,"%.1f",hold/20.0)).getString(),width-4),x,y+26,0xFFABA5B2);
         graphics.fill(x,y+40,x+width-4,y+42,0xFF342D3D);graphics.fill(x,y+40,x+(int)((width-4)*(1-cooldown)),y+42,0xFFEBA85F);return height;
      }

      int needed=stepsword.mahoutsukai.fabric.SpellCasting.requiredTicks(spell),elapsed=player.isUsingItem()?player.getTicksUsingItem():0;

      boolean channel=spell.getItem() instanceof stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll||staff&&Utils.getMode(spell)!=0;

      String key=player.isUsingItem()?(elapsed>=needed?(channel?"mahoutsukai.cast.channeling":"mahoutsukai.cast.ready"):"mahoutsukai.hints.countdown"):"mahoutsukai.hints.hold";

      String seconds=String.format(java.util.Locale.ROOT,"%.1f",Math.max(0,needed-elapsed)/20.0);

      graphics.text(font,fit(font,Component.translatable(key,seconds).getString(),width-4),x,y+13,elapsed>=needed?0xFFB1D9B5:0xFFBFAACD);

      String action=Component.translatable(channel?"mahoutsukai.hints.keep_holding":"mahoutsukai.hints.release",Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage()).getString();

      graphics.text(font,fit(font,action,width-4),x,y+26,0xFFABA5B2);

      graphics.fill(x,y+40,x+width-4,y+42,0xFF342D3D);

      graphics.fill(x,y+40,x+(int)((width-4)*Math.min(1,elapsed/(double)needed)),y+42,elapsed>=needed?0xFF8DBA94:0xFFA486BA);

      return height;

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

            hudX(),

            hudY() + dist,

            color

         );

      }



      if (mahou.getDamageExchangeUses() > 0) {

         dist += 12;

         String damageExchange = I18n.get("mahoutsukai.damage_exchange.count", new Object[0]);

         graphics.text(

            fr,

            damageExchange + " " + mahou.getDamageExchangeUses(),

            hudX(),

            hudY() + dist,

            color

         );

      }



      if (mahou.getImmunityDamageType() != null && EffectUtil.hasBuff(Minecraft.getInstance().player, ModEffects.IMMUNITY_EXCHANGE)) {

         dist += 12;

         String immunityExchange = I18n.get("mahoutsukai.immunity_exchange.last", new Object[0]);

         graphics.text(

            fr,

            immunityExchange + " " + mahou.getImmunityDamageType(),

            hudX(),

            hudY() + dist,

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
