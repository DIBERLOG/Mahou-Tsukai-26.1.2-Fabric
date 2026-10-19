package stepsword.mahoutsukai.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Original settings required by the connected gameplay systems. */
public final class MTConfig {
 public static int POWER_CONSOLIDATION_FEAR_RADIUS=15;
 public static boolean ONE_LIFE=false;
 public static java.util.List<String> BUFF_CLEAR_ITEMS=java.util.List.of("minecraft:milk_bucket");
 public static int LEY_RENDER_DISTANCE=100;
 public static int LEY_RENDER_HEIGHT=70;
 public static boolean LEY_RUNES=true;

 public static boolean SHOW_CAST_HINTS=true;
 public static int CLIENT_GUI_MAHOU_PLACEMENT_X=10;
 public static int CLIENT_GUI_MAHOU_PLACEMENT_Y=20;

 public static boolean HEAVENS_CUP_CLIENT_CIRCLE=true;
 public static boolean MYSTIC_STAFF_EXPLOSION_SOUND=true;
 public static boolean POWER_CONSOLIDATION_RETRO_FOG_FIX=false;
 public static boolean NO_SHADERS=false;
 public static java.util.List<String> MAGIC_CIRCLES=java.util.List.of();
 public static java.util.List<String> MAGIC_RUNES=java.util.List.of();
 public static boolean CACHE_IMAGES=false;
 public static int RHONGOMYNIAD_MAX_SMITES=10;
 public static int RHONGOMYNIAD_RANGE=20;
 public static double RHONGOMYNIAD_NERF_FACTOR=0.6;
 public static double RHONGOMYNIAD_SPEED_FACTOR=1.4;
 public static boolean RHONGOMYNIAD_RESPECT_IMMUNE=false;
 public static double PROJECTOR_DEFAULT_ROTATION_SPEED=1;
 public static boolean PROJECTOR_CONTINUES_ROTATE_WHEN_OFF=false;
 public static double PROJECTOR_OFFSET_CAP=60;
 public static double PROJECTOR_SIZE_CAP=100;
 public static double FOG_PROJECTOR_MAX_RADIUS=30;

    public static int MANA_CIRCUIT_MAGITECH_CAPACITY = 100000;
    public static int MANA_CIRCUIT_CAPACITY = 100000;
    public static int MANA_CIRCUIT_RANGE = 10;
    public static int ATTUNED_DIAMOND_CAPACITY = 10000;
    public static int ATTUNED_EMERALD_CAPACITY = 5000;
    public static boolean MAGIC_DAMAGES_PLAYER = false;
    public static boolean MAGIC_DAMAGE_FLAT = true;
    public static double MAGIC_DAMAGE_VALUE = 1.0;
    public static boolean MAGIC_DAMAGE_GENERIC = true;
    public static boolean CREATIVE_IGNORES_MANA_COSTS = true;
    public static double MANA_RECOVERY_SLEEP = 0.5;
    public static double DEATH_COLLECTION_DRAIN_PER_SOUL = 1.0;
    public static double DEATH_COLLECTION_SOUL_VALUE_PLAYER = 1.0;
    public static boolean DEATH_COLLECTION_NEGATIVE_REGEN_PUNISHMENT = true;
    public static int MANA_REGEN_PER_TICK = 1;
    public static double MANA_ADDED_SCALING_REGEN_PER_TICK = 0.0;
    public static double MANA_REGEN_HUNGER_FACTOR = 1.0;
    public static int LEY_PUNISHMENT = -1;
    public static double LEY_FACTOR = 0.01;
    public static boolean LEY_FLAT = false;
    public static int LEY_DISTANCE = 300;
    public static int LEY_OFFSET = 0;
    public static boolean LEY_AFFECTS_MANA = true;
    public static boolean LEY_BLACKLIST = true;
    public static java.util.List<String> LEY_DIMENSION_LIST = java.util.List.of();
    public static boolean LEY_ELYTRA_LIMITED_TO_FAY_SIGHT = false;
    public static double FAE_SPAWN_RATE = 1.0;
    public static boolean FAE_NOISE = true;
    public static double MANA_REGEN_PER_GEAS = 3.0;
    public static double GEAS_MAX_MANA_REGEN = 10000000;
    public static double WILLIAM_PERSONAL_MANA_FACTOR = 0.01;
    public static boolean CREATIVE_MODE_SPELLS = false;
    public static int ASCENSION_SCROLL_MANA_COST = 30;
    public static int FAY_SIGHT_MANA_COST = 100;
    public static int FAY_SIGHT_TIME = 600;
    public static boolean SHOW_BLEEDING = true;
    public static int RULE_BREAKER_DURABILITY = 40;
    public static int RULE_BREAKER_DURATION = 800;
    public static boolean BLEEDING_FROM_DAGGER_ONLY = false;
    public static boolean BLOOD_CIRCLE_SOLID_ONLY = true;
    public static int ASCENSION_BLOCK_CYCLE = 4;
    public static int RULE_BREAKER_MANA_COST = 5000;
    public static int RULE_BREAKER_RANGE = 20;
    public static java.util.List<String> RULE_BREAKER_ITEM_WHITELIST = java.util.List.of("minecraft:chorus_fruit");
    public static java.util.List<String> SPELL_BLACKLIST = java.util.List.of();
    public static java.util.List<String> ACTIVE_MAGIC_CIRCLE_NAMES = java.util.List.of("mahoutsukai:mahoujin_drain_life_barrier");
    public static java.util.List<Integer> ACTIVE_MAGIC_CIRCLE_LIMITS = java.util.List.of(10);
    public static int ACTIVE_MAGIC_CIRCLE_REPLACEMENT_TIME = 201;
    public static int GEAS_MANA_COST = 30;
    public static int GLOBAL_LOOK_RANGE = 100;
    public static java.util.List<String> GEAS_BLACKLIST = java.util.List.of("minecraft:armor_stand");
    public static int DAMAGE_EXCHANGE_MANA_COST = 40;
    public static int DAMAGE_EXCHANGE_MANA_GAIN = 20;
    public static double DAMAGE_EXCHANGE_REDUCE_TO = 1.0;
    public static int DAMAGE_EXCHANGE_CAP = 5;
    public static int IMMUNITY_EXCHANGE_MANA_COST = 400;
    public static int IMMUNITY_EXCHANGE_TIME = 1200;
    public static int PROTECTIVE_DISPLACEMENT_MANA_COST = 50;
   public static int ALCHEMICAL_EXCHANGE_BLOCK_CYCLE = 20;
   public static int ALCHEMICAL_EXCHANGE_HEIGHT = 5;
   public static int ALCHEMICAL_EXCHANGE_MANA_COST = 16;
   public static java.util.List<? extends String> ALCHEMICAL_DIRT_CLASS = java.util.Arrays.asList("minecraft:dirt", "minecraft:sand", "minecraft:soul_sand", "minecraft:snow_block");
   public static java.util.List<? extends String> ALCHEMICAL_STONE_CLASS = java.util.Arrays.asList(
      "minecraft:stone",
      "minecraft:ice",
      "minecraft:packed_ice",
      "minecraft:sandstone",
      "minecraft:red_sandstone",
      "minecraft:gravel",
      "minecraft:end_stone",
      "minecraft:purpur_block",
      "minecraft:magma_block",
      "minecraft:netherrack",
      "minecraft:terracotta"
   );
   public static java.util.List<? extends String> ALCHEMICAL_METAL_CLASS = java.util.Arrays.asList("minecraft:iron_ore", "minecraft:gold_ore", "minecraft:nether_quartz_ore");
   public static java.util.List<? extends String> ALCHEMICAL_GEM_CLASS = java.util.Arrays.asList("minecraft:diamond_ore", "minecraft:emerald_ore");
   public static java.util.List<? extends String> ALCHEMICAL_CLAY_CLASS = java.util.Arrays.asList(
      "minecraft:clay", "minecraft:coal_ore", "minecraft:redstone_ore", "minecraft:lapis_ore"
   );
   public static java.util.List<? extends String> ALCHEMICAL_WOOD_CLASS = java.util.Arrays.asList(
      "minecraft:oak_log",
      "minecraft:birch_log",
      "minecraft:acacia_log",
      "minecraft:jungle_log",
      "minecraft:spruce_log",
      "minecraft:dark_oak_log",
      "minecraft:cactus",
      "minecraft:pumpkin",
      "minecraft:melon",
      "minecraft:brown_mushroom_block",
      "minecraft:red_mushroom_block",
      "minecraft:hay_block",
      "minecraft:nether_wart_block"
   );
   public static java.util.List<? extends String> ALCHEMICAL_SPECIAL_CLASS = java.util.Arrays.asList(
      "minecraft:prismarine", "minecraft:glowstone", "minecraft:sea_lantern", "minecraft:obsidian", "minecraft:slime_block"
   );
   public static java.util.List<? extends String> ALCHEMICAL_GRASS_CLASS = java.util.Arrays.asList("minecraft:grass_block", "minecraft:mycelium");
   public static java.util.List<? extends String> ALCHEMICAL_FLUID_CLASS = java.util.Arrays.asList("minecraft:lava", "minecraft:water");
   public static java.util.List<? extends String> ALCHEMICAL_WILDCARD_CLASS = java.util.Arrays.asList();
   public static int CATALYST_EXCHANGE_BLOCK_CYCLE = 4;
   public static int CATALYST_EXCHANGE_MANA_COST = 50;
   public static int CHRONAL_EXCHANGE_BLOCK_CYCLE = 20;
   public static int CHRONAL_EXCHANGE_MANA_GAIN_LOSS = 10;
   public static int DURABILITY_EXCHANGE_BLOCK_CYCLE = 20;
   public static int DURABILITY_EXCHANGE_MANA_GAIN_CAP = 10;
   public static double DURABILITY_EXCHANGE_EFFICIENCY = 1.0;
   public static java.util.List<? extends String> DURABILITY_EXCHANGE_BLACKLIST = new java.util.ArrayList<>();
    public static java.util.List<Double> DURABILITY_EXCHANGE_TAX_BRACKETS = java.util.List.of(5000.0,20000.0,100000.0);
    public static java.util.List<Double> DURABILITY_EXCHANGE_TAX_RATES = java.util.List.of(0.02,0.03,0.04);
    public static boolean ALARM_ACTION_BAR_MESSAGES = false;
    public static int ALARM_BARRIER_CYCLE = 20;
    public static int ALARM_BARRIER_MANA_COST = 1;
    public static int ALARM_BARRIER_MANA_CYCLE = 10;
    public static int ALARM_BARRIER_RADIUS = 10;
    public static int CONTRACT_BLOCK_CYCLE = 4;
    public static int CONTRACT_MANA_COST = 10;
    public static int CONTRACT_RADIUS = 10;
    public static int DISPLACEMENT_BARRIER_CYCLE = 4;
    public static int DISPLACEMENT_BARRIER_MANA_COST = 1;
    public static int DISPLACEMENT_BARRIER_MANA_CYCLE = 5;
    public static int DISPLACEMENT_BARRIER_RADIUS = 11;
    public static int DRAIN_LIFE_AFFECTED_LIMIT = 100000;
    public static int DRAIN_LIFE_BARRIER_CYCLE = 20;
    public static int DRAIN_LIFE_BARRIER_MANA_COST = 5;
    public static int DRAIN_LIFE_BARRIER_MANA_CYCLE = 1;
    public static int DRAIN_LIFE_BARRIER_RADIUS = 10;
    public static double DRAIN_LIFE_DAMAGE = 2.0;
    public static java.util.List<? extends String> DRAIN_LIFE_ENTITY_BLACKLIST = new java.util.ArrayList<>();
    public static double DRAIN_LIFE_HEAL_FACTOR = 0.5;
    public static boolean DRAIN_LIFE_IGNORE_HUNGER = false;
    public static int DRAIN_LIFE_MANA_REGEN = 1;
    public static double DRAIN_LIFE_PUNISH_RESIST = 1.8;
    public static int ENCLOSURE_BARRIER_CYCLE = 20;
    public static int ENCLOSURE_BARRIER_MANA_COST = 20;
    public static int ENCLOSURE_BARRIER_RADIUS = 5;
    public static int GRAVITY_BARRIER_CYCLE = 1;
    public static double GRAVITY_BARRIER_FACTOR = 1.4;
    public static int GRAVITY_BARRIER_MANA_COST = 1;
    public static int GRAVITY_BARRIER_MANA_CYCLE = 2;
    public static double GRAVITY_BARRIER_MAX = 5.0E7;
    public static int GRAVITY_BARRIER_RADIUS = 10;
    public static boolean RHO_AIAS_KILL_DEBUG = false;
    public static int TANGIBLE_BARRIER_CYCLE = 20;
    public static int TANGIBLE_BARRIER_MANA_COST = 1;
    public static int TANGIBLE_BARRIER_MANA_CYCLE = 3;
    public static int TANGIBLE_BARRIER_RADIUS = 10;
    public static int EQUIVALENT_DISPLACEMENT_BLOCK_CYCLE = 4;
    public static boolean EQUIVALENT_DISPLACEMENT_DIMENSIONAL_TRAVEL = true;
    public static int EQUIVALENT_DISPLACEMENT_MANA_COST = 60;
    public static double EQUIVALENT_DISPLACEMENT_MAX_DISTANCE = -1.0;
    public static int EQUIVALENT_DISPLACEMENT_RECEIVE_COOLDOWN = 40;
    public static int MYSTIC_EYES_MANA_COST = 320;
    public static int MYSTIC_EYES_RANGE_FROM_LOOK_VEC = 4;
    public static int MYSTIC_EYES_RANGE_FROM_USER = 5;
    public static int MYSTIC_EYES_TIME = 600;
    public static int ORDERED_DISPLACEMENT_BLOCK_CYCLE = 4;
    public static int ORDERED_DISPLACEMENT_MANA_COST = 40;
    public static int ORDERED_DISPLACEMENT_RADIUS = 20;
    public static int ORDERED_DISPLACEMENT_RECEIVE_COOLDOWN = 15;
    public static java.util.List<? extends String> RETRIBUTION_BLACKLIST = new java.util.ArrayList<>();
    public static boolean RETRIBUTION_MANA_CHARGED_PER_PERCENT = false;
    public static int RETRIBUTION_MANA_PER_DIFFERENCE = 40;
    public static int DEATH_COLLECTION_MANA_COST = 400;
    public static double DEATH_COLLECTION_MAX = 1.0E7;
    public static int DEATH_COLLECTION_RANGE_FROM_USER = 10;
    public static double DEATH_COLLECTION_REVIVE_VALUE = 12.0;
    public static double DEATH_COLLECTION_SOUL_VALUE_MOB = 0.25;
    public static int DEATH_COLLECTION_TIME = 600;
    public static int PROJECTILE_DISPLACEMENT_MANA_COST = 50;
    public static double KODOKU_HEALTH_FACTOR_MOB = 0.1;
    public static double KODOKU_ARMOR_FACTOR_MOB = 0.0;
    public static double KODOKU_HEALTH_FACTOR = 0.1;
    public static double KODOKU_ARMOR_FACTOR = 0.0;
    public static int CLARENT_ATTACK_CAP = 1600;
    public static boolean CLARENT_BLACKLIST_TOGGLE = true;
    public static double CLARENT_DECREASE_PER_BLOCKING_TICK = 0.02;
    public static int CLARENT_DURABILITY = 1500;
    public static double CLARENT_MANA_PER_DAMAGE = 0.02;
    public static double CLARENT_STORED_FACTOR = 0.5;
    public static java.util.List<? extends String> CLARENT_SWORD_BLACKLIST = java.util.List.of("mahoutsukai:caliburn","mahoutsukai:morgan");
    public static boolean CLARENT_UNBREAKABLE = true;
    public static double CLARENT_WOUND_DAMAGE = 0.2;
    public static int CLARENT_WOUND_DAMAGE_HITS = 3;
    public static int CLARENT_WOUND_TICKS = 600;
    public static double POWER_CONSOLIDATION_FOG_RADIUS=24.0;
    public static int POWER_CONSOLIDATION_LAKE_MANA_COST=30;
    public static int POWER_CONSOLIDATION_LAKE_CYCLE=20;
    public static int POWER_CONSOLIDATION_LAKE_RADIUS=12;
    public static int POWER_CONSOLIDATION_ATTACK_CAP = 5000000;
    public static boolean POWER_CONSOLIDATION_UNSOCKETS_APOTH_GEMS=true;
    public static java.util.List<? extends String> POWER_CONSOLIDATION_ENCHANT_BLACKLIST = java.util.List.of("minecraft:mending");
    public static java.util.List<? extends Double> POWER_CONSOLIDATION_TIERS = java.util.List.of(100.0,150.0,200.0,250.0,300.0,350.0,400.0,500.0,600.0,700.0,800.0,950.0,1100.0,1251.0,1452.0,1653.0,1904.0,2155.0,2457.0,2809.0,3211.0,3664.0,4167.0,4721.0,5325.0,6030.0,6836.0,7742.0,8749.0,9857.0,11116.0,12528.0,14141.0,15956.0,17972.0,20240.0,22812.0,25736.0,29013.0,32694.0,36829.0,41518.0,46763.0,52663.0,59320.0,66834.0,75306.0,84838.0,95580.0,107634.0,121251.0,136584.0,153833.0,173251.0,195141.0,219755.0,247495.0,278716.0,313871.0,353465.0,398053.0,448290.0,504832.0,568536.0,640260.0,721014.0,811957.0,914349.0,1029654.0,1159535.0,1305759.0,1470444.0,1655911.0,1864731.0,2099881.0,2364689.0,2662889.0,2998717.0,3376864.0,3802727.0,4282259.0,4822269.0,5000000.0);
    public static int STRENGTHENING_CAP = 50;
    public static java.util.List<? extends String> STRENGTHENING_ITEM_BLACKLIST = new java.util.ArrayList<>();
    public static int STRENGTHENING_MANA_COST = 50;
    public static int DAMAGE_REPLICATION_MANA_COST = 160;
    public static int SELECTIVE_DISPLACEMENT_COOLDOWN = 60;
    public static int SELECTIVE_DISPLACEMENT_HOLD_TIME = 5;
    public static int SELECTIVE_DISPLACEMENT_MANA_COST = 900;
    public static int SELECTIVE_DISPLACEMENT_RANGE = 40;
    public static int SELECTIVE_DISPLACEMENT_TIME = 1200;
    public static boolean SHOW_SELECTED_ENTITY = true;
    public static int PROJECTION_MANA_COST=1000, PROJECTION_DURABILITY_FACTOR=5;
    public static boolean PROJECTION_IS_BLACKLIST=true,PROJECTION_DURABILITY_FLAT=true,PROJECTION_ANVIL_CANCEL=false;
    public static java.util.List<String> PROJECTION_BLACKLIST=java.util.List.of(), PROJECTION_ENCHANTMENT_BLACKLIST=java.util.List.of(),PROJECTION_EXPLICIT_WHITELIST=java.util.List.of();
    public static int PROXIMITY_PROJECTION_MANA_COST=160,PROXIMITY_PROJECTION_DURABILITY=130;
    public static int FAMILIARS_GARDEN_MANA_COST = 200;
    public static int FAMILIARS_GARDEN_RANGE = 7;
    public static int FAMILIARS_GARDEN_TIME = 1200;
    public static boolean FAMILIAR_ACTION_BAR_MESSAGES = false;
    public static boolean FAMILIAR_CHUNKLOADS = true;
    public static int FAMILIAR_HEALTH = 10;
    public static int FAMILIAR_INTERESTING_BLOCK_RENDER_RANGE = 8;
    public static double FAMILIAR_OUCH_CHANCE = 0.3;
    public static int FAMILIAR_REPORT_SURROUNDINGS_TIME = 400;
    public static int RECALL_FAMILIAR_MANA_COST = 20;
    public static int SUMMON_FAMILIAR_MANA_COST = 200;
    public static int SWAP_FAMILIAR_MANA_COST = 40;
    public static double BLACK_FLAME_DAMAGE = 1.0;
    public static int BLACK_FLAME_DEATH_AGE = 6;
    public static int BLACK_FLAME_DEBUFF_TIME = 180;
    public static int BLACK_FLAME_MANA_COST = 300;
    public static int BLACK_FLAME_RANGE_FROM_USER = 30;
    public static int BLACK_FLAME_TIME = 100;
    public static int BLACK_FLAME_UPDATE_TICKS = 1;
    public static int BLACK_FLAME_IGNITION_TICKS=1;
    public static int CLAIRVOYANCE_MANA_COST=220;
    public static int CLAIRVOYANCE_RANGE=30;
    public static int CLAIRVOYANCE_TIME=1200;
    public static int HEAVENS_CUP_BATCH_SIZE=10;
    public static int HEAVENS_CUP_DURATION=1200;
    public static java.util.List<? extends String> HEAVENS_CUP_EFFECTS=java.util.List.of("poison","weakness","blindness","slowness","wither","levitation");
    public static int HEAVENS_CUP_MANA_COST=2;
    public static int HEAVENS_CUP_NETWORK_DISTANCE=30;
    public static int HEAVENS_CUP_START_DISTANCE=20;
    public static int INSIGHT_BOTTOM_X=0;
    public static int INSIGHT_BOTTOM_Y=0;
    public static int INSIGHT_LEFT_X=0;
    public static int INSIGHT_LEFT_Y=0;
    public static int INSIGHT_MANA_COST=320;
    public static int INSIGHT_RIGHT_X=0;
    public static int INSIGHT_RIGHT_Y=0;
    public static int INSIGHT_TIME=1200;
    public static int REVERSION_EYES_MANA_COST=410;
    public static int REVERSION_EYES_RANGE_FROM_LOOK_VEC=4;
    public static int REVERSION_EYES_RANGE_FROM_USER=10;
    public static int REVERSION_EYES_TIME=600;
    public static int RIPPER_COOLDOWN=800;
    public static double RIPPER_DAMAGE=2.5;
    public static double RIPPER_DAMAGE_BONUS_FROM_BEHIND=6.0;
    public static boolean RIPPER_DAMAGE_BONUS_SCALES_WITH_MANA=false;
    public static int RIPPER_DURABILITY=1200;
    public static int RIPPER_FOG_DISTANCE=5;
    public static int RIPPER_FOG_MANA_COST=200;
    public static int RIPPER_FOG_RANGE=20;
    public static boolean RIPPER_GIVES_INVIS=true;
    public static int RIPPER_INVIS_COOLDOWN=40;
    public static int RIPPER_INVIS_MANA_COST=0;
    public static int RIPPER_MANA_COST=800;
    public static double RIPPER_MOVEMENT_SPEED=0.05;
    public static double RIPPER_SPEED=3.0;
    public static boolean SHOW_MORGAN_CALIBURN_COOLDOWN=false;
    public static int GANDR_BLACK_PARTICLES_HIT=150;
    public static int GANDR_CLOUD_DURATION=200;
    public static double GANDR_DAMAGE_FACTOR=0.01F;
    public static boolean GANDR_DAMAGE_SCALES=true;
    public static java.util.List<? extends String> GANDR_EFFECT_BLACKLIST=java.util.List.of();
    public static java.util.List<? extends String> GANDR_EFFECT_WHITELIST=java.util.List.of(
               "mahoutsukai:misfortune", "mahoutsukai:bound", "mahoutsukai:fear", "mahoutsukai:black_burning", "mahoutsukai:wounded", "mahoutsukai:confusion"
            );
    public static double GANDR_HIT_RADIUS=6.0;
    public static double GANDR_MANA_COST_FACTOR=0.05;
    public static boolean GANDR_MANA_SCALES=true;
    public static double GANDR_MAX_DAMAGE=1000.0;
    public static double GANDR_MIN_DAMAGE=5.0;
    public static int GANDR_RED_PARTICLES_HIT=40;
    public static int GANDR_RED_PARTICLES_LAUNCH=25;
    public static double GANDR_SPEED=1.0;
    public static int SMOKE_RING_PARTICLE_QUALITY=24;
    public static java.util.List<? extends String> RHO_AIAS_COLLIDE_KILL_LIST=java.util.List.of("mahoutsukai:gandr_entity");
    public static java.util.List<? extends String> RHO_AIAS_ENTITY_KILL_LIST=java.util.List.of("mahoutsukai:gandr_entity");
    public static int RHO_AIAS_KILL_RANGE=30;
    public static int RHO_AIAS_LIFE=1200;
    public static int RHO_AIAS_MANA_COST=300;
    public static boolean RHO_AIAS_SNEAK_BOOP=true;
    public static double FALLEN_DOWN_BEAM_DAMAGE=2.0;
    public static double FALLEN_DOWN_BEAM_GROWTH=0.12;
    public static java.util.List<? extends String> FALLEN_DOWN_BEAM_IN=java.util.List.of(
               "minecraft:grass_block",
               "minecraft:sand",
               "minecraft:red_sand",
               "minecraft:cobblestone",
               "minecraft:clay",
               "minecraft:ice",
               "minecraft:packed_ice",
               "minecraft:blue_ice",
               "minecraft:snow_block",
               "minecraft:snow",
               "minecraft:water"
            );
    public static double FALLEN_DOWN_BEAM_MANA_SCALED_DAMAGE=0.0;
    public static java.util.List<? extends String> FALLEN_DOWN_BEAM_OUT=java.util.List.of(
               "minecraft:dirt",
               "minecraft:glass",
               "minecraft:red_stained_glass",
               "minecraft:stone",
               "minecraft:terracotta",
               "minecraft:water",
               "minecraft:water",
               "minecraft:water",
               "minecraft:water",
               "minecraft:air",
               "minecraft:air"
            );
    public static double FALLEN_DOWN_BEAM_TARGET_HEALTH_PERCENTAGE_DAMAGE=0.05;
    public static int FALLEN_DOWN_BLOCK_BREAK_PER_TICK=800;
    public static int FALLEN_DOWN_MANA_PER_TICK=2000;
    public static int FALLEN_DOWN_RADIUS=30;
    public static int AUTHORITY_BLOCK_RESISTANCE_LIMIT=45;
    public static double AUTHORITY_DAMAGE_TO_OTHER=3.0;
    public static double AUTHORITY_DAMAGE_TO_SELF=1.5;
    public static boolean AUTHORITY_DROPS_BLOCKS=true;
    public static int AUTHORITY_MANA_COST=900;
    public static double AUTHORITY_NORMAL_SLOW_DOWN_FACTOR=0.875;
    public static double AUTHORITY_SPEED_MULTIPLIER=1.8;
    public static int AUTHORITY_TIME=400;
    public static int MENTAL_DISPLACEMENT_MANA_COST=300;
    public static int MENTAL_DISPLACEMENT_RANGE=20;
    public static int POSSESS_ENTITY_MANA_COST=200;
    public static boolean SPATIAL_DISORIENTATION_AIR_RESISTANCE=false;
    public static double SPATIAL_DISORIENTATION_AOE_RADIUS=4.0;
    public static int SPATIAL_DISORIENTATION_MANA_COST=100;
    public static int SPATIAL_DISORIENTATION_MANA_COST_AOE=20;
    public static int SPATIAL_DISORIENTATION_MANA_COST_ST=200;
    public static double SPATIAL_DISORIENTATION_SPEED=7.0;
    public static int SPATIAL_NO_AIR_RESISTANCE_TIME_LIMIT=60;
    public static double POWER_CONSOLIDATION_CALIBURN_RING_SPEED=1.0;
    public static int CALIBURN_MORGAN_ABILITY_MANA_COST=300;
    public static double MORGAN_ADULT_INCREASE=0.2;
    public static int MORGAN_CALIBURN_POWER_COOLDOWN=600;
    public static double MORGAN_CHILD_INCREASE=1.0;
    public static int MORGAN_HEAL_FACTOR=30;
    public static int MORGAN_MAX_BALL_RANGE=15;
    public static int MORGAN_RAGE_TIME=120;
    public static int MORGAN_SPIKE_RANGE=30;
    public static java.util.List<? extends String> MORGAN_TRANSFORM_ENTITY_WHITELIST=java.util.List.of("minecraft:wolf");
    public static int MORGAN_UPGRADE_MANA_COST=400;
    public static int POWER_CONSOLIDATION_DURABILITY=1000;
    public static java.util.List<? extends String> POWER_CONSOLIDATION_FEAR_BLACKLIST=java.util.List.of();
    public static boolean POWER_CONSOLIDATION_PROJECTIONS_ALLOWED=false;
    public static int POWER_CONSOLIDATION_PULL_RADIUS=20;
    public static int POWER_CONSOLIDATION_SMITE_RADIUS=30;
    public static double SMITE_VOLUME=1.0;
    public static java.util.List<? extends String> UNHOLY_WORDS=java.util.List.of("zomb", "vampir", "demon", "devil", "skele", "lich", "evil", "curse", "undead", "wither");
    public static boolean TREASURY_PROJECTION_DESPAWN_AFTER_LAND=false;
    public static double TREASURY_PROJECTION_SPEED=30.0;
    public static double TREASURY_PROJECTION_SPEED_MIN=10.0;
    public static double WEAPON_SHOOTER_DAMAGE_MULTIPLIER=1.0;
    public static int WEAPON_SHOOTER_DURABILITY=30;
    public static int WEAPON_SHOOTER_MANA_COST=100;
    public static double WEAPON_SHOOTER_PROJECTILE_SPEED=3.0;
    public static boolean CALIBURN_FUN_REQUIRED=false;
    public static java.util.List<? extends String> CALIBURN_MANDATORY_FUN_REQUIREMENTS=java.util.List.of(
               "EMRYS_HIT_PHANTOM",
               "SHOOT_A_SWORD",
               "SPATIAL_THROW_SOMETHING",
               "BORROWED_AUTHORITY_PUNCH",
               "BUTTERFLY_FUTURE_BLOCK_REMOVE",
               "GEAS_PICKUP_CANCEL",
               "CREEPER_BINDING_EYES",
               "CREEPER_POSSESSION",
               "NOBU_FIRE_SQUAD",
               "SWAP_TWO_THINGS"
            );
    public static java.util.List<? extends String> POWER_CONSOLIDATION_BLACKLIST=java.util.List.of("mahoutsukai:clarent", "mahoutsukai:morgan");
    public static boolean POWER_CONSOLIDATION_CALIBURN_BLACKLIST_TOGGLE=true;
    public static int POWER_CONSOLIDATION_LAKE_THRESHHOLD=150;
    public static double POWER_CONSOLIDATION_NERF_FACTOR=0.6;
    public static int POWER_CONSOLIDATION_SWORD_MANA_COST=5000;
    public static java.util.List<? extends String> POWER_CONSOLIDATION_TIER_UPGRADE_ITEM=java.util.List.of("minecraft:nether_star");
    public static double CHAIN_VOLUME=2.0;
    public static boolean TREASURY_PROJECTION_ALLOW_ALL=false;
    public static int TREASURY_PROJECTION_AOE_COOLDOWN=4;
    public static int TREASURY_PROJECTION_AOE_MANA_COST=120;
    public static double TREASURY_PROJECTION_AOE_MAX_SPAWNS=3.0;
    public static double TREASURY_PROJECTION_AOE_SWORDS_PER_SPAWN=5.0;
    public static java.util.List<? extends String> TREASURY_PROJECTION_BLACKLIST=java.util.List.of();
    public static int TREASURY_PROJECTION_CHAINS_COOLDOWN=10;
    public static int TREASURY_PROJECTION_CHAINS_COUNT=16;
    public static double TREASURY_PROJECTION_CHAINS_DAMAGE=1.0;
    public static int TREASURY_PROJECTION_CHAINS_MANA_COST=120;
    public static int TREASURY_PROJECTION_COOLDOWN=10;
    public static double TREASURY_PROJECTION_COOLDOWN_INCREASE_PER_SWORD=1.0;
    public static double TREASURY_PROJECTION_COOLDOWN_MIN=3.0;
    public static boolean TREASURY_PROJECTION_DENY_ALL=false;
    public static int TREASURY_PROJECTION_DISTANCE=20;
    public static boolean TREASURY_PROJECTION_ENDER_CHEST_ONLY=false;
    public static int TREASURY_PROJECTION_GAUNTLET_MANA_COST=120;
    public static boolean TREASURY_PROJECTION_INCREASE_REQUIRES_UNIQUE=true;
    public static int TREASURY_PROJECTION_MIN_DISTANCE=8;
    public static int TREASURY_PROJECTION_SCROLL_MANA_COST=1000;
    public static double TREASURY_PROJECTION_SPEED_INCREASE_PER_SWORD=1.0;
    public static java.util.List<? extends String> TREASURY_PROJECTION_WHITELIST=java.util.List.of();
    public static double MYSTIC_CODE_RING_SPEED=1.0;
    public static int CHAIN_QUALITY=11;
    public static int POWER_CONSOLIDATION_SWEEP_LOOK_RADIUS=3;
    public static int POWER_CONSOLIDATION_SWEEP_RADIUS=6;
    public static int MARBLE_DIMENSION_DURABILITY=3;
    public static double MARBLE_DIMENSION_SPAWN_CHANCE=0.8;
    public static int MARBLE_DIMENSION_SPAWN_RATE=13;
    public static int MARBLE_MANA_COST=4000;
    public static java.util.List<? extends String> MARBLE_MOB_BLACKLIST=java.util.List.of();
    public static java.util.List<? extends String> MARBLE_SWORD_SPAWN_LIST=java.util.List.of("minecraft:iron_sword", "minecraft:diamond_sword", "minecraft:stone_sword");
    public static int MYSTIC_STAFF_AOE_DELAY=3;
    public static double MYSTIC_STAFF_AOE_FACTOR=0.004;
    public static int MYSTIC_STAFF_AOE_MANA_COST=600;
    public static boolean MYSTIC_STAFF_AOE_SCALES=true;
    public static double MYSTIC_STAFF_AOE_SCALING_MANA=0.0;
    public static int MYSTIC_STAFF_AOE_SIZE=4;
    public static java.util.List<? extends String> MYSTIC_STAFF_BEAM_BLOCK_WHITELIST=java.util.List.of();
    public static double MYSTIC_STAFF_BEAM_DAMAGE_ACCELERATION=1.5;
    public static double MYSTIC_STAFF_BEAM_DAMAGE_FACTOR=0.001;
    public static boolean MYSTIC_STAFF_BEAM_DAMAGE_SCALES=true;
    public static int MYSTIC_STAFF_BEAM_MANA_PER_TICK=500;
    public static int MYSTIC_STAFF_BEAM_MAX_BLOCK_BREAK_PER_TICK=90;
    public static double MYSTIC_STAFF_BEAM_SCALING_MANA=0.0;
    public static double MYSTIC_STAFF_BIG_FACTOR=0.012;
    public static int MYSTIC_STAFF_BIG_MANA_COST=5000;
    public static boolean MYSTIC_STAFF_BIG_RAIN=true;
    public static boolean MYSTIC_STAFF_BIG_SCALES=true;
    public static int MYSTIC_STAFF_BIG_SIZE=30;
    public static double MYSTIC_STAFF_SCALING_MANA=0.0;
    public static int MYSTIC_STAFF_SUMMON_MANA_COST=100;
    public static boolean MYSTIC_STAFF_WORLD_DAMAGE=true;
    public static double NOBU_DRUM_VOLUME=10.0;
    public static int BUTTERFLY_EFFECT_BLOCK_LIMIT=4;
    public static int BUTTERFLY_EFFECT_CHUNK_LIMIT=40;
    public static int BUTTERFLY_EFFECT_DURATION=400;
    public static int BUTTERFLY_EFFECT_MANA_COST=100;
    public static boolean BUTTERFLY_EFFECT_SINGLE_USE=false;
    public static boolean BUTTERFLY_EFFECT_VISIBLE_TO_OTHERS=false;
    public static int SCRYING_BLOCK_CYCLE=4;
    public static int SCRYING_DURATION=1200;
    public static int SCRYING_MANA_COST=50;
    public static boolean SCRY_NON_PLAYERS=true;
    public static double KODOKU_CONFUSE_CHANCE=0.01;
    public static double KODOKU_FIRE_CHANCE=0.1;
    public static double KODOKU_GLOW_CHANCE=0.02;
    public static double KODOKU_HOP_CHANCE=0.08;
    public static double KODOKU_HUNGER_CHANCE=0.15;
    public static double KODOKU_MISFORTUNE_AGGRO_CHANCE=4.0E-4;
    public static double KODOKU_MISFORTUNE_DROP_CHANCE=7.142857142857143E-5;
    public static double KODOKU_MISFORTUNE_LIGHTNING_CHANCE=2.0E-5;
    public static double KODOKU_MISFORTUNE_TRIP_CHANCE=4.0E-5;
    public static double KODOKU_SPLASH_CHANCE=0.05;
    public static double KODOKU_TELEPORT_CHANCE=0.08;
    public static int KODOKU_TOOL_BREAK_DIVISOR=10;
    public static int EXPLOSION_PARTICLE_QUALITY=8;
    public static double MYSTIC_STAFF_BEAM_VOLUME_FACTOR=0.1;
    public static int KODOKU_LOOT_DIVISOR=10;
    public static boolean DISABLE_EMRYS=false;
    public static boolean EMRYS_BOLT_FIRE=false;
    public static double EMRYS_DAMAGE_FOCUSED_PER_SECOND=4.0;
    public static double EMRYS_DAMAGE_ZAP=1.5;
    public static int EMRYS_MANA_COST_FOCUSED=200;
    public static int EMRYS_MANA_COST_PASSIVE=80;
    public static int EMRYS_MAX_RANGE=22;
    public static int FIRST_SORCERY_DURABILITY=50;
    public static java.util.List<? extends String> MYSTIC_CODE_ENCHANT_BLACKLIST=java.util.List.of();
    public static java.util.List<? extends String> MYSTIC_CODE_FIRST_SORCERY_BLACKLIST=java.util.List.of("mahoutsukai:scroll_boundary_drain_life");
    public static int REPLICA_ATTACK_MANA_COST=30;
    public static double REPLICA_ATTACK_RANGE=8.0;
    public static java.util.List<? extends String> REPLICA_BANNED_DAMAGE_TYPES=java.util.List.of();
    public static double REPLICA_BASE_DAMAGE=4.0;
    public static double REPLICA_COVER_MOVE_RANGE=20.0;
    public static boolean REPLICA_DISABLED=false;
    public static int REPLICA_DURABILITY=10000;
    public static boolean REPLICA_IS_UNBREAKABLE=true;
    public static double REPLICA_MAX_DAMAGE=5000000.0;
    public static int REPLICA_SHOCKWAVE_COOLDOWN=50;
    public static double REPLICA_TELEPORT_FRIEND_RANGE=6.0;
    public static int REPLICA_TELEPORT_LIFE=140;
    public static int REPLICA_TELEPORT_START_TELEPORTING=110;
    public static int REPLICA_TELEPORT_STOP_TELEPORTING=120;
    public static boolean REPLICA_TELEPORT_CROSS_DIMENSION=true;
    public static int REPLICA_TELEPORT_MANA_COST=300;
    public static double REPLICA_TELEPORT_MAX_DISTANCE=-1.0;
    public static double NOBU_ADJUSTMENT_TICKS_PER_DEGREE=0.5;
    public static java.util.List<? extends String> NOBU_ALLOWED_BOWS=java.util.List.of("minecraft:bow");
    public static double NOBU_AOE_FACTOR=0.5;
    public static double NOBU_AOE_HIT=-1.0;
    public static int NOBU_AUTONOMOUS_LIFE_TICKS=600;
    public static double NOBU_BULLET_DAMAGE=8.0;
    public static double NOBU_CIRCLE_RADIUS=15.0;
    public static double NOBU_DISTANCE_REQUIREMENT=100.0;
    public static int NOBU_DURABILITY=10000;
    public static boolean NOBU_ENABLED=true;
    public static double NOBU_FIRING_LINE_SPACE=20.0;
    public static boolean NOBU_IS_UNBREAKABLE=false;
    public static int NOBU_MANA_PER_CONSTRUCT=40;
    public static int NOBU_MANA_PER_SHOT=20;
    public static int NOBU_MAX_GUNS=20;
    public static double NOBU_MAX_SPAWN_HEIGHT=12.0;
    public static double NOBU_PITCH_INCREMENT=15.0;
    public static double NOBU_POWER_DAMAGE_FACTOR=0.4F;
    public static int NOBU_RECOIL_TIME=3;
    public static int NOBU_SPAWN_AND_FIRE_FREQ=2;
    public static int RHONGOMYNIAD_MANA_COST=300;
    public static double WILLIAM_CREATION_COST=500.0;
    public static double ACCEPTABLE_PLAGIARISM=0.4;
    public static int CHARACTER_RATIO_POINTS=10;
    public static double CHARACTER_SCORE_FACTOR=0.01;
    public static double PLAGIARISM_NEW_WORD_WEIGHT=0.5;
    public static double PLAGIARISM_SIMILAR_WORD_COUNT_WEIGHT=0.5;
    public static int PUNCTUATION_RATIO_POINTS=400;
    public static double PUNCTUATION_SCORE_FACTOR=0.01;
    public static int REAL_WORD_POINTS=4;
    public static int SPACE_RATIO_POINTS=1000;
    public static double SPACE_SCORE_FACTOR=0.001;
    public static java.util.List<? extends String> WILLIAM_DICTIONARIES=java.util.List.of("English");
    public static int WILLIAM_MAX_PAGES=100;
    public static double WILLIAM_NEARBY_MANA_FACTOR=0.01;
    public static int WILLIAM_NEARBY_MANA_RANGE=30;
    public static int WILLIAM_PLETHORA_FREQUENCY=100;
    public static int WILLIAM_THEATER_RANGE=40;
    public static double WORD_SCORE_FACTOR=2.0E-5;
    public static boolean NOBU_PERSONAL_DRUM=true;
    private static final java.util.Map<String,double[]> ORIGINAL_RANGES = java.util.Map.ofEntries(
        java.util.Map.entry("POWER_CONSOLIDATION_FEAR_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("LEY_RENDER_DISTANCE",new double[]{0,100000000}),
        java.util.Map.entry("LEY_RENDER_HEIGHT",new double[]{-100000000,100000000}),
        java.util.Map.entry("CLIENT_GUI_MAHOU_PLACEMENT_X",new double[]{0,100000000}),
        java.util.Map.entry("CLIENT_GUI_MAHOU_PLACEMENT_Y",new double[]{0,100000000}),
        java.util.Map.entry("RHONGOMYNIAD_RANGE",new double[]{0,10000000}),
        java.util.Map.entry("RHONGOMYNIAD_NERF_FACTOR",new double[]{-10000000,10000000}),
        java.util.Map.entry("RHONGOMYNIAD_SPEED_FACTOR",new double[]{-10000000,10000000}),
        java.util.Map.entry("RHONGOMYNIAD_MAX_SMITES",new double[]{0,10000000}),
        java.util.Map.entry("PROJECTOR_DEFAULT_ROTATION_SPEED",new double[]{-10000000,10000000}),
        java.util.Map.entry("PROJECTOR_OFFSET_CAP",new double[]{-10000000,10000000}),
        java.util.Map.entry("PROJECTOR_SIZE_CAP",new double[]{-10000000,10000000}),
        java.util.Map.entry("FOG_PROJECTOR_MAX_RADIUS",new double[]{-10000000,10000000}),





        java.util.Map.entry("ACCEPTABLE_PLAGIARISM",new double[]{0.0,10000.0}),
        java.util.Map.entry("CHARACTER_RATIO_POINTS",new double[]{0,10000}),
        java.util.Map.entry("CHARACTER_SCORE_FACTOR",new double[]{0.0,10000.0}),
        java.util.Map.entry("PLAGIARISM_NEW_WORD_WEIGHT",new double[]{0.0,10000.0}),
        java.util.Map.entry("PLAGIARISM_SIMILAR_WORD_COUNT_WEIGHT",new double[]{0.0,10000.0}),
        java.util.Map.entry("PUNCTUATION_RATIO_POINTS",new double[]{0,10000}),
        java.util.Map.entry("PUNCTUATION_SCORE_FACTOR",new double[]{0.0,10000.0}),
        java.util.Map.entry("REAL_WORD_POINTS",new double[]{0,10000}),
        java.util.Map.entry("SPACE_RATIO_POINTS",new double[]{0,10000}),
        java.util.Map.entry("SPACE_SCORE_FACTOR",new double[]{0.0,10000.0}),
        java.util.Map.entry("WILLIAM_MAX_PAGES",new double[]{0,10000}),
        java.util.Map.entry("WILLIAM_NEARBY_MANA_FACTOR",new double[]{0.0,10000.0}),
        java.util.Map.entry("WILLIAM_NEARBY_MANA_RANGE",new double[]{0,10000}),
        java.util.Map.entry("WILLIAM_PLETHORA_FREQUENCY",new double[]{1,10000}),
        java.util.Map.entry("WILLIAM_THEATER_RANGE",new double[]{0,10000}),
        java.util.Map.entry("WORD_SCORE_FACTOR",new double[]{0.0,10000.0}),

        java.util.Map.entry("RHONGOMYNIAD_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("WILLIAM_CREATION_COST",new double[]{0.0,1000000.0}),

        java.util.Map.entry("NOBU_ADJUSTMENT_TICKS_PER_DEGREE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_AOE_FACTOR",new double[]{-1000000.0,1.0E7}),
        java.util.Map.entry("NOBU_AOE_HIT",new double[]{-1000000.0,1.0E7}),
        java.util.Map.entry("NOBU_AUTONOMOUS_LIFE_TICKS",new double[]{0,10000000}),
        java.util.Map.entry("NOBU_BULLET_DAMAGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_CIRCLE_RADIUS",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_DISTANCE_REQUIREMENT",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_DURABILITY",new double[]{0,10000000}),
        java.util.Map.entry("NOBU_FIRING_LINE_SPACE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_MANA_PER_CONSTRUCT",new double[]{0,10000000}),
        java.util.Map.entry("NOBU_MANA_PER_SHOT",new double[]{0,10000000}),
        java.util.Map.entry("NOBU_MAX_GUNS",new double[]{0,10000000}),
        java.util.Map.entry("NOBU_MAX_SPAWN_HEIGHT",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_PITCH_INCREMENT",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_POWER_DAMAGE_FACTOR",new double[]{0.0,1.0E7}),
        java.util.Map.entry("NOBU_RECOIL_TIME",new double[]{0,10000000}),
        java.util.Map.entry("NOBU_SPAWN_AND_FIRE_FREQ",new double[]{1,10000000}),

        java.util.Map.entry("REPLICA_TELEPORT_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("REPLICA_TELEPORT_MAX_DISTANCE",new double[]{-1.0E7,1.0E7}),

        java.util.Map.entry("REPLICA_ATTACK_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("REPLICA_ATTACK_RANGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("REPLICA_BASE_DAMAGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("REPLICA_COVER_MOVE_RANGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("REPLICA_DURABILITY",new double[]{0,10000000}),
        java.util.Map.entry("REPLICA_MAX_DAMAGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("REPLICA_SHOCKWAVE_COOLDOWN",new double[]{0,1000000}),
        java.util.Map.entry("REPLICA_TELEPORT_FRIEND_RANGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("REPLICA_TELEPORT_LIFE",new double[]{0,10000000}),
        java.util.Map.entry("REPLICA_TELEPORT_START_TELEPORTING",new double[]{0,10000000}),
        java.util.Map.entry("REPLICA_TELEPORT_STOP_TELEPORTING",new double[]{0,10000000}),

        java.util.Map.entry("FIRST_SORCERY_DURABILITY",new double[]{0,100000000}),

        java.util.Map.entry("EMRYS_DAMAGE_FOCUSED_PER_SECOND",new double[]{0.0,1.0E8}),
        java.util.Map.entry("EMRYS_DAMAGE_ZAP",new double[]{0.0,1.0E8}),
        java.util.Map.entry("EMRYS_MANA_COST_FOCUSED",new double[]{0,100000000}),
        java.util.Map.entry("EMRYS_MANA_COST_PASSIVE",new double[]{0,100000000}),
        java.util.Map.entry("EMRYS_MAX_RANGE",new double[]{0,100000000}),

        java.util.Map.entry("KODOKU_LOOT_DIVISOR",new double[]{0,100000000}),

        java.util.Map.entry("EXPLOSION_PARTICLE_QUALITY",new double[]{2,64}),
        java.util.Map.entry("MYSTIC_STAFF_BEAM_VOLUME_FACTOR",new double[]{0.0,10.0}),

        java.util.Map.entry("KODOKU_CONFUSE_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_FIRE_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_GLOW_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_HOP_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_HUNGER_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_MISFORTUNE_AGGRO_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_MISFORTUNE_DROP_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_MISFORTUNE_LIGHTNING_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_MISFORTUNE_TRIP_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_SPLASH_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_TELEPORT_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("KODOKU_TOOL_BREAK_DIVISOR",new double[]{0,100000000}),

        java.util.Map.entry("SCRYING_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("SCRYING_DURATION",new double[]{0,100000000}),
        java.util.Map.entry("SCRYING_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("BUTTERFLY_EFFECT_BLOCK_LIMIT",new double[]{0,100000000}),
        java.util.Map.entry("BUTTERFLY_EFFECT_CHUNK_LIMIT",new double[]{0,100000000}),
        java.util.Map.entry("BUTTERFLY_EFFECT_DURATION",new double[]{0,100000000}),
        java.util.Map.entry("BUTTERFLY_EFFECT_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("MYSTIC_STAFF_AOE_DELAY",new double[]{1,10000000}),
        java.util.Map.entry("MYSTIC_STAFF_AOE_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MYSTIC_STAFF_AOE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_STAFF_AOE_SCALING_MANA",new double[]{0.0,1.0E7}),
        java.util.Map.entry("MYSTIC_STAFF_AOE_SIZE",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_STAFF_BEAM_DAMAGE_ACCELERATION",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MYSTIC_STAFF_BEAM_DAMAGE_FACTOR",new double[]{-1.0E7,1.0E8}),
        java.util.Map.entry("MYSTIC_STAFF_BEAM_MANA_PER_TICK",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_STAFF_BEAM_MAX_BLOCK_BREAK_PER_TICK",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_STAFF_BEAM_SCALING_MANA",new double[]{0.0,1.0E7}),
        java.util.Map.entry("MYSTIC_STAFF_BIG_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MYSTIC_STAFF_BIG_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_STAFF_BIG_SIZE",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_STAFF_SCALING_MANA",new double[]{0.0,1.0E7}),
        java.util.Map.entry("MYSTIC_STAFF_SUMMON_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("NOBU_DRUM_VOLUME",new double[]{0.0,10.0}),

        java.util.Map.entry("MARBLE_DIMENSION_DURABILITY",new double[]{0,100000000}),
        java.util.Map.entry("MARBLE_DIMENSION_SPAWN_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("MARBLE_DIMENSION_SPAWN_RATE",new double[]{1,100000000}),
        java.util.Map.entry("MARBLE_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("POWER_CONSOLIDATION_SWEEP_LOOK_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_SWEEP_RADIUS",new double[]{0,100000000}),

        java.util.Map.entry("CHAIN_QUALITY",new double[]{3,10000}),

        java.util.Map.entry("MYSTIC_CODE_RING_SPEED",new double[]{0.0,1.0E8}),

        java.util.Map.entry("CHAIN_VOLUME",new double[]{0.0,10.0}),
        java.util.Map.entry("TREASURY_PROJECTION_AOE_COOLDOWN",new double[]{1,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_AOE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_AOE_MAX_SPAWNS",new double[]{1.0,100000.0}),
        java.util.Map.entry("TREASURY_PROJECTION_AOE_SWORDS_PER_SPAWN",new double[]{0.0,100000.0}),
        java.util.Map.entry("TREASURY_PROJECTION_CHAINS_COOLDOWN",new double[]{1,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_CHAINS_COUNT",new double[]{1,120}),
        java.util.Map.entry("TREASURY_PROJECTION_CHAINS_DAMAGE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("TREASURY_PROJECTION_CHAINS_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_COOLDOWN",new double[]{1,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_COOLDOWN_INCREASE_PER_SWORD",new double[]{-10000.0,10000.0}),
        java.util.Map.entry("TREASURY_PROJECTION_COOLDOWN_MIN",new double[]{1.0,100000.0}),
        java.util.Map.entry("TREASURY_PROJECTION_DISTANCE",new double[]{0,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_GAUNTLET_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_MIN_DISTANCE",new double[]{0,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_SCROLL_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("TREASURY_PROJECTION_SPEED_INCREASE_PER_SWORD",new double[]{-10000.0,10000.0}),

        java.util.Map.entry("POWER_CONSOLIDATION_LAKE_THRESHHOLD",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_NERF_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("POWER_CONSOLIDATION_SWORD_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("TREASURY_PROJECTION_SPEED",new double[]{0.0,1.0E8}),
        java.util.Map.entry("TREASURY_PROJECTION_SPEED_MIN",new double[]{1.0,100000.0}),
        java.util.Map.entry("WEAPON_SHOOTER_DAMAGE_MULTIPLIER",new double[]{0.0,1.0E7}),
        java.util.Map.entry("WEAPON_SHOOTER_DURABILITY",new double[]{0,10000000}),
        java.util.Map.entry("WEAPON_SHOOTER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("WEAPON_SHOOTER_PROJECTILE_SPEED",new double[]{0.0,1.0E7}),

        java.util.Map.entry("CALIBURN_MORGAN_ABILITY_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("MORGAN_ADULT_INCREASE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MORGAN_CALIBURN_POWER_COOLDOWN",new double[]{0,100000000}),
        java.util.Map.entry("MORGAN_CHILD_INCREASE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MORGAN_HEAL_FACTOR",new double[]{0,100000000}),
        java.util.Map.entry("MORGAN_MAX_BALL_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("MORGAN_RAGE_TIME",new double[]{0,100000000}),
        java.util.Map.entry("MORGAN_SPIKE_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("MORGAN_UPGRADE_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_DURABILITY",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_PULL_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_SMITE_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("SMITE_VOLUME",new double[]{0.0,10.0}),

        java.util.Map.entry("POWER_CONSOLIDATION_CALIBURN_RING_SPEED",new double[]{0.0,1.0E8}),

        java.util.Map.entry("SPATIAL_DISORIENTATION_AOE_RADIUS",new double[]{0.0,1.0E7}),
        java.util.Map.entry("SPATIAL_DISORIENTATION_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("SPATIAL_DISORIENTATION_MANA_COST_AOE",new double[]{0,100000000}),
        java.util.Map.entry("SPATIAL_DISORIENTATION_MANA_COST_ST",new double[]{0,100000000}),
        java.util.Map.entry("SPATIAL_DISORIENTATION_SPEED",new double[]{0.0,1.0E7}),
        java.util.Map.entry("SPATIAL_NO_AIR_RESISTANCE_TIME_LIMIT",new double[]{0,10000000}),

        java.util.Map.entry("POSSESS_ENTITY_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("MENTAL_DISPLACEMENT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("MENTAL_DISPLACEMENT_RANGE",new double[]{0,100000000}),

        java.util.Map.entry("AUTHORITY_BLOCK_RESISTANCE_LIMIT",new double[]{0,100000000}),
        java.util.Map.entry("AUTHORITY_DAMAGE_TO_OTHER",new double[]{0.0,1.0E8}),
        java.util.Map.entry("AUTHORITY_DAMAGE_TO_SELF",new double[]{0.0,1.0E8}),
        java.util.Map.entry("AUTHORITY_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("AUTHORITY_NORMAL_SLOW_DOWN_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("AUTHORITY_SPEED_MULTIPLIER",new double[]{0.0,1.0E8}),
        java.util.Map.entry("AUTHORITY_TIME",new double[]{0,100000000}),

        java.util.Map.entry("FALLEN_DOWN_BEAM_DAMAGE",new double[]{0.0,1.0E9}),
        java.util.Map.entry("FALLEN_DOWN_BEAM_GROWTH",new double[]{0.0,1.0E9}),
        java.util.Map.entry("FALLEN_DOWN_BEAM_MANA_SCALED_DAMAGE",new double[]{0.0,1.0E9}),
        java.util.Map.entry("FALLEN_DOWN_BEAM_TARGET_HEALTH_PERCENTAGE_DAMAGE",new double[]{0.0,1.0E9}),
        java.util.Map.entry("FALLEN_DOWN_BLOCK_BREAK_PER_TICK",new double[]{0,100000000}),
        java.util.Map.entry("FALLEN_DOWN_MANA_PER_TICK",new double[]{0,100000000}),
        java.util.Map.entry("FALLEN_DOWN_RADIUS",new double[]{0,100000000}),

        java.util.Map.entry("RHO_AIAS_KILL_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("RHO_AIAS_LIFE",new double[]{0,100000000}),
        java.util.Map.entry("RHO_AIAS_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("SMOKE_RING_PARTICLE_QUALITY",new double[]{2,64}),
        java.util.Map.entry("GANDR_BLACK_PARTICLES_HIT",new double[]{0,100000000}),
        java.util.Map.entry("GANDR_CLOUD_DURATION",new double[]{0,100000000}),
        java.util.Map.entry("GANDR_DAMAGE_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GANDR_HIT_RADIUS",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GANDR_MANA_COST_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GANDR_MAX_DAMAGE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GANDR_MIN_DAMAGE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GANDR_RED_PARTICLES_HIT",new double[]{0,100000000}),
        java.util.Map.entry("GANDR_RED_PARTICLES_LAUNCH",new double[]{0,100000000}),
        java.util.Map.entry("GANDR_SPEED",new double[]{0.0,1.0E8}),

        java.util.Map.entry("RIPPER_COOLDOWN",new double[]{0,100000000}),
        java.util.Map.entry("RIPPER_DAMAGE",new double[]{0.0,1000000.0}),
        java.util.Map.entry("RIPPER_DAMAGE_BONUS_FROM_BEHIND",new double[]{0.0,1000000.0}),
        java.util.Map.entry("RIPPER_DURABILITY",new double[]{0,1000000000}),
        java.util.Map.entry("RIPPER_FOG_DISTANCE",new double[]{1,100000000}),
        java.util.Map.entry("RIPPER_FOG_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("RIPPER_FOG_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("RIPPER_INVIS_COOLDOWN",new double[]{0,100000000}),
        java.util.Map.entry("RIPPER_INVIS_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("RIPPER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("RIPPER_MOVEMENT_SPEED",new double[]{-1.0E7,1000000.0}),
        java.util.Map.entry("RIPPER_SPEED",new double[]{-1000000.0,1000000.0}),

        java.util.Map.entry("REVERSION_EYES_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("REVERSION_EYES_RANGE_FROM_LOOK_VEC",new double[]{0,100000000}),
        java.util.Map.entry("REVERSION_EYES_RANGE_FROM_USER",new double[]{0,100000000}),
        java.util.Map.entry("REVERSION_EYES_TIME",new double[]{0,100000000}),

        java.util.Map.entry("CLAIRVOYANCE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("CLAIRVOYANCE_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("CLAIRVOYANCE_TIME",new double[]{0,100000000}),
        java.util.Map.entry("HEAVENS_CUP_BATCH_SIZE",new double[]{1,100000000}),
        java.util.Map.entry("HEAVENS_CUP_DURATION",new double[]{0,100000000}),
        java.util.Map.entry("HEAVENS_CUP_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("HEAVENS_CUP_NETWORK_DISTANCE",new double[]{0,100000000}),
        java.util.Map.entry("HEAVENS_CUP_START_DISTANCE",new double[]{0,100000000}),
        java.util.Map.entry("INSIGHT_BOTTOM_X",new double[]{-10000,10000}),
        java.util.Map.entry("INSIGHT_BOTTOM_Y",new double[]{-10000,10000}),
        java.util.Map.entry("INSIGHT_LEFT_X",new double[]{-10000,10000}),
        java.util.Map.entry("INSIGHT_LEFT_Y",new double[]{-10000,10000}),
        java.util.Map.entry("INSIGHT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("INSIGHT_RIGHT_X",new double[]{-10000,10000}),
        java.util.Map.entry("INSIGHT_RIGHT_Y",new double[]{-10000,10000}),
        java.util.Map.entry("INSIGHT_TIME",new double[]{0,100000000}),

        java.util.Map.entry("BLACK_FLAME_IGNITION_TICKS",new double[]{1,100000000}),
        java.util.Map.entry("BLACK_FLAME_DAMAGE",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("BLACK_FLAME_DEATH_AGE",new double[]{0,100000000}),
        java.util.Map.entry("BLACK_FLAME_DEBUFF_TIME",new double[]{0,100000000}),
        java.util.Map.entry("BLACK_FLAME_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("BLACK_FLAME_RANGE_FROM_USER",new double[]{0,100000000}),
        java.util.Map.entry("BLACK_FLAME_TIME",new double[]{0,100000000}),
        java.util.Map.entry("BLACK_FLAME_UPDATE_TICKS",new double[]{1,100000000}),

        java.util.Map.entry("FAMILIARS_GARDEN_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("FAMILIARS_GARDEN_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("FAMILIARS_GARDEN_TIME",new double[]{0,100000000}),
        java.util.Map.entry("FAMILIAR_HEALTH",new double[]{0,100000000}),
        java.util.Map.entry("FAMILIAR_INTERESTING_BLOCK_RENDER_RANGE",new double[]{1,64}),
        java.util.Map.entry("FAMILIAR_OUCH_CHANCE",new double[]{0.0,1.0}),
        java.util.Map.entry("FAMILIAR_REPORT_SURROUNDINGS_TIME",new double[]{1,100000000}),
        java.util.Map.entry("RECALL_FAMILIAR_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("SUMMON_FAMILIAR_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("SWAP_FAMILIAR_MANA_COST",new double[]{0,100000000}),

        java.util.Map.entry("PROXIMITY_PROJECTION_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("PROXIMITY_PROJECTION_DURABILITY",new double[]{0,100000000}),
        java.util.Map.entry("PROJECTION_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("PROJECTION_DURABILITY_FACTOR",new double[]{0,100000000}),
        java.util.Map.entry("MANA_CIRCUIT_MAGITECH_CAPACITY",new double[]{0,100000000}),
        java.util.Map.entry("MANA_CIRCUIT_CAPACITY",new double[]{0,100000000}),
        java.util.Map.entry("MANA_CIRCUIT_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("ATTUNED_DIAMOND_CAPACITY",new double[]{0,100000000}),
        java.util.Map.entry("ATTUNED_EMERALD_CAPACITY",new double[]{0,100000000}),
        java.util.Map.entry("MAGIC_DAMAGE_VALUE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MANA_RECOVERY_SLEEP",new double[]{0.0,1.0E8}),
        java.util.Map.entry("DEATH_COLLECTION_DRAIN_PER_SOUL",new double[]{0.0,1.0E7}),
        java.util.Map.entry("DEATH_COLLECTION_SOUL_VALUE_PLAYER",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MANA_REGEN_PER_TICK",new double[]{0,100000000}),
        java.util.Map.entry("MANA_ADDED_SCALING_REGEN_PER_TICK",new double[]{0.0,1.0E7}),
        java.util.Map.entry("MANA_REGEN_HUNGER_FACTOR",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("LEY_PUNISHMENT",new double[]{-10000000,100000000}),
        java.util.Map.entry("LEY_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("LEY_DISTANCE",new double[]{1,100000000}),
        java.util.Map.entry("LEY_OFFSET",new double[]{0,100000000}),
        java.util.Map.entry("FAE_SPAWN_RATE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("MANA_REGEN_PER_GEAS",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GEAS_MAX_MANA_REGEN",new double[]{0.0,1.0E8}),
        java.util.Map.entry("WILLIAM_PERSONAL_MANA_FACTOR",new double[]{0.0,10000.0}),
        java.util.Map.entry("ASCENSION_SCROLL_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("FAY_SIGHT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("FAY_SIGHT_TIME",new double[]{0,100000000}),
        java.util.Map.entry("RULE_BREAKER_DURABILITY",new double[]{0,10000000}),
        java.util.Map.entry("RULE_BREAKER_DURATION",new double[]{0,10000000}),
        java.util.Map.entry("ASCENSION_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("RULE_BREAKER_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("RULE_BREAKER_RANGE",new double[]{0,10000000}),
        java.util.Map.entry("ACTIVE_MAGIC_CIRCLE_REPLACEMENT_TIME",new double[]{0,10000000}),
        java.util.Map.entry("GEAS_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("GLOBAL_LOOK_RANGE",new double[]{0,100000000}),
        java.util.Map.entry("DAMAGE_EXCHANGE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("DAMAGE_EXCHANGE_MANA_GAIN",new double[]{0,100000000}),
        java.util.Map.entry("DAMAGE_EXCHANGE_REDUCE_TO",new double[]{0.0,1.0E8}),
        java.util.Map.entry("DAMAGE_EXCHANGE_CAP",new double[]{0,1000000000}),
        java.util.Map.entry("IMMUNITY_EXCHANGE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("IMMUNITY_EXCHANGE_TIME",new double[]{0,100000000}),
        java.util.Map.entry("PROTECTIVE_DISPLACEMENT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("ALCHEMICAL_EXCHANGE_BLOCK_CYCLE",new double[]{0,100000000}),
        java.util.Map.entry("ALCHEMICAL_EXCHANGE_HEIGHT",new double[]{0,100000000}),
        java.util.Map.entry("ALCHEMICAL_EXCHANGE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("CATALYST_EXCHANGE_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("CATALYST_EXCHANGE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("CHRONAL_EXCHANGE_BLOCK_CYCLE",new double[]{0,100000000}),
        java.util.Map.entry("CHRONAL_EXCHANGE_MANA_GAIN_LOSS",new double[]{0,100000000}),
        java.util.Map.entry("DURABILITY_EXCHANGE_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("DURABILITY_EXCHANGE_MANA_GAIN_CAP",new double[]{0,100000000}),
        java.util.Map.entry("DURABILITY_EXCHANGE_EFFICIENCY",new double[]{0.0,1.0E8}),
        java.util.Map.entry("ALARM_BARRIER_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("ALARM_BARRIER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("ALARM_BARRIER_MANA_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("ALARM_BARRIER_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("CONTRACT_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("CONTRACT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("CONTRACT_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("DISPLACEMENT_BARRIER_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("DISPLACEMENT_BARRIER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("DISPLACEMENT_BARRIER_MANA_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("DISPLACEMENT_BARRIER_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("DRAIN_LIFE_AFFECTED_LIMIT",new double[]{0,100000000}),
        java.util.Map.entry("DRAIN_LIFE_BARRIER_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("DRAIN_LIFE_BARRIER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("DRAIN_LIFE_BARRIER_MANA_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("DRAIN_LIFE_BARRIER_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("DRAIN_LIFE_DAMAGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("DRAIN_LIFE_HEAL_FACTOR",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("DRAIN_LIFE_MANA_REGEN",new double[]{0,10000000}),
        java.util.Map.entry("DRAIN_LIFE_PUNISH_RESIST",new double[]{0.0,1.0E7}),
        java.util.Map.entry("ENCLOSURE_BARRIER_CYCLE",new double[]{0,100000000}),
        java.util.Map.entry("ENCLOSURE_BARRIER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("ENCLOSURE_BARRIER_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("GRAVITY_BARRIER_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("GRAVITY_BARRIER_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GRAVITY_BARRIER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("GRAVITY_BARRIER_MANA_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("GRAVITY_BARRIER_MAX",new double[]{0.0,1.0E8}),
        java.util.Map.entry("GRAVITY_BARRIER_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("TANGIBLE_BARRIER_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("TANGIBLE_BARRIER_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("TANGIBLE_BARRIER_MANA_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("TANGIBLE_BARRIER_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("EQUIVALENT_DISPLACEMENT_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("EQUIVALENT_DISPLACEMENT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("EQUIVALENT_DISPLACEMENT_MAX_DISTANCE",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("EQUIVALENT_DISPLACEMENT_RECEIVE_COOLDOWN",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_EYES_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_EYES_RANGE_FROM_LOOK_VEC",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_EYES_RANGE_FROM_USER",new double[]{0,100000000}),
        java.util.Map.entry("MYSTIC_EYES_TIME",new double[]{0,100000000}),
        java.util.Map.entry("ORDERED_DISPLACEMENT_BLOCK_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("ORDERED_DISPLACEMENT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("ORDERED_DISPLACEMENT_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("ORDERED_DISPLACEMENT_RECEIVE_COOLDOWN",new double[]{0,100000000}),
        java.util.Map.entry("RETRIBUTION_MANA_PER_DIFFERENCE",new double[]{0,100000000}),
        java.util.Map.entry("DEATH_COLLECTION_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("DEATH_COLLECTION_MAX",new double[]{0.0,1.0E7}),
        java.util.Map.entry("DEATH_COLLECTION_RANGE_FROM_USER",new double[]{0,100000000}),
        java.util.Map.entry("DEATH_COLLECTION_REVIVE_VALUE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("DEATH_COLLECTION_SOUL_VALUE_MOB",new double[]{0.0,1.0E8}),
        java.util.Map.entry("DEATH_COLLECTION_TIME",new double[]{0,100000000}),
        java.util.Map.entry("PROJECTILE_DISPLACEMENT_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("KODOKU_HEALTH_FACTOR_MOB",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("KODOKU_ARMOR_FACTOR_MOB",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("KODOKU_HEALTH_FACTOR",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("KODOKU_ARMOR_FACTOR",new double[]{-1.0E7,1.0E7}),
        java.util.Map.entry("CLARENT_ATTACK_CAP",new double[]{0,100000000}),
        java.util.Map.entry("CLARENT_DECREASE_PER_BLOCKING_TICK",new double[]{0.0,1.0E8}),
        java.util.Map.entry("CLARENT_DURABILITY",new double[]{0,100000000}),
        java.util.Map.entry("CLARENT_MANA_PER_DAMAGE",new double[]{0.0,1.0E7}),
        java.util.Map.entry("CLARENT_STORED_FACTOR",new double[]{0.0,1.0E8}),
        java.util.Map.entry("CLARENT_WOUND_DAMAGE",new double[]{0.0,1.0E8}),
        java.util.Map.entry("CLARENT_WOUND_DAMAGE_HITS",new double[]{0,100000000}),
        java.util.Map.entry("CLARENT_WOUND_TICKS",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_FOG_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_LAKE_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_LAKE_CYCLE",new double[]{1,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_LAKE_RADIUS",new double[]{0,100000000}),
        java.util.Map.entry("POWER_CONSOLIDATION_ATTACK_CAP",new double[]{0,100000000}),
        java.util.Map.entry("STRENGTHENING_CAP",new double[]{0,100000000}),
        java.util.Map.entry("STRENGTHENING_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("DAMAGE_REPLICATION_MANA_COST",new double[]{0,100000000}),
        java.util.Map.entry("SELECTIVE_DISPLACEMENT_COOLDOWN",new double[]{0,10000000}),
        java.util.Map.entry("SELECTIVE_DISPLACEMENT_HOLD_TIME",new double[]{0,100000}),
        java.util.Map.entry("SELECTIVE_DISPLACEMENT_MANA_COST",new double[]{0,10000000}),
        java.util.Map.entry("SELECTIVE_DISPLACEMENT_RANGE",new double[]{0,10000000}),
        java.util.Map.entry("SELECTIVE_DISPLACEMENT_TIME",new double[]{0,10000000})
    );
    private static final com.google.gson.Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonObject DEFAULTS = defaults();

    private static JsonObject defaults() {
        JsonObject values = new JsonObject();
        try {
            for (Field field : MTConfig.class.getFields()) values.add(field.getName(), JSON.toJsonTree(field.get(null)));
        } catch (IllegalAccessException exception) { throw new ExceptionInInitializerError(exception); }
        return values;
    }

    private static final java.util.Set<String> CLIENT_FIELDS=java.util.Set.of("SHOW_CAST_HINTS","HEAVENS_CUP_CLIENT_CIRCLE","MYSTIC_STAFF_EXPLOSION_SOUND","POWER_CONSOLIDATION_CALIBURN_RING_SPEED","LEY_RENDER_DISTANCE","FAE_NOISE","CLIENT_GUI_MAHOU_PLACEMENT_X","CLIENT_GUI_MAHOU_PLACEMENT_Y","MYSTIC_CODE_RING_SPEED","SHOW_BLEEDING","MYSTIC_STAFF_BEAM_VOLUME_FACTOR","INSIGHT_LEFT_X","INSIGHT_LEFT_Y","INSIGHT_RIGHT_X","INSIGHT_RIGHT_Y","INSIGHT_BOTTOM_X","INSIGHT_BOTTOM_Y","CHAIN_QUALITY","EXPLOSION_PARTICLE_QUALITY","SMOKE_RING_PARTICLE_QUALITY","SHOW_MORGAN_CALIBURN_COOLDOWN","NO_SHADERS","SHOW_SELECTED_ENTITY","SELECTIVE_DISPLACEMENT_HOLD_TIME","CACHE_IMAGES");

    public static JsonObject defaultSettings() { return DEFAULTS.deepCopy(); }
    public static JsonObject snapshot() { return defaults(); }
    public static JsonObject serverSettings() {
        JsonObject values=defaults(); CLIENT_FIELDS.forEach(values::remove); return values;
    }
    public static void applyServerSettings(JsonObject overrides) {
        for(var entry:overrides.entrySet()) if(CLIENT_FIELDS.contains(entry.getKey()) || !DEFAULTS.has(entry.getKey())) throw new IllegalArgumentException("Invalid server setting: "+entry.getKey());
        JsonObject values=defaults();overrides.entrySet().forEach(e->values.add(e.getKey(),e.getValue()));apply(values);
    }
    public static void resetServerSettings() {
        JsonObject values=DEFAULTS.deepCopy();CLIENT_FIELDS.forEach(values::remove);applyServerSettings(values);
    }
    public static void saveClient(Path path) throws IOException {
        JsonObject local=defaults();local.entrySet().removeIf(e->!CLIENT_FIELDS.contains(e.getKey()));
        Files.createDirectories(path.getParent());
        Path pending=Files.createTempFile(path.getParent(),"mahou-client-",".tmp");
        try {Files.writeString(pending,JSON.toJson(local)+"\n");Files.move(pending,path,java.nio.file.StandardCopyOption.REPLACE_EXISTING);}
        finally {Files.deleteIfExists(pending);}
    }
    public static void loadClient(Path path) throws IOException {
        JsonObject local=DEFAULTS.deepCopy();local.entrySet().removeIf(e->!CLIENT_FIELDS.contains(e.getKey()));
        if(!Files.exists(path)){Files.createDirectories(path.getParent());Files.writeString(path,JSON.toJson(local)+"\n");}
        else {
            JsonObject overrides=JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            for(var e:overrides.entrySet()){if(!CLIENT_FIELDS.contains(e.getKey()))throw new IllegalArgumentException("Unknown client setting: "+e.getKey());local.add(e.getKey(),e.getValue());}
        }
        JsonObject values=defaults();local.entrySet().forEach(e->values.add(e.getKey(),e.getValue()));apply(values);
    }

    /** Migrate only the three old shipped defaults once; preserve custom values and the original file. */
    private static void migrateProgressionDefaults(Path path) throws IOException {
        Path marker=path.resolveSibling(path.getFileName()+".progression-0.26");
        if(Files.exists(marker))return;
        if(Files.exists(path)){
            JsonObject values=JsonParser.parseString(Files.readString(path)).getAsJsonObject();boolean changed=false;
            String[] names={"PROXIMITY_PROJECTION_DURABILITY","NOBU_DISTANCE_REQUIREMENT","POWER_CONSOLIDATION_FOG_RADIUS"};double[] old={30,200,8},current={130,100,24};
            for(int i=0;i<names.length;i++)if(values.has(names[i])&&values.get(names[i]).isJsonPrimitive()&&values.get(names[i]).getAsJsonPrimitive().isNumber()&&values.get(names[i]).getAsDouble()==old[i]){values.addProperty(names[i],current[i]);changed=true;}
            if(changed){Path backup=path.resolveSibling(path.getFileName()+".before-0.26");if(!Files.exists(backup))Files.copy(path,backup);Files.writeString(path,JSON.toJson(values)+"\n");}
        }
        Files.createDirectories(path.getParent());Files.writeString(marker,"Progression defaults migrated; subsequent custom overrides are preserved.\n");
    }

    private static void migrateClarentLimit(Path path) throws IOException {
        Path marker=path.resolveSibling(path.getFileName()+".clarent-0.27");if(Files.exists(marker))return;
        if(Files.exists(path)){
            JsonObject values=JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if(values.has("CLARENT_ATTACK_CAP")&&values.get("CLARENT_ATTACK_CAP").isJsonPrimitive()&&values.get("CLARENT_ATTACK_CAP").getAsJsonPrimitive().isNumber()&&values.get("CLARENT_ATTACK_CAP").getAsDouble()==500000){
                Path backup=path.resolveSibling(path.getFileName()+".before-0.27");if(!Files.exists(backup))Files.copy(path,backup);values.addProperty("CLARENT_ATTACK_CAP",1600);Files.writeString(path,JSON.toJson(values)+"\n");
            }
        }
        Files.createDirectories(path.getParent());Files.writeString(marker,"Clarent default limit migrated.\n");
    }

    public static void load(Path path) throws IOException {
        migrateClarentLimit(path);
        migrateProgressionDefaults(path);
        JsonObject values = DEFAULTS.deepCopy();
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, JSON.toJson(values) + "\n");
        } else {
            for (var entry : JsonParser.parseString(Files.readString(path)).getAsJsonObject().entrySet()) {
                if (!values.has(entry.getKey())) throw new IllegalArgumentException("Unknown economy setting: " + entry.getKey());
                values.add(entry.getKey(), entry.getValue());
            }
        }
        JsonObject current=defaults();CLIENT_FIELDS.forEach(name->values.add(name,current.get(name)));
        apply(values);
    }

    private static void apply(JsonObject values) {
        Map<Field, Object> pending = new LinkedHashMap<>();
        try {
            for (Field field : MTConfig.class.getFields()) {
                var value = values.get(field.getName());
                if (field.getType() == java.util.List.class) {
                    if (!value.isJsonArray()) throw new IllegalArgumentException("String list required: " + field.getName());
                    var list = new java.util.ArrayList<Object>();
                    for (var element : value.getAsJsonArray()) {
                        if (field.getGenericType().getTypeName().contains("Double")) {
                            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("Numeric tax list required");
                            double number=element.getAsDouble();
                            if(!Double.isFinite(number) || number<0 || number>100000000) throw new IllegalArgumentException("Invalid tax value");
                            list.add(number);continue;
                        }
                        if (field.getName().equals("ACTIVE_MAGIC_CIRCLE_LIMITS")) {
                            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("Integer limits required");
                            list.add(element.getAsBigDecimal().intValueExact());
                            continue;
                        }
                        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
                            throw new IllegalArgumentException("String list required: " + field.getName());
                        list.add(element.getAsString());
                    }
                    pending.put(field, java.util.List.copyOf(list));
                    continue;
                }
                if (!value.isJsonPrimitive()) throw new IllegalArgumentException("Invalid economy value: " + field.getName());
                Object parsed;
                if (field.getType() == boolean.class) {
                    if (!value.getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Boolean required: " + field.getName());
                    parsed = value.getAsBoolean();
                } else {
                    if (!value.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("Number required: " + field.getName());
                    double number = value.getAsDouble();
                    double[] range=ORIGINAL_RANGES.get(field.getName());
                    if(range==null) throw new IllegalStateException("Missing original setting range: "+field.getName());
                    double minimum=range[0],maximum=range[1];
                    if (!Double.isFinite(number) || number < minimum || number > maximum)
                        throw new IllegalArgumentException("Economy setting out of original range: " + field.getName());
                    if (field.getType() == int.class) parsed = value.getAsBigDecimal().intValueExact();
                    else parsed = number;
                }
                pending.put(field, parsed);
            }
            for (var entry : pending.entrySet()) entry.getKey().set(null, entry.getValue());
        } catch (IllegalAccessException exception) { throw new IllegalStateException(exception); }
    }
}
