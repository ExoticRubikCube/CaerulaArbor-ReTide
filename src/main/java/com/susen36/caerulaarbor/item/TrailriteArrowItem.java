package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.bullets.ShotOceanArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TrailriteArrowItem extends ArrowItem {
	public TrailriteArrowItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
		return new ShotOceanArrowEntity(level, shooter, ammo.copyWithCount(1), weapon);
	}
}
