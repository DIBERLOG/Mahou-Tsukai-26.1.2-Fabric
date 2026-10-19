package stepsword.mahoutsukai.fabric;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.item.spells.SpellScroll;
import stepsword.mahoutsukai.util.Utils;
/** Shared charge timing, previews and feedback for direct scrolls and Mystic Code. */
public final class SpellCasting {
 private static final java.util.Set<String> WEAPON_SCROLLS=java.util.Set.of("scroll_presence_concealment","scroll_mystic_staff","scroll_weapon_shooter","scroll_treasury_projection","scroll_proximity_projection","scroll_spatial_disorientation","scroll_probability_alter","scroll_projection");
 private static final java.util.Map<java.util.UUID,stepsword.mahoutsukai.entity.mahoujin.GandrEntity> DOOM_CIRCLES=new java.util.HashMap<>();
 public static String id(ItemStack stack){return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();}
 public static boolean summonsWeapon(ItemStack stack){return WEAPON_SCROLLS.contains(id(stack));}
 public static boolean accepts(ItemStack stack){return stack.getItem() instanceof SpellScroll && !summonsWeapon(stack);}
 public static ItemStack active(ItemStack held){if(held.getItem() instanceof stepsword.mahoutsukai.item.mysticcode.MysticCode){var inv=Utils.getMCInventory(held);return inv==null?ItemStack.EMPTY:inv.getItem(stepsword.mahoutsukai.item.mysticcode.MysticCode.getSlot(held));}return held;}
 public static int requiredTicks(ItemStack spell){if(spell.getItem() instanceof stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff)return stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff.chargeTicks(spell);return switch(id(spell)){case "scroll_doom"->60;case "scroll_gandr"->52;case "scroll_fallen_down"->52;default->summonsWeapon(spell)?40:20;};}
 public static void cancel(Player player){var circle=DOOM_CIRCLES.remove(player.getUUID());if(circle!=null)circle.discard();}
 public static void clear(){DOOM_CIRCLES.values().forEach(net.minecraft.world.entity.Entity::discard);DOOM_CIRCLES.clear();}
 public static void tick(net.minecraft.world.level.Level level,LivingEntity user,ItemStack spell,int remaining){
  if(!(level instanceof net.minecraft.server.level.ServerLevel world)||!(user instanceof Player player))return;
  int elapsed=player.getTicksUsingItem(),needed=requiredTicks(spell);boolean doom=id(spell).equals("scroll_doom");
  if(doom && !DOOM_CIRCLES.containsKey(player.getUUID())){var circle=new stepsword.mahoutsukai.entity.mahoujin.GandrEntity(level,player,0.6F,0.1F,1,1,0.8F,0.4F,1);circle.setLookPos(false,player,circle);if(level.addFreshEntity(circle))DOOM_CIRCLES.put(player.getUUID(),circle);}
  var style=SpellPresentation.of(spell.getItem());
  if(elapsed==1 || elapsed%40==1 && elapsed<needed)level.playSound(null,player.blockPosition(),stepsword.mahoutsukai.sounds.ModSounds.spellCast(spell.getItem(),false),net.minecraft.sounds.SoundSource.PLAYERS,0.55F,style.pitch());
  if(elapsed==needed)level.playSound(null,player.blockPosition(),net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME,net.minecraft.sounds.SoundSource.PLAYERS,0.25F,style.pitch()*1.35F);
 }
 public static void completed(Player player,net.minecraft.world.item.Item spell){cancel(player);present(player,spell,player.position());}
 public static void present(Player player,net.minecraft.world.item.Item spell,net.minecraft.world.phys.Vec3 position){
  if(!(player.level() instanceof net.minecraft.server.level.ServerLevel world))return;
  var style=SpellPresentation.of(spell);
  world.playSound(null,position.x,position.y,position.z,stepsword.mahoutsukai.sounds.ModSounds.spellCast(spell,true),net.minecraft.sounds.SoundSource.PLAYERS,0.8F,style.pitch());
  var packet=new stepsword.mahoutsukai.networking.SpellVisualPacket(position,style);
  for(var viewer:world.players())if(viewer==player||viewer.position().distanceToSqr(position)<48*48)stepsword.mahoutsukai.networking.PacketHandler.sendTo(viewer,packet);
 }
}
