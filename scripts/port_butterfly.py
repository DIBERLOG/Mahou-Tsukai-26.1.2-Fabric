from remaining_port_helpers import *
ritual('item/spells/familiar/ButterflyEffectSpellScroll.java','block/spells/familiar/ButterflyEffectMahoujin.java','tile/familiar/ButterflyEffectMahoujinTileEntity.java','butterflyEffectMahoujin')
for cls in ['SerializableBlockPlace','SerializableBlockBreak','SerializableHit']:copy('entity/butterfly/'+cls+'.java')
entity('BUTTERFLY','entity/butterfly/ButterflyEntity.java','butterfly_entity',.2,.2)
p=main/'entity/butterfly/ButterflyEntity.java';s=p.read_text(encoding='utf-8');s='\n'.join(l for l in s.splitlines() if not l.startswith('import net.neoforged.') and not l.startswith('import stepsword.mahoutsukai.MahouTsukaiMod') and not l.startswith('import stepsword.mahoutsukai.handlers.ServerHandler'));s=s.replace('FakePlayer','net.fabricmc.fabric.api.entity.FakePlayer').replace('Safenet.fabricmc.fabric.api.entity.FakePlayer','SafeFakePlayer');s=s.replace('ServerHandler.tickCounter','this.level().getGameTime()');a=s.index('   public void attack(');b=s.index('   public boolean triggerEffect',a);s=s[:a]+'''   public void attack(net.fabricmc.fabric.api.entity.FakePlayer player,Entity target){player.attack(target);}
'''+s[b:];a=s.index('   public boolean isInvisible()');b=s.index('   public boolean isInvisibleTo',a);s=s[:a]+s[b:];s=s.replace('ModEffects.FORESIGHT.get()','ModEffects.FORESIGHT').replace('return ((Optional)this.entityData.get(CASTER)).orElse(UUID.randomUUID());','return this.entityData.get(CASTER).orElse(null);').replace('if (effectIdx <','if (effectIdx >=0 && effectIdx <');s=s.replace('      output.store("MAHOUTSUKAI_CASTER", UUIDUtil.CODEC, this.getCasterUUID());','      output.storeNullable("MAHOUTSUKAI_CASTER", UUIDUtil.CODEC, this.getCasterUUID());');s=s.replace('      this.setDying(input.getBooleanOr', '      input.read("MAHOUTSUKAI_ORIGIN",BlockPos.CODEC).ifPresent(pos->{this.origPos=pos;this.origPos1=pos.offset(1,1,1);});\n      this.setDying(input.getBooleanOr');s=s.replace('      output.putInt("MAHOUTSUKAI_EFFECT",','      output.storeNullable("MAHOUTSUKAI_ORIGIN",BlockPos.CODEC,this.origPos);\n      output.putInt("MAHOUTSUKAI_EFFECT",');write(p,s)
p=main/'entity/butterfly/SerializableHit.java';s=p.read_text(encoding='utf-8').replace('ItemStack.CODEC','ItemStack.OPTIONAL_CODEC');write(p,s)
p=main/'entity/ModEntityDataSerializers.java';insert(p,' public static void init()', ''' public static final EntityDataSerializer<java.util.Optional<java.util.UUID>> OPTIONAL_UUID=EntityDataSerializer.forValueType(net.minecraft.core.UUIDUtil.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs::optional));
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","optional_uuid"),OPTIONAL_UUID);}
''')
p=main/'potion/ForesightPotion.java';write(p,'''package stepsword.mahoutsukai.potion;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.butterfly.ButterflyEntity;
public class ForesightPotion extends net.minecraft.world.effect.MobEffect {
 protected ForesightPotion(){super(net.minecraft.world.effect.MobEffectCategory.NEUTRAL,ModEffects.getColorNumber(252,45,45));}
 public static boolean checkBlockAndChunk(BlockPos pos,Level level){
  if(MTConfig.BUTTERFLY_EFFECT_BLOCK_LIMIT<=0 || MTConfig.BUTTERFLY_EFFECT_CHUNK_LIMIT<=0)return false;
  var cp=new ChunkPos(pos);var all=level.getEntitiesOfClass(ButterflyEntity.class,new AABB(cp.getMinBlockX(),level.getMinY(),cp.getMinBlockZ(),cp.getMaxBlockX()+1,level.getMaxY(),cp.getMaxBlockZ()+1));
  return all.size()<MTConfig.BUTTERFLY_EFFECT_CHUNK_LIMIT && all.stream().filter(e->pos.equals(e.origPos)).count()<MTConfig.BUTTERFLY_EFFECT_BLOCK_LIMIT;
 }
 private static ButterflyEntity butterfly(Player p){return new ButterflyEntity(p.level(),p,p.getRandom().nextInt(6000000),p.getRandom().nextFloat()*.5f+.2f);}
 private static boolean add(Player p,ButterflyEntity b){if(!checkBlockAndChunk(b.origPos,p.level()) || !p.level().addFreshEntity(b))return false;stepsword.mahoutsukai.advancements.ModTriggers.BUTTERFLY.trigger((ServerPlayer)p);if(MTConfig.BUTTERFLY_EFFECT_SINGLE_USE)p.removeEffect(ModEffects.FORESIGHT);return true;}
 public static boolean deferPlace(Player p,BlockPos pos,Direction face,BlockState state,ItemStack consumed){if(p==null || p.level().isClientSide() || !p.hasEffect(ModEffects.FORESIGHT) || state.hasBlockEntity())return false;var b=butterfly(p);b.setPlaceEffect(pos,face,state,consumed);return add(p,b);}
 public static boolean deferBreak(Level world,Player p,BlockPos pos,BlockState state){if(world.isClientSide() || !p.hasEffect(ModEffects.FORESIGHT) || state.isAir() || state.hasBlockEntity())return false;var hit=p.pick(8,1,false);if(!(hit instanceof BlockHitResult r))return false;var b=butterfly(p);b.setBreakEffect(pos,r.getDirection(),state);return add(p,b);}
 public static void butterflyHit(Player p){if(p==null || p.level().isClientSide() || p.isSpectator() || p.hasEffect(ModEffects.BOUND) || !p.hasEffect(ModEffects.FORESIGHT))return;var b=butterfly(p);b.setHitEffect(p,p.getMainHandItem(),p.getOffhandItem());add(p,b);}
}
''')
insert(main/'potion/ModEffects.java',' public static void init()', ' public static final Holder<MobEffect> FORESIGHT=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","foresight"),new ForesightPotion());\n')
p=main/'util/EffectUtil.java';append(p,''' public static boolean tryChangeBlockState(boolean killTE,net.minecraft.core.BlockPos pos,net.minecraft.world.level.block.state.BlockState state,Level world,net.minecraft.world.entity.player.Player owner,java.util.UUID ownerId){
  if(!(world instanceof net.minecraft.server.level.ServerLevel level) || unchangableBlock(world.getBlockState(pos).getBlock()) || !killTE && world.getBlockEntity(pos)!=null || stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects(level,pos.getCenter()))return false;
  var actor=owner!=null?owner:stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNotExists(level,"faker",ownerId);
  if(!world.mayInteract(actor,pos) || !net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world,actor,pos,world.getBlockState(pos),world.getBlockEntity(pos)))return false;
  return world.setBlockAndUpdate(pos,state);
 }
''')
insert(main/'fabric/MahouFabric.java','        stepsword.mahoutsukai.entity.ModEntityDataSerializers.init();', '        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world,player,pos,state,be)->!stepsword.mahoutsukai.potion.ForesightPotion.deferBreak(world,player,pos,state));\n')
insert(main/'advancements/ModTriggers.java',' public static void init()', ' public static final MahouTrigger BUTTERFLY=Registry.register(BuiltInRegistries.TRIGGER_TYPES,Identifier.fromNamespaceAndPath("mahoutsukai","butterfly"),new MahouTrigger());\n')
p=copy('networking/ButterflyHitPacket.java');s=p.read_text(encoding='utf-8');s=s.replace('import net.neoforged.neoforge.network.handling.IPayloadContext;','');a=s.index('   public static void handle(');b=s.index('   @Override',a);s=s[:a]+s[b:];s=s.replace('      return MahouPackets.BUTTERFLY_HIT_TYPE;','      return TYPE;');s=s.replace('public class ButterflyHitPacket implements CustomPacketPayload {','public class ButterflyHitPacket implements CustomPacketPayload {\n   public static final Type<ButterflyHitPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","butterfly_hit"));');write(p,s)
insert(main/'fabric/MahouNetworking.java','        PayloadTypeRegistry.serverboundPlay()', '''        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.ButterflyHitPacket.TYPE,stepsword.mahoutsukai.networking.ButterflyHitPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.ButterflyHitPacket.TYPE,(packet,context)->{if(!RemoteViewActions.leave(context.player()))stepsword.mahoutsukai.potion.ForesightPotion.butterflyHit(context.player());});
''')
p=client/'fabric/mixin/RemoteViewActionMixin.java';s=p.read_text(encoding='utf-8').replace('  if(p!=null && mc.hitResult!=null','  if(p!=null && p.hasEffect(stepsword.mahoutsukai.potion.ModEffects.FORESIGHT) && mc.hitResult!=null && mc.hitResult.getType()==net.minecraft.world.phys.HitResult.Type.MISS)net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.ButterflyHitPacket());\n  if(p!=null && mc.hitResult!=null');write(p,s)
write(main/'fabric/mixin/ButterflyPlacementMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
@Mixin(BlockItem.class)
public abstract class ButterflyPlacementMixin {
 @WrapMethod(method="place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;")
 private InteractionResult mahoutsukai$place(BlockPlaceContext context,Operation<InteractionResult> original){
  var p=context.getPlayer();var world=context.getLevel();var pos=context.getClickedPos();var before=world.getBlockState(pos);var consumed=context.getItemInHand().copyWithCount(1);
  boolean foresight=p!=null && !world.isClientSide() && p.hasEffect(stepsword.mahoutsukai.potion.ModEffects.FORESIGHT);
  var result=original.call(context);var placed=world.getBlockState(pos);
  if(foresight && result.consumesAction() && !before.equals(placed) && stepsword.mahoutsukai.potion.ForesightPotion.deferPlace(p,pos,context.getClickedFace(),placed,consumed))world.setBlockAndUpdate(pos,before);
  return result;
 }
}
''')
import json
p=res/'mahoutsukai.fabric.mixins.json';d=json.loads(p.read_text());d['mixins'].append('ButterflyPlacementMixin');write(p,json.dumps(d,indent=2)+'\n')
config()
