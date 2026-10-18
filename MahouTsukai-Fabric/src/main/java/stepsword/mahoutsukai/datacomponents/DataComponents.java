package stepsword.mahoutsukai.datacomponents;

import stepsword.mahoutsukai.datacomponents.projected.ProjectedMahou;
import stepsword.mahoutsukai.datacomponents.stackholder.StackHolderMahou;
import net.minecraft.world.item.ItemStack;
import com.mojang.serialization.Codec;
import stepsword.mahoutsukai.datacomponents.selectedtarget.SelectedTargetMahou;
import net.minecraft.world.phys.Vec3;
import stepsword.mahoutsukai.util.Codecs;
import stepsword.mahoutsukai.datacomponents.replica.ReplicaMahou;
import stepsword.mahoutsukai.datacomponents.mysticcode.MysticCodeData;
import stepsword.mahoutsukai.datacomponents.kodoku.KodokuMahou;
import stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import stepsword.mahoutsukai.datacomponents.scrollmahou.ScrollMahou;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import stepsword.mahoutsukai.datacomponents.fiction.FictionMahou;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.datacomponents.gemmahou.GemMahou;

/** Only adapted components are registered. Original IDs and field formats retained. */
public final class DataComponents {
 public static final Codec<stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant> FOG_PROJECTOR_ENCHANT_COMPONENT_CODEC=RecordCodecBuilder.create(i->i.group(stepsword.mahoutsukai.util.Codecs.FOG_PROJECTOR_VALUES_CODEC.fieldOf("pv").forGetter(stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant::getPv)).apply(i,stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant::new));
 public static final DataComponentType<stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant> FOG_PROJECTOR_ENCHANT_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.fog.FogProjectorEnchant>builder().persistent(FOG_PROJECTOR_ENCHANT_COMPONENT_CODEC).networkSynchronized(ByteBufCodecs.fromCodec(FOG_PROJECTOR_ENCHANT_COMPONENT_CODEC)).build();
 public static final Codec<stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant> PROJECTOR_ENCHANT_COMPONENT_CODEC=RecordCodecBuilder.create(i->i.group(stepsword.mahoutsukai.util.Codecs.PROJECTOR_VALUES_CODEC.listOf().fieldOf("pv").forGetter(stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant::getPv)).apply(i,v->new stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant(new java.util.ArrayList<>(v))));
 public static final DataComponentType<stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant> PROJECTOR_ENCHANT_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.projectorenchant.ProjectorEnchant>builder().persistent(PROJECTOR_ENCHANT_COMPONENT_CODEC).networkSynchronized(ByteBufCodecs.fromCodec(PROJECTOR_ENCHANT_COMPONENT_CODEC)).build();
 public static final Codec<stepsword.mahoutsukai.datacomponents.lance.LanceMahou> LANCE_CODEC=RecordCodecBuilder.create(i->i.group(Codec.FLOAT.fieldOf("attackDamage").forGetter(stepsword.mahoutsukai.datacomponents.lance.LanceMahou::getAttackDamage),Codec.FLOAT.fieldOf("weight").forGetter(stepsword.mahoutsukai.datacomponents.lance.LanceMahou::getWeight)).apply(i,stepsword.mahoutsukai.datacomponents.lance.LanceMahou::new));
 public static final DataComponentType<stepsword.mahoutsukai.datacomponents.lance.LanceMahou> LANCE_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.lance.LanceMahou>builder().persistent(LANCE_CODEC).networkSynchronized(ByteBufCodecs.fromCodec(LANCE_CODEC)).build();
 public static final DataComponentType<stepsword.mahoutsukai.util.FogProjectorValues> FOG_PROJECTOR_VALUES_COMPONENT=DataComponentType.<stepsword.mahoutsukai.util.FogProjectorValues>builder().persistent(stepsword.mahoutsukai.util.Codecs.FOG_PROJECTOR_VALUES_CODEC).networkSynchronized(ByteBufCodecs.fromCodec(stepsword.mahoutsukai.util.Codecs.FOG_PROJECTOR_VALUES_CODEC)).build();
 public static final DataComponentType<stepsword.mahoutsukai.render.enchant.ProjectorValues> PROJECTOR_VALUES_COMPONENT=DataComponentType.<stepsword.mahoutsukai.render.enchant.ProjectorValues>builder().persistent(stepsword.mahoutsukai.util.Codecs.PROJECTOR_VALUES_CODEC).networkSynchronized(ByteBufCodecs.fromCodec(stepsword.mahoutsukai.util.Codecs.PROJECTOR_VALUES_CODEC)).build();
    public static final DataComponentType<Integer> MANA_CIRCUIT_MANA_COMPONENT = DataComponentType.<Integer>builder()
        .persistent(Codec.INT).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.INT).build();
    public static final DataComponentType<java.util.UUID> MANA_CIRCUIT_OWNER_COMPONENT = DataComponentType.<java.util.UUID>builder()
        .persistent(net.minecraft.core.UUIDUtil.CODEC).networkSynchronized(net.minecraft.core.UUIDUtil.STREAM_CODEC).build();
    public static final Codec<GemMahou> GEM_CODEC = GemMahou.CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, GemMahou> GEM_STREAM_CODEC = GemMahou.STREAM_CODEC;
    public static final DataComponentType<GemMahou> GEM_COMPONENT = DataComponentType.<GemMahou>builder()
        .persistent(GEM_CODEC).networkSynchronized(GEM_STREAM_CODEC).build();

   public static final Codec<FictionMahou> FICTION_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("fame").forGetter(FictionMahou::getFame),
            Codec.INT.fieldOf("style").forGetter(FictionMahou::getStyle),
            UUIDUtil.CODEC.fieldOf("caster").forGetter(FictionMahou::getCaster)
         )
         .apply(instance, FictionMahou::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FictionMahou> FICTION_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, FictionMahou::getFame, ByteBufCodecs.INT, FictionMahou::getStyle, UUIDUtil.STREAM_CODEC, FictionMahou::getCaster, FictionMahou::new
   );
    public static final DataComponentType<FictionMahou> FICTION_COMPONENT = DataComponentType.<FictionMahou>builder()
        .persistent(FICTION_CODEC).networkSynchronized(FICTION_STREAM_CODEC).build();
   public static final Codec<ScrollMahou> SCROLL_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("casterUUID").forGetter(ScrollMahou::getCasterUUID),
            CompoundTag.CODEC.optionalFieldOf("spellStorage").forGetter(ScrollMahou::getSpellStorage),
            Codec.STRING.optionalFieldOf("casterName").forGetter(ScrollMahou::getCasterName)
         )
         .apply(instance, ScrollMahou::new)
   );
   public static final StreamCodec<ByteBuf, ScrollMahou> SCROLL_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
      ScrollMahou::getCasterUUID,
      ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG),
      ScrollMahou::getSpellStorage,
      ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
      ScrollMahou::getCasterName,
      ScrollMahou::new
   );
    public static final DataComponentType<ScrollMahou> SCROLL_COMPONENT = DataComponentType.<ScrollMahou>builder()
        .persistent(SCROLL_CODEC).networkSynchronized(SCROLL_STREAM_CODEC).build();
   public static final Codec<RuleBreakerMahou> RULE_BREAKER_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("modulus").forGetter(RuleBreakerMahou::getModulus), Codec.INT.fieldOf("designation").forGetter(RuleBreakerMahou::getDesignation)
         )
         .apply(instance, RuleBreakerMahou::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, RuleBreakerMahou> RULE_BREAKER_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, RuleBreakerMahou::getModulus, ByteBufCodecs.INT, RuleBreakerMahou::getDesignation, RuleBreakerMahou::new
   );
    public static final DataComponentType<RuleBreakerMahou> RULE_BREAKER_COMPONENT = DataComponentType.<RuleBreakerMahou>builder()
        .persistent(RULE_BREAKER_CODEC).networkSynchronized(RULE_BREAKER_STREAM_CODEC).build();
   public static final Codec<KodokuMahou> KODOKU_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.fieldOf("kodoku").forGetter(KodokuMahou::getKodoku)).apply(instance,KodokuMahou::new));
   public static final StreamCodec<ByteBuf,KodokuMahou> KODOKU_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,KodokuMahou::getKodoku,KodokuMahou::new);
   public static final DataComponentType<KodokuMahou> KODOKU_COMPONENT=DataComponentType.<KodokuMahou>builder().persistent(KODOKU_CODEC).networkSynchronized(KODOKU_STREAM_CODEC).build();
    public static final Codec<stepsword.mahoutsukai.datacomponents.sword.SwordMahou> SWORD_CODEC=RecordCodecBuilder.create(instance->instance.group(
        Codec.FLOAT.fieldOf("attackDamage").forGetter(stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getAttackDamage),Codec.DOUBLE.fieldOf("innateCap").forGetter(stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getInnateCap)).apply(instance,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,stepsword.mahoutsukai.datacomponents.sword.SwordMahou> SWORD_STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.FLOAT,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getAttackDamage,ByteBufCodecs.DOUBLE,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::getInnateCap,stepsword.mahoutsukai.datacomponents.sword.SwordMahou::new);
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.sword.SwordMahou> SWORD_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.sword.SwordMahou>builder().persistent(SWORD_CODEC).networkSynchronized(SWORD_STREAM_CODEC).build();
    public static final Codec<stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou> STRENGTHENING_CODEC=RecordCodecBuilder.create(instance->instance.group(Codec.BOOL.fieldOf("strengthened").forGetter(stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::isUnbreakable),Codec.INT.fieldOf("uses").forGetter(stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::getUses)).apply(instance,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou> STRENGTHENING_STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::isUnbreakable,ByteBufCodecs.INT,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::getUses,stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou::new);
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou> STRENGTHENING_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.strengthening.StrengtheningMahou>builder().persistent(STRENGTHENING_CODEC).networkSynchronized(STRENGTHENING_STREAM_CODEC).build();
   public static final Codec<StackHolderMahou> STACK_HOLDERS_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(ItemStack.CODEC.optionalFieldOf("stack").forGetter(StackHolderMahou::getStack)).apply(instance, StackHolderMahou::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, StackHolderMahou> STACK_HOLDERS_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(ItemStack.STREAM_CODEC), StackHolderMahou::getStack, StackHolderMahou::new
   );
   public static final DataComponentType<StackHolderMahou> STACK_HOLDERS_COMPONENT = DataComponentType.<StackHolderMahou>builder().persistent(STACK_HOLDERS_CODEC).networkSynchronized(STACK_HOLDERS_STREAM_CODEC).build();
   public static final Codec<ProjectedMahou> PROJECTED_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.BOOL.fieldOf("projected").forGetter(ProjectedMahou::getProjected)).apply(instance, ProjectedMahou::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ProjectedMahou> PROJECTED_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, ProjectedMahou::getProjected, ProjectedMahou::new
   );
   public static final DataComponentType<ProjectedMahou> PROJECTED_COMPONENT = DataComponentType.<ProjectedMahou>builder().persistent(PROJECTED_CODEC).networkSynchronized(PROJECTED_STREAM_CODEC).build();
    public static final Codec<stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou> MODE_CODEC=RecordCodecBuilder.create(i->i.group(Codec.INT.fieldOf("mode").forGetter(stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou::getMode)).apply(i,stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou::new));
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou> MODE_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou>builder().persistent(MODE_CODEC).networkSynchronized(StreamCodec.composite(ByteBufCodecs.INT,stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou::getMode,stepsword.mahoutsukai.datacomponents.modemahou.ModeMahou::new)).build();
    public static final Codec<stepsword.mahoutsukai.util.MahouColor> COLOR_CODEC=Codec.FLOAT.listOf().comapFlatMap(values->net.minecraft.util.Util.fixedSize(values,3).map(c->new stepsword.mahoutsukai.util.MahouColor(c.get(0),c.get(1),c.get(2))),color->java.util.List.of(color.r,color.g,color.b));
    public static final Codec<stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou> COLORS_CODEC=RecordCodecBuilder.create(i->i.group(COLOR_CODEC.listOf().fieldOf("colors").forGetter(stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou::getColors)).apply(i,stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou::new));
    public static final StreamCodec<ByteBuf,stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou> COLORS_STREAM_CODEC=new StreamCodec<>(){
      public stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou decode(ByteBuf buffer){int n=buffer.readInt();if(n<0 || n>64)throw new IllegalArgumentException("Invalid color count");var colors=new java.util.ArrayList<stepsword.mahoutsukai.util.MahouColor>();for(int i=0;i<n;i++)colors.add(new stepsword.mahoutsukai.util.MahouColor(buffer.readFloat(),buffer.readFloat(),buffer.readFloat()));return new stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou(colors);}
      public void encode(ByteBuf buffer,stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou colors){buffer.writeInt(colors.getColors().size());for(var c:colors.getColors()){buffer.writeFloat(c.r);buffer.writeFloat(c.g);buffer.writeFloat(c.b);}}
    };
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou> COLORS_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou>builder().persistent(COLORS_CODEC).networkSynchronized(COLORS_STREAM_CODEC).build();
    public static final DataComponentType<stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou> SECOND_COLORS_COMPONENT=DataComponentType.<stepsword.mahoutsukai.datacomponents.itemcolor.ColorMahou>builder().persistent(COLORS_CODEC).networkSynchronized(COLORS_STREAM_CODEC).build();
   public static final Codec<MysticCodeData> MYSTIC_CODE_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(MysticCodeData::getSlot),
            Codec.INT.fieldOf("color").forGetter(MysticCodeData::getColor),
            Codec.STRING.fieldOf("slot1").forGetter(MysticCodeData::getSlot1),
            Codec.STRING.fieldOf("slot2").forGetter(MysticCodeData::getSlot2),
            Codec.STRING.fieldOf("slot3").forGetter(MysticCodeData::getSlot3)
         )
         .apply(instance, MysticCodeData::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, MysticCodeData> MYSTIC_CODE_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      MysticCodeData::getSlot,
      ByteBufCodecs.INT,
      MysticCodeData::getColor,
      ByteBufCodecs.STRING_UTF8,
      MysticCodeData::getSlot1,
      ByteBufCodecs.STRING_UTF8,
      MysticCodeData::getSlot2,
      ByteBufCodecs.STRING_UTF8,
      MysticCodeData::getSlot3,
      MysticCodeData::new
   );
   public static final DataComponentType<MysticCodeData> MYSTIC_CODE_COMPONENT=DataComponentType.<MysticCodeData>builder().persistent(MYSTIC_CODE_CODEC).networkSynchronized(MYSTIC_CODE_STREAM_CODEC).build();

   public static final Codec<ReplicaMahou> REPLICA_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Vec3.CODEC.optionalFieldOf("loc").forGetter(ReplicaMahou::getLoc),
            Codec.STRING.optionalFieldOf("dim").forGetter(ReplicaMahou::getDim),
            Codec.BOOL.fieldOf("friendly").forGetter(ReplicaMahou::getFriendly),
            Codec.INT.fieldOf("slot").forGetter(ReplicaMahou::getSlot),
            Codec.STRING.listOf().fieldOf("types").forGetter(ReplicaMahou::getTypes)
         )
         .apply(instance, ReplicaMahou::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ReplicaMahou> REPLICA_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(Codecs.VEC3_STREAM),
      ReplicaMahou::getLoc,
      ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
      ReplicaMahou::getDim,
      ByteBufCodecs.BOOL,
      ReplicaMahou::getFriendly,
      ByteBufCodecs.INT,
      ReplicaMahou::getSlot,
      Codecs.STRING_ARRAY_LIST,
      ReplicaMahou::getTypes,
      ReplicaMahou::new
   );
   public static final DataComponentType<ReplicaMahou> REPLICA_COMPONENT=DataComponentType.<ReplicaMahou>builder().persistent(REPLICA_CODEC).networkSynchronized(REPLICA_STREAM_CODEC).build();

   public static final DataComponentType<SelectedTargetMahou> SELECTED_TARGET_COMPONENT=DataComponentType.<SelectedTargetMahou>builder().persistent(RecordCodecBuilder.create(i->i.group(Codec.INT.fieldOf("targetId").forGetter(SelectedTargetMahou::getTargetId)).apply(i,SelectedTargetMahou::new))).networkSynchronized(ByteBufCodecs.INT.map(SelectedTargetMahou::new,SelectedTargetMahou::getTargetId)).build();
    public static final DataComponentType<Boolean> SHOULD_RENDER_COMPONENT=DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build();
    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","should_render"),SHOULD_RENDER_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","fog_projector_enchant_mahou"),FOG_PROJECTOR_ENCHANT_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","projector_enchant_mahou"),PROJECTOR_ENCHANT_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","lance_mahou"),LANCE_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","fog_projector_values_mahou"),FOG_PROJECTOR_VALUES_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","projector_values_mahou"),PROJECTOR_VALUES_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","selected_target_mahou"),SELECTED_TARGET_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","replica_mahou"),REPLICA_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","mystic_code_mahou"),MYSTIC_CODE_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","colors_mahou"),COLORS_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","second_colors_mahou"),SECOND_COLORS_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","mode_mahou"),MODE_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","projected_mahou"),PROJECTED_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","stack_holder_mahou"),STACK_HOLDERS_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","sword_mahou"),SWORD_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","strengthening_mahou"),STRENGTHENING_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai","kodoku_mahou"),KODOKU_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "rule_breaker_mahou"), RULE_BREAKER_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "scroll_mahou"), SCROLL_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "fiction_mahou"), FICTION_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "mana_circuit_mana_mahou"), MANA_CIRCUIT_MANA_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "mana_circuit_owner_mahou"), MANA_CIRCUIT_OWNER_COMPONENT);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "gem_mahou"), GEM_COMPONENT);
    }
}
