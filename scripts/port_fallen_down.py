from remaining_port_helpers import *
ritual('item/spells/secret/FallenDownSpellScroll.java','block/spells/secret/FallenDownMahoujin.java','tile/secret/FallenDownMahoujinTileEntity.java','fallenDownMahoujin')
for field,cls,id in [('FALLEN_DOWN','FallenDownEntity','fallen_down_entity'),('FALLEN_DOWN_BEAM','FallenDownBeamEntity','fallen_down_beam_entity')]:
 entity(field,'entity/mahoujin/'+cls+'.java',id)
 p=main/('entity/mahoujin/'+cls+'.java');s=p.read_text().replace('import stepsword.mahoutsukai.handlers.ServerHandler;','').replace('import stepsword.mahoutsukai.item.mysticcode.MysticCode;','').replace('ServerHandler.tickCounter','this.level().getGameTime()').replace(' && !(caster.getUseItem().getItem() instanceof MysticCode)','').replace('EffectUtil.tryFakeExplosion(','EffectUtil.applySpellBlockChanges(').replace('List<String> inblocks','List<? extends String> inblocks').replace('List<String> outblocks','List<? extends String> outblocks').replace('bs.isFlammable(this.level(), pos, Direction.UP)','((net.minecraft.world.level.block.FireBlock)Blocks.FIRE).canBurn(bs)').replace('e.hurt(e.damageSources().onFire(), damage + extra);','e.hurtServer((ServerLevel)this.level(),e.damageSources().onFire(), damage + extra);')
 # Native FallingBlockEntity.fall already inserts the entity into the world.
 s=s.replace('                  world.addFreshEntity(fallingblockentity);','')
 s=s.replace('            this.discard();\n         }\n\n         if (caster == null)', '            this.discard();return;\n         }\n\n         if (caster == null)')
 write(p,s)
append(main/'util/Utils.java',''' public static net.minecraft.resources.Identifier getRegistryKey(net.minecraft.world.level.block.Block block,net.minecraft.world.level.Level world){return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);}
''')
append(main/'util/EffectUtil.java',''' @FunctionalInterface public interface SpellBlockOperation {boolean apply(net.minecraft.world.level.block.state.BlockState state,net.minecraft.world.entity.Entity entity,boolean drop,net.minecraft.core.BlockPos pos);}
 /** Native block-break permission and shield checks replace the original NeoForge explosion events. */
 public static void applySpellBlockChanges(java.util.List<net.minecraft.core.BlockPos> affected,net.minecraft.world.entity.Entity target,net.minecraft.world.entity.player.Player caster,boolean drop,java.util.ArrayList<net.minecraft.world.entity.Entity> entities,net.minecraft.world.phys.Vec3 location,int limit,SpellBlockOperation operation){
  if(!(target.level() instanceof net.minecraft.server.level.ServerLevel world))return;
  var owner=caster!=null?caster:stepsword.mahoutsukai.entity.butterfly.SafeFakePlayer.createFakePlayerIfNull(world);int changed=0;
  for(var original:affected){if(limit>=0 && changed>=limit)break;var pos=original.immutable();var state=world.getBlockState(pos);
   if(unchangableBlock(state.getBlock()) || stepsword.mahoutsukai.effects.mystic.RhoAiasSpellEffect.protects(world,net.minecraft.world.phys.Vec3.atCenterOf(pos)))continue;
   if(!net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world,owner,pos,state,world.getBlockEntity(pos)))continue;
   if(operation.apply(state,target,drop,pos))changed++;
  }
 }
''')
p=res/'mahoutsukai.accesswidener';s=p.read_text();lines=['accessible method net/minecraft/world/level/block/FireBlock canBurn (Lnet/minecraft/world/level/block/state/BlockState;)Z','accessible field net/minecraft/world/level/Level rainLevel F'];write(p,s+'\n'+'\n'.join(x for x in lines if x not in s)+'\n')
config()
# Adapt every original layer to native collected geometry, retaining all ring sizes and colors.
for cls,method,field,entitycls in [('RenderFallenDownEntity','renderFallenDown','FALLEN_DOWN','FallenDownEntity'),('RenderFallenDownBeamEntity','renderFallenDownBeam','FALLEN_DOWN_BEAM','FallenDownBeamEntity')]:
 p=client/('render/entity/'+cls+'.java');s=(orig/('render/entity/'+cls+'.java')).read_text();s=s.replace('float partialTicks) {','float partialTicks,SubmitNodeCollector buffer) {').replace('      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();','').replace('BufferSource buffer,','SubmitNodeCollector buffer,');s=re.sub(r'\s*(?:VertexConsumer )?bb = buffer.getBuffer\(type\);','',s);s=s.replace('renderCircle(bb, matrix,','renderCircle(buffer,type, matrix,');s=s.replace('   public static void renderCircle(VertexConsumer bb, PoseStack matrix,','   public static void renderCircle(SubmitNodeCollector buffer,RenderType type, PoseStack matrix,');s=s.replace('      Matrix4f m = matrix.last().pose();','      buffer.submitCustomGeometry(matrix,type,(pose,bb)->{\n      Matrix4f m = pose.pose();');pos=s.rfind('   }');s=s[:pos]+'      });\n'+s[pos:];s=re.sub(r'\s*buffer.endBatch\([^;]+;','',s);s=re.sub(r'\s*RenderUtils.conditionalBatchEnder\([^;]+;','',s)
 s=s.replace('MahoujinRenderType.wrapCullLayer(','(');write(p,s)
 p=client/'fabric/SixFamilyClient.java';text=p.read_text();line=f'  net.minecraft.client.renderer.entity.EntityRenderers.register(stepsword.mahoutsukai.entity.ModEntities.{field}.get(),stepsword.mahoutsukai.render.entity.{cls}::new);';text=text.replace(' public static void init(){',' public static void init(){\n'+line) if line not in text else text
 anchor='   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.GandrEntity.class';i=text.index(anchor);line=f'   for(var entity:mc.level.getEntitiesOfClass(stepsword.mahoutsukai.entity.mahoujin.{entitycls}.class,mc.player.getBoundingBox().inflate(256)))stepsword.mahoutsukai.render.entity.{cls}.{method}(entity,entity.getX()-camera.x,entity.getY()-camera.y,entity.getZ()-camera.z,context.poseStack(),partial,context.submitNodeCollector());\n';text=text[:i]+line+text[i:] if line not in text else text;write(p,text)
append(client/'render/RenderUtils.java',''' public static net.minecraft.client.renderer.rendertype.RenderType renderRingCull(com.mojang.blaze3d.vertex.PoseStack matrix,net.minecraft.client.renderer.SubmitNodeCollector collector,double cy,float flatness,float radius,float height,int segments,int lx,int ly,float r,float g,float b,float a,int index){return renderRing(matrix,collector,cy,flatness,radius,height,segments,lx,ly,r,g,b,a,MahoujinRenderType.createScopedRunesRenderType("render_utils_ring_cull",runes,index));}
''')
