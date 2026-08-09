package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.TheLastKnightEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TheLastKnightModel extends GeoModel<TheLastKnightEntity> {
	private static final ResourceLocation TEXTURE_KNIGHT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/last_knight.png");
	private static final ResourceLocation TEXTURE_HORSE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/knight_amd_horse.png");
	private static final ResourceLocation GEO_KNIGHT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/last_knight.geo.json");
	private static final ResourceLocation GEO_HORSE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/last_knight_horse.geo.json");
	private static final ResourceLocation ANIM_KNIGHT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/last_knight.animation.json");
	private static final ResourceLocation ANIM_HORSE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/last_knight_horse.animation.json");

	@Override
	public ResourceLocation getAnimationResource(TheLastKnightEntity entity) {
		return entity.getPhase() == 1 ? ANIM_HORSE : ANIM_KNIGHT;
	}

	@Override
	public ResourceLocation getModelResource(TheLastKnightEntity entity) {
		return entity.getPhase() == 1 ? GEO_HORSE : GEO_KNIGHT;
	}

	@Override
	public ResourceLocation getTextureResource(TheLastKnightEntity entity) {
		return entity.getPhase() == 1 ? TEXTURE_HORSE : TEXTURE_KNIGHT;
	}

	@Override
	public void setCustomAnimations(TheLastKnightEntity animatable, long instanceId, AnimationState<TheLastKnightEntity> animationState) {
		if (animatable.getPhase() == 1) {
			GeoBone head = getAnimationProcessor().getBone("Head2");
			if (head != null) {
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
				head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
			}
		} else {
			GeoBone head = getAnimationProcessor().getBone("Head");
			if (head != null) {
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
				head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
			}
		}
	}
}
