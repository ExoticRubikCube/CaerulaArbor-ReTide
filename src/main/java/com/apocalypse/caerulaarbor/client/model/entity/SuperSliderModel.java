package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.SuperSliderEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SuperSliderModel extends GeoModel<SuperSliderEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/super_slider.png");

	@Override
	public ResourceLocation getAnimationResource(SuperSliderEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/slidingfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SuperSliderEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/slidingfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SuperSliderEntity entity) {
		return TEXTURE;
	}

}
