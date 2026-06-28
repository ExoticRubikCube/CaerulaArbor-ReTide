package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.MoistEnderCrystalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MoistEnderCrystalModel extends GeoModel<MoistEnderCrystalEntity> {
	@Override
	public ResourceLocation getAnimationResource(MoistEnderCrystalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/moist_crystal.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MoistEnderCrystalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/moist_crystal.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MoistEnderCrystalEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

}
