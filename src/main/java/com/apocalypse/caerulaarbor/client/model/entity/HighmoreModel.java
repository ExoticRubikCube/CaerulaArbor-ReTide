package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.HighmoreEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HighmoreModel extends GeoModel<HighmoreEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/highmore.png");

	@Override
	public ResourceLocation getAnimationResource(HighmoreEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/highmore.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(HighmoreEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/highmore.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(HighmoreEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(HighmoreEntity animatable, long instanceId, AnimationState<HighmoreEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("core");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
