
package com.susen36.caerulaarbor.item;

import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;


public class TrailedStoneSwordItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_STONE_TOOL,
			131,
			4f,
			1f,
			7,
			() -> Ingredient.of(ItemTags.create(ResourceLocation.parse("forge:stone")))
	);

	public TrailedStoneSwordItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -2.4f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double dam;
        int sharpnessLevel = 0;
        if (world instanceof Level level) {
            sharpnessLevel = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.SHARPNESS)
                    .map(h -> itemstack.getEnchantmentLevel(h)).orElse(0);
        }
        dam = 50 + 10 * sharpnessLevel;
        EPUtils.causeSanityInjury(entity, sourceentity, dam);
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), (entity.getY() + entity.getBbHeight() * 0.5), entity.getZ(), 9, 1.2, 1.5, 1.2, 0.1);
                final int tick2 = ticks;
                CaerulaArbor.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 5, 1);
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
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailed_stone_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailed_stone_sword.description_1"));
	}
}