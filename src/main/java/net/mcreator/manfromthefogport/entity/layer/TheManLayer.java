package net.mcreator.manfromthefogport.entity.layer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import net.mcreator.manfromthefogport.entity.TheManEntity;

import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.geckolib.renderer.base.GeoRenderer;

public class TheManLayer extends AutoGlowingGeoLayer<TheManEntity, Void, LivingEntityRenderState> {
    private static final Identifier GLOW =
        Identifier.fromNamespaceAndPath("man_from_the_fog_port", "textures/entities/stalker_glow.png");

    public TheManLayer(GeoRenderer<TheManEntity, Void, LivingEntityRenderState> renderer) {
        super(renderer);
    }

    @Override
    protected Identifier getTextureResource(LivingEntityRenderState renderState) {
        return GLOW;
    }

    @Override
    protected RenderType getRenderType(LivingEntityRenderState renderState) {
        return RenderTypes.eyes(getTextureResource(renderState));
    }
}
