package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.api.event.SanityEvent;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;


public class RelicCursedGLOWBODYItem extends RelicItemBase {
	public RelicCursedGLOWBODYItem() {
		super(Relic.CURSED_GLOWBODY, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 60;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
        String hoverText = null;
		if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
			list.add(Component.translatable("item.caerula_arbor.cursed.used"));
		}
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (!entity.level().isClientSide())
			entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
		if (world instanceof ServerLevel level)
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 72, 1, 2, 1, 0.1);
		SIHelper.causeSanityInjury(entity, 500, SanityEvent.Hurt.Type.FOOD);
		if (entity instanceof Player player)
			player.getCooldowns().addCooldown(itemstack.getItem(), 200);
		return retval;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);

		boolean isUsed = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
				.copyTag()
				.getBoolean("used");

		if (!isUsed) {
			PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
			if (!Relic.CURSED_GLOWBODY.gained(capability)) {
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();

				world.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundSource.NEUTRAL, 2.0F, 1.0F);

				if (world instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 99, 1.0, 1.0, 1.0, 1.0);
				}

				Relic.CURSED_GLOWBODY.set(capability, 1);
				capability.syncPlayerVariables(entity);

				if (world.isClientSide()) {
					Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
				}

				CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
			}
		}
	}
}