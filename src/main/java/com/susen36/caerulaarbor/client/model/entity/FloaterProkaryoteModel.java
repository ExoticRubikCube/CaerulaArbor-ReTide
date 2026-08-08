package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FloaterProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloaterProkaryoteModel extends GeoModel<FloaterProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/floater.png");

	@Override
	public ResourceLocation getAnimationResource(FloaterProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/floater.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FloaterProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/floater.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FloaterProkaryoteEntity entity) {
		return TEXTURE;
	}

}