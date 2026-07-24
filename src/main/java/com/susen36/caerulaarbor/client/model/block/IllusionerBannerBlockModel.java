package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.blockentity.IllusionerBannerTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IllusionerBannerBlockModel extends GeoModel<IllusionerBannerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(IllusionerBannerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_banner.animation.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_banner.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IllusionerBannerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_banner.geo.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_banner.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IllusionerBannerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/illusioner_banner.png");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/illusioner_banner.png");
	}
}
