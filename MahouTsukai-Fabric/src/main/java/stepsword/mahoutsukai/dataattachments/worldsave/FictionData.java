package stepsword.mahoutsukai.dataattachments.worldsave;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.WrittenBookContent;

public class FictionData {
   public int style;
   public int fame;
   public String text;
   public long creationTime;
   public UUID uuid;
   public WrittenBookContent content;
   public static final Codec<FictionData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("style").forGetter(FictionData::getStyle),
            Codec.INT.fieldOf("fame").forGetter(FictionData::getFame),
            Codec.STRING.fieldOf("text").forGetter(FictionData::getText),
            Codec.LONG.fieldOf("creationTime").forGetter(FictionData::getCreationTime),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(FictionData::getUuid),
            WrittenBookContent.CODEC.fieldOf("content").forGetter(FictionData::getContent)
         )
         .apply(instance, FictionData::new)
   );

   public FictionData(int style, int fame, String text, long creationTime, UUID uuid, WrittenBookContent content) {
      this.style = style;
      this.fame = fame;
      this.text = text;
      this.creationTime = creationTime;
      this.uuid = uuid;
      this.content = content;
   }

   public FictionData() {
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.text, this.creationTime);
   }

   public int getStyle() {
      return this.style;
   }

   public int getFame() {
      return this.fame;
   }

   public String getText() {
      return this.text;
   }

   public long getCreationTime() {
      return this.creationTime;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public WrittenBookContent getContent() {
      return this.content;
   }

   public CompoundTag toNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putInt("style", this.style);
      tag.putInt("fame", this.fame);
      tag.putString("text", this.text);
      tag.putLong("creation", this.creationTime);
      tag.storeNullable("caster", UUIDUtil.CODEC, this.uuid);
      tag.storeNullable("written_book_content", WrittenBookContent.CODEC, this.content);
      return tag;
   }

   public static FictionData fromNBT(CompoundTag tag) {
      FictionData ret = new FictionData();
      ret.style = tag.getIntOr("style", 0);
      ret.fame = tag.getIntOr("fame", 0);
      ret.text = tag.getStringOr("text", "");
      ret.creationTime = tag.getLongOr("creation", 0L);
      ret.uuid = (UUID)tag.read("caster", UUIDUtil.CODEC).orElse(null);
      ret.content = (WrittenBookContent)tag.read("written_book_content", WrittenBookContent.CODEC).orElse(null);
      return ret;
   }
}
