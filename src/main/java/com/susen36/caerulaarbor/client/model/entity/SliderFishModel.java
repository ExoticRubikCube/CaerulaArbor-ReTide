package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.SliderFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SliderFishModel extends GeoModel<SliderFishEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/slider.png");

	@Override
	public ResourceLocation getAnimationResource(SliderFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/slidingfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SliderFishEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/slidingfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SliderFishEntity entity) {
		return TEXTURE;
	}

}
