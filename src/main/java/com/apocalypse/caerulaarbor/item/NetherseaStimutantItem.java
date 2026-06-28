
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class NetherseaStimutantItem extends Item {
	public NetherseaStimutantItem() {
		super(new Item.Properties().stacksTo(4).rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.nethersea_stimutant.description_0"));
		list.add(Component.translatable("item.caerula_arbor.nethersea_stimutant.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (entity != null) {
			if (!entity.level().isClientSide()) {
				entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_ATTACK_PERCLY.get(), 280, 3));
				entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 2));
				entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 300, 3));
				entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 480, 0));
			}
			SIHelper.causeSanityInjury(entity, 75, SanityEvent.Hurt.Type.FOOD);
			if (!(entity instanceof Player)) {
				resultStack.shrink(1);
				ItemStack emptyCup = new ItemStack(CaerulaArborModItems.OCEANGLASS_CUP.get());
				if (resultStack.isEmpty()) {
					return emptyCup;
				}
			} else if (entity instanceof Player player && !player.getAbilities().instabuild) {
				resultStack.shrink(1);
				ItemStack emptyCup = new ItemStack(CaerulaArborModItems.OCEANGLASS_CUP.get());
				if (resultStack.isEmpty()) {
					return emptyCup;
				}
				if (!player.getInventory().add(emptyCup)) {
					player.drop(emptyCup, false);
				}
			}
			CaerulaArborMod.queueServerWork(240, () -> {
				if (entity.isAlive() && entity.hasEffect(CaerulaArborModMobEffects.ADD_ATTACK_PERCLY.get())) {
					if (Math.random() < 0.5) {
						if (!entity.level().isClientSide()) {
							entity.addEffect(new MobEffectInstance(MobEffects.POISON, 280, 0));
						}
					} else if (!entity.level().isClientSide()) {
						entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 280, 0));
					}
					SIHelper.causeSanityInjury(entity, 125, SanityEvent.Hurt.Type.FOOD);
				}
			});
		}
		return resultStack;
	}
}
