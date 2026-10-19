package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import stepsword.mahoutsukai.fabric.SpellPresentation;

/** One small server event per successful cast; all animation is local. */
public record SpellVisualPacket(double x,double y,double z,int family,int color,int accent,int seed) implements CustomPacketPayload {
 public static final Type<SpellVisualPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","spell_visual"));
 public static final StreamCodec<ByteBuf,SpellVisualPacket> STREAM_CODEC=new StreamCodec<>(){
  public SpellVisualPacket decode(ByteBuf b){return new SpellVisualPacket(b.readDouble(),b.readDouble(),b.readDouble(),b.readUnsignedByte(),b.readInt(),b.readInt(),b.readInt());}
  public void encode(ByteBuf b,SpellVisualPacket p){b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);b.writeByte(p.family);b.writeInt(p.color);b.writeInt(p.accent);b.writeInt(p.seed);}
 };
 public SpellVisualPacket(net.minecraft.world.phys.Vec3 pos,SpellPresentation style){this(pos.x,pos.y,pos.z,style.family().ordinal(),style.color(),style.accent(),style.seed());}
 @Override public Type<SpellVisualPacket> type(){return TYPE;}
}
