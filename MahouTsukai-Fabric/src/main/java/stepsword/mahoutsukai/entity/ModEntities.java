package stepsword.mahoutsukai.entity;
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
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.<FaeEntity>create(MobCategory.CREATURE, FaeEntity::new)
   .dimensions(net.minecraft.world.entity.EntityDimensions.fixed(0.5F, 0.5F)).trackable(200, 3, false).build(ResourceKey.create(Registries.ENTITY_TYPE, FAE_ID)));
 public static final Supplier<EntityType<FaeEntity>> FAE = () -> FAE_TYPE;
 private static final Identifier DOOM_ID = Identifier.fromNamespaceAndPath("mahoutsukai", "doom");
 private static final EntityType<stepsword.mahoutsukai.doom.DoomEntity> DOOM_TYPE = Registry.register(BuiltInRegistries.ENTITY_TYPE, DOOM_ID,
  EntityType.Builder.<stepsword.mahoutsukai.doom.DoomEntity>of(stepsword.mahoutsukai.doom.DoomEntity::new, MobCategory.MISC)
   .sized(1, 1).clientTrackingRange(256).updateInterval(1).fireImmune().build(ResourceKey.create(Registries.ENTITY_TYPE, DOOM_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.doom.DoomEntity>> DOOM = () -> DOOM_TYPE;
 private static final Identifier KODOKU_ID=Identifier.fromNamespaceAndPath("mahoutsukai","kodoku_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.kodoku.KodokuEntity> KODOKU_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,KODOKU_ID,
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.<stepsword.mahoutsukai.entity.kodoku.KodokuEntity>create(MobCategory.MISC,stepsword.mahoutsukai.entity.kodoku.KodokuEntity::new)
   .dimensions(net.minecraft.world.entity.EntityDimensions.fixed(0.4F,0.3F)).trackable(64,3,false).build(ResourceKey.create(Registries.ENTITY_TYPE,KODOKU_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.kodoku.KodokuEntity>> KODOKU=()->KODOKU_TYPE;
 private static final Identifier FAMILIAR_ID=Identifier.fromNamespaceAndPath("mahoutsukai","familiar_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.FamiliarEntity> FAMILIAR_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,FAMILIAR_ID,
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.<stepsword.mahoutsukai.entity.FamiliarEntity>create(MobCategory.MISC,stepsword.mahoutsukai.entity.FamiliarEntity::new)
   .dimensions(net.minecraft.world.entity.EntityDimensions.fixed(0.5F,0.5F)).trackable(200,3,false).build(ResourceKey.create(Registries.ENTITY_TYPE,FAMILIAR_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.FamiliarEntity>> FAMILIAR=()->FAMILIAR_TYPE;
 private static final Identifier GANDR_ID=Identifier.fromNamespaceAndPath("mahoutsukai","gandr_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.GandrEntity> GANDR_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,GANDR_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.GandrEntity>of(stepsword.mahoutsukai.entity.mahoujin.GandrEntity::new,MobCategory.MISC).sized(1,1).clientTrackingRange(64).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,GANDR_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.GandrEntity>> GANDR=()->GANDR_TYPE;
 private static final Identifier RHO_AIAS_ID=Identifier.fromNamespaceAndPath("mahoutsukai","rho_aias_mahoujin_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity> RHO_AIAS_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,RHO_AIAS_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,RHO_AIAS_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.RhoAiasMahoujinEntity>> RHO_AIAS=()->RHO_AIAS_TYPE;
 private static final Identifier FALLEN_DOWN_ID=Identifier.fromNamespaceAndPath("mahoutsukai","fallen_down_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.FallenDownEntity> FALLEN_DOWN_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,FALLEN_DOWN_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.FallenDownEntity>of(stepsword.mahoutsukai.entity.mahoujin.FallenDownEntity::new,MobCategory.MISC).sized(1f,1f).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,FALLEN_DOWN_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.FallenDownEntity>> FALLEN_DOWN=()->FALLEN_DOWN_TYPE;
 private static final Identifier FALLEN_DOWN_BEAM_ID=Identifier.fromNamespaceAndPath("mahoutsukai","fallen_down_beam_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.FallenDownBeamEntity> FALLEN_DOWN_BEAM_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,FALLEN_DOWN_BEAM_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.FallenDownBeamEntity>of(stepsword.mahoutsukai.entity.mahoujin.FallenDownBeamEntity::new,MobCategory.MISC).sized(1f,1f).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,FALLEN_DOWN_BEAM_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.FallenDownBeamEntity>> FALLEN_DOWN_BEAM=()->FALLEN_DOWN_BEAM_TYPE;
 private static final Identifier MENTAL_DISPLACEMENT_ID=Identifier.fromNamespaceAndPath("mahoutsukai","mental_displacement");
 private static final EntityType<stepsword.mahoutsukai.entity.MentalDisplacementEntity> MENTAL_DISPLACEMENT_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,MENTAL_DISPLACEMENT_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.MentalDisplacementEntity>of(stepsword.mahoutsukai.entity.MentalDisplacementEntity::new,MobCategory.MISC).sized(1F,1F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,MENTAL_DISPLACEMENT_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.MentalDisplacementEntity>> MENTAL_DISPLACEMENT=()->MENTAL_DISPLACEMENT_TYPE;
 private static final Identifier SPATIAL_DISORIENTATION_ID=Identifier.fromNamespaceAndPath("mahoutsukai","spatial_mahoujin_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity> SPATIAL_DISORIENTATION_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,SPATIAL_DISORIENTATION_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,SPATIAL_DISORIENTATION_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.SpatialDisorientationMahoujinEntity>> SPATIAL_DISORIENTATION=()->SPATIAL_DISORIENTATION_TYPE;
 private static final Identifier SMITE_ID=Identifier.fromNamespaceAndPath("mahoutsukai","smite_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.SmiteEntity> SMITE_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,SMITE_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.SmiteEntity>of(stepsword.mahoutsukai.entity.mahoujin.SmiteEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,SMITE_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.SmiteEntity>> SMITE=()->SMITE_TYPE;
 private static final Identifier MORGAN_BALL_ID=Identifier.fromNamespaceAndPath("mahoutsukai","morgan_ball_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity> MORGAN_BALL_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,MORGAN_BALL_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity>of(stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,MORGAN_BALL_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.MorganBallEntity>> MORGAN_BALL=()->MORGAN_BALL_TYPE;
 private static final Identifier WEAPON_PROJECTILE_ID=Identifier.fromNamespaceAndPath("mahoutsukai","weapon_projectile_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.WeaponProjectileEntity> WEAPON_PROJECTILE_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,WEAPON_PROJECTILE_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.WeaponProjectileEntity>of(stepsword.mahoutsukai.entity.WeaponProjectileEntity::new,MobCategory.MISC).sized(1F,1F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,WEAPON_PROJECTILE_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.WeaponProjectileEntity>> WEAPON_PROJECTILE=()->WEAPON_PROJECTILE_TYPE;
 private static final Identifier GATE_WEAPON_PROJECTILE_ID=Identifier.fromNamespaceAndPath("mahoutsukai","gate_weapon_projectile_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity> GATE_WEAPON_PROJECTILE_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,GATE_WEAPON_PROJECTILE_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity>of(stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity::new,MobCategory.MISC).sized(1F,1F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,GATE_WEAPON_PROJECTILE_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity>> GATE_WEAPON_PROJECTILE=()->GATE_WEAPON_PROJECTILE_TYPE;
 private static final Identifier GATE_CHAIN_ID=Identifier.fromNamespaceAndPath("mahoutsukai","gate_chain_mahoujin_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.GateChainEntity> GATE_CHAIN_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,GATE_CHAIN_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.GateChainEntity>of(stepsword.mahoutsukai.entity.mahoujin.GateChainEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,GATE_CHAIN_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.GateChainEntity>> GATE_CHAIN=()->GATE_CHAIN_TYPE;
 private static final Identifier EXPLOSION_MAHOUJIN_ID=Identifier.fromNamespaceAndPath("mahoutsukai","mahoujin_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.MysticStaffMahoujinEntity> EXPLOSION_MAHOUJIN_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,EXPLOSION_MAHOUJIN_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.MysticStaffMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.MysticStaffMahoujinEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,EXPLOSION_MAHOUJIN_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.MysticStaffMahoujinEntity>> EXPLOSION_MAHOUJIN=()->EXPLOSION_MAHOUJIN_TYPE;
 private static final Identifier AOE_EXPLOSION_MAHOUJIN_ID=Identifier.fromNamespaceAndPath("mahoutsukai","aoe_mahoujin_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity> AOE_EXPLOSION_MAHOUJIN_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,AOE_EXPLOSION_MAHOUJIN_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,AOE_EXPLOSION_MAHOUJIN_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.MysticStaffAOEMahoujinEntity>> AOE_EXPLOSION_MAHOUJIN=()->AOE_EXPLOSION_MAHOUJIN_TYPE;
 private static final Identifier BEAM_MAHOUJIN_ID=Identifier.fromNamespaceAndPath("mahoutsukai","mahoujin_beam_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity> BEAM_MAHOUJIN_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,BEAM_MAHOUJIN_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,BEAM_MAHOUJIN_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.MysticStaffBeamMahoujinEntity>> BEAM_MAHOUJIN=()->BEAM_MAHOUJIN_TYPE;
 private static final Identifier BEAM_PROJECTILE_ID=Identifier.fromNamespaceAndPath("mahoutsukai","beam_projectile_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.BeamProjectileEntity> BEAM_PROJECTILE_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,BEAM_PROJECTILE_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.BeamProjectileEntity>of(stepsword.mahoutsukai.entity.mahoujin.BeamProjectileEntity::new,MobCategory.MISC).sized(1f,1f).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,BEAM_PROJECTILE_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.BeamProjectileEntity>> BEAM_PROJECTILE=()->BEAM_PROJECTILE_TYPE;
 private static final Identifier BUTTERFLY_ID=Identifier.fromNamespaceAndPath("mahoutsukai","butterfly_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.butterfly.ButterflyEntity> BUTTERFLY_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,BUTTERFLY_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.butterfly.ButterflyEntity>of(stepsword.mahoutsukai.entity.butterfly.ButterflyEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,BUTTERFLY_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.butterfly.ButterflyEntity>> BUTTERFLY=()->BUTTERFLY_TYPE;
 private static final Identifier LIGHTNING_ID=Identifier.fromNamespaceAndPath("mahoutsukai","lightning_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.LightningEntity> LIGHTNING_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,LIGHTNING_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.LightningEntity>of(stepsword.mahoutsukai.entity.mahoujin.LightningEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,LIGHTNING_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.LightningEntity>> LIGHTNING=()->LIGHTNING_TYPE;
 private static final Identifier LIGHTNING_BOLT_ID=Identifier.fromNamespaceAndPath("mahoutsukai","lightning_bolt_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.MahouLightningBoltEntity> LIGHTNING_BOLT_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,LIGHTNING_BOLT_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.MahouLightningBoltEntity>of(stepsword.mahoutsukai.entity.MahouLightningBoltEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,LIGHTNING_BOLT_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.MahouLightningBoltEntity>> LIGHTNING_BOLT=()->LIGHTNING_BOLT_TYPE;
 private static final Identifier REPLICA_TELEPORT_ID=Identifier.fromNamespaceAndPath("mahoutsukai","replica_teleport_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.ReplicaTeleportMahoujinEntity> REPLICA_TELEPORT_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,REPLICA_TELEPORT_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.ReplicaTeleportMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.ReplicaTeleportMahoujinEntity::new,MobCategory.MISC).sized(1F,1F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,REPLICA_TELEPORT_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.ReplicaTeleportMahoujinEntity>> REPLICA_TELEPORT=()->REPLICA_TELEPORT_TYPE;
 private static final Identifier GUN_ENTITY_ID=Identifier.fromNamespaceAndPath("mahoutsukai","gun_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.GunEntity> GUN_ENTITY_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,GUN_ENTITY_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.GunEntity>of(stepsword.mahoutsukai.entity.mahoujin.GunEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,GUN_ENTITY_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.GunEntity>> GUN_ENTITY=()->GUN_ENTITY_TYPE;
 private static final Identifier GUN_BEAM_PROJECTILE_ID=Identifier.fromNamespaceAndPath("mahoutsukai","gun_beam_projectile_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity> GUN_BEAM_PROJECTILE_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,GUN_BEAM_PROJECTILE_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity>of(stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity::new,MobCategory.MISC).sized(1f,1f).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,GUN_BEAM_PROJECTILE_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.GunBeamProjectileEntity>> GUN_BEAM_PROJECTILE=()->GUN_BEAM_PROJECTILE_TYPE;
 private static final Identifier WILLIAM_ID=Identifier.fromNamespaceAndPath("mahoutsukai","william_entity");
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.WilliamEntity> WILLIAM_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,WILLIAM_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.WilliamEntity>of(stepsword.mahoutsukai.entity.mahoujin.WilliamEntity::new,MobCategory.MISC).sized(1F,1F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,WILLIAM_ID)));
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.WilliamEntity>> WILLIAM=()->WILLIAM_TYPE;
 private static final Identifier AUTHORITY_ID=Identifier.fromNamespaceAndPath("mahoutsukai","authority_mahoujin_entity");
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity>> AUTHORITY=()->ModEntities.AUTHORITY_TYPE;
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity> AUTHORITY_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,AUTHORITY_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity>of(stepsword.mahoutsukai.entity.mahoujin.AuthorityMahoujinEntity::new,MobCategory.MISC).sized(0.6F,1.8F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,AUTHORITY_ID)));
 private static final Identifier RHONGOMYNIAD_ID=Identifier.fromNamespaceAndPath("mahoutsukai","rhongomyniad_entity");
 public static final Supplier<EntityType<stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity>> RHONGOMYNIAD=()->ModEntities.RHONGOMYNIAD_TYPE;
 private static final EntityType<stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity> RHONGOMYNIAD_TYPE=Registry.register(BuiltInRegistries.ENTITY_TYPE,RHONGOMYNIAD_ID,EntityType.Builder.<stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity>of(stepsword.mahoutsukai.entity.mahoujin.RhongomyniadEntity::new,MobCategory.MISC).sized(1F,2.5F).clientTrackingRange(200).updateInterval(1).build(ResourceKey.create(Registries.ENTITY_TYPE,RHONGOMYNIAD_ID)));
 public static void init() {
  legacy("aoe_explosion_mahoujin_entity",AOE_EXPLOSION_MAHOUJIN.get());
  legacy("authority_entity",AUTHORITY.get());
  legacy("beam_mahoujin_entity",BEAM_MAHOUJIN.get());
  legacy("explosion_mahoujin_entity",EXPLOSION_MAHOUJIN.get());
  legacy("gate_chain_entity",GATE_CHAIN.get());
  legacy("mental_displacement_entity",MENTAL_DISPLACEMENT.get());
  legacy("rho_aias_entity",RHO_AIAS.get());
  legacy("spatial_disorientation_entity",SPATIAL_DISORIENTATION.get());
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(FAMILIAR_TYPE,stepsword.mahoutsukai.entity.FamiliarEntity.registerAttributes());
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(KODOKU_TYPE,stepsword.mahoutsukai.entity.kodoku.KodokuEntity.registerAttributes());
  net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(FAE_TYPE, FaeEntity.registerAttributes());
 }
 /** Original IDs decode into the already registered native type, including its renderer/save ID. */
 private static void legacy(String name,EntityType<?> nativeType) {
  var id=Identifier.fromNamespaceAndPath("mahoutsukai",name);
  Registry.register(BuiltInRegistries.ENTITY_TYPE,id,EntityType.Builder.<net.minecraft.world.entity.Entity>of(
   (ignored,level)->nativeType.create(level,net.minecraft.world.entity.EntitySpawnReason.LOAD),MobCategory.MISC)
   .sized(nativeType.getDimensions().width(),nativeType.getDimensions().height())
   .build(ResourceKey.create(Registries.ENTITY_TYPE,id)));
 }
}
