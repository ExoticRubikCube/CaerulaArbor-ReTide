package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.CreeperFishEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CreeperFishModel extends GeoModel<CreeperFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/creeperfish.png");

	@Override
	public ResourceLocation getAnimationResource(CreeperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/explosivefish.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CreeperFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/explosivefish.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CreeperFishEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(CreeperFishEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
