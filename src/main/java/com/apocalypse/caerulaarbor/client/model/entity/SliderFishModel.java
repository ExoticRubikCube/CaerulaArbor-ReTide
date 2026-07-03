package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.SliderFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SliderFishModel extends GeoModel<SliderFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/slider.png");

	@Override
	public ResourceLocation getAnimationResource(SliderFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/slidingfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SliderFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/slidingfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SliderFishEntity entity) {
		return TEXTURE;
	}

}
