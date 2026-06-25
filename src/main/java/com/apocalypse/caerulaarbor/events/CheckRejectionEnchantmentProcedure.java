package com.apocalypse.caerulaarbor.events;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEnchantments;

public class CheckRejectionEnchantmentProcedure {

	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		ItemStack armor = ItemStack.EMPTY;
		for (int index0 = 0; index0 < 4; index0++) {
			armor = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, (int) index0)) : ItemStack.EMPTY).copy();
			if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.REJECTION_CURSE.get(), armor) != 0) {
				return true;
			}
		}
		return false;
	}

}
