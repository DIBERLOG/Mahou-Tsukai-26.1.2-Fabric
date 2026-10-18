"""Recover the connected gem/battery spending group without changing originals."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
source = root/'recovered/stepsword/mahoutsukai'
target = root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai'
names = ['item/attunedgems/AttunedGem.java', 'item/attunedgems/AttunedDiamond.java',
         'item/attunedgems/AttunedEmerald.java', 'item/attunedgems/Attuner.java',
         'entity/butterfly/SafeFakePlayer.java', 'tile/UpdatingTileEntity.java',
         'tile/TickingTileEntity.java', 'tile/circuits/ManaCircuitTileEntity.java',
         'tile/circuits/ManaCircuitMagitechTileEntity.java', 'block/BlockBase.java',
         'block/BlockTileEntity.java', 'block/circuits/ManaCircuitBlock.java',
         'block/circuits/ManaCircuitMagitechBlock.java', 'block/itemblock/ManaCircuitBlockItem.java']
for name in names:
    text = (source/name).read_text()
    text = text.replace('net.neoforged.neoforge.common.util.FakePlayer', 'net.fabricmc.fabric.api.entity.FakePlayer')
    text = text.replace('javax.annotation.Nullable', 'org.jetbrains.annotations.Nullable')
    text = text.replace('import stepsword.mahoutsukai.item.FaeEssence;', 'import stepsword.mahoutsukai.fabric.MahouIds;')
    text = text.replace('FaeEssence.faeID', 'MahouIds.FAE_UUID')
    text = text.replace('playerIn.getMainHandItem().getItem() instanceof FaeEssence',
        'net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(playerIn.getMainHandItem().getItem()).equals(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "fae_essence"))')
    # Null owner has no component representation; keep it absent until placement assigns ownership.
    text = text.replace('builder.set(DataComponents.MANA_CIRCUIT_OWNER_COMPONENT, this.ownerUUID);',
        'if (this.ownerUUID != null) builder.set(DataComponents.MANA_CIRCUIT_OWNER_COMPONENT, this.ownerUUID);')
    out = target/name
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(text)

text = (source/'mana/PlayerManaManager.java').read_text()
text = text[:text.index('   public static void regenMana(')]
unused = ['LivingDeathEvent','Leylines','GeasEffect','FaeEntity','William','PacketHandler','PlayerMahouPacket','EffectUtil','PlayerHelp']
text = '\n'.join(line for line in text.splitlines() if not any(line.startswith('import ') and line.endswith('.'+name+';') for name in unused))
text = text.replace('net.neoforged.neoforge.common.util.FakePlayer', 'net.fabricmc.fabric.api.entity.FakePlayer')
text = text.replace('player.hurt(MTConfig.MAGIC_DAMAGE_GENERIC', 'player.hurtServer((net.minecraft.server.level.ServerLevel)player.level(), MTConfig.MAGIC_DAMAGE_GENERIC')
text = text.replace('player.lastHurtByPlayerMemoryTime = 100;',
    '((stepsword.mahoutsukai.fabric.mixin.LivingCombatAccessor)player).mahoutsukai$setLastHurtByPlayerMemoryTime(100);')
text += '''
   public static void updateManaCounter(ServerPlayer player, int mana) {
      stepsword.mahoutsukai.fabric.ManaProgression.recordSpent(Utils.getPlayerMahou(player), mana,
          stepsword.mahoutsukai.fabric.ManaConfig.current);
      stepsword.mahoutsukai.fabric.MahouNetworking.sync(player);
   }
   public static void updateClientMahou(ServerPlayer player, IMahou mahou) {
      if (player != null && mahou != null) stepsword.mahoutsukai.fabric.MahouNetworking.sync(player);
   }
   public static boolean hasMana(Player player, int drain) { return countMana(player) > drain; }
}
'''
(target/'mana').mkdir(exist_ok=True)
(target/'mana/PlayerManaManager.java').write_text(text)
