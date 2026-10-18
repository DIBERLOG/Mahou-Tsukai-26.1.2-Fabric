package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class EntityPathPacket implements CustomPacketPayload {
   public static final Type<EntityPathPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","entity_path"));
   public ArrayList<Integer> entities;
   public ArrayList<Path> paths;
   public static final StreamCodec<ByteBuf, EntityPathPacket> STREAM_CODEC = new StreamCodec<ByteBuf, EntityPathPacket>() {
      public EntityPathPacket decode(ByteBuf buf) {
         EntityPathPacket packet = new EntityPathPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, EntityPathPacket packet) {
         packet.encode(buf);
      }
   };

   public EntityPathPacket() {
   }

   public EntityPathPacket(ArrayList<Integer> entities, ArrayList<Path> paths) {
      this.entities = entities;
      this.paths = paths;
   }

   public void decode(ByteBuf buf) {
      this.entities = new ArrayList<>();
      this.paths = new ArrayList<>();
      int n = buf.readInt();

      for (int i = 0; i < n; i++) {
         this.entities.add(buf.readInt());
      }

      n = buf.readInt();

      for (int i = 0; i < n; i++) {
         int points = buf.readInt();
         if (points > 0) {
            Node[] p = new Node[points];

            for (int j = 0; j < points; j++) {
               p[j] = new Node(buf.readInt(), buf.readInt(), buf.readInt());
            }

            this.paths.add(new Path(Arrays.asList(p), p[p.length - 1].asBlockPos(), false));
         }
      }
   }

   public void encode(ByteBuf buf) {
      buf.writeInt(this.entities.size());

      for (int i = 0; i < this.entities.size(); i++) {
         buf.writeInt(this.entities.get(i));
      }

      buf.writeInt(this.paths.size());

      for (int i = 0; i < this.paths.size(); i++) {
         int points = this.paths.get(i).getNodeCount();
         buf.writeInt(points);

         for (int j = 0; j < points; j++) {
            Node p = this.paths.get(i).getNode(j);
            buf.writeInt(p.x);
            buf.writeInt(p.y);
            buf.writeInt(p.z);
         }
      }
   }


   public Type<EntityPathPacket> type() {
      return TYPE;
   }
}
