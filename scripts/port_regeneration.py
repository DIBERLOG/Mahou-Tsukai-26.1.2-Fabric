from pathlib import Path
import re,json,shutil
root=Path('MahouTsukai-Fabric/src'); main=root/'main/java/stepsword/mahoutsukai'; orig=Path('recovered/stepsword/mahoutsukai')
def write(name,s):
 p=main/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
def read(name):return (orig/name).read_text(encoding='utf-8')
for n in ['entity/fae/FaeEntity.java','entity/fae/FaeAIFollow.java','entity/fae/FaeAIWander.java','entity/fae/FaeMoveControl.java','effects/Leylines.java','potion/EyesPotion.java','potion/FaySightEyesPotion.java','datacomponents/fiction/FictionMahou.java']:
 s=read(n).replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable')
 if n.endswith('FaeEntity.java'):
  s=re.sub(r'import net.neoforged.*;\n','',s).replace('import stepsword.mahoutsukai.MahouTsukaiMod;\n','').replace('import stepsword.mahoutsukai.networking.PacketHandler;','import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;')
  s=s.replace('MTConfig.FAE_NOISE && MahouTsukaiMod.proxy.isMahouTsukai()', 'MTConfig.FAE_NOISE')
  a=s.index('      AtEntity event =');b=s.index('\n   public void sendToAllNear',a)
  s=s[:a]+'''      this.sendToAllNear(this.level().getServer().getPlayerList(), x, y, z,
         volume > 1.0F ? 16.0F * volume : 16.0, EffectUtil.getDimension(this.level()),
         new FaeNoisePacket((int)x, (int)y, (int)z, soundIn, category, volume, pitch));
   }
''' +s[b:]
  s=s.replace('PacketHandler.sendTo(serverPlayer, packetIn);','if (serverPlayer.connection != null && ServerPlayNetworking.canSend(serverPlayer, FaeNoisePacket.TYPE))\n                     ServerPlayNetworking.send(serverPlayer, packetIn);')
  aliases={'Eye':'eye','Emerald':'emerald','Iron':'iron','Diamond':'diamond','Ender':'ender','Quartz':'quartz','Gold':'gold'}
  for k,v in aliases.items():s=s.replace('ModItems.powdered'+k+'.get()', 'ModItems.REGISTERED.get("powdered_'+v+'")')
 if n.endswith('FictionMahou.java'):s=s.replace('import stepsword.mahoutsukai.item.FaeEssence;', 'import stepsword.mahoutsukai.fabric.MahouIds;').replace('FaeEssence.faeID','MahouIds.FAE_UUID')
 write(n,s)
write('util/EffectUtil.java','''package stepsword.mahoutsukai.util;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
/** Original helpers required by the connected systems. */
public final class EffectUtil {
 public static boolean hasBuff(LivingEntity entity, Holder<MobEffect> effect) { return entity != null && entity.hasEffect(effect); }
 public static Identifier getDimension(Level world) { return world.dimension().identifier(); }
 public static boolean compareDimensions(Identifier a, Identifier b) { return a.equals(b); }
 public static boolean inGenericBlacklist(String value, List<? extends String> values) { return value != null && values != null && values.contains(value); }
}
''')
write('potion/ModEffects.java','''package stepsword.mahoutsukai.potion;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
public final class ModEffects {
 public static final Holder<MobEffect> FAY_SIGHT_EYES = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
  Identifier.fromNamespaceAndPath("mahoutsukai", "fay_sight_eyes"), new FaySightEyesPotion());
 public static int getColorNumber(int r, int g, int b) { return (r << 16) | (g << 8) | b; }
 public static void init() {}
}
''')
write('entity/ModEntities.java','''package stepsword.mahoutsukai.entity;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import stepsword.mahoutsukai.entity.fae.FaeEntity;
public final class ModEntities {
 private static final Identifier FAE_ID = Identifier.fromNamespaceAndPath("mahoutsukai", "fae_entity");
 private static final EntityType<FaeEntity> FAE_TYPE = Registry.register(BuiltInRegistries.ENTITY_TYPE, FAE_ID,
  EntityType.Builder.of(FaeEntity::new, MobCategory.CREATURE).sized(0.5F, 0.5F)
   .clientTrackingRange(200).updateInterval(3).build(ResourceKey.create(Registries.ENTITY_TYPE, FAE_ID)));
 public static final Supplier<EntityType<FaeEntity>> FAE = () -> FAE_TYPE;
 public static void init() {
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(FAE_TYPE, FaeEntity.registerAttributes());
 }
}
''')
write('sounds/ModSounds.java','''package stepsword.mahoutsukai.sounds;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
public final class ModSounds {
 private static Supplier<SoundEvent> register(String name) {
  var id = Identifier.fromNamespaceAndPath("mahoutsukai", name);
  var sound = Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
  return () -> sound;
 }
 public static final Supplier<SoundEvent> FAE_CHIME_1 = register("fae_chime_1");
 public static final Supplier<SoundEvent> FAE_CHIME_2 = register("fae_chime_2");
 public static final Supplier<SoundEvent> FAE_CHIME_3 = register("fae_chime_3");
 public static final Supplier<SoundEvent> FAE_CHIME_4 = register("fae_chime_4");
 public static void init() {}
}
''')
s=read('networking/FaeNoisePacket.java');s=re.sub(r'import net.neoforged.*;\n','',s);a=s.index('   public static void handle(');b=s.index('   public Type<FaeNoisePacket> type()',a);s=s[:a]+s[b:];s=s.replace('return MahouPackets.FAE_NOISE_TYPE;','return TYPE;');s=s.replace('public class FaeNoisePacket implements CustomPacketPayload {','''public class FaeNoisePacket implements CustomPacketPayload {
   public static final Type<FaeNoisePacket> TYPE = new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "fae_noise_packet"));''');s=re.sub(r'(?m)^   (float|int|SoundEvent|SoundSource) (\w+);',r'   public \1 \2;',s);write('networking/FaeNoisePacket.java',s)
p=main/'util/Utils.java';s=p.read_text();s=s.replace('    public static void debug', '''    public static boolean isBlockAir(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos) {
        var block = world.getBlockState(pos);
        return block.isAir() || block.is(net.minecraft.world.level.block.Blocks.AIR)
            || block.is(net.minecraft.world.level.block.Blocks.CAVE_AIR) || block.is(net.minecraft.world.level.block.Blocks.VOID_AIR);
    }
    public static stepsword.mahoutsukai.datacomponents.fiction.FictionMahou getFictionMahou(ItemStack stack) {
        var value = stack.get(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT);
        if (value == null) {
            value = new stepsword.mahoutsukai.datacomponents.fiction.FictionMahou();
            stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.FICTION_COMPONENT, value);
            return value;
        }
        return value.copy();
    }
    public static void debug''');p.write_text(s)
# The exact original read-only contribution methods; gameplay of these spells/items is separate.
s=read('effects/secret/GeasEffect.java');a=s.index('   public static int getUniqueGeasCount(');b=s.index('   public static String regToName',a);write('effects/secret/GeasEffect.java','''package stepsword.mahoutsukai.effects.secret;
import java.util.HashSet;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.dataattachments.mahou.IMahou;
import stepsword.mahoutsukai.util.Utils;
/** Original regeneration query. Contract creation/enforcement is not yet connected. */
public class GeasEffect {
 public static String BREAK = "==MAHOU==";
'''+s[a:b]+'}\n')
s=read('item/william/William.java');a=s.index('   public static int itemWilliamPlethora');b=s.index('   public static void williamBookTraded',a);s=s[a:b].replace('stack.is(ModItems.william)', 'net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "william"))');write('item/william/William.java','''package stepsword.mahoutsukai.item.william;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import stepsword.mahoutsukai.util.Utils;
/** Original inventory contribution query; full William item mechanics are not yet ported. */
public class William {
'''+s+'}\n')
# Original persisted and network component layouts.
p=main/'datacomponents/DataComponents.java';s=p.read_text();o=read('datacomponents/DataComponents.java');a=o.index('   public static final Codec<FictionMahou> FICTION_CODEC');b=o.index('   public static final DeferredHolder',a);defs=o[a:b];s=s.replace('import com.mojang.serialization.Codec;', '''import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;''');s=s.replace('    public static void init()',defs+'''    public static final DataComponentType<FictionMahou> FICTION_COMPONENT = DataComponentType.<FictionMahou>builder()
        .persistent(FICTION_CODEC).networkSynchronized(FICTION_STREAM_CODEC).build();
    public static void init()''');s=s.replace('    public static void init() {','''    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "fiction_mahou"), FICTION_COMPONENT);''');p.write_text(s)
