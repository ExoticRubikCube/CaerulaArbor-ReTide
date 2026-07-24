package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.blockentity.CentrifugerTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CentrifugerBlockModel extends GeoModel<CentrifugerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(CentrifugerTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/centrifuger.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CentrifugerTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/centrifuger.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CentrifugerTileEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/centrifuger.png");
	}
}
