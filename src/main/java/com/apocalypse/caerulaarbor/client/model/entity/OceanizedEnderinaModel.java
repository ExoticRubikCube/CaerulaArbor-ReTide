package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.enderdragon.OceanizedEnderinaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OceanizedEnderinaModel extends GeoModel<OceanizedEnderinaEntity> {
	private static final ResourceLocation TEXTURE_DEFAULT = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina.png");
	private static final ResourceLocation TEXTURE_PHASE_1 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_1.png");
	private static final ResourceLocation TEXTURE_PHASE_2 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_2.png");
	private static final ResourceLocation TEXTURE_PHASE_3 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_3.png");
	private static final ResourceLocation TEXTURE_PHASE_4 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_4.png");
	private static final ResourceLocation TEXTURE_PHASE_5 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_5.png");
	private static final ResourceLocation TEXTURE_NOISE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_enderina_noise.png");

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
		if (entity.getDeathTextureTick() > 0) {
			return TEXTURE_NOISE;
		}
		int phase = entity.getEntityData().get(OceanizedEnderinaEntity.DATA_PHASE);
		return switch (phase) {
			case 1 -> TEXTURE_PHASE_1;
			case 2 -> TEXTURE_PHASE_2;
			case 3 -> TEXTURE_PHASE_3;
			case 4 -> TEXTURE_PHASE_4;
			case 5 -> TEXTURE_PHASE_5;
			default -> TEXTURE_DEFAULT;
		};
	}

	@Override
	public void setCustomAnimations(OceanizedEnderinaEntity animatable, long instanceId, AnimationState<OceanizedEnderinaEntity> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("Head");
		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
