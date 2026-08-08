package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.init.CAEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ItemUtils {

	private ItemUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * 将副手上的 {@code 锋利} 等级转写到主手物品的 {@code 联觉} 附魔上。
	 *
	 * <p>仅当实体当前主手物品与传入的 {@code itemstack} 为同一种物品，且副手 {@code 锋利}
	 * 等级高于主手现有 {@code 联觉} 等级时，才会执行转写。转写成功后会移除副手的
	 * {@code 锋利}，并播放粒子与音效反馈。</p>
	 *
	 * @param world 世界访问器
	 * @param x 粒子与音效触发位置的 X 坐标
	 * @param y 粒子与音效触发位置的 Y 坐标
	 * @param z 粒子与音效触发位置的 Z 坐标
	 * @param entity 执行转写的生物实体
	 * @param itemstack 主手目标物品
	 */
	public static void transferSharpnessToSynesthesia(LevelAccessor world, double x, double y, double z, LivingEntity entity, ItemStack itemstack) {
		if (entity.getMainHandItem().getItem() == itemstack.getItem()) {
			if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.SHARPNESS), entity.getOffhandItem()) != 0
					&& EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.SHARPNESS), entity.getOffhandItem()) > EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack)) {
				Holder<Enchantment> synesthesia = CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SYNESTHESIA);
				EnchantmentHelper.updateEnchantments(itemstack, enchantments -> enchantments.removeIf(enchantment -> enchantment.equals(synesthesia)));
				itemstack.enchant(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SYNESTHESIA), EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.SHARPNESS), entity.getOffhandItem()));
				Holder<Enchantment> sharpness = CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.SHARPNESS);
				EnchantmentHelper.updateEnchantments(entity.getOffhandItem(), enchantments -> enchantments.removeIf(enchantment -> enchantment.equals(sharpness)));
				if (world instanceof ServerLevel level)
					level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 72, 1.2, 2, 1.2, 0.2);
				if (world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 3, 1);
				}
			}
		}
	}
}