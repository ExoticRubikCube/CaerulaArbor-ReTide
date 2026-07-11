package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.AccumulatorCloneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AccumulatorCloneModel extends GeoModel<AccumulatorCloneEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/accumulator.png");

	@Override
	public ResourceLocation getAnimationResource(AccumulatorCloneEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/accumulator.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AccumulatorCloneEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/accumulator.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AccumulatorCloneEntity entity) {
		return TEXTURE;
	}

}
