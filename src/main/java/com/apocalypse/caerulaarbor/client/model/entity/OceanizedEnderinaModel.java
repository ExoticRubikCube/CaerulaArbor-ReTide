package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedEnderinaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedEnderinaModel extends GeoModel<OceanizedEnderinaEntity> {
	private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina.png");
	private static final ResourceLocation NOISE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_noise.png");
	private static final ResourceLocation DEATH_1_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_1.png");
	private static final ResourceLocation DEATH_2_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_2.png");
	private static final ResourceLocation DEATH_3_TEXTURE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_3.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedEnderinaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_enderina.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedEnderinaEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_enderina.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedEnderinaEntity entity) {
		int deathTick = entity.getDeathTextureTick();
		if (deathTick >= 30) {
			return DEATH_3_TEXTURE;
		}
		if (deathTick >= 20) {
			return DEATH_2_TEXTURE;
		}
		if (deathTick >= 10) {
			return DEATH_1_TEXTURE;
		}
		int reviveTick = entity.getEntityData().get(OceanizedEnderinaEntity.DATA_REVIVE_TICK);
		int phase = entity.getEntityData().get(OceanizedEnderinaEntity.DATA_PHASE);
		if (phase > 0 && reviveTick > 0 && (entity.getHealth() >= entity.getMaxHealth() || reviveTick < 100)) {
			return NOISE_TEXTURE;
		}
		return DEFAULT_TEXTURE;
	}

	@Override
	public void setCustomAnimations(OceanizedEnderinaEntity animatable, long instanceId, AnimationState<OceanizedEnderinaEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
