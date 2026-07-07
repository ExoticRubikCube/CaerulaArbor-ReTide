package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.block.blockentity.CrisisTableTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrisisTableBlockModel extends GeoModel<CrisisTableTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(CrisisTableTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "animations/crisis_table.animation.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/crisis_table.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CrisisTableTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "geo/crisis_table.geo.json");
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/crisis_table.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CrisisTableTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/crisis_table_dim.png");
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/crisis_table.png");
	}
}
