package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import stepsword.mahoutsukai.containers.MysticCodeContents;
import stepsword.mahoutsukai.item.mysticcode.MysticCode;
import stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll;
import stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll;
import stepsword.mahoutsukai.util.Utils;

public class MysticCodeSwitchPacket implements CustomPacketPayload {
   public int slot;
   public static final Type<MysticCodeSwitchPacket> TYPE=new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mystic_code_switch"));
   public static final StreamCodec<ByteBuf, MysticCodeSwitchPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, p -> p.slot, MysticCodeSwitchPacket::new
   );

   public MysticCodeSwitchPacket(int s) {
      this.slot = s;
   }

   public static void handleSwitch(MysticCodeSwitchPacket message, Player player) {
      if(message.slot<0 || message.slot>=3 || player.isSpectator() || player.hasEffect(stepsword.mahoutsukai.potion.ModEffects.BOUND))return;
      ItemStack stack = player.getMainHandItem();
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof MysticCode) {
         MysticCode.setSlot(stack, message.slot);
         ((MysticCode)stack.getItem()).setRandomColor(stack, player.getRandom());
         MysticCodeContents inventory = Utils.getMCInventory(stack);
         if (inventory != null) {
            int slot = MysticCode.getSlot(stack);
            if (slot >= 0 && slot < 3) {
               ItemStack ammo = inventory.getItem(slot);
               if (ammo.getItem() instanceof GandrSpellScroll) {
                  ((MysticCode)stack.getItem()).setColor(stack, 8);
               }

               if (ammo.getItem() instanceof FallenDownSpellScroll) {
                  ((MysticCode)stack.getItem()).setColor(stack, 9);
               }
            }
         }
      }
   }

   public Type<MysticCodeSwitchPacket> type() {
      return TYPE;
   }
}
