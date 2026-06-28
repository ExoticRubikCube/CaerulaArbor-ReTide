package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.base.SeaMonster;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class OceanizedSheepEntity extends SeaMonster {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(OceanizedSheepEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(OceanizedSheepEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(OceanizedSheepEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_fur = SynchedEntityData.defineId(OceanizedSheepEntity.class, EntityDataSerializers.BOOLEAN);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public OceanizedSheepEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CaerulaArborModEntities.OCEANIZED_SHEEP.get(), world);
	}

	public OceanizedSheepEntity(EntityType<OceanizedSheepEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setMaxUpStep(0.6f);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "oceanized_sheep");
		this.entityData.define(DATA_fur, true);
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return this.entityData.get(TEXTURE);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.4, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 4;
			}
		});
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true, false));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true, false));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, false));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Illusioner.class, true, false));
		this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Pillager.class, true, false));
		this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Vindicator.class, true, false));
		this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Witch.class, true, false));
		this.targetSelector.addGoal(10, new NearestAttackableTargetGoal<>(this, Piglin.class, true, false));
		this.targetSelector.addGoal(11, new NearestAttackableTargetGoal<>(this, PiglinBrute.class, true, false));
		this.targetSelector.addGoal(12, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true, false));
		this.targetSelector.addGoal(13, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			@Override
			public boolean canUse() {
				double x = OceanizedSheepEntity.this.getX();
				double y = OceanizedSheepEntity.this.getY();
				double z = OceanizedSheepEntity.this.getZ();
				Entity entity = OceanizedSheepEntity.this;
				Level world = OceanizedSheepEntity.this.level();
				return super.canUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedSheepEntity.this.getX();
				double y = OceanizedSheepEntity.this.getY();
				double z = OceanizedSheepEntity.this.getZ();
				Entity entity = OceanizedSheepEntity.this;
				Level world = OceanizedSheepEntity.this.level();
				return super.canContinueToUse() && EntityUtils.isOceanizedPlayerNearby(world, x, y, z);
			}
		});
		this.targetSelector.addGoal(14, new NearestAttackableTargetGoal(this, Animal.class, true, false) {
			@Override
			public boolean canUse() {
				double x = OceanizedSheepEntity.this.getX();
				double y = OceanizedSheepEntity.this.getY();
				double z = OceanizedSheepEntity.this.getZ();
				Entity entity = OceanizedSheepEntity.this;
				Level world = OceanizedSheepEntity.this.level();
				return super.canUse() && EntityUtils.canAttackAnimals();
			}

			@Override
			public boolean canContinueToUse() {
				double x = OceanizedSheepEntity.this.getX();
				double y = OceanizedSheepEntity.this.getY();
				double z = OceanizedSheepEntity.this.getZ();
				Entity entity = OceanizedSheepEntity.this;
				Level world = OceanizedSheepEntity.this.level();
				return super.canContinueToUse() && EntityUtils.canAttackAnimals();
			}
		});
		this.goalSelector.addGoal(15, new RandomStrollGoal(this, 0.4));
		this.goalSelector.addGoal(16, new FloatGoal(this));
		this.goalSelector.addGoal(17, new RandomLookAroundGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.sheep.ambient"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.sheep.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.sheep.death"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("Datafur", this.entityData.get(DATA_fur));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("Datafur"))
			this.entityData.set(DATA_fur, compound.getBoolean("Datafur"));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		super.mobInteract(sourceentity, hand);
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
        if (entity == null || sourceentity == null)
            return InteractionResult.PASS;
        if (((Entity) sourceentity instanceof LivingEntity _entity && _entity.isHolding(Items.SHEARS)) && entity instanceof OceanizedSheepEntity _datEntL1 && _datEntL1.getEntityData().get(DATA_fur)) {
            if (entity instanceof OceanizedSheepEntity _datEntSetL)
                _datEntSetL.getEntityData().set(DATA_fur, false);
            if (entity instanceof OceanizedSheepEntity animatable)
                animatable.setTexture("oceanized_sheep_furless");
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.sheep.shear")), SoundSource.PLAYERS, 1, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel _level) {
                ItemEntity entityToSpawn = new ItemEntity(_level, x, (y + 0.65), z, new ItemStack(Blocks.WHITE_WOOL));
                entityToSpawn.setPickUpDelay(10);
                _level.addFreshEntity(entityToSpawn);
            }
            for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 1, 3); index0++) {
                if ((LevelAccessor) world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, x, (y + 0.65), z, new ItemStack(CaerulaArborModItems.SEA_TRAIL_MOR.get()));
                    entityToSpawn.setPickUpDelay(10);
                    _level.addFreshEntity(entityToSpawn);
                }
            }
            EntityUtils.deductSanity(sourceentity, 64);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this != null) {
            if (!((Entity) this instanceof OceanizedSheepEntity _datEntL0 && _datEntL0.getEntityData().get(DATA_fur))) {
                if ((world.getBlockState(BlockPos.containing(x + getLookAngle().x, y, z + getLookAngle().z))).getBlock() == CaerulaArborModBlocks.TRAIL_MUSHROOM.get()) {
                    if (this instanceof OceanizedSheepEntity) {
                        this.setAnimation("animation.oceanized_sheep.eat");
                    }
                    if ((Entity) this instanceof OceanizedSheepEntity _datEntSetL)
                        _datEntSetL.getEntityData().set(DATA_fur, true);
                    world.setBlock(BlockPos.containing(x + getLookAngle().x, y, z + getLookAngle().z), Blocks.AIR.defaultBlockState(), 3);
                    CaerulaArborMod.queueServerWork(20, () -> {
                        if ((Entity) this instanceof OceanizedSheepEntity _datEntL10 && _datEntL10.getEntityData().get(DATA_fur)) {
                            if ((Entity) this instanceof OceanizedSheepEntity animatable)
                                animatable.setTexture("oceanized_sheep");
                        }
                    });
                }
            }
        }
        this.refreshDimensions();
	}

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
		builder = builder.add(Attributes.MAX_HEALTH, 16);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 3);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))

			) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_sheep.move"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.oceanized_sheep.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 18L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.oceanized_sheep.attack"));
		}
		return PlayState.CONTINUE;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;
		if (this.deathTime == 20) {
			this.remove(OceanizedSheepEntity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 1, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 1, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 1, this::procedurePredicate));
	}
}
