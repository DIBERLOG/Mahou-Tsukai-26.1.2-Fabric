package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.fluids.ModFluids;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.tile.projection.PowerConsolidationMahoujinTileEntity;

/** Real ritual, block entity, native fluid and persistent attachment integration. */
public final class PowerLakeIntegration {
    private static BlockPos protectedBlock;
    private static boolean registered;
    private static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static void cycle(PowerConsolidationMahoujinTileEntity circle){circle.mahoujinTick(circle.getLevel(),circle.getBlockPos(),circle.getBlockState(),circle);}
    public static void run(ServerPlayer fixture){
        var world=fixture.level();
        if(!registered){
            registered=true;
            net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((level,player,pos,state,be)->!pos.equals(protectedBlock));
        }
        var player=DoomIntegration.player(world,"PowerLakeCheck",GameType.SURVIVAL,580);
        player.snapTo(580.5,250,580.5,0,0);
        var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)world.getServer().getPlayerList()).mahoutsukai$playersByUUID();
        players.put(player.getUUID(),player);
        var mana=((MahouHolder)player).mahoutsukai$getMahou();
        mana.setMaxMana(2000);mana.setStoredMana(2000);player.getInventory().clearContent();
        int radius=MTConfig.POWER_CONSOLIDATION_LAKE_RADIUS;
        int cost=MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST;
        int interval=MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE;
        try{
            MTConfig.POWER_CONSOLIDATION_LAKE_RADIUS=5;
            MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST=30;
            MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE=20;
            var pos=new BlockPos(580,250,580);
            // Solid enclosed fixture prevents fluid from spilling into unrelated checks.
            for(var p:BlockPos.betweenClosed(pos.offset(-7,-7,-7),pos.offset(7,-1,7)))world.setBlockAndUpdate(p,Blocks.STONE.defaultBlockState());
            world.setBlockAndUpdate(pos,ModBlocks.mahoujin.get().defaultBlockState());
            for(var id:new String[]{"diamond","diamond","emerald"}){
                var powder=new ItemStack(ModItems.REGISTERED.get("powdered_"+id));
                player.setItemInHand(InteractionHand.MAIN_HAND,powder);
                world.getBlockState(pos).useItemOn(powder,world,player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
                require(powder.isEmpty(),"Lake catalyst consumed through native interaction");
            }
            require(world.getBlockEntity(pos) instanceof PowerConsolidationMahoujinTileEntity,"Diamond/diamond/emerald creates native lake ritual without cloth");
            var circle=(PowerConsolidationMahoujinTileEntity)world.getBlockEntity(pos);
            require(player.getUUID().equals(circle.getCasterUUID()) && !circle.hasCloth() && circle.blockCycle()==20,"Original caster, no cloth, twenty tick interval");
            cycle(circle);
            require(world.getBlockState(pos.below()).is(Blocks.DIRT) && mana.getStoredMana()==1985,"First original expansion creates dirt shell and spends thirty mana");
            mana.setStoredMana(0);cycle(circle);
            require(circle.saveWithoutMetadata(world.registryAccess()).getIntOr(PowerConsolidationMahoujinTileEntity.LAKED,-1)==1,"Insufficient mana pauses expansion without advancing saved stage");
            var batteryPos=pos.offset(4,0,0);
            world.setBlockAndUpdate(batteryPos,ModBlocks.circuit.get().defaultBlockState());
            var battery=(stepsword.mahoutsukai.tile.circuits.ManaCircuitTileEntity)world.getBlockEntity(batteryPos);
            battery.setOwnerUUID(player.getUUID());battery.setOn(true);battery.setMana(90);
            cycle(circle);
            require(battery.getMana()==75 && mana.getStoredMana()==0 && world.getBlockState(pos.below()).is(Blocks.DIRT),"Owned battery funds next expansion before personal mana");
            var chestPos=pos.offset(1,-1,0);world.setBlockAndUpdate(chestPos,Blocks.CHEST.defaultBlockState());
            var chest=(net.minecraft.world.level.block.entity.ChestBlockEntity)world.getBlockEntity(chestPos);chest.setItem(0,new ItemStack(Items.DIAMOND,7));
            protectedBlock=pos.offset(-1,-1,0);world.setBlockAndUpdate(protectedBlock,Blocks.GOLD_BLOCK.defaultBlockState());
            var bedrock=pos.offset(0,-2,0);world.setBlockAndUpdate(bedrock,Blocks.BEDROCK.defaultBlockState());
            cycle(circle);
            require(world.getBlockState(chestPos).is(Blocks.CHEST) && chest.getItem(0).getCount()==7,"Lake skips original block entities and preserves their inventories");
            require(world.getBlockState(protectedBlock).is(Blocks.GOLD_BLOCK) && world.getBlockState(bedrock).is(Blocks.BEDROCK),"Native claim callback and protected blocks prevent lake replacement");
            var saved=circle.saveWithoutMetadata(world.registryAccess());
            require(saved.getIntOr(PowerConsolidationMahoujinTileEntity.LAKED,-1)==3,"Original lake stage NBT preserved");
            world.removeBlockEntity(pos);var loaded=new PowerConsolidationMahoujinTileEntity(pos,ModBlocks.powerConsolidationMahoujin.get().defaultBlockState());
            loaded.loadWithComponents(TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),saved));world.setBlockEntity(loaded);
            require(loaded.getCasterUUID().equals(player.getUUID()),"Native lake reload restores caster");
            cycle(loaded);require(battery.getMana()==45,"Reloaded circle resumes from saved stage");
            cycle(loaded);require(world.getBlockState(pos).isAir(),"Final original stage removes completed ritual");
            var fluid=world.getFluidState(pos.below());
            require(fluid.isSource() && fluid.getType()==ModFluids.MURKY_STILL && fluid.is(net.minecraft.tags.FluidTags.WATER),"Native source has murky identity and water-like entity physics");
            require(ModFluids.MURKY_STILL.getTickDelay(world)==5 && ModFluids.MURKY_FLOW.getTickDelay(world)==20,"Original source five tick and flowing twenty tick delays");
            require(ModFluids.MURKY_FLOW.getFlowing(6,false).createLegacyBlock().getBlock()==ModBlocks.murkyFluid.get(),"Flowing state stays murky instead of turning into vanilla water");
            var chunk=world.getChunkAt(pos);var data=chunk.getAttachedOrCreate(ChunkMahou.TYPE);
            require(data.lakeBlocks.contains(pos.below()),"Chunk tracks generated lake blocks");
            var copy=data.copy();copy.lakeBlocks.clear();require(!data.lakeBlocks.isEmpty(),"Chunk snapshots do not share mutable position sets");
            var nbt=ChunkMahou.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,data).getOrThrow();
            var restored=ChunkMahou.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,nbt).getOrThrow();
            require(restored.lakeBlocks.equals(data.lakeBlocks) && restored.needsRetroFix==data.needsRetroFix,"Native persistent codec round trips original chunk fields");
            var wire=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),world.registryAccess());
            try{ChunkMahou.STREAM_CODEC.encode(wire,data);require(ChunkMahou.STREAM_CODEC.decode(wire).lakeBlocks.equals(data.lakeBlocks),"Tracking clients receive complete lake positions");}finally{wire.release();}
            var bucket=((net.minecraft.world.level.block.BucketPickup)ModBlocks.murkyFluid.get()).pickupBlock(player,world,pos.below(),world.getBlockState(pos.below()));
            require(bucket.is(ModItems.murkyBucket.get()) && world.getBlockState(pos.below()).isAir() && !chunk.getAttachedOrCreate(ChunkMahou.TYPE).lakeBlocks.contains(pos.below()),"Native bucket pickup returns original murky bucket and removes chunk entry");
            System.out.println("PASS: POWER_LAKE_INTEGRATION: real catalyst acquisition, original staged geometry/cost/battery priority, mana pause, containers/claims/bedrock, native stage reload/completion, custom fluid physics/timing/identity/bucket and persistent/synchronized chunk fields");
        }finally{
            MTConfig.POWER_CONSOLIDATION_LAKE_RADIUS=radius;MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST=cost;MTConfig.POWER_CONSOLIDATION_LAKE_CYCLE=interval;
            protectedBlock=null;players.remove(player.getUUID());player.discard();
        }
    }
}
