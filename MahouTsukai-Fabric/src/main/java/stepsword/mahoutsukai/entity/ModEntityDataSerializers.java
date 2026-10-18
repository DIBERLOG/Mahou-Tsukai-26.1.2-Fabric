package stepsword.mahoutsukai.entity;
import net.minecraft.network.syncher.*;
import stepsword.mahoutsukai.dataattachments.mahoujinentity.MahoujinData;
public final class ModEntityDataSerializers {
 public static final EntityDataSerializer<MahoujinData> MAHOUJIN=EntityDataSerializer.forValueType(MahoujinData.STREAM_CODEC);
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","mahoujin"),MAHOUJIN);}
 public static final EntityDataSerializer<net.minecraft.world.phys.Vec3> VEC3=EntityDataSerializer.forValueType(net.minecraft.world.phys.Vec3.STREAM_CODEC);
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","vec3"),VEC3);}
 public static final EntityDataSerializer<java.util.List<Integer>> INT_LIST=EntityDataSerializer.forValueType(net.minecraft.network.codec.ByteBufCodecs.VAR_INT.apply(net.minecraft.network.codec.ByteBufCodecs.list()));
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","int_list"),INT_LIST);}
 public static final EntityDataSerializer<java.util.Optional<java.util.UUID>> OPTIONAL_UUID=EntityDataSerializer.forValueType(net.minecraft.core.UUIDUtil.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs::optional));
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","optional_uuid"),OPTIONAL_UUID);}
 public static void init(){}
}
