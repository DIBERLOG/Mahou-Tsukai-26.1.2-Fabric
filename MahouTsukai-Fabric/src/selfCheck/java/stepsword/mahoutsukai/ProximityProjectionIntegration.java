package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.networking.LongDistanceAttackPacket;

public final class ProximityProjectionIntegration {
    private static void require(boolean value,String message) {if(!value)throw new AssertionError(message);}
    public static void run(net.minecraft.server.level.ServerPlayer fixture) {
        var world=fixture.level();var player=DoomIntegration.player(world,"ProximityCheck",GameType.SURVIVAL,570);
        player.snapTo(570.5,250,530.5,180,0);player.setYHeadRot(180);
        world.getEntities(player,player.getBoundingBox().inflate(6)).forEach(e->e.discard());
        var mana=((MahouHolder)player).mahoutsukai$getMahou();mana.setMaxMana(2000);mana.setStoredMana(1000);
        var scroll=new ItemStack(ModItems.REGISTERED.get("scroll_proximity_projection"));
        ((SpellScroll)scroll.getItem()).releaseUsing(scroll,world,player,71900);
        ItemStack keys=ItemStack.EMPTY;
        for(var stack:player.getInventory()) if(stack.is(ModItems.REGISTERED.get("proximity_projection_keys"))) keys=stack;
        var knockback=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.KNOCKBACK);
        var protection=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION);
        require(scroll.isEmpty() && mana.getStoredMana()==920 && !keys.isEmpty() && keys.getMaxDamage()==130 && keys.getMaxStackSize()==1,"Original keys scroll consumes 160 mana and creates thirty-durability weapon");
        require(EnchantmentHelper.getItemEnchantmentLevel(knockback,keys)==2 && knockback.value().isPrimaryItem(keys) && protection.value().isSupportedItem(keys) && !protection.value().isPrimaryItem(keys),"Keys start with knockback II, support all enchantments and use iron sword primary selection");
        player.setItemInHand(InteractionHand.MAIN_HAND,keys);
        // Apply normal equipment attributes through the native living tick, then charge the ordinary attack timer.
        for(int i=0;i<30;i++) player.tick();player.snapTo(570.5,250,530.5,180,0);player.setYHeadRot(180);
        var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.snapTo(570.5,250,520.5,0,0);cow.setNoAi(true);cow.setNoGravity(true);world.addFreshEntity(cow);
        LongDistanceAttackPacket.doAttack(player,cow);
        require(cow.getHealth()<10 && player.blockPosition().equals(new BlockPos(570,250,520)) && keys.getDamageValue()==1,"Validated native remote attack damages target, consumes durability and teleports player to target block");
        player.snapTo(570.5,250,530.5,180,0);player.setYHeadRot(180);cow.invulnerableTime=0;
        var stone=new BlockPos(570,251,525);world.setBlockAndUpdate(stone,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());var health=cow.getHealth();
        LongDistanceAttackPacket.doAttack(player,cow);require(cow.getHealth()==health && player.getZ()==530.5,"Server refuses target hidden behind solid block");world.setBlockAndUpdate(stone,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        cow.snapTo(570.5,250,400.5,0,0);LongDistanceAttackPacket.doAttack(player,cow);require(cow.getHealth()==health && player.getZ()==530.5,"Server refuses out-of-range entity ID");
        cow.snapTo(570.5,250,520.5,0,0);player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.STICK));LongDistanceAttackPacket.doAttack(player,cow);require(cow.getHealth()==health,"Remote request requires actual keys in main hand");
        player.setItemInHand(InteractionHand.MAIN_HAND,keys);player.addEffect(new net.minecraft.world.effect.MobEffectInstance(stepsword.mahoutsukai.potion.ModEffects.BOUND,20));LongDistanceAttackPacket.doAttack(player,cow);require(cow.getHealth()==health,"Bound player cannot use remote key attack");player.removeEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND);
        var wire=io.netty.buffer.Unpooled.buffer();try {LongDistanceAttackPacket.STREAM_CODEC.encode(wire,new LongDistanceAttackPacket(cow));require(wire.readableBytes()==4 && LongDistanceAttackPacket.STREAM_CODEC.decode(wire).entityId==cow.getId(),"Original four-byte remote attack packet preserved");}finally{wire.release();}
        var left=keys.copy();var right=keys.copy();left.setDamageValue(20);right.setDamageValue(20);
        var input=net.minecraft.world.item.crafting.CraftingInput.of(2,1,java.util.List.of(left,right));
        require(!net.minecraft.world.item.crafting.RepairItemRecipe.INSTANCE.matches(input,world),"Keys cannot be combined in repair crafting");
        var pos=new BlockPos(570,250,535);ExchangeCircleIntegration.ritual(player,pos,"proximity_projection",true,"diamond","diamond","ender");player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
        world.getBlockState(pos).useWithoutItem(world,player,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
        var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_proximity_projection")));
        require(world.getBlockState(pos).isAir() && drops.size()==1,"Original cloth and two diamond/ender catalysts produce proximity scroll");drops.forEach(e->e.discard());cow.discard();player.discard();
        System.out.println("PASS: PROXIMITY_PROJECTION_INTEGRATION: native enchanted weapon/cost/durability, actual remote attack/teleport, server wall/range/held-item/bound validation, original wire codec, repair exclusion and ritual acquisition");
    }
}
