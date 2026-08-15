package com.susen36.caerulaarbor.block;

import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collections;

public interface NetherseaBrandBlock {
	default void applyNetherseaBrand(LevelAccessor world, Entity entity) {
		if (entity == null || this.isInactive())
			return;
		double lvl = 0;
		double gap;
		double lvl1 = 0;
		ItemStack a0;
		ItemStack a1;
		ItemStack a2;
		ItemStack a3;
		if (entity instanceof LivingEntity) {
			if (!(entity instanceof LivingEntity livEnt1 && livEnt1.hasEffect(CAMobEffects.TRAIL_BUFF))) {
				if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
					livingEntity.addEffect(new MobEffectInstance(CAMobEffects.TRAIL_BUFF, 10, 0, false, false));
			}
			gap = 20;
			a0 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();
			a1 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
			a2 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
			a3 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
			if (a0.getItem() == CAItems.SEALEATHER_BOOTS.get()) {
				gap = gap + 8;
			} else if (a0.getItem() == CAItems.SEALEATHER_CHITIN_BOOTS.get()) {
				gap = gap + 6;
			} else if (a0.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
				gap = gap + 8;
			}
			if (a1.getItem() == CAItems.SEALEATHER_LEGGINGS.get()) {
				gap = gap + 6;
			} else if (a1.getItem() == CAItems.SEALEATHER_CHITIN_LEGGINGS.get()) {
				gap = gap + 5;
			} else if (a1.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get()) {
				gap = gap + 6;
			}
			if (a2.getItem() == CAItems.SEALEATHER_CHESTPLATE.get()) {
				gap = gap + 4;
			} else if (a2.getItem() == CAItems.SEALEATHER_CHITIN_CHESTPLATE.get()) {
				gap = gap + 4;
			} else if (a2.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()) {
				gap = gap + 4;
			}
			if (a3.getItem() == CAItems.SEALEATHER_HELMET.get()) {
				gap = gap + 2;
			} else if (a3.getItem() == CAItems.SEALEATHER_CHITIN_HELMET.get()) {
				gap = gap + 3;
			} else if (a3.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get()) {
				gap = gap + 2;
			}
			if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.NETHERSEA_WALKER), a0) != 0) {
				lvl = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.NETHERSEA_WALKER), a0);
				if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
					int finalLvl = (int) lvl;
					ResourceLocation speedId = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_movement_speed");
					ResourceLocation efficiencyId = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_movement_efficiency");
					double speedValue = 0.3D + (double) finalLvl * 0.115D;

					AttributeInstance speedAttr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
					if (speedAttr != null) {
						AttributeModifier existingSpeed = speedAttr.getModifier(speedId);
						if (existingSpeed == null) {
							speedAttr.addTransientModifier(new AttributeModifier(speedId, speedValue, AttributeModifier.Operation.ADD_VALUE));
						} else if (Math.abs(existingSpeed.amount() - speedValue) > 1.0E-7D) {
							speedAttr.removeModifier(speedId);
							speedAttr.addTransientModifier(new AttributeModifier(speedId, speedValue, AttributeModifier.Operation.ADD_VALUE));
						}
					}

					AttributeInstance effAttr = livingEntity.getAttribute(Attributes.MOVEMENT_EFFICIENCY);
					if (effAttr != null && effAttr.getModifier(efficiencyId) == null) {
						effAttr.addTransientModifier(new AttributeModifier(efficiencyId, 1.0D, AttributeModifier.Operation.ADD_VALUE));
					}
				}

				if (entity instanceof Player player && player.level().isClientSide()) {
					RandomSource rand = player.getRandom();
					double dx = player.getX() - player.xo;
					double dz = player.getZ() - player.zo;
					double distSqr = Mth.square(dx) + Mth.square(dz);
					if (distSqr > 1.0E-6D && rand.nextFloat() < 0.25F) {
						entity.level().addParticle(
								ParticleTypes.SPLASH,
								player.getX() + (rand.nextDouble() - 0.5D) * (double) player.getBbWidth(),
								player.getY() + 0.1D,
								player.getZ() + (rand.nextDouble() - 0.5D) * (double) player.getBbWidth(),
								(rand.nextDouble() - 0.5D) * 0.05D,
								0.02D,
								(rand.nextDouble() - 0.5D) * 0.05D
						);
					}
				}
			} else if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
				ResourceLocation speedId = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_movement_speed");
				ResourceLocation efficiencyId = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "nethersea_walker_movement_efficiency");

				AttributeInstance speedAttr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
				if (speedAttr != null && speedAttr.getModifier(speedId) != null) {
					speedAttr.removeModifier(speedId);
				}
				AttributeInstance effAttr = livingEntity.getAttribute(Attributes.MOVEMENT_EFFICIENCY);
				if (effAttr != null && effAttr.getModifier(efficiencyId) != null) {
					effAttr.removeModifier(efficiencyId);
				}
			}
			if (entity.tickCount % gap == 0 && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "immue_to_nethersea_brand")))) {
				boolean skipDamage = false;
				if (entity instanceof Player) {
					if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.DEPTH_STRIDER), a0) != 0) {
						lvl1 = EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.DEPTH_STRIDER), a0);
					}
					if (Math.random() < 0.2 * lvl + 0.05 * lvl1) {
						skipDamage = true;
					} else if ((ModCapabilities.getPlayerVariables(entity)).player_oceanization >= 3) {
						skipDamage = true;
					} else if (((Player) entity).isCreative() || ((Player) entity).isSpectator()) {
						skipDamage = true;
					}
				}
				if (!skipDamage && entity instanceof LivingEntity livingEntity && !entity.isInvulnerable()) {
					MapVariables mapVars = MapVariables.get(world);
					float damage = 1.0F;
					if (mapVars.strategy_silence > 0) {
						damage = damage + 1.0F;
					}
					if (mapVars.strategy_sublimation > 0) {
						damage = damage + 1.0F;
					}
					entity.hurt(CADamageTypes.source(world, CADamageTypes.TRAIL_DAMAGE), damage);
					EPUtils.causeSanityInjury(livingEntity, 0.75 + damage * 0.4);
				}
			}
		}
	}

	/**
	 * 判断目标位置是否允许放置海嗣痕迹方块。
	 *
	 * <p>该方法检查目标位置正下方的方块：它的上表面必须能够承托方块，
	 * 或者被显式标记为 {@code trail_existable} 标签；同时该支撑方块不能是
	 * {@code SEA_TRAIL_SOLID}，以避免在实心海嗣痕迹上继续叠放普通痕迹。
	 *
	 * @param world 世界
	 * @param x 目标 X 坐标
	 * @param y 目标 Y 坐标
	 * @param z 目标 Z 坐标
	 * @return 若当前位置允许放置海嗣痕迹，则返回 {@code true}
	 */
	static boolean canPutTrail(LevelAccessor world, double x, double y, double z) {
		BlockPos belowPos = BlockPos.containing(x, y - 1, z);
		BlockState belowState = world.getBlockState(belowPos);
		return (belowState.isFaceSturdy(world, belowPos, Direction.UP)
				|| belowState.is(BlockTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "trail_existable"))))
				&& belowState.getBlock() != CABlocks.SEA_TRAIL_SOLID.get();
	}

	/**
	 * 该海嗣痕迹方块是否会在 {@code randomTick} / {@code tick} 中向邻近方块蔓延。
	 *
	 * <p>默认返回 {@code false}，表示不蔓延；需要蔓延的方块（如涌动的溟痕）应覆写为
	 * {@code true}。
	 *
	 * @return 若该方块允许向邻近方块蔓延，则返回 {@code true}
	 */
	default boolean canSpread() {
		return false;
	}

	/**
	 * 该海嗣痕迹方块是否处于失活状态。
	 *
	 * <p>默认返回 {@code false}；被烧焦的海嗣痕迹方块应覆写为 {@code true}，用于关闭
	 * 「加强上方海嗣 + 伤害非海嗣」的品牌效果。
	 *
	 * @return 若该方块处于失活状态，则返回 {@code true}
	 */
	default boolean isInactive() {
		return false;
	}

	/**
	 * 按当前海嗣痕迹方块的生长规则提高 {@code grow_age} 属性值。
	 *
	 * <p>该方法会直接读取目标位置上的方块状态；若该方块不存在 {@code grow_age}
	 * 整型属性，则不执行任何操作。年龄达到该属性允许的最大值后不再增长，且每次
	 * 最多一次性增加 {@code 8}（不超过最大值）。
	 *
	 * @param world 世界
	 * @param pos 目标方块位置
	 */
	default void addGrowAge(LevelAccessor world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Property<?> property = state.getBlock().getStateDefinition().getProperty("grow_age");

		if (property instanceof IntegerProperty growAgeProperty) {
			int currentAge = state.getValue(growAgeProperty);

			int maxAge = Collections.max(growAgeProperty.getPossibleValues());// 动态获取该方块允许的最大年龄，消除硬编码的 30
			if (currentAge >= maxAge) {
				return;
			}
			int nextAge = Math.min(currentAge + 8, maxAge);
			if (currentAge != nextAge) {
				world.setBlock(pos, state.setValue(growAgeProperty, nextAge), 3); // 3 = 方块更新标志位
			}
		}
	}
}
