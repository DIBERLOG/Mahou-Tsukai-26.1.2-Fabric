package stepsword.mahoutsukai.render.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
public final class ParticleMagic extends SingleQuadParticle {
 private static SpriteSet sprites;
 private double slow=.6;
 public ParticleMagic(ClientLevel world,double x,double y,double z,float scale,double vx,double vy,double vz,String texture,float size,float r,float g,float b,float a,boolean shaders,double slow){this(world,x,y,z,scale,vx,vy,vz,texture,size,r,g,b,a,shaders);this.slow=slow;}
 public ParticleMagic(ClientLevel world,double x,double y,double z,float scale,double vx,double vy,double vz,String texture,float size,float r,float g,float b,float a,boolean shaders){
  super(world,x,y,z,sprites.get(net.minecraft.util.RandomSource.create()));
  xd=vx;yd=vy;zd=vz;rCol=r;gCol=g;bCol=b;alpha=a;quadSize=size*.5f;float max=g==1?25+size*10:50+size*100;lifetime=(int)((int)(max/(Math.random()*.3+.6))*scale);hasPhysics=false;
 }
 public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();setPos(x+xd,y+yd,z+zd);xd*=slow;yd*=slow;zd*=slow;alpha=1-(float)age/lifetime;oRoll=roll;}
 protected Layer getLayer(){return Layer.TRANSLUCENT;}
 public int getLightColor(float partial){return 0xF000F0;}
 public static final class Provider implements ParticleProvider<net.minecraft.core.particles.SimpleParticleType>{
  public Provider(SpriteSet spriteSet){sprites=spriteSet;}
  public Particle createParticle(net.minecraft.core.particles.SimpleParticleType type,ClientLevel world,double x,double y,double z,double vx,double vy,double vz,net.minecraft.util.RandomSource random){return new ParticleMagic(world,x,y,z,.5f,vx,vy,vz,"textures/particle/white_magic_particle",.2f,1,0,0,1,true);}
 }
}
