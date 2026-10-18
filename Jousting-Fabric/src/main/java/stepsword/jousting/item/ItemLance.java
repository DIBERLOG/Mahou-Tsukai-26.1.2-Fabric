package stepsword.jousting.item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import stepsword.jousting.JoustingMod;

public class ItemLance extends ItemBase {
   private Supplier<Double> attackDamage;
   private Supplier<Double> weight;
   private Supplier<Integer> durability;
   public static final Identifier speedlance = Identifier.fromNamespaceAndPath("jousting", "lance_speed");
   public static final Identifier attacklance = Identifier.fromNamespaceAndPath("jousting", "lance_attack_damage");

   public ItemLance(Supplier<Integer> getDurability, Supplier<Double> getDamage, Supplier<Double> getWeight) {
      super(new Properties().durability(getDurability.get()).enchantable(3));
      this.attackDamage = getDamage;
      this.weight = getWeight;
      this.durability = getDurability;
   }

   public ItemLance(String name, CreativeModeTab group, Supplier<Integer> getDurability, Supplier<Double> getDamage, Supplier<Double> getWeight) {
      super(new Properties().durability(getDurability.get()).enchantable(3));
      this.attackDamage = getDamage;
      this.weight = getWeight;
      this.durability = getDurability;
   }

   public InteractionResult use(Level worldIn, Player player, InteractionHand handIn) {
      if (handIn == InteractionHand.MAIN_HAND && player.getVehicle() != null) {
         player.startUsingItem(handIn);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      if (count <= 4 && player.level().isClientSide() && player instanceof Player) {
         JoustingMod.proxy.stopUsing((Player)player);
      }

      if (!player.level().isClientSide() && player.getVehicle() != null) {
         double motion = 1.0;
         if (player.getVehicle() instanceof LivingEntity) {
            float z = (float)this.getUseDuration(stack, player) - count;
            double w = this.getWeight(stack);
            addspeed((LivingEntity)player.getVehicle(), true, (1.0F + z / 120.0F) * (1.0F + z / 120.0F) / 2.0F);
            AttributeInstance attr = ((LivingEntity)player.getVehicle()).getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
               motion = attr.getValue();
            }
         }

         AABB aabb = player.getBoundingBox();
         aabb = aabb.minmax(player.getVehicle().getBoundingBox()).inflate(motion);
         List<Entity> entities = player.level().getEntities(player, aabb, a -> a instanceof LivingEntity && a != player.getVehicle());
         Vec3 v = player.getLookAngle();
         double len = motion;
         v = v.normalize();
         ArrayList<AABB> lst = new ArrayList<>();
         Vec3 ppos = player.position();

         for (int m = 0; m < len; m++) {
            Vec3 r = ppos.add(v.scale(m));
            float d = 1.0F;
            lst.add(new AABB(r.x - d, r.y - d * 2.0F, r.z - d, r.x + d, r.y + d * 2.0F, r.z + d));
         }

         for (Entity e : entities) {
            if (e instanceof LivingEntity) {
               AABB bb = e.getBoundingBox();
               boolean intersects = false;
               player.position();

               for (int m = 0; m < lst.size(); m++) {
                  AABB q = lst.get(m);
                  if (q.intersects(bb)) {
                     intersects = true;
                  }
               }

               if (intersects) {
                  if (!(((LivingEntity)e).getUseItem().getItem() instanceof ShieldItem)) {
                     if (player instanceof Player p) {
                        this.attack(p, e);
                        Vec3 kv = player.getLookAngle().normalize();
                        ((LivingEntity)e).knockback((float)this.getWeight(stack), -kv.x, -kv.z);
                     } else {
                        e.hurtServer((net.minecraft.server.level.ServerLevel)level,player.damageSources().mobAttack(player),(float)this.getAttackDamage(stack));
                        Vec3 kv = player.getLookAngle().normalize();
                        ((LivingEntity)e).knockback((float)this.getWeight(stack), -kv.x, -kv.z);
                     }

                     stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                  } else {
                     stack.hurtAndBreak(30, player, EquipmentSlot.MAINHAND);
                  }

                  if (e.getVehicle() != null) {
                     if (((LivingEntity)e).getMainHandItem().getItem() instanceof ItemLance) {
                        joust((LivingEntity)e, player);
                     } else {
                        e.removeVehicle();
                     }
                  }

                  for (Entity passenger : e.getIndirectPassengers()) {
                     if (((LivingEntity)passenger).getMainHandItem().getItem() instanceof ItemLance
                        && ((LivingEntity)passenger).getUseItem().getItem() instanceof ItemLance) {
                        joust((LivingEntity)passenger, player);
                     } else {
                        passenger.removeVehicle();
                     }
                  }
               }
            }
         }
      }
   }

   public int getMaxDamage(ItemStack stack) {
      return this.durability.get();
   }

   public boolean releaseUsing(ItemStack p_77615_1_, Level p_77615_2_, LivingEntity player, int p_77615_4_) {
      if (!player.level().isClientSide()) {
         if (player.getVehicle() instanceof LivingEntity) {
            double w = this.getWeight(player.getMainHandItem());
            addspeed((LivingEntity)player.getVehicle(), false, (float)(1.0 + 1.0 / w));
         }

         if (player instanceof Player) {
            ((Player)player).getCooldowns().addCooldown(p_77615_1_, 40);
         }
      }

      return super.releaseUsing(p_77615_1_, p_77615_2_, player, p_77615_4_);
   }

   public static void joust(LivingEntity e1, LivingEntity e2) {
      if (e1.getMainHandItem().getItem() instanceof ItemLance && e2.getMainHandItem().getItem() instanceof ItemLance) {
         ItemLance lance1 = (ItemLance)e1.getMainHandItem().getItem();
         ItemLance lance2 = (ItemLance)e2.getMainHandItem().getItem();
         if (e1.getVehicle() instanceof LivingEntity && e2.getVehicle() instanceof LivingEntity) {
            double horse1 = ((LivingEntity)e1.getVehicle()).getAttribute(Attributes.MOVEMENT_SPEED).getValue();
            double horse2 = ((LivingEntity)e1.getVehicle()).getAttribute(Attributes.MOVEMENT_SPEED).getValue();
            double val1 = horse1 * 2.0 + lance1.getAttackDamage(e1.getMainHandItem()) / 4.0;
            double val2 = horse2 * 2.0 + lance2.getAttackDamage(e2.getMainHandItem()) / 4.0;
            if (e2.getOffhandItem().getItem() instanceof ShieldItem) {
               e1.getMainHandItem().hurtAndBreak(50, e1, EquipmentSlot.MAINHAND);
               val1 -= lance1.getAttackDamage(e1.getMainHandItem()) / 8.0;
            }

            if (e1.getOffhandItem().getItem() instanceof ShieldItem) {
               e2.getMainHandItem().hurtAndBreak(50, e2, EquipmentSlot.MAINHAND);
               val2 -= lance2.getAttackDamage(e1.getMainHandItem()) / 8.0;
            }

            if (val1 > val2) {
               e2.removeVehicle();
            } else if (val2 > val1) {
               e1.removeVehicle();
            } else if (e1.getRandom().nextBoolean()) {
               e1.removeVehicle();
            } else {
               e2.removeVehicle();
            }
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext pContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag pTooltipFlag) {
      if (stack.getItem() instanceof ItemLance lance) {
         DecimalFormat df = new DecimalFormat("#.#");
         String w = df.format(this.getWeight(stack));
         String a = df.format(this.getAttackDamage(stack));
         tooltip.accept(Component.translatable("jousting.lance.weight").append(":" + w).withStyle(ChatFormatting.DARK_PURPLE));
         tooltip.accept(Component.translatable("jousting.lance.attackdamage").append(":" + a).withStyle(ChatFormatting.BLUE));
      }
   }

   public static void addspeed(LivingEntity living, boolean add, float s) {
      AttributeModifier a = new AttributeModifier(speedlance, s, Operation.ADD_MULTIPLIED_TOTAL);
      AttributeInstance attr = living.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attr != null) {
         attr.removeModifier(speedlance);
         if (add) {
            attr.addPermanentModifier(a);
         }
      }
   }

   public double getAttackDamage(ItemStack stack) {
      return this.attackDamage.get();
   }

   public double getWeight(ItemStack stack) {
      return this.weight.get();
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return (int)(120.0 / this.getWeight(stack));
   }

   public ItemUseAnimation getUseAnimation(ItemStack pStack) {
      return ItemUseAnimation.NONE;
   }

   public void attack(Player player, Entity pTarget) {
      AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attackDamage == null) {
         player.attack(pTarget);
      } else {
         AttributeModifier modifier = new AttributeModifier(attacklance, this.getAttackDamage(player.getMainHandItem()), Operation.ADD_VALUE);
         attackDamage.removeModifier(attacklance);
         attackDamage.addTransientModifier(modifier);

         try {
            player.attack(pTarget);
         } finally {
            attackDamage.removeModifier(attacklance);
         }
      }
   }
}
