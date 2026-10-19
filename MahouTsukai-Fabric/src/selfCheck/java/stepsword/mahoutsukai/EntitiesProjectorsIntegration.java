package stepsword.mahoutsukai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.entity.mahoujin.*;
import stepsword.mahoutsukai.tile.*;
import stepsword.mahoutsukai.networking.*;
import stepsword.mahoutsukai.util.Utils;

public final class EntitiesProjectorsIntegration {
 private static void require(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void run(ServerPlayer fixture){
  var p=DoomIntegration.player(fixture.level(),"EntityProjectors",GameType.SURVIVAL,620);
  var world=p.level();var players=((stepsword.mahoutsukai.checks.mixin.PlayerListAccessor)world.getServer().getPlayerList()).mahoutsukai$playersByUUID();players.put(p.getUUID(),p);
  var pos=new BlockPos(620,280,620);var fogPos=pos.east();
  try{
   RemainingFamiliesIntegration.ready(p,620.5,620.5);
   var spell=stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings.Spell.RHO_AIAS;
   var colors=java.util.List.of(10,20,30,40,50,60);
   SettingsUpdatePacket.handle(new SettingsUpdatePacket(spell.ordinal(),colors),p);
   require(Utils.getSettingsMahou(p).getIntSettings(spell.ordinal()).equals(colors),"Original spell color update applies to sender");
   SettingsUpdatePacket.handle(new SettingsUpdatePacket(spell.ordinal(),java.util.List.of(300,20,30,40,50,60)),p);
   SettingsUpdatePacket.handle(new SettingsUpdatePacket(-1,colors),p);
   SettingsUpdatePacket.handle(new SettingsUpdatePacket(spell.ordinal(),java.util.List.of(1,2,3)),p);
   require(Utils.getSettingsMahou(p).getIntSettings(spell.ordinal()).equals(colors),"Invalid RGB/spell/count rejected without changing settings");
   var colorSave=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,world.registryAccess());p.saveWithoutId(colorSave);
   require(colorSave.buildResult().getCompound("mahoutsukai:settings_mahou").isPresent(),"Native player save includes spell settings");
   var circle=new AuthorityMahoujinEntity(world,p,new Vec3(0,0,1),p.getEyePosition(),1,1,.2f,1,3);
   require(world.addFreshEntity(circle),"Authority native insertion");circle.tick();
   require(circle.isAlive()&&circle.getCircleSize()==3&&circle.getCasterUUID().equals(p.getUUID()),"Authority owner/size/direction synchronized");
   circle.setLife(19);circle.tick();require(circle.isRemoved(),"Authority original 20-tick lifetime");
   var lance=new ItemStack(Items.STICK);lance.set(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT,new stepsword.mahoutsukai.datacomponents.lance.LanceMahou(9,3));
   var rhongo=new RhongomyniadEntity(world,p,1,lance);rhongo.setPos(p.position());require(world.addFreshEntity(rhongo),"Rhongomyniad native insertion");
   int before=Utils.getPlayerMahou(p).getStoredMana();
   require(RhongomyniadEntity.operate(world,p.getUUID(),pos)&&Utils.getPlayerMahou(p).getStoredMana()==before-stepsword.mahoutsukai.mana.PlayerManaManager.spellCost(stepsword.mahoutsukai.config.MTConfig.RHONGOMYNIAD_MANA_COST),"Rhongomyniad actual mana cost");
   var zombie=RemainingFamiliesIntegration.mob(p,EntityType.ZOMBIE,622.5,280,620.5);rhongo.setLife(39);rhongo.tick();
   var smites=world.getEntitiesOfClass(SmiteEntity.class,zombie.getBoundingBox().inflate(1),e->e.target==zombie);
   require(!smites.isEmpty()&&smites.getFirst().damage==9,"Rhongomyniad auto-strikes original special targets using stored lance damage");
   var cow=RemainingFamiliesIntegration.mob(p,EntityType.COW,623.5,280,620.5);
   cow.hurtServer(world,p.damageSources().mobAttack(zombie),1);
   require(world.getEntitiesOfClass(SmiteEntity.class,zombie.getBoundingBox().inflate(1),e->e.target==zombie).size()>smites.size(),"Rhongomyniad retaliation against attacker");
   var saved=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,world.registryAccess());rhongo.nocaster=true;rhongo.saveWithoutId(saved);
   var loaded=new RhongomyniadEntity(world);loaded.load(TagValueInput.create(ProblemReporter.DISCARDING,world.registryAccess(),saved.buildResult()));
   require(loaded.nocaster&&loaded.getStack().get(stepsword.mahoutsukai.datacomponents.DataComponents.LANCE_COMPONENT).getAttackDamage()==9,"Rhongomyniad stack and autonomous mode persist");
   rhongo.nocaster=false;require(rhongo.collect(p,InteractionHand.MAIN_HAND).consumesAction()&&rhongo.isRemoved()&&rhongo.getStack().isEmpty(),"Owner retrieves lance exactly once");
   zombie.discard();cow.discard();for(var smite:world.getEntitiesOfClass(SmiteEntity.class,p.getBoundingBox().inflate(12)))smite.discard();
   world.setBlockAndUpdate(pos,ModBlocks.projector.get().defaultBlockState());world.setBlockAndUpdate(fogPos,ModBlocks.fogProjector.get().defaultBlockState());
   var projector=(MahoujinProjectorTileEntity)world.getBlockEntity(pos);projector.caster=p.getUUID();
   var update=new MahoujinProjectorUpdatePacket(10,20,30,200,1,2,3,4,5,6,7,8,true,true,pos,-1,-1,.5f,2,3,4,5,6,7,1,false);
   MahoujinProjectorUpdatePacket.handle(update,p);require(projector.pv.csize==4&&projector.pv.cx==1&&projector.pv.cr==10,"Validated projector update changes real block entity");
   var tag=projector.saveWithoutMetadata(world.registryAccess());var reloaded=new MahoujinProjectorTileEntity(pos,projector.getBlockState());reloaded.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING,world.registryAccess(),tag));
   require(reloaded.pv.equals(projector.pv)&&p.getUUID().equals(reloaded.caster),"Projector settings and owner survive native block entity serialization");
   var drops=net.minecraft.world.level.block.Block.getDrops(projector.getBlockState(),world,pos,projector,p,ItemStack.EMPTY);
   require(drops.size()==1&&drops.getFirst().get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_VALUES_COMPONENT).csize==4,"Original projector loot preserves settings");
   reloaded.applyComponentsFromItemStack(drops.getFirst());reloaded.pv.csize=9;
   require(drops.getFirst().get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_VALUES_COMPONENT).csize==4,"Placed projector does not mutate saved item component");
   var armor=new ItemStack(Items.DIAMOND_CHESTPLATE);p.setItemInHand(InteractionHand.MAIN_HAND,armor);
   ProjectorRightClickPacket.handle(new ProjectorRightClickPacket(ProjectorRightClickPacket.ARMOR_ACTION.ADD,pos),p);
   ProjectorRightClickPacket.handle(new ProjectorRightClickPacket(ProjectorRightClickPacket.ARMOR_ACTION.ADD,pos),p);
   var decor=armor.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT);
   require(decor!=null&&decor.getPv().size()==2,"Native armor packet adds original decorative circles");
   projector.pv.csize=7;require(decor.getPv().getFirst().csize==4,"Armor projection snapshots are independent of source block");
   ProjectorRightClickPacket.handle(new ProjectorRightClickPacket(ProjectorRightClickPacket.ARMOR_ACTION.REPLACE,pos),p);
   require(armor.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT).getPv().size()==2&&armor.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT).getPv().getLast().csize==7,"Armor replace preserves earlier layers");
   ProjectorRightClickPacket.handle(new ProjectorRightClickPacket(ProjectorRightClickPacket.ARMOR_ACTION.REMOVE,pos),p);
   require(armor.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT).getPv().size()==1,"Armor removes last layer");
   var storedArmor=RemainingItemsIntegration.reload(armor,world);require(storedArmor.get(stepsword.mahoutsukai.datacomponents.DataComponents.PROJECTOR_ENCHANT_COMPONENT).getPv().size()==1,"Armor projection survives item serialization");
   var stick=new ItemStack(Items.STICK);p.setItemInHand(InteractionHand.MAIN_HAND,stick);p.setShiftKeyDown(false);
   var hit=new net.minecraft.world.phys.BlockHitResult(Vec3.atCenterOf(pos),net.minecraft.core.Direction.UP,pos,false);
   stepsword.mahoutsukai.fabric.ProjectorInteractions.use(p,world,InteractionHand.MAIN_HAND,hit);projector.pv.csize=10;p.setShiftKeyDown(true);
   stepsword.mahoutsukai.fabric.ProjectorInteractions.use(p,world,InteractionHand.MAIN_HAND,hit);require(projector.pv.csize==7,"Stick restores copied circle settings");p.setShiftKeyDown(false);
   projector.pv.csize=2;p.snapTo(660.5,280,620.5,0,0);MahoujinProjectorUpdatePacket.handle(update,p);require(projector.pv.csize==2,"Remote projector update rejected");p.snapTo(620.5,280,620.5,0,0);
   var nan=new MahoujinProjectorUpdatePacket(10,20,30,200,Float.NaN,2,3,4,5,6,7,8,true,true,pos,-1,-1,.5f,2,3,4,5,6,7,1,false);MahoujinProjectorUpdatePacket.handle(nan,p);require(projector.pv.csize==2,"Non-finite projector packet rejected atomically");
   projector.caster=java.util.UUID.randomUUID();MahoujinProjectorUpdatePacket.handle(update,p);require(projector.pv.csize==2,"Non-owner projector edits rejected");
   var fog=(FogProjectorTileEntity)world.getBlockEntity(fogPos);fog.pv.particle_fog_size=12;fog.pv.camera_fog_color_r=45;
   var fogUpdate=new FogProjectorUpdatePacket(fogPos,8,40,50,60,180,.1f,.01f,true,false,true,0,1,0,true,2,20,10,80,90,100,30,true);
   FogProjectorUpdatePacket.handle(fogUpdate,p);require(fog.pv.camera_fog_color_r==80&&fog.pv.particle_fog_frequency_num==2,"Validated native fog update");
   fogUpdate.particle_fog_thickness=Float.NaN;FogProjectorUpdatePacket.handle(fogUpdate,p);require(fog.pv.particle_fog_thickness==.1f,"Non-finite fog update rejected");
   var fogTag=fog.saveWithoutMetadata(world.registryAccess());var loadedFog=new FogProjectorTileEntity(fogPos,fog.getBlockState());loadedFog.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING,world.registryAccess(),fogTag));require(loadedFog.pv.equals(fog.pv),"Fog settings persist through native serialization");
   var wire=new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());try{update.encode(wire);var decoded=MahoujinProjectorUpdatePacket.STREAM_CODEC.decode(wire);require(!wire.isReadable(),"Original projector wire consumed exactly");projector.caster=p.getUUID();MahoujinProjectorUpdatePacket.handle(decoded,p);require(projector.pv.csize==4,"Projector wire roundtrip applies actual settings");}finally{wire.release();}
   System.out.println("PASS: ENTITIES_PROJECTORS: Authority lifetime, Rhongomyniad targeting/native damage retaliation/mana/retrieval/persistence, projector loot/persistence/wire/range/ownership/NaN validation, armor add/replace/remove and stick copy/restore");
  }finally{world.removeBlock(pos,false);world.removeBlock(fogPos,false);players.remove(p.getUUID());p.discard();}
 }
}
