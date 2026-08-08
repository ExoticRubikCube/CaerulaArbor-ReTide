package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.NetherseaSlimeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NetherseaSlimeModel extends GeoModel<NetherseaSlimeEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/nethersea_slime.png");

	@Override
	public ResourceLocation getAnimationResource(NetherseaSlimeEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/nethersea_slime.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(NetherseaSlimeEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/nethersea_slime.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(NetherseaSlimeEntity entity) {
		return TEXTURE;
	}

}