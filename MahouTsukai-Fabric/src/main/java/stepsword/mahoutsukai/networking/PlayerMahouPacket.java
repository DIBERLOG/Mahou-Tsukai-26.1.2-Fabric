package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.dataattachments.mahou.Mahou;
import stepsword.mahoutsukai.util.BlockDim;
import stepsword.mahoutsukai.util.ByteBufUtils;
import stepsword.mahoutsukai.util.UnorderedList;

public class PlayerMahouPacket implements CustomPacketPayload {
   IMahou mahou;
   public static final Type<PlayerMahouPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai", "player_mahou_packet"));
   public static final StreamCodec<ByteBuf, PlayerMahouPacket> STREAM_CODEC = new StreamCodec<ByteBuf, PlayerMahouPacket>() {
      public PlayerMahouPacket decode(ByteBuf buf) {
         PlayerMahouPacket packet = new PlayerMahouPacket();
         packet.decode(buf);
         return packet;
      }

      public void encode(ByteBuf buf, PlayerMahouPacket packet) {
         packet.encode(buf);
      }
   };

   public PlayerMahouPacket() {
   }

   public PlayerMahouPacket(IMahou mahou) {
      this.mahou = mahou;
   }

   public void decode(ByteBuf buf) {
      this.mahou = new Mahou();
      this.mahou.setStoredMana(buf.readInt());
      this.mahou.setMaxMana(buf.readInt());
      this.mahou.setChargeRate(buf.readDouble());
      this.mahou.setManaToUseCounter(buf.readInt());
      this.mahou.setManaFull(buf.readBoolean());
      this.mahou.setProtectiveDisplacement(buf.readInt());
      this.mahou.setDamageExchangeUses(buf.readInt());
      this.mahou.setDeathCollectionUsesLeft(buf.readFloat());
      this.mahou.setPossessing(buf.readBoolean());
      this.mahou.setHasMagic(buf.readBoolean());
      this.mahou.setPlayerPrevYaw(buf.readFloat());
      this.mahou.setPlayerPrevPitch(buf.readFloat());
      this.mahou.setLastRecipeCloth(buf.readBoolean());
      this.mahou.setCancelTime(buf.readLong());
      this.mahou.setVisible(buf.readBoolean());
      this.mahou.setFaeSpawn(buf.readBoolean());
      this.mahou.setGuidePage(buf.readInt());
      this.mahou.setEnemy(this.readUUID(buf));
      this.mahou.setLastArrowShot(this.readUUID(buf));
      this.mahou.setDamageReplicationTarget(this.readUUID(buf));
      this.mahou.setPlayerSavedSpawn(this.readBP(buf));
      this.mahou.setPlayerMarbleSpawn(this.readBP(buf));
      this.mahou.setMentalDisplacementPos(this.readBP(buf));
      this.mahou.setFamiliarInterestingBlock(this.readBP(buf));
      this.mahou.setPlayerPrevEyePos(this.readV(buf));
      this.mahou.setPlayerOldDimension(this.readString(buf));
      this.mahou.setImmunityDamageType(this.readString(buf));
      this.mahou.setMemorizedEDMTE(this.readBD(buf));
      this.mahou.setLastRecipe(this.readUL(buf));
      Collection<String> geas = this.readSC(buf);
      if (geas != null) {
         this.mahou.setGeas(new HashSet<>(geas));
      }

      this.mahou.setMandatoryFun(new HashMap<>(this.readHM(buf)));
   }

   public void encode(ByteBuf buf) {
      this.write(buf, this.mahou.getStoredMana());
      this.write(buf, this.mahou.getMaxMana());
      this.write(buf, this.mahou.getChargeRate());
      this.write(buf, this.mahou.getManaToUseCounter());
      this.write(buf, this.mahou.isManaFull());
      this.write(buf, this.mahou.getProtectiveDisplacement());
      this.write(buf, this.mahou.getDamageExchangeUses());
      this.write(buf, this.mahou.getDeathCollectionUsesLeft());
      this.write(buf, this.mahou.isPossessing());
      this.write(buf, this.mahou.hasMagic());
      this.write(buf, this.mahou.getPlayerPrevYaw());
      this.write(buf, this.mahou.getPlayerPrevPitch());
      this.write(buf, this.mahou.getLastRecipeCloth());
      this.write(buf, this.mahou.getCancelTime());
      this.write(buf, this.mahou.getVisible());
      this.write(buf, this.mahou.getFaeSpawn());
      this.write(buf, this.mahou.getGuidePage());
      this.write(buf, this.mahou.getEnemy());
      this.write(buf, this.mahou.getLastArrowShot());
      this.write(buf, this.mahou.getDamageReplicationTarget());
      this.write(buf, this.mahou.getPlayerSavedSpawn());
      this.write(buf, this.mahou.getPlayerMarbleSpawn());
      this.write(buf, this.mahou.getMentalDisplacementPos());
      this.write(buf, this.mahou.getFamiliarInterestingBlock());
      this.write(buf, this.mahou.getPlayerPrevEyePos());
      this.write(buf, this.mahou.getPlayerOldDimension());
      this.write(buf, this.mahou.getImmunityDamageType());
      this.write(buf, this.mahou.getMemorizedEDMTE());
      this.write(buf, this.mahou.getLastRecipe());
      this.write(buf, this.mahou.getGeas());
      this.write(buf, this.mahou.getMandatoryFun());
   }

   void write(ByteBuf buf, double d) {
      buf.writeDouble(d);
   }

   void write(ByteBuf buf, float d) {
      buf.writeFloat(d);
   }

   void write(ByteBuf buf, int d) {
      buf.writeInt(d);
   }

   void write(ByteBuf buf, boolean b) {
      buf.writeBoolean(b);
   }

   void write(ByteBuf buf, long b) {
      buf.writeLong(b);
   }

   void write(ByteBuf buf, UUID uuid) {
      if (this.writePresent(buf, uuid)) {
         buf.writeInt(uuid.toString().length());
         buf.writeCharSequence(uuid.toString(), StandardCharsets.UTF_8);
      }
   }

   UUID readUUID(ByteBuf buf) {
      UUID ret = null;
      if (this.readPresent(buf)) {
         ret = UUID.fromString(buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString());
      }

      return ret;
   }

   void write(ByteBuf buf, BlockPos pos) {
      if (this.writePresent(buf, pos)) {
         buf.writeInt(pos.getX());
         buf.writeInt(pos.getY());
         buf.writeInt(pos.getZ());
      }
   }

   BlockPos readBP(ByteBuf buf) {
      BlockPos ret = null;
      if (this.readPresent(buf)) {
         ret = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      }

      return ret;
   }

   void write(ByteBuf buf, BlockDim bd) {
      if (bd != null && bd.dim != null && bd.pos != null) {
         this.writePresent(buf, bd);
         this.write(buf, bd.dim.toString());
         this.write(buf, bd.pos);
      } else {
         this.writePresent(buf, null);
      }
   }

   BlockDim readBD(ByteBuf buf) {
      BlockDim ret = null;
      if (this.readPresent(buf)) {
         String s = this.readString(buf);
         if (s != null) {
            Identifier rs = Identifier.parse(s);
            BlockPos p = this.readBP(buf);
            ret = new BlockDim(p, rs);
         }
      }

      return ret;
   }

   void write(ByteBuf buf, Vec3 v) {
      if (this.writePresent(buf, v)) {
         buf.writeDouble(v.x);
         buf.writeDouble(v.y);
         buf.writeDouble(v.z);
      }
   }

   Vec3 readV(ByteBuf buf) {
      Vec3 v = null;
      if (this.readPresent(buf)) {
         v = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
      }

      return v;
   }

   void write(ByteBuf buf, String s) {
      String s2 = ChatFormatting.stripFormatting(s);
      if (this.writePresent(buf, s2)) {
         ByteBufUtils.writeUTF8String(buf, s);
      }
   }

   String readString(ByteBuf buf) {
      String ret = null;
      if (this.readPresent(buf)) {
         ret = ByteBufUtils.readUTF8String(buf);
      }

      return ret;
   }

   void write(ByteBuf buf, UnorderedList lst) {
      if (this.writePresent(buf, lst)) {
         this.write(buf, lst.getOrder());
      }
   }

   UnorderedList readUL(ByteBuf buf) {
      UnorderedList lst = null;
      if (this.readPresent(buf)) {
         Collection<String> ul = this.readSC(buf);
         lst = new UnorderedList(new ArrayList<>(ul));
      }

      return lst;
   }

   void write(ByteBuf buf, Collection<String> coll) {
      if (coll != null && coll.size() != 0) {
         this.writePresent(buf, coll);
         buf.writeInt(coll.size());
         Iterator<String> iter = coll.iterator();

         while (iter.hasNext()) {
            this.write(buf, iter.next());
         }
      } else {
         this.writePresent(buf, null);
      }
   }

   Collection<String> readSC(ByteBuf buf) {
      Collection<String> ret = new ArrayList<>();
      if (this.readPresent(buf)) {
         int k = buf.readInt();

         for (int i = 0; i < k; i++) {
            ret.add(this.readString(buf));
         }
      }

      if (ret.size() == 0) {
         ret = null;
      }

      return ret;
   }

   void write(ByteBuf buf, HashMap<String, Boolean> coll) {
      if (coll != null && coll.size() != 0) {
         this.writePresent(buf, coll);
         buf.writeInt(coll.size());

         for (String s : coll.keySet()) {
            this.write(buf, s);
            this.write(buf, coll.get(s));
         }
      } else {
         this.writePresent(buf, null);
      }
   }

   HashMap<String, Boolean> readHM(ByteBuf buf) {
      HashMap<String, Boolean> ret = new HashMap<>();
      if (this.readPresent(buf)) {
         int k = buf.readInt();

         for (int i = 0; i < k; i++) {
            ret.put(this.readString(buf), buf.readBoolean());
         }
      }

      return ret;
   }

   boolean writePresent(ByteBuf buf, Object object) {
      if (object == null) {
         buf.writeBoolean(false);
         return false;
      } else {
         buf.writeBoolean(true);
         return true;
      }
   }

   boolean readPresent(ByteBuf buf) {
      return buf.readBoolean();
   }

   public IMahou state() { return this.mahou; }

   public Type<PlayerMahouPacket> type() {
      return TYPE;
   }
}

