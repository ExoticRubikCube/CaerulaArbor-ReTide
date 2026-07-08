package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.manager.TransformManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class OceanizeCatalystItem extends Item {
	private static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses"));
	private static final TagKey<EntityType<?>> CANNOT_TRANSFORM = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "cannot_transform"));

	public OceanizeCatalystItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
	}

	@Override
	public @NotNull InteractionResult interactLivingEntity(
			@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand
	) {
		if (!(target instanceof Mob mob) || !target.isAlive()) {
			return InteractionResult.PASS;
		}

		if (target.getType().is(BOSSES) && mob.getHealth() > player.getHealth() * 4.5F) {
			return InteractionResult.PASS;
		}

		if (target.getType().is(CANNOT_TRANSFORM) || target.getMobType() == MobType.UNDEAD || target.isBaby()) {
			return InteractionResult.PASS;
		}

		Level level = player.level();
		if (level.isClientSide()) {
			return InteractionResult.sidedSuccess(true);
		}

		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		double healthPercentage = 1 - target.getHealth() / target.getMaxHealth();
		if (Math.random() < healthPercentage + 0.05 && TransformManager.transformToSeaborn(level, targetX, targetY, targetZ, target)) {
			target.discard();
			stack.shrink(1);
			return InteractionResult.sidedSuccess(false);
		}

		double damageToHealth = Math.min(target.getHealth() * 0.33, player.getHealth() * 1.5);
		level.playSound(null, BlockPos.containing(targetX, targetY, targetZ), SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 1, 1);
		target.hurt(CADamageTypes.source(level, CADamageTypes.EXTRACTOR_DAMAGE), 0.5F);
		SIHelper.causeSanityInjury(target, player, 256, SanityEvent.Hurt.Type.ENTITY);
		target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
		target.setHealth((float) Math.max(target.getHealth() - damageToHealth, 0.5));
		stack.shrink(1);
		return InteractionResult.sidedSuccess(false);
	}
}
