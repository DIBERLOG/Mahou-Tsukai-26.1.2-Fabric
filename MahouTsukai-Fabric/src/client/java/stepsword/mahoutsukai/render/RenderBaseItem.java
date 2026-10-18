package stepsword.mahoutsukai.render;
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
 public static final ExtraModelKey<QuadCollection> CLARENT=ExtraModelKey.create(()->"mahoutsukai:clarent");
 public static final ExtraModelKey<QuadCollection> RED_KEY=ExtraModelKey.create(()->"mahoutsukai:proximity_projection_key");
 public static final ExtraModelKey<QuadCollection> SPATIAL_STAFF=ExtraModelKey.create(()->"mahoutsukai:spatial_disorientation_staff");
 public static final class ModelKeyPair {public final ExtraModelKey<QuadCollection> key;public ModelKeyPair(String name){key=ExtraModelKey.create(()->name);}}
 public static final ModelKeyPair caliburn=new ModelKeyPair("mahoutsukai:caliburn_model");
 public static final ModelKeyPair morgan=new ModelKeyPair("mahoutsukai:morgan_model");
 public static final ModelKeyPair treasuryGauntlet=new ModelKeyPair("mahoutsukai:treasury_projection_gauntlet_model");
 public static final ModelKeyPair standby=new ModelKeyPair("mahoutsukai:weapon_projectile_bow_standby");
 public static final ModelKeyPair mystic_staff=new ModelKeyPair("mahoutsukai:mystic_staff_model");
 public static final ModelKeyPair mysticCode=new ModelKeyPair("mahoutsukai:item/mystic_code_model");
 public static final ModelKeyPair mysticCode1=new ModelKeyPair("mahoutsukai:item/mystic_code_1");
 public static final ModelKeyPair mysticCode2=new ModelKeyPair("mahoutsukai:item/mystic_code_2");
 public static final ModelKeyPair mysticCode3=new ModelKeyPair("mahoutsukai:item/mystic_code_3");
 public static final ModelKeyPair mysticCodeIster=new ModelKeyPair("mahoutsukai:item/mystic_code_ister");
 public static final ModelKeyPair mysticCodeFirstSorcery=new ModelKeyPair("mahoutsukai:item/mystic_code_first_sorcery_model");
 public static final ModelKeyPair mysticCodeFirstSorcery1=new ModelKeyPair("mahoutsukai:item/mystic_code_first_sorcery_1");
 public static final ModelKeyPair mysticCodeFirstSorcery2=new ModelKeyPair("mahoutsukai:item/mystic_code_first_sorcery_2");
 public static final ModelKeyPair mysticCodeFirstSorcery3=new ModelKeyPair("mahoutsukai:item/mystic_code_first_sorcery_3");
 public static final ModelKeyPair mysticCodeFirstSorceryIster=new ModelKeyPair("mahoutsukai:item/mystic_code_first_sorcery_ister");
 public static final ModelKeyPair staff=new ModelKeyPair("mahoutsukai:item/staff_emrys_model");
 public static final ModelKeyPair replica=new ModelKeyPair("mahoutsukai:item/replica_model");
 public static final ModelKeyPair replicaBlocking=new ModelKeyPair("mahoutsukai:item/replica_blocking_model");
 public static final ModelKeyPair rhongomyniad=new ModelKeyPair("mahoutsukai:item/rhongomyniad");
 public static void init() {
  ModelLoadingPlugin.register(context->context.addModel(rhongomyniad.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/rhongomyniad"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(replicaBlocking.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/replica_blocking_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(replica.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/replica_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(staff.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/staff_emrys_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCodeFirstSorceryIster.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_first_sorcery_ister"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCodeFirstSorcery3.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_first_sorcery_3"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCodeFirstSorcery2.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_first_sorcery_2"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCodeFirstSorcery1.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_first_sorcery_1"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCodeFirstSorcery.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_first_sorcery_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCodeIster.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_ister"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCode3.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_3"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCode2.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_2"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCode1.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_1"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mysticCode.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_code_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(mystic_staff.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/mystic_staff_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(standby.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/weapon_projectile_bow_standby"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(treasuryGauntlet.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/treasury_projection_gauntlet_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(morgan.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/morgan_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(caliburn.key,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/caliburn_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(SPATIAL_STAFF,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/spatial_disorientation_staff_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(RED_KEY,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/proximity_projection_key"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
  ModelLoadingPlugin.register(context->context.addModel(CLARENT,new SimpleUnbakedExtraModel<>(Identifier.fromNamespaceAndPath("mahoutsukai","item/clarent_model"),(model,baker)->model.bakeTopGeometry(model.getTopTextureSlots(),baker,net.minecraft.client.renderer.block.dispatch.BlockModelRotation.IDENTITY))));
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
