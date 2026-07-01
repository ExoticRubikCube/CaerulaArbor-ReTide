package com.apocalypse.caerulaarbor.entity.warden;

import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class OceanizedWardenEntity extends AbstractOceanizedWardenEntity {
	public OceanizedWardenEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(CAEntities.OCEANIZED_WARDEN.get(), world);
	}

	public OceanizedWardenEntity(EntityType<OceanizedWardenEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(OceanizedWardenEntity.this, 2, true) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 5.0625;
			}

			@Override
			public boolean canUse() {
				return super.canUse() && OceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && OceanizedWardenEntity.this.isDurative();
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
		this.goalSelector.addGoal(13, new RandomStrollGoal(OceanizedWardenEntity.this, 1) {
			@Override
			public boolean canUse() {
				return super.canUse() && OceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && OceanizedWardenEntity.this.isDurative();
			}
		});
		this.goalSelector.addGoal(14, new LookAtPlayerGoal(this, Player.class, 9F));
		this.goalSelector.addGoal(15, new RandomLookAroundGoal(OceanizedWardenEntity.this) {
			@Override
			public boolean canUse() {
				return super.canUse() && OceanizedWardenEntity.this.isDurative();
			}

			@Override
			public boolean canContinueToUse() {
				return super.canContinueToUse() && OceanizedWardenEntity.this.isDurative();
			}
		});
	}

	@Override
	protected String getDefaultTexture() {
		return "oceanized_warden";
	}

	@Override
	protected String getAmbientSoundId() {
		return "entity.warden.ambient";
	}

	@Override
	protected String getHurtSoundId() {
		return "entity.warden.hurt";
	}

	@Override
	protected String getDeathSoundId() {
		return "entity.warden.death";
	}

	@Override
	protected String getAnimationPrefix() {
		return "animation.oceanized_warden";
	}

	@Override
	protected int getAttackAnimationLength() {
		return 18;
	}

	@Override
	protected int getInitialHeartbeatGap() {
		return 40;
	}
}
