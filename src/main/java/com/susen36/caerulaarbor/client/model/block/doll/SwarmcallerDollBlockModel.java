package com.susen36.caerulaarbor.client.model.block.doll;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.doll.SwarmcallerDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwarmcallerDollBlockModel extends GeoModel<SwarmcallerDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(SwarmcallerDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/swarmcaller_doll.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SwarmcallerDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/swarmcaller_doll.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SwarmcallerDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/swarmcaller_doll.png");
	}
}