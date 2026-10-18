from remaining_port_helpers import *
ritual('item/spells/mystic/RhoAiasSpellScroll.java','block/spells/mystic/RhoAiasMahoujin.java','tile/mystic/RhoAiasMahoujinTileEntity.java','rhoAiasMahoujin')
entity('RHO_AIAS','entity/mahoujin/RhoAiasMahoujinEntity.java','rho_aias_mahoujin_entity')
p=copy('effects/mystic/RhoAiasSpellEffect.java');s=p.read_text().replace('import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;','');a=s.index('   public static void rhoAiasExplosionProtection');s=s[:a]+'''   public static boolean protects(ServerLevel world,Vec3 point){for(Entity entity:world.getEntities().getAll())if(entity instanceof RhoAiasMahoujinEntity shield && !shield.isRemoved() && shield.isPointInRadiusOfCircle(point))return true;return false;}
   public static List<BlockPos> protectBlocks(ServerLevel world,List<BlockPos> blocks){return new ArrayList<>(blocks.stream().filter(p->!protects(world,Vec3.atCenterOf(p))).toList());}
   public static List<Entity> protectEntities(ServerLevel world,List<Entity> entities){return new ArrayList<>(entities.stream().filter(e->!protects(world,e.position()) && !protects(world,e.position().add(0,e.getBbHeight(),0))).toList());}
}
''';write(p,s)
p=main/'entity/mahoujin/RhoAiasMahoujinEntity.java';s=p.read_text().replace('this.yRot = this.getRotationYaw();','this.setYRot(this.getRotationYaw());').replace('this.xRot = this.getRotationPitch();','this.setXRot(this.getRotationPitch());').replace('this.setRot(this.yRot, this.xRot);','this.setRot(this.getYRot(), this.getXRot());').replace('List<String> killList','List<? extends String> killList').replace('List<String> collideList','List<? extends String> collideList');a=s.index('      if (e instanceof GunBeamProjectileEntity g)');b=s.index('\n   }',a);s=s[:a]+'      e.discard();'+s[b:];write(p,s)
# Custom serializers are registered with Fabric before native entity types initialize.
p=main/'entity/ModEntityDataSerializers.java';insert(p,' public static void init()', ''' public static final EntityDataSerializer<net.minecraft.world.phys.Vec3> VEC3=EntityDataSerializer.forValueType(net.minecraft.world.phys.Vec3.STREAM_CODEC);
 static {net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("mahoutsukai","vec3"),VEC3);}
''')
config()
