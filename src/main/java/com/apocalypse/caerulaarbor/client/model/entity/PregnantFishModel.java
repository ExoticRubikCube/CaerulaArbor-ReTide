package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.PregnantFishEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PregnantFishModel extends GeoModel<PregnantFishEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/pergnantfish.png");

	@Override
	public ResourceLocation getAnimationResource(PregnantFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/pregnant.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PregnantFishEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/pregnant.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PregnantFishEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(PregnantFishEntity animatable, long instanceId, AnimationState<PregnantFishEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
