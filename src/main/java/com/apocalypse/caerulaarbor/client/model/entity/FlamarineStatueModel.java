package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.FlamarineStatueEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FlamarineStatueModel extends GeoModel<FlamarineStatueEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/living_armor.png");

	@Override
	public ResourceLocation getAnimationResource(FlamarineStatueEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/living_armor.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FlamarineStatueEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/living_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlamarineStatueEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(FlamarineStatueEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
