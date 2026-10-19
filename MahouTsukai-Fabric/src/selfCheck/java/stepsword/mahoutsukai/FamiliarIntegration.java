package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.effects.familiar.SummonFamiliarSpellEffect;
import stepsword.mahoutsukai.entity.FamiliarEntity;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.ModEffects;

public final class FamiliarIntegration {
    private static void require(boolean value,String message) {if(!value)throw new AssertionError(message);}
    private static ItemStack cast(ServerPlayer player,String name) {
        var stack=new ItemStack(ModItems.REGISTERED.get("scroll_"+name));
        ((SpellScroll)stack.getItem()).releaseUsing(stack,player.level(),player,71900);return stack;
    }
    public static void run(ServerPlayer fixture) {
        var world=fixture.level();var player=DoomIntegration.player(world,"FamiliarCheck",GameType.SURVIVAL,540);
        player.snapTo(540.5,250,490.5,180,0);player.setYHeadRot(180);
        var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)world.getServer().getPlayerList()).mahoutsukai$playersByUUID();players.put(player.getUUID(),player);
        var mana=((MahouHolder)player).mahoutsukai$getMahou();mana.setMaxMana(4000);mana.setStoredMana(3000);
        require(cast(player,"summon_familiar").isEmpty(),"Summon scroll consumed");
        var familiar=SummonFamiliarSpellEffect.familiarMap.get(player.getUUID());
        require(familiar!=null && familiar.isAlive() && familiar.getOwner()==player && familiar.getMaxHealth()==10 && familiar.getFamiliarId()!=null && mana.getStoredMana()==2900,"Original familiar is tamed, has unique identity/ten health and costs two hundred mana");
        var original=familiar;cast(player,"summon_familiar");familiar=SummonFamiliarSpellEffect.familiarMap.get(player.getUUID());
        require(original.isRemoved() && familiar!=original && familiar.getFamiliarId()!=original.getFamiliarId(),"Resummoning replaces previous familiar");
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND_ORE));SummonFamiliarSpellEffect.familiarRightClick(player,familiar,InteractionHand.MAIN_HAND);
        require("minecraft:diamond_ore".equals(familiar.getSearchBlock()),"Owner can request search block");
        player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);boolean holiday=familiar.getHoliday();SummonFamiliarSpellEffect.familiarRightClick(player,familiar,InteractionHand.MAIN_HAND);
        require(familiar.getHoliday()!=holiday && familiar.getSearchBlock()==null,"Owner empty-hand interaction toggles holiday color and clears search");
        familiar.snapTo(540.5,250,480.5,0,0);int before=mana.getStoredMana();
        require(cast(player,"recall_familiar").isEmpty() && familiar.position().equals(player.position()) && mana.getStoredMana()==before-10,"Recall moves familiar to owner and costs twenty");
        familiar.snapTo(543.5,250,480.5,0,0);var ownerPos=player.position();var targetPos=familiar.blockPosition();before=mana.getStoredMana();
        require(cast(player,"swap_familiar").isEmpty() && familiar.position().equals(ownerPos) && player.position().equals(Vec3.atLowerCornerOf(targetPos)) && mana.getStoredMana()==before-20,"Swap exchanges familiar position with owner's block destination and costs forty");
        familiar.snapTo(player.position());before=mana.getStoredMana();
        require(cast(player,"familiars_garden").isEmpty() && familiar.getGardenTicks()==1200 && mana.getStoredMana()==before-100,"Original garden costs two hundred and lasts twelve hundred ticks");
        var cow=EntityType.COW.create(world,EntitySpawnReason.COMMAND);cow.snapTo(familiar.getX()+2,250,familiar.getZ(),0,0);cow.setNoAi(true);world.addFreshEntity(cow);
        var hostile=EntityType.ZOMBIE.create(world,EntitySpawnReason.COMMAND);hostile.snapTo(familiar.getX()+3,250,familiar.getZ(),0,0);hostile.setNoAi(true);world.addFreshEntity(hostile);hostile.setTarget(player);hostile.setLastHurtByMob(player);
        familiar.gardenUpdate();
        require(familiar.getGardenTicks()==1199 && cow.hasEffect(ModEffects.PEACEFUL_GARDEN) && hostile.hasEffect(ModEffects.PEACEFUL_GARDEN),"Garden pulse applies peace and decrements persistent timer");
        hostile.tick();require(hostile.getTarget()==null && hostile.getLastHurtByMob()==null,"Actual living tick clears hostility during garden");
        float health=cow.getHealth();require(!cow.hurtServer(world,player.damageSources().generic(),5) && cow.getHealth()==health,"Garden target ignores actual incoming damage");
        cow.removeEffect(ModEffects.PEACEFUL_GARDEN);hostile.invulnerableTime=0;
        require(!cow.hurtServer(world,hostile.damageSources().mobAttack(hostile),5) && cow.getHealth()==health,"Peaceful attacker cannot damage unbuffed target");
        familiar.setSearchBlock("minecraft:diamond_ore");
        var ore=familiar.blockPosition().offset(1,-1,1);world.setBlockAndUpdate(ore,Blocks.DIAMOND_ORE.defaultBlockState());
        int reporting=MTConfig.FAMILIAR_REPORT_SURROUNDINGS_TIME;MTConfig.FAMILIAR_REPORT_SURROUNDINGS_TIME=1;player.getRandom().setSeed(719L);
        for(int i=0;i<64 && !ore.equals(mana.getFamiliarInterestingBlock());i++) familiar.reportSurroundings();
        MTConfig.FAMILIAR_REPORT_SURROUNDINGS_TIME=reporting;
        require(ore.equals(mana.getFamiliarInterestingBlock()) && player.hasEffect(ModEffects.INTERESTING_BLOCK),"Familiar searches requested block and synchronizes original owner hint");
        world.setBlockAndUpdate(ore,Blocks.AIR.defaultBlockState());
        familiar.setColor(.2f,.4f,.6f);familiar.loadChunks();
        var save=TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess());familiar.saveWithoutId(save);var saved=save.buildResult();var familiarId=familiar.getFamiliarId();
        familiar.discard();SummonFamiliarSpellEffect.familiarMap.clear();
        var loaded=new FamiliarEntity(stepsword.mahoutsukai.entity.ModEntities.FAMILIAR.get(),world);loaded.load(TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),saved));world.addFreshEntity(loaded);loaded.updateMap();
        require(loaded.isAlive() && SummonFamiliarSpellEffect.familiarMap.get(player.getUUID())==loaded && loaded.getFamiliarId().equals(familiarId) && loaded.getOwner()==player && loaded.getGardenTicks()==1199 && "minecraft:diamond_ore".equals(loaded.getSearchBlock()) && Math.abs(loaded.getColor()[2]-.6f)<.0001,"Native unload/reload restores ownership, identity, map, garden timer, search and color");
        MTConfig.FAMILIAR_HEALTH=17;var configured=new FamiliarEntity(world);require(configured.getMaxHealth()==17 && configured.getHealth()==17,"World familiar health setting applies to newly spawned creature");configured.discard();MTConfig.FAMILIAR_HEALTH=10;
        require(!loaded.causeFallDamage(100,1,loaded.damageSources().fall()),"Native familiar flight remains immune to falling");
        loaded.loadChunks();MTConfig.FAMILIAR_CHUNKLOADS=false;loaded.loadChunks();MTConfig.FAMILIAR_CHUNKLOADS=true;
        String[][] recipes={{"summon_familiar","quartz"},{"recall_familiar","ender"},{"swap_familiar","emerald"},{"familiars_garden","iron"}};
        var pos=new BlockPos(540,250,495);
        for(var recipe:recipes) {
            ExchangeCircleIntegration.ritual(player,pos,recipe[0],true,"quartz","quartz",recipe[1]);player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
            world.getBlockState(pos).useWithoutItem(world,player,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
            var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_"+recipe[0])));
            require(world.getBlockState(pos).isAir() && drops.size()==1,"Original familiar ritual acquisition: "+recipe[0]);drops.forEach(e->e.discard());
        }
        var wire=io.netty.buffer.Unpooled.buffer();try {var packet=new stepsword.mahoutsukai.networking.GardenPacket(1.5,2.5,3.5);stepsword.mahoutsukai.networking.GardenPacket.STREAM_CODEC.encode(wire,packet);require(wire.readableBytes()==24 && stepsword.mahoutsukai.networking.GardenPacket.STREAM_CODEC.decode(wire).posZ==3.5,"Original garden wire format");}finally{wire.release();}
        loaded.discard();cow.discard();hostile.discard();SummonFamiliarSpellEffect.familiarMap.remove(player.getUUID());players.remove(player.getUUID());player.discard();
        System.out.println("PASS: FAMILIAR_INTEGRATION: summon/replace, owner interactions, recall/swap, original costs, garden timer/native peace damage and AI, native owner/map/save reload, chunk ticket lifecycle and four ritual acquisitions");
    }
}
