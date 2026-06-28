package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.MegaChestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MegaChestModel extends GeoModel<MegaChestEntity> {
	@Override
	public ResourceLocation getAnimationResource(MegaChestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/chestmega.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MegaChestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/chestmega.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MegaChestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
