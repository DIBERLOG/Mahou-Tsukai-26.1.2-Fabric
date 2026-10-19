package stepsword.mahoutsukai.potion;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
public final class ModEffects {
 public static final Holder<MobEffect> FAY_SIGHT_EYES = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
  Identifier.fromNamespaceAndPath("mahoutsukai", "fay_sight_eyes"), new FaySightEyesPotion());
 public static final Holder<MobEffect> BLEEDING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
  Identifier.fromNamespaceAndPath("mahoutsukai", "bleeding"), new BleedingPotion());
 public static final Holder<MobEffect> PROBABILITY_ALTER = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
  Identifier.fromNamespaceAndPath("mahoutsukai", "probability_alter"), new ProbabilityAlterPotion());
 public static final Holder<MobEffect> ACTING=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","acting"),new ActingPotion());
 public static final Holder<MobEffect> ABYSS=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","abyss"),new AbyssPotion());
 public static final Holder<MobEffect> MANA_TAKEN_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","mana_taken_cooldown"),new ManaTakenCooldownPotion());
 public static int getColorNumber(int r, int g, int b) { return (r << 16) | (g << 8) | b; }
 public static final Holder<MobEffect> IMMUNITY_EXCHANGE = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","immunity_exchange"),new ImmunityExchangePotion());
 public static final Holder<MobEffect> BINDING_EYES = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","binding_eyes"),new BindingEyesPotion());
 public static final Holder<MobEffect> BOUND = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","bound"),new BoundPotion());
 public static final Holder<MobEffect> DEATH_COLLECTION_EYES = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","death_collection_eyes"),new DeathCollectionEyesPotion());
 public static final Holder<MobEffect> WOUNDED=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","wounded"),new WoundedPotion());
 public static final Holder<MobEffect> SELECTIVE_DISPLACEMENT=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","selective_displacement"),new SelectiveDisplacementPotion());
 public static final Holder<MobEffect> SELECTIVE_DISPLACEMENT_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","selective_displacement_cooldown"),new SelectiveDisplacementCooldownPotion());
 public static final Holder<MobEffect> PEACEFUL_GARDEN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","peaceful_garden"),new GardenPeacePotion());
 public static final Holder<MobEffect> INTERESTING_BLOCK=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","interesting_block"),new InterestingBlockPotion());
 public static final Holder<MobEffect> BLACK_FLAME_EYES=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","black_flame_eyes"),new BlackFlameEyesPotion());
 public static final Holder<MobEffect> BLACK_BURNING=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","black_burning"),new BlackBurningPotion());
 public static final Holder<MobEffect> INSIGHT=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","insight_eyes"),new InsightEyesPotion());
 public static final Holder<MobEffect> CLAIRVOYANCE=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","clairvoyance_eyes"),new ClairvoyanceEyesPotion());
 public static final Holder<MobEffect> REVERSION_EYES=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","reversion_eyes"),new ReversionEyesPotion());
 public static final Holder<MobEffect> REVERTED=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","reverted"),new RevertedPotion());
 public static final Holder<MobEffect> RIPPER_INVISIBILITY=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","ripper_invis"),new RipperInvisPotion());
 public static final Holder<MobEffect> RIPPER_INVIS_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","ripper_invis_cooldown"),new RipperInvisCooldownPotion());
 public static final Holder<MobEffect> RIPPER_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","ripper_cooldown"),new RipperCooldownPotion());
 public static final Holder<MobEffect> BORROWED_AUTHORITY=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","borrowed_authority"),new BorrowedAuthorityPotion());
 public static final Holder<MobEffect> BA_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","ba_cooldown"),new BorrowedAuthorityCooldownPotion());
 public static final Holder<MobEffect> AUTHORITY_JUMP=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","authority_jump"),new AuthorityJumpPotion());
 public static final Holder<MobEffect> WALL_STICK=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","wall_stick"),new WallStickPotion());
 public static final Holder<MobEffect> FLUNG=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","flung"),new FlungPotion());
 public static final Holder<MobEffect> CM_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","cm_cooldown"),new CaliburnMorganCooldownPotion());
 public static final Holder<MobEffect> RAGE=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","rage"),new RagePotion());
 public static final Holder<MobEffect> FORESIGHT=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","foresight"),new ForesightPotion());
 public static final Holder<MobEffect> MISFORTUNE=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","misfortune"),new MisfortunePotion());
 public static final Holder<MobEffect> ZAP_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","zap_cooldown"),new ZapCooldownPotion());
 public static final Holder<MobEffect> REPLICA_SHOCKWAVE_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","rpsw_cooldown"),new ReplicaShockwaveCooldownPotion());
 public static final Holder<MobEffect> LEGACY_REPLICA_SHOCKWAVE_COOLDOWN=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","replica_shockwave_cooldown"),new ReplicaShockwaveCooldownPotion());
 public static final Holder<MobEffect> FEAR=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","fear"),new FearPotion());
 public static final Holder<MobEffect> CONFUSION=Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","confusion"),new ConfusionPotion());
 public static void init() {}
}
