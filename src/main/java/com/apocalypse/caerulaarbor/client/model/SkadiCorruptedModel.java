package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.SkadiCorruptedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SkadiCorruptedModel extends GeoModel<SkadiCorruptedEntity> {
	@Override
	public ResourceLocation getAnimationResource(SkadiCorruptedEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/skadi_corrupted.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SkadiCorruptedEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/skadi_corrupted.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SkadiCorruptedEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(SkadiCorruptedEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
