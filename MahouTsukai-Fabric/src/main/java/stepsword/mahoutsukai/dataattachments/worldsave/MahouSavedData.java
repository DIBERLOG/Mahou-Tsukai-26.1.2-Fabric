package stepsword.mahoutsukai.dataattachments.worldsave;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import stepsword.mahoutsukai.item.ModItems;
import stepsword.mahoutsukai.item.william.WrittenEvaluation;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.util.AltChecker;
import stepsword.mahoutsukai.util.PlayerHelp;
import stepsword.mahoutsukai.util.Utils;

public class MahouSavedData extends SavedData {
   public static final Codec<MahouSavedData> CODEC = CompoundTag.CODEC.xmap(MahouSavedData::load, WorldMahouStorage::writeNBT);
   public static final SavedDataType<MahouSavedData> TYPE = new SavedDataType(
      Identifier.fromNamespaceAndPath("mahoutsukai", "mahoutsukai_world"), MahouSavedData::new, CODEC, null
   );
   public ConcurrentHashMap<UUID, BlockPos> realityMarbleSpawns = new ConcurrentHashMap<>();
   public Multimap<UUID, UUID> playerContracts = TreeMultimap.create();
   public ConcurrentHashMap<FictionData, UUID> playerCreations = new ConcurrentHashMap<>();

   public MahouSavedData() {
   }

   public MahouSavedData(ConcurrentHashMap<UUID, BlockPos> m, Multimap<UUID, UUID> m2, ConcurrentHashMap<FictionData, UUID> m3) {
      this.realityMarbleSpawns = m;
      this.playerContracts = m2;
      this.playerCreations = m3;
   }

   private static MahouSavedData load(CompoundTag tag) {
      MahouSavedData data = new MahouSavedData();
      WorldMahouStorage.readNBT(data, tag);
      return data;
   }

   public int addFameIfExists(int increase, String s, ServerLevel level) {
      return this.addFameIfExists(increase, s, null, level);
   }

   public int addFameIfExists(int increase, String s, UUID not_me, ServerLevel level) {
      for (FictionData d : this.playerCreations.keySet()) {
         if (d.text.equals(s) && (not_me == null || !AltChecker.isSamePlayer(not_me, d.uuid, level))) {
            if (increase != 0) {
               this.setDirty();
            }

            d.fame += increase;
            return d.fame;
         }
      }

      return 0;
   }

   public ItemStack addPlayerFictionIfDifferent(String bookText, UUID caster, WrittenBookContent content, Player player) {
      FictionData data = new FictionData();
      data.creationTime = System.currentTimeMillis();
      data.text = bookText;
      data.fame = 0;
      data.uuid = caster;
      data.content = content;
      int CREATION_LIMIT = 10;
      int creations = 0;
      FictionData earliest_for_caster = data;
      FictionData earliest_for_text = data;

      for (FictionData d : this.playerCreations.keySet()) {
         UUID creator = this.playerCreations.get(d);
         if (creator != null && creator.equals(caster)) {
            if (d.creationTime < earliest_for_caster.creationTime) {
               earliest_for_caster = d;
            }

            creations++;
         }

         if (d.text.length() == data.text.length() && d.text.equals(data.text) && d.creationTime < earliest_for_text.creationTime) {
            earliest_for_text = d;
         }
      }

      UUID author = this.playerCreations.get(earliest_for_text);
      ItemStack stack;
      if (earliest_for_text == data) {
         int evaluate = WrittenEvaluation.evaluateString(bookText, this.playerCreations);
         int cost = this.getCreationCost(evaluate);
         if (PlayerManaManager.drainMana(player, cost, false, false) == cost) {
            stack = new ItemStack(ModItems.william.get());
            FictionMahou mahou = Utils.getFictionMahou(stack);
            mahou.setCaster(caster);
            mahou.setStyle(evaluate);
            mahou.setFame(0);
            Utils.setFictionMahou(stack, mahou);
            this.setColors(stack, player, evaluate);
            data.style = evaluate;
            if (creations >= CREATION_LIMIT) {
               this.playerCreations.remove(earliest_for_caster);
            }

            this.playerCreations.put(data, caster);
            PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.FICTION_CREATED);
            this.setDirty();
         } else {
            PlayerHelp.sendHelpMessageReplaced(player, PlayerHelp.Message.FICTION_NOT_ENOUGH_MANA, evaluate, cost);
            stack = ItemStack.EMPTY;
         }
      } else if (author != null && author.equals(caster)) {
         int cost = this.getCreationCost(earliest_for_text.style);
         if (PlayerManaManager.drainMana(player, cost, false, false) == cost) {
            stack = new ItemStack(ModItems.william.get());
            FictionMahou mahou = Utils.getFictionMahou(stack);
            mahou.setCaster(caster);
            mahou.setStyle(earliest_for_text.style);
            mahou.setFame(earliest_for_text.fame);
            Utils.setFictionMahou(stack, mahou);
            this.setColors(stack, player, earliest_for_text.style);
            PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.FICTION_RECREATED);
            this.setDirty();
         } else {
            PlayerHelp.sendHelpMessageReplaced(player, PlayerHelp.Message.FICTION_NOT_ENOUGH_MANA, earliest_for_text.style, cost);
            stack = ItemStack.EMPTY;
         }
      } else {
         PlayerHelp.sendHelpMessage(player, PlayerHelp.Message.FICTION_NOT_YOURS);
         stack = ItemStack.EMPTY;
      }

      return stack;
   }

   public int getCreationCost(int style) {
      return Math.max(0, (int)(style * MTConfig.WILLIAM_CREATION_COST));
   }

   public void setColors(ItemStack s, Player player, int style) {
      if (style >= 0) {
         List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.WILLIAM_BOOK);
         Utils.color(
            s,
            settings.get(0).intValue(),
            settings.get(1).intValue(),
            settings.get(2).intValue(),
            settings.get(3).intValue(),
            settings.get(4).intValue(),
            settings.get(5).intValue()
         );
         settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.WILLIAM_MAGIC);
         Utils.secondcolor(
            s,
            settings.get(0).intValue(),
            settings.get(1).intValue(),
            settings.get(2).intValue(),
            settings.get(3).intValue(),
            settings.get(4).intValue(),
            settings.get(5).intValue()
         );
      } else {
         List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.RLYEH_BOOK);
         Utils.color(
            s,
            settings.get(0).intValue(),
            settings.get(1).intValue(),
            settings.get(2).intValue(),
            settings.get(3).intValue(),
            settings.get(4).intValue(),
            settings.get(5).intValue()
         );
         settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.RLYEH_MAGIC);
         Utils.secondcolor(
            s,
            settings.get(0).intValue(),
            settings.get(1).intValue(),
            settings.get(2).intValue(),
            settings.get(3).intValue(),
            settings.get(4).intValue(),
            settings.get(5).intValue()
         );
      }
   }

   public void changePlayerSpawn(UUID uuid, BlockPos pos) {
      if (uuid != null && pos != null) {
         this.realityMarbleSpawns.put(uuid, pos);
         this.setDirty();
      }
   }

   public boolean addContractIfNotExists(UUID uuid, UUID uuid2) {
      if (uuid == null || uuid2 == null) {
         return false;
      } else if (!this.playerContracts.containsEntry(uuid, uuid2) && !this.playerContracts.containsEntry(uuid2, uuid)) {
         this.playerContracts.put(uuid, uuid2);
         this.playerContracts.put(uuid2, uuid);
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean doesContractExist(UUID uuid, UUID uuid2) {
      return uuid != null
         && uuid2 != null
         && this.playerContracts != null
         && (this.playerContracts.containsEntry(uuid, uuid2) || this.playerContracts.containsEntry(uuid2, uuid));
   }

   public void removeContract(UUID uuid, UUID uuid2) {
      if (this.playerContracts.containsEntry(uuid, uuid2)) {
         this.playerContracts.remove(uuid, uuid2);
      }

      if (this.playerContracts.containsEntry(uuid2, uuid)) {
         this.playerContracts.remove(uuid2, uuid);
      }

      this.setDirty();
   }

   public BlockPos getPlayerSpawn(UUID uuid) {
      return this.realityMarbleSpawns.getOrDefault(uuid, null);
   }

   public ConcurrentHashMap<UUID, BlockPos> getAllSpawns() {
      return this.realityMarbleSpawns;
   }

   public Multimap<UUID, UUID> getPlayerContracts() {
      return this.playerContracts;
   }
}
