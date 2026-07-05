package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.ApostleProkaryoteEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ApostleProkaryoteModel extends GeoModel<ApostleProkaryoteEntity> {
	private static final ResourceLocation UNSHELLED_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/apostle_unshelled.png");
	private static final ResourceLocation SHELLED_TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/apostle.png");

	@Override
	public ResourceLocation getAnimationResource(ApostleProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/apostle.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(ApostleProkaryoteEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/apostle.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ApostleProkaryoteEntity entity) {
		return entity.getEntityData().get(ApostleProkaryoteEntity.DATA_SHELLED) ? SHELLED_TEXTURE : UNSHELLED_TEXTURE;
	}

	@Override
	public void setCustomAnimations(ApostleProkaryoteEntity animatable, long instanceId, AnimationState<ApostleProkaryoteEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
