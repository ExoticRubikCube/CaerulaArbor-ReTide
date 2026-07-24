package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.SpikeChestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpikeChestModel extends GeoModel<SpikeChestEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/chest_spike.png");

	@Override
	public ResourceLocation getAnimationResource(SpikeChestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/spike_chest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SpikeChestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/spike_chest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpikeChestEntity entity) {
		return TEXTURE;
	}

}
