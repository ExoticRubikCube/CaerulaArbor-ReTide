package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class OilAndCreamItem extends Item {
	public OilAndCreamItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(1).saturationModifier(0f).alwaysEdible().build()));
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
		list.add(Component.translatable("item.caerula_arbor.oil_and_cream.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(CAItems.EMPTY_CAN.get());
		super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        {
            double setval = 0;
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.disoclusion = setval;
            capability.syncPlayerVariables(entity);
        }
        {
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            double setval = Math.max(capability.player_light - 30, 0);
            capability.player_light = setval;
            capability.syncPlayerVariables(entity);
        }
        entity.hurt(entity.level().damageSources().inFire(), 12);
        if (!entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1200, 0));
        }
        entity.igniteForSeconds(8);
        if (world instanceof ServerLevel level) {
            ItemEntity entityToSpawn = new ItemEntity(level, x, y, z, new ItemStack(Items.STICK));
            entityToSpawn.setPickUpDelay(10);
            level.addFreshEntity(entityToSpawn);
        }
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