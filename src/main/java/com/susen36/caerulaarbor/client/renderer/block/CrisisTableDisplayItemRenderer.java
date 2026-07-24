package com.susen36.caerulaarbor.client.renderer.block;

import com.susen36.caerulaarbor.block.item.CrisisTableDisplayItem;
import com.susen36.caerulaarbor.client.model.block.CrisisTableDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CrisisTableDisplayItemRenderer extends GeoItemRenderer<CrisisTableDisplayItem> {
	public CrisisTableDisplayItemRenderer() {
		super(new CrisisTableDisplayModel());
	}

	@Override
	public RenderType getRenderType(CrisisTableDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
