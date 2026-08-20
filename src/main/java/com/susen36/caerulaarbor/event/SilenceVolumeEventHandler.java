package com.susen36.caerulaarbor.event;

import com.susen36.caerulaarbor.manager.upgrade.SilenceUpgradeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 静谧氛围音量处理：刷新音量缩放系数，并每 tick 驱动背景音乐音量随静谧系数持续增减。
 * 实际计算见 {@link SilenceUpgradeManager}。
 */
@EventBusSubscriber(value = {Dist.CLIENT})
public class SilenceVolumeEventHandler {

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.isAlive()) {
			// 目标值低频重算(每5tick)，展示值高频平滑(每tick)，避免范围扫描与音量台阶同时在每个声音上累积
			if (player.tickCount % 5 == 0) {
				SilenceUpgradeManager.tickVolumeScale(player);
				SilenceUpgradeManager.tickSmoothVolumeScale();
				Minecraft.getInstance().getSoundManager().updateSourceVolume(SoundSource.MUSIC, Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC));
			}
		}
	}
}