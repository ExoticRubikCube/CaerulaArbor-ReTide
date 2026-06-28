package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.utils.EffectUtils;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

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
				return Ingredient.of(new ItemStack(CaerulaArborModItems.TRAILRITE.get()));
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
        if (entity != null && sourceentity != null) {
            if (Math.random() < 0.15 + itemstack.getEnchantmentLevel(Enchantments.SILK_TOUCH) * 0.08) {
                ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "axe_cleave"))), sourceentity),
                        (float) (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * (0.08 + itemstack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY) * 0.03 + itemstack.getEnchantmentLevel(Enchantments.SHARPNESS) * 0.02)));
                if (world instanceof ServerLevel _level)
                    _level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.BLOODOOZE.get()), x, (y + 1), z, 32, 2, 2, 2, 0.15);
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty")), SoundSource.PLAYERS, 1, 1);
                }
            }
            EntityUtils.deductSanity(entity, 225);
        }
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
		if (selected)
			EffectUtils.addReachEffect(entity, 20, 2);
	}
}
