package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CAParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import java.util.List;
import java.util.function.Consumer;

public class TrailriteAxeItem extends AxeItem {
	public TrailriteAxeItem() {
		super(new Tier() {
			public int getUses() {
				return 7999;
			}

			public float getSpeed() {
				return 9f;
			}

			public float getAttackDamageBonus() {
				return 24f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 23;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.TRAILRITE.get()));
			}
		}, 1, -3.2f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (Math.random() < 0.15 + itemstack.getEnchantmentLevel(Enchantments.SILK_TOUCH) * 0.08) {
            ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.AXE_CLEAVE, sourceentity), (float) (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * (0.08 + itemstack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY) * 0.03 + itemstack.getEnchantmentLevel(Enchantments.SHARPNESS) * 0.02)));
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.BLOODOOZE.get(), x, (y + 1), z, 32, 2, 2, 2, 0.15);
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EMPTY, SoundSource.PLAYERS, 1, 1);
            }
        }
        SIHelper.causeSanityInjury(entity, sourceentity, 225, SanityEvent.Hurt.Type.ENTITY);
        return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailrite_axe.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_axe.description_1"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.ADD_REACH.get()))) {
                if (entity instanceof LivingEntity living && !living.level().isClientSide())
                    living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 20, 2, false, false));
            }
        }
	}

	@Override
	public boolean canBeHurtBy(DamageSource pDamageSource) {
		return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return Math.min(amount, 1);
	}
}
