package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.AccumulatorProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AccumulatorProkaryoteModel extends GeoModel<AccumulatorProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/accumulator.png");

	@Override
	public ResourceLocation getAnimationResource(AccumulatorProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/accumulator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AccumulatorProkaryoteEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/accumulator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AccumulatorProkaryoteEntity entity) {
		return TEXTURE;
	}

}
