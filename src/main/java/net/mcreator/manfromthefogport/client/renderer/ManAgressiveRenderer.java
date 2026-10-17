package net.mcreator.manfromthefogport.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.manfromthefogport.entity.model.ManAgressiveModel;
import net.mcreator.manfromthefogport.entity.layer.ManAgressiveLayer;
import net.mcreator.manfromthefogport.entity.ManAgressiveEntity;

import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.constant.dataticket.DataTicket;

public class ManAgressiveRenderer extends GeoEntityRenderer<ManAgressiveEntity, LivingEntityRenderState> {
	public static final DataTicket<Float> VISUAL_SCALE = DataTicket.create("man_from_the_fog_port_man_agressive_visual_scale", Float.class);

	public ManAgressiveRenderer(EntityRendererProvider.Context context) {
		super(context, new ManAgressiveModel());
		this.shadowRadius = 0.5f;
		this.withRenderLayer(new ManAgressiveLayer(this));
	}

	@Override
	public RenderType getRenderType(LivingEntityRenderState renderState, Identifier texture) {
		if (!renderState.isInvisible)
			return RenderTypes.entityTranslucent(texture);
		return super.getRenderType(renderState, texture);
	}

	@Override
	public void adjustModelBonesForRender(RenderPassInfo<LivingEntityRenderState> renderPassInfo, BoneSnapshots snapshots) {
		// Hide Blockbench utility bones (same as before)
		snapshots.ifPresent("hitbox", snapshot -> snapshot.skipRender(true).skipChildrenRender(true));
		snapshots.ifPresent("tag_name", snapshot -> snapshot.skipRender(true).skipChildrenRender(true));
		// The head rotation is now done with this render helper to target the bone
	}
}