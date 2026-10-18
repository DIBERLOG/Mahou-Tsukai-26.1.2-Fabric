from pathlib import Path
import re
m=Path('MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai');o=Path('recovered/stepsword/mahoutsukai');c=Path('MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai')
s=(o/'effects/secret/GeasEffect.java').read_text();s=re.sub(r'import net.neoforged.*;\n','',s)
a=s.index('      Entity e = Utils.selectEntityNearCursor(');b=s.index('      if (e instanceof LivingEntity)',a);s=s[:a]+'      Entity e = Utils.selectGeasTarget(caster);\n'+s[b:]
s=s.replace('geasItem(Pre event)','geasItem(Player player, ItemEntity item)').replace('hasGeas(event.getPlayer(), event.getItemEntity().getItem())','hasGeas(player, item.getItem())').replace('MandatoryFun.completeFunReq(event.getPlayer(),','MandatoryFun.completeFunReq(player,')
a=s.index('   public static boolean geasLivingHurt(');b=s.index('   public static boolean addGeas(',a);s=s[:a]+'''   public static boolean geasLivingHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource source) {
      return source.getEntity() instanceof Player player && hasGeas(player,target);
   }

'''+s[b:];(m/'effects/secret/GeasEffect.java').write_text(s)
# Preserve the existing saved MandatoryFun achievement marker, without pulling unported Caliburn gating into this stage.
s=(o/'config/MandatoryFun.java').read_text();a=s.index('   public static boolean meetsAllFunReqs');b=s.index('   public enum FUN_REQS',a);s=s[:a]+s[b:];(m/'config/MandatoryFun.java').write_text(s)
for n in ['item/spells/secret/GeasSpellScroll.java','tile/secret/GeasMahoujinTileEntity.java','block/spells/secret/GeasMahoujin.java']:
 p=m/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text((o/n).read_text().replace('javax.annotation.Nullable','org.jetbrains.annotations.Nullable').replace('ModItems.geasScroll.get()','ModItems.REGISTERED.get("scroll_geas")'))
p=m/'util/Utils.java';s=p.read_text();orig=(o/'util/Utils.java').read_text();a=orig.index('   public static boolean GetIntersection(');b=orig.index('   @Deprecated',a);geometry=orig[a:b];a=orig.index('   public static boolean blockBetween(');b=orig.index('   public static boolean getPredicate(',a);block=orig[a:b];a=orig.index('   public static class Vec3H {');h=orig[a:orig.rfind('}')];s=s.replace('import net.minecraft.world.entity.player.Player;', '''import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;''');s=s.replace('    public static void debug',geometry+block+h+'''    /** Original SCROLL_GEAS targeting branch: strict ray/box hit, opaque-block check, nearest entity. */
    public static Entity selectGeasTarget(Player player) {
        double distance=stepsword.mahoutsukai.config.MTConfig.GLOBAL_LOOK_RANGE;
        var eyes=player.getEyePosition(1);var end=eyes.add(player.getLookAngle().scale(distance));
        return player.level().getEntities(player, player.getBoundingBox().inflate(distance), entity ->
            !EffectUtil.inEntityBlacklist(entity,stepsword.mahoutsukai.config.MTConfig.GEAS_BLACKLIST)
             && (entity instanceof net.minecraft.world.entity.LivingEntity || entity instanceof net.minecraft.world.entity.item.ItemEntity))
            .stream().filter(entity -> {
                var box=entity.getBoundingBox();
                return CheckLineBox(new Vec3(box.minX,box.minY,box.minZ),new Vec3(box.maxX,box.maxY,box.maxZ),eyes,end)
                    && !blockBetween(eyes,box.getCenter(),player.level());
            }).min(java.util.Comparator.comparingDouble(entity->entity.position().distanceTo(eyes))).orElse(null);
    }
    public static String getRegistryName(Entity entity) { return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(); }
    public static void debug''');p.write_text(s)
p=m/'util/EffectUtil.java';s=p.read_text().replace(' public static boolean hasBuff', ''' public static boolean inEntityBlacklist(net.minecraft.world.entity.Entity entity,List<? extends String> values) {
  if(entity==null) return false;
  var id=net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
  return inGenericBlacklist(id.toString(),values) || inGenericBlacklist(id.getNamespace(),values);
 }
 public static boolean hasBuff''');p.write_text(s)
p=m/'config/MTConfig.java';s=p.read_text().replace('    private static final com.google.gson.Gson JSON', '''    public static int GEAS_MANA_COST = 30;
    public static int GLOBAL_LOOK_RANGE = 100;
    public static java.util.List<String> GEAS_BLACKLIST = java.util.List.of("minecraft:armor_stand");
    private static final com.google.gson.Gson JSON''');p.write_text(s)
p=m/'item/ModItems.java';s=p.read_text().replace('    static {','    static {\n        register("scroll_geas",stepsword.mahoutsukai.item.spells.secret.GeasSpellScroll::new);',1);p.write_text(s)
p=m/'block/ModBlocks.java';s=p.read_text().replace('    private static Supplier<Block> plain(', '''    public static final Supplier<Block> geasMahoujin = plain("mahoujin_geas",new stepsword.mahoutsukai.block.spells.secret.GeasMahoujin(),false);
    private static Supplier<Block> plain(''');p.write_text(s)
p=m/'tile/ModTileEntities.java';s=p.read_text().replace('    public static void init() {}','''    public static final Supplier<BlockEntityType<stepsword.mahoutsukai.tile.secret.GeasMahoujinTileEntity>> geas = register("mahoujin_geas",stepsword.mahoutsukai.tile.secret.GeasMahoujinTileEntity::new,ModBlocks.geasMahoujin.get());
    public static void init() {}''');p.write_text(s)
p=m/'block/spells/MahoujinRecipeRegistrar.java';s=p.read_text().replace('      var sight =','''      var geas = makeRecipe(POWDERED_GOLD, POWDERED_EMERALD, POWDERED_IRON);
      recipes.put(geas,(BlockBase)ModBlocks.geasMahoujin.get());needsCloth.put(geas,true);
      var sight =''',1);p.write_text(s)
p=m/'fabric/MahouFabric.java';s=p.read_text().replace('''            if (entity instanceof net.minecraft.world.entity.player.Player && amount''','''            if (stepsword.mahoutsukai.effects.secret.GeasEffect.geasLivingHurt(entity,source)) return false;
            if (entity instanceof net.minecraft.world.entity.player.Player && amount''');s=s.replace('        ModItems.init();','''        ModItems.init();
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player,world,hand,target,hit)->
            target instanceof net.minecraft.world.entity.LivingEntity living && stepsword.mahoutsukai.effects.secret.GeasEffect.hasGeas(player,living)
                ? net.minecraft.world.InteractionResult.FAIL : net.minecraft.world.InteractionResult.PASS);''',1);p.write_text(s)
p=m/'fabric/mixin/GeasPickupMixin.java';p.write_text('''package stepsword.mahoutsukai.fabric.mixin;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ItemEntity.class)
public abstract class GeasPickupMixin {
 @Inject(method="playerTouch",at=@At("HEAD"),cancellable=true)
 private void mahoutsukai$geasPickup(Player player,CallbackInfo callback) {
  if(!player.level().isClientSide() && stepsword.mahoutsukai.effects.secret.GeasEffect.geasItem(player,(ItemEntity)(Object)this)) callback.cancel();
 }
}
''')
import json
p=Path('MahouTsukai-Fabric/src/main/resources/mahoutsukai.fabric.mixins.json');s=json.loads(p.read_text());s['mixins'].append('GeasPickupMixin');p.write_text(json.dumps(s,indent=2))
p=c/'fabric/MahouFabricClient.java';s=p.read_text().replace('        stepsword.mahoutsukai.Keybinds.init();','''        stepsword.mahoutsukai.Keybinds.init();
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(stepsword.mahoutsukai.tile.ModTileEntities.geas.get(), stepsword.mahoutsukai.render.tile.MahoujinRenderer::new);''');p.write_text(s)
# Original resources for the fourth ritual.
script=Path('scripts/copy_economy_resources.py').read_text();ns={'__file__':str(Path('scripts/copy_economy_resources.py').resolve())};exec(script[:script.index('for item in [')],ns)
ns['copy']('assets/mahoutsukai/items/scroll_geas.json');ns['model']('mahoutsukai:item/scroll_geas');ns['copy']('assets/mahoutsukai/blockstates/mahoujin_geas.json');ns['model']('mahoutsukai:block/mahoujin_geas')
p=Path('scripts/check_resources.py');s=p.read_text().replace("'scroll_probability_alter'}","'scroll_probability_alter', 'scroll_geas'}");p.write_text(s)
