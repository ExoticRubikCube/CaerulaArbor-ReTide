package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IreneEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class IreneModel extends GeoModel<IreneEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/irene.png");

	@Override
	public ResourceLocation getAnimationResource(IreneEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "animations/irene.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(IreneEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "geo/irene.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(IreneEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(IreneEntity animatable, long instanceId, AnimationState<IreneEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
