package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.TribunalHealerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TribunalHealerModel extends GeoModel<TribunalHealerEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/tribunalhealer.png");

	@Override
	public ResourceLocation getAnimationResource(TribunalHealerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/tribunal_healer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TribunalHealerEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/tribunal_healer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TribunalHealerEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(TribunalHealerEntity animatable, long instanceId, AnimationState<TribunalHealerEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
