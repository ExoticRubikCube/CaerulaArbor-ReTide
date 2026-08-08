package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.JuniorWarriorPriestEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class JuniorWarriorPriestModel extends GeoModel<JuniorWarriorPriestEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/warriorpriest_junior.png");

	@Override
	public ResourceLocation getAnimationResource(JuniorWarriorPriestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/warriorpriest.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(JuniorWarriorPriestEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/warriorpriest.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(JuniorWarriorPriestEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(JuniorWarriorPriestEntity animatable, long instanceId, AnimationState<JuniorWarriorPriestEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}