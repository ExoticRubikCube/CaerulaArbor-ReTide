package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.init.CAEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class ItemUtils {

	private ItemUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static String getCursedDescription(ItemStack itemstack) {
		String first_two;
		String locId;
		locId = itemstack.getDescriptionId();
		first_two = Component.translatable((locId + ".description_0")).getString() + "\n" + Component.translatable((locId + ".description_1")).getString();
		if (itemstack.getOrCreateTag().getBoolean("used")) {
			return first_two + "\n" + Component.translatable("item.caerula_arbor.cursed.used").getString();
		}
		return first_two;
	}

	public static boolean isFilledwithPersonnel(ItemStack itemstack) {
		String name = itemstack.getOrCreateTag().getString("name");
		if ((name).equals("apocata")) {
			return false;
		}
		return !(name).isEmpty();
	}

	public static String getOneUseItemDescription(ItemStack itemstack) {
		String locId = itemstack.getDescriptionId();
		String first_two = Component.translatable((locId + ".description_0")).getString() + "\n" + Component.translatable((locId + ".description_1")).getString();
		if (itemstack.getOrCreateTag().getBoolean("used")) {
			return first_two + "\n" + Component.translatable("item.caerula_arbor.relics.used").getString();
		}
		return first_two;
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
			if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, entity.getOffhandItem()) != 0
					&& entity.getOffhandItem().getEnchantmentLevel(Enchantments.SHARPNESS) > itemstack.getEnchantmentLevel(CAEnchantments.SYNESTHESIA.get())) {
				{
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemstack);
					if (enchantments.containsKey(CAEnchantments.SYNESTHESIA.get())) {
						enchantments.remove(CAEnchantments.SYNESTHESIA.get());
						EnchantmentHelper.setEnchantments(enchantments, itemstack);
					}
				}
				itemstack.enchant(CAEnchantments.SYNESTHESIA.get(), entity.getOffhandItem().getEnchantmentLevel(Enchantments.SHARPNESS));
				{
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(entity.getOffhandItem());
					if (enchantments.containsKey(Enchantments.SHARPNESS)) {
						enchantments.remove(Enchantments.SHARPNESS);
						EnchantmentHelper.setEnchantments(enchantments, entity.getOffhandItem());
					}
				}
				if (world instanceof ServerLevel level)
					level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 72, 1.2, 2, 1.2, 0.2);
				if (world instanceof Level level) {
					level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.PLAYERS, 3, 1);
				}
			}
		}
	}
}
