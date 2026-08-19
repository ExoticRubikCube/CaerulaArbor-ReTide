package com.susen36.caerulaarbor.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

// 使用 Blockbench 4.12.4 制作
// 面向 Minecraft 1.17 及以上版本导出，使用 Mojang 映射
// 将此类粘贴到你的模组中，并生成所需的全部导入语句
public class ModelSkimmingSeaBullet<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "skimming_sea_bullet"), "main");
	public final ModelPart thrower;

	public ModelSkimmingSeaBullet(ModelPart root) {
		this.thrower = root.getChild("thrower");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition thrower = partdefinition.addOrReplaceChild("thrower",
				CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(0, 14).addBox(-3.5F, -4.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.25F)),
				PartPose.offset(0.0F, 2.6F, 0.5F));
		return LayerDefinition.create(meshdefinition, 28, 28);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		thrower.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
