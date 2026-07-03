package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.CollectorProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CollectorProkaryoteModel extends GeoModel<CollectorProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/collector.png");

	@Override
	public ResourceLocation getAnimationResource(CollectorProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/collector.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CollectorProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/collector.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CollectorProkaryoteEntity entity) {
		return TEXTURE;
	}

}
