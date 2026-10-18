package stepsword.mahoutsukai.fabric;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.config.MTConfig;

/** Fabric replacement for NeoForge's automatic synchronization of server capacities. */
public record EconomyConfigPacket(int diamond, int emerald, int circuit, int magitech, int ruleBreakerDurability, int clarentDurability, int selectiveRange, int keysDurability, int lookRange, int gardenRange, int familiarRenderRange, double lakeFogRange) implements CustomPacketPayload {
    public static final Type<EconomyConfigPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai", "fabric_economy_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EconomyConfigPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, EconomyConfigPacket::diamond, ByteBufCodecs.INT, EconomyConfigPacket::emerald,
        ByteBufCodecs.INT, EconomyConfigPacket::circuit, ByteBufCodecs.INT, EconomyConfigPacket::magitech,
        ByteBufCodecs.INT, EconomyConfigPacket::ruleBreakerDurability, ByteBufCodecs.INT, EconomyConfigPacket::clarentDurability, ByteBufCodecs.INT, EconomyConfigPacket::selectiveRange, ByteBufCodecs.INT, EconomyConfigPacket::keysDurability, ByteBufCodecs.INT, EconomyConfigPacket::lookRange, ByteBufCodecs.INT, EconomyConfigPacket::gardenRange, ByteBufCodecs.INT, EconomyConfigPacket::familiarRenderRange, ByteBufCodecs.DOUBLE, EconomyConfigPacket::lakeFogRange, EconomyConfigPacket::new);
    public EconomyConfigPacket(int diamond, int emerald, int circuit, int magitech) {
        this(diamond, emerald, circuit, magitech, 40,1500,40);
    }
    public EconomyConfigPacket(int diamond,int emerald,int circuit,int magitech,int ruleBreakerDurability) { this(diamond,emerald,circuit,magitech,ruleBreakerDurability,1500,40); }
    public EconomyConfigPacket(int diamond,int emerald,int circuit,int magitech,int ruleBreakerDurability,int clarentDurability) {this(diamond,emerald,circuit,magitech,ruleBreakerDurability,clarentDurability,40);}
    public EconomyConfigPacket(int diamond,int emerald,int circuit,int magitech,int ruleBreakerDurability,int clarentDurability,int selectiveRange) {this(diamond,emerald,circuit,magitech,ruleBreakerDurability,clarentDurability,selectiveRange,30,100,7,8);}
    public EconomyConfigPacket(int diamond,int emerald,int circuit,int magitech,int ruleBreakerDurability,int clarentDurability,int selectiveRange,int keysDurability,int lookRange) {this(diamond,emerald,circuit,magitech,ruleBreakerDurability,clarentDurability,selectiveRange,keysDurability,lookRange,7,8);}
    public EconomyConfigPacket(int diamond,int emerald,int circuit,int magitech,int ruleBreakerDurability,int clarentDurability,int selectiveRange,int keysDurability,int lookRange,int gardenRange,int familiarRenderRange) {this(diamond,emerald,circuit,magitech,ruleBreakerDurability,clarentDurability,selectiveRange,keysDurability,lookRange,gardenRange,familiarRenderRange,8.0);}
    public EconomyConfigPacket {
        if(!Double.isFinite(lakeFogRange) || lakeFogRange<0 || lakeFogRange>100000000) throw new IllegalArgumentException("Lake fog radius outside original range");
        if(gardenRange<0 || gardenRange>100000000 || familiarRenderRange<1 || familiarRenderRange>64) throw new IllegalArgumentException("Familiar radius outside original range");
        if(keysDurability<0 || keysDurability>100000000 || lookRange<0 || lookRange>100000000) throw new IllegalArgumentException("Proximity values outside original range");
        if(selectiveRange<0 || selectiveRange>100000000) throw new IllegalArgumentException("Selection radius outside original range");
        if(clarentDurability<0 || clarentDurability>100000000) throw new IllegalArgumentException("Clarent durability outside original range");
        if (ruleBreakerDurability < 0 || ruleBreakerDurability > 10000000)
            throw new IllegalArgumentException("Rule Breaker durability outside original range");
        for (int value : new int[]{diamond, emerald, circuit, magitech})
            if (value < 0 || value > 100000000) throw new IllegalArgumentException("Capacity outside original range");
    }
    public static EconomyConfigPacket current() {
        return new EconomyConfigPacket(MTConfig.ATTUNED_DIAMOND_CAPACITY, MTConfig.ATTUNED_EMERALD_CAPACITY,
            MTConfig.MANA_CIRCUIT_CAPACITY, MTConfig.MANA_CIRCUIT_MAGITECH_CAPACITY, MTConfig.RULE_BREAKER_DURABILITY, MTConfig.CLARENT_DURABILITY,MTConfig.SELECTIVE_DISPLACEMENT_RANGE,MTConfig.PROXIMITY_PROJECTION_DURABILITY,MTConfig.GLOBAL_LOOK_RANGE,MTConfig.FAMILIARS_GARDEN_RANGE,MTConfig.FAMILIAR_INTERESTING_BLOCK_RENDER_RANGE,MTConfig.POWER_CONSOLIDATION_FOG_RADIUS);
    }
    public void apply() {
        MTConfig.POWER_CONSOLIDATION_FOG_RADIUS=lakeFogRange;
        MTConfig.FAMILIARS_GARDEN_RANGE=gardenRange;
        MTConfig.FAMILIAR_INTERESTING_BLOCK_RENDER_RANGE=familiarRenderRange;
        MTConfig.PROXIMITY_PROJECTION_DURABILITY=keysDurability;
        MTConfig.GLOBAL_LOOK_RANGE=lookRange;
        MTConfig.RULE_BREAKER_DURABILITY = ruleBreakerDurability;
        MTConfig.CLARENT_DURABILITY = clarentDurability;
        MTConfig.SELECTIVE_DISPLACEMENT_RANGE=selectiveRange;
        MTConfig.ATTUNED_DIAMOND_CAPACITY = diamond;
        MTConfig.ATTUNED_EMERALD_CAPACITY = emerald;
        MTConfig.MANA_CIRCUIT_CAPACITY = circuit;
        MTConfig.MANA_CIRCUIT_MAGITECH_CAPACITY = magitech;
    }
    @Override public Type<EconomyConfigPacket> type() { return TYPE; }
}
