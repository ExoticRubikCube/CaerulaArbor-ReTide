package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.blockentity.HighmoreSpawnblockTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighmoreSpawnblockBlockModel extends GeoModel<HighmoreSpawnblockTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(HighmoreSpawnblockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/highmore_spawnblock.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreSpawnblockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/highmore_spawnblock.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreSpawnblockTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/block/highmore_spawnblock.png");
	}
}
