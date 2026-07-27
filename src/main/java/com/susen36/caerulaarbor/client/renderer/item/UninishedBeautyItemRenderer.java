package com.susen36.caerulaarbor.client.renderer.item;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.item.UninishedBeautyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.HashSet;
import java.util.Set;

public class UninishedBeautyItemRenderer extends GeoItemRenderer<UninishedBeautyItem> {
	private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;

	protected boolean renderArms = false;
	protected MultiBufferSource currentBuffer;
	protected RenderType renderType;
	public ItemDisplayContext transformType;
	protected UninishedBeautyItem animatable;
	private final Set<String> hiddenBones = new HashSet<>();

	public UninishedBeautyItemRenderer() {
		super(new UninishedBeautyItemModel());
	}

	@Override
	public RenderType getRenderType(UninishedBeautyItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		this.transformType = transformType;
		super.renderByItem(stack, transformType, matrixStack, bufferIn, combinedLightIn, p_239207_6_);
	}

	@Override
	public void actuallyRender(PoseStack matrixStackIn, UninishedBeautyItem animatable, BakedGeoModel model, RenderType type, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, boolean isRenderer, float partialTicks, int packedLightIn,
	                           int packedOverlayIn, int color) {
		this.currentBuffer = renderTypeBuffer;
		this.renderType = type;
		this.animatable = animatable;
		super.actuallyRender(matrixStackIn, animatable, model, type, renderTypeBuffer, vertexBuilder, isRenderer, partialTicks, packedLightIn, packedOverlayIn, color);
		if (this.renderArms) {
			this.renderArms = false;
		}
	}

	@Override
	public void renderRecursively(PoseStack stack, UninishedBeautyItem animatable, GeoBone bone, RenderType type, MultiBufferSource buffer, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLightIn, int packedOverlayIn,
	                              float red, float green, float blue, float alpha) {
		Minecraft mc = Minecraft.getInstance();
		String name = bone.getName();
		boolean renderingArms = false;

		if (name.equals("left_arm") || name.equals("right_arm")) {
			bone.setHidden(true);
			renderingArms = true;
		} else {
			bone.setHidden(this.hiddenBones.contains(name));
		}

		if (this.transformType != null && this.transformType.firstPerson() && renderingArms) {
			AbstractClientPlayer player = mc.player;
			if (player != null) {
				float armsAlpha = player.isInvisible() ? 0.15f : 1.0f;
				PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
				PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();

				stack.pushPose();
				RenderUtils.translateMatrixToBone(stack, bone);
				RenderUtils.translateToPivotPoint(stack, bone);
				RenderUtils.rotateMatrixAroundBone(stack, bone);
				RenderUtils.scaleMatrixForBone(stack, bone);
				RenderUtils.translateAwayFromPivotPoint(stack, bone);

				ResourceLocation loc = player.getSkin().texture();
				VertexConsumer armBuilder = this.currentBuffer.getBuffer(RenderType.entitySolid(loc));
				VertexConsumer sleeveBuilder = this.currentBuffer.getBuffer(RenderType.entityTranslucent(loc));

				if (name.equals("left_arm")) {
					stack.translate(-1.0f * SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
					this.renderPartOverBone(model.leftArm, bone, stack, armBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha);
					this.renderPartOverBone(model.leftSleeve, bone, stack, sleeveBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha);
				} else if (name.equals("right_arm")) {
					stack.translate(SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
					this.renderPartOverBone(model.rightArm, bone, stack, armBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha);
					this.renderPartOverBone(model.rightSleeve, bone, stack, sleeveBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, armsAlpha);
				}

				this.currentBuffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(this.animatable)));
				stack.popPose();
			}
		}
		super.renderRecursively(stack, animatable, bone, type, buffer, bufferIn, isReRender, partialTick, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	private void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float alpha) {
		model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
		model.xRot = 0.0f;
		model.yRot = 0.0f;
		model.zRot = 0.0f;

		int color = FastColor.ARGB32.color((int) (alpha * 255.0F), 255, 255, 255);
		model.render(stack, buffer, packedLightIn, packedOverlayIn, color);
	}

	@Override
	public ResourceLocation getTextureLocation(UninishedBeautyItem instance) {
		return super.getTextureLocation(instance);
	}
}