package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.SaintCarmenEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SaintCarmenModel extends GeoModel<SaintCarmenEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/saint_carmen.png");

	@Override
	public ResourceLocation getAnimationResource(SaintCarmenEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/saint_carmen.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SaintCarmenEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/saint_carmen.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SaintCarmenEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(SaintCarmenEntity animatable, long instanceId, AnimationState<SaintCarmenEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
