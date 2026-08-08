package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.CaerulaOffspringEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CaerulaOffspringModel extends GeoModel<CaerulaOffspringEntity> {

	@Override
	public ResourceLocation getAnimationResource(CaerulaOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/izumik_offspring.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CaerulaOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/izumik_offspring.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CaerulaOffspringEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}