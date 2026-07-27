
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
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
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 32;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.nethersea_stimutant.description_0"));
		list.add(Component.translatable("item.caerula_arbor.nethersea_stimutant.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
        if (!entity.level().isClientSide()) {
            entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_ATTACK_PERCLY, 280, 3));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 300, 3));
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 480, 0));
        }
        SIHelper.causeSanityInjury(entity, 75, SanityEvent.Hurt.Type.FOOD);
        if (!(entity instanceof Player)) {
            resultStack.shrink(1);
            ItemStack emptyCup = new ItemStack(CAItems.OCEANGLASS_CUP.get());
            if (resultStack.isEmpty()) {
                return emptyCup;
            }
        } else if (entity instanceof Player player && !player.getAbilities().instabuild) {
            resultStack.shrink(1);
            ItemStack emptyCup = new ItemStack(CAItems.OCEANGLASS_CUP.get());
            if (resultStack.isEmpty()) {
                return emptyCup;
            }
            if (!player.getInventory().add(emptyCup)) {
                player.drop(emptyCup, false);
            }
        }
        CaerulaArborMod.queueServerWork(240, () -> {
            if (entity.isAlive() && entity.hasEffect(CAMobEffects.ADD_ATTACK_PERCLY)) {
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
        return resultStack;
	}
}