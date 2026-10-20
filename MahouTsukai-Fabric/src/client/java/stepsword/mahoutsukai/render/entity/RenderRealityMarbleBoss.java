package stepsword.mahoutsukai.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.Identifier;
import stepsword.mahoutsukai.entity.RealityMarbleBossEntity;
import stepsword.mahoutsukai.render.entity.archmage.ArchmageModel;

/** Native Archmage cuboid rig, synced combat poses and emissive eyes/crystal. */
public class RenderRealityMarbleBoss extends MobRenderer<RealityMarbleBossEntity, RenderRealityMarbleBoss.State, ArchmageModel> {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("mahoutsukai", "textures/entity/illager_archmage.png");

   private final net.minecraft.client.renderer.item.ItemModelResolver items;
   public static final class State extends ArmedEntityRenderState {
      public int attackTicks,attackKind,arrivalTicks,recoveryTicks;
      public float melee,hurtTime,healthRatio;
      public boolean levitating;
   }

   public RenderRealityMarbleBoss(EntityRendererProvider.Context context) {
      super(context, new ArchmageModel(context.bakeLayer(ArchmageModel.LAYER)), .8F);
      items=context.getItemModelResolver();
      addLayer(new net.minecraft.client.renderer.entity.layers.RenderLayer<State,ArchmageModel>(this){
         @Override public void submit(PoseStack pose,net.minecraft.client.renderer.SubmitNodeCollector collector,int light,State state,float yaw,float pitch){
            for(var hand:net.minecraft.world.entity.HumanoidArm.values()){
               var item=hand==net.minecraft.world.entity.HumanoidArm.RIGHT?state.rightHandItemState:state.leftHandItemState;
               if(item.isEmpty())continue;
               pose.pushPose();getParentModel().translateToHand(state,hand,pose);
               // Emrys's original sprite runs diagonally; stand it upright around its real grip.
               pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(135));pose.scale(1.9F,1.9F,1.9F);
               item.submit(pose,collector,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,state.outlineColor);pose.popPose();
            }
         }
      });
      addLayer(new net.minecraft.client.renderer.entity.layers.EyesLayer<State,ArchmageModel>(this){
         public net.minecraft.client.renderer.rendertype.RenderType renderType(){return net.minecraft.client.renderer.rendertype.RenderTypes.eyes(Identifier.fromNamespaceAndPath("mahoutsukai","textures/entity/illager_archmage_emissive.png"));}
      });
   }

   @Override
   public Identifier getTextureLocation(State state) {
      return TEXTURE;
   }

   @Override
   public State createRenderState() {
      return new State();
   }

   @Override
   public void extractRenderState(RealityMarbleBossEntity entity, State state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      ArmedEntityRenderState.extractArmedEntityRenderState(entity,state,items,partialTick);
      items.updateForLiving(state.rightHandItemState,state.rightHandItemStack,net.minecraft.world.item.ItemDisplayContext.NONE,entity);
      items.updateForLiving(state.leftHandItemState,state.leftHandItemStack,net.minecraft.world.item.ItemDisplayContext.NONE,entity);
      state.attackTicks = entity.attackTicks();
      state.attackKind = entity.attackKind();
      state.melee=entity.getAttackAnim(partialTick);
      state.levitating=entity.levitating();
      state.arrivalTicks=entity.arrivalTicks();
      state.recoveryTicks=entity.recoveryTicks();
      state.hurtTime=Math.max(0,entity.hurtTime-partialTick);
      state.healthRatio=entity.getHealth()/entity.getMaxHealth();
   }

   @Override
   protected void scale(State state, PoseStack pose) {
      // Geometry is already 48 model units (three blocks); no hidden model/hitbox scale.
   }
   @Override protected float getFlipDegrees(){return 0;}
}
