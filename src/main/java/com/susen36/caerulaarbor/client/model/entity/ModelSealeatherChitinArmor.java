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

// 使用 Blockbench 4.12.6 制作
// 面向 Minecraft 1.17 及以上版本导出，使用 Mojang 映射
// 将此类粘贴到你的模组中，并生成所需的全部导入语句
public class ModelSealeatherChitinArmor<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "sealeather_chitin_armor"), "main");
	public final ModelPart Head;
	public final ModelPart helmet;
	public final ModelPart Body;
	public final ModelPart chestplt;
	public final ModelPart RightArm;
	public final ModelPart chestarmR;
	public final ModelPart LeftArm;
	public final ModelPart chesarmL;
	public final ModelPart RightLeg;
	public final ModelPart legR;
	public final ModelPart bootR;
	public final ModelPart LeftLeg;
	public final ModelPart legL;
	public final ModelPart bootL;

	public ModelSealeatherChitinArmor(ModelPart root) {
		this.Head = root.getChild("Head");
		this.helmet = this.Head.getChild("helmet");
		this.Body = root.getChild("Body");
		this.chestplt = this.Body.getChild("chestplt");
		this.RightArm = root.getChild("RightArm");
		this.chestarmR = this.RightArm.getChild("chestarmR");
		this.LeftArm = root.getChild("LeftArm");
		this.chesarmL = this.LeftArm.getChild("chesarmL");
		this.RightLeg = root.getChild("RightLeg");
		this.legR = this.RightLeg.getChild("legR");
		this.bootR = this.RightLeg.getChild("bootR");
		this.LeftLeg = root.getChild("LeftLeg");
		this.legL = this.LeftLeg.getChild("legL");
		this.bootL = this.LeftLeg.getChild("bootL");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition helmet = Head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.52F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition cube_r1 = helmet.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 35).addBox(-1.0F, -4.0F, -5.0F, 2.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.3054F, 0.0F, 0.0F));
		PartDefinition HatLayer_r1 = helmet.addOrReplaceChild("HatLayer_r1", CubeListBuilder.create().texOffs(30, 0).addBox(-4.0F, -32.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)),
				PartPose.offsetAndRotation(0.0F, 24.0F, -2.0F, 0.0F, -0.7854F, 0.0F));
		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(16, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition chestplt = Body.addOrReplaceChild("chestplt", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.27F)).texOffs(62, 0)
				.addBox(-3.0F, -1.0F, -4.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(114, 0).addBox(-8.0F, -1.0F, 1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition cube_r2 = chestplt.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -1.0F, -1.0F, 14.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.1309F, 0.0F, 0.0F));
		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
		PartDefinition chestarmR = RightArm.addOrReplaceChild("chestarmR", CubeListBuilder.create().texOffs(24, 45).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition cube_r3 = chestarmR.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(24, 36).mirror().addBox(-4.0F, -2.0F, -3.0F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offsetAndRotation(-3.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.8727F));
		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
		PartDefinition chesarmL = LeftArm.addOrReplaceChild("chesarmL", CubeListBuilder.create().texOffs(40, 45).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition cube_r4 = chesarmL.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 36).addBox(-3.0F, -2.0F, -3.0F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.8727F));
		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
		PartDefinition legR = RightLeg.addOrReplaceChild("legR",
				CubeListBuilder.create().texOffs(0, 50).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)).texOffs(16, 61).addBox(-3.0F, -2.0F, -3.0F, 3.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition bootR = RightLeg.addOrReplaceChild("bootR",
				CubeListBuilder.create().texOffs(56, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F)).texOffs(34, 61).addBox(-3.0F, 7.0F, -3.0F, 3.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(16, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
		PartDefinition legL = LeftLeg.addOrReplaceChild("legL",
				CubeListBuilder.create().texOffs(56, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)).texOffs(16, 61).mirror().addBox(0.0F, -2.0F, -3.0F, 3.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition bootL = LeftLeg.addOrReplaceChild("bootL",
				CubeListBuilder.create().texOffs(56, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.27F)).texOffs(34, 61).mirror().addBox(0.0F, 7.0F, -3.0F, 3.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}