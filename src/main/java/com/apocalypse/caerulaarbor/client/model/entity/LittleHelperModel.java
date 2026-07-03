package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.helper.LittleHelperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LittleHelperModel extends GeoModel<LittleHelperEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/little_helper.png");

	@Override
	public ResourceLocation getAnimationResource(LittleHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/little_helper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(LittleHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/little_helper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LittleHelperEntity entity) {
		return TEXTURE;
	}

}
