package stepsword.mahoutsukai;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import stepsword.mahoutsukai.fabric.ClarentGuard;
import stepsword.mahoutsukai.item.ModItems;

/** Native use lifecycle and player-persistent deadlines, with an isolated player per scenario. */
public final class Clarent043Checks {
 private static void require(boolean ok,String text){if(!ok)throw new AssertionError(text);}
 public static void run(ServerPlayer fixture)throws Exception{
  var world=fixture.level();var p=DoomIntegration.player(world,"Guard43",GameType.SURVIVAL,1830);p.setServerLevel(world);
  var sword=new ItemStack(ModItems.REGISTERED.get("clarent"));p.setItemInHand(InteractionHand.MAIN_HAND,sword);
  var update=LivingEntity.class.getDeclaredMethod("updatingUsingItem");update.setAccessible(true);
  try{
   sword.getItem().use(world,p,InteractionHand.MAIN_HAND);require(ClarentGuard.canBlock(p)&&p.getUseItemRemainingTicks()==300,"Real use starts exactly fifteen seconds of protection");
   for(int i=0;i<299;i++)update.invoke(p);
   require(p.isUsingItem()&&p.getUseItemRemainingTicks()==1,"Native item-use countdown retains its last blocking tick");
   update.invoke(p);require(!p.isUsingItem()&&!ClarentGuard.canBlock(p)&&ClarentGuard.cooldownTicks(p)==600,"The 300th native use tick ends protection and starts thirty seconds cooldown");
   var replacement=sword.copy();p.setItemInHand(InteractionHand.MAIN_HAND,replacement);replacement.getItem().use(world,p,InteractionHand.MAIN_HAND);require(!p.isUsingItem(),"Replacing the sword cannot bypass cooldown");
   var save=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess());p.saveWithoutId(save);
   var rejoined=DoomIntegration.player(world,"GuardRejoin43",GameType.SURVIVAL,1830);rejoined.setServerLevel(world);rejoined.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,world.registryAccess(),save.buildResult()));
   require(ClarentGuard.cooldownTicks(rejoined)==600&&!ClarentGuard.begin(rejoined),"A real player save/load preserves the absolute cooldown deadline");
   var respawned=DoomIntegration.player(world,"GuardRespawn43",GameType.SURVIVAL,1830);respawned.setServerLevel(world);respawned.restoreFrom(p,false);net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(p,respawned,false);require(!ClarentGuard.begin(respawned)&&ClarentGuard.cooldownTicks(respawned)==600&&respawned.getCooldowns().isOnCooldown(sword),"Native respawn event copies the deadline and restores the item cooldown");
   for(int i=0;i<599;i++)p.getCooldowns().tick();require(p.getCooldowns().isOnCooldown(sword),"Native cooldown remains active for 599 ticks");p.getCooldowns().tick();require(!p.getCooldowns().isOnCooldown(sword),"Native cooldown expires on its 600th tick");
   long now=world.getServer().overworld().getGameTime();p.setAttached(ClarentGuard.READY_AT,now+1);require(!ClarentGuard.begin(p),"One tick before the server deadline is too early");
   p.setAttached(ClarentGuard.READY_AT,now);sword.getItem().use(world,p,InteractionHand.MAIN_HAND);require(ClarentGuard.canBlock(p),"The exact ready deadline allows reuse");
   p.setAttached(ClarentGuard.ACTIVE_UNTIL,now+1);require(ClarentGuard.canBlock(p),"Last server protection tick is active");p.setAttached(ClarentGuard.ACTIVE_UNTIL,now);require(!ClarentGuard.canBlock(p),"Protection ends at the exact deadline before damage is processed");ClarentGuard.tick(p);require(!p.isUsingItem()&&ClarentGuard.cooldownTicks(p)==600,"Server deadline stops the native stance");
   p.setAttached(ClarentGuard.READY_AT,now);p.getCooldowns().removeCooldown(p.getCooldowns().getCooldownGroup(sword));sword.getItem().use(world,p,InteractionHand.MAIN_HAND);
   p.releaseUsingItem();require(!p.isUsingItem()&&ClarentGuard.cooldownTicks(p)==600,"Early release immediately starts the full thirty-second cooldown");
   p.setAttached(ClarentGuard.READY_AT,now);p.getCooldowns().removeCooldown(p.getCooldowns().getCooldownGroup(sword));sword.getItem().use(world,p,InteractionHand.MAIN_HAND);p.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);ClarentGuard.tick(p);require(!ClarentGuard.canBlock(p)&&ClarentGuard.cooldownTicks(p)==600,"Changing weapons ends protection and starts cooldown");
   System.out.println("PASS: CLARENT_043: native 300-tick guard / 600-tick cooldown, exact deadline boundaries, early release, sword replacement, real NBT reload, respawn persistence and weapon switching");
  }finally{world.removePlayerImmediately(p,net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);}
 }
}
