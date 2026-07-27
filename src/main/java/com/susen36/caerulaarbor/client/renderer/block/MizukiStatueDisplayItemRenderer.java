package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.MizukiStatueDisplayItem;
import com.susen36.caerulaarbor.client.model.block.MizukiStatueDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MizukiStatueDisplayItemRenderer extends GeoItemRenderer<MizukiStatueDisplayItem> {
	public MizukiStatueDisplayItemRenderer() {
		super(new MizukiStatueDisplayModel());
	}

	@Override
	public RenderType getRenderType(MizukiStatueDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}