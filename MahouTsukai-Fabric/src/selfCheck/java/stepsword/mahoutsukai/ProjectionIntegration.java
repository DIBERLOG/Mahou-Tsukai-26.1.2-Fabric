package stepsword.mahoutsukai;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.datacomponents.DataComponents;
import stepsword.mahoutsukai.effects.projection.ProjectionSpellEffect;
import stepsword.mahoutsukai.effects.projection.StrengtheningSpellEffect;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;

public final class ProjectionIntegration {
    private static void require(boolean value,String message) {if(!value)throw new AssertionError(message);}
    public static void run(ServerPlayer fixture) {
        var world=fixture.level();
        var player=DoomIntegration.player(world,"ProjectionCheck",GameType.SURVIVAL,510);
        player.snapTo(510.5,250,510.5,180,0);player.setYHeadRot(180);
        world.getEntities(player,player.getBoundingBox().inflate(6)).forEach(e->e.discard());
        var mana=((MahouHolder)player).mahoutsukai$getMahou();mana.setMaxMana(10000);mana.setStoredMana(6000);
        var zombie=EntityType.ZOMBIE.create(world,EntitySpawnReason.COMMAND);
        zombie.snapTo(510.5,250,506.5,0,0);zombie.setNoAi(true);zombie.setNoGravity(true);
        var sword=new ItemStack(Items.DIAMOND_SWORD);
        var sharpness=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);
        var unbreaking=world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        sword.enchant(sharpness,2);sword.enchant(unbreaking,3);sword.setDamageValue(100);
        zombie.setItemSlot(EquipmentSlot.MAINHAND,sword);world.addFreshEntity(zombie);
        // Native selector sees loaded/ticking entities; initial spawn is also exercised directly through equipment.
        var strengthened=sword.copy();StrengtheningSpellEffect.setStrengthened(strengthened,world);
        require(!ProjectionSpellEffect.canProject(strengthened,world),"Temporarily unbreakable weapon cannot be projected");
        var memorized=ProjectionSpellEffect.memorize(player);
        require(memorized==zombie.getMainHandItem(),"Projection strict cursor finds equipped weapon");
        var scroll=new ItemStack(ModItems.REGISTERED.get("scroll_projection"));
        ((SpellScroll)scroll.getItem()).releaseUsing(scroll,world,player,71999);
        require(scroll.getCount()==1 && Utils.getStackHolderMahou(scroll).getStack().isPresent() && mana.getStoredMana()==5000,"Original memorization charges mana and retains scroll");
        zombie.getMainHandItem().setDamageValue(200);
        require(Utils.getStackHolderMahou(scroll).getStack().orElseThrow().getDamageValue()==100,"Memorized weapon is a snapshot");
        var copiedScroll=scroll.copy();var holder=Utils.getStackHolderMahou(copiedScroll);holder.getStack().orElseThrow().setDamageValue(300);Utils.setStackHolderMahou(copiedScroll,holder);
        require(Utils.getStackHolderMahou(scroll).getStack().orElseThrow().getDamageValue()==100,"Native scroll copies isolate nested item mutations");
        var ops=world.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        var saved=ItemStack.CODEC.encodeStart(ops,scroll).getOrThrow();scroll=ItemStack.CODEC.parse(ops,saved).getOrThrow();
        require(Utils.getStackHolderMahou(scroll).getStack().orElseThrow().getDamageValue()==100,"Memorized nested item survives native save/load");
        var wire=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),world.registryAccess());
        try {ItemStack.STREAM_CODEC.encode(wire,scroll);var received=ItemStack.STREAM_CODEC.decode(wire);require(Utils.getStackHolderMahou(received).getStack().orElseThrow().getDamageValue()==100,"Memorized item survives native registry network codec");}finally{wire.release();}
        MTConfig.PROJECTION_ENCHANTMENT_BLACKLIST=List.of("minecraft:sharpness");
        ((SpellScroll)scroll.getItem()).releaseUsing(scroll,world,player,71999);
        ItemStack projected=ItemStack.EMPTY;
        for(var stack:player.getInventory()) if(ProjectionSpellEffect.getProjected(stack)) projected=stack;
        require(scroll.isEmpty() && mana.getStoredMana()==4000 && !projected.isEmpty(),"Projection consumes memorized scroll and original thousand mana");
        require(projected.getDamageValue()==projected.getMaxDamage()-5 && StrengtheningSpellEffect.getStrengthened(projected)==0 && !projected.has(net.minecraft.core.component.DataComponents.UNBREAKABLE),"Copy has original five durability and loses temporary strengthening");
        require(EnchantmentHelper.getItemEnchantmentLevel(sharpness,projected)==0 && EnchantmentHelper.getItemEnchantmentLevel(unbreaking,projected)==3 && EnchantmentHelper.getItemEnchantmentLevel(sharpness,sword)==2,"Only projected copy loses blacklisted enchantment");
        MTConfig.PROJECTION_ENCHANTMENT_BLACKLIST=List.of();MTConfig.PROJECTION_EXPLICIT_WHITELIST=List.of();StrengtheningSpellEffect.reduceStrengthened(sword,5);MTConfig.PROJECTION_BLACKLIST=List.of("minecraft");
        require(!ProjectionSpellEffect.canProject(sword,world),"Projection namespace blacklist enforced");
        MTConfig.PROJECTION_IS_BLACKLIST=false;require(ProjectionSpellEffect.canProject(sword,world),"Original whitelist inversion supported");
        MTConfig.PROJECTION_IS_BLACKLIST=true;MTConfig.PROJECTION_BLACKLIST=List.of();
        require(!ProjectionSpellEffect.canProject(new ItemStack(Items.DIRT),world),"Ordinary blocks cannot be projected");
        var left=new ItemStack(Items.DIAMOND_SWORD);var right=new ItemStack(Items.DIAMOND_SWORD);
        left.enchant(sharpness,2);right.enchant(sharpness,2);ProjectionSpellEffect.setProjected(right);
        var menu=new AnvilMenu(71,player.getInventory());menu.getSlot(0).set(left);menu.getSlot(1).set(right);menu.createResult();
        require(!menu.getSlot(2).getItem().isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(sharpness,menu.getSlot(2).getItem())==2,"Native anvil cannot increase shared enchantment using projected copy");
        MTConfig.PROJECTION_ANVIL_CANCEL=true;menu.createResult();require(menu.getSlot(2).getItem().isEmpty() && menu.getCost()==0,"Native anvil cancellation clears output and cost");MTConfig.PROJECTION_ANVIL_CANCEL=false;
        left.setDamageValue(100);menu.getSlot(0).set(left);menu.getSlot(1).set(new ItemStack(Items.DIAMOND_SWORD));menu.createResult();
        require(!menu.getSlot(2).getItem().isEmpty(),"Unprojected anvil remains functional");
        var pos=new BlockPos(510,250,515);ExchangeCircleIntegration.ritual(player,pos,"projection",true,"diamond","diamond","quartz");
        player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
        world.getBlockState(pos).useWithoutItem(world,player,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
        var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_projection")));
        require(world.getBlockState(pos).isAir() && drops.size()==1,"Original cloth ritual produces projection scroll");drops.forEach(e->e.discard());zombie.discard();player.discard();
        System.out.println("PASS: PROJECTION_INTEGRATION: strict memorization, mana/consumption, nested copy/save/network isolation, five durability, strengthening reset, enchant filters, namespace lists, native anvil limits/cancellation and ritual acquisition");
    }
}
