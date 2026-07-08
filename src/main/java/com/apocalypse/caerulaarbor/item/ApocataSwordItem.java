package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.Comparator;
import java.util.List;

public class ApocataSwordItem extends SwordItem {
	public ApocataSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 114514;
			}

			public float getSpeed() {
				return 99f;
			}

			public float getAttackDamageBonus() {
				return 65532f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 99;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.APOCALYPSE.get()));
			}
		}, 3, 0f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		if (entity instanceof Player player && player.getMainHandItem() == itemstack) {
			HitResult hitResult = player.pick(player.getAttributeValue(ForgeMod.ENTITY_REACH.get()), 0.0F, false);
			if (hitResult.getType() == HitResult.Type.MISS && !player.level().isClientSide()) {
				Vec3 center = new Vec3(player.getX(), player.getY(), player.getZ());
				List<Entity> entities = player.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32.0), target -> true).stream()
						.sorted(Comparator.comparingDouble(target -> target.distanceToSqr(center)))
						.toList();
				for (Entity target : entities) {
					if (target instanceof LightningBolt) {
						continue;
					}
					if (target.getDisplayName().getString().equals("item")) {
						continue;
					}
					if (target != entity) {
						target.hurt(CADamageTypes.source(player.level(), CADamageTypes.INV_KILLER), 114514);
					}
				}
			}
		}
		return retval;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(ItemStack itemstack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.apocata_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.apocata_sword.description_1"));
	}
}
