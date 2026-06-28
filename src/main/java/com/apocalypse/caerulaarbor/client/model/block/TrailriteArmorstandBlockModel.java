package com.apocalypse.caerulaarbor.client.model.block;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import com.apocalypse.caerulaarbor.block.entity.TrailriteArmorstandTileEntity;

public class TrailriteArmorstandBlockModel extends GeoModel<TrailriteArmorstandTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(TrailriteArmorstandTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/trairite_armorstand.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TrailriteArmorstandTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/trairite_armorstand.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TrailriteArmorstandTileEntity animatable) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/block/trailrte_armorstand.png");
	}
}
