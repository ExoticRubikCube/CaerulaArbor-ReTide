package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.TrailriteArmorstandDisplayItem;
import com.susen36.caerulaarbor.client.model.block.TrailriteArmorstandDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TrailriteArmorstandDisplayItemRenderer extends GeoItemRenderer<TrailriteArmorstandDisplayItem> {
	public TrailriteArmorstandDisplayItemRenderer() {
		super(new TrailriteArmorstandDisplayModel());
	}

	@Override
	public RenderType getRenderType(TrailriteArmorstandDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
