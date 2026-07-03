package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.BoneFishEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BoneFishModel extends GeoModel<BoneFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/bonefish.png");

	@Override
	public ResourceLocation getAnimationResource(BoneFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/bonefish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BoneFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/bonefish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BoneFishEntity entity) {
		return TEXTURE;
	}

}
