package com.susen36.caerulaarbor.item;

import com.susen36.babel.util.EPUtils;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CADamageTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


public class MutagenisisCapsuleItem extends Item {
	public MutagenisisCapsuleItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(1f).alwaysEdible().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.mutagenisis_capsule.description_0"));
		list.add(Component.translatable("item.caerula_arbor.mutagenisis_capsule.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
        double ocean;
        ocean = ModCapabilities.getPlayerVariables(entity).player_oceanization;
        if (ocean < 2.9) {
            EPUtils.causeSanityInjury(entity, (ocean + 1) * 2);
            entity.hurt(CADamageTypes.source(world, CADamageTypes.OCEANIZE_DAMAGE), (float) (3 * (ocean + 1)));
            if (!entity.level().isClientSide()) {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, (int) ocean));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, (int) ocean));
            }
            if (entity.isAlive()) {
                {
                    double setval = ocean + 1;
                    PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                    capability.player_oceanization = setval;
                    capability.syncPlayerVariables(entity);
                }
            }
            if (ocean + 1 > 2.9) {
                if ((Entity) entity instanceof ServerPlayer player) {
                    AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "they_shall_pay"));
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
            } else {
                if ((Entity) entity instanceof ServerPlayer player) {
                    AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "they_shall_welcome"));
                    AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                    if (!ap.isDone()) {
                        for (String criteria : ap.getRemainingCriteria())
                            player.getAdvancements().award(adv, criteria);
                    }
                }
            }
        } else if ((Entity) entity instanceof ServerPlayer player) {
            AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "they_shall_welcome"));
            AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
            if (!ap.isDone()) {
                for (String criteria : ap.getRemainingCriteria())
                    player.getAdvancements().award(adv, criteria);
            }
        }
        return retval;
	}
}