from pathlib import Path
m=Path('MahouTsukai-Fabric/src/main/java/stepsword/mahoutsukai');o=Path('recovered/stepsword/mahoutsukai');c=Path('MahouTsukai-Fabric/src/client/java/stepsword/mahoutsukai')
for n in ['item/Dagger.java','potion/BleedingPotion.java','potion/NoShowEffect.java','item/rulebreaker/ProbabilityAlterRandom.java','potion/ProbabilityAlterPotion.java','datacomponents/rulebreaker/RuleBreakerMahou.java','item/rulebreaker/RuleBreaker.java']:
 s=(o/n).read_text();s=s.replace('player.hurt(player.damageSources().generic(), 2.0F);','player.hurtServer((net.minecraft.server.level.ServerLevel)world, player.damageSources().generic(), 2.0F);')
 if n.endswith('Dagger.java'):
  s=s.replace('new Properties().stacksTo(1).craftRemainder(new ItemStackTemplate(ModItems.dagger))','new Properties().stacksTo(1)')
  s=s.replace('   public InteractionResult use(','''   @Override public ItemStack getCraftingRemainder(ItemStack input) { return new ItemStack(this); }
   public InteractionResult use(''')
 if n.endswith('RuleBreaker.java'):
  s=s.replace('import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;','')
  s=s.replace('ruleBreakerLivingHurt(LivingIncomingDamageEvent event)', 'ruleBreakerLivingHurt(LivingEntity target, net.minecraft.world.damagesource.DamageSource source)').replace('event.getSource().getEntity()', 'source.getEntity()').replace('         LivingEntity target = event.getEntity();\n','')
 p=m/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
p=m/'config/MTConfig.java';s=p.read_text().replace('    private static final com.google.gson.Gson JSON', '''    public static boolean SHOW_BLEEDING = true;
    public static int RULE_BREAKER_DURABILITY = 40;
    public static int RULE_BREAKER_DURATION = 800;
    private static final com.google.gson.Gson JSON''').replace('case "DEATH_COLLECTION_DRAIN_PER_SOUL",', 'case "RULE_BREAKER_DURABILITY", "RULE_BREAKER_DURATION", "DEATH_COLLECTION_DRAIN_PER_SOUL",');p.write_text(s)
p=m/'potion/ModEffects.java';s=p.read_text().replace(' public static int getColorNumber', ''' public static final Holder<MobEffect> BLEEDING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
  Identifier.fromNamespaceAndPath("mahoutsukai", "bleeding"), new BleedingPotion());
 public static final Holder<MobEffect> PROBABILITY_ALTER = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
  Identifier.fromNamespaceAndPath("mahoutsukai", "probability_alter"), new ProbabilityAlterPotion());
 public static int getColorNumber''').replace('return (r << 16) | (g << 8) | b;', 'return r << 16 + g << 8 + b;');p.write_text(s)
p=m/'item/ModItems.java';s=p.read_text().replace('    static {','''    public static final Supplier<Item> dagger = register("dagger", Dagger::new);
    public static final Supplier<Item> ruleBreaker = register("rule_breaker", stepsword.mahoutsukai.item.rulebreaker.RuleBreaker::new);
    static {''',1);p.write_text(s)
p=m/'datacomponents/DataComponents.java';s=p.read_text();orig=(o/'datacomponents/DataComponents.java').read_text();a=orig.index('   public static final Codec<RuleBreakerMahou> RULE_BREAKER_CODEC');b=orig.index('   public static final DeferredHolder',a);defs=orig[a:b];s=s.replace('import com.mojang.serialization.Codec;', 'import com.mojang.serialization.Codec;\nimport stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou;');s=s.replace('    public static void init()',defs+'''    public static final DataComponentType<RuleBreakerMahou> RULE_BREAKER_COMPONENT = DataComponentType.<RuleBreakerMahou>builder()
        .persistent(RULE_BREAKER_CODEC).networkSynchronized(RULE_BREAKER_STREAM_CODEC).build();
    public static void init()''');s=s.replace('    public static void init() {','''    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("mahoutsukai", "rule_breaker_mahou"), RULE_BREAKER_COMPONENT);''');p.write_text(s)
p=m/'util/Utils.java';s=p.read_text().replace('    public static GemMahou getGemMahou','''    public static stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou getRuleBreakerMahou(ItemStack stack) {
        var type = stepsword.mahoutsukai.datacomponents.DataComponents.RULE_BREAKER_COMPONENT;
        var value = stack.get(type);
        if (value == null) { value = new stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou(); stack.set(type, value); return value; }
        return value.copy();
    }
    public static void setRuleBreakerMahou(ItemStack stack, stepsword.mahoutsukai.datacomponents.rulebreaker.RuleBreakerMahou value) {
        stack.set(stepsword.mahoutsukai.datacomponents.DataComponents.RULE_BREAKER_COMPONENT, value);
    }
    public static GemMahou getGemMahou''');orig=(o/'util/Utils.java').read_text();a=orig.index('   public static float avgStep');b=orig.index('   public static ArrayList<BlockPos> findTilesInRange',a);s=s.replace('    public static void debug',orig[a:b]+'    public static void debug');p.write_text(s)
p=m/'fabric/mixin/LivingMahouMixin.java';s=p.read_text().replace('    @Override', '''    @Inject(method = "tick", at = @At("HEAD"))
    private void mahoutsukai$probabilityTick(CallbackInfo ci) {
        stepsword.mahoutsukai.potion.ProbabilityAlterPotion.applyProbabilityAlter((LivingEntity)(Object)this);
    }
    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void mahoutsukai$ruleBreakerHit(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source,
            float amount, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        stepsword.mahoutsukai.item.rulebreaker.RuleBreaker.ruleBreakerLivingHurt((LivingEntity)(Object)this, source);
    }
    @Override''',1);p.write_text(s)
p=Path('MahouTsukai-Fabric/src/main/resources/mahoutsukai.accesswidener');s=p.read_text()+'\naccessible field net/minecraft/world/entity/Entity random Lnet/minecraft/util/RandomSource;\nmutable field net/minecraft/world/entity/Entity random Lnet/minecraft/util/RandomSource;\n';p.write_text(s)
# Full original animated blade, adapted to Fabric extra model baking.
p=c/'render/RenderBaseItem.java';p.write_text('''package stepsword.mahoutsukai.render;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
public final class RenderBaseItem {
 public static final ExtraModelKey<QuadCollection> RULE_BREAKER = ExtraModelKey.create(() -> "mahoutsukai:rule_breaker");
 public static void init() {
  ModelLoadingPlugin.register(context -> context.addModel(RULE_BREAKER,
   new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai", "item/rule_breaker_model"),
    (model, baker) -> model.bakeTopGeometry(model.getTopTextureSlots(), baker, net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
 }
 public static void render(QuadCollection collection, PoseStack matrix, SubmitNodeCollector collector, RenderType type, boolean bright) {
  QuadInstance instance = new QuadInstance();
  if (bright) instance.setLightCoords(15728880);
  instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
  collector.submitCustomGeometry(matrix, type != null ? type : Sheets.translucentItemSheet(), (pose, buffer) -> {
   if (collection != null) for (var quad : collection.getAll()) buffer.putBakedQuad(pose, quad, instance);
  });
 }
}
''')
p=c/'render/item/RuleBreakerRenderer.java';s=(o/'render/item/RuleBreakerRenderer.java').read_text().replace('.getStandaloneModel(RenderBaseItem.rule_breaker.key)', '.getModel(RenderBaseItem.RULE_BREAKER)');p.write_text(s)
p=c/'render/RenderUtils.java';s=p.read_text().replace('    public static final Identifier mahoujin','''    public static void rotateQ(float degrees, float x, float y, float z, com.mojang.blaze3d.vertex.PoseStack matrix) {
        float radians = degrees * (float)(Math.PI / 180.0);
        float sin = org.joml.Math.sin(radians / 2);
        matrix.mulPose(new org.joml.Quaternionf(x*sin, y*sin, z*sin, org.joml.Math.cos(radians / 2)));
    }
    public static void conditionalBatchEnder(net.minecraft.client.renderer.rendertype.RenderType type, net.minecraft.client.renderer.MultiBufferSource buffer) {
        if (buffer instanceof net.minecraft.client.renderer.MultiBufferSource.BufferSource source) source.endBatch(type);
    }
    public static final Identifier mahoujin''');p.write_text(s)
p=c/'render/item/MahouSpecialItemModel.java';s=p.read_text().replace('            if (!renderer.equals', '            if (renderer.equals("rule_breaker")) return new RuleBreakerRenderer();\n            if (!renderer.equals',1);p.write_text(s)
p=c/'fabric/MahouFabricClient.java';s=p.read_text().replace('    public void onInitializeClient() {','    public void onInitializeClient() {\n        stepsword.mahoutsukai.render.RenderBaseItem.init();',1);p.write_text(s)
# Resource closure.
script=Path('scripts/copy_economy_resources.py').read_text();ns={'__file__':str(Path('scripts/copy_economy_resources.py').resolve())};exec(script[:script.index('for item in [')],ns)
for name in ['dagger','rule_breaker']:
 ns['copy']('assets/mahoutsukai/items/'+name+'.json');ns['model']('mahoutsukai:item/'+name)
ns['model']('mahoutsukai:item/rule_breaker_model');ns['copy']('data/mahoutsukai/recipe/dagger.json')
for name in ['bleeding','probability_alter']:ns['copy']('assets/mahoutsukai/textures/mob_effect/'+name+'.png')
p=Path('scripts/check_resources.py');s=p.read_text().replace("ids |= {'scroll_fay_sight'", "ids |= {'dagger', 'rule_breaker'}\nids |= {'scroll_fay_sight'").replace("{'mana_circuit', 'mana_circuit_magitech'}", "{'mana_circuit', 'mana_circuit_magitech', 'rule_breaker'}");p.write_text(s)
