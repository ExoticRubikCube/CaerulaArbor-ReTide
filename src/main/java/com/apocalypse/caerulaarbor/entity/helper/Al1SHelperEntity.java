package com.apocalypse.caerulaarbor.entity.helper;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class Al1SHelperEntity extends LittleHelperEntity {
	public Al1SHelperEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.AL_1_S_HELPER.get(), world);
	}

	public Al1SHelperEntity(EntityType<? extends LittleHelperEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected String getDefaultTexture() {
		return "little_helper_al1s";
	}

	@Override
	protected InteractionResult handleApocalypseInteract(Player sourceentity) {
		if (this.level() instanceof ServerLevel serverLevel && sourceentity.isHolding(CAItems.APOCALYPSE.get())) {
			serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 32, 0.75, 0.75, 0.75, 0.15);
			this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "al1s_spec")), SoundSource.BLOCKS, 3, 1);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void playPassengerLeftClickSound(Player passenger) {
		if (!this.level().isClientSide()) {
			this.level().playSound(null, BlockPos.containing(passenger.getX(), passenger.getY(), passenger.getZ()),
					ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "al1s_work")), SoundSource.BLOCKS, 3, 1);
		}
	}

	@Override
	protected ItemStack getRecycleItemStack() {
		return new ItemStack(CAItems.ITEM_HELPER_AL_1S.get());
	}

	@Override
	@Nullable
	protected ResourceLocation getCustomDeathSound() {
		return null;
	}
}
