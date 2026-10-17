package net.mcreator.manfromthefogport.entity.model;

import net.minecraft.resources.Identifier;

import net.mcreator.manfromthefogport.entity.ManAgressiveEntity;

import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.cache.GeckoLibResources;

public class ManAgressiveModel extends DefaultedEntityGeoModel<ManAgressiveEntity> {
	public static final DataTicket<String> TEXTURE = DataTicket.create("man_from_the_fog_port_man_agressive_texture", String.class);
	/** Flat layout fallback when assets were imported without entity/ subfolders. */
	private static final Identifier MODEL_FLAT = Identifier.fromNamespaceAndPath("man_from_the_fog_port", "the_stalker_from_the_fog");
	private static final Identifier ANIM_FLAT = Identifier.fromNamespaceAndPath("man_from_the_fog_port", "the_stalker_from_the_fog");

	public ManAgressiveModel() {
		// DefaultedEntityGeoModel prefixes model/anim keys with entity/
		// matching: geckolib/models/entity/<path> and geckolib/animations/entity/<path>
		super(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "the_stalker_from_the_fog"));
	}

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		Identifier preferred = super.getModelResource(renderState);
		if (GeckoLibResources.getBakedModels().cache().containsKey(preferred))
			return preferred;
		return MODEL_FLAT;
	}

	@Override
	public Identifier getAnimationResource(ManAgressiveEntity animatable) {
		Identifier preferred = super.getAnimationResource(animatable);
		if (GeckoLibResources.getBakedAnimations().cache().containsKey(preferred))
			return preferred;
		return ANIM_FLAT;
	}

	@Override
	public Identifier[] getAnimationResourceFallbacks(ManAgressiveEntity animatable) {
		return new Identifier[]{ANIM_FLAT};
	}

	/**
	 * MCreator stores entity textures under textures/entities/ (plural),
	 * not GeckoLib DefaultedEntityGeoModel's textures/entity/.
	 */
	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		String texture = renderState.getOrDefaultGeckolibData(TEXTURE, "stalker");
		return Identifier.fromNamespaceAndPath("man_from_the_fog_port", "textures/entities/" + texture + ".png");
	}

	@Override
	public void addAdditionalStateData(ManAgressiveEntity animatable, Object relatedObject, GeoRenderState renderState) {
		renderState.addGeckolibData(TEXTURE, animatable.getTexture());
	}
}