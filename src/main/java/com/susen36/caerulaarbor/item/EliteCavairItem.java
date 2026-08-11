package com.susen36.caerulaarbor.item;

import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class EliteCavairItem extends Item {
	public EliteCavairItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(11).saturationModifier(0.75f).alwaysEdible().build()));
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack(CAItems.EMPTY_CAN.get());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.elite_cavair.description_0"));
		list.add(Component.translatable("item.caerula_arbor.elite_cavair.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(CAItems.EMPTY_CAN.get());
		super.finishUsingItem(itemstack, world, entity);
        if (!entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY, 1200, 3));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 800, 1));
        }
        {
            double setval = 0;
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.disoclusion = setval;
            capability.syncPlayerVariables(entity);
        }
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            double setval = Math.min(capability.player_light + 10, 100);
            capability.player_light = setval;
            capability.syncPlayerVariables(entity);
        }
        EPUtils.causeSanityInjury(entity, 45);
        if ((Entity) entity instanceof ServerPlayer player) {
            AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "but_i_refuse"));
            AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
            if (!ap.isDone()) {
                for (String criteria : ap.getRemainingCriteria())
                    player.getAdvancements().award(adv, criteria);
            }
        }
        if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
	}
}