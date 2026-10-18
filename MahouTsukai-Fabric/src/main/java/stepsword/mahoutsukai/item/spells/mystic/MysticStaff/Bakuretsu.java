package stepsword.mahoutsukai.item.spells.mystic.MysticStaff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import stepsword.mahoutsukai.config.MTConfig;
import stepsword.mahoutsukai.entity.mahoujin.MysticStaffMahoujinEntity;
import stepsword.mahoutsukai.util.EffectUtil;
import stepsword.mahoutsukai.util.Utils;

public class Bakuretsu {
   public int radius;
   public float posX;
   public float posY;
   public float posZ;
   public HashMap<Player, Vec3> knockback = new HashMap<>();
   public float damage;

   public Bakuretsu() {
   }

   public Bakuretsu(int r, float x, float y, float z, float damage) {
      this();
      this.radius = r;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.damage = damage;
   }

   public Bakuretsu(int r, BlockPos pos) {
      this();
      this.radius = r;
      this.posX = pos.getX();
      this.posY = pos.getY();
      this.posZ = pos.getZ();
   }

   public void explosionA(Level world, Player entity) {
      List<BlockPos> affected = new ArrayList<>();
      var fake = new net.minecraft.world.level.ServerExplosion((net.minecraft.server.level.ServerLevel)world,entity,null,null,new Vec3(this.posX,this.posY,this.posZ),this.radius,false,Explosion.BlockInteraction.DESTROY);
      List<Entity> lst = this.playerKnockback(world, entity, fake);
      if (MTConfig.MYSTIC_STAFF_WORLD_DAMAGE) {
         for (float x = this.posX - this.radius; x < this.posX + this.radius + 1.0F; x++) {
            for (float y = this.posY - this.radius; y < this.posY + this.radius + 1.0F; y++) {
               for (float z = this.posZ - this.radius; z < this.posZ + this.radius + 1.0F; z++) {
                  float cmp = this.radius * this.radius
                     - (this.posX - x) * (this.posX - x)
                     - (this.posY - y) * (this.posY - y)
                     - (this.posZ - z) * (this.posZ - z);
                  if (cmp > 0.0F) {
                     BlockPos tmp = Utils.toBlockPos(x, y, z);
                     Block b = world.getBlockState(tmp).getBlock();
                     if (!EffectUtil.unchangableBlock(b, Arrays.asList(Blocks.CAVE_AIR, Blocks.VOID_AIR, Blocks.OBSIDIAN, Blocks.AIR))
                        && b.getExplosionResistance() < 4000.0F) {
                        affected.add(tmp);
                     }
                  }
               }
            }
         }

         EffectUtil.applySpellBlockChanges(affected,entity,entity,false,new ArrayList<>(lst),new Vec3(this.posX,this.posY,this.posZ),-1,(state,owner,drop,pos)->owner.level().destroyBlock(pos,false,owner));
      }

      Vec3 boom = new Vec3(this.posX, this.posY, this.posZ);

      for (Entity e : lst) {
         this.hurt(e, boom, entity);
      }
   }

   public List<Entity> playerKnockback(Level world, Player player, Explosion explosion) {
      AABB aabb = new AABB(
         this.posX - this.radius, this.posY - this.radius, this.posZ - this.radius, this.posX + this.radius, this.posY + this.radius, this.posZ + this.radius
      );
      List<Entity> entities = world.getEntities((Entity)null, aabb, Entity::isAlive);
      List<Entity> ret = new ArrayList<>();
      new Vec3(this.posX, this.posY, this.posZ);

      for (Entity entity : entities) {
         if (!entity.ignoreExplosion(explosion) && !(entity instanceof MysticStaffMahoujinEntity)) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            double cmp = this.radius * this.radius - (this.posX - x) * (this.posX - x) - (this.posY - y) * (this.posY - y) - (this.posZ - z) * (this.posZ - z);
            if (cmp > 0.0) {
               ret.add(entity);
            }
         }
      }

      return ret;
   }

   public void hurt(Entity entity, Vec3 boom, Player player) {
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      double cmp = this.radius * this.radius - (this.posX - x) * (this.posX - x) - (this.posY - y) * (this.posY - y) - (this.posZ - z) * (this.posZ - z);
      float density = getBlockDensity(boom, entity);
      if(!stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects((net.minecraft.server.level.ServerLevel)entity.level(),entity.position()))entity.hurtServer((net.minecraft.server.level.ServerLevel)entity.level(),player.damageSources().explosion(player,null),this.damage*density);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).lastHurtByPlayerMemoryTime = 100;
      }

      Vec3 kb = new Vec3(x, y, z).add(new Vec3(-this.posX, -this.posY, -this.posZ)).normalize().scale(Math.sqrt(cmp) / 4.0);
      double motionX = entity.getDeltaMovement().x;
      double motionY = entity.getDeltaMovement().y;
      double motionZ = entity.getDeltaMovement().z;
      motionX += kb.x;
      motionY += kb.y;
      motionZ += kb.z;
      entity.setDeltaMovement(motionX, motionY, motionZ);
      if (entity instanceof Player) {
         this.knockback.put((Player)entity, kb);
      }
   }

   public static float getBlockDensity(Vec3 explosionVector, Entity entity) {
      AABB AABB = entity.getBoundingBox();
      double d0 = 1.0 / ((AABB.maxX - AABB.minX) * 2.0 + 1.0);
      double d1 = 1.0 / ((AABB.maxY - AABB.minY) * 2.0 + 1.0);
      double d2 = 1.0 / ((AABB.maxZ - AABB.minZ) * 2.0 + 1.0);
      double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
      double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
      if (!(d0 < 0.0) && !(d1 < 0.0) && !(d2 < 0.0)) {
         int i = 0;
         int j = 0;

         for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1)) {
               for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
                  double d5 = Mth.lerp(f, AABB.minX, AABB.maxX);
                  double d6 = Mth.lerp(f1, AABB.minY, AABB.maxY);
                  double d7 = Mth.lerp(f2, AABB.minZ, AABB.maxZ);
                  Vec3 Vec3 = new Vec3(d5 + d3, d6, d7 + d4);
                  if (entity.level()
                        .clip(new ClipContext(Vec3, explosionVector, net.minecraft.world.level.ClipContext.Block.OUTLINE, Fluid.NONE, entity))
                        .getType()
                     == Type.MISS) {
                     i++;
                  }

                  j++;
               }
            }
         }

         return (float)i / j;
      } else {
         return 0.0F;
      }
   }
}
