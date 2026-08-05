package com.susen36.caerulaarbor.client.renderer.item;

import com.susen36.caerulaarbor.client.model.item.UninishedBeautyItemModel;
import com.susen36.caerulaarbor.item.UninishedBeautyItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class UninishedBeautyItemRenderer extends GeoItemRenderer<UninishedBeautyItem> {
	public UninishedBeautyItemRenderer() {
		super(new UninishedBeautyItemModel());
	}

	@Override
	public RenderType getRenderType(UninishedBeautyItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
