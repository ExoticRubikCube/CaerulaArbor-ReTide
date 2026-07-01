package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
//TODO 需要清理，记得清理渲染器和贴图以及模型
public class DamageTesterEntity extends PathfinderMob {
	public DamageTesterEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.DAMAGE_TESTER.get(), world);
	}

	public DamageTesterEntity(EntityType<DamageTesterEntity> type, Level world) {
		super(type, world);
		setMaxUpStep(0.6f);
		xpReward = 0;
		setNoAi(false);
		setPersistenceRequired();
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(3, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public double getMyRidingOffset() {
		return -0.35D;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
	}

	ResourceKey<DamageType> INV_KILLER = ResourceKey.create(Registries.DAMAGE_TYPE
			, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer"));

	boolean shouldDie = false;

	@Override
	public void actuallyHurt(DamageSource pSource, float pAmount) {
		Level level = this.level();
		if (!level.isClientSide() && level.getServer() != null) {
			level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Source: " + pSource.getMsgId()), false);
		}
		if (pSource.is(DamageTypes.FELL_OUT_OF_WORLD) || pSource.is(DamageTypes.GENERIC_KILL) || pSource.is(INV_KILLER)) {
			this.shouldDie = true;
		}
		super.actuallyHurt(pSource, pAmount);
	}

	@Override
	public void setHealth(float pHealth) {
		if (shouldDie) super.setHealth(pHealth);
		else super.setHealth(this.getMaxHealth());
		float pAmount = this.getHealth() - pHealth;
		if (pAmount <= 0) return;
		Level level = this.level();
		if (!level.isClientSide() && level.getServer() != null) {
			level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Amount:" + pAmount), false);
		}
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		super.mobInteract(sourceentity, hand);
		Entity entity = this;
		Level world = this.level();
		if (sourceentity.isHolding(CAItems.APOCALYPSE.get()) || sourceentity.isHolding(CAItems.BANNED_ITEM.get())) {
			entity.hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer")))), 114514);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.1);
		builder = builder.add(Attributes.MAX_HEALTH, 1000000);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		return builder;
	}
}
