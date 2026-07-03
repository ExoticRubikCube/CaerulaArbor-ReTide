
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.OceanizedHorseEntity;
import com.apocalypse.caerulaarbor.entity.ReaperFishEntity;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OceanExtractorItem extends Item {
	public OceanExtractorItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack(CAItems.OCEAN_EXTRACTOR.get());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.ocean_extractor.description_0"));
	}

	@Override
	public @NotNull InteractionResult interactLivingEntity(
			@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand
	) {
		ItemStack reward = ItemStack.EMPTY;
		if (target instanceof ReaperFishEntity) {
			reward = new ItemStack(CAItems.DNA_REAPER.get());
		} else if (target instanceof OceanizedHorseEntity) {
			reward = new ItemStack(CAItems.DNA_HORSE.get());
		}

		if (reward.isEmpty()) {
			return InteractionResult.PASS;
		}

		Level level = player.level();
		if (level.isClientSide()) {
			return InteractionResult.sidedSuccess(true);
		}

		ItemHandlerHelper.giveItemToPlayer(player, reward);
		stack.shrink(1);
		target.hurt(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
				.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "extractor_damage"))), player), 0.5F);
		return InteractionResult.sidedSuccess(false);
	}
}
