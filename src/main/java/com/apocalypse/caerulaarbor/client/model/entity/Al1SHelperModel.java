package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.helper.Al1SHelperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Al1SHelperModel extends GeoModel<Al1SHelperEntity> {
	@Override
	public ResourceLocation getAnimationResource(Al1SHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/little_helper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(Al1SHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/little_helper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Al1SHelperEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
