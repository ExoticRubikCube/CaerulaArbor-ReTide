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

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class ModelHighmoreShoot<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CaerulaArborMod.MODID, "highmore_shoot"), "main");
	public final ModelPart horizontal;
	public final ModelPart vertical;

	public ModelHighmoreShoot(ModelPart root) {
		this.horizontal = root.getChild("horizontal");
		this.vertical = this.horizontal.getChild("vertical");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition horizontal = partdefinition.addOrReplaceChild("horizontal",
				CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -2.0F, -2.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 24).addBox(1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(24, 0)
						.addBox(3.0F, -2.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(24, 12).addBox(-5.0F, -2.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(24, 24)
						.addBox(-7.0F, -2.0F, -9.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 36).addBox(5.0F, -2.0F, -9.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 72)
						.addBox(-10.0F, -2.0F, -22.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(72, 0).addBox(9.0F, -2.0F, -22.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(24, 36)
						.addBox(-9.0F, -2.0F, -15.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 48).addBox(7.0F, -2.0F, -15.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.25F, 2.0F, -1.0F, -1.5708F, 0.0F, 3.1416F));
		PartDefinition vertical = horizontal.addOrReplaceChild("vertical",
				CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(48, 12).addBox(-3.0F, -2.0F, -2.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(24, 48)
						.addBox(1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(48, 24).addBox(3.0F, -2.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(48, 36)
						.addBox(-5.0F, -2.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(48, 48).addBox(-7.0F, -2.0F, -9.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 60)
						.addBox(5.0F, -2.0F, -9.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(72, 12).addBox(-10.0F, -2.0F, -22.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(22, 72)
						.addBox(9.0F, -2.0F, -22.0F, 1.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(24, 60).addBox(-9.0F, -2.0F, -15.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(48, 60)
						.addBox(7.0F, -2.0F, -15.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.5708F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		horizontal.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}