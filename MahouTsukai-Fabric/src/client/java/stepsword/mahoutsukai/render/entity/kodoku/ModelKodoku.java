package stepsword.mahoutsukai.render.entity.kodoku;

import java.util.Arrays;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ModelKodoku extends EntityModel<LivingEntityRenderState> {
   public static ModelLayerLocation KODOKU_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath("mahoutsukai", "kodoku"), "main");
   private final ModelPart[] wormparts = new ModelPart[5];
   private static final int[][] WORM_BOX_LENGTH = new int[][]{{2, 2, 1}, {3, 3, 2}, {4, 4, 3}, {3, 3, 1}, {2, 2, 1}};
   private static final int[][] WORM_TEXTURE_POSITIONS = new int[][]{{0, 0}, {4, 4}, {9, 9}, {16, 16}, {13, 13}};

   public ModelKodoku(ModelPart r) {
      super(r);
      Arrays.setAll(this.wormparts, z -> r.getChild(getSegmentName(z)));
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      float[] afloat = new float[7];
      float f = -3.5F;

      for (int i = 0; i < 5; i++) {
         partdefinition.addOrReplaceChild(
            getSegmentName(i),
            CubeListBuilder.create()
               .texOffs(WORM_TEXTURE_POSITIONS[i][0], WORM_TEXTURE_POSITIONS[i][1])
               .addBox(WORM_BOX_LENGTH[i][0] * -0.4F, 0.0F, WORM_BOX_LENGTH[i][2] * -0.4F, WORM_BOX_LENGTH[i][0], WORM_BOX_LENGTH[i][1], WORM_BOX_LENGTH[i][2]),
            PartPose.offset(0.0F, 24 - WORM_BOX_LENGTH[i][1], f)
         );
         afloat[i] = f;
         if (i < 4) {
            f += (WORM_BOX_LENGTH[i][2] + WORM_BOX_LENGTH[i + 1][2]) * 0.5F;
         }
      }

      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   private static String getSegmentName(int b) {
      return "segment" + b;
   }

   public void setupAnim(LivingEntityRenderState state) {
      super.setupAnim(state);

      for (int i = 0; i < this.wormparts.length; i++) {
         this.wormparts[i].yRot = Mth.cos(state.ageInTicks * 0.4F + i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (1 + Math.abs(i - 2));
         this.wormparts[i].x = Mth.sin(state.ageInTicks * 0.4F + i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * Math.abs(i - 2);
      }
   }
}
