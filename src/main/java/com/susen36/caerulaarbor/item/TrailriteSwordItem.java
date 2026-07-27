
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;
import java.util.function.Consumer;


public class TrailriteSwordItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			7999,
			9f,
			17f,
			23,
			() -> Ingredient.of(new ItemStack(CAItems.TRAILRITE.get()))
	);

	public TrailriteSwordItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(TIER, 3, -2.4f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double absorp;
        double rate;
        SIHelper.causeSanityInjury(entity, sourceentity, 330, SanityEvent.Hurt.Type.ENTITY);
        if (!(entity instanceof Player)) {
            int lootingLevel = 0;
            int sharpnessLevel = 0;
            if (world instanceof Level level) {
                lootingLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.LOOTING)
                        .map(h -> itemstack.getEnchantmentLevel(h)).orElse(0);
                sharpnessLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.SHARPNESS)
                        .map(h -> itemstack.getEnchantmentLevel(h)).orElse(0);
            }
            if (Math.random() < 0.2 + lootingLevel * 0.02) {
                rate = 0.025 + sharpnessLevel * 0.005;
                if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) > ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * rate) {
                    absorp = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * rate;
                    if (absorp > ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                        absorp = (Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1;
                    }
                    if (!(((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) - absorp < 1)) {
                        if ((Entity) entity instanceof LivingEntity livingEntity)
                            livingEntity.setHealth((float) (livingEntity.getHealth() - absorp));
                        if ((Entity) sourceentity instanceof LivingEntity livingSourceEntity)
                            livingSourceEntity.setHealth((float) (livingSourceEntity.getHealth() + absorp));
                        if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1, 1);
                        }
                        if (world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.INSTANT_EFFECT, x, (y + 1), z, 32, 2, 2, 2, 0.25);
                    }
                }
            }
        }
        return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailrite_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_sword.description_1"));
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