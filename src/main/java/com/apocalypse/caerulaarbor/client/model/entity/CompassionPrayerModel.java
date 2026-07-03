package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.CompassionPrayerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CompassionPrayerModel extends GeoModel<CompassionPrayerEntity> {
	private static final ResourceLocation PHASE_0_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/compassion_prayer.png");
	private static final ResourceLocation PHASE_1_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/compassion_prayer_a.png");

	@Override
	public ResourceLocation getAnimationResource(CompassionPrayerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/compassion_prayer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CompassionPrayerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/compassion_prayer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CompassionPrayerEntity entity) {
		return entity.getEntityData().get(CompassionPrayerEntity.DATA_PHASE) == 0 ? PHASE_0_TEXTURE : PHASE_1_TEXTURE;
	}

	@Override
	public void setCustomAnimations(CompassionPrayerEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
