package stepsword.mahoutsukai.render.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import stepsword.mahoutsukai.render.RenderBaseItem;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;

/** Original special-model ID with native renderers for the connected items. */
public final class MahouSpecialItemModel {
    public static final Identifier TYPE_ID = Identifier.fromNamespaceAndPath("mahoutsukai", "magical_item");
    public record Unbaked(String renderer) implements SpecialModelRenderer.Unbaked<MagicalItemRenderState> {
        public static final MapCodec<Unbaked> MAP_CODEC = Codec.STRING.fieldOf("renderer").xmap(Unbaked::new, Unbaked::renderer);
        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<MagicalItemRenderState>> type() { return MAP_CODEC; }
        @Override
        public SpecialModelRenderer<MagicalItemRenderState> bake(SpecialModelRenderer.BakingContext context) {
            if (renderer.equals("fog_projector")) return new FogProjectorItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),context.entityModelSet());
            if (renderer.equals("mahoujin_projector")) return new MahoujinProjectorItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),context.entityModelSet());
            if (renderer.equals("staff_emrys")) return new StaffEmrysRenderer();
            if (renderer.equals("mystic_code")) return new MysticCodeRenderer();
            if (renderer.equals("mystic_code_1")) return new MysticCodeRenderer(RenderBaseItem.mysticCode1);
            if (renderer.equals("mystic_code_2")) return new MysticCodeRenderer(RenderBaseItem.mysticCode2);
            if (renderer.equals("mystic_code_3")) return new MysticCodeRenderer(RenderBaseItem.mysticCode3);
            if (renderer.equals("mystic_code_ister")) return new MysticCodeRenderer(RenderBaseItem.mysticCodeIster);
            if (renderer.equals("mystic_code_first_sorcery")) return new MysticCodeFirstSorceryRenderer();
            if (renderer.equals("mystic_code_first_sorcery_1")) return new MysticCodeFirstSorceryRenderer(RenderBaseItem.mysticCodeFirstSorcery1);
            if (renderer.equals("mystic_code_first_sorcery_2")) return new MysticCodeFirstSorceryRenderer(RenderBaseItem.mysticCodeFirstSorcery2);
            if (renderer.equals("mystic_code_first_sorcery_3")) return new MysticCodeFirstSorceryRenderer(RenderBaseItem.mysticCodeFirstSorcery3);
            if (renderer.equals("mystic_code_first_sorcery_ister")) return new MysticCodeFirstSorceryRenderer(RenderBaseItem.mysticCodeFirstSorceryIster);
            if (renderer.equals("replica")) return new ReplicaRenderer();
            if (renderer.equals("replica_blocking")) return new ReplicaRenderer(RenderBaseItem.replicaBlocking);
            if (renderer.equals("nobu")) return new NobuRenderer();
            if (renderer.equals("nobu_ground")) return new NobuRenderer(-0.25, 1.2, 0.5, 90.0F, 45.0F, 0.0F, 1.0F);
            if (renderer.equals("nobu_tp_idle")) return new NobuRenderer(0.5, 1.5, 0.5, 0.0F, -90.0F, 90.0F, 1.0F);
            if (renderer.equals("nobu_tp_use")) return new NobuRenderer(0.5, 0.5, -0.7, 0.0F, 0.0F, 90.0F, 1.0F);
            if (renderer.equals("nobu_fp_r_idle")) return new NobuRenderer(0.3, 0.8, -1.0, 0.0F, 0.0F, 90.0F, 1.8F);
            if (renderer.equals("nobu_fp_r_use")) return new NobuRenderer(0.5, 0.4, -0.4, 0.0F, -7.0F, 90.0F, 1.8F);
            if (renderer.equals("nobu_fp_l_idle")) return new NobuRenderer(0.7, 0.8, -1.0, 0.0F, 0.0F, 90.0F, 1.8F);
            if (renderer.equals("nobu_fp_l_use")) return new NobuRenderer(0.5, 0.4, -0.4, 0.0F, -7.0F, 90.0F, 1.8F);
            if (renderer.equals("william")) return new WilliamRenderer();
            if (renderer.equals("william_gui")) return new WilliamRenderer(0.25, 0.2, 0.5, 30.0F, 60.0F, 0.0F, 0.6F);
            if (renderer.equals("william_ground")) return new WilliamRenderer(0.5, 0.85, 0.7, 180.0F, 0.0F, 0.0F, 0.4F);
            if (renderer.equals("william_tp")) return new WilliamRenderer(0.5, 0.65, 0.5, 180.0F, 0.0F, 0.0F, 0.4F);
            if (renderer.equals("william_fp_right")) return new WilliamRenderer(0.4, 0.85, 0.5, 180.0F, 90.0F, 0.0F, 0.7F);
            if (renderer.equals("william_fp_left")) return new WilliamRenderer(0.2, 0.85, 0.5, 180.0F, 90.0F, 0.0F, 0.7F);
            if (renderer.equals("mahoujin")) return new MahoujinItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), context.entityModelSet());
            if (renderer.equals("clarent") || renderer.equals("clarent_blocking")) return new ClarentRenderer();
            if (renderer.equals("proximity_projection_keys")) return new ProximityProjectionKeysRenderer();
            if (renderer.equals("mystic_staff")) return new MysticStaffRenderer();
            if (renderer.equals("spatial_disorientation_staff")) return new SpatialStaffRenderer();
            if (renderer.equals("caliburn")) return new CaliburnRenderer();
            if (renderer.equals("morgan")) return new MorganRenderer();
            if (renderer.equals("rhongomyniad")) return new RhongomyniadRenderer();
            if (renderer.equals("treasury_projection_gauntlet")) return new TreasuryProjectionGauntletRenderer();
            if (renderer.equals("weapon_projectile_bow")) return new WeaponProjectileBowRenderer();
            if (renderer.equals("rule_breaker")) return new RuleBreakerRenderer();
            if (!renderer.equals("mana_circuit") && !renderer.equals("mana_circuit_magitech"))
                throw new IllegalArgumentException("Renderer not yet ported: " + renderer);
            return new ManaCircuitItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), context.entityModelSet());
        }
    }
}
