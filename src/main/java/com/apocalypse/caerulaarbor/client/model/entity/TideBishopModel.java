package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.TideBishopEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TideBishopModel extends GeoModel<TideBishopEntity> {
	@Override
	public ResourceLocation getAnimationResource(TideBishopEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/tidebishop.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TideBishopEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/tidebishop.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TideBishopEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/" + entity.getTextureName() + ".png");
	}

	@Override
	public void setCustomAnimations(TideBishopEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
