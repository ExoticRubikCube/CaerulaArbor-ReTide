package com.susen36.caerulaarbor.recipe;

import com.susen36.caerulaarbor.init.CABlocks;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAPotions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber
public class BrewingRecipeHandler {
	@SubscribeEvent
	public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
		PotionBrewing.Builder builder = event.getBuilder();
		// 快速游泳
		addPotionRecipe(builder, Potions.AWKWARD, CAItems.CORAL_FEET.get(), CAPotions.FAST_SWIM_POTION);
		addPotionRecipe(builder, CAPotions.FAST_SWIM_POTION, Items.REDSTONE, CAPotions.FAST_SWIM_POTION_LONG);
		addPotionRecipe(builder, CAPotions.FAST_SWIM_POTION, Items.GLOWSTONE_DUST, CAPotions.FAST_SWIM_POTION_II);
		addPotionRecipe(builder, CAPotions.FAST_SWIM_POTION_II, CAItems.CELL_CLUSTER.get(), CAPotions.FAST_SWIM_POTION_III);

		// 瞬间理智
		addPotionRecipe(builder, Potions.AWKWARD, CABlocks.TRAIL_MUSHROOM.get(), CAPotions.INST_SANITY);
		addPotionRecipe(builder, CAPotions.INST_SANITY, Items.GLOWSTONE_DUST, CAPotions.INST_SANITY_II);

		// 理智治愈
		addPotionRecipe(builder, CAPotions.INST_SANITY, CAItems.TRAIL_APPLE.get(), CAPotions.SANITY_CURE);
		addPotionRecipe(builder, CAPotions.SANITY_CURE, Items.GLOWSTONE_DUST, CAPotions.SANITY_CURE_II);

		// 理智免疫
		addPotionRecipe(builder, CAPotions.SANITY_CURE, CAItems.FERMENTED_OCEAN_EYE.get(), CAPotions.SANITY_IMMUE_POTION);
		addPotionRecipe(builder, CAPotions.SANITY_IMMUE_POTION, CAItems.NERVOUS_REGENERATION.get(), CAPotions.LONG_SNT_IMMUE);

		// 百分比再生
		addPotionRecipe(builder, Potions.AWKWARD, CAItems.TEAR_ISHARMLA.get(), CAPotions.PERCENTAGE_REGENERATION);
		addPotionRecipe(builder, CAPotions.PERCENTAGE_REGENERATION, Items.GLOWSTONE_DUST, CAPotions.PERCENTAGE_REGENERATION_II);

		// 制作浆果（物品产出）
		addItemRecipe(builder, Potions.AWKWARD, Items.SWEET_BERRIES, CACollectible.CANNED_CHERRY.get());
	}

	private static void addPotionRecipe(PotionBrewing.Builder builder, Holder<Potion> input, ItemLike ingredient, Holder<Potion> output) {
		builder.addMix(input, ingredient.asItem(), output);
	}

	private static void addItemRecipe(PotionBrewing.Builder builder, Holder<Potion> input, ItemLike ingredient, ItemLike output) {
		builder.addRecipe(new CustomItemBrewingRecipe(input, ingredient, output));
	}

	private static class CustomPotionBrewingRecipe implements IBrewingRecipe {
		private final Holder<Potion> inputPotion;
		private final ItemLike ingredient;
		private final Holder<Potion> outputPotion;

		public CustomPotionBrewingRecipe(Holder<Potion> inputPotion, ItemLike ingredient, Holder<Potion> outputPotion) {
			this.inputPotion = inputPotion;
			this.ingredient = ingredient;
			this.outputPotion = outputPotion;
		}

		@Override
		public boolean isInput(ItemStack input) {
			Item inputItem = input.getItem();
			PotionContents contents = input.get(DataComponents.POTION_CONTENTS);
			return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && contents != null && contents.potion().map(potion -> potion.equals(inputPotion)).orElse(false);
		}

		@Override
		public boolean isIngredient(ItemStack ingredientStack) {
			return Ingredient.of(new ItemStack(ingredient)).test(ingredientStack);
		}

		@Override
		public ItemStack getOutput(ItemStack input, ItemStack ingredientStack) {
			if (isInput(input) && isIngredient(ingredientStack)) {
				ItemStack result = new ItemStack(input.getItem());
				result.set(DataComponents.POTION_CONTENTS, new PotionContents(outputPotion));
				return result;
			}
			return ItemStack.EMPTY;
		}
	}

	private static class CustomItemBrewingRecipe implements IBrewingRecipe {
		private final Holder<Potion> inputPotion;
		private final ItemLike ingredient;
		private final ItemLike outputItem;

		public CustomItemBrewingRecipe(Holder<Potion> inputPotion, ItemLike ingredient, ItemLike outputItem) {
			this.inputPotion = inputPotion;
			this.ingredient = ingredient;
			this.outputItem = outputItem;
		}

		@Override
		public boolean isInput(ItemStack input) {
			Item inputItem = input.getItem();
			PotionContents contents = input.get(DataComponents.POTION_CONTENTS);
			return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && contents != null && contents.potion().map(potion -> potion.equals(inputPotion)).orElse(false);
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