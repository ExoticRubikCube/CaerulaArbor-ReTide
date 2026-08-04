package com.susen36.caerulaarbor.mixin;

import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.loading.math.MathValue;

import java.util.List;

@Mixin(AnimationController.class)
public interface AnimationControllerInvoker {
	@Invoker("getAnimationPointAtTick")
	AnimationPoint getAnimationPointAtTick(List<Keyframe<MathValue>> frames, double tick, boolean isRotation, Direction.Axis axis);
}
