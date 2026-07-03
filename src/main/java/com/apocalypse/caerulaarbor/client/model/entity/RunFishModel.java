package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.RunFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunFishModel extends GeoModel<RunFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/shell_sea_runner.png");

	@Override
	public ResourceLocation getAnimationResource(RunFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/shell_sea_runner.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RunFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/shell_sea_runner.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RunFishEntity entity) {
		return TEXTURE;
	}

}
