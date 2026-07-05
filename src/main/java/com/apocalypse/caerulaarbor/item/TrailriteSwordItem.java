
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class TrailriteSwordItem extends SwordItem {
	public TrailriteSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 7999;
			}

			public float getSpeed() {
				return 9f;
			}

			public float getAttackDamageBonus() {
				return 17f;
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
		}, 3, -2.4f, new Item.Properties().fireResistant());
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
            if (Math.random() < 0.2 + itemstack.getEnchantmentLevel(Enchantments.MOB_LOOTING) * 0.02) {
                rate = 0.025 + itemstack.getEnchantmentLevel(Enchantments.SHARPNESS) * 0.005;
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
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailrite_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_sword.description_1"));
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
}
