package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCreeperEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PocketSeaCreeperModel extends GeoModel<PocketSeaCreeperEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/pocket_sea_creeper.png");

	@Override
	public ResourceLocation getAnimationResource(PocketSeaCreeperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/pocket_sea_creeper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaCreeperEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/pocket_sea_creeper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaCreeperEntity entity) {
		return TEXTURE;
	}

	@Override
	public void setCustomAnimations(PocketSeaCreeperEntity animatable, long instanceId, AnimationState<PocketSeaCreeperEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}