package stepsword.mahoutsukai.item.spells.projection.TreasuryProjection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

import stepsword.mahoutsukai.advancements.MahouTrigger;
import stepsword.mahoutsukai.advancements.ModTriggers;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.settingsmahou.MahouSettings;
import stepsword.mahoutsukai.effects.projection.ProjectionSpellEffect;
import stepsword.mahoutsukai.entity.mahoujin.GateChainEntity;
import stepsword.mahoutsukai.entity.mahoujin.GateWeaponProjectileEntity;
import stepsword.mahoutsukai.item.ItemBase;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.sounds.ModSounds;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class TreasuryProjectionGauntlet extends ItemBase {
   private String MODE_TAG = "MAHOUTSUKAI_TREASURY_MODE";

   public TreasuryProjectionGauntlet() {
      super("treasury_projection_gauntlet", new Properties().stacksTo(1));
   }

   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player.isDiscrete()) {
         if (!world.isClientSide()) {
            Utils.setMode(stack, (Utils.getMode(stack) + 1) % 3);
            int mode = Utils.getMode(stack);
            MutableComponent tt;
            if (mode == 0) {
               tt = Component.translatable("mahoutsukai.treasurygauntlet.targeted");
            } else if (mode == 1) {
               tt = Component.translatable("mahoutsukai.treasurygauntlet.chains");
            } else {
               tt = Component.translatable("mahoutsukai.treasurygauntlet.aoe");
            }

            player.sendOverlayMessage(tt);
         }

         return InteractionResult.SUCCESS;
      } else {
         player.startUsingItem(hand);
         return InteractionResult.PASS;
      }
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack stack, int count) {
      int mode = Utils.getMode(stack);
      if (player instanceof Player && !player.level().isClientSide()) {
         ArrayList<ItemStack> projectables = this.getProjectables((Player)player);
         int numValid = countNonProjected(projectables, level);
         if (mode == 0) {
            if (!projectables.isEmpty()) {
               int mana = MTConfig.TREASURY_PROJECTION_GAUNTLET_MANA_COST;
               ItemStack projectile = projectables.get(player.getRandom().nextInt(projectables.size()));
               int cooldown = (int)Math.max(
                  MTConfig.TREASURY_PROJECTION_COOLDOWN_MIN,
                  MTConfig.TREASURY_PROJECTION_COOLDOWN - numValid * MTConfig.TREASURY_PROJECTION_COOLDOWN_INCREASE_PER_SWORD
               );
               if (count % cooldown == cooldown / 2 && PlayerManaManager.drainMana((Player)player, mana, false, false) == mana) {
                  Vec3 v = this.findSpotToCast(player.level(), (Player)player);
                  RandomSource random = player.getRandom();
                  shootWeaponAt(
                     projectile,
                     player.level(),
                     v,
                     player,
                     random,
                     (float)Math.max(
                        MTConfig.TREASURY_PROJECTION_SPEED_MIN,
                        MTConfig.TREASURY_PROJECTION_SPEED - MTConfig.TREASURY_PROJECTION_SPEED_INCREASE_PER_SWORD * numValid
                     )
                  );
               }
            }
         } else if (mode == 1) {
            int mana = MTConfig.TREASURY_PROJECTION_CHAINS_MANA_COST;
            int cooldown = MTConfig.TREASURY_PROJECTION_CHAINS_COOLDOWN;
            int cnt = MTConfig.TREASURY_PROJECTION_CHAINS_COUNT;
            if (count % cooldown == cooldown / 2 && count < this.getUseDuration(stack, player)) {
               if (this.getUseDuration(stack, player) - count <= cooldown) {
                  this.chains((Player)player, true, cnt, mana);
               } else if (count % 19 * cooldown == 0) {
                  this.chains((Player)player, true, cnt, mana);
               } else {
                  this.chains((Player)player, false, 1, mana);
               }
            }
         } else if (mode == 2 && projectables.size() > 0) {
            int mana = MTConfig.TREASURY_PROJECTION_AOE_MANA_COST;
            int cooldown = (int)Math.max(
               MTConfig.TREASURY_PROJECTION_COOLDOWN_MIN,
               MTConfig.TREASURY_PROJECTION_AOE_COOLDOWN - numValid * MTConfig.TREASURY_PROJECTION_COOLDOWN_INCREASE_PER_SWORD
            );
            if (count % cooldown == cooldown / 2 && PlayerManaManager.drainMana((Player)player, mana, false, false) == mana && projectables.size() > 0) {
               int numSpawns = (int)Math.min(MTConfig.TREASURY_PROJECTION_AOE_MAX_SPAWNS, numValid * MTConfig.TREASURY_PROJECTION_AOE_SWORDS_PER_SPAWN);

               for (int i = 0; i < Math.max(1, numSpawns); i++) {
                  ItemStack projectile = projectables.get(player.getRandom().nextInt(projectables.size()));
                  behindShoot(
                     projectile,
                     player.level(),
                     player,
                     player.getRandom(),
                     (float)Math.max(
                        MTConfig.TREASURY_PROJECTION_SPEED_MIN,
                        MTConfig.TREASURY_PROJECTION_SPEED - MTConfig.TREASURY_PROJECTION_SPEED_INCREASE_PER_SWORD * numValid
                     )
                  );
               }
            }
         }
      }

      super.onUseTick(level, player, stack, count);
   }

   public static int countNonProjected(ArrayList<ItemStack> lst, Level level) {
      int ret = 0;
      HashSet<String> seen = new HashSet<>();
      if (lst != null) {
         for (ItemStack s : lst) {
            String registryName = Utils.getRegistryName(s.getItem(), level);
            if (!ProjectionSpellEffect.getProjected(s) && (!MTConfig.TREASURY_PROJECTION_INCREASE_REQUIRES_UNIQUE || !seen.contains(registryName))) {
               ret++;
               seen.add(registryName);
            }
         }
      }

      return ret;
   }

   protected boolean isArrow(ItemStack stack, Level level) {
      Item tmpi = stack.getItem();
      if (stack.isEmpty()) {
         return false;
      }

      List<? extends String> white = MTConfig.TREASURY_PROJECTION_WHITELIST;
      List<? extends String> black = MTConfig.TREASURY_PROJECTION_BLACKLIST;
      boolean allow_all = MTConfig.TREASURY_PROJECTION_ALLOW_ALL;
      boolean deny_all = MTConfig.TREASURY_PROJECTION_DENY_ALL;
      boolean sword = stepsword.mahoutsukai.fabric.JoustingCompatibility.isLance(tmpi) || Utils.isSword(stack)
         || Utils.isTool(stack)
         || tmpi instanceof ShieldItem
         || tmpi instanceof ShearsItem
         || tmpi instanceof FishingRodItem
         || tmpi instanceof HoeItem
         || tmpi instanceof TridentItem
;
      if (deny_all) {
         sword = false;
      }

      boolean inwhite = false;
      boolean inblack = false;
      inwhite = EffectUtil.inItemBlacklist(stack.getItem(), white, level);
      inblack = EffectUtil.inItemBlacklist(stack.getItem(), black, level);
      return inblack ? false : sword || inwhite || allow_all;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BLOCK;
   }

   public Vec3 findSpotToCast(Level world, Player player) {
      Entity e = Utils.selectEntityNearCursor(
         player,
         MTConfig.GLOBAL_LOOK_RANGE,
         player.level(),
         a -> Utils.getPredicate(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR, a, player),
         Utils.nearAllowed(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR)
      );
      Vec3 v;
      if (e instanceof LivingEntity) {
         v = e.position().add(0.0, e.getBbHeight() / 2.0F, 0.0);
      } else {
         v = this.getTargetBlock(player);
      }

      return v;
   }

   public ArrayList<ItemStack> getProjectables(Player player) {
      int size = player.getInventory().getContainerSize();
      ArrayList<ItemStack> projectables = new ArrayList<>();
      if (!MTConfig.TREASURY_PROJECTION_ENDER_CHEST_ONLY) {
         for (int i = 0; i < size; i++) {
            ItemStack stack1 = player.getInventory().getItem(i);
            if (this.isArrow(stack1, player.level())) {
               projectables.add(stack1);
            }

            for(ItemStack stack2:Utils.snapshotNestedItems(stack1))if(this.isArrow(stack2,player.level()))projectables.add(stack2);
         }
      }

      for (int i = 0; i < player.getEnderChestInventory().getContainerSize(); i++) {
         ItemStack stack1 = player.getEnderChestInventory().getItem(i);
         if (this.isArrow(stack1, player.level())) {
            projectables.add(stack1);
         }

         for(ItemStack stack2:Utils.snapshotNestedItems(stack1))if(this.isArrow(stack2,player.level()))projectables.add(stack2);
      }

      return projectables;
   }

   public Vec3 getTargetBlock(Player player) {
      HitResult result = Utils.rayTraceEyes(player, MTConfig.GLOBAL_LOOK_RANGE);
      Entity e = Utils.selectEntityNearCursor(
         player,
         MTConfig.GLOBAL_LOOK_RANGE,
         player.level(),
         a -> Utils.getPredicate(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR, a, player),
         Utils.nearAllowed(Utils.MAHOU_SELECTION.WEAPON_NO_NEAR)
      );
      Vec3 target;
      if (result.getType() == Type.BLOCK && e == null) {
         BlockHitResult br = (BlockHitResult)result;
         target = new Vec3(br.getBlockPos().getX() + 0.5, br.getBlockPos().getY(), br.getBlockPos().getZ() + 0.5).add(0.0, 1.0, 0.0);
      } else if (e != null) {
         target = e.getEyePosition(1.0F);
      } else {
         Vec3 v = player.getViewVector(0.0F);
         Vec3 v2 = new Vec3(v.x * MTConfig.GLOBAL_LOOK_RANGE, v.y * MTConfig.GLOBAL_LOOK_RANGE, v.z * MTConfig.GLOBAL_LOOK_RANGE);
         Vec3 v3 = player.getEyePosition(1.0F).add(v2);

         while (Utils.isBlockAir(player.level(), Utils.toBlockPos(v3)) && v3.y > 0.0) {
            v3 = v3.add(new Vec3(0.0, -1.0, 0.0));
         }

         target = v3;
      }

      return target;
   }

   public int getMaxStackSize(ItemStack stack) {
      return 1;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public static void shootWeaponAt(ItemStack stack, Level world, Vec3 pos, LivingEntity player, RandomSource random, float speed) {
      int d = MTConfig.TREASURY_PROJECTION_DISTANCE;
      int d2 = MTConfig.TREASURY_PROJECTION_MIN_DISTANCE;
      int tries = 0;

      GateWeaponProjectileEntity weapon;
      do {
         tries++;
         int circleDegrees = random.nextInt(360);
         int groundDegrees = random.nextInt(70) + 20;
         int distance = random.nextInt(d2) + (d - d2);
         double height = distance * Math.cos(EffectUtil.toRad(groundDegrees));
         double radius = Math.sqrt(distance * distance - height * height);
         double zplus = radius * Math.sin(EffectUtil.toRad(circleDegrees));
         double xplus = -radius * Math.cos(EffectUtil.toRad(circleDegrees));
         weapon = new GateWeaponProjectileEntity(world, pos.x + xplus, pos.y + height, pos.z + zplus, stack.copy(), player, speed);
         List<Integer> settings = MahouSettings.getIntSettings((Player)player, MahouSettings.Spell.TREASURY_WEAPONS);
         weapon.setColor(
            Utils.colorConvert(settings.get(0)), Utils.colorConvert(settings.get(1)), Utils.colorConvert(settings.get(2)), 1.0F, 0.0F, 0.0F, 0.0F, 1.0F
         );
         weapon.xRot = groundDegrees;
         weapon.yRot = circleDegrees - 90;
         weapon.setOwner(player);
         Vec3 aim = weapon.position().subtract(pos);
         weapon.shoot(-aim.x, -aim.y, -aim.z, 3.0F, 4.0F);
      } while (!world.isEmptyBlock(Utils.toBlockPos(weapon.position())) && tries < 20);

      world.addFreshEntity(weapon);
      if (player instanceof ServerPlayer) {
         ((MahouTrigger)ModTriggers.TREASURY_PROJECTION).trigger((ServerPlayer)player);
      }
   }

   public static void behindShoot(ItemStack stack, Level world, LivingEntity player, RandomSource random, float speed) {
      int tries = 0;

      GateWeaponProjectileEntity weapon;
      do {
         tries++;
         float scale = (float)(player.getRandom().nextFloat() * (MTConfig.TREASURY_PROJECTION_AOE_MAX_SPAWNS - 2.0) + 3.0);
         float height = (float)(player.getRandom().nextFloat() * (MTConfig.TREASURY_PROJECTION_AOE_MAX_SPAWNS - 1.0) + 1.0);
         Vec3 look = player.getLookAngle();
         Vec3 playerpos = player.position();
         Vec3 playerperp = playerpos;
         playerperp = playerperp.add(look.normalize().scale(-3.0));
         if (player.getRandom().nextBoolean()) {
            playerperp = playerperp.add(
               Vec3.directionFromRotation(0.0F, player.yRot + 90.0F).normalize().scale(player.getRandom().nextInt((int)scale) - 0.3).scale(2.0)
            );
         } else {
            playerperp = playerperp.add(
               Vec3.directionFromRotation(0.0F, player.yRot - 90.0F).normalize().scale(player.getRandom().nextInt((int)scale) - 0.3).scale(2.0)
            );
         }

         playerperp = playerperp.add(
            Vec3.directionFromRotation(player.xRot - 90.0F, player.yRot).normalize().scale(player.getRandom().nextInt((int)(scale * 0.75)) + 1.8).scale(2.0)
         );
         weapon = new GateWeaponProjectileEntity(world, playerperp.x, playerperp.y, playerperp.z, stack.copy(), player, speed);
         List<Integer> settings = MahouSettings.getIntSettings((Player)player, MahouSettings.Spell.TREASURY_WEAPONS);
         weapon.setColor(
            Utils.colorConvert(settings.get(0)), Utils.colorConvert(settings.get(1)), Utils.colorConvert(settings.get(2)), 1.0F, 0.0F, 0.0F, 0.0F, 1.0F
         );
         weapon.xRot = player.xRot;
         weapon.yRot = player.yRot;
         Vec3 pos = weapon.position().add(player.getLookAngle());
         weapon.setOwner(player);
         Vec3 aim = weapon.position().subtract(pos);
         weapon.shoot(-aim.x, -aim.y, -aim.z, 3.0F, 4.0F);
      } while (!world.isEmptyBlock(Utils.toBlockPos(weapon.position())) && tries < 20);

      world.addFreshEntity(weapon);
      if (player instanceof ServerPlayer) {
         ((MahouTrigger)ModTriggers.TREASURY_PROJECTION).trigger((ServerPlayer)player);
      }
   }

   public void chains(Player player, boolean prison, int tospawn, int mana) {
      if (PlayerManaManager.drainMana(player, mana * tospawn, false, false) == mana * tospawn) {
         Entity e = Utils.selectEntityNearCursor(
            player,
            MTConfig.GLOBAL_LOOK_RANGE,
            player.level(),
            a -> Utils.getPredicate(Utils.MAHOU_SELECTION.WEAPON, a, player),
            Utils.nearAllowed(Utils.MAHOU_SELECTION.WEAPON)
         );
         boolean entity = false;
         Vec3 v;
         float h;
         float w;
         if (e instanceof LivingEntity) {
            v = e.position();
            h = e.getBbHeight() + 2.0F;
            w = e.getBbWidth() + 1.5F;
            entity = true;
         } else {
            v = this.getTargetBlock(player);
            h = 5.0F;
            w = 3.0F;
         }

         if (prison) {
            ArrayList<Integer> degreeList = new ArrayList<>();

            for (int i = 0; i < tospawn; i++) {
               int q = 360 / tospawn;
               int init = q * i;
               init += player.getRandom().nextInt(q / 3) - q / 6;
               degreeList.add(init);
            }

            for (int i = 0; i < tospawn; i++) {
               if (i < degreeList.size()) {
                  int circleDegrees = degreeList.get(i);
                  double zplus = w * Math.sin(EffectUtil.toRad(circleDegrees));
                  double xplus = -w * Math.cos(EffectUtil.toRad(circleDegrees));
                  Vec3 chainpos = v.add(0.0, h, 0.0).add(new Vec3(xplus, 0.0, zplus));
                  Vec3 aimTo = v.subtract(chainpos)
                     .normalize()
                     .add(player.getRandom().nextFloat() * 2.0F - 1.0F, player.getRandom().nextFloat() - 0.5, player.getRandom().nextFloat() * 2.0F - 1.0F)
                     .normalize()
                     .scale(40.0);
                  BlockHitResult raytrace = player.level().clip(new ClipContext(chainpos, chainpos.add(aimTo), Block.COLLIDER, Fluid.SOURCE_ONLY, player));
                  Vec3 pos = EffectUtil.fromBlockPos(raytrace.getBlockPos());
                  if (!Utils.isBlockAir(player.level(), Utils.toBlockPos(pos))) {
                     List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.TREASURY_CHAINS);
                     GateChainEntity chain = new GateChainEntity(
                        player.level(),
                        Utils.colorConvert(settings.get(0)),
                        Utils.colorConvert(settings.get(1)),
                        Utils.colorConvert(settings.get(2)),
                        1.0F,
                        chainpos,
                        pos
                     );
                     Vec3 aim = chain.position().subtract(pos);
                     chain.shoot(-aim.x, -aim.y, -aim.z);
                     chain.setCircleSize(1.0F);
                     player.level()
                        .playSound(
                           null,
                           Utils.toBlockPos(chain.position()),
                           (SoundEvent)ModSounds.CHAINS.get(),
                           SoundSource.NEUTRAL,
                           (float)MTConfig.CHAIN_VOLUME,
                           1.2F
                        );
                     player.level().addFreshEntity(chain);
                  }
               }
            }
         } else if (entity) {
            int circleDegrees = player.getRandom().nextInt(360);
            double zplus = w * Math.sin(EffectUtil.toRad(circleDegrees));
            double xplus = -w * Math.cos(EffectUtil.toRad(circleDegrees));
            Vec3 chainpos = v.add(0.0, h, 0.0).add(new Vec3(xplus, 0.0, zplus));
            List<Integer> settings = MahouSettings.getIntSettings(player, MahouSettings.Spell.TREASURY_CHAINS);
            Vec3 pos = e.position();
            GateChainEntity chain = new GateChainEntity(
               player.level(),
               Utils.colorConvert(settings.get(0)),
               Utils.colorConvert(settings.get(1)),
               Utils.colorConvert(settings.get(2)),
               1.0F,
               chainpos,
               pos
            );
            Vec3 aim = chain.position().subtract(pos);
            chain.shoot(-aim.x, -aim.y, -aim.z);
            chain.setCircleSize(1.0F);
            player.level()
               .playSound(null, Utils.toBlockPos(chain.position()), (SoundEvent)ModSounds.CHAINS.get(), SoundSource.NEUTRAL, (float)MTConfig.CHAIN_VOLUME, 1.2F);
            player.level().addFreshEntity(chain);
         }
      }
   }
}
