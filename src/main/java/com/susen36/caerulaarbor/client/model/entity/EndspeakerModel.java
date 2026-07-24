package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.EndspeakerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EndspeakerModel<T extends EndspeakerEntity> extends GeoModel<T> {
	@Override
	public ResourceLocation getAnimationResource(T entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/endspeaker_" + entity.getPhase() + ".animation.json");
	}

	@Override
	public ResourceLocation getModelResource(T entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/endspeaker_" + entity.getPhase() + ".geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		String textureName = "endspeaker_" + entity.getPhase();
		if (entity.getPhase() == 3 && entity.getHealth() < entity.getMaxHealth() * 0.4F) {
			textureName = "endspeaker_3_broken";
		}
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/" + textureName + ".png");
	}

	@Override
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}
