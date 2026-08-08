package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class TulipMedcineItem extends Item {
	public TulipMedcineItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(2f).alwaysEdible().build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 60;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.tulip_medcine.description_0"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(CAItems.OCEAN_PHLOEM.get());
		super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if (world instanceof ServerLevel level)
			level.sendParticles(ParticleTypes.CLOUD, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.15);
		double setval = 0;
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		capability.disoclusion = setval;
		capability.syncPlayerVariables(entity);
        if (!entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 2));
        }
        if (entity instanceof ServerPlayer player) {
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