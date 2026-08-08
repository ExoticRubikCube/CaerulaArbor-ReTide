package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.FeederProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FeederProkaryoteModel extends GeoModel<FeederProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/feeder.png");

	@Override
	public ResourceLocation getAnimationResource(FeederProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/feeder.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FeederProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/feeder.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FeederProkaryoteEntity entity) {
		return TEXTURE;
	}

}