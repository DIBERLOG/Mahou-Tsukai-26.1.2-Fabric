package stepsword.mahoutsukai.tile.exchange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class AlchemicalExchangeMahoujinTileEntity extends MahoujinTickingTileEntity {
   private long placedTime = 0L;
   private long lastProcessingDay = Long.MIN_VALUE;
   private int processingLayer = 0;
   private static final String PLACED_TIME_TAG = "MAHOUTSUKAI_PLACED_TIME_TAG";
   private static final String LAST_PROCESSING_DAY_TAG = "MAHOUTSUKAI_LAST_PROCESSING_DAY_TAG";
   private static final String PROCESSING_LAYER_TAG = "MAHOUTSUKAI_PROCESSING_LAYER_TAG";
   private static HashSet<BlockState> dirtClass = new HashSet<>();
   private static HashSet<BlockState> stoneClass = new HashSet<>();
   private static HashSet<BlockState> gemClass = new HashSet<>();
   private static HashSet<BlockState> metalClass = new HashSet<>();
   private static HashSet<BlockState> clayClass = new HashSet<>();
   private static HashSet<BlockState> woodClass = new HashSet<>();
   private static HashSet<BlockState> grassClass = new HashSet<>();
   private static HashSet<BlockState> specialClass = new HashSet<>();
   private static HashSet<BlockState> fluidClass = new HashSet<>();
   private static HashSet<BlockState> wildcardClass = new HashSet<>();
   private static ArrayList<HashSet> blockClasses = new ArrayList<>();
   private ArrayList<Integer> indices = null;

   public AlchemicalExchangeMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.alchemicalExchange.get(), p, bs);
   }

   @Override
   public int blockCycle() {
      return MTConfig.ALCHEMICAL_EXCHANGE_BLOCK_CYCLE;
   }

   @Override
   public void mahoujinTick(Level level, BlockPos blockPos, BlockState blockState, MahoujinTickingTileEntity blockEntity) {
      if (ActiveCircleConfig.tryToOperate(this, this.getCasterUUID())) {
         this.transformLayers();
      }
   }

   public void setPlacedTime(long t) {
      this.placedTime = t;
   }

   @Override
   protected void saveAdditional(ValueOutput output) {
      output.putInt("MAHOUTSUKAI_PROCESSING_LAYER_TAG", this.processingLayer);
      output.putLong("MAHOUTSUKAI_PLACED_TIME_TAG", this.placedTime);
      output.putLong("MAHOUTSUKAI_LAST_PROCESSING_DAY_TAG", this.lastProcessingDay);
      super.saveAdditional(output);
   }

   @Override
   public void loadAdditional(ValueInput input) {
      this.placedTime = input.getLongOr("MAHOUTSUKAI_PLACED_TIME_TAG", 0L);
      this.lastProcessingDay = input.getLongOr("MAHOUTSUKAI_LAST_PROCESSING_DAY_TAG", Long.MIN_VALUE);
      this.processingLayer = input.getIntOr("MAHOUTSUKAI_PROCESSING_LAYER_TAG", 0);
      super.loadAdditional(input);
   }

   public void transformLayers() {
      long clockTime = this.level.getOverworldClockTime();
      long processingDay = Math.floorDiv(clockTime, 24000);
      long p = Math.floorMod(clockTime, 24000);
      p -= p % 20L;
      if (MTConfig.ALCHEMICAL_EXCHANGE_HEIGHT <= 0) {
         this.processingLayer = 0;
         this.indices = null;
      } else {
         if (this.indices == null) {
            this.indices = this.generateIndices(blockClasses, this.getCaster());
         }

         if (p == this.placedTime && processingDay != this.lastProcessingDay) {
            this.processingLayer = 1;
            this.lastProcessingDay = processingDay;
         }

         if (this.processingLayer > 0) {
            this.transformLayer(this.processingLayer, this.indices, this.getCaster());
            this.processingLayer++;
            if (this.processingLayer > MTConfig.ALCHEMICAL_EXCHANGE_HEIGHT) {
               this.processingLayer = 0;
               this.indices = null;
            }
         }
      }
   }

   public ArrayList<Integer> generateIndices(ArrayList<HashSet> lengths, Player player) {
      ArrayList<Integer> ret = new ArrayList<>();

      for (int z = 0; z < lengths.size(); z++) {
         if (player != null) {
            ret.add(player.random.nextInt(Math.max(1, lengths.get(z).size())));
         } else {
            ret.add(Utils.getRandom(this.level).nextInt(Math.max(1, lengths.get(z).size())));
         }
      }

      return ret;
   }

   public boolean transformLayer(int layer, ArrayList<Integer> indices, Player player) {
      Stream<BlockPos> layerBlocks = BlockPos.betweenClosedStream(this.worldPosition.offset(-2, -layer, -2), this.worldPosition.offset(2, -layer, 2));
      layerBlocks.forEach(
         tmp -> {
            BlockState currentState = this.level.getBlockState(tmp);
            if (!Utils.isBlockAir(this.level, tmp)) {
               BlockState tb = null;

               for (int i = 0; i < blockClasses.size(); i++) {
                  HashSet<BlockState> s = blockClasses.get(i);
                  if (s.contains(currentState)) {
                     int target;
                     if (i < indices.size()) {
                        target = indices.get(i);
                     } else {
                        target = 0;
                     }

                     if (target < s.size()) {
                        tb = new ArrayList<>(s).get(target);
                        break;
                     }
                  }
               }

               if (tb != null) {
                  boolean nearbyBatteriesGaveMana = PlayerManaManager.getManaFromBatteriesFirst(
                     this.worldPosition, this.level, this.getCasterUUID(), MTConfig.ALCHEMICAL_EXCHANGE_MANA_COST
                  );
                  if (nearbyBatteriesGaveMana
                     || player != null
                        && PlayerManaManager.drainMana(player, MTConfig.ALCHEMICAL_EXCHANGE_MANA_COST, false, false) == MTConfig.ALCHEMICAL_EXCHANGE_MANA_COST) {
                     EffectUtil.tryChangeBlockState(false, tmp, tb, this.level, this.getCaster());
                     if (player != null) {
                        PlayerManaManager.updateClientMahou((ServerPlayer)player, Utils.getPlayerMahou(player));
                     }
                  }
               }
            }
         }
      );
      return true;
   }

   public static void init() {
      try {
         blockClasses = new ArrayList<>();
         dirtClass = new HashSet<>();
         stoneClass = new HashSet<>();
         metalClass = new HashSet<>();
         gemClass = new HashSet<>();
         clayClass = new HashSet<>();
         woodClass = new HashSet<>();
         specialClass = new HashSet<>();
         grassClass = new HashSet<>();
         fluidClass = new HashSet<>();
         wildcardClass = new HashSet<>();
         addBlocksToList(dirtClass, MTConfig.ALCHEMICAL_DIRT_CLASS);
         addBlocksToList(stoneClass, MTConfig.ALCHEMICAL_STONE_CLASS);
         addBlocksToList(metalClass, MTConfig.ALCHEMICAL_METAL_CLASS);
         addBlocksToList(gemClass, MTConfig.ALCHEMICAL_GEM_CLASS);
         addBlocksToList(clayClass, MTConfig.ALCHEMICAL_CLAY_CLASS);
         addBlocksToList(woodClass, MTConfig.ALCHEMICAL_WOOD_CLASS);
         addBlocksToList(specialClass, MTConfig.ALCHEMICAL_SPECIAL_CLASS);
         addBlocksToList(grassClass, MTConfig.ALCHEMICAL_GRASS_CLASS);
         addBlocksToList(fluidClass, MTConfig.ALCHEMICAL_FLUID_CLASS);
         addBlocksToList(wildcardClass, MTConfig.ALCHEMICAL_WILDCARD_CLASS);
         blockClasses.add(dirtClass);
         blockClasses.add(stoneClass);
         blockClasses.add(metalClass);
         blockClasses.add(gemClass);
         blockClasses.add(clayClass);
         blockClasses.add(woodClass);
         blockClasses.add(specialClass);
         blockClasses.add(grassClass);
         blockClasses.add(fluidClass);
         blockClasses.add(wildcardClass);
      } catch (Exception var1) {
      }
   }

   public static void addBlocksToList(HashSet<BlockState> lst, List<? extends String> registryNames) {
      for (String registryName : registryNames) {
         String s = registryName;
         String[] variant = s.split("@");
         if (variant.length > 0) {
            Block b = Utils.getBlockFromRegistryName(variant[0]);
            if (b != null && b != Blocks.AIR) {
               BlockState bs = b.defaultBlockState();
               lst.add(bs);
            } else {
               Iterable<Holder<Block>> tag = Utils.getBlockTagFromRegistryName(variant[0]);
               if (!tag.iterator().hasNext()) {
                  Utils.err("Mahou Tsukai Alchemical Exchange: Registry block " + s + " not found.");
               }

               for (Holder<Block> blockHolder : tag) {
                  Block taggedBlock = (Block)blockHolder.value();
                  lst.add(taggedBlock.defaultBlockState());
               }
            }
         }
      }
   }
}
