package stepsword.mahoutsukai.tile.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.dataattachments.worldsave.MahouSavedData;
import stepsword.mahoutsukai.effects.secret.GeasEffect;
import stepsword.mahoutsukai.mana.ActiveCircleConfig;
import stepsword.mahoutsukai.mana.PlayerManaManager;
import stepsword.mahoutsukai.tile.AboveChecker;
import stepsword.mahoutsukai.tile.MahoujinTickingTileEntity;
import stepsword.mahoutsukai.tile.ModTileEntities;
import stepsword.mahoutsukai.util.Utils;

public class ContractMahoujinTileEntity extends MahoujinTickingTileEntity implements AboveChecker {
   public ContractMahoujinTileEntity(BlockPos p, BlockState bs) {
      super((BlockEntityType)ModTileEntities.contract.get(), p, bs);
   }

   @Override
   public Class<? extends Entity> checkForEntity() {
      return Player.class;
   }

   @Override
   public int checkTicks() {
      return MTConfig.CONTRACT_BLOCK_CYCLE;
   }

   @Override
   public boolean firstEntityOnly() {
      return true;
   }

   @Override
   public boolean preConditions() {
      return !this.level.isClientSide() && ActiveCircleConfig.tryToOperate(this, this.getCasterUUID());
   }

   @Override
   public boolean ifEntityFoundAbove(Entity e) {
      MahouSavedData save = Utils.getWorldMahou(this.level);
      List<Player> targets = this.getTargets();
      if (targets != null && !targets.isEmpty() && e instanceof Player caster && caster.getUUID().equals(this.getCasterUUID())) {
         for (Player entityIn : targets) {
            if (!save.doesContractExist(caster.getUUID(), entityIn.getUUID())) {
               int cost = MTConfig.CONTRACT_MANA_COST;
               if (canPayContractCost(caster, cost)
                  && canPayContractCost(entityIn, cost)
                  && PlayerManaManager.drainMana(caster, cost, false, false) == cost
                  && PlayerManaManager.drainMana(entityIn, cost, false, false) == cost
                  && save.addContractIfNotExists(caster.getUUID(), entityIn.getUUID())) {
                  entityIn.sendOverlayMessage(Component.literal(ChatFormatting.RED + "A contract has been formed with " + caster.getDisplayName().getString()));
                  caster.sendOverlayMessage(Component.literal(ChatFormatting.RED + "A contract has been formed with " + entityIn.getDisplayName().getString()));
               }
            }
         }
      }

      return false;
   }

   public ArrayList<Player> getTargets() {
      int radius = MTConfig.CONTRACT_RADIUS;
      Stream<BlockPos> blocks = BlockPos.betweenClosedStream(
         this.worldPosition.offset(-radius, -radius, -radius), this.worldPosition.offset(radius, radius, radius)
      );
      ArrayList<Player> ret = new ArrayList<>();
      blocks.forEach(tmp -> {
         BlockEntity te = this.level.getBlockEntity(tmp);
         if (te instanceof ContractMahoujinTileEntity mte && !this.worldPosition.equals(tmp)) {
            List<? extends Entity> es = mte.checkAbove(te, true);
            if (!es.isEmpty() && es.getFirst() instanceof Player p && p.isAlive() && p.getUUID().equals(mte.getCasterUUID())) {
               ret.add(p);
            }
         }
      });
      return ret;
   }

   private static boolean canPayContractCost(Player player, int cost) {
      return cost <= 0 || player.isCreative() && MTConfig.CREATIVE_IGNORES_MANA_COSTS || PlayerManaManager.countMana(player) >= cost;
   }

   public static boolean isImmuneToSpell(Level world, UUID caster, Entity e) {
      MahouSavedData save = Utils.getWorldMahou(world);
      if (e instanceof ArmorStand) {
         return true;
      }

      if (e instanceof TamableAnimal && caster != null) {
         LivingEntity owner = ((TamableAnimal)e).getOwner();
         UUID ownerId = owner == null ? null : owner.getUUID();
         if (ownerId != null && (ownerId.equals(caster) || save != null && save.doesContractExist(caster, ownerId))) {
            return true;
         }
      }


      if (e instanceof Player && caster != null) {
         if (e.getUUID().equals(caster)) {
            return true;
         }

         if (save != null && save.doesContractExist(caster, e.getUUID())) {
            return true;
         }

         if (((Player)e).isCreative() || e.isSpectator()) {
            return true;
         }
      }

      if (caster != null && e instanceof LivingEntity) {
         MinecraftServer server = world.getServer();
         if (server != null) {
            Player player = server.getPlayerList().getPlayer(caster);
            if (player != null && GeasEffect.hasGeas(player, (LivingEntity)e)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isImmuneToSpellClient(Player caster, Entity e) {
      if (e instanceof ArmorStand) {
         return true;
      }

      if (e instanceof TamableAnimal && caster != null) {
         LivingEntity owner = ((TamableAnimal)e).getOwner();
         UUID ownerId = owner == null ? null : owner.getUUID();
         if (ownerId != null && ownerId.equals(caster.getUUID())) {
            return true;
         }
      }


      if (e instanceof Player && caster != null) {
         if (e.getUUID().equals(caster.getUUID())) {
            return true;
         }

         if (((Player)e).isCreative() || e.isSpectator()) {
            return true;
         }
      }

      return caster != null && e instanceof LivingEntity && GeasEffect.hasGeas(caster, (LivingEntity)e);
   }

   public static void breakContract(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
      if (entity instanceof Player player && !player.level().isClientSide()) {
         MahouSavedData save = Utils.getWorldMahou(player.level());
         
         if (source.getEntity() != null && save.doesContractExist(player.getUUID(), source.getEntity().getUUID())) {
            player.sendOverlayMessage(Component.literal(ChatFormatting.RED + "A contract has broken."));
            if (source.getEntity() instanceof Player) {
               ((Player)source.getEntity()).sendOverlayMessage(Component.literal(ChatFormatting.RED + "A contract has broken."));
               save.removeContract(player.getUUID(), source.getEntity().getUUID());
            }
         }
      }
   }
}
