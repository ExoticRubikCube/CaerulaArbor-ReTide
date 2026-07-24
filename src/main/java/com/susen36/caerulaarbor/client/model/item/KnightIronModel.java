package com.susen36.caerulaarbor.client.model.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.item.KnightIronItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KnightIronModel extends GeoModel<KnightIronItem> {
	@Override
	public ResourceLocation getAnimationResource(KnightIronItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/knight_armor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(KnightIronItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/knight_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(KnightIronItem object) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/item/knight_armor.png");
	}
}
