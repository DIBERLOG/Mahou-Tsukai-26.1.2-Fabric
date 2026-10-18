package stepsword.mahoutsukai;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.GameType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.util.Utils;
import stepsword.mahoutsukai.potion.ModEffects;

public final class CompatibilityIntegration {
    private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    public static void run(ServerPlayer fixture) throws Exception {
        var level=fixture.level();var player=DoomIntegration.player(level,"Compatibility",GameType.SURVIVAL,560);
        var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)level.getServer().getPlayerList()).mahoutsukai$playersByUUID();players.put(player.getUUID(),player);
        level.getChunkAt(player.blockPosition());
        var zombie=EntityType.ZOMBIE.create(level,EntitySpawnReason.COMMAND);zombie.setPos(player.position().add(2,0,0));require(level.addFreshEntity(zombie),"Native fear target inserted");
        var config=MTConfig.serverSettings();int hud=MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X;
        try {
            var chunk=level.getChunkAt(player.blockPosition());var lakePos=player.blockPosition().offset(0,20,0);
            level.setBlockAndUpdate(lakePos,stepsword.mahoutsukai.block.ModBlocks.murkyFluid.get().defaultBlockState());
            var lake=new stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou();chunk.setAttached(stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou.TYPE,lake);
            MTConfig.POWER_CONSOLIDATION_RETRO_FOG_FIX=false;stepsword.mahoutsukai.fluids.MurkyWaterBlock.repairLakeOnLoad(level,chunk);
            require(chunk.getAttachedOrCreate(stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou.TYPE).lakeBlocks.isEmpty(),"Disabled retro fog fix leaves old index unchanged");
            MTConfig.POWER_CONSOLIDATION_RETRO_FOG_FIX=true;stepsword.mahoutsukai.fluids.MurkyWaterBlock.repairLakeOnLoad(level,chunk);
            var repaired=chunk.getAttachedOrCreate(stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou.TYPE);
            require(repaired.lakeBlocks.contains(lakePos) && !repaired.needsRetroFix,"Original retro fix reconstructs old lake index and clears one-time flag");
            stepsword.mahoutsukai.fluids.MurkyWaterBlock.repairLakeOnLoad(level,chunk);
            require(chunk.getAttachedOrCreate(stepsword.mahoutsukai.dataattachments.chunks.ChunkMahou.TYPE)==repaired,"Lake repair runs once, not on every reload");
            level.removeBlock(lakePos,false);
            var mahou=Utils.getPlayerMahou(player);mahou.setMaxMana(1000);mahou.setStoredMana(1000);
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(stepsword.mahoutsukai.item.ModItems.caliburn.get()));
            stepsword.mahoutsukai.mana.PlayerManaManager.onPlayerTick(player,1);
            require(zombie.hasEffect(ModEffects.FEAR) && player.getUUID().equals(Utils.getLivingMahou(zombie).getFearPerson()),"Caliburn applies original fear to native undead");
            zombie.setTarget(player);require(zombie.getTarget()==null,"Native Mob.setTarget rejects attack under fear");
            zombie.removeEffect(ModEffects.FEAR);zombie.setTarget(player);require(zombie.getTarget()==player,"Targeting recovers after fear expires");
            mahou.setDamageExchangeUses(5);Utils.getLivingMahou(player).setFearPerson(player.getUUID());
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.MILK_BUCKET));player.startUsingItem(InteractionHand.MAIN_HAND);
            ((stepsword.mahoutsukai.checks.mixin.LivingUseInvoker)player).mahoutsukai$completeUsingItem();
            require(mahou.getDamageExchangeUses()==0 && Utils.getLivingMahou(player).getFearPerson()==null,"Native milk consumption clears both player and living magic buffs");
            MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X=77;
            var override=config.deepCopy();override.addProperty("LEY_DISTANCE",777);override.addProperty("PROJECTOR_SIZE_CAP",13);
            var packet=new stepsword.mahoutsukai.fabric.ServerSettingsPacket(override.toString());
            var wire=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),level.registryAccess());
            try{stepsword.mahoutsukai.fabric.ServerSettingsPacket.CODEC.encode(wire,packet);stepsword.mahoutsukai.fabric.ServerSettingsPacket.CODEC.decode(wire).apply();require(!wire.isReadable(),"Complete settings wire consumed exactly");}finally{wire.release();}
            require(MTConfig.LEY_DISTANCE==777 && MTConfig.PROJECTOR_SIZE_CAP==13 && MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X==77,"Server sync applies geometry and limits while preserving local HUD");
            var invalid=override.deepCopy();invalid.addProperty("LEY_DISTANCE",0);boolean rejected=false;
            try{new stepsword.mahoutsukai.fabric.ServerSettingsPacket(invalid.toString()).apply();}catch(IllegalArgumentException expected){rejected=true;}
            require(rejected && MTConfig.LEY_DISTANCE==777,"Invalid complete configuration rejected atomically");
            var commands=level.getServer().getCommands().getDispatcher();var source=level.getServer().createCommandSourceStack().withEntity(player);
            for(String name:java.util.List.of("mahoucancel","clearfictions","clearplayerfictions","mahouset","mahousummon"))require(commands.getRoot().getChild(name)!=null,"Original command registered: "+name);
            commands.execute("mahouset souls @s 12",source);require(mahou.getDeathCollectionUsesLeft()==12,"Original mahouset souls executes through dispatcher");
            commands.execute("mahoucancel",source);require(mahou.getCancelTime()==level.getGameTime(),"Original boundary cancellation timestamp applied");
            MTConfig.ONE_LIFE=true;mahou.setMaxMana(1000);mahou.setDeathCollectionUsesLeft(12);mahou.setDamageExchangeUses(8);
            player.hurtServer(level,player.damageSources().genericKill(),Float.MAX_VALUE);
            require(player.isDeadOrDying() && mahou.getMaxMana()==100 && mahou.getDeathCollectionUsesLeft()==0 && mahou.getDamageExchangeUses()==0,"ONE_LIFE resets magic through native lethal damage");
            System.out.println("PASS: COMPATIBILITY: native Caliburn fear/targeting, milk buff removal, ONE_LIFE death, five commands and complete validated server settings sync");
        } finally {MTConfig.applyServerSettings(config);MTConfig.CLIENT_GUI_MAHOU_PLACEMENT_X=hud;zombie.discard();players.remove(player.getUUID());player.discard();}
    }
}
