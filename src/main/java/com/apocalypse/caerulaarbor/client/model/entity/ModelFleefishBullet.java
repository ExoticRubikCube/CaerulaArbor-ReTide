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
public class ModelFleefishBullet<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CaerulaArborMod.MODID, "fleefish_bullet"), "main");
	public final ModelPart thrower;

	public ModelFleefishBullet(ModelPart root) {
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
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		thrower.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}