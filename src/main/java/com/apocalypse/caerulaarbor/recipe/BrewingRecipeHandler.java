package com.apocalypse.caerulaarbor.recipe;

import com.apocalypse.caerulaarbor.init.CAPotions;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrewingRecipeHandler {
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// Fast Swim
			addPotionRecipe(Potions.AWKWARD, CAItems.CORAL_FEET.get(), CAPotions.FAST_SWIM_POTION.get());
			addPotionRecipe(CAPotions.FAST_SWIM_POTION.get(), Items.REDSTONE, CAPotions.FAST_SWIM_POTION_LONG.get());
			addPotionRecipe(CAPotions.FAST_SWIM_POTION.get(), Items.GLOWSTONE_DUST, CAPotions.FAST_SWIM_POTION_II.get());
			addPotionRecipe(CAPotions.FAST_SWIM_POTION_II.get(), CAItems.CELL_CLUSTER.get(), CAPotions.FAST_SWIM_POTION_III.get());

			// Instant Sanity
			addPotionRecipe(Potions.AWKWARD, CABlocks.TRAIL_MUSHROOM.get(), CAPotions.INST_SANITY.get());
			addPotionRecipe(CAPotions.INST_SANITY.get(), Items.GLOWSTONE_DUST, CAPotions.INST_SANITY_II.get());

			// Sanity Cure
			addPotionRecipe(CAPotions.INST_SANITY.get(), CAItems.TRAIL_APPLE.get(), CAPotions.SANITY_CURE.get());
			addPotionRecipe(CAPotions.SANITY_CURE.get(), Items.GLOWSTONE_DUST, CAPotions.SANITY_CURE_II.get());

			// Sanity Immunity
			addPotionRecipe(CAPotions.SANITY_CURE.get(), CAItems.FERMENTED_OCEAN_EYE.get(), CAPotions.SANITY_IMMUE_POTION.get());
			addPotionRecipe(CAPotions.SANITY_IMMUE_POTION.get(), CAItems.NERVOUS_REGENERATION.get(), CAPotions.LONG_SNT_IMMUE.get());

			// Percentage Regeneration
			addPotionRecipe(Potions.AWKWARD, CAItems.TEAR_ISHARMLA.get(), CAPotions.PERCENTAGE_REGENERATION.get());
			addPotionRecipe(CAPotions.PERCENTAGE_REGENERATION.get(), Items.GLOWSTONE_DUST, CAPotions.PERCENTAGE_REGENERATION_II.get());

			// Make Berry (Item output)
			addItemRecipe(Potions.AWKWARD, Items.SWEET_BERRIES, CAItems.CANNED_CHERRY.get());
		});
	}

	private static void addPotionRecipe(Potion input, ItemLike ingredient, Potion output) {
		BrewingRecipeRegistry.addRecipe(new CustomPotionBrewingRecipe(input, ingredient, output));
	}

	private static void addItemRecipe(Potion input, ItemLike ingredient, ItemLike output) {
		BrewingRecipeRegistry.addRecipe(new CustomItemBrewingRecipe(input, ingredient, output));
	}

	private static class CustomPotionBrewingRecipe implements IBrewingRecipe {
		private final Potion inputPotion;
		private final ItemLike ingredient;
		private final Potion outputPotion;

		public CustomPotionBrewingRecipe(Potion inputPotion, ItemLike ingredient, Potion outputPotion) {
			this.inputPotion = inputPotion;
			this.ingredient = ingredient;
			this.outputPotion = outputPotion;
		}

		@Override
		public boolean isInput(ItemStack input) {
			Item inputItem = input.getItem();
			return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && PotionUtils.getPotion(input) == inputPotion;
		}

		@Override
		public boolean isIngredient(ItemStack ingredientStack) {
			return Ingredient.of(new ItemStack(ingredient)).test(ingredientStack);
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredientStack) {
			if (isInput(input) && isIngredient(ingredientStack)) {
				return PotionUtils.setPotion(new ItemStack(input.getItem()), outputPotion);
			}
			return ItemStack.EMPTY;
		}
	}

	private static class CustomItemBrewingRecipe implements IBrewingRecipe {
		private final Potion inputPotion;
		private final ItemLike ingredient;
		private final ItemLike outputItem;

		public CustomItemBrewingRecipe(Potion inputPotion, ItemLike ingredient, ItemLike outputItem) {
			this.inputPotion = inputPotion;
			this.ingredient = ingredient;
			this.outputItem = outputItem;
		}

		@Override
		public boolean isInput(ItemStack input) {
			Item inputItem = input.getItem();
			return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && PotionUtils.getPotion(input) == inputPotion;
		}

		@Override
		public boolean isIngredient(ItemStack ingredientStack) {
			return Ingredient.of(new ItemStack(ingredient)).test(ingredientStack);
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredientStack) {
			if (isInput(input) && isIngredient(ingredientStack)) {
				return new ItemStack(outputItem);
			}
			return ItemStack.EMPTY;
		}
	}
}
