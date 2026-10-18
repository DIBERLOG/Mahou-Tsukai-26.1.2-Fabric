package stepsword.mahoutsukai.item;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import stepsword.mahoutsukai.item.catalysts.*;

/** Only original items whose complete class dependencies have been adapted. */
public final class ModItems {
    public static final Map<String, Item> REGISTERED = new LinkedHashMap<>();
    public static final Supplier<Item> hammer = register("hammer", Hammer::new);
    public static final Supplier<Item> mortarAndPestle = register("mortar_and_pestle", MortarAndPestle::new);
    public static final Supplier<Item> dagger = register("dagger", Dagger::new);
    public static final Supplier<Item> ruleBreaker = register("rule_breaker", stepsword.mahoutsukai.item.rulebreaker.RuleBreaker::new);
    public static final Map<String, PowderedCatalyst> catalystMap = new LinkedHashMap<>();
    public static final Supplier<Item> faeEssence = register("fae_essence", FaeEssence::new);
    public static final Supplier<Item> murkyBucket = register("murky_bucket", () -> new net.minecraft.world.item.BucketItem(stepsword.mahoutsukai.fluids.ModFluids.MURKY_STILL,ItemBase.withRegisteredId(new Item.Properties()).craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));
    public static final Supplier<Item> insightEyesScroll=register("scroll_insight",stepsword.mahoutsukai.item.spells.eyes.InsightEyesSpellScroll::new);
    public static final Supplier<Item> clairvoyanceEyesScroll=register("scroll_prediction",stepsword.mahoutsukai.item.spells.eyes.ClairvoyanceEyesSpellScroll::new);
    public static final Supplier<Item> cupOfHeavenScroll=register("scroll_cup_of_heaven",stepsword.mahoutsukai.item.spells.mystic.CupOfHeavenSpellScroll::new);
    public static final Supplier<Item> reversionEyesScroll=register("scroll_reversion_eyes",stepsword.mahoutsukai.item.spells.eyes.ReversionEyesSpellScroll::new);
    public static final Supplier<Item> theripper=register("theripper",stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper::new);
    public static final Supplier<Item> presenceConcealmentScroll=register("scroll_presence_concealment",stepsword.mahoutsukai.item.spells.secret.PresenceConcealmentSpellScroll::new);
    public static final Supplier<Item> gandrScroll=register("scroll_gandr",stepsword.mahoutsukai.item.spells.secret.GandrSpellScroll::new);
    public static final Supplier<Item> rhoAiasScroll=register("scroll_rho_aias",stepsword.mahoutsukai.item.spells.mystic.RhoAiasSpellScroll::new);
    public static final Supplier<Item> fallenDownScroll=register("scroll_fallen_down",stepsword.mahoutsukai.item.spells.secret.FallenDownSpellScroll::new);
    public static final Supplier<Item> borrowedAuthorityScroll=register("scroll_borrowed_authority",stepsword.mahoutsukai.item.spells.mystic.BorrowedAuthoritySpellScroll::new);
    public static final Supplier<Item> mentalDisplacementScroll=register("scroll_mental_displacement",stepsword.mahoutsukai.item.spells.displacement.MentalDisplacementSpellScroll::new);
    public static final Supplier<Item> possessEntityScroll=register("scroll_possess_entity",stepsword.mahoutsukai.item.spells.familiar.PossessEntitySpellScroll::new);
    public static final Supplier<Item> spatialDisorientationScroll=register("scroll_spatial_disorientation",stepsword.mahoutsukai.item.spells.mystic.SpatialDisorientationSpellScroll::new);
    public static final Supplier<Item> spatialStaff=register("spatial_disorientation_staff",stepsword.mahoutsukai.item.spells.mystic.SpatialDisorientation.SpatialDisorientationStaff::new);
    public static final Supplier<Item> caliburn=register("caliburn",stepsword.mahoutsukai.item.caliburn.Caliburn::new);
    public static final Supplier<Item> morgan=register("morgan",stepsword.mahoutsukai.item.morgan.Morgan::new);
    public static final Supplier<Item> weaponShooterScroll=register("scroll_weapon_shooter",stepsword.mahoutsukai.item.spells.projection.WeaponShooterSpellScroll::new);
    public static final Supplier<Item> weaponShooterBow=register("weapon_projectile_bow",stepsword.mahoutsukai.item.spells.projection.WeaponProjectile.WeaponProjectileBow::new);
    public static final Supplier<Item> funbook=register("funbook",stepsword.mahoutsukai.item.MahouTsukaiFunbook::new);
    public static final Supplier<Item> treasuryProjectionScroll=register("scroll_treasury_projection",stepsword.mahoutsukai.item.spells.projection.TreasuryProjectionSpellScroll::new);
    public static final Supplier<Item> treasuryProjectionGauntlet=register("treasury_projection_gauntlet",stepsword.mahoutsukai.item.spells.projection.TreasuryProjection.TreasuryProjectionGauntlet::new);
    public static final Supplier<Item> realityMarbleScroll=register("scroll_reality_marble",stepsword.mahoutsukai.item.spells.projection.RealityMarbleSpellScroll::new);
    public static final Supplier<Item> explosionStaffScroll=register("scroll_mystic_staff",stepsword.mahoutsukai.item.spells.mystic.MysticStaffSpellScroll::new);
    public static final Supplier<Item> explosionStaff=register("mystic_staff",stepsword.mahoutsukai.item.spells.mystic.MysticStaff.MysticStaff::new);
    public static final Supplier<Item> butterflyEffectScroll=register("scroll_butterfly_effect",stepsword.mahoutsukai.item.spells.familiar.ButterflyEffectSpellScroll::new);
    public static final Supplier<Item> emrysStaff=register("staff_emrys",stepsword.mahoutsukai.item.emrys.StaffEmrys::new);
    public static final Supplier<Item> mysticCode=register("mystic_code",stepsword.mahoutsukai.item.mysticcode.MysticCode::new);
    public static final Supplier<Item> mysticCodeFirstSorcery=register("mystic_code_first_sorcery",stepsword.mahoutsukai.item.mysticcode.MysticCodeFirstSorcery::new);
    public static final Supplier<Item> replica=register("replica",stepsword.mahoutsukai.item.replica.Replica::new);
    public static final Supplier<Item> nobu=register("nobu",stepsword.mahoutsukai.item.nobu.Nobu::new);
    public static final Supplier<Item> guidebook=register("guidebook",MahouTsukaiGuidebook::new);
    public static final Supplier<Item> william=register("william",stepsword.mahoutsukai.item.william.William::new);
    static {
        if(stepsword.mahoutsukai.fabric.JoustingCompatibility.loaded())JoustingItems.register();
        register("scroll_selective_displacement",stepsword.mahoutsukai.item.spells.secret.SelectiveDisplacementSpellScroll::new);
        register("scroll_damage_replication",stepsword.mahoutsukai.item.spells.mystic.DamageReplicationSpellScroll::new);
        register("clarent",stepsword.mahoutsukai.item.clarent.Clarent::new);
        register("proximity_projection_keys",stepsword.mahoutsukai.item.spells.projection.ProximityProjection.ProximityProjectionKeys::new);
        register("scroll_proximity_projection",stepsword.mahoutsukai.item.spells.projection.ProximityProjectionSpellScroll::new);
        register("scroll_summon_familiar",stepsword.mahoutsukai.item.spells.familiar.SummonFamiliarSpellScroll::new);
        register("scroll_recall_familiar",stepsword.mahoutsukai.item.spells.familiar.RecallFamiliarSpellScroll::new);
        register("scroll_swap_familiar",stepsword.mahoutsukai.item.spells.familiar.SwapFamiliarSpellScroll::new);
        register("scroll_familiars_garden",stepsword.mahoutsukai.item.spells.familiar.FamiliarsGardenSpellScroll::new);
        register("scroll_black_flame",stepsword.mahoutsukai.item.spells.eyes.BlackFlameEyesSpellScroll::new);
        register("scroll_projection",stepsword.mahoutsukai.item.spells.projection.ProjectionSpellScroll::new);
        register("scroll_strengthening",stepsword.mahoutsukai.item.spells.projection.StrengtheningSpellScroll::new);
        register("kodoku",stepsword.mahoutsukai.item.kodoku.KodokuItem::new);
        register("faecake",Faecake::new);
        register("scroll_death_collection",stepsword.mahoutsukai.item.spells.eyes.DeathCollectionEyesSpellScroll::new);
        register("scroll_projectile_displacement",stepsword.mahoutsukai.item.spells.displacement.ProjectileDisplacementSpellScroll::new);
        register("scroll_retribution",stepsword.mahoutsukai.item.spells.secret.RetributionSpellScroll::new);
        register("scroll_mystic_eyes",stepsword.mahoutsukai.item.spells.eyes.BindingEyesSpellScroll::new);
        register("scroll_boundary_tangible",stepsword.mahoutsukai.item.spells.boundary.TangibleBoundarySpellScroll::new);
        register("scroll_boundary_raise_enclosure",stepsword.mahoutsukai.item.spells.boundary.RaiseEnclosureBoundarySpellScroll::new);
        register("scroll_boundary_drain_life",stepsword.mahoutsukai.item.spells.boundary.DrainLifeBoundarySpellScroll::new);
        register("scroll_boundary_displacement",stepsword.mahoutsukai.item.spells.boundary.DisplacementBoundarySpellScroll::new);
        register("scroll_boundary_gravity",stepsword.mahoutsukai.item.spells.boundary.GravityBoundarySpellScroll::new);
        register("scroll_boundary_alarm",stepsword.mahoutsukai.item.spells.boundary.AlarmBoundarySpellScroll::new);
        register("scroll_protective_displacement",stepsword.mahoutsukai.item.spells.displacement.ProtectiveDisplacementSpellScroll::new);
        register("scroll_immunity_exchange",stepsword.mahoutsukai.item.spells.exchange.ImmunityExchangeSpellScroll::new);
        register("scroll_damage_exchange",stepsword.mahoutsukai.item.spells.exchange.DamageExchangeSpellScroll::new);
        register("scroll_geas",stepsword.mahoutsukai.item.spells.secret.GeasSpellScroll::new);
        register("scroll_probability_alter", stepsword.mahoutsukai.item.spells.secret.ProbabilityAlterSpellScroll::new);
        register("scroll_doom", stepsword.mahoutsukai.doom.DoomScroll::new);
        register("scroll_fay_sight", stepsword.mahoutsukai.item.spells.eyes.FaySightEyesSpellScroll::new);
        register("scroll_ascension", stepsword.mahoutsukai.item.spells.displacement.AscensionSpellScroll::new);
        register("attuned_diamond", stepsword.mahoutsukai.item.attunedgems.AttunedDiamond::new);
        register("attuned_emerald", stepsword.mahoutsukai.item.attunedgems.AttunedEmerald::new);
        register("attuner", stepsword.mahoutsukai.item.attunedgems.Attuner::new);
        register("mortar", Mortar::new);
        register("pestle", Pestle::new);
        register("powdered_diamond", PowderedDiamond::new);
        register("powdered_emerald", PowderedEmerald::new);
        register("powdered_ender", PowderedEnder::new);
        register("powdered_eye", PowderedEye::new);
        register("powdered_gold", PowderedGold::new);
        register("powdered_iron", PowderedIron::new);
        register("powdered_quartz", PowderedQuartz::new);
    }

    static Supplier<Item> register(String name, Supplier<? extends Item> factory) {
        Identifier id = Identifier.fromNamespaceAndPath("mahoutsukai", name);
        ItemBase.pushRegistrationId(id);
        try {
            Item item = Registry.register(BuiltInRegistries.ITEM, id, factory.get());
            REGISTERED.put(name, item);
            return () -> item;
        } finally {
            ItemBase.popRegistrationId();
        }
    }

    public static void init() {
        for (var item : REGISTERED.values()) if (item instanceof PowderedCatalyst catalyst) catalystMap.put(catalyst.getCatalystName(), catalyst);
    }
}
