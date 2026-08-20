
package com.susen36.caerulaarbor.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.client.model.entity.MartusModel;
import com.susen36.caerulaarbor.client.model.entity.layer.MartusLayer;
import com.susen36.caerulaarbor.entity.MartusEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MartusRenderer extends GeoEntityRenderer<MartusEntity> {
	public MartusRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MartusModel());
		this.shadowRadius = 0.6f;
		this.addRenderLayer(new MartusLayer(this));
	}

	@Override
	public RenderType getRenderType(MartusEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}


	@Override
	protected float getDeathMaxRotation(MartusEntity entityLivingBaseIn) {
		return 0.0F;
	}

	@Override
	public void render(MartusEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		// 两形态统一按祝福名单连线；名单仅在第一形态祝福或第二形态哺育生机期间填充，非技能期为空则无连线
		ListTag blessedIds = entity.getEntityData().get(MartusEntity.DATA_BLESSED_IDS).getList("Blessed", Tag.TAG_INT);
		for (Tag tag : blessedIds) {
			Entity target = entity.level().getEntity(((IntTag) tag).getAsInt());
			if (!(target instanceof LivingEntity blessed) || !blessed.isAlive()) {
				continue;
			}
			renderLeash(entity, blessed, partialTick, poseStack, bufferSource);
		}
	}

	private void renderLeash(MartusEntity martus, LivingEntity blessed, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
		poseStack.pushPose();
		Vec3 leashOffset = new Vec3(0.0D, 1.0D, 0.0D);
		double offsetX = Mth.lerp(partialTick, martus.xo, martus.getX());
		double offsetY = Mth.lerp(partialTick, martus.yo, martus.getY()) + leashOffset.y;
		double offsetZ = Mth.lerp(partialTick, martus.zo, martus.getZ());
		poseStack.translate(0.0D, leashOffset.y + 0.8D, 0.0D);
		float leashX = (float) (Mth.lerp(partialTick, blessed.xo, blessed.getX()) - offsetX);
		float leashY = (float) (Mth.lerp(partialTick, blessed.yo, blessed.getY()) - offsetY);
		float leashZ = (float) (Mth.lerp(partialTick, blessed.zo, blessed.getZ()) - offsetZ);
		float width = 0.2F;
		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.leash());
		Matrix4f matrix4f = poseStack.last().pose();
		float f4 = (float) Mth.fastInvSqrt(leashX * leashX + leashZ * leashZ) * width / 2.0F;
		float fZ = leashZ * f4;
		float fX = leashX * f4;
		int anchorBlockLight = martus.level().getBrightness(LightLayer.BLOCK, blessed.blockPosition());
		int anchorSkyLight = martus.level().getBrightness(LightLayer.SKY, blessed.blockPosition());
		int jointNum = (int) Math.sqrt(martus.distanceToSqr(blessed)) * 8;
		for (int i = jointNum; i >= 0; --i) {
			addVertexPair(vertexconsumer, matrix4f, leashX, leashY, leashZ, anchorBlockLight, anchorSkyLight, width, 0, fZ, fX, i, jointNum);
		}
		poseStack.popPose();
	}

	private static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, float leashX, float leashY, float leashZ,
	                                  int anchorBlockLight, int anchorSkyLight,
	                                  float widthZ, float widthX, float fZ, float fX, int progress, int jointNum) {
		float progressPercent = (float) progress / jointNum;
		int i = (int) Mth.lerp(progressPercent, (float) 15, (float) anchorBlockLight);
		int j = (int) Mth.lerp(progressPercent, (float) 15, (float) anchorSkyLight);
		int k = LightTexture.pack(i, j);
		float progressX = leashX * progressPercent;
		float progressY = leashY > 0.0F ? leashY * progressPercent * progressPercent : leashY - leashY * (1.0F - progressPercent) * (1.0F - progressPercent);
		float progressZ = leashZ * progressPercent;
		vertexConsumer.addVertex(matrix4f, progressX - fZ, progressY + widthX, progressZ + fX)
				.setColor(0.7F, 0.85F, 1.0F, 0.8F).setLight(k);
		vertexConsumer.addVertex(matrix4f, progressX + fZ, progressY + widthZ - widthX, progressZ - fX)
				.setColor(0.7F, 0.85F, 1.0F, 0.8F).setLight(k);
	}
}
