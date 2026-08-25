package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.entity.enderdragon.OceanizedEnderDragonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class OceanizedEnderDragonModel extends GeoModel<OceanizedEnderDragonEntity> {
	private static final ResourceLocation TEXTURE_DEFAULT = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_ender_dragon.png");
	private static final ResourceLocation TEXTURE_PHASE_1 = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_ender_dragon_1.png");
	private static final ResourceLocation TEXTURE_PHASE_2 = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_ender_dragon_2.png");
	private static final ResourceLocation TEXTURE_PHASE_3 = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_ender_dragon_3.png");
	private static final ResourceLocation TEXTURE_PHASE_4 = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_ender_dragon_4.png");
	private static final ResourceLocation TEXTURE_PHASE_5 = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "textures/entities/oceanized_ender_dragon_5.png");

	@Override
	public ResourceLocation getAnimationResource(OceanizedEnderDragonEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "animations/oceanized_ender_dragon.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OceanizedEnderDragonEntity entity) {
		return ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "geo/oceanized_ender_dragon.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OceanizedEnderDragonEntity entity) {
		int tick = entity.getDeathTextureTick();
		if (tick > 0) {
			float pct = tick / 40.0F;
			int phase = Mth.clamp((int) Math.ceil(pct * 5.0F), 1, 5);
			return switch (phase) {
				case 1 -> TEXTURE_PHASE_1;
				case 2 -> TEXTURE_PHASE_2;
				case 3 -> TEXTURE_PHASE_3;
				case 4 -> TEXTURE_PHASE_4;
				case 5 -> TEXTURE_PHASE_5;
				default -> TEXTURE_DEFAULT;
			};
		}
		return TEXTURE_DEFAULT;
	}

	@Override
	public void setCustomAnimations(OceanizedEnderDragonEntity entity, long instanceId, AnimationState<OceanizedEnderDragonEntity> event) {
		super.setCustomAnimations(entity, instanceId, event);
		if (entity.isDeadOrDying() || entity.isReviving()) {
			return;
		}
		float partialTick = event.getPartialTick();
		float flap = entity.flapTime * Mth.TWO_PI;
		float rootWave = Mth.sin(flap - 1.0F) + 1.0F;
		rootWave = (rootWave * rootWave + rootWave * 2.0F) * 0.05F;
		GeoBone root = this.getAnimationProcessor().getBone("root");
		if (root != null) {
			root.setPosY((rootWave - 2.0F) * 16.0F);
			root.setRotX(rootWave * 2.0F * Mth.DEG_TO_RAD);
		}

		double[] neckBase = entity.getLatencyPos(6, partialTick);
		double[] neckPrevious = entity.getLatencyPos(5, partialTick);
		float neckYawDelta = Mth.wrapDegrees((float) (neckPrevious[0] - entity.getLatencyPos(10, partialTick)[0]));
		float neckYawMid = Mth.wrapDegrees((float) (neckPrevious[0] + neckYawDelta / 2.0F));
		float previousNeckX = 0.0F;
		float previousNeckY = 0.0F;
		float previousNeckZ = 0.0F;
		for (int index = 0; index < 5; index++) {
			GeoBone bone = this.getAnimationProcessor().getBone("neck" + (index + 1));
			if (bone == null) {
				continue;
			}
			double[] sample = entity.getLatencyPos(5 - index, partialTick);
			float absoluteY = Mth.wrapDegrees((float) (sample[0] - neckBase[0])) * Mth.DEG_TO_RAD * 1.5F;
			float absoluteX = Mth.cos(index * 0.45F + flap) * 0.15F + entity.getHeadPartYOffset(index, neckBase, sample) * Mth.DEG_TO_RAD * 1.5F * 5.0F;
			float absoluteZ = -Mth.wrapDegrees((float) (sample[0] - neckYawMid)) * Mth.DEG_TO_RAD * 1.5F;
			bone.setRotY(absoluteY - previousNeckY);
			bone.setRotX(absoluteX - previousNeckX);
			bone.setRotZ(absoluteZ - previousNeckZ);
			previousNeckY = absoluteY;
			previousNeckX = absoluteX;
			previousNeckZ = absoluteZ;
		}

		GeoBone body = this.getAnimationProcessor().getBone("body");
		if (body != null) {
			body.setRotZ(-neckYawDelta * 1.5F * Mth.DEG_TO_RAD);
		}

		GeoBone head = this.getAnimationProcessor().getBone("head");
		if (head != null) {
			double[] sample = entity.getLatencyPos(0, partialTick);
			float absoluteY = Mth.wrapDegrees((float) (sample[0] - neckBase[0])) * Mth.DEG_TO_RAD;
			float absoluteX = entity.getHeadPartYOffset(6, neckBase, sample) * Mth.DEG_TO_RAD * 1.5F * 5.0F;
			float absoluteZ = -Mth.wrapDegrees((float) (sample[0] - neckYawMid)) * Mth.DEG_TO_RAD;
			head.setRotY(absoluteY - previousNeckY);
			head.setRotX(absoluteX - previousNeckX);
			head.setRotZ(absoluteZ - previousNeckZ);
		}

		double[] tailBase = entity.getLatencyPos(11, partialTick);
		float tailWave = 0.0F;
		float previousTailX = 0.0F;
		float previousTailY = 0.0F;
		float previousTailZ = 0.0F;
		for (int index = 0; index < 12; index++) {
			GeoBone bone = this.getAnimationProcessor().getBone("tail" + (index + 1));
			if (bone == null) {
				continue;
			}
			double[] sample = entity.getLatencyPos(12 + index, partialTick);
			tailWave += Mth.sin(index * 0.45F + flap) * 0.05F;
			float absoluteY = Mth.wrapDegrees((float) (sample[0] - tailBase[0])) * 1.5F * Mth.DEG_TO_RAD;
			float absoluteX = tailWave + (float) (sample[1] - tailBase[1]) * Mth.DEG_TO_RAD * 1.5F * 5.0F;
			float absoluteZ = Mth.wrapDegrees((float) (sample[0] - neckYawMid)) * Mth.DEG_TO_RAD * 1.5F;
			bone.setRotY(absoluteY - previousTailY);
			bone.setRotX(absoluteX - previousTailX);
			bone.setRotZ(absoluteZ - previousTailZ);
			previousTailY = absoluteY;
			previousTailX = absoluteX;
			previousTailZ = absoluteZ;
		}
	}
}