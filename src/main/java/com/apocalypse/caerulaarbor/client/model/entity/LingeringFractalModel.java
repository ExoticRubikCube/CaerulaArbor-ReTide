package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.routeshaper.LingeringFractalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LingeringFractalModel extends GeoModel<LingeringFractalEntity> {
	@Override
	public ResourceLocation getAnimationResource(LingeringFractalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/routeshaper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LingeringFractalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/routeshaper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LingeringFractalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/lingering_fractal.png");
	}

}

