package com.susen36.caerulaarbor.recipe;

import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@EventBusSubscriber
public class AnvilRecipeHandler {
	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		if ((event.getLeft().getItem() == CACollectible.SOLO_MUSIC_BOX) && (event.getRight().getItem() == Items.COPPER_INGOT)) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(4);
				event.setOutput(new ItemStack(CAItems.MUSIC_BOX_FIXED.get()));
			}
		} else if ((event.getLeft().getItem() == Items.IRON_HELMET) && (event.getRight().getItem() == CACollectible.RELIC_CROWN)) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(5);
				event.setOutput(new ItemStack(CAItems.WEARABLE_CROWN_HELMET.get()));
			}
		} else if ((event.getLeft().getItem() == Items.IRON_CHESTPLATE) && (event.getRight().getItem() == CACollectible.KING_ARMOR)) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(5);
				event.setOutput(new ItemStack(CAItems.WEARABLE_CHEST_CHESTPLATE.get()));
			}
		} else if ((event.getLeft().getItem() == Items.IRON_SWORD) && (event.getRight().getItem() == CAItems.KNIGHT_CORPSE.get())) {
			if ((event.getLeft().getCount() == 1) && (event.getRight().getCount() >= 1)) {
				event.setMaterialCost(1);
				event.setCost(4);
				event.setOutput(new ItemStack(CAItems.IRON_SWORD_OF_KNIGHT_CORPUS.get()));
			}
		} else if ((event.getLeft().getItem() == CAItems.LEGENDARY_SPEAR.get() || event.getLeft().getItem() == CAItems.HIGHMORE_SCYTHE.get()) && (event.getLeft().getCount() == 1)) {
			Holder<Enchantment> sharpness = CAEnchantments.getHolder(event.getPlayer().level().registryAccess(), Enchantments.SHARPNESS);
			Holder<Enchantment> synesthesia = CAEnchantments.getHolder(event.getPlayer().level().registryAccess(), CAEnchantments.SYNESTHESIA);
			int sharpLevel = EnchantmentHelper.getItemEnchantmentLevel(sharpness, event.getRight());
			int synLevel = EnchantmentHelper.getItemEnchantmentLevel(synesthesia, event.getLeft());
			if (sharpLevel > synLevel) {
				ItemStack output = event.getLeft().copy();
				EnchantmentHelper.updateEnchantments(output, enchantments -> enchantments.removeIf(enchantment -> enchantment.equals(synesthesia)));
				output.enchant(synesthesia, sharpLevel);
				event.setOutput(output);
				event.setCost(sharpLevel);
				event.setMaterialCost(0);
			}
		}
	}

	@SubscribeEvent
	public static void handleCustomAnvilRecipes(AnvilUpdateEvent event) {
		ItemStack leftItem = event.getLeft();
		ItemStack rightItem = event.getRight();

		if (leftItem.isEmpty() || rightItem.isEmpty()) return;

		if (leftItem.is(Items.IRON_SWORD) && rightItem.is(CAItems.KNIGHT_CORPSE.get())) {
			ItemStack output = leftItem.copy();
			CompoundTag nbtTag = leftItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			if (!nbtTag.isEmpty()) {
				CustomData.update(DataComponents.CUSTOM_DATA, output, tag -> tag.merge(nbtTag));
			}
			event.setOutput(output);
			event.setCost(1);
			event.setMaterialCost(1);
			return;
		}

		boolean isTargetWeapon = leftItem.is(CAItems.LEGENDARY_SPEAR.get()) || leftItem.is(CAItems.HIGHMORE_SCYTHE.get());
		if (isTargetWeapon) {
			var registryAccess = event.getPlayer().level().registryAccess();
			Holder<Enchantment> sharpness = CAEnchantments.getHolder(registryAccess, Enchantments.SHARPNESS);
			Holder<Enchantment> synesthesia = CAEnchantments.getHolder(registryAccess, CAEnchantments.SYNESTHESIA);

			int sharpLevel = EnchantmentHelper.getItemEnchantmentLevel(sharpness, rightItem);
			int synLevel = EnchantmentHelper.getItemEnchantmentLevel(synesthesia, leftItem);

			if (sharpLevel > synLevel) {
				ItemStack output = leftItem.copy();
				EnchantmentHelper.updateEnchantments(output, mutable -> mutable.set(synesthesia, sharpLevel));

				event.setOutput(output);
				event.setCost(sharpLevel * 2L);
				event.setMaterialCost(1);
			}
		}
	}

	@SubscribeEvent
	public static void refundSharpnessSourceItemOnTake(AnvilRepairEvent event) {
		Player player = event.getEntity();
		if (!player.level().isClientSide()) {
			ItemStack leftItem = event.getLeft();
			ItemStack rightItem = event.getRight();

			boolean isTargetWeapon = leftItem.is(CAItems.LEGENDARY_SPEAR.get()) || leftItem.is(CAItems.HIGHMORE_SCYTHE.get());
			if (isTargetWeapon) {
				var registryAccess = player.level().registryAccess();
				Holder<Enchantment> sharpness = CAEnchantments.getHolder(registryAccess, Enchantments.SHARPNESS);
				Holder<Enchantment> synesthesia = CAEnchantments.getHolder(registryAccess, CAEnchantments.SYNESTHESIA);
				int sharpLevel = EnchantmentHelper.getItemEnchantmentLevel(sharpness, rightItem);
				int synLevel = EnchantmentHelper.getItemEnchantmentLevel(synesthesia, leftItem);

				if (sharpLevel > synLevel) {
					ItemStack returnedItem = rightItem.copy();
					returnedItem.setCount(1);
					EnchantmentHelper.updateEnchantments(returnedItem, mutable -> mutable.removeIf(holder -> holder.equals(sharpness)));

					ItemHandlerHelper.giveItemToPlayer(player, returnedItem);
				}
			}
		}
	}
}