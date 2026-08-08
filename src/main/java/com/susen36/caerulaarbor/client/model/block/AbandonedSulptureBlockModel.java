package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.AbandonedSulptureTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbandonedSulptureBlockModel extends GeoModel<AbandonedSulptureTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(AbandonedSulptureTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/abandoned_sulpture.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbandonedSulptureTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/abandoned_sulpture.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbandonedSulptureTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/theabandoned_sculpture.png");
	}
}