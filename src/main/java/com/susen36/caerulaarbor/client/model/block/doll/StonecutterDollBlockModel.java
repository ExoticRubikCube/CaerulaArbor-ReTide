package com.susen36.caerulaarbor.client.model.block.doll;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.doll.StonecutterDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StonecutterDollBlockModel extends GeoModel<StonecutterDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(StonecutterDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/stonecutter_doll.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(StonecutterDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/stonecutter_doll.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(StonecutterDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/stonecutter_doll.png");
	}
}