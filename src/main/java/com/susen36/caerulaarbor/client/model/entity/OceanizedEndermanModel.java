package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.OceanizedEndermanEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedEndermanModel extends GeoModel<OceanizedEndermanEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanzied_enderman.png");
	private static final ResourceLocation TEXTURE_CREEPER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanzied_enderman_creeper.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedEndermanEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanzied_enderman.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedEndermanEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanzied_enderman.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedEndermanEntity entity) {
		return entity.isHolding() ? TEXTURE_CREEPER : TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedEndermanEntity animatable, long instanceId, AnimationState<OceanizedEndermanEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

		GeoBone creeper = getAnimationProcessor().getBone("creeper");
		if (creeper != null) {
			creeper.setHidden(!animatable.isHolding());
		}
	}
}