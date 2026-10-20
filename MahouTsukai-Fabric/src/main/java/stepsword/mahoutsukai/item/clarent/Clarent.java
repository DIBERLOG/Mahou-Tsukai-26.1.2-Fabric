package stepsword.mahoutsukai.item.clarent;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.level.Level;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.sword.SwordMahou;
import stepsword.mahoutsukai.item.GrowSwordItem;
import stepsword.mahoutsukai.util.Utils;

public class Clarent extends GrowSwordItem {
   protected String name;
   public static String MT_ATTACK_DAMAGE = "mahoutsukai_clarent_attack";

   public Clarent() {
      super(ToolMaterial.IRON, 1.0F, 10, MTConfig.CLARENT_DURABILITY);
   }

   public static final float MAX_DAMAGE=1600;
   private static final String MEMORY="mahoutsukai_clarent_memory",UPDATED="mahoutsukai_clarent_updated";
   public static float remembered(ItemStack stack){return Math.clamp(stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getFloatOr(MEMORY,0),0,MAX_DAMAGE);}
   public static float cap(){return Math.min(MAX_DAMAGE,Math.max(0,MTConfig.CLARENT_ATTACK_CAP));}
   /** AttackDamage remains the total: permanent floor + volatile power. Older stacks begin volatile. */
   public static void decay(ItemStack stack,long now){
      var tag=stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
      long previous=tag.getLongOr(UPDATED,now);var stats=Utils.getSwordMahou(stack);float total=Math.clamp(stats.getAttackDamage(),0,cap()),memory=Math.min(remembered(stack),total);
      long elapsed=Math.max(0,now-previous);
      if(elapsed>0){float lost=(float)((total-memory)*(1-Math.pow(.5,elapsed/1200.0)));total-=lost*(2F/3F);memory+=lost/3F;}
      stats.setAttackDamage(total);Utils.setSwordMahou(stack,stats);tag.putFloat(MEMORY,memory);tag.putLong(UPDATED,now);stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(tag));
   }
   @Override public void inventoryTick(ItemStack stack,net.minecraft.server.level.ServerLevel world,net.minecraft.world.entity.Entity entity,net.minecraft.world.entity.EquipmentSlot slot){super.inventoryTick(stack,world,entity,slot);if(world.getGameTime()%20==0)decay(stack,world.getGameTime());}
   @Override public float getAttack(ItemStack stack){return Math.clamp(super.getAttack(stack),0,cap());}

   public static float calculateDamageFromWeapon(ItemStack s) {
      float r = 1.0F;
      ItemAttributeModifiers map = GrowSwordItem.attributes(s,s.getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,ItemAttributeModifiers.EMPTY));
      float d = 1.0F;

      for (Entry me : map.modifiers()) {
         if (me.attribute().is(Attributes.ATTACK_DAMAGE)) {
            AttributeModifier m = me.modifier();
            if (m.operation() == Operation.ADD_VALUE) {
               r = (float)(r + m.amount());
            }

            d = r;
            if (m.operation() == Operation.ADD_MULTIPLIED_BASE) {
               d = (float)(d + r * m.amount());
            }

            if (m.operation() == Operation.ADD_MULTIPLIED_TOTAL) {
               d = (float)(d * (1.0 + m.amount()));
            }
         }
      }

      return d;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return stepsword.mahoutsukai.fabric.ClarentGuard.ACTIVE_TICKS;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BLOCK;
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      if (handIn != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
      if (player.getCooldowns().isOnCooldown(player.getItemInHand(handIn))) return InteractionResult.FAIL;
      if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer && !stepsword.mahoutsukai.fabric.ClarentGuard.begin(serverPlayer)) return InteractionResult.FAIL;
      if (handIn == InteractionHand.MAIN_HAND) {
         player.startUsingItem(handIn);
      }

      return InteractionResult.CONSUME;
   }

   public boolean releaseUsing(ItemStack stack, Level world, LivingEntity player, int cnt) {
      if(player instanceof net.minecraft.server.level.ServerPlayer p)stepsword.mahoutsukai.fabric.ClarentGuard.finish(p);
      super.releaseUsing(stack, world, player, cnt);
      return true;
   }

   @Override public ItemStack finishUsingItem(ItemStack stack,Level world,LivingEntity entity){if(entity instanceof net.minecraft.server.level.ServerPlayer p)stepsword.mahoutsukai.fabric.ClarentGuard.finish(p);return stack;}

   public static int auraColor(float power){return power<25?0x69BFFF:power<100?0xAF5BFF:power<500?0xFF476B:0xFFD369;}
   public static void aura(Player player){
      if(player.level() instanceof net.minecraft.server.level.ServerLevel world && player.getMainHandItem().getItem() instanceof Clarent && player.tickCount%4==0){
       float power=Utils.getSwordMahou(player.getMainHandItem()).getAttackDamage();
       world.sendParticles(new net.minecraft.core.particles.DustParticleOptions(auraColor(power),1.2F),player.getX(),player.getY()+1,player.getZ(),4,0.6,0.7,0.6,0.01);
      }
   }
   @Override public void appendHoverText(ItemStack stack,net.minecraft.world.item.Item.TooltipContext context,net.minecraft.world.item.component.TooltipDisplay display,java.util.function.Consumer<net.minecraft.network.chat.Component> text,net.minecraft.world.item.TooltipFlag flag){
    super.appendHoverText(stack,context,display,text,flag);
    text.accept(net.minecraft.network.chat.Component.translatable("mahoutsukai.clarent.permanent",String.format(java.util.Locale.ROOT,"%.1f",Utils.getSwordMahou(stack).getAttackDamage())));
    text.accept(net.minecraft.network.chat.Component.translatable("mahoutsukai.clarent.memory",String.format(java.util.Locale.ROOT,"%.1f",remembered(stack)),1600));
    text.accept(net.minecraft.network.chat.Component.translatable("mahoutsukai.clarent.hint"));
   }
   public int getMaxDamage(ItemStack stack) {
      return MTConfig.CLARENT_DURABILITY;
   }
}
