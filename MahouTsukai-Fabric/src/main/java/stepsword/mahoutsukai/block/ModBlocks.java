package stepsword.mahoutsukai.block;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import stepsword.mahoutsukai.block.circuits.ManaCircuitBlock;
import stepsword.mahoutsukai.block.circuits.ManaCircuitMagitechBlock;
import stepsword.mahoutsukai.block.itemblock.ManaCircuitBlockItem;
import stepsword.mahoutsukai.item.ModItems;

public final class ModBlocks {
    public static final Supplier<Block> fogProjector=plain("fog_projector",new FogProjector(),true);
    public static final Supplier<Block> projector=plain("mahoujin_projector",new MahoujinProjector(),true);
    public static final Supplier<Block> circuit = register("mana_circuit", new ManaCircuitBlock());
    public static final Supplier<Block> circuitMagitech = register("mana_circuit_magitech", new ManaCircuitMagitechBlock());

    public static final Supplier<Block> spellClothBlock = plain("spell_cloth", new stepsword.mahoutsukai.block.spells.SpellCloth(), true);
    public static final Supplier<Block> mahoujin = plain("mahoujin", new stepsword.mahoutsukai.block.spells.Mahoujin(), true);
    public static final Supplier<Block> faySightEyesMahoujin = plain("mahoujin_fay_sight", new stepsword.mahoutsukai.block.spells.eyes.FaySightEyesMahoujin(), false);
    public static final Supplier<Block> ascensionMahoujin = plain("mahoujin_ascension", new stepsword.mahoutsukai.block.spells.displacement.AscensionMahoujin(), false);
    public static final Supplier<Block> probabilityAlterMahoujin = plain("mahoujin_probability_alter", new stepsword.mahoutsukai.block.spells.secret.ProbabilityAlterMahoujin(), false);
    public static final Supplier<Block> geasMahoujin = plain("mahoujin_geas",new stepsword.mahoutsukai.block.spells.secret.GeasMahoujin(),false);
    public static final Supplier<Block> damageExchangeMahoujin = plain("mahoujin_damage_exchange",new stepsword.mahoutsukai.block.spells.exchange.DamageExchangeMahoujin(),false);
    public static final Supplier<Block> immunityExchangeMahoujin = plain("mahoujin_immunity_exchange",new stepsword.mahoutsukai.block.spells.exchange.ImmunityExchangeMahoujin(),false);
    public static final Supplier<Block> protectiveDisplacementMahoujin = plain("mahoujin_protective_displacement",new stepsword.mahoutsukai.block.spells.displacement.ProtectiveDisplacementMahoujin(),false);
    public static final Supplier<Block> chronalExchangeMahoujin = plain("mahoujin_chronal_exchange",new stepsword.mahoutsukai.block.spells.exchange.ChronalExchangeMahoujin(),false);
    public static final Supplier<Block> catalystExchangeMahoujin = plain("mahoujin_catalyst_exchange",new stepsword.mahoutsukai.block.spells.exchange.CatalystExchangeMahoujin(),false);
    public static final Supplier<Block> alchemicalExchangeMahoujin = plain("mahoujin_alchemical_exchange",new stepsword.mahoutsukai.block.spells.exchange.AlchemicalExchangeMahoujin(),false);
    public static final Supplier<Block> durabilityExchangeMahoujin = plain("mahoujin_durability_exchange",new stepsword.mahoutsukai.block.spells.exchange.DurabilityExchangeMahoujin(),false);
    public static final Supplier<Block> alarmBoundaryMahoujin = plain("mahoujin_alarm_barrier",new stepsword.mahoutsukai.block.spells.boundary.AlarmBoundaryMahoujin(),false);
    public static final Supplier<Block> gravityBoundaryMahoujin = plain("mahoujin_gravity_barrier",new stepsword.mahoutsukai.block.spells.boundary.GravityBoundaryMahoujin(),false);
    public static final Supplier<Block> displacementBoundaryMahoujin = plain("mahoujin_displacement_barrier",new stepsword.mahoutsukai.block.spells.boundary.DisplacementBoundaryMahoujin(),false);
    public static final Supplier<Block> drainLifeBoundaryMahoujin = plain("mahoujin_drain_life_barrier",new stepsword.mahoutsukai.block.spells.boundary.DrainLifeBoundaryMahoujin(),false);
    public static final Supplier<Block> raiseEnclosureBoundaryMahoujin = plain("mahoujin_raise_enclosure",new stepsword.mahoutsukai.block.spells.boundary.RaiseEnclosureBoundaryMahoujin(),false);
    public static final Supplier<Block> tangibleBoundaryMahoujin = plain("mahoujin_tangible_barrier",new stepsword.mahoutsukai.block.spells.boundary.TangibleBoundaryMahoujin(),false);
    public static final Supplier<Block> contractMahoujin = plain("mahoujin_contract",new stepsword.mahoutsukai.block.spells.exchange.ContractMahoujin(),false);
    public static final Supplier<Block> invisibleBarrierBlock = plain("invisible_barrier_block",new stepsword.mahoutsukai.block.spells.boundary.InvisibleBarrierBlock(),false);
    public static final Supplier<Block> orderedDisplacementMahoujin = plain("mahoujin_ordered_displacement",new stepsword.mahoutsukai.block.spells.displacement.OrderedDisplacementMahoujin(),false);
    public static final Supplier<Block> equivalentDisplacementMahoujin = plain("mahoujin_equivalent_displacement",new stepsword.mahoutsukai.block.spells.displacement.EquivalentDisplacementMahoujin(),false);
    public static final Supplier<Block> mysticEyesMahoujin = plain("mahoujin_mystic_eyes",new stepsword.mahoutsukai.block.spells.eyes.BindingEyesMahoujin(),false);
    public static final Supplier<Block> retributionMahoujin = plain("mahoujin_retribution",new stepsword.mahoutsukai.block.spells.secret.RetributionMahoujin(),false);
    public static final Supplier<Block> projectileDisplacementMahoujin = plain("mahoujin_projectile_displacement",new stepsword.mahoutsukai.block.spells.displacement.ProjectileDisplacementMahoujin(),false);
    public static final Supplier<Block> deathCollectionMahoujin = plain("mahoujin_death_collection",new stepsword.mahoutsukai.block.spells.eyes.DeathCollectionEyesMahoujin(),false);
    public static final Supplier<Block> strengtheningMahoujin = plain("mahoujin_strengthening",new stepsword.mahoutsukai.block.spells.projection.StrengtheningMahoujin(),false);
    public static final Supplier<Block> damageReplicationMahoujin=plain("mahoujin_damage_replication",new stepsword.mahoutsukai.block.spells.mystic.DamageReplicationMahoujin(),false);
    public static final Supplier<Block> selectiveDisplacementMahoujin=plain("mahoujin_selective_displacement",new stepsword.mahoutsukai.block.spells.secret.SelectiveDisplacementMahoujin(),false);
    public static final Supplier<Block> projectionMahoujin=plain("mahoujin_projection",new stepsword.mahoutsukai.block.spells.projection.ProjectionMahoujin(),false);
    public static final Supplier<Block> proximityProjectionMahoujin=plain("mahoujin_proximity_projection",new stepsword.mahoutsukai.block.spells.projection.ProximityProjectionMahoujin(),false);
    public static final Supplier<Block> summonFamiliarMahoujin = plain("mahoujin_summon_familiar",new stepsword.mahoutsukai.block.spells.familiar.SummonFamiliarMahoujin(),false);
    public static final Supplier<Block> recallFamiliarMahoujin = plain("mahoujin_recall_familiar",new stepsword.mahoutsukai.block.spells.familiar.RecallFamiliarMahoujin(),false);
    public static final Supplier<Block> swapFamiliarMahoujin = plain("mahoujin_swap_familiar",new stepsword.mahoutsukai.block.spells.familiar.SwapFamiliarMahoujin(),false);
    public static final Supplier<Block> familiarsGardenMahoujin = plain("mahoujin_familiars_garden",new stepsword.mahoutsukai.block.spells.familiar.FamiliarsGardenMahoujin(),false);
    public static final Supplier<Block> blackFireBlock = plain("black_fire",new stepsword.mahoutsukai.block.BlackFireBlock(),false);
    public static final Supplier<Block> blackFlameMahoujin = plain("mahoujin_black_flame",new stepsword.mahoutsukai.block.spells.eyes.BlackFlameEyesMahoujin(),false);
    public static final Supplier<Block> murkyFluid=plain("murky_water",new stepsword.mahoutsukai.fluids.MurkyWaterBlock(),false);
    public static final Supplier<Block> powerConsolidationMahoujin=plain("mahoujin_power_consolidation",new stepsword.mahoutsukai.block.spells.projection.PowerConsolidationMahoujin(),false);
    public static final Supplier<Block> insightEyesMahoujin=plain("mahoujin_insight",new stepsword.mahoutsukai.block.spells.eyes.InsightEyesMahoujin(),false);
    public static final Supplier<Block> predictionMahoujin=plain("mahoujin_prediction",new stepsword.mahoutsukai.block.spells.eyes.ClairvoyanceEyesMahoujin(),false);
    public static final Supplier<Block> cupOfHeavenMahoujin=plain("mahoujin_cup_of_heaven",new stepsword.mahoutsukai.block.spells.mystic.CupOfHeavenMahoujin(),false);
    public static final Supplier<Block> reversionEyesMahoujin=plain("mahoujin_reversion_eyes",new stepsword.mahoutsukai.block.spells.eyes.ReversionEyesMahoujin(),false);
    public static final Supplier<Block> presenceConcealmentMahoujin=plain("mahoujin_presence_concealment",new stepsword.mahoutsukai.block.spells.secret.PresenceConcealmentMahoujin(),false);
    public static final Supplier<Block> gandrMahoujin=plain("mahoujin_gandr",new stepsword.mahoutsukai.block.spells.secret.GandrMahoujin(),false);
    public static final Supplier<Block> rhoAiasMahoujin=plain("mahoujin_rho_aias",new stepsword.mahoutsukai.block.spells.mystic.RhoAiasMahoujin(),false);
    public static final Supplier<Block> fallenDownMahoujin=plain("mahoujin_fallen_down",new stepsword.mahoutsukai.block.spells.secret.FallenDownMahoujin(),false);
    public static final Supplier<Block> borrowedAuthorityMahoujin=plain("mahoujin_borrowed_authority",new stepsword.mahoutsukai.block.spells.mystic.BorrowedAuthorityMahoujin(),false);
    public static final Supplier<Block> mentalDisplacementMahoujin=plain("mahoujin_mental_displacement",new stepsword.mahoutsukai.block.spells.displacement.MentalDisplacementMahoujin(),false);
    public static final Supplier<Block> possessEntityMahoujin=plain("mahoujin_possess_entity",new stepsword.mahoutsukai.block.spells.familiar.PossessEntityMahoujin(),false);
    public static final Supplier<Block> spatialDisorientationMahoujin=plain("mahoujin_spatial_disorientation",new stepsword.mahoutsukai.block.spells.mystic.SpatialDisorientationMahoujin(),false);
    public static final Supplier<Block> weaponShooterMahoujin=plain("mahoujin_weapon_shooter",new stepsword.mahoutsukai.block.spells.projection.WeaponShooterMahoujin(),false);
    public static final Supplier<Block> treasuryProjectionMahoujin=plain("mahoujin_treasury_projection",new stepsword.mahoutsukai.block.spells.projection.TreasuryProjectionMahoujin(),false);
    public static final Supplier<Block> realityMarbleMahoujin=plain("mahoujin_reality_marble",new stepsword.mahoutsukai.block.spells.projection.RealityMarbleMahoujin(),false);
    public static final Supplier<Block> mysticStaffMahoujin=plain("mahoujin_mystic_staff",new stepsword.mahoutsukai.block.spells.mystic.MysticStaffMahoujin(),false);
    public static final Supplier<Block> butterflyEffectMahoujin=plain("mahoujin_butterfly_effect",new stepsword.mahoutsukai.block.spells.familiar.ButterflyEffectMahoujin(),false);
    public static final Supplier<Block> scryingMahoujin=plain("mahoujin_scrying",new stepsword.mahoutsukai.block.spells.displacement.ScryingMahoujin(),false);
    private static Supplier<Block> plain(String name, Block block, boolean item) {
        var id = Identifier.fromNamespaceAndPath("mahoutsukai", name);
        Registry.register(BuiltInRegistries.BLOCK,id,block);
        if (item) {
            var blockItem = name.equals("mahoujin") ? new stepsword.mahoutsukai.block.itemblock.MahoujinBlockItem(block,name) : ((BlockBase)block).createItemBlock();
            Registry.register(BuiltInRegistries.ITEM,id,blockItem); ModItems.REGISTERED.put(name,blockItem);
        }
        return () -> block;
    }
    private static Supplier<Block> register(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath("mahoutsukai", name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        var item = Registry.register(BuiltInRegistries.ITEM, id, new ManaCircuitBlockItem(block, name));
        ModItems.REGISTERED.put(name, item);
        return () -> block;
    }
    public static void init() {}
}
