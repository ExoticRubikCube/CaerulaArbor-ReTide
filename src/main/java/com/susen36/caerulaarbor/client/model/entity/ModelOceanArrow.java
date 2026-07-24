package com.susen36.caerulaarbor.client.model.entity;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

// 使用 Blockbench 4.12.5 制作
// 面向 Minecraft 1.17 及以上版本导出，使用 Mojang 映射
// 将此类粘贴到你的模组中，并生成所需的全部导入语句
public class ModelOceanArrow<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "ocean_arrow"), "main");
	public final ModelPart arrow;

	public ModelOceanArrow(ModelPart root) {
		this.arrow = root.getChild("arrow");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition arrow = partdefinition.addOrReplaceChild("arrow", CubeListBuilder.create().texOffs(0, -5).addBox(0.0F, -8.0F, -2.5F, 0.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
				.addBox(-2.5F, -8.0F, 0.0F, 5.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 11).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 15, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		arrow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}