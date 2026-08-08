package com.susen36.caerulaarbor.client.model.block;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.blockentity.CrisisTableTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrisisTableBlockModel extends GeoModel<CrisisTableTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(CrisisTableTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/crisis_table.animation.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/crisis_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CrisisTableTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/crisis_table.geo.json");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/crisis_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CrisisTableTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/crisis_table_dim.png");
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/block/crisis_table.png");
	}
}