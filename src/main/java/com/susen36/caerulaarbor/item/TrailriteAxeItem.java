package com.susen36.caerulaarbor.item;

import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;
import java.util.function.Consumer;


public class TrailriteAxeItem extends AxeItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			7999,
			9f,
			24f,
			23,
			() -> Ingredient.of(new ItemStack(CAItems.TRAILRITE.get()))
	);

	public TrailriteAxeItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(TIER, 1, -3.2f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        int silkTouchLevel = 0;
        int efficiencyLevel = 0;
        int sharpnessLevel = 0;
        if (world instanceof Level level) {
            silkTouchLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.SILK_TOUCH)
                    .map(itemstack::getEnchantmentLevel).orElse(0);
            efficiencyLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.EFFICIENCY)
                    .map(itemstack::getEnchantmentLevel).orElse(0);
            sharpnessLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.SHARPNESS)
                    .map(itemstack::getEnchantmentLevel).orElse(0);
        }
        if (Math.random() < 0.15 + silkTouchLevel * 0.08) {
            entity.hurt(CADamageTypes.source(world, CADamageTypes.AXE_CLEAVE, sourceentity), (float) (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * (0.08 + efficiencyLevel * 0.03 + sharpnessLevel * 0.02)));
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.BLOODOOZE.get(), x, (y + 1), z, 32, 2, 2, 2, 0.15);
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EMPTY, SoundSource.PLAYERS, 1, 1);
            }
        }
        EPUtils.causeSanityInjury(entity, sourceentity, 11);
        return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailrite_axe.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_axe.description_1"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.ADD_REACH))) {
                if (entity instanceof LivingEntity living && !living.level().isClientSide())
                    living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 20, 2, false, false));
            }
        }
	}

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource pDamageSource) {
		return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return Math.min(amount, 1);
	}
}