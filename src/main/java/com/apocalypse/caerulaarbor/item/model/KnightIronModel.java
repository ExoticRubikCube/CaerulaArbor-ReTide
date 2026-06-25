package com.apocalypse.caerulaarbor.item.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.item.KnightIronItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KnightIronModel extends GeoModel<KnightIronItem> {
	@Override
	public ResourceLocation getAnimationResource(KnightIronItem object) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/knight_armor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(KnightIronItem object) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/knight_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(KnightIronItem object) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/item/knight_armor.png");
	}
}
