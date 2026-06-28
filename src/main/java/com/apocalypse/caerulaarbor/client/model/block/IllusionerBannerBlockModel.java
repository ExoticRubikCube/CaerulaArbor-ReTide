package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.entity.IllusionerBannerTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IllusionerBannerBlockModel extends GeoModel<IllusionerBannerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(IllusionerBannerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_banner.animation.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/oceanized_banner.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IllusionerBannerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_banner.geo.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/oceanized_banner.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IllusionerBannerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/illusioner_banner.png");
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/illusioner_banner.png");
	}
}
