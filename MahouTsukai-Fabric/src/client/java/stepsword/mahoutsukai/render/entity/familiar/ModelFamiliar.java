package stepsword.mahoutsukai.render.entity.familiar;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ModelFamiliar extends EntityModel<FamiliarRenderState> {
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart wingLeft;
   private final ModelPart wingRight;
   private final ModelPart head;
   private final ModelPart legLeft;
   private final ModelPart legRight;

   public ModelFamiliar() {
      this(createBodyLayer().bakeRoot());
   }

   public ModelFamiliar(ModelPart root) {
      super(root);
      this.body = root.getChild("body");
      this.tail = root.getChild("tail");
      this.wingLeft = root.getChild("left_wing");
      this.wingRight = root.getChild("right_wing");
      this.head = root.getChild("head");
      this.legLeft = root.getChild("left_leg");
      this.legRight = root.getChild("right_leg");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 2.0F, -1.5F, 3.0F, 4.0F, 3.0F),
         PartPose.offsetAndRotation(0.0F, 16.5F, -3.0F, 0.4937F, 0.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F),
         PartPose.offsetAndRotation(0.0F, 21.07F, 1.16F, 1.015F, 0.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 1.8F, -1.5F, 1.0F, 5.0F, 3.0F),
         PartPose.offsetAndRotation(1.5F, 16.94F, -3.26F, (float) -Math.PI * 2.0F / 9.0F, (float) -Math.PI, 0.0F)
      );
      root.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 1.8F, -1.5F, 1.0F, 5.0F, 3.0F),
         PartPose.offsetAndRotation(-1.5F, 16.94F, -3.26F, (float) -Math.PI * 2.0F / 9.0F, (float) -Math.PI, 0.0F)
      );
      PartDefinition head = root.addOrReplaceChild(
         "head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 3.0F, 2.0F), PartPose.offset(0.0F, 15.69F, -2.76F)
      );
      head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.2F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, -0.5F, -1.5F));
      root.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F),
         PartPose.offsetAndRotation(1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F),
         PartPose.offsetAndRotation(-1.0F, 22.0F, -1.05F, -0.0299F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 64, 64);
   }

   public void setupAnim(FamiliarRenderState state) {
      super.setupAnim(state);
      this.prepare(state.familiarPose);
      this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
      this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
      this.head.zRot = 0.0F;
      this.head.x = 0.0F;
      this.body.x = 0.0F;
      this.tail.x = 0.0F;
      this.wingRight.x = -1.3F;
      this.wingLeft.x = 1.3F;
      if (state.familiarPose == ModelFamiliar.State.STANDING) {
         this.legLeft.xRot = this.legLeft.xRot + Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
         this.legRight.xRot = this.legRight.xRot + Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;
      }

      float bob = state.flapAngle * 0.3F;
      this.head.y += bob;
      this.tail.xRot = this.tail.xRot + Mth.cos(state.walkAnimationPos * 0.6662F) * 0.3F * state.walkAnimationSpeed;
      this.tail.y += bob;
      this.body.y += bob;
      this.wingLeft.zRot = -0.0873F - state.flapAngle;
      this.wingLeft.y += bob;
      this.wingRight.zRot = 0.0873F + state.flapAngle;
      this.wingRight.y += bob;
      this.legLeft.y += bob;
      this.legRight.y += bob;
   }

   private void prepare(ModelFamiliar.State state) {
      if (state == ModelFamiliar.State.FLYING) {
         this.legLeft.xRot += (float) Math.PI * 2.0F / 9.0F;
         this.legRight.xRot += (float) Math.PI * 2.0F / 9.0F;
      }
   }

   public enum State {
      FLYING,
      STANDING,
      SITTING;
   }
}
