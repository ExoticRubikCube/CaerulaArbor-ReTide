package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.NetherseaSlimeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NetherseaSlimeModel extends GeoModel<NetherseaSlimeEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/nethersea_slime.png");

	@Override
	public ResourceLocation getAnimationResource(NetherseaSlimeEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/nethersea_slime.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(NetherseaSlimeEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/nethersea_slime.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(NetherseaSlimeEntity entity) {
		return TEXTURE;
	}

}
