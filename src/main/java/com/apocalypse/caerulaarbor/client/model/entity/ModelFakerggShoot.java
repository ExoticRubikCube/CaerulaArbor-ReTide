package com.apocalypse.caerulaarbor.client.model.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
public class ModelFakerggShoot<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CaerulaArborMod.MODID, "fakergg_shoot"), "main");
	public final ModelPart ball;

	public ModelFakerggShoot(ModelPart root) {
		this.ball = root.getChild("ball");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition ball = partdefinition.addOrReplaceChild("ball",
				CubeListBuilder.create().texOffs(0, 0).addBox(-5.1684F, -5.388F, -6.5368F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-1.0F)).texOffs(1, 2).addBox(-1.3665F, -8.3598F, -2.619F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.1502F, 19.3297F, 1.9307F));
		PartDefinition ball_r1 = ball.addOrReplaceChild("ball_r1", CubeListBuilder.create().texOffs(1, 2).addBox(-1.0F, -1.9F, 0.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.4821F, -5.087F, 0.681F, -0.3491F, 0.0F, 0.7854F));
		PartDefinition ball_r2 = ball.addOrReplaceChild("ball_r2", CubeListBuilder.create().texOffs(1, 2).addBox(-2.0F, -2.7F, -4.2F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.3665F, -6.3598F, 1.081F, 0.5672F, 0.0F, -0.7854F));
		PartDefinition ball_r3 = ball.addOrReplaceChild("ball_r3", CubeListBuilder.create().texOffs(1, 2).addBox(0.0F, -2.0F, 0.3F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(5.6335F, 1.6402F, 1.881F, -0.5672F, 0.0F, 1.5708F));
		PartDefinition ball_r4 = ball.addOrReplaceChild("ball_r4", CubeListBuilder.create().texOffs(1, 2).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-5.2665F, 4.2402F, -3.119F, 0.4363F, 0.0F, -2.2689F));
		PartDefinition ball_r5 = ball.addOrReplaceChild("ball_r5", CubeListBuilder.create().texOffs(1, 2).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.8335F, 5.5402F, -1.519F, 1.309F, -1.4399F, 1.5708F));
		PartDefinition ball_r6 = ball.addOrReplaceChild("ball_r6", CubeListBuilder.create().texOffs(1, 2).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.1665F, 4.9402F, 2.481F, -1.5708F, -1.0908F, -1.5708F));
		PartDefinition ball_r7 = ball.addOrReplaceChild("ball_r7", CubeListBuilder.create().texOffs(1, 2).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.1665F, -1.2598F, 3.981F, -1.5708F, 0.4363F, -1.5708F));
		PartDefinition ball_r8 = ball.addOrReplaceChild("ball_r8", CubeListBuilder.create().texOffs(1, 2).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-6.1665F, -0.3598F, 0.881F, 0.0F, 0.0F, -1.5708F));
		PartDefinition ball_r9 = ball.addOrReplaceChild("ball_r9", CubeListBuilder.create().texOffs(1, 2).addBox(6.0F, -8.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.3665F, -6.3598F, -1.119F, 0.0F, 0.0F, 1.5708F));
		return LayerDefinition.create(meshdefinition, 40, 20);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		ball.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}