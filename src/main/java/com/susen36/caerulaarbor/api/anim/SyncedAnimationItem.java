package com.susen36.caerulaarbor.api.anim;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface SyncedAnimationItem {
	String GECKO_ANIMATION_KEY = "geckoAnim";

	void setAnimationProcedure(String animation);

	default String consumeQueuedAnimation(ItemStack stack) {
		String animation = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(GECKO_ANIMATION_KEY);
		if (!animation.isEmpty()) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(GECKO_ANIMATION_KEY, ""));
		}
		return animation;
	}
}