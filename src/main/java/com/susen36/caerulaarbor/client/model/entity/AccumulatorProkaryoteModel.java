package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.AccumulatorProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AccumulatorProkaryoteModel extends GeoModel<AccumulatorProkaryoteEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/accumulator.png");
	private static final ResourceLocation MODEL_DEFAULT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/accumulator.geo.json");
	private static final ResourceLocation MODEL_PET = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/accumulator_pet.geo.json");
	private static final ResourceLocation ANIM_DEFAULT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/accumulator.animation.json");
	private static final ResourceLocation ANIM_PET = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/accumulator_pet.animation.json");

	@Override
	public ResourceLocation getAnimationResource(AccumulatorProkaryoteEntity entity) {
		if (entity.isDivicellularVariant()) {
			return ANIM_PET;
		}
		return ANIM_DEFAULT;
	}

	@Override
	public ResourceLocation getModelResource(AccumulatorProkaryoteEntity entity) {
		if (entity.isDivicellularVariant()) {
			return MODEL_PET;
		}
		return MODEL_DEFAULT;
	}

	@Override
	public ResourceLocation getTextureResource(AccumulatorProkaryoteEntity entity) {
		return TEXTURE;
	}

}