package stepsword.jousting;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import stepsword.jousting.config.JConfig;
import stepsword.jousting.item.*;

/** Native registration of the six original Jousting lances and their mounted combat. */
public final class JoustingMod implements ModInitializer {
 public static final CommonProxy proxy=new CommonProxy();
 public static java.util.function.Consumer<Player> stopUsing=Player::stopUsingItem;
 public static final class CommonProxy{public void stopUsing(Player p){JoustingMod.stopUsing.accept(p);}}
 @Override public void onInitialize(){
  LanceSettings.load();
  for(String material:new String[]{"wood","iron","gold","emerald","diamond","netherite"}){
   String prefix=material.equals("wood")?"WOODEN":material.toUpperCase(java.util.Locale.ROOT);
   var id=Identifier.fromNamespaceAndPath("jousting","lance_"+material);ItemBase.pushRegistrationId(id);
   try{var item=Registry.register(BuiltInRegistries.ITEM,id,new ItemLance(()->(int)LanceSettings.value(prefix+"_LANCE_DURABILITY"),()->LanceSettings.value(prefix+"_LANCE_DAMAGE"),()->LanceSettings.value(prefix+"_LANCE_WEIGHT")));
    net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(net.minecraft.world.item.CreativeModeTabs.COMBAT).register(entries->entries.accept(item));
   }finally{ItemBase.popRegistrationId();}
  }
  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.clientboundPlay().register(LanceSettingsPacket.TYPE,LanceSettingsPacket.CODEC);
  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->sender.sendPacket(new LanceSettingsPacket(LanceSettings.json().toString())));
  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK.register(level->{
   // Native stop/death/dismount can bypass releaseUsing; never persist a charge speed boost.
   for(var entity:level.getAllEntities())if(entity instanceof LivingEntity living){var speed=living.getAttribute(Attributes.MOVEMENT_SPEED);
    if(speed!=null && speed.hasModifier(ItemLance.speedlance) && living.getPassengers().stream().noneMatch(p->p instanceof LivingEntity rider && rider.isUsingItem() && rider.getUseItem().getItem() instanceof ItemLance))speed.removeModifier(ItemLance.speedlance);
   }
  });
 }
}
