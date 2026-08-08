package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.PocketSeaDollTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PocketSeaDollBlockModel extends GeoModel<PocketSeaDollTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(PocketSeaDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/pocket_sea_creeper_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/pocket_sea_creeper_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaDollTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/pocket_sea_creeper.png");
	}
}