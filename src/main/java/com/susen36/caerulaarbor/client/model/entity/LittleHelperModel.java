package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.helper.LittleHelperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LittleHelperModel extends GeoModel<LittleHelperEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/little_helper.png");

	@Override
	public ResourceLocation getAnimationResource(LittleHelperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/little_helper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LittleHelperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/little_helper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LittleHelperEntity entity) {
		return TEXTURE;
	}

}