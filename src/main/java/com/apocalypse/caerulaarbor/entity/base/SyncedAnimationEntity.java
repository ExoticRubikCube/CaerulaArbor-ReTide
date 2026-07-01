package com.apocalypse.caerulaarbor.entity.base;

public interface SyncedAnimationEntity {
	String UNDEFINED_ANIMATION = "undefined";

	String getSyncedAnimation();

	void setAnimation(String animation);

	void setAnimationProcedure(String animation);

	default void syncClientAnimation() {
		String animation = this.getSyncedAnimation();
		if (UNDEFINED_ANIMATION.equals(animation)) {
			return;
		}

		this.setAnimation(UNDEFINED_ANIMATION);
		this.setAnimationProcedure(animation);
	}
}
