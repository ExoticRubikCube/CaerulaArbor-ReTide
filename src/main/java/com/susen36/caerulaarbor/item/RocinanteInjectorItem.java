package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.OceanizedHorseEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class RocinanteInjectorItem extends Item {
	public RocinanteInjectorItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public @NotNull InteractionResult interactLivingEntity(
			@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand
	) {
		if (!(target instanceof OceanizedHorseEntity)) {
			return InteractionResult.PASS;
		}

		Level level = player.level();
		if (level.isClientSide()) {
			return InteractionResult.sidedSuccess(true);
		}

		double targetX = target.getX();
		double targetY = target.getY();
		double targetZ = target.getZ();
		stack.shrink(1);
		level.playSound(null, BlockPos.containing(targetX, targetY, targetZ), SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.NEUTRAL, 2, 1);
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, targetX, targetY + 1, targetZ, 32, 1, 1, 1, 1);
		}
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(CAItems.OCEAN_EXTRACTOR.get()));
		target.discard();
		if (level instanceof ServerLevel serverLevel) {
			Entity entityToSpawn = CAEntities.ROCINANTE.get().spawn(serverLevel, BlockPos.containing(targetX, targetY, targetZ), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setYRot(level.getRandom().nextFloat() * 360F);
			}
		}
		return InteractionResult.sidedSuccess(false);
	}
}