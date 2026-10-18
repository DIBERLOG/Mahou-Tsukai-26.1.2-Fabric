package stepsword.mahoutsukai.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.block.ModBlocks;
import stepsword.mahoutsukai.block.spells.Mahoujin;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.potion.ModEffects;
import stepsword.mahoutsukai.tile.MahoujinTileEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class KeyPressPacket implements CustomPacketPayload {
   public static final Type<KeyPressPacket> TYPE = new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "key_press_packet"));
   String keyD;
   public static final StreamCodec<ByteBuf, KeyPressPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, p -> p.keyD, KeyPressPacket::new);

   public KeyPressPacket(String k) {
      this.keyD = k;
   }

   public static void handle(KeyPressPacket message, ServerPlayer player) {
      if (message.keyD.equals("key.drawMahoujin")) drawMahoujin(player);
   }
   public static void drawMahoujin(Player player) {
      Level world = player.level();
      IMahou playerMahou = Utils.getPlayerMahou(player);
      if (playerMahou != null && EffectUtil.hasBuff(player, ModEffects.BLEEDING)) {
         Vec3 pp = player.position();
         Vec3 pv = player.getViewVector(1.0F);
         pv = new Vec3(pv.x, 0.0, pv.z).normalize();
         Vec3 pgoal = new Vec3(pv.x + pp.x, pp.y - 1.0, pv.z + pp.z);
         BlockPos posInFront = Utils.toBlockPos(pgoal);
         BlockState blockInFrontState = world.getBlockState(posInFront);
         if (Utils.isBlockAir(world, posInFront)) {
            posInFront = posInFront.below();
            blockInFrontState = world.getBlockState(posInFront);
         }

         if (!Utils.isBlockAir(world, posInFront)
            && !(blockInFrontState.getBlock() instanceof LiquidBlock)
            && (
               blockInFrontState.entityCanStandOnFace(world, posInFront, player, Direction.UP) && blockInFrontState.getBlock() != ModBlocks.mahoujin.get()
                  || blockInFrontState.getBlock() instanceof ChestBlock
                  || !MTConfig.BLOOD_CIRCLE_SOLID_ONLY
            )) {
            BlockPos mahoujinPos = posInFront.above();
            if (!Utils.isBlockAir(world, mahoujinPos) && world.getBlockState(mahoujinPos).getBlock() != ModBlocks.spellClothBlock.get()) {
               mahoujinPos = mahoujinPos.above();
               posInFront = posInFront.above();
               blockInFrontState = world.getBlockState(posInFront);
            }

            if (blockInFrontState.entityCanStandOnFace(world, posInFront, player, Direction.UP) && blockInFrontState.getBlock() != ModBlocks.mahoujin.get()
               || blockInFrontState.getBlock() instanceof ChestBlock
               || !MTConfig.BLOOD_CIRCLE_SOLID_ONLY) {
               if (Utils.isBlockAir(world, mahoujinPos) || world.getBlockState(mahoujinPos).getBlock() == ModBlocks.spellClothBlock.get()) {
                  boolean clothFlag = world.getBlockState(mahoujinPos).getBlock() == ModBlocks.spellClothBlock.get();
                  world.setBlockAndUpdate(mahoujinPos, ((Mahoujin)ModBlocks.mahoujin.get()).defaultBlockState());
                  BlockEntity te = world.getBlockEntity(mahoujinPos);
                  if (te instanceof MahoujinTileEntity) {
                     ((MahoujinTileEntity)te).setCaster(player);
                     ((MahoujinTileEntity)te).setCloth(clothFlag);
                     ((MahoujinTileEntity)te).setFay(false);
                  }

                  playerMahou.setHasMagic(true);
                  ((MahouTrigger)ModTriggers.MAHOUTSUKAI.get()).trigger((ServerPlayer)player);
                  PlayerManaManager.updateClientMahou((ServerPlayer)player, playerMahou);
               }

               EffectUtil.debuff(player, ModEffects.BLEEDING);
            }
         }
      }
   }

   public Type<KeyPressPacket> type() {
      return TYPE;
   }
}
