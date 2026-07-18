package com.apocalypse.caerulaarbor.client.model.entity;

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
	private static final ResourceLocation PHASE_0_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/skadi_corrupted_0.png");
	private static final ResourceLocation PHASE_1_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/skadi_corrupted_1.png");
	private static final ResourceLocation PHASE_2_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/skadi_corrupted.png");

	@Override
	public ResourceLocation getAnimationResource(SkadiCorruptedEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/skadi_corrupted.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SkadiCorruptedEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/skadi_corrupted.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SkadiCorruptedEntity entity) {
		return switch (entity.getPhase()) {
			case 1 -> PHASE_1_TEXTURE;
			case 2 -> PHASE_2_TEXTURE;
			default -> PHASE_0_TEXTURE;
		};
	}

	@Override
	public void setCustomAnimations(SkadiCorruptedEntity animatable, long instanceId, AnimationState<SkadiCorruptedEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
