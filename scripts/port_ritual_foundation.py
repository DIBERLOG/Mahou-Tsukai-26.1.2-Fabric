from pathlib import Path
import re
m=Path('MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai');o=Path('recovered/stepsword/mahoutsukai')
def w(n,s):
 p=m/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s,encoding='utf-8')
files=['item/FaeEssence.java','block/spells/SpellCloth.java','block/spells/MahoujinBlockTileEntity.java','block/spells/SingleUseMahoujinBlockTileEntity.java','tile/MahoujinTileEntity.java','tile/ScrollGiver.java','tile/AboveChecker.java','tile/MahoujinTickingTileEntity.java','tile/eyes/FaySightEyesMahoujinTileEntity.java','block/spells/eyes/FaySightEyesMahoujin.java','tile/displacement/AscensionMahoujinTileEntity.java','block/spells/displacement/AscensionMahoujin.java','tile/secret/ProbabilityAlterMahoujinTileEntity.java','block/spells/secret/ProbabilityAlterMahoujin.java','item/spells/secret/ProbabilityAlterSpellScroll.java','mana/ActiveCircleConfig.java','advancements/MahouTrigger.java','block/itemblock/MahoujinBlockItem.java']
aliases={'faySightEyesScroll':'scroll_fay_sight','ascensionScroll':'scroll_ascension','probabilityAlterScroll':'scroll_probability_alter'}
for n in files:
 s=(o/n).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable').replace('javax.annotation.Nonnull','org.jetbrains.annotations.NotNull').replace('@Nonnull','@NotNull')
 for a,b in aliases.items():s=s.replace('ModItems.'+a+'.get()', 'ModItems.REGISTERED.get("'+b+'")')
 w(n,s)
w('advancements/ModTriggers.java','''package stepsword.mahoutsukai.advancements;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
public final class ModTriggers {
 private static final MahouTrigger MAHOU = Registry.register(BuiltInRegistries.TRIGGER_TYPES,
  Identifier.fromNamespaceAndPath("mahoutsukai", "mahoutsukai"), new MahouTrigger());
 public static final Supplier<MahouTrigger> MAHOUTSUKAI = () -> MAHOU;
 public static void init() {}
}
''')
s=(o/'networking/KeyPressPacket.java').read_text();s=re.sub(r'import net.neoforged.*;\n','',s);a=s.index('   public static void handle(');b=s.index('      Player player = context.player();',a);s=s[:a]+'''   public static void handle(KeyPressPacket message, ServerPlayer player) {
      if (message.keyD.equals("key.drawMahoujin")) drawMahoujin(player);
   }
   public static void drawMahoujin(Player player) {
'''+s[b+len('      Player player = context.player();\n'):];s=s.replace('return MahouPackets.KEY_PRESS_TYPE;', 'return TYPE;').replace('public class KeyPressPacket implements CustomPacketPayload {','''public class KeyPressPacket implements CustomPacketPayload {
   public static final Type<KeyPressPacket> TYPE = new Type<>(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai", "key_press_packet"));''');w('networking/KeyPressPacket.java',s)
# Original circle interaction; item container access is supplied by Fabric Transfer API.
s=(o/'block/spells/Mahoujin.java').read_text();s=re.sub(r'import net.neoforged.*;\n','',s).replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable');s=s.replace('import stepsword.mahoutsukai.MahouTsukaiMod;\n','');s=re.sub(r'import stepsword.mahoutsukai.tile.(displacement.OrderedDisplacementMahoujinTileEntity|exchange.AlchemicalExchangeMahoujinTileEntity|exchange.ChronalExchangeMahoujinTileEntity);\n','',s)
a=s.index('               boolean cloth = mahou.getLastRecipeCloth();');b=s.index('\n            }\n         }\n      }\n\n      return InteractionResult.FAIL;',a);s=s[:a]+'''               if (stepsword.mahoutsukai.fabric.RitualInventory.repeat(player, mahoujin, mahou.getLastRecipe(), mahou.getLastRecipeCloth())) {
                  this.transformToSpell(mahoujin, level, player, pos);
                  return InteractionResult.SUCCESS;
               }'''+s[b:]
a=s.index('         boolean gamestages =');b=s.index('         boolean banned =',a);s=s[:a]+s[b:];s=s.replace('if (gamestages && newBlock != null && !banned)', 'if (newBlock != null && !banned)');a=s.index('               if (mte instanceof OrderedDisplacement');b=s.index('               IMahou mahou =',a);s=s[:a]+s[b:]
for a,b in {'Diamond':'diamond','Ender':'ender','Quartz':'quartz','Gold':'gold','Iron':'iron','Emerald':'emerald','Eye':'eye'}.items():s=s.replace('ModItems.powdered'+a+'.get()', 'ModItems.REGISTERED.get("powdered_'+b+'")')
w('block/spells/Mahoujin.java',s)
# Register only real adapted recipes; unknown recipes remain untransformed.
s=(o/'block/spells/MahoujinRecipeRegistrar.java').read_text();a=s.index('   public static void init()');b=s.index('   public static UnorderedList makeRecipe',a);s=s[:a]+'''   public static void init() {
      var sight = makeRecipe(POWDERED_EYE, POWDERED_EYE, POWDERED_EYE);
      var ascent = makeRecipe(POWDERED_ENDER, POWDERED_ENDER, POWDERED_GOLD);
      var probability = makeRecipe(POWDERED_GOLD, POWDERED_DIAMOND, POWDERED_QUARTZ);
      recipes.put(sight, (BlockBase)ModBlocks.faySightEyesMahoujin.get()); needsCloth.put(sight, true);
      recipes.put(ascent, (BlockBase)ModBlocks.ascensionMahoujin.get()); needsCloth.put(ascent, false);
      recipes.put(probability, (BlockBase)ModBlocks.probabilityAlterMahoujin.get()); needsCloth.put(probability, false);
      for (var item : ModItems.REGISTERED.values()) if (item instanceof PowderedCatalyst catalyst)
         ingredients.put(catalyst.getCatalystName(), catalyst);
   }

'''+s[b:]
for a,b in {'Diamond':'diamond','Ender':'ender','Quartz':'quartz','Gold':'gold','Iron':'iron','Emerald':'emerald','Eye':'eye'}.items():s=s.replace('ModItems.powdered'+a+'.get()', 'ModItems.REGISTERED.get("powdered_'+b+'")')
w('block/spells/MahoujinRecipeRegistrar.java',s)
# New defaults/ranges from original SERVER builder.
p=m/'config/MTConfig.java';s=p.read_text().replace('    private static final com.google.gson.Gson JSON', '''    public static boolean BLOOD_CIRCLE_SOLID_ONLY = true;
    public static int ASCENSION_BLOCK_CYCLE = 4;
    public static int RULE_BREAKER_MANA_COST = 5000;
    public static int RULE_BREAKER_RANGE = 20;
    public static java.util.List<String> RULE_BREAKER_ITEM_WHITELIST = java.util.List.of("minecraft:chorus_fruit");
    public static java.util.List<String> SPELL_BLACKLIST = java.util.List.of();
    public static java.util.List<String> ACTIVE_MAGIC_CIRCLE_NAMES = java.util.List.of("mahoutsukai:mahoujin_drain_life_barrier");
    public static java.util.List<Integer> ACTIVE_MAGIC_CIRCLE_LIMITS = java.util.List.of(10);
    public static int ACTIVE_MAGIC_CIRCLE_REPLACEMENT_TIME = 201;
    private static final com.google.gson.Gson JSON''');s=s.replace('var list = new java.util.ArrayList<String>();','var list = new java.util.ArrayList<Object>();');s=s.replace('''                        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())''','''                        if (field.getName().equals("ACTIVE_MAGIC_CIRCLE_LIMITS")) {
                            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("Integer limits required");
                            list.add(element.getAsBigDecimal().intValueExact());
                            continue;
                        }
                        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())''');s=s.replace('case "LEY_DISTANCE" -> 1;', 'case "LEY_DISTANCE", "ASCENSION_BLOCK_CYCLE" -> 1;').replace('case "RULE_BREAKER_DURABILITY",', 'case "RULE_BREAKER_MANA_COST", "RULE_BREAKER_RANGE", "ACTIVE_MAGIC_CIRCLE_REPLACEMENT_TIME", "RULE_BREAKER_DURABILITY",');p.write_text(s)
p=m/'util/Utils.java';s=p.read_text().replace('    public static void debug', '''    public static void setScrollMahou(ItemStack stack, stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou value) {
        stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.SCROLL_COMPONENT, value);
    }
    public static net.minecraft.core.BlockPos toBlockPos(net.minecraft.world.phys.Vec3 pos) { return net.minecraft.core.BlockPos.containing(pos); }
    public static String getRegistryName(net.minecraft.world.level.block.Block block, net.minecraft.world.level.Level level) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
    }
    public static void giveOrDrop(Player player, ItemStack stack) { if (!player.getInventory().add(stack)) player.drop(stack, false); }
    public static java.util.ArrayList<net.minecraft.core.BlockPos> findTilesInRange(net.minecraft.world.entity.LivingEntity player, int range,
            java.util.function.Predicate<net.minecraft.world.level.block.entity.BlockEntity> pred) {
        var min = net.minecraft.world.level.ChunkPos.containing(new net.minecraft.core.BlockPos((int)(player.getX()-range),0,(int)(player.getZ()-range)));
        var max = net.minecraft.world.level.ChunkPos.containing(new net.minecraft.core.BlockPos((int)(player.getX()+range),0,(int)(player.getZ()+range)));
        var found = new java.util.ArrayList<net.minecraft.core.BlockPos>();
        for(int x=min.x();x<=max.x();x++) for(int z=min.z();z<=max.z();z++)
            player.level().getChunk(x,z).getBlockEntities().forEach((pos,entity)-> { if(pred.test(entity)) found.add(pos); });
        return found;
    }
    public static void debug''');p.write_text(s)
p=m/'util/EffectUtil.java';s=p.read_text().replace(' public static boolean hasBuff', ''' public static final net.minecraft.world.level.block.state.BlockState AIR = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
 public static void debuff(LivingEntity entity, Holder<MobEffect> effect) { if (hasBuff(entity,effect)) entity.removeEffect(effect); }
 public static boolean inBlockBlacklist(net.minecraft.world.level.block.Block block, List<? extends String> values, Level level) {
  return block != null && inGenericBlacklist(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString(), values);
 }
 public static boolean inItemBlacklist(net.minecraft.world.item.Item item, List<? extends String> values, Level level) {
  return item != null && inGenericBlacklist(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString(), values);
 }
 public static boolean hasBuff''');p.write_text(s)
p=m/'item/ModItems.java';s=p.read_text().replace('    static {','''    public static final Map<String, PowderedCatalyst> catalystMap = new LinkedHashMap<>();
    public static final Supplier<Item> faeEssence = register("fae_essence", FaeEssence::new);
    static {
        register("scroll_probability_alter", stepsword.mahoutsukai.item.spells.secret.ProbabilityAlterSpellScroll::new);''',1).replace('    public static void init() {}','''    public static void init() {
        for (var item : REGISTERED.values()) if (item instanceof PowderedCatalyst catalyst) catalystMap.put(catalyst.getCatalystName(), catalyst);
    }''');p.write_text(s)
p=m/'block/ModBlocks.java';s=p.read_text().replace('    private static Supplier<Block> register(', '''    public static final Supplier<Block> spellClothBlock = plain("spell_cloth", new stepsword.mahoutsukai.block.spells.SpellCloth(), true);
    public static final Supplier<Block> mahoujin = plain("mahoujin", new stepsword.mahoutsukai.block.spells.Mahoujin(), true);
    public static final Supplier<Block> faySightEyesMahoujin = plain("mahoujin_fay_sight", new stepsword.mahoutsukai.block.spells.eyes.FaySightEyesMahoujin(), false);
    public static final Supplier<Block> ascensionMahoujin = plain("mahoujin_ascension", new stepsword.mahoutsukai.block.spells.displacement.AscensionMahoujin(), false);
    public static final Supplier<Block> probabilityAlterMahoujin = plain("mahoujin_probability_alter", new stepsword.mahoutsukai.block.spells.secret.ProbabilityAlterMahoujin(), false);
    private static Supplier<Block> plain(String name, Block block, boolean item) {
        var id = Identifier.fromNamespaceAndPath("mahoutsukai", name);
        Registry.register(BuiltInRegistries.BLOCK,id,block);
        if (item) {
            var blockItem = name.equals("mahoujin") ? new stepsword.mahoutsukai.block.itemblock.MahoujinBlockItem(block,name) : ((BlockBase)block).createItemBlock();
            Registry.register(BuiltInRegistries.ITEM,id,blockItem); ModItems.REGISTERED.put(name,blockItem);
        }
        return () -> block;
    }
    private static Supplier<Block> register(''');p.write_text(s)
p=m/'tile/ModTileEntities.java';s=p.read_text().replace('    public static void init() {}','''    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> Supplier<BlockEntityType<T>> register(String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory, net.minecraft.world.level.block.Block block) {
        var type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath("mahoutsukai",name),
            FabricBlockEntityTypeBuilder.create(factory,block).build()); return () -> type;
    }
    public static final Supplier<BlockEntityType<MahoujinTileEntity>> mahoujin = register("mahoujin",MahoujinTileEntity::new,ModBlocks.mahoujin.get());
    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.eyes.FaySightEyesMahoujinTileEntity>> faySightEyes = register("mahoujin_fay_sight",stepsword.mahoutsukai.tile.eyes.FaySightEyesMahoujinTileEntity::new,ModBlocks.faySightEyesMahoujin.get());
    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.displacement.AscensionMahoujinTileEntity>> ascension = register("mahoujin_ascension",stepsword.mahoutsukai.tile.displacement.AscensionMahoujinTileEntity::new,ModBlocks.ascensionMahoujin.get());
    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity>> probabilityAlter = register("mahoujin_probability_alter",stepsword.mahoutsukai.tile.secret.ProbabilityAlterMahoujinTileEntity::new,ModBlocks.probabilityAlterMahoujin.get());
    public static void init() {}''');p.write_text(s)
p=m/'fabric/MahouFabric.java';s=p.read_text().replace('        ModItems.init();','        ModItems.init();\n        stepsword.mahoutsukai.advancements.ModTriggers.init();').replace('        MahouNetworking.register();','''        stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar.init();
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> stepsword.mahoutsukai.mana.ActiveCircleConfig.clearActiveCircles());
        MahouNetworking.register();''');p.write_text(s)
p=m/'fabric/MahouNetworking.java';s=p.read_text().replace('    public static void register() {','''    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(stepsword.mahoutsukai.networking.KeyPressPacket.TYPE, stepsword.mahoutsukai.networking.KeyPressPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(stepsword.mahoutsukai.networking.KeyPressPacket.TYPE,
            (packet, context) -> stepsword.mahoutsukai.networking.KeyPressPacket.handle(packet, context.player()));''');p.write_text(s)
