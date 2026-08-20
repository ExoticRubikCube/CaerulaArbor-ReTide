package com.susen36.caerulaarbor.mixin;

import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在 {@link SoundEngine} 音量的最终咽喉点施加静谧氛围系数。
 * <p>
 * 原版 {@code SoundEngine.play(SoundInstance)} 输入一次性音源(爆炸/雨/经验球)音量会被 clamp 回 1.0，
 * 提前乘系数无效。这里在 {@code calculateVolume(float, SoundSource)} 的返回值上(clamp 之后)统一缩放，
 * 爆炸、雨、经验球以及其他一次性与循环音源都能被覆盖。
 * </p>
 * <p>
 *
 */
@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
	// 当前正在结算音量的声音实例，供音量裁剪判断伊莎玛拉发声(不参与削减)；与 calculateVolume(SoundInstance) 在同一调用栈内
	@Unique
	private static SoundInstance silenceInstance;

	@Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("HEAD"))
	private void caerulaTrackOneShot(SoundInstance instance, CallbackInfo ci) {
		silenceInstance = instance;
	}

	@Inject(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At("HEAD"))
	private void caerulaTrackLooping(SoundInstance instance, CallbackInfoReturnable<Float> cir) {
		silenceInstance = instance;
	}

	@Inject(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("RETURN"), cancellable = true)
	private void caerulaApplySilence(float volume, SoundSource source, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(SilenceUpgradeManager.scaleVolume(silenceInstance, cir.getReturnValue()));
	}
}