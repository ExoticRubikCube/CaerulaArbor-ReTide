package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.SwarmcallerDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwarmcallerDollBlockModel extends GeoModel<SwarmcallerDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(SwarmcallerDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/umbrella_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SwarmcallerDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/umbrella_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SwarmcallerDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/umbrella.png");
	}
}