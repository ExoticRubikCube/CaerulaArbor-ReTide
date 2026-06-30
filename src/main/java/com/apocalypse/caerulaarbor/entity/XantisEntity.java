package com.apocalypse.caerulaarbor.entity;

import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.util.EntityPredicateUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class XantisEntity extends TamableAnimal implements GeoEntity, SyncedAnimationEntity {

	private boolean isXantisTapative() {
		return EntityPredicateUtils.isXantisTapative(this);
	}
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(XantisEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(XantisEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(XantisEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_NIUBI = SynchedEntityData.defineId(XantisEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_TAP_TICK = SynchedEntityData.defineId(XantisEntity.class, EntityDataSerializers.INT);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";

	public XantisEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.XANTIS.get(), world);
	}

	public XantisEntity(EntityType<XantisEntity> type, Level world) {
		super(type, world);
		xpReward = 512;
		setNoAi(false);
		setMaxUpStep(1.25f);
		setPersistenceRequired();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SHOOT, false);
		this.entityData.define(ANIMATION, "undefined");
		this.entityData.define(TEXTURE, "xantis");
		this.entityData.define(DATA_NIUBI, true);
		this.entityData.define(DATA_TAP_TICK, 0);
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
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 2.25;
			}
		});
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.goalSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
		this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1, (float) 3, (float) 24, false));
		this.targetSelector.addGoal(5, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(6, new FollowMobGoal(this, 1, (float) 16, (float) 12));
		this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && isXantisTapative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isXantisTapative();
			}
		});
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return super.canUse() && isXantisTapative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && isXantisTapative();
			}
		});
		this.goalSelector.addGoal(9, new FloatGoal(this));
	}

	@Override
	public MobType getMobType() {
		return MobType.WATER;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.AXOLOTL_ATTACK;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.AXOLOTL_DEATH;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
        boolean result = false;
        Entity sourceentity = source.getEntity();
        if (sourceentity != null) {
            result = sourceentity instanceof LivingEntity _entity && _entity.isHolding(CAItems.APOCATA_SWORD.get());
        }
        if (result){
            this.setNoNiubi();
            return super.hurt(source,114514);
        }
        if (this.isNiubi()) return super.hurt(source, 0.1F);
        return super.hurt(source, amount);
	}

	@Override
    protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount){
    	if (!isNiubi()) super.actuallyHurt(pDamageSource, pDamageAmount);
        else super.actuallyHurt(pDamageSource, (float) Math.min(0.1, pDamageAmount));
    }

	@Override
    public void die(@NotNull DamageSource pSource){
        if (isNiubi()){
			this.hurt(this.level().damageSources().fellOutOfWorld(), 0.1F);
			return;
        }
        super.die(pSource);
    }

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataNIUBI", this.entityData.get(DATA_NIUBI));
		compound.putInt("DataTAP_TICK", this.entityData.get(DATA_TAP_TICK));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture"))
			this.setTexture(compound.getString("Texture"));
		if (compound.contains("DataNIUBI"))
			this.entityData.set(DATA_NIUBI, compound.getBoolean("DataNIUBI"));
		if (compound.contains("DataTAP_TICK"))
			this.entityData.set(DATA_TAP_TICK, compound.getInt("DataTAP_TICK"));
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		ItemStack itemstack = sourceentity.getItemInHand(hand);
        this.level().isClientSide();
        InteractionResult retval;
		Item item = itemstack.getItem();
		if (itemstack.getItem() instanceof SpawnEggItem) {
            super.mobInteract(sourceentity, hand);
        } else if (this.level().isClientSide()) {
            if ((this.isTame() && this.isOwnedBy(sourceentity) || this.isFood(itemstack))) {
                this.level().isClientSide();
            }
        } else {
			if (this.isTame()) {
				if (this.isOwnedBy(sourceentity)) {
					if (item.isEdible() && this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal((float) item.getFoodProperties().getNutrition());
                        this.level().isClientSide();
                    } else if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						this.usePlayerItem(sourceentity, hand, itemstack);
						this.heal(4);
                        this.level().isClientSide();
                    } else {
                        super.mobInteract(sourceentity, hand);
                    }
				}
			} else if (this.isFood(itemstack)) {
				this.usePlayerItem(sourceentity, hand, itemstack);
				if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, sourceentity)) {
					this.tame(sourceentity);
					this.level().broadcastEntityEvent(this, (byte) 7);
				} else {
					this.level().broadcastEntityEvent(this, (byte) 6);
				}
				this.setPersistenceRequired();
                this.level().isClientSide();
            } else {
				retval = super.mobInteract(sourceentity, hand);
				if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
					this.setPersistenceRequired();
			}
		}
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		Entity entity = this;
		Level world = this.level();
        double tapTick;
        boolean isNiubi;
        tapTick = entity instanceof XantisEntity _datEntI ? _datEntI.getEntityData().get(DATA_TAP_TICK) : 0;
        if ((Entity) sourceentity instanceof LivingEntity _entity && _entity.isHolding(CAItems.BANNED_ITEM.get())) {
            isNiubi = entity instanceof XantisEntity _datEntL2 && _datEntL2.getEntityData().get(DATA_NIUBI);
            if (isNiubi) {
                if (entity instanceof XantisEntity _datEntSetL)
                    _datEntSetL.getEntityData().set(DATA_NIUBI, false);
                if ((LevelAccessor) world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.LARGE_SMOKE, x, (y + 0.5), z, 32, 0.5, 0.5, 0.5, 0.1);
                return InteractionResult.SUCCESS;
            }
        }
        if (tapTick <= 0) {
            if (entity instanceof XantisEntity) {
                ((XantisEntity) entity).setAnimation("animation.xantis.tap");
            }
            if (entity instanceof XantisEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_TAP_TICK, 9);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public void baseTick() {
		super.baseTick();
        LevelAccessor world = this.level();
        double tapTick;
        if (tickCount % 40 == 0) {
            {
                final Vec3 _center = new Vec3(this.getX(), this.getY(), this.getZ());
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator instanceof XantisEntity) {
                        continue;
                    }
                    if (entityiterator instanceof Mob _entity && (Entity) this instanceof LivingEntity _ent)
                        _entity.setTarget(_ent);
                }
            }
        }
        tapTick = (Entity) this instanceof XantisEntity _datEntI ? _datEntI.getEntityData().get(DATA_TAP_TICK) : 0;
        if (tapTick > 0) {
            if ((Entity) this instanceof XantisEntity _datEntSetI)
                _datEntSetI.getEntityData().set(DATA_TAP_TICK, (int) (tapTick - 1));
        }
        AttributeInstance MAX_H = this.getAttribute(Attributes.MAX_HEALTH);
        if (MAX_H != null && MAX_H.getBaseValue() != 10) MAX_H.setBaseValue(10);
		this.refreshDimensions();
		if (this.getY() < -99) this.setPosRaw(this.getX(), 256, this.getZ());
	}

	public int MAX_DIST = 16777216;
    @Override
    public void setPos(double x, double y, double z){
        if (this.distanceToSqr(x,y,z) > MAX_DIST) return;
        super.setPos(x,y,z);
    }
    @Override
    public void moveTo(double x, double y, double z, float pYRot, float pXRot){
        if (this.distanceToSqr(x,y,z) > MAX_DIST) return;
        super.moveTo(x,y,z,pYRot,pXRot);
    }

	@Override
    public void setHealth(float pHealth){
        if (isNiubi() && pHealth < 1) {
            if(this.getHealth() > 1) super.setHealth(1);
            else super.setHealth(this.getMaxHealth());
            return;
        }
        super.setHealth(pHealth);
    }

	@Override
	public EntityDimensions getDimensions(Pose p_33597_) {
		return super.getDimensions(p_33597_).scale((float) 1);
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageable) {
		XantisEntity retval = CAEntities.XANTIS.get().create(serverWorld);
		retval.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(retval.blockPosition()), MobSpawnType.BREEDING, null, null);
		return retval;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return Objects.equals(CAItems.APOCALYPSE.get(), stack.getItem());
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
	}

	public static void init() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
		builder = builder.add(Attributes.MAX_HEALTH, 10);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1);
		return builder;
	}

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F))) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.xantis.move"));
			}
			if (this.isDeadOrDying()) {
				return event.setAndContinue(RawAnimation.begin().thenPlay("animation.xantis.die"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.xantis.idle"));
		}
		return PlayState.STOP;
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
			this.swinging = true;
			this.lastSwing = level().getGameTime();
		}
		if (this.swinging && this.lastSwing + 9L <= level().getGameTime()) {
			this.swinging = false;
		}
		if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.xantis.attack"));
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
		if (isNiubi()){
            this.deathTime = 0;
            this.setHealth(10);
            return;
        }
		++this.deathTime;
		if (this.deathTime >= 19) {
			this.remove(XantisEntity.RemovalReason.KILLED);
			this.dropExperience();
		}
	}

	@Override
    public void remove(@NotNull RemovalReason pReason){
        if (isNiubi() && (pReason == RemovalReason.DISCARDED || pReason == RemovalReason.KILLED)){
            this.hurt(this.level().damageSources().fellOutOfWorld(), 0.1F);
            return;
        }
        super.remove(pReason);
    }

	public String getSyncedAnimation() {
		return this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
		data.add(new AnimationController<>(this, "attacking", 0, this::attackingPredicate));
		data.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	public boolean isNiubi(){return this.entityData.get(DATA_NIUBI);}
	public void setNoNiubi(){this.entityData.set(DATA_NIUBI, false);}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
