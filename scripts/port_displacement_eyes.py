from pathlib import Path
import re,shutil,json
root=Path.cwd();src=root/'MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai';orig=root/'recovered/stepsword/mahoutsukai';res=root/'MahouTsukai-Fabric/src/main/resources'
def edit(p,a,b):
 s=p.read_text();assert a in s,(p,a);p.write_text(s.replace(a,b))
def copy(rel):
 p=src/rel;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((orig/rel).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable'));return p
for group,cls,field,name,cats,item,scrollcls,itemfield in [('displacement','OrderedDisplacement','orderedDisplacement','ordered_displacement',['ENDER']*3,None,None,None),('displacement','EquivalentDisplacement','equivalentDisplacement','equivalent_displacement',['ENDER','ENDER','EMERALD'],None,None,None),('eyes','BindingEyes','mysticEyes','mystic_eyes',['EYE','EYE','GOLD'],'mystic_eyes','BindingEyes','bindingEyes'),('secret','Retribution','retribution','retribution',['EMERALD','IRON','DIAMOND'],'retribution','Retribution','retribution')]:
 copy(f'block/spells/{group}/{cls}Mahoujin.java');p=copy(f'tile/{group}/{cls}MahoujinTileEntity.java')
 if item:
  p.write_text(p.read_text().replace(f'ModItems.{itemfield}Scroll.get()',f'ModItems.REGISTERED.get("scroll_{item}")'))
  copy(f'item/spells/{group}/{scrollcls}SpellScroll.java');edit(src/'item/ModItems.java','    static {',f'    static {{\n        register("scroll_{item}",stepsword.mahoutsukai.item.spells.{group}.{scrollcls}SpellScroll::new);')
  for folder in ['items','models/item']:shutil.copyfile(root/f'recovered/assets/mahoutsukai/{folder}/scroll_{item}.json',res/f'assets/mahoutsukai/{folder}/scroll_{item}.json')
  model=json.loads((res/f'assets/mahoutsukai/models/item/scroll_{item}.json').read_text())
  for tex in model.get('textures',{}).values():
   ns,path=tex.split(':');q=res/f'assets/{ns}/textures/{path}.png';q.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f'recovered/assets/{ns}/textures/{path}.png',q)
 edit(src/'block/ModBlocks.java','    private static Supplier<Block> plain',f'    public static final Supplier<Block> {field}Mahoujin = plain("mahoujin_{name}",new stepsword.mahoutsukai.block.spells.{group}.{cls}Mahoujin(),false);\n    private static Supplier<Block> plain')
 edit(src/'tile/ModTileEntities.java','    public static void init()',f'    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity>> {itemfield or field} = register("mahoujin_{name}",stepsword.mahoutsukai.tile.{group}.{cls}MahoujinTileEntity::new,ModBlocks.{field}Mahoujin.get());\n    public static void init()')
 edit(src/'block/spells/MahoujinRecipeRegistrar.java','   public static void init() {',f'   public static void init() {{\n      var {field} = makeRecipe('+','.join('POWDERED_'+c for c in cats)+f');\n      recipes.put({field},(BlockBase)ModBlocks.{field}Mahoujin.get());needsCloth.put({field},{str(bool(item)).lower()});')
 client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/MahouFabricClient.java';edit(client,'        stepsword.mahoutsukai.Keybinds.init();',f'        stepsword.mahoutsukai.Keybinds.init();\n        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.{itemfield or field}.get(),stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);')
 shutil.copyfile(root/f'recovered/assets/mahoutsukai/blockstates/mahoujin_{name}.json',res/f'assets/mahoutsukai/blockstates/mahoujin_{name}.json')
copy('potion/BindingEyesPotion.java');p=copy('potion/BoundPotion.java');s=p.read_text().replace('import net.minecraft.client.player.ClientInput;\n','');s=s[:s.index('   public static void boundMovement')]+ '}\n';p.write_text(s)
for name,cls in [('binding_eyes','BindingEyes'),('bound','Bound')]:
 edit(src/'potion/ModEffects.java',' public static void init()',f' public static final Holder<MobEffect> {name.upper()} = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,Identifier.fromNamespaceAndPath("mahoutsukai","{name}"),new {cls}Potion());\n public static void init()')
 shutil.copyfile(root/f'recovered/assets/mahoutsukai/textures/mob_effect/{name}.png',res/f'assets/mahoutsukai/textures/mob_effect/{name}.png')
# Original targeting helpers; retain the cone/line sort used by Retribution.
def method(text,needle):
 start=text.index('   public static ',text.index(needle)-30) if False else text.rfind('   public static ',0,text.index(needle)+len(needle));brace=text.index('{',text.index(needle));depth=1;end=brace+1
 while depth:
  if text[end]=='{':depth+=1
  elif text[end]=='}':depth-=1
  end+=1
 return text[start:end]+'\n'
u=(orig/'util/Utils.java').read_text();e=(orig/'util/EffectUtil.java').read_text();helpers=method(u,'distanceToLine(')+method(u,'selectEntityNearCursor(')+method(u,'selectEntitiesNearCursor(')
# first occurrences may be callers; select declarations explicitly.
helpers=''
for name in ['distanceToLine','selectEntityNearCursor','selectEntitiesNearCursor']:
 match=re.search(r'   public static [^\n]+ '+name+r'\(',u);assert match,name
 helpers+=method(u[match.start():],name+'(')
p=src/'util/Utils.java';edit(p,'    public static void debug(String message)',helpers+'    public static void debug(String message)');s=p.read_text().replace('List<Entity>','java.util.List<Entity>').replace('ArrayList<Entity>','java.util.ArrayList<Entity>').replace('new ArrayList<>','new java.util.ArrayList<>').replace('Predicate<Entity>','java.util.function.Predicate<Entity>').replace('Comparator.comparingDouble','java.util.Comparator.comparingDouble').replace('AABB aabb','net.minecraft.world.phys.AABB aabb');p.write_text(s)
helpers=''
for declaration in ['public static double pointToLineDistance','public static boolean inRange','public static boolean isLookingAtMe(LivingEntity','public static boolean isLookingAtMe(Entity']:
 start=e.index('   '+declaration);helpers+=method(e[start:],declaration+'(' if '(' not in declaration else declaration)
helpers=helpers.replace('Vec3','net.minecraft.world.phys.Vec3').replace('Entity me','net.minecraft.world.entity.Entity me').replace('Livingnet.minecraft.world.entity.Entity','LivingEntity')
edit(src/'util/EffectUtil.java',' public static boolean hasBuff',helpers+' public static boolean hasBuff')
p=copy('effects/secret/RetributionSpellEffect.java');s=p.read_text();a=s.index('         a -> Utils.getPredicate');b=s.index('\n      );',a);s=s[:a]+'         a -> a instanceof LivingEntity, true'+s[b:];s=s.replace('Utils.getRegistryKey(target, target.level()) != null','net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()) != null');p.write_text(s)
p=copy('effects/displacement/EquivalentDisplacementSpellEffect.java');s=p.read_text();s=re.sub(r'import net.neoforged[^\n]+\n','',s);s=s.replace('equivalentDisplacementShiftRightClick(RightClickBlock event)','equivalentDisplacementShiftRightClick(Player player, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, InteractionHand hand)').replace('event.getLevel()','world').replace('event.getHand()','hand').replace('event.getPos()','pos').replace('         Player player = event.getEntity();\n','').replace('event.getEntity()','player');p.write_text(s)
p=src/'tickets/ModTickets.java';p.parent.mkdir(parents=True,exist_ok=True);p.write_text('''package stepsword.mahoutsukai.tickets;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.TicketType;
public final class ModTickets {
 public static final Supplier<TicketType> EQUIV_DISP_TICKET=register("eq_disp_ticket");
 private static Supplier<TicketType> register(String name) {
  var type=Registry.register(BuiltInRegistries.TICKET_TYPE,Identifier.fromNamespaceAndPath("mahoutsukai",name),new TicketType(0L,6));return ()->type;
 }
 public static void init() {}
}
''')
p=src/'fabric/MahouFabric.java';edit(p,'        stepsword.mahoutsukai.datacomponents.DataComponents.init();','        stepsword.mahoutsukai.tickets.ModTickets.init();\n        stepsword.mahoutsukai.datacomponents.DataComponents.init();');edit(p,'            target instanceof net.minecraft.world.entity.LivingEntity living &&','            stepsword.mahoutsukai.potion.BoundPotion.boundAttackFromPlayer(player) || target instanceof net.minecraft.world.entity.LivingEntity living &&');edit(p,'        stepsword.mahoutsukai.advancements.ModTriggers.init();','''        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,world,hand,hit)-> {
            if(stepsword.mahoutsukai.potion.BoundPotion.boundRightClickBlock(player)) return net.minecraft.world.InteractionResult.FAIL;
            stepsword.mahoutsukai.effects.displacement.EquivalentDisplacementSpellEffect.equivalentDisplacementShiftRightClick(player,world,hit.getBlockPos(),hand);
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player,world,hand,pos,side)->
            stepsword.mahoutsukai.potion.BoundPotion.boundLeftClickBlock(player) ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player,world,hand)->
            stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player) ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((entity,world)-> {
            if(entity instanceof stepsword.mahoutsukai.tile.displacement.EquivalentDisplacementMahoujinTileEntity circle) circle.releaseChunks();
        });
        stepsword.mahoutsukai.advancements.ModTriggers.init();''');edit(p,'                stepsword.mahoutsukai.mana.PlayerManaManager.onPlayerTick(player, tick);','                stepsword.mahoutsukai.mana.PlayerManaManager.onPlayerTick(player, tick);\n                stepsword.mahoutsukai.potion.BindingEyesPotion.bindingEyesPlayerTick(player);')
p=src/'block/spells/Mahoujin.java';edit(p,'               if(mte instanceof stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity','               if(mte instanceof stepsword.mahoutsukai.tile.displacement.OrderedDisplacementMahoujinTileEntity ordered) ordered.setTeleporterId(worldIn.getGameTime());\n               if(mte instanceof stepsword.mahoutsukai.tile.exchange.ChronalExchangeMahoujinTileEntity')
# Only native server entity tick is skipped, preserving the original bound behavior for mobs.
(src/'fabric/mixin/BoundEntityTickMixin.java').write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
@Mixin(ServerLevel.class)
public abstract class BoundEntityTickMixin {
 @WrapOperation(method="tickNonPassenger",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/Entity;tick()V"))
 private void mahoutsukai$bound(Entity entity,Operation<Void> tick) {
  if(entity instanceof LivingEntity living && stepsword.mahoutsukai.potion.BindingEyesPotion.bindingEyesLivingUpdate(living)) return;
  tick.call(entity);
 }
}
''')
client=root/'MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai/fabric/mixin/BoundInputMixin.java';client.parent.mkdir(parents=True,exist_ok=True);client.write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(KeyboardInput.class)
public abstract class BoundInputMixin extends ClientInput {
 @Inject(method="tick",at=@At("TAIL"))
 private void mahoutsukai$stop(CallbackInfo ci) {
  var player=Minecraft.getInstance().player;
  if(player!=null && stepsword.mahoutsukai.potion.BoundPotion.boundRightClickItem(player)) {
   keyPresses=stepsword.mahoutsukai.potion.BoundPotion.STANDING_STILL;moveVector=Vec2.ZERO;
  }
 }
}
''')
p=res/'mahoutsukai.fabric.mixins.json';j=json.loads(p.read_text());j['mixins'].append('BoundEntityTickMixin');j['client']=['BoundInputMixin'];p.write_text(json.dumps(j,indent=2)+'\n')
# Exact effective defaults for every newly used scalar setting.
original=(orig/'config/MTConfig.java').read_text();p=src/'config/MTConfig.java';needed=set()
for rel in ['tile/displacement/OrderedDisplacementMahoujinTileEntity.java','tile/displacement/EquivalentDisplacementMahoujinTileEntity.java','effects/secret/RetributionSpellEffect.java','item/spells/secret/RetributionSpellScroll.java','item/spells/eyes/BindingEyesSpellScroll.java','potion/BindingEyesPotion.java']:
 needed.update(re.findall(r'MTConfig\.(\w+)',(src/rel).read_text()))
existing=set(re.findall(r'public static [\w.<>? ]+ (\w+) =',p.read_text()));fields=[]
for name in sorted(needed-existing):
 m=re.search(r'   public static (int|double|boolean|List<\? extends String>) '+name+r' = (.*?);',original,re.S);assert m,name
 typ,val=m.groups();typ=typ.replace('List<','java.util.List<');val=val.replace('new ArrayList<>()','new java.util.ArrayList<>()').replace('Arrays.asList','java.util.Arrays.asList')
 d=re.search(r'"'+name+r'",\s*(-?[\d.E]+|true|false)(?:,|\s*\))',original)
 if d:val=d.group(1)
 fields.append(f'    public static {typ} {name} = {val};\n')
edit(p,'    private static final com.google.gson.Gson JSON',''.join(fields)+'    private static final com.google.gson.Gson JSON')
print('Adapted ordered/equivalent displacement, binding eyes and retribution')
