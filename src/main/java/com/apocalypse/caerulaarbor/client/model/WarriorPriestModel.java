package com.apocalypse.caerulaarbor.client.model;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.entity.WarriorPriestEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WarriorPriestModel extends GeoModel<WarriorPriestEntity> {
	@Override
	public ResourceLocation getAnimationResource(WarriorPriestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/warriorpriest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WarriorPriestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/warriorpriest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WarriorPriestEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(WarriorPriestEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
