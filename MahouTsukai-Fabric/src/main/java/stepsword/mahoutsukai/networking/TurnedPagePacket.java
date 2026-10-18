package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.item.william.BookReadingTracker;
import stepsword.mahoutsukai.item.william.WrittenEvaluation;
import stepsword.mahoutsukai.util.Utils;

public class TurnedPagePacket implements CustomPacketPayload {
 public static final Type<TurnedPagePacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","turned_page_packet"));
   int page;
   public static ConcurrentHashMap<UUID, BookReadingTracker> lastOpened = new ConcurrentHashMap<>();
   public static final StreamCodec<ByteBuf, TurnedPagePacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, p -> p.page, TurnedPagePacket::new);

   public TurnedPagePacket(int page) {
      this.page = page;
   }

   public static void handle(TurnedPagePacket message, ServerPlayer player) {
      if(message.page<0 || message.page>100)return;
      ItemStack held=player.getMainHandItem().getItem() instanceof WrittenBookItem?player.getMainHandItem():player.getOffhandItem();
      if(!(held.getItem() instanceof WrittenBookItem))return;
      var book=held.get(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT);if(book==null || message.page>=book.pages().size())return;
      if(!lastOpened.containsKey(player.getUUID()) || !lastOpened.get(player.getUUID()).text.equals(WrittenEvaluation.getBookText(held)))return;
      MahouSavedData mahou = Utils.getWorldMahou(player.level());
      if (lastOpened != null && lastOpened.containsKey(player.getUUID())) {
         BookReadingTracker tracker = lastOpened.get(player.getUUID());
         long now = System.currentTimeMillis();
         if (message.page == tracker.lastPage + 1 && now - tracker.lastUpdated > 500L && now - tracker.lastUpdated < 120000L && now > tracker.lastUpdated) {
            mahou.addFameIfExists(1, tracker.text, player.getUUID(), player.level());
         }

         tracker.lastPage = message.page;
         tracker.lastUpdated = now;
      }
   }

   public static void openedBook(net.minecraft.world.entity.player.Player player,ItemStack stack) {
      lastOpened.remove(player.getUUID());
      if (stack.getItem() instanceof WrittenBookItem) {
         BookReadingTracker tracker = new BookReadingTracker();
         tracker.lastUpdated = System.currentTimeMillis();
         tracker.lastPage = 0;
         tracker.text = WrittenEvaluation.getBookText(stack);
         lastOpened.put(player.getUUID(), tracker);
      }
   }

   public Type<TurnedPagePacket> type() {
      return TYPE;
   }
}