package stepsword.mahoutsukai.fireball;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import stepsword.mahoutsukai.entity.ModEntities;
import stepsword.mahoutsukai.item.spells.SpellScroll;

/** Additional classic spell; inherited paid casting refunds a failed spawn and early cancellation. */
public final class FireballScroll extends SpellScroll {
 public static final int HOLD_TICKS=24, COOLDOWN_TICKS=40, RAW_MANA_COST=80;
 public FireballScroll(){super("fireball");}
 @Override public int getInitialManaCost(){return RAW_MANA_COST;}
 @Override public void appendHoverText(ItemStack stack,net.minecraft.world.item.Item.TooltipContext context,net.minecraft.world.item.component.TooltipDisplay display,java.util.function.Consumer<net.minecraft.network.chat.Component> tooltip,net.minecraft.world.item.TooltipFlag flag){super.appendHoverText(stack,context,display,tooltip,flag);tooltip.accept(net.minecraft.network.chat.Component.translatable("mahoutsukai.fireball.hint"));}
 @Override public boolean doSpell(Player user,ScrollMahou binding,ItemStack stack){
  if(!(user.level() instanceof ServerLevel world)||user.getCooldowns().isOnCooldown(stack))return false;
  var direction=user.getLookAngle().normalize();
  var eyes=user.getEyePosition();var origin=eyes.add(direction.scale(.6));
  var obstruction=world.clip(new ClipContext(eyes,origin,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,user));
  if(obstruction.getType()!=HitResult.Type.MISS)return false;
  var projectile=new FireballEntity(ModEntities.FIREBALL.get(),world);
  projectile.setOwner(user);projectile.setPos(origin);projectile.setYRot(user.getYRot());projectile.setXRot(user.getXRot());
  projectile.setDeltaMovement(direction.scale(FireballEntity.SPEED));
  if(world.getBlockCollisions(projectile,projectile.getBoundingBox()).iterator().hasNext()||!world.addFreshEntity(projectile))return false;
  user.getCooldowns().addCooldown(stack,COOLDOWN_TICKS);
  world.playSound(null,origin.x,origin.y,origin.z,SoundEvents.BLAZE_SHOOT,SoundSource.PLAYERS,.8F,.85F);
  return true;
 }
}
