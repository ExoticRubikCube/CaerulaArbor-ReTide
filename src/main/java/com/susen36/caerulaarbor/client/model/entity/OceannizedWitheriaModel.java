package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.wither.AbstractOceanizedWitherEntity;
import com.susen36.caerulaarbor.entity.wither.OceanizedWitheriaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceannizedWitheriaModel extends GeoModel<OceanizedWitheriaEntity> {
	private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_witheria.png");
	private static final ResourceLocation SHELLED_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_witheria_anger.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedWitheriaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanzied_witheria.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedWitheriaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanzied_witheria.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedWitheriaEntity entity) {
		return entity.getEntityData().get(AbstractOceanizedWitherEntity.DATA_SHELLED) ? SHELLED_TEXTURE : DEFAULT_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedWitheriaEntity animatable, long instanceId, AnimationState<OceanizedWitheriaEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}