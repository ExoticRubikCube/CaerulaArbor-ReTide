package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.SkimmingSeaDrifterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SkimmingSeaDrifterModel extends GeoModel<SkimmingSeaDrifterEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/skimming_sea_drifter.png");

	@Override
	public ResourceLocation getAnimationResource(SkimmingSeaDrifterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/skimming_sea_drifter.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SkimmingSeaDrifterEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/skimming_sea_drifter.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SkimmingSeaDrifterEntity entity) {
		return TEXTURE;
	}

}
