package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.block.blockentity.ChestmegaSpawnerTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChestmegaSpawnerBlockModel extends GeoModel<ChestmegaSpawnerTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(ChestmegaSpawnerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chestmega_block.animation.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/chestmega_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ChestmegaSpawnerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chestmega_block.geo.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/chestmega_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ChestmegaSpawnerTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/megachest.png");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/megachest.png");
	}
}
