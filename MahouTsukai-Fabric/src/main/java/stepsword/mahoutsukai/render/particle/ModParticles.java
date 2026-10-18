package stepsword.mahoutsukai.render.particle;
public final class ModParticles {
 private static final net.minecraft.core.particles.SimpleParticleType PETAL_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","particle_petal"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> PETAL=()->PETAL_TYPE;
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> FOG1=()->ModParticles.FOG_TYPE;
 private static final net.minecraft.core.particles.SimpleParticleType FOG_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","fog1"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 private static final net.minecraft.core.particles.SimpleParticleType MAGIC_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","native_magic"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> MAGIC=()->MAGIC_TYPE;
 private static final net.minecraft.core.particles.SimpleParticleType YELLOW_LIGHTNING_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","particle_yellow_lightning"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> YELLOW_LIGHTNING=()->YELLOW_LIGHTNING_TYPE;
 private static final net.minecraft.core.particles.SimpleParticleType RED_LIGHTNING_TYPE=net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE,net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","particle_red_lightning"),net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple(true));
 public static final java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> RED_LIGHTNING=()->RED_LIGHTNING_TYPE;
 public static void init() {}
}
