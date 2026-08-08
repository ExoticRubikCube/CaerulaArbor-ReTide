package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.TidutantExcrescenceEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TidutantExcrescenceModel extends GeoModel<TidutantExcrescenceEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/tidutant_excrescence.png");

	@Override
	public ResourceLocation getAnimationResource(TidutantExcrescenceEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/tidutant_excrescence.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TidutantExcrescenceEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/tidutant_excrescence.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TidutantExcrescenceEntity entity) {
		return TEXTURE;
	}

}