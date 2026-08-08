package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.MizukiStatueTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MizukiStatueBlockModel extends GeoModel<MizukiStatueTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(MizukiStatueTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/mizuki.animation.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/mizuki.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MizukiStatueTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/mizuki.geo.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/mizuki.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MizukiStatueTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/mizuki_dim.png");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/mizuki.png");
	}
}