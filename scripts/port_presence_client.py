from pathlib import Path
import json,shutil
base=Path('MahouTsukai-Fabric/src');main=base/'main/java/stepsword/mahoutsukai';client=base/'client/java/stepsword/mahoutsukai';res=base/'main/resources'
def put(rel,s,cl=False):
 p=(client if cl else main)/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
put('fabric/PresenceRenderState.java','''package stepsword.mahoutsukai.fabric;
public interface PresenceRenderState {boolean mahoutsukai$isConcealed();void mahoutsukai$setConcealed(boolean value);}
''',True)
put('fabric/mixin/PresenceStateMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import org.spongepowered.asm.mixin.*;
@Mixin(net.minecraft.client.renderer.entity.state.LivingEntityRenderState.class)
public abstract class PresenceStateMixin implements stepsword.mahoutsukai.fabric.PresenceRenderState {
 @Unique private boolean mahoutsukai$concealed;
 public boolean mahoutsukai$isConcealed(){return mahoutsukai$concealed;}
 public void mahoutsukai$setConcealed(boolean value){mahoutsukai$concealed=value;}
}
''',True)
put('fabric/mixin/PresenceRendererMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LivingEntityRenderer.class)
public abstract class PresenceRendererMixin {
 @Inject(method="extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",at=@At("TAIL"))
 private void mahoutsukai$extract(LivingEntity entity,LivingEntityRenderState state,float partial,CallbackInfo ci){((stepsword.mahoutsukai.fabric.PresenceRenderState)(Object)state).mahoutsukai$setConcealed(entity instanceof net.minecraft.world.entity.player.Player && entity.hasEffect(stepsword.mahoutsukai.potion.ModEffects.RIPPER_INVISIBILITY));}
 @Inject(method="submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$hide(LivingEntityRenderState state,PoseStack pose,SubmitNodeCollector collector,CameraRenderState camera,CallbackInfo ci){if(((stepsword.mahoutsukai.fabric.PresenceRenderState)(Object)state).mahoutsukai$isConcealed())ci.cancel();}
}
''',True)
put('fabric/mixin/RipperAttackMixin.java','''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Minecraft.class)
public abstract class RipperAttackMixin {
 @Unique private boolean mahoutsukai$lastWasMain;
 @Shadow private int missTime;
 @Inject(method="startAttack",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$dualAttack(CallbackInfoReturnable<Boolean> ci){
  var client=(Minecraft)(Object)this;var player=client.player;
  if(player==null || client.level==null || client.gameMode==null || client.hitResult==null || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND) || !(player.getMainHandItem().getItem() instanceof stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper) || !(player.getOffhandItem().getItem() instanceof stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper))return;
  mahoutsukai$lastWasMain=!mahoutsukai$lastWasMain;var hand=mahoutsukai$lastWasMain?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND;
  switch(client.hitResult.getType()){
   case ENTITY->{var target=((EntityHitResult)client.hitResult).getEntity();player.connection.send(new ServerboundSetCarriedItemPacket(player.getInventory().getSelectedSlot()));net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new stepsword.mahoutsukai.networking.RipperUseEntityPacket(hand,target.getId()));if(!player.isSpectator()){stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.attack(player,target);player.resetAttackStrengthTicker();}}
   case BLOCK->{var hit=(net.minecraft.world.phys.BlockHitResult)client.hitResult;if(!client.level.isEmptyBlock(hit.getBlockPos()))client.gameMode.startDestroyBlock(hit.getBlockPos(),hit.getDirection());}
   case MISS->{if(client.gameMode.hasMissTime())missTime=10;player.resetAttackStrengthTicker();}
  }
  player.swing(hand);ci.setReturnValue(true);
 }
}
''',True)
# Original fog motion, gray coloration, size and 800 tick fade, on native particle quads.
put('render/particle/ParticleFog.java','''package stepsword.mahoutsukai.render.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
public class ParticleFog extends SingleQuadParticle {
 private boolean moving;
 public ParticleFog(ClientLevel world,double x,double y,double z,SpriteSet sprite){
  super(world,x,y,z,sprite.get(net.minecraft.util.RandomSource.create()));
  float color=.8f+.2f*random.nextFloat();rCol=gCol=bCol=color;alpha=0;quadSize=(8+random.nextFloat()*12-2)*1.3f*.5f;roll=random.nextFloat()*(float)(2*Math.PI);oRoll=roll;lifetime=801;hasPhysics=false;
 }
 public void tick(){
  age++;if(random.nextFloat()<.001 || !moving){xd=random.nextFloat()*.01f-.005f;zd=random.nextFloat()*.01f-.005f;moving=true;}
  yd=0;xo=x;yo=y;zo=z;x+=xd;z+=zd;alpha=age<40?age/40f*.5f:age>730?(800-age)/70f*.5f:.5f;if(age>800)remove();
 }
 protected Layer getLayer(){return Layer.TRANSLUCENT;}
 public int getLightColor(float partial){return 0xF000F0;}
 public static final class Provider implements ParticleProvider<net.minecraft.core.particles.SimpleParticleType>{
  private final SpriteSet sprites;public Provider(SpriteSet sprites){this.sprites=sprites;}
  public Particle createParticle(net.minecraft.core.particles.SimpleParticleType type,ClientLevel world,double x,double y,double z,double vx,double vy,double vz,net.minecraft.util.RandomSource random){return new ParticleFog(world,x,y,z,sprites);}
 }
}
''',True)
p=main/'render/particle/ModParticles.java';s=p.read_text(encoding='utf-8').replace(' public static void init() {}',' public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> FOG1=()->FOG_TYPE;\n private static final net.minecraft.core.particles.SimpleParticleType FOG_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","fog1"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));\n public static void init() {}');p.write_text(s,encoding='utf-8')
p=client/'fabric/SixFamilyClient.java';s=p.read_text(encoding='utf-8').replace(' public static void init(){',''' public static void init(){
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.presenceConcealment.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);
  net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance().register(stepsword.mahoutsukai.render.particle.ModParticles.FOG1.get(),stepsword.mahoutsukai.render.particle.ParticleFog.Provider::new);
  ClientPlayNetworking.registerGlobalReceiver(RipperInvisiblePacket.TYPE,(packet,context)->{if(context.client().level.getEntity(packet.entityids) instanceof net.minecraft.world.entity.LivingEntity living){if(packet.invisible)stepsword.mahoutsukai.util.EffectUtil.buff(living,ModEffects.RIPPER_INVISIBILITY,false,2000000000,false);else living.removeEffect(ModEffects.RIPPER_INVISIBILITY);}});
  ClientPlayNetworking.registerGlobalReceiver(FogPacket.TYPE,(packet,context)->{
   var world=context.client().level;var entity=world.getEntity(packet.eid);if(entity==null)return;var v=entity.position();double unit=Math.max(1,packet.space);
   for(double x=-packet.range;x<packet.range;x+=unit)for(double y=-packet.range/4;y<packet.range/2;y+=unit)for(double z=-packet.range;z<packet.range;z+=unit)if(x*x+y*y+z*z<packet.range*packet.range)world.addParticle(stepsword.mahoutsukai.render.particle.ModParticles.FOG1.get(),v.x+x,v.y+y,v.z+z,0,0,0);
  });''');p.write_text(s,encoding='utf-8')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text(encoding='utf-8'));j['client']+=['PresenceStateMixin','PresenceRendererMixin','RipperAttackMixin'];p.write_text(json.dumps(j,indent=2)+'\n',encoding='utf-8')
p=res/'assets/mahoutsukai/particles/fog1.json';p.write_text('{"textures":["mahoutsukai:depth"]}\n',encoding='utf-8');p=res/'assets/mahoutsukai/textures/particle/depth.png';shutil.copyfile(Path('recovered/assets/mahoutsukai/textures/particle/depth.png'),p)
