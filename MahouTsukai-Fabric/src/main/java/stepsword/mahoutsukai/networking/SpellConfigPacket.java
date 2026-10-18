package stepsword.mahoutsukai.networking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.config.MTConfig;
/** Original server-controlled settings used by native client prediction and knife attributes. */
public record SpellConfigPacket(int predictionRange,int knifeDurability,double knifeDamage,double knifeSpeed,double movementSpeed) implements CustomPacketPayload {
 public static final Type<SpellConfigPacket> TYPE=new Type<>(Identifier.fromNamespaceAndPath("mahoutsukai","fabric_spell_config"));
 public static final StreamCodec<RegistryFriendlyByteBuf,SpellConfigPacket> STREAM_CODEC=StreamCodec.composite(ByteBufCodecs.INT,SpellConfigPacket::predictionRange,ByteBufCodecs.INT,SpellConfigPacket::knifeDurability,ByteBufCodecs.DOUBLE,SpellConfigPacket::knifeDamage,ByteBufCodecs.DOUBLE,SpellConfigPacket::knifeSpeed,ByteBufCodecs.DOUBLE,SpellConfigPacket::movementSpeed,SpellConfigPacket::new);
 public SpellConfigPacket {
  if(predictionRange<0 || predictionRange>100000000 || knifeDurability<0 || knifeDurability>1000000000 || !Double.isFinite(knifeDamage) || knifeDamage<0 || knifeDamage>1000000 || !Double.isFinite(knifeSpeed) || knifeSpeed< -1000000 || knifeSpeed>1000000 || !Double.isFinite(movementSpeed) || movementSpeed< -10000000 || movementSpeed>1000000)throw new IllegalArgumentException("Spell setting outside original range");
 }
 public static SpellConfigPacket current(){return new SpellConfigPacket(MTConfig.CLAIRVOYANCE_RANGE,MTConfig.RIPPER_DURABILITY,MTConfig.RIPPER_DAMAGE,MTConfig.RIPPER_SPEED,MTConfig.RIPPER_MOVEMENT_SPEED);}
 public void apply(){MTConfig.CLAIRVOYANCE_RANGE=predictionRange;MTConfig.RIPPER_DURABILITY=knifeDurability;MTConfig.RIPPER_DAMAGE=knifeDamage;MTConfig.RIPPER_SPEED=knifeSpeed;MTConfig.RIPPER_MOVEMENT_SPEED=movementSpeed;}
 public Type<SpellConfigPacket> type(){return TYPE;}
}
