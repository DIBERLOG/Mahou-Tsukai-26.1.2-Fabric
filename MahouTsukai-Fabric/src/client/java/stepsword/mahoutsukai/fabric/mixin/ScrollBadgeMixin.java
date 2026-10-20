package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stepsword.mahoutsukai.render.gui.ScrollBadges;

@Mixin(GuiGraphicsExtractor.class)
public abstract class ScrollBadgeMixin {
 @Inject(method="item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",at=@At("TAIL"))
 private void mahoutsukai$badges(LivingEntity owner,Level level,ItemStack stack,int x,int y,int seed,CallbackInfo ci){
  ScrollBadges.render((GuiGraphicsExtractor)(Object)this,stack,x,y);
 }
 @Inject(method="itemCount",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$scrollCount(Font font,ItemStack stack,int x,int y,String override,CallbackInfo ci){
  if(!ScrollBadges.supports(stack)||ScrollBadges.role(stack)==null)return;
  if(stack.getCount()!=1||override!=null){
   String count=override==null?String.valueOf(stack.getCount()):override;
   ((GuiGraphicsExtractor)(Object)this).text(font,count,x+17-font.width(count),y,-1,true);
  }
  ci.cancel();
 }
}
