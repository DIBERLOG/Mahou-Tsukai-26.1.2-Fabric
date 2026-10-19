package stepsword.mahoutsukai;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity;
import stepsword.mahoutsukai.item.ModItems;

/** Focused checks for the deliberately raised Nobu damage and safe existing-config upgrade. */
public final class Nobu031Checks {
    private static void require(boolean value,String message){if(!value)throw new AssertionError(message);}

    public static void run(ServerPlayer fixture)throws Exception {
        configuration();
        double configuredDamage=MTConfig.NOBU_BULLET_DAMAGE;
        var world=fixture.level();
        var caster=DoomIntegration.player(world,"Nobu031",GameType.SURVIVAL,552);
        caster.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(ModItems.nobu.get()));
        try {
            MTConfig.NOBU_BULLET_DAMAGE=12;
            hit(caster,12,false);
            hit(caster,12,true);
            MTConfig.NOBU_BULLET_DAMAGE=17.5;
            hit(caster,17.5,false);
        } finally {
            MTConfig.NOBU_BULLET_DAMAGE=configuredDamage;
            world.removePlayerImmediately(caster,Entity.RemovalReason.DISCARDED);
        }
        modeSwitch(world);
        System.out.println("PASS: NOBU_031: default12, one-time8-to12 upgrade/backup, custom and later8 overrides preserved, bounds and native manual/autonomous beam hits, owner-only construct cleanup on mode switch");
    }

    /** Switching mode must drop only this owner's constructs, immediately and in both directions. */
    private static void modeSwitch(net.minecraft.server.level.ServerLevel world){
        var owner=DoomIntegration.player(world,"NobuModeOwner",GameType.SURVIVAL,552);
        var bystander=DoomIntegration.player(world,"NobuModeOther",GameType.SURVIVAL,560);
        try {
            var nobu=(stepsword.mahoutsukai.item.nobu.Nobu)ModItems.nobu.get();
            owner.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(nobu));
            bystander.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(nobu));
            stepsword.mahoutsukai.util.Utils.setMode(owner.getMainHandItem(),2);
            var ownerStorage=stepsword.mahoutsukai.item.nobu.Nobu.getStorage(owner);
            var ownGun=new stepsword.mahoutsukai.entity.mahoujin.GunEntity(world,owner,1,0,0,1,1,1,1,1);
            require(world.addFreshEntity(ownGun),"Owner construct inserted");
            ownerStorage.guns.put(0,ownGun);
            var otherStorage=stepsword.mahoutsukai.item.nobu.Nobu.getStorage(bystander);
            var otherGun=new stepsword.mahoutsukai.entity.mahoujin.GunEntity(world,bystander,0,1,0,1,1,1,1,1);
            require(world.addFreshEntity(otherGun),"Bystander construct inserted");
            otherStorage.guns.put(0,otherGun);
            int otherMode=stepsword.mahoutsukai.util.Utils.getMode(bystander.getMainHandItem());

            owner.setShiftKeyDown(true);
            nobu.use(world,owner,InteractionHand.MAIN_HAND);
            owner.setShiftKeyDown(false);
            require(ownGun.isRemoved()&&ownerStorage.guns.isEmpty(),"Mode switch immediately removes the owner's previous constructs");
            require(!otherGun.isRemoved()&&otherStorage.guns.size()==1,"Mode switch leaves other players' constructs untouched");
            require(stepsword.mahoutsukai.util.Utils.getMode(owner.getMainHandItem())==3,"Mode advances from circle to line");
            require(stepsword.mahoutsukai.util.Utils.getMode(bystander.getMainHandItem())==otherMode,"Bystander mode is unchanged");

            owner.setShiftKeyDown(true);
            nobu.use(world,owner,InteractionHand.MAIN_HAND);
            owner.setShiftKeyDown(false);
            require(stepsword.mahoutsukai.util.Utils.getMode(owner.getMainHandItem())==0,"Mode cycles back to automatic in the other direction");
            require(!otherGun.isRemoved(),"Repeated switch still ignores other players");
        } finally {
            stepsword.mahoutsukai.item.nobu.Nobu.clearNobuStorage(owner);
            stepsword.mahoutsukai.item.nobu.Nobu.clearNobuStorage(bystander);
            stepsword.mahoutsukai.item.nobu.Nobu.player_storage.remove(owner.getUUID());
            stepsword.mahoutsukai.item.nobu.Nobu.player_storage.remove(bystander.getUUID());
            world.removePlayerImmediately(owner,Entity.RemovalReason.DISCARDED);
            world.removePlayerImmediately(bystander,Entity.RemovalReason.DISCARDED);
        }
    }

    private static void hit(ServerPlayer caster,double expected,boolean autonomous){
        var target=RemainingFamiliesIntegration.mob(caster,EntityType.COW,556,280,512);
        var beam=new GunBeamProjectileEntity(caster.level());
        try {
            target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);target.setHealth(100);target.invulnerableTime=0;
            beam.setCasterUUID(caster.getUUID());beam.autonomous=autonomous;beam.setPos(554,280,512);
            require(caster.level().addFreshEntity(beam),"Native Nobu projectile insertion");
            require(caster.getMainHandItem().getEnchantments().isEmpty(),"Nobu damage baseline must be unenchanted");
            beam.onHit(new EntityHitResult(target));
            require(Math.abs(100-target.getHealth()-expected)<.001,"Native Nobu hit applies "+expected+" magic damage; autonomous="+autonomous+", actual="+(100-target.getHealth()));
        } finally {target.discard();beam.discard();}
    }

    private static void configuration()throws Exception {
        require(MTConfig.defaultSettings().get("NOBU_BULLET_DAMAGE").getAsDouble()==12,"Fresh Nobu damage default is12");
        JsonObject saved=MTConfig.serverSettings();
        Path dir=Files.createTempDirectory(Path.of("."),"nobu-031-config-");
        try {
            Path fresh=dir.resolve("fresh.json");MTConfig.load(fresh);
            require(MTConfig.NOBU_BULLET_DAMAGE==12&&read(fresh).get("NOBU_BULLET_DAMAGE").getAsDouble()==12,"New config writes and loads12");
            Path old=dir.resolve("old.json");String original="{\"NOBU_BULLET_DAMAGE\":8,\"NOBU_POWER_DAMAGE_FACTOR\":0.7}\n";Files.writeString(old,original);MTConfig.load(old);
            require(MTConfig.NOBU_BULLET_DAMAGE==12&&MTConfig.NOBU_POWER_DAMAGE_FACTOR==.7,"Old damage8 upgrades without changing unrelated customization");
            Path backup=dir.resolve("old.json.before-nobu-0.31");require(Files.readString(backup).equals(original),"Migration backup preserves original bytes");
            var settings=read(old);settings.addProperty("NOBU_BULLET_DAMAGE",8);Files.writeString(old,settings.toString());MTConfig.load(old);
            require(MTConfig.NOBU_BULLET_DAMAGE==8&&Files.readString(backup).equals(original),"Migration marker preserves later deliberate8 and original backup");
            Path custom=dir.resolve("custom.json");Files.writeString(custom,"{\"NOBU_BULLET_DAMAGE\":17.5}");MTConfig.load(custom);
            require(MTConfig.NOBU_BULLET_DAMAGE==17.5&&!Files.exists(dir.resolve("custom.json.before-nobu-0.31")),"Initial custom damage is preserved without unnecessary backup");
            for(double allowed:new double[]{0,10000000}){var values=new JsonObject();values.addProperty("NOBU_BULLET_DAMAGE",allowed);MTConfig.applyServerSettings(values);require(MTConfig.NOBU_BULLET_DAMAGE==allowed,"Configured boundary accepted");}
            for(double invalid:new double[]{-1,10000001}){double before=MTConfig.NOBU_BULLET_DAMAGE;var values=new JsonObject();values.addProperty("NOBU_BULLET_DAMAGE",invalid);boolean rejected=false;try{MTConfig.applyServerSettings(values);}catch(IllegalArgumentException expected){rejected=true;}require(rejected&&MTConfig.NOBU_BULLET_DAMAGE==before,"Invalid damage rejected atomically");}
        } finally {
            MTConfig.applyServerSettings(saved);
            try(var paths=Files.list(dir)){for(Path path:paths.toList())Files.delete(path);}Files.delete(dir);
        }
    }
    private static JsonObject read(Path file)throws Exception{return JsonParser.parseString(Files.readString(file)).getAsJsonObject();}
}
