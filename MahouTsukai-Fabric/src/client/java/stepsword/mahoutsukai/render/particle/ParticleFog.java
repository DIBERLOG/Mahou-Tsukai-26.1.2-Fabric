package stepsword.mahoutsukai.render.particle;
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
  public Particle createParticle(net.minecraft.core.particles.SimpleParticleType type,ClientLevel world,double x,double y,double z,double vx,double vy,double vz,net.minecraft.util.RandomSource random){return ProjectedFogParticle.ambient(world,x,y,z);}
 }
}
