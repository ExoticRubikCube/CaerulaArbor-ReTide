package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class OceanizedEnderDragonModel extends GeoModel<OceanizedEnderDragonEntity> {
	private static final ResourceLocation TEXTURE_DEFAULT = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon.png");
	private static final ResourceLocation TEXTURE_PHASE_1 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon_1.png");
	private static final ResourceLocation TEXTURE_PHASE_2 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon_2.png");
	private static final ResourceLocation TEXTURE_PHASE_3 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon_3.png");
	private static final ResourceLocation TEXTURE_PHASE_4 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon_4.png");
	private static final ResourceLocation TEXTURE_PHASE_5 = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon_5.png");
	private static final ResourceLocation TEXTURE_NOISE = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "textures/entities/oceanized_ender_dragon_noise.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedEnderDragonEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "animations/oceanized_ender_dragon.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedEnderDragonEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "geo/oceanized_ender_dragon.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedEnderDragonEntity entity) {
		if (entity.getDeathTextureTick() > 0) {
			return TEXTURE_NOISE;
		}
		int phase = entity.getEntityData().get(OceanizedEnderDragonEntity.DATA_PHASE);
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
	public void setCustomAnimations(OceanizedEnderDragonEntity entity, long instanceId, AnimationState<OceanizedEnderDragonEntity> event) {
		super.setCustomAnimations(entity, instanceId, event);
		float partialTick = event.getPartialTick();

		// 颈部基准偏航（延迟6帧）
		double baseYRot = entity.getLatencyYRot(6, partialTick);

		// ===== 颈部 5 段 FK：yRot = 蛇形偏航延迟 =====
		float prevYRot = 0.0F;
		for (int i = 0; i < 5; i++) {
			GeoBone bone = this.getAnimationProcessor().getBone("neck" + (i + 1));
			if (bone == null) continue;

			double segmentYRot = entity.getLatencyYRot(5 - i, partialTick);
			float absYRot = Mth.wrapDegrees((float) (segmentYRot - baseYRot)) * ((float) Math.PI / 180F) * 1.5F;
			bone.setRotY(absYRot - prevYRot);
			prevYRot = absYRot;
		}

		// ===== 头部：yRot = 蛇形偏航延迟继承颈部（xRot/zRot 由 json 动画驱动） =====
		GeoBone head = this.getAnimationProcessor().getBone("head");
		if (head != null) {
			double headYRot = entity.getLatencyYRot(0, partialTick);
			float absYRot = Mth.wrapDegrees((float) (headYRot - baseYRot)) * ((float) Math.PI / 180F);
			head.setRotY(absYRot - prevYRot);
		}

		// ===== 尾部 12 段 FK：yRot = 蛇形偏航延迟 =====
		double tailBaseYRot = entity.getLatencyYRot(11, partialTick);
		prevYRot = 0.0F;

		for (int j = 0; j < 12; j++) {
			GeoBone bone = this.getAnimationProcessor().getBone("tail" + (j + 1));
			if (bone == null) continue;

			double segmentYRot = entity.getLatencyYRot(12 + j, partialTick);
			float absYRot = Mth.wrapDegrees((float) (segmentYRot - tailBaseYRot)) * 1.5F * ((float) Math.PI / 180F);
			bone.setRotY(absYRot - prevYRot);
			prevYRot = absYRot;
		}
	}
}