package stepsword.mahoutsukai.item.guide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.world.entity.player.Player;
import stepsword.mahoutsukai.Keybinds;
import stepsword.mahoutsukai.block.spells.MahoujinRecipeRegistrar;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.config.MandatoryFun;
import stepsword.mahoutsukai.item.nobu.Nobu;

public class GuidebookInstance {
   ArrayList<Page> pages = new ArrayList<>();
   int currentPage = 0;
   int contentsCount;
   boolean fun = false;

   public GuidebookInstance() {
      this.createPages();
   }

   public GuidebookInstance(boolean guide, Player player) {
      if (guide) {
         this.createPages();
      } else {
         this.fun = true;
         this.createFunPages(player);
      }
   }

   public int getCurrentPage() {
      return this.currentPage;
   }

   public void setCurrentPage(int z) {
      this.currentPage = z;
   }

   public void createFunPages(Player player) {
      this.pages
         .add(
            new DoubleInfoPage("mahoutsukai.book.fun1.name", "mahoutsukai.book.fun1.desc", "", "mahoutsukai.book.fun2.name", "mahoutsukai.book.fun2.desc", "")
         );
      this.createReqPages(player);
   }

   public void createReqPages(Player player) {
      if (MandatoryFun.configReqs == null) {
         MandatoryFun.configReqs = MandatoryFun.getConfigRequirements();
      }

      for (MandatoryFun.FUN_REQS v : MandatoryFun.FUN_REQS.values()) {
         if (MandatoryFun.configReqs.containsKey(v.name()) && MandatoryFun.configReqs.get(v.name())) {
            String translate = v.name().toLowerCase(Locale.ENGLISH);
            DoubleInfoPage di;
            if (MandatoryFun.meetsFunReq(player, v)) {
               di = new DoubleInfoPage(
                  "mahoutsukai.book." + translate + ".name",
                  "mahoutsukai.book.fun_req_met.desc",
                  "",
                  "mahoutsukai.book." + translate + ".name",
                  "mahoutsukai.book." + translate + ".desc",
                  ""
               );
            } else {
               di = new DoubleInfoPage(
                  "mahoutsukai.book." + translate + ".name",
                  "mahoutsukai.book.fun_req_not_met.desc",
                  "",
                  "mahoutsukai.book." + translate + ".name",
                  "mahoutsukai.book." + translate + ".desc",
                  ""
               );
            }

            this.pages.add(di);
         }
      }

      ContentsPage contents1 = new ContentsPage(this.pages, 0, 28);
      this.pages.add(0, contents1);
      this.contentsCount = 1;
   }

   public void createPages() {
      this.pages = new ArrayList<>();
      this.pages
         .add(
            new InfoPage(
               "mahoutsukai.book.intro.name", "mahoutsukai.book.intro.desc", Keybinds.drawMahoujin.getTranslatedKeyMessage().getString().toUpperCase()
            )
         );
      this.pages.add(new InfoPage("mahoutsukai.book.mana.name", "mahoutsukai.book.mana.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.magic.name", "mahoutsukai.book.magic.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.gems.name", "mahoutsukai.book.gems.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.catalysts.name", "mahoutsukai.book.catalysts.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.catalysts2.name", "mahoutsukai.book.catalysts2.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.categories.name", "mahoutsukai.book.categories.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.categories2.name", "mahoutsukai.book.categories2.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.leylines.name", "mahoutsukai.book.leylines.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.fae.name", "mahoutsukai.book.fae.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.fae_essence.name", "mahoutsukai.book.fae_essence.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.faecake.name", "mahoutsukai.book.faecake.desc", ""));
      this.pages
         .add(
            new InfoPage(
               "mahoutsukai.book.mystic_code.name",
               "mahoutsukai.book.mystic_code.desc",
               Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase()
            )
         );
      this.pages.add(new InfoPage("mahoutsukai.book.mystic_code_first_sorcery.name", "mahoutsukai.book.mystic_code_first_sorcery.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.projector.name", "mahoutsukai.book.projector.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.projector2.name", "mahoutsukai.book.projector2.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.fog_projector.name", "mahoutsukai.book.fog_projector.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.circuits.name", "mahoutsukai.book.circuits.desc", ""));
      this.pages.add(new InfoPage("mahoutsukai.book.boundaries.name", "mahoutsukai.book.boundaries.desc", ""));
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.alarm_boundary.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.alarm_boundary.desc"
               )
               .setValues(MTConfig.ALARM_BARRIER_MANA_COST, MTConfig.ALARM_BARRIER_MANA_CYCLE, MTConfig.ALARM_BARRIER_RADIUS)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.displacement_boundary.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.displacement_boundary.desc"
               )
               .setValues(MTConfig.DISPLACEMENT_BARRIER_MANA_COST, MTConfig.DISPLACEMENT_BARRIER_MANA_CYCLE, MTConfig.DISPLACEMENT_BARRIER_RADIUS - 1)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.enclosure_boundary.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.enclosure_boundary.desc"
               )
               .setValues(MTConfig.ENCLOSURE_BARRIER_MANA_COST, MTConfig.ENCLOSURE_BARRIER_RADIUS)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.tangible_boundary.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.tangible_boundary.desc"
               )
               .setValues(MTConfig.TANGIBLE_BARRIER_MANA_COST, MTConfig.TANGIBLE_BARRIER_MANA_CYCLE, MTConfig.TANGIBLE_BARRIER_RADIUS)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.gravity_boundary.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.gravity_boundary.desc"
               )
               .setValues(MTConfig.GRAVITY_BARRIER_MANA_COST, MTConfig.GRAVITY_BARRIER_MANA_CYCLE, MTConfig.GRAVITY_BARRIER_RADIUS)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.drain_life_boundary.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_IRON, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.drain_life_boundary.desc"
               )
               .setValues(MTConfig.DRAIN_LIFE_BARRIER_MANA_COST, MTConfig.DRAIN_LIFE_BARRIER_MANA_CYCLE, MTConfig.DRAIN_LIFE_BARRIER_RADIUS)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.ascension.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.ascension.desc"
               )
               .setValues(MTConfig.ASCENSION_SCROLL_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.protective_displacement.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.protective_displacement.desc"
               )
               .setValues(MTConfig.PROTECTIVE_DISPLACEMENT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.projectile_displacement.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.projectile_displacement.desc"
               )
               .setValues(MTConfig.PROJECTILE_DISPLACEMENT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.ordered_displacement.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.ordered_displacement.desc"
               )
               .setValues(MTConfig.ORDERED_DISPLACEMENT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.equivalent_displacement.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.equivalent_displacement.desc"
               )
               .setValues(MTConfig.EQUIVALENT_DISPLACEMENT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.mental_displacement.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.mental_displacement.desc"
               )
               .setValues(MTConfig.MENTAL_DISPLACEMENT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.scrying.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EYE
                  ),
                  "mahoutsukai.book.scrying.desc"
               )
               .setValues(MTConfig.SCRYING_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.strengthening.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.strengthening.desc"
               )
               .setValues(MTConfig.STRENGTHENING_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.projection.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.projection.desc"
               )
               .setValues(MTConfig.PROJECTION_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.proximity_projection.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.proximity_projection.desc"
               )
               .setValues(MTConfig.PROXIMITY_PROJECTION_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.weapon_projectiles.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.weapon_projectiles.desc"
               )
               .setValues(MTConfig.WEAPON_SHOOTER_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.reality_marble.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.reality_marble.desc"
               )
               .setValues(MTConfig.MARBLE_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.power_consolidation.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.power_consolidation.desc"
               )
               .setValues(MTConfig.POWER_CONSOLIDATION_LAKE_MANA_COST, MTConfig.POWER_CONSOLIDATION_SWORD_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.treasury_projection.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_EYE
                  ),
                  "mahoutsukai.book.treasury_projection.desc"
               )
               .setValues(MTConfig.TREASURY_PROJECTION_SCROLL_MANA_COST, MTConfig.TREASURY_PROJECTION_GAUNTLET_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.treasury_projection2.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_EYE
                  ),
                  "mahoutsukai.book.treasury_projection2.desc"
               )
               .setValues(MTConfig.TREASURY_PROJECTION_SCROLL_MANA_COST, MTConfig.TREASURY_PROJECTION_GAUNTLET_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.damage_replication.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.damage_replication.desc"
               )
               .setValues(MTConfig.DAMAGE_REPLICATION_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.rho_aias.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.rho_aias.desc"
               )
               .setValues(MTConfig.RHO_AIAS_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.explosion_staff.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.explosion_staff.desc"
               )
               .setValues()
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.explosion_staff2.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.explosion_staff2.desc"
               )
               .setValues(
                  MTConfig.MYSTIC_STAFF_SUMMON_MANA_COST,
                  MTConfig.MYSTIC_STAFF_BIG_MANA_COST,
                  MTConfig.MYSTIC_STAFF_AOE_MANA_COST,
                  MTConfig.MYSTIC_STAFF_BEAM_MANA_PER_TICK
               )
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.spatial_disorientation.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.spatial_disorientation.desc"
               )
               .setValues(
                  MTConfig.SPATIAL_DISORIENTATION_MANA_COST, MTConfig.SPATIAL_DISORIENTATION_MANA_COST_ST, MTConfig.SPATIAL_DISORIENTATION_MANA_COST_AOE
               )
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.borrowed_authority.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.borrowed_authority.desc"
               )
               .setValues(MTConfig.AUTHORITY_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.cup_of_heaven.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_QUARTZ
               ),
               "mahoutsukai.book.cup_of_heaven.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.cup_of_heaven2.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_QUARTZ
               ),
               "mahoutsukai.book.cup_of_heaven2.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.cup_of_heaven3.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_QUARTZ
               ),
               "mahoutsukai.book.cup_of_heaven3.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.binding_eyes.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.binding_eyes.desc"
               )
               .setValues(MTConfig.MYSTIC_EYES_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.clairvoyance.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.clairvoyance.desc"
               )
               .setValues(MTConfig.CLAIRVOYANCE_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.black_flame_eyes.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.black_flame_eyes.desc"
               )
               .setValues(MTConfig.BLACK_FLAME_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.death_collection_eyes.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.death_collection_eyes.desc"
               )
               .setValues(MTConfig.DEATH_COLLECTION_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.reversion_eyes.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.reversion_eyes.desc"
               )
               .setValues(MTConfig.REVERSION_EYES_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.fay_sight_eyes.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE
                  ),
                  "mahoutsukai.book.fay_sight_eyes.desc"
               )
               .setValues(MTConfig.FAY_SIGHT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.insight.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.insight.desc"
               )
               .setValues(MTConfig.INSIGHT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.summon_familiar.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.summon_familiar.desc"
               )
               .setValues(MTConfig.SUMMON_FAMILIAR_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.recall_familiar.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.recall_familiar.desc"
               )
               .setValues(MTConfig.RECALL_FAMILIAR_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.familiar_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.familiar_exchange.desc"
               )
               .setValues(MTConfig.SWAP_FAMILIAR_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.shared_vision.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.shared_vision.desc"
               )
               .setValues(MTConfig.POSSESS_ENTITY_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.familiars_garden.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.familiars_garden.desc"
               )
               .setValues(MTConfig.FAMILIARS_GARDEN_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.butterfly_effect.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_QUARTZ, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.butterfly_effect.desc"
               )
               .setValues(MTConfig.BUTTERFLY_EFFECT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.damage_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.damage_exchange.desc"
               )
               .setValues(MTConfig.DAMAGE_EXCHANGE_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.chronal_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.chronal_exchange.desc"
               )
               .setValues(MTConfig.CHRONAL_EXCHANGE_MANA_GAIN_LOSS)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.durability_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_DIAMOND
                  ),
                  "mahoutsukai.book.durability_exchange.desc"
               )
               .setValues(MTConfig.DURABILITY_EXCHANGE_MANA_GAIN_CAP)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.catalyst_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.catalyst_exchange.desc"
               )
               .setValues(MTConfig.CATALYST_EXCHANGE_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.alchemical_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_GOLD
                  ),
                  "mahoutsukai.book.alchemical_exchange.desc"
               )
               .setValues(MTConfig.ALCHEMICAL_EXCHANGE_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.contract.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_ENDER
                  ),
                  "mahoutsukai.book.contract.desc"
               )
               .setValues(MTConfig.CONTRACT_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.immunity_exchange.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_EYE
                  ),
                  "mahoutsukai.book.immunity_exchange.desc"
               )
               .setValues(MTConfig.IMMUNITY_EXCHANGE_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.retribution.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.retribution.desc"
               )
               .setValues(MTConfig.RETRIBUTION_MANA_PER_DIFFERENCE)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.presence_concealment.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.presence_concealment.desc"
               )
               .setValues(MTConfig.RIPPER_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.presence_concealment2.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.presence_concealment2.desc"
               )
               .setValues(MTConfig.RIPPER_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.gandr.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_DIAMOND
               ),
               "mahoutsukai.book.gandr.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.geas.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.geas.desc"
               )
               .setValues(MTConfig.GEAS_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.geas2.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_EMERALD, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.geas2.desc"
               )
               .setValues(MTConfig.GEAS_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.fallen_down.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.fallen_down.desc"
               )
               .setValues(MTConfig.FALLEN_DOWN_MANA_PER_TICK)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.fallen_down2.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_IRON
                  ),
                  "mahoutsukai.book.fallen_down2.desc"
               )
               .setValues(MTConfig.FALLEN_DOWN_MANA_PER_TICK)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.probability_alter.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_GOLD, MahoujinRecipeRegistrar.POWDERED_DIAMOND, MahoujinRecipeRegistrar.POWDERED_QUARTZ
                  ),
                  "mahoutsukai.book.probability_alter.desc"
               )
               .setValues(MTConfig.RULE_BREAKER_MANA_COST)
         );
      this.pages
         .add(
            new SpellPage(
                  "mahoutsukai.book.selective_displacement.name",
                  MahoujinRecipeRegistrar.makeRecipe(
                     MahoujinRecipeRegistrar.POWDERED_EYE, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EMERALD
                  ),
                  "mahoutsukai.book.selective_displacement.desc"
               )
               .setValues(MTConfig.SELECTIVE_DISPLACEMENT_MANA_COST)
               .setStrings(Keybinds.selectiveDisplacement.getTranslatedKeyMessage().getString().toUpperCase())
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.kodoku.name", "mahoutsukai.book.kodoku.desc", "", "mahoutsukai.book.kodoku2.name", "mahoutsukai.book.kodoku2.desc", ""
            )
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.kodoku3.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EYE
               ),
               "mahoutsukai.book.kodoku3.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.kodoku4.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EYE
               ),
               "mahoutsukai.book.kodoku4.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.kodoku5.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EYE
               ),
               "mahoutsukai.book.kodoku5.desc"
            )
         );
      this.pages
         .add(
            new SpellPage(
               "mahoutsukai.book.kodoku6.name",
               MahoujinRecipeRegistrar.makeRecipe(
                  MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_ENDER, MahoujinRecipeRegistrar.POWDERED_EYE
               ),
               "mahoutsukai.book.kodoku6.desc"
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.power_consolidation2.name",
               "mahoutsukai.book.power_consolidation2.desc",
               Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase(),
               "mahoutsukai.book.power_consolidation3.name",
               "mahoutsukai.book.power_consolidation3.desc",
               ""
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.clarent.name", "mahoutsukai.book.clarent.desc", "", "mahoutsukai.book.clarent2.name", "mahoutsukai.book.clarent2.desc", ""
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.morgan.name",
               "mahoutsukai.book.morgan.desc",
               "",
               "mahoutsukai.book.morgan2.name",
               "mahoutsukai.book.morgan2.desc",
               Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase()
            )
         );
      this.pages.add(new InfoPage("mahoutsukai.book.emrys.name", "mahoutsukai.book.emrys.desc", ""));
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.rhongomyniad.name",
               "mahoutsukai.book.rhongomyniad.desc",
               "",
               "mahoutsukai.book.rhongomyniad2.name",
               "mahoutsukai.book.rhongomyniad2.desc",
               MTConfig.RHONGOMYNIAD_MANA_COST + ""
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.replica.name",
               "mahoutsukai.book.replica.desc",
               Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase(),
               "mahoutsukai.book.replica2.name",
               "mahoutsukai.book.replica2.desc",
               Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase()
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.replica3.name",
               "mahoutsukai.book.replica3.desc",
               Keybinds.changeMysticCode.getTranslatedKeyMessage().getString().toUpperCase(),
               "mahoutsukai.book.replica4.name",
               "mahoutsukai.book.replica4.desc",
               Math.min(100.0, MTConfig.DEATH_COLLECTION_MAX) + ""
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.rule_breaker.name",
               "mahoutsukai.book.rule_breaker.desc",
               "",
               "mahoutsukai.book.rule_breaker2.name",
               "mahoutsukai.book.rule_breaker2.desc",
               ""
            )
         );
      this.pages
         .add(
            new DoubleInfoPage("mahoutsukai.book.nobu.name", "mahoutsukai.book.nobu.desc", "", "mahoutsukai.book.nobu2.name", "mahoutsukai.book.nobu2.desc", "")
         );
      this.pages
         .add(
            new DoubleInfoPage(
                  "mahoutsukai.book.nobu3.name", "mahoutsukai.book.nobu3.desc", "", "mahoutsukai.book.nobu4.name", "mahoutsukai.book.nobu4.desc", ""
               )
               .setValues(
                  new ArrayList<>(
                     Arrays.asList(
                        Keybinds.gunUp.getTranslatedKeyMessage().getString().toUpperCase(),
                        Keybinds.gunDown.getTranslatedKeyMessage().getString().toUpperCase(),
                        Nobu.getManaPerShot() + "",
                        Nobu.getManaPerConstruct() + ""
                     )
                  )
               )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.william.name",
               "mahoutsukai.book.william.desc",
               "",
               "mahoutsukai.book.william_explanation.name",
               "mahoutsukai.book.william_explanation.desc",
               ""
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.william2.name",
               "mahoutsukai.book.william2.desc",
               "",
               "mahoutsukai.book.william2_explanation.name",
               "mahoutsukai.book.william2_explanation.desc",
               String.format("%.1f", MTConfig.WILLIAM_CREATION_COST)
            )
         );
      this.pages
         .add(
            new DoubleInfoPage(
               "mahoutsukai.book.william3.name",
               "mahoutsukai.book.william3.desc",
               "",
               "mahoutsukai.book.william3_explanation.name",
               "mahoutsukai.book.william3_explanation.desc",
               ""
            )
         );
      ContentsPage contents1 = new ContentsPage(this.pages, 0, 28);
      ContentsPage contents2 = new ContentsPage(this.pages, 28, 56);
      ContentsPage contents3 = new ContentsPage(this.pages, 56, 84);
      ContentsPage contents4 = new ContentsPage(this.pages, 84, 112);
      this.pages.add(0, contents4);
      this.pages.add(0, contents3);
      this.pages.add(0, contents2);
      this.pages.add(0, contents1);
      this.contentsCount = 4;
   }

   public ArrayList<Page> getPages() {
      return this.pages;
   }

   public Page getPage() {
      return this.pages.get(Math.min(this.pages.size() - 1, this.getCurrentPage()));
   }

   public int getContentsCount() {
      return this.contentsCount;
   }
}
