package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.AbsorberLimbEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AbsorberLimbModel extends GeoModel<AbsorberLimbEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/absorber_limb.png");

	@Override
	public ResourceLocation getAnimationResource(AbsorberLimbEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/absorber_limb.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AbsorberLimbEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/absorber_limb.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbsorberLimbEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(AbsorberLimbEntity animatable, long instanceId, AnimationState<AbsorberLimbEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
