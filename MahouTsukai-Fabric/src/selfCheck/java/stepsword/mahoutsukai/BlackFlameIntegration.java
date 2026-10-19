package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.block.BlackFireBlock;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.fabric.MahouHolder;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.potion.BlackFlameEyesPotion;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.util.Utils;

public final class BlackFlameIntegration {
    private static Cow burning;
    private static ServerPlayer owner;
    private static boolean verified,damageVerified;
    private static void require(boolean value,String message) {if(!value)throw new AssertionError(message);}
    public static void run(ServerPlayer fixture) {
        var world=fixture.level();owner=DoomIntegration.player(world,"BlackFlameCheck",GameType.SURVIVAL,470);
        owner.snapTo(470.5,250,490.5,180,12);owner.setYHeadRot(180);
        var mana=((MahouHolder)owner).mahoutsukai$getMahou();mana.setMaxMana(1000);mana.setStoredMana(1000);
        world.getEntities(owner,owner.getBoundingBox().inflate(5)).forEach(e->e.discard());
        var scroll=new ItemStack(ModItems.REGISTERED.get("scroll_black_flame"));((SpellScroll)scroll.getItem()).releaseUsing(scroll,world,owner,71900);
        require(scroll.isEmpty() && mana.getStoredMana()==850 && owner.getEffect(ModEffects.BLACK_FLAME_EYES).getDuration()==100,"Black flame eyes cost three hundred and last one hundred ticks");
        burning=EntityType.COW.create(world,EntitySpawnReason.COMMAND);burning.snapTo(470.5,250,486.5,0,0);burning.setNoAi(true);burning.setNoGravity(true);world.addFreshEntity(burning);
        BlackFlameEyesPotion.blackFlameEyesPlayerTick(owner);
        require(burning.hasEffect(ModEffects.BLACK_BURNING) && burning.getEffect(ModEffects.BLACK_BURNING).getDuration()==180,"Strict gaze applies original black burning debuff");
        burning.snapTo(476.5,250,486.5,0,0);owner.setXRot(0);
        var stone=new BlockPos(470,251,482);var firePos=stone.south();world.setBlockAndUpdate(stone,Blocks.STONE.defaultBlockState());world.setBlockAndUpdate(firePos.below(),Blocks.STONE.defaultBlockState());world.setBlockAndUpdate(firePos,Blocks.AIR.defaultBlockState());
        BlackFlameEyesPotion.blackFlameEyesPlayerTick(owner);
        var fire=(BlackFireBlock)ModBlocks.blackFireBlock.get();
        require(world.getBlockState(firePos).is(fire),"Original interpolated gaze places black fire adjacent to hit face");
        require(mana.getPlayerPrevEyePos()!=null && mana.getPlayerPrevYaw()==180,"Eye sweep remembers view and eye position");
        world.setBlockAndUpdate(firePos,Blocks.AIR.defaultBlockState());
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((level,player,pos,state,tile)->!pos.equals(firePos));
        BlackFlameEyesPotion.blackFlameEyesPlayerTick(owner);require(world.getBlockState(firePos).isAir(),"Claim cancellation blocks gaze ignition");
        owner.removeEffect(ModEffects.BLACK_FLAME_EYES);require(mana.getPlayerPrevEyePos()==null && mana.getPlayerPrevYaw()==0 && mana.getPlayerPrevPitch()==0,"Native effect removal resets gaze sweep state");
        var test=new BlockPos(473,250,490);world.setBlockAndUpdate(test,Blocks.STONE.defaultBlockState());
        require(fire.canCatchFire(world,test,Direction.UP),"Black flame attachment accepts original non-vanilla-flammable stone");
        world.setBlockAndUpdate(test,Blocks.BEDROCK.defaultBlockState());require(!fire.canCatchFire(world,test,Direction.UP),"Protected block excluded");
        world.setBlockAndUpdate(test,Blocks.CHEST.defaultBlockState());require(!fire.canCatchFire(world,test,Direction.UP),"Block entities excluded");
        world.setBlockAndUpdate(test,Blocks.WATER.defaultBlockState());require(!fire.canCatchFire(world,test,Direction.UP),"Fluids excluded");
        world.setBlockAndUpdate(test,Blocks.STONE.defaultBlockState());var flame=test.above();world.setBlockAndUpdate(flame,fire.defaultBlockState());
        for(int i=0;i<8 && world.getBlockState(flame).is(fire);i++) fire.tick(world.getBlockState(flame),world,flame,world.getRandom());
        require(world.getBlockState(flame).isAir() && world.getBlockState(test).is(Blocks.STONE),"Original death-age expiry preserves supporting stone");
        world.setBlockAndUpdate(test,Blocks.NETHERRACK.defaultBlockState());world.setBlockAndUpdate(flame,fire.defaultBlockState().setValue(BlackFireBlock.AGE,15));
        fire.tick(world.getBlockState(flame),world,flame,world.getRandom());require(world.getBlockState(flame).is(fire),"Netherrack can sustain terminal-age flame without an invalid sixteenth state");
        world.setRainLevel(1);fire.tick(world.getBlockState(flame),world,flame,world.getRandom());require(world.getBlockState(flame).isAir(),"Rain extinguishes original black flame");world.setRainLevel(0);
        world.setBlockAndUpdate(flame,fire.defaultBlockState());var touched=EntityType.COW.create(world,EntitySpawnReason.COMMAND);touched.snapTo(Vec3.atCenterOf(flame));
        world.getBlockState(flame).entityInside(world,flame,touched,net.minecraft.world.entity.InsideBlockEffectApplier.NOOP,false);
        require(touched.hasEffect(ModEffects.BLACK_BURNING),"Native block contact applies black burning");touched.discard();world.setBlockAndUpdate(flame,Blocks.AIR.defaultBlockState());
        owner.addEffect(new net.minecraft.world.effect.MobEffectInstance(ModEffects.BLACK_FLAME_EYES,2));mana.setPlayerPrevYaw(1);mana.setPlayerPrevPitch(2);mana.setPlayerPrevEyePos(owner.getEyePosition());
        var ritualPos=new BlockPos(470,250,495);ExchangeCircleIntegration.ritual(owner,ritualPos,"black_flame",true,"eye","eye","diamond");owner.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);
        world.getBlockState(ritualPos).useWithoutItem(world,owner,new BlockHitResult(Vec3.atCenterOf(ritualPos),Direction.UP,ritualPos,false));
        var drops=world.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(ritualPos).inflate(2),e->e.getItem().is(ModItems.REGISTERED.get("scroll_black_flame")));
        require(world.getBlockState(ritualPos).isAir() && drops.size()==1,"Original eyes/diamond cloth ritual produces scroll");drops.forEach(e->e.discard());
        var wire=io.netty.buffer.Unpooled.buffer();try {var packet=new stepsword.mahoutsukai.networking.BlackFirePacket(burning.getId(),true);stepsword.mahoutsukai.networking.BlackFirePacket.STREAM_CODEC.encode(wire,packet);require(wire.readableBytes()==5 && stepsword.mahoutsukai.networking.BlackFirePacket.STREAM_CODEC.decode(wire).onFire,"Original black fire five-byte codec preserved");}finally{wire.release();}
        System.out.println("PASS: BLACK_FLAME_INTEGRATION: original cost/eye targeting, sweep ignition, claim cancellation, protected block/container/fluid exclusions, fire aging/netherrack/rain, contact debuff, removal reset, ritual and wire format");
    }
    public static boolean verifyNativeTicks() {
        if(verified)return true;
        // Synthetic players have no live network listener to invoke doTick; use the actual native player tick.
        owner.doTick();
        if(!damageVerified && burning.level().getGameTime()%20==0) {
            require(burning.getHealth()==9,"Actual native living tick inflicts original one damage every second");
            burning.discard();burning=null;damageVerified=true;
        }
        if(!damageVerified || owner.hasEffect(ModEffects.BLACK_FLAME_EYES))return false;
        require(Utils.getPlayerMahou(owner).getPlayerPrevEyePos()==null,"Natural expiration invokes native extra-potion removal callback");
        owner.discard();owner=null;verified=true;
        System.out.println("PASS: BLACK_FLAME_NATIVE_TICK: periodic wither damage and natural gaze expiration reset");return true;
    }
}
