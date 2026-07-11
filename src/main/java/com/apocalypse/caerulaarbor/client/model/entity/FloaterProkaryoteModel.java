package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.FloaterProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloaterProkaryoteModel extends GeoModel<FloaterProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/floater.png");

	@Override
	public ResourceLocation getAnimationResource(FloaterProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/floater.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FloaterProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/floater.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FloaterProkaryoteEntity entity) {
		return TEXTURE;
	}

}
