package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.crawler.PocketSeaCrawlerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PocketSeaCrawlerModel extends GeoModel<PocketSeaCrawlerEntity> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/pocket_sea_crawlerfish.png");
	private static final ResourceLocation TEXTURE_CHARGED = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/nourished_crawler.png");
	private static final ResourceLocation MODEL_NORMAL = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/pocket_sea_cawler.geo.json");
	private static final ResourceLocation MODEL_CHARGED = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/nourished_crawler.geo.json");

	@Override
	public ResourceLocation getAnimationResource(PocketSeaCrawlerEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/pocket_sea_creeper.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PocketSeaCrawlerEntity entity) {
		return entity.charged() ? MODEL_CHARGED : MODEL_NORMAL;
	}

	@Override
	public ResourceLocation getTextureResource(PocketSeaCrawlerEntity entity) {
		return entity.charged() ? TEXTURE_CHARGED : TEXTURE;
	}

	@Override
	public void setCustomAnimations(PocketSeaCrawlerEntity animatable, long instanceId, AnimationState<PocketSeaCrawlerEntity> animationState) {
		GeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}