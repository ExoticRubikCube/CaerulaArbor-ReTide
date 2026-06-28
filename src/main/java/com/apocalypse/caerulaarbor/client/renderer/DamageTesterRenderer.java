package com.apocalypse.caerulaarbor.client.renderer;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.DamageTesterEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class DamageTesterRenderer extends HumanoidMobRenderer<DamageTesterEntity, HumanoidModel<DamageTesterEntity>> {
	public DamageTesterRenderer(EntityRendererProvider.Context context) {
		super(context, new HumanoidModel<DamageTesterEntity>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
		this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(DamageTesterEntity entity) {
		return new ResourceLocation(CaerulaArborMod.MODID, "textures/entities/limitless.png");
	}
}
