package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.GladiiaWhirlEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GladiiaWhirlModel extends GeoModel<GladiiaWhirlEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/gladiia_whirl.png");

	@Override
	public ResourceLocation getAnimationResource(GladiiaWhirlEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/gladiia_whirl.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GladiiaWhirlEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/gladiia_whirl.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GladiiaWhirlEntity entity) {
		return TEXTURE;
	}

}
