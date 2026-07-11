package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.FeederProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FeederProkaryoteModel extends GeoModel<FeederProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/feeder.png");

	@Override
	public ResourceLocation getAnimationResource(FeederProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/feeder.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FeederProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/feeder.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FeederProkaryoteEntity entity) {
		return TEXTURE;
	}

}
