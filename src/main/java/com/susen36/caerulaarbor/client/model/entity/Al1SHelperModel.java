package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.helper.Al1SHelperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Al1SHelperModel extends GeoModel<Al1SHelperEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/little_helper_al1s.png");

	@Override
	public ResourceLocation getAnimationResource(Al1SHelperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/little_helper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(Al1SHelperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/little_helper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Al1SHelperEntity entity) {
		return TEXTURE;
	}

}