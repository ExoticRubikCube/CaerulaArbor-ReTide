
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;

public class DeductOneSanityMobEffect extends MobEffect {
	public DeductOneSanityMobEffect() {
		super(MobEffectCategory.HARMFUL, -6684724);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
		cures.add(new ItemStack(Items.MILK_BUCKET));
		cures.add(new ItemStack(Items.TOTEM_OF_UNDYING));
		cures.add(new ItemStack(Items.HONEY_BOTTLE));
		return cures;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
        SIHelper.causeSanityInjury(entity, (double) amplifier + 1, SanityEvent.Hurt.Type.POTION);
    }

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void initializeClient(java.util.function.Consumer<IClientMobEffectExtensions> consumer) {
		consumer.accept(new IClientMobEffectExtensions() {
			@Override
			public boolean isVisibleInInventory(MobEffectInstance effect) {
				return false;
			}

			@Override
			public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
				return false;
			}

			@Override
			public boolean isVisibleInGui(MobEffectInstance effect) {
				return false;
			}
		});
	}
}
