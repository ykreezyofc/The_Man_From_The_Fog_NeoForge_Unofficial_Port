package net.mcreator.manfromthefogport.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.manfromthefogport.entity.model.TheManModel;
import net.mcreator.manfromthefogport.entity.layer.TheManLayer;
import net.mcreator.manfromthefogport.entity.TheManEntity;

import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.constant.dataticket.DataTicket;

public class TheManRenderer extends GeoEntityRenderer<TheManEntity, LivingEntityRenderState> {
	public static final DataTicket<Float> VISUAL_SCALE = DataTicket.create("man_from_the_fog_port_the_man_visual_scale", Float.class);

	public TheManRenderer(EntityRendererProvider.Context context) {
		super(context, new TheManModel());
		this.shadowRadius = 0.5f;
		this.withRenderLayer(new TheManLayer(this));
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