package com.apocalypse.caerulaarbor.item;

import net.minecraft.world.item.ItemStack;

public interface SyncedAnimationItem {
	String GECKO_ANIMATION_KEY = "geckoAnim";

	void setAnimationProcedure(String animation);

	default String consumeQueuedAnimation(ItemStack stack) {
		String animation = stack.getOrCreateTag().getString(GECKO_ANIMATION_KEY);
		if (!animation.isEmpty()) {
			stack.getOrCreateTag().putString(GECKO_ANIMATION_KEY, "");
		}
		return animation;
	}
}
