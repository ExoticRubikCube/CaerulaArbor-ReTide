package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.TidutantExcrescenceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TidutantExcrescenceModel extends GeoModel<TidutantExcrescenceEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/tidutant_excrescence.png");

	@Override
	public ResourceLocation getAnimationResource(TidutantExcrescenceEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/tidutant_excrescence.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TidutantExcrescenceEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/tidutant_excrescence.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TidutantExcrescenceEntity entity) {
		return TEXTURE;
	}

}
