package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ReaperFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ReaperFishModel extends GeoModel<ReaperFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/reaperfish.png");

	@Override
	public ResourceLocation getAnimationResource(ReaperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/reaperfish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ReaperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/reaperfish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ReaperFishEntity entity) {
		return TEXTURE;
	}

}
