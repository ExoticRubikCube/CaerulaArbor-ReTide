package com.apocalypse.caerulaarbor.utils;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.SummonFractalProcedure;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

public class EntityUtils {

	private EntityUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void spawnLinkParticles(LevelAccessor world, Entity a, Entity b) {
		if (a == null || b == null || !(world instanceof ServerLevel))
			return;
		ServerLevel level = (ServerLevel) world;
		double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		double dz = a.getZ() - b.getZ();
		for (int i = 0; i < 40; i++) {
			if (Math.random() < 0.1) {
				double t = 0.025 * i;
				level.sendParticles(ParticleTypes.DOLPHIN,
					b.getX() + dx * t,
					b.getY() + dy * t + 1,
					b.getZ() + dz * t,
					3, 0.1, 0.1, 0.1, 0.01);
			}
		}
	}

	public static void assembleFractals(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		double sklp = 0;
		boolean isLingering = entity instanceof LineringPathshaperEntity;
		isLingering = entity instanceof LineringPathshaperEntity;
		if (isLingering) {
			sklp = entity instanceof LineringPathshaperEntity _datEntI ? _datEntI.getEntityData().get(LineringPathshaperEntity.DATA_skillp) : 0;
		} else {
			sklp = entity instanceof RouteShaperEntity _datEntI ? _datEntI.getEntityData().get(RouteShaperEntity.DATA_skillp) : 0;
		}
		if (sklp >= 8) {
			SummonFractalProcedure.execute(world, x, y, z, entity);
			if (Math.random() < 0.33) {
				SummonFractalProcedure.execute(world, x, y, z, entity);
			}
			if (entity instanceof RouteShaperEntity _datEntSetI)
				_datEntSetI.getEntityData().set(RouteShaperEntity.DATA_skillp, 0);
			if (entity instanceof LineringPathshaperEntity _datEntSetI)
				_datEntSetI.getEntityData().set(LineringPathshaperEntity.DATA_skillp, 0);
		} else {
			if (entity instanceof RouteShaperEntity _datEntSetI)
				_datEntSetI.getEntityData().set(RouteShaperEntity.DATA_skillp, (int) (sklp + 2));
			if (entity instanceof LineringPathshaperEntity _datEntSetI)
				_datEntSetI.getEntityData().set(LineringPathshaperEntity.DATA_skillp, (int) (sklp + 2));
		}
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof RouteFractalEntity || entityiterator instanceof LingeringFractalEntity) {
					if (entityiterator instanceof Mob _entity && sourceentity instanceof LivingEntity _ent)
						_entity.setTarget(_ent);
				}
			}
		}
	}

	public static void boostFractals(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.tickCount % 20 == 7) {
			if ((entity instanceof RouteShaperEntity _datEntI ? _datEntI.getEntityData().get(RouteShaperEntity.DATA_phase) : 0) == 1 || entity instanceof LineringPathshaperEntity) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof RouteFractalEntity) {
							if (!(entityiterator instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(CaerulaArborModMobEffects.SEEK_OF_FRACTAL.get()))) {
								if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
									_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SEEK_OF_FRACTAL.get(), 999, 0));
							}
						}
					}
				}
			}
		}
	}

	public static boolean canAttackAnimals() {
		return false;
	}

	public static boolean canPlayerEvo(Entity entity) {
		if (entity == null)
			return false;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).can_player_evo
				&& (RelicUtils.hasDiso(entity) || (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization > 2.9);
	}

	public static Entity catchNearestEnemy(LevelAccessor world, double x, double y, double z, Entity obj) {
		if (obj == null)
			return null;
		Entity enemy = null;
		double minDist = 0;
		double d = 0;
		minDist = 999;
		for (Entity entityiterator : world.getEntities(obj, new AABB((x + 4), (y + 4), (z + 4), (x - 4), (y - 4), (z - 4)))) {
			if (!(entityiterator instanceof LivingEntity)) {
				continue;
			}
			if (entityiterator instanceof Monster || (entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == obj) {
				d = entityiterator != null ? obj.distanceTo(entityiterator) : -1;
				if (d <= 4) {
					if (d < minDist) {
						minDist = d;
						enemy = entityiterator;
					}
				}
			}
		}
		return enemy;
	}

	public static void deductSanity(Entity entity, double amount) {
		if (entity == null)
			return;
		double snt = 0;
		double deletion = 0;
		double resis = 0;
		if ((entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity0.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0) < 0) {
			return;
		}
		if (entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CaerulaArborModMobEffects.UNDER_BREAK.get())) {
			return;
		}
		if (!(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CaerulaArborModMobEffects.SANITY_IMMUE.get()))) {
			resis = 0.01 * (100 - (entity instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RESISTANCE.get())
					? _livingEntity3.getAttribute(CaerulaArborModAttributes.SANITY_RESISTANCE.get()).getValue()
					: 0));
			deletion = amount * (entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get())
					? _livingEntity4.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).getValue()
					: 0) * resis;
			snt = (entity instanceof LivingEntity _livingEntity5 && _livingEntity5.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity5.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0)
					- deletion;
			if (snt < -1) {
				snt = -1;
			}
			if (snt > 1000) {
				snt = 1000;
			}
			if (new Object() {
				public boolean checkGamemode(Entity _ent) {
					if (_ent instanceof ServerPlayer _serverPlayer) {
						return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
					} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
						return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
								&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
					}
					return false;
				}
			}.checkGamemode(entity)) {
				snt = entity instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity7.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0;
			}
			if (new Object() {
				public boolean checkGamemode(Entity _ent) {
					if (_ent instanceof ServerPlayer _serverPlayer) {
						return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
					} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
						return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
								&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
					}
					return false;
				}
			}.checkGamemode(entity)) {
				snt = entity instanceof LivingEntity _livingEntity9 && _livingEntity9.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity9.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0;
			}
			if (entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
				_livingEntity10.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(snt);
			if (entity instanceof ServerPlayer _player) {
				Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "terror_of_knowing"));
				AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						_player.getAdvancements().award(_adv, criteria);
				}
			}
		}
	}

	public static void deductSanity50(Entity entity) {
		deductSanity(entity, 50);
	}

	public static void deductSanity75(Entity entity) {
		deductSanity(entity, 75);
	}

	public static void deductSanity128(Entity entity) {
		deductSanity(entity, 128);
	}

	public static void restoreSanity(Entity entity, double amount) {
		if (entity == null)
			return;
		if (amount <= 0)
			return;
		double snt = (entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity0.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0) + amount;
		if (snt > 1000) {
			snt = 1000;
		}
		if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()))
			_livingEntity1.getAttribute(CaerulaArborModAttributes.SANITY.get()).setBaseValue(snt);
	}

	public static void restoreSanity15(Entity entity) {
		restoreSanity(entity, 15);
	}

	public static void restoreSanity50(Entity entity) {
		restoreSanity(entity, 50);
	}

	public static void restoreSanity125(Entity entity) {
		restoreSanity(entity, 125);
	}

	public static void restorePlayerLights(Entity player, double num) {
		if (player == null)
			return;
		if (num <= 0)
			return;
		double snt = (player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light + num;
		if (snt > 100) {
			snt = 100;
		}
		double _setval = snt;
		player.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
			capability.player_light = _setval;
			capability.syncPlayerVariables(player);
		});
	}

	public static boolean isAlive(Entity entity) {
		return entity != null && entity.isAlive();
	}

	public static void heal(Entity entity, double amount) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living) {
			living.heal((float) amount);
		}
	}

	public static void healWithParticles(LevelAccessor world, Entity entity, double flatAmount, double maxHealthMultiplier) {
		if (entity == null || !entity.isAlive())
			return;
		if (entity instanceof LivingEntity living) {
			double maxHealth = living.getMaxHealth();
			heal(entity, maxHealth * maxHealthMultiplier + flatAmount);
			if (world instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.CHERRY_LEAVES,
					entity.getX(), entity.getY() + 1, entity.getZ(),
					24, 1, 1, 1, 0.1);
			}
		}
	}

	public static void applyNetherseaBuff(LevelAccessor world, Entity entity) {
		if (entity == null || !entity.isAlive()) {
			return;
		}
		if (!(entity instanceof LivingEntity living)) {
			return;
		}
		CaerulaArborModVariables.MapVariables mapVars = CaerulaArborModVariables.MapVariables.get(world);
		if (entity.getType().is(OCEAN_OFFSPRING)) {
			if (mapVars.strategy_silence >= 2) {
				living.setHealth((float) (living.getHealth() + living.getMaxHealth() * 0.0025));
			} else if (mapVars.strategy_subsisting >= 3) {
				living.setHealth((float) (living.getHealth() + living.getMaxHealth() * 0.001));
			}
			if (!living.level().isClientSide()) {
				living.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.RUNNING_ON_TRAIL.get(), 5, 0, false, false));
			}
		} else if (entity instanceof Player) {
			if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null)
					.orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization >= 3) {
				if (mapVars.strategy_silence >= 2) {
					living.heal((float) (living.getMaxHealth() * 0.0025));
				} else if (mapVars.strategy_subsisting >= 3) {
					living.heal((float) (living.getMaxHealth() * 0.001));
				}
				if (!living.level().isClientSide()) {
					living.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.RUNNING_ON_TRAIL.get(), 5, 0, false, false));
					living.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 0, false, false));
				}
			}
		}
	}

	public static void initPigSanity(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
			living.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(6);
	}

	public static void initCatSanity(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
			living.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(4);
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(35);
	}

	public static void initSliderSanity(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_RATE.get()))
			living.getAttribute(CaerulaArborModAttributes.SANITY_RATE.get()).setBaseValue(10);
	}

	public static void initAplusMagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
	}

	public static void initBplusMagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(18);
	}

	public static void initSmagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(40);
	}

	public static void initBmagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(15);
	}

	public static void initWardenAttributes(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(75);
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
			living.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.01);
	}

	public static void initLastKnightAttributes(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()))
			living.getAttribute(CaerulaArborModAttributes.GENERAL_DEFENSE.get()).setBaseValue(20);
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(60);
	}

	public static void initEndspeakerAbilities(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (inquirybility(world, 0)) {
			if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
				living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get())
						.setBaseValue((living.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get())
								? living.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
								: 0) + 40);
		}
		if (inquirybility(world, 1)) {
			if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
				living.getAttribute(CaerulaArborModAttributes.MISSRATE.get())
						.setBaseValue((living.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get())
								? living.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).getBaseValue()
								: 0) + 50);
		}
	}

	public static void deductSanityWithParticles(LevelAccessor world, double x, double y, double z, Entity entity, double amount) {
		if (entity == null)
			return;
		deductSanity(entity, amount);
		new Object() {
			void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 0.5 * entity.getBbHeight()), z, 16, 0.86, 1.2, 0.86, 0.1);
				final int tick2 = ticks;
				CaerulaArborMod.queueServerWork(tick2, () -> {
					if (timedlooptotal > timedloopiterator + 1) {
						timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
					}
				});
			}
		}.timedLoop(0, 3, 5);
	}

	public static void castDragonBreath(LevelAccessor world, double x, double y, double z, Entity owner, Entity target, double type) {
		if (owner == null) return;
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = CaerulaArborModEntities.MOIST_DRAGON_BREATH.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn instanceof MoistDragonBreathEntity _datEntSetS){
				RandomSource random = world.getRandom();
				entityToSpawn.setDeltaMovement(owner.getLookAngle().scale(0.25).add(
					Mth.nextDouble(random, -0.15, 0.15),
					Mth.nextDouble(random, -0.15, 0.15),
					Mth.nextDouble(random, -0.15, 0.15)
        		));
        		SoundEvent SHOOT = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "caster_cast"));
        		_level.playSound(owner, BlockPos.containing(x, y, z), SHOOT, SoundSource.HOSTILE, 2, 
        		Mth.nextFloat(owner.level().getRandom(), 0.9f, 1.1f));
				SynchedEntityData data = _datEntSetS.getEntityData();
				data.set(MoistDragonBreathEntity.DATA_OWNER, owner.getStringUUID());
				if(target != null) data.set(MoistDragonBreathEntity.DATA_TARGET, target.getStringUUID());
				data.set(MoistDragonBreathEntity.DATA_TYPE, (int) type);
				if(type > 0.5){
					AttributeInstance instance = _datEntSetS.getAttribute(Attributes.MAX_HEALTH);
					if (instance != null) instance.setBaseValue(instance.getBaseValue() * 2);
					_datEntSetS.setHealth(_datEntSetS.getMaxHealth());
				}
			}
		}
	}

	public static InteractionResult containFish(Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return InteractionResult.PASS;
		boolean given = false;
		if ((sourceentity instanceof LivingEntity _entity) ? _entity.isHolding(Items.BUCKET) : false) {
			if (entity instanceof RunFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CaerulaArborModItems.BUCKET_RUNFISH.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof SliderFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CaerulaArborModItems.BUCKET_SLIDER.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof ChiselerFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CaerulaArborModItems.BUCKET_CHISELER.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof FloaterProkaryoteEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CaerulaArborModItems.BUCKET_FLOATER.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof BoneFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CaerulaArborModItems.BUCKET_BONEFISH.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof CollectorProkaryoteEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CaerulaArborModItems.BUCKET_COLLECTOR.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			}
			if (given) {
				if (sourceentity instanceof Player _player) {
					ItemStack _stktoremove = new ItemStack(Items.BUCKET);
					_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
				}
				if (!entity.level().isClientSide())
					entity.discard();
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.FAIL;
	}

	public static void initDirection(Entity entity) {
		if (entity == null)
			return;
		{
			Entity _ent = entity;
			_ent.setYRot((float) (90 * Mth.nextInt(RandomSource.create(), 0, 3)));
			_ent.setXRot(0);
			_ent.setYBodyRot(_ent.getYRot());
			_ent.setYHeadRot(_ent.getYRot());
			_ent.yRotO = _ent.getYRot();
			_ent.xRotO = _ent.getXRot();
			if (_ent instanceof LivingEntity _entity) {
				_entity.yBodyRotO = _entity.getYRot();
				_entity.yHeadRotO = _entity.getYRot();
			}
		}
		if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			_livingEntity2.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(18);
	}

	public static void damagedByNethseabrand(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double lvl = 0;
		double gap = 0;
		double lvl1 = 0;
		ItemStack a0 = ItemStack.EMPTY;
		ItemStack a1 = ItemStack.EMPTY;
		ItemStack a2 = ItemStack.EMPTY;
		ItemStack a3 = ItemStack.EMPTY;
		if (entity instanceof LivingEntity) {
			if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CaerulaArborModMobEffects.TRAIL_BUFF.get()))) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.TRAIL_BUFF.get(), 10, 0, false, false));
			}
			gap = 20;
			a0 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();
			a1 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
			a2 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
			a3 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
			if (a0.getItem() == CaerulaArborModItems.SEALEATHER_BOOTS.get()) {
				gap = gap + 8;
			} else if (a0.getItem() == CaerulaArborModItems.SEALEATHER_CHITIN_BOOTS.get()) {
				gap = gap + 6;
			} else if (a0.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_BOOTS.get()) {
				gap = gap + 8;
			}
			if (a1.getItem() == CaerulaArborModItems.SEALEATHER_LEGGINGS.get()) {
				gap = gap + 6;
			} else if (a1.getItem() == CaerulaArborModItems.SEALEATHER_CHITIN_LEGGINGS.get()) {
				gap = gap + 5;
			} else if (a1.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_LEGGINGS.get()) {
				gap = gap + 6;
			}
			if (a2.getItem() == CaerulaArborModItems.SEALEATHER_CHESTPLATE.get()) {
				gap = gap + 4;
			} else if (a2.getItem() == CaerulaArborModItems.SEALEATHER_CHITIN_CHESTPLATE.get()) {
				gap = gap + 4;
			} else if (a2.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_CHESTPLATE.get()) {
				gap = gap + 4;
			}
			if (a3.getItem() == CaerulaArborModItems.SEALEATHER_HELMET.get()) {
				gap = gap + 2;
			} else if (a3.getItem() == CaerulaArborModItems.SEALEATHER_CHITIN_HELMET.get()) {
				gap = gap + 3;
			} else if (a3.getItem() == CaerulaArborModItems.TRAILRITE_ARMOR_HELMET.get()) {
				gap = gap + 2;
			}
			if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.NETHERSEA_WALKER.get(), a0) != 0) {
				lvl = a0.getEnchantmentLevel(CaerulaArborModEnchantments.NETHERSEA_WALKER.get());
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.RUNNING_ON_TRAIL.get(), 30, (int) lvl, false, false));
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 0, false, false));
			}
			if (entity.tickCount % gap == 0 && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "immue_to_nethersea_brand")))) {
				if (entity instanceof Player) {
					if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.DEPTH_STRIDER, a0) != 0) {
						lvl1 = a0.getEnchantmentLevel(Enchantments.DEPTH_STRIDER);
					}
					if (Math.random() < 0.2 * lvl + 0.05 * lvl1) {
						return;
					}
					if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization >= 3) {
						return;
					}
				}
				if (!(entity instanceof LivingEntity _livEnt28 && _livEnt28.getMobType() == MobType.UNDEAD)) {
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "trail_damage")))), 2);
				}
				EntityUtils.deductSanity(entity, 20);
			}
		}
	}

	public static void endspeakerRevive(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Endspeaker0Entity endspeaker0) {
			endspeaker0.setAnimation("animation.endspeaker_0.die");
			if (entity instanceof Endspeaker0Entity _datEntSetI)
				_datEntSetI.getEntityData().set(Endspeaker0Entity.DATA_EvolveTime, 300);
		} else if (entity instanceof Endspeaker1Entity endspeaker1) {
			endspeaker1.setAnimation("animation.endspeaker_1.die");
			if (entity instanceof Endspeaker1Entity _datEntSetI)
				_datEntSetI.getEntityData().set(Endspeaker1Entity.DATA_EvolveTime, 300);
		} else if (entity instanceof Endspeaker2Entity endspeaker2) {
			endspeaker2.setAnimation("animation.endspeaker_2.die");
			endspeaker2.getEntityData().set(Endspeaker2Entity.DATA_EvolveTime, 300);
			endspeaker2.getEntityData().set(Endspeaker2Entity.DATA_duration, 999);
		}
	}

	public static void endspeakerToPhase2(LevelAccessor world, double x, double y, double z, Entity entity, double phase) {
		if (entity == null)
			return;
		Entity sacrifice = null;
		double tx = 0;
		double ty = 0;
		double tz = 0;
		double r = 0;
        Entity result;
        if (entity == null) {
            result = null;
        } else {
            Entity sacrifice1 = null;
            Entity player = null;
            double minDIst = 0;
            double curDist = 0;
            double curPlayerDist = 0;
            double minPlayerDist = 0;
            double bestowed = 0;
            minDIst = 999;
            minPlayerDist = 999;
            {
                final Vec3 _center1 = new Vec3(x, (y + 24), z);
                List<Entity> _entfound1 = world.getEntitiesOfClass(Entity.class, new AABB(_center1, _center1).inflate(64 / 2d), e1 -> true).stream().sorted(Comparator.comparingDouble(_entcnd1 -> _entcnd1.distanceToSqr(_center1))).toList();
                for (Entity entityiterator1 : _entfound1) {
                    if (!(entityiterator1 instanceof Monster || entityiterator1 instanceof Player)) {
                        continue;
                    }
                    if (entityiterator1.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_edible")))) {
                        if (entityiterator1 instanceof BaselayerAbyssalEntity && inquirybility(world, 0)) {
                            continue;
                        } else if (entityiterator1 instanceof PredatorAbyssalEntity && inquirybility(world, 1)) {
                            continue;
                        } else if (entityiterator1 instanceof GuideAbyssalEntity && inquirybility(world, 2)) {
                            continue;
                        } else if (entityiterator1 instanceof SplasherAbyssalEntity && inquirybility(world, 3)) {
                            continue;
                        } else if (entityiterator1 instanceof UmbrellaAbyssalEntity && inquirybility(world, 4)) {
                            continue;
                        } else if (entityiterator1 instanceof CrackerAbyssalEntity && inquirybility(world, 5)) {
                            continue;
                        }
                        curDist = entityiterator1 != null ? entity.distanceTo(entityiterator1) : -1;
                        if (curDist < minDIst) {
                            minDIst = curDist;
                            sacrifice1 = entityiterator1;
                        }
                    } else if (entityiterator1 instanceof Player && !(new Object() {
                        public boolean checkGamemode(Entity _ent) {
                            if (_ent instanceof ServerPlayer _serverPlayer) {
                                return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                            } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                        && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                            }
                            return false;
                        }
                    }.checkGamemode(entityiterator1))) {
                        curPlayerDist = entityiterator1 != null ? entity.distanceTo(entityiterator1) : -1;
                        if (curPlayerDist < minPlayerDist) {
                            minPlayerDist = curPlayerDist;
                            player = entityiterator1;
                        }
                    }
                }
            }
            if (sacrifice1 == null) {
                if (player == null) {
                    result = entity;
                } else {
                    result = player;
                }
            } else {
                if (sacrifice1 instanceof BaselayerAbyssalEntity) {
                    bestowed = 0;
                } else if (sacrifice1 instanceof PredatorAbyssalEntity) {
                    bestowed = 1;
                } else if (sacrifice1 instanceof GuideAbyssalEntity) {
                    bestowed = 2;
                } else if (sacrifice1 instanceof SplasherAbyssalEntity) {
                    bestowed = 3;
                } else if (sacrifice1 instanceof UmbrellaAbyssalEntity) {
                    bestowed = 4;
                } else if (sacrifice1 instanceof CrackerAbyssalEntity) {
                    bestowed = 5;
                }
                WorldUtils.bestowAbility(world, bestowed);
                result = sacrifice1;
            }
        }
        sacrifice = result;
		if (!(sacrifice == null)) {
			tx = sacrifice.getX();
			ty = sacrifice.getY();
			tz = sacrifice.getZ();
			r = 1 + phase;
			if (world instanceof Level _level) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_eat")), SoundSource.HOSTILE, 4, 1);
			}
			{
				final Vec3 _center = new Vec3(tx, ty, tz);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate((2 * r) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (!(entityiterator instanceof LivingEntity)) {
						continue;
					}
					if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffpsring"))) && entityiterator instanceof Player) {
						continue;
					}
					if ((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization > 2) {
						continue;
					}
					if ((entityiterator != null ? sacrifice.distanceTo(entityiterator) : -1) < r) {
						entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")))),
								(float) ((double) CaerulaConfigsConfiguration.SANITY_BREAK.get() * 6));
					}
				}
			}
			if (!(sacrifice instanceof Player)) {
				if (sacrifice.isAlive()) {
					sacrifice.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage")))), 114514);
				}
				if (sacrifice.isAlive()) {
					if (!sacrifice.level().isClientSide())
						sacrifice.discard();
				}
			}
		} else {
			tx = x;
			ty = y;
			tz = z;
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles((CaerulaArborModParticleTypes.ENDSPEAKER_PARTICLE.get()), tx, (ty + 1), tz, 128, 1, 1, 1, 0.075);
		if (phase == 1) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_1.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else if (phase == 2) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_2.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		} else {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_3.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		}
	}

	public static void endspeakerLinkPtcTo(LevelAccessor world, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		double vx = 0;
		double vy = 0;
		double vz = 0;
		double size = 0;
		vx = toX - fromX;
		vy = toY - fromY;
		vz = toZ - fromZ;
		size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(fromX, fromY, fromZ), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.teleport")), SoundSource.HOSTILE, 1, 1);
		}
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(CaerulaArborModParticleTypes.ENDSPEAKER_INV.get(), (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 0.5), (fromZ + (vz / size) * index0), 8, 0.32, 0.5, 0.32, 0.05);
		}
	}

	public static void enderinaLinkPtcTo(LevelAccessor world, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		double vx = 0;
		double vy = 0;
		double vz = 0;
		double size = 0;
		vx = toX - fromX;
		vy = toY - fromY;
		vz = toZ - fromZ;
		size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel serverLevel)
				serverLevel.sendParticles(CaerulaArborModParticleTypes.EDERMAN_PTC.get(), (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 1), (fromZ + (vz / size) * index0), 1, 0, 0, 0, 0.01);
		}
	}

	public static double getNodeLivingBarrier(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_living_barrier;
	}

	public static String getSilenceMigration(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_migration.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_silence + 5))).getString();
	}

	public static double getNodeRealDamage(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_real_damage;
	}

	public static double getNodeHealDamage(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_heal_damage;
	}

	public static double getNodeWorseBreak(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_worse_break;
	}

	public static String getSilenceSubsis(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_subsisting.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_silence + 5))).getString();
	}

	public static double getComplexPulling(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return 0;
		if ((entity instanceof LivingEntity _entUseItem0 ? _entUseItem0.getUseItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
			return entity instanceof LivingEntity _entUseTicks3 ? _entUseTicks3.getTicksUsingItem() : 0;
		}
		return 0;
	}

	public static double getNodeAddDamage(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_add_damage;
	}

	public static String getPlayerSurvconta(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_SURVIVOR);
	}

	public static double getNodeAddSanity(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_add_sanity;
	}

	public static double getSeabornAround(LevelAccessor world, double x, double y, double z, Entity center) {
		if (center == null)
			return 0;
		double count = 0;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator == center) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					if (!(entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))
							|| entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet"))))) {
						count = count + 1;
					}
				}
			}
		}
		return count;
	}

	public static double getSeabornNum(LevelAccessor world, double x, double y, double z) {
		double count = 0;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					if (!(entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "bossoffspring")))
							|| entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet"))))) {
						count = count + 1;
					}
				}
			}
		}
		return count;
	}

	public static Entity getGladiiaAround(LevelAccessor world, double x, double y, double z) {
		Entity g = world.getEntitiesOfClass(GladiiaEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().sorted(new Object() {
			Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
				return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
			}
		}.compareDistOf(x, y, z)).findFirst().orElse(null);
		if (!(g == null) && g.isAlive()) {
			return g;
		}
		return null;
	}

	public static void givePlayerReserve(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		double r = 0;
		double exp = 0;
		double r_a = 0;
		boolean creative = false;
		creative = new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
			}
		}.checkGamemode(entity);
		if (itemstack.getItem() == CaerulaArborModItems.GENE_SAMPLE_NORMAL.get()) {
			r = 1;
			exp = 1;
		} else if (itemstack.getItem() == CaerulaArborModItems.GENE_SAMPLE_UPGRADED.get()) {
			r = 4;
			exp = 2;
		} else if (itemstack.getItem() == CaerulaArborModItems.GENE_SAMPLE_SUPERB.get()) {
			r_a = 1;
			exp = 3;
		}
		if (exp > 0) {
			if ((entity instanceof Player _plr ? _plr.experienceLevel : 0) >= exp || creative) {
				{
					double _setval = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).reserve_quantity + r;
					entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.reserve_quantity = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
				{
					double _setval = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).reserve_quality + r_a;
					entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.reserve_quality = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
				if (!creative) {
					if (entity instanceof Player _player)
						_player.giveExperienceLevels(-((int) exp));
				}
				if (world instanceof Level _level) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.experience_orb.pickup")), SoundSource.PLAYERS, 2, 1);
				}
				itemstack.shrink(1);
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.gene_sample.no_exp").getString())), true);
			}
		}
	}

	public static String getDescrSubsis(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_subsisting.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting))).getString();
	}

	public static double getSanityIndex(Entity entity) {
		if (entity == null)
			return 0;
		return Math.ceil(
				(entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity0.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0) / 50);
	}

	public static String getSilenceBreed(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_breed.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_silence + 5))).getString();
	}

	public static double getNodeAddResis(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_add_resis;
	}

	public static String getPlayerEnrave(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).relic_hand_ENGRAVE);
	}

	public static Entity getNearestEnemy(LevelAccessor world, double x, double y, double z, Entity exception0, Entity exception1, Entity obj) {
		if (exception0 == null || exception1 == null || obj == null)
			return null;
		Entity enemy = null;
		double minDist = 0;
		double d = 0;
		minDist = 999;
		for (Entity entityiterator : world.getEntities(obj, new AABB((x + 42), (y + 40), (z + 42), (x - 42), (y - 40), (z - 42)))) {
			if (!(entityiterator instanceof LivingEntity)) {
				continue;
			}
			if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				continue;
			}
			d = entityiterator != null ? obj.distanceTo(entityiterator) : -1;
			if (d <= 42) {
				if (entityiterator == exception0) {
					continue;
				}
				if (entityiterator == exception1) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sea_friend")))) {
					continue;
				}
				if (new Object() {
					public boolean checkGamemode(Entity _ent) {
						if (_ent instanceof ServerPlayer _serverPlayer) {
							return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
							return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
									&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(entityiterator)) {
					continue;
				}
				if (d < minDist) {
					minDist = d;
					enemy = entityiterator;
				}
			}
		}
		return enemy;
	}

	public static double getStraSilence(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_silence;
	}

	public static double getStraSubsis(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting;
	}

	public static double getStraBreed(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_breed;
	}

	public static double getStraGrow(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_grow;
	}

	public static double getStraMigration(LevelAccessor world) {
		return CaerulaArborModVariables.MapVariables.get(world).strategy_migration;
	}

	public static double getEntityCosine(Entity A, Entity B) {
		if (A == null || B == null)
			return 0;
		return MathUtils.getCosine(B.getX() - A.getX(), B.getZ() - A.getZ(), A.getLookAngle().x, A.getLookAngle().z);
	}

	public static double getFellowAround(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return 0;
		double num = 0;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator == entity) {
					continue;
				}
				if ((ForgeRegistries.ENTITY_TYPES.getKey(entityiterator.getType()).toString()).equals(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString())) {
					num = num + 1;
				}
			}
		}
		return num;
	}

	public static double getNodeAddSpeed(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_add_speed;
	}

	public static double getIllusionNum(LevelAccessor world, double x, double y, double z) {
		double count = 0;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof OceanIllusionEntity) {
					count = count + 1;
				}
			}
		}
		return count;
	}

	public static double getNodeAddMiss(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_add_miss;
	}

	public static String getLiveMaxShown(Entity entity) {
		if (entity == null)
			return "";
		return "/" + Math.round((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_maxlive);
	}

	public static double getNodeEutectes(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).PEVO_NODE_eunectes;
	}

	public static String getSilenceGrow(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_grow.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_silence + 5))).getString();
	}

	public static void giveSpearFight(Entity entity) {
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.SPEAR_FIGHT.get()))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.SPEAR_FIGHT.get(), 60, 0, false, false));
		}
	}

	public static String getDescrBreed(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_breed.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_breed))).getString();
	}

	public static String getDescrGrow(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_grow.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_grow))).getString();
	}

	public static double getSlimeSize(Entity entity) {
		if (entity == null)
			return 0;
		return (entity instanceof NetherseaSlimeEntity _datEntI ? _datEntI.getEntityData().get(NetherseaSlimeEntity.DATA_SIZE) : 0) * 0.5;
	}

	public static void giveLessArmor(Entity obj, double limit) {
		if (obj == null)
			return;
		if (obj instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.LESS_ARMOR.get())) {
			if ((obj instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) < limit) {
				if (obj instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.LESS_ARMOR.get(), 300,
							(int) ((obj instanceof LivingEntity _livEnt && _livEnt.hasEffect(CaerulaArborModMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CaerulaArborModMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) + 1), false, true));
			}
		} else {
			if (obj instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.LESS_ARMOR.get(), 300, 0, false, true));
		}
	}

	public static void repellerChop(LevelAccessor world, double x, double y, double z, Entity entity, double rate) {
		if (entity == null)
			return;
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.guardian.attack")), SoundSource.HOSTILE, 2, 1);
		}
		{
			final Vec3 _center = new Vec3((x + 1.8 * entity.getLookAngle().x), (y + 1.5), (z + 1.8 * entity.getLookAngle().z));
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (!(entityiterator instanceof LivingEntity)) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					if (!((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entityiterator)) {
						continue;
					}
				}
				entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "repeller_attack"))), entity),
						(float) ((entity instanceof LivingEntity _livingEntity7 && _livingEntity7.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity7.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate));
				if (entity instanceof LivingEntity _entity)
					_entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + 3));
				for (int index0 = 0; index0 < 2; index0++) {
					giveLessArmor(entityiterator, 18);
				}
			}
		}
	}

	public static String getDescrMigra(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_migration.description_" + Math.round(CaerulaArborModVariables.MapVariables.get(world).strategy_migration))).getString();
	}

	public static void gainLessSpeed(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.ADD_REACH.get()))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_REACH.get(), 20, 3, false, false));
		}
	}

	public static void giveGuideLay(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
			if (!(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CaerulaArborModMobEffects.MUTE.get()))) {
				if (!(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CaerulaArborModMobEffects.GUIDE_PATH_AHEAD.get()))) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.GUIDE_PATH_AHEAD.get(), 20, 0));
				}
			}
		}
	}

	public static String getSanity(Entity entity) {
		if (entity == null)
			return "";
		double modi = 0;
		modi = entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get())
				? _livingEntity0.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).getValue()
				: 0;
		if (modi <= 0) {
			return "Infinity";
		}
		return ""
				+ ((int) ((entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY.get()) ? _livingEntity1.getAttribute(CaerulaArborModAttributes.SANITY.get()).getBaseValue() : 0)
						/ modi));
	}

	public static String getLives(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_lives);
	}

	public static String getShield(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_shield);
	}

	public static String getPalsy(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round(entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.NUMB.get()) ? _livingEntity0.getAttribute(CaerulaArborModAttributes.NUMB.get()).getBaseValue() : 0);
	}

	public static String getHealth(Entity entity) {
		if (entity == null)
			return "";
		return (new java.text.DecimalFormat("##.##").format(entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1)) + "/"
				+ (new java.text.DecimalFormat("##.#").format(entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1));
	}

	public static double getHealthPerc(Entity entity) {
		if (entity == null)
			return 0;
		return (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) / (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1);
	}

	public static String getLight(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light);
	}

	public static Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
		return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
	}

	public static void gladiiaLinkPtcToEntity(LevelAccessor world, Entity entity, Entity tgt) {
		if (entity == null || tgt == null)
			return;
        double fromX = entity.getX();
        double fromY = entity.getY();
        double fromZ = entity.getZ();
        double vx = 0;
        double vy = 0;
        double vz = 0;
        double size = 0;
        vx = tgt.getX() - fromX;
        vy = tgt.getY() - fromY;
        vz = tgt.getZ() - fromZ;
        size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
        for (int index0 = 0; index0 < (int) size; index0++) {
            if (world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.DRIPPING_WATER, (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 0.5), (fromZ + (vz / size) * index0), 8, 0.32, 0.5, 0.32, 0.05);
        }
    }

	public static double getSpeed(Entity e) {
		if (e == null)
			return 0;
		return Math.sqrt(e.getDeltaMovement().x() * e.getDeltaMovement().x() + e.getDeltaMovement().y() * e.getDeltaMovement().y() + e.getDeltaMovement().z() * e.getDeltaMovement().z());
	}

	public static double getSize(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getBbWidth() * entity.getBbHeight();
	}

	public static boolean inquirybility(LevelAccessor world, double index) {
		int comparator = (int) Math.pow(2, index);
		int inq = (int) CaerulaArborModVariables.MapVariables.get(world).endspeaker_abolities & comparator;
		return inq == comparator;
	}

	public static void getEndspeakerPrefixes(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		StringBuilder prefixes = new StringBuilder();
		double index = 0;
		double count = 0;
		{
			String[] _array = (Component.translatable("entity.caerula_arbor.endspeaker.prefix").getString()).split((","));
            for (String stringiterator : _array) {
                if (inquirybility(world, index)) {
                    prefixes.append(stringiterator);
                    count = count + 1;
                }
                index = index + 1;
            }
        }
		if (count > 5) {
			prefixes = new StringBuilder(Component.translatable("entity.caerula_arbor.endspeaker.prefix.all").getString());
		}
		if (count >= 4) {
			prefixes.insert(0, "§b");
		} else if (count >= 2) {
			prefixes.insert(0, "§e");
		}
		if (!prefixes.isEmpty()) {
			entity.setCustomName(Component.literal((prefixes + entity.getDisplayName().getString())));
		}
	}

	public static void healFromGladiia(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.tickCount % 5 == 0) {
			if (!(getGladiiaAround(world, x, y, z) == null)) {
				if (entity instanceof LivingEntity _entity)
					_entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.008));
			}
		}
	}

	public static void setFastSwim(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()))
			_livingEntity1.getAttribute(ForgeMod.SWIM_SPEED.get())
					.setBaseValue(((entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? _livingEntity0.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() : 0) * 10));
	}

	public static void initHunter(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()))
			_livingEntity1.getAttribute(ForgeMod.SWIM_SPEED.get())
					.setBaseValue(((entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()) ? _livingEntity0.getAttribute(ForgeMod.SWIM_SPEED.get()).getBaseValue() : 0) * 8));
		if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()))
			_livingEntity2.getAttribute(CaerulaArborModAttributes.SANITY_MODIFIER.get()).setBaseValue(0.33);
	}

	public static void wardenRangedAttack(LevelAccessor world, Entity obj, boolean isSonic, double rate, double xx, double yy, double zz) {
		if (obj == null)
			return;
		Entity enemy = null;
		double damage = 0;
		double r = 0;
		enemy = obj instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null;
		r = 3;
		if (isSonic) {
			r = 4.5;
		}
		{
			final Vec3 _center = new Vec3(xx, yy, zz);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate((2 * r) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (!(entityiterator instanceof Mob) && !(entityiterator instanceof Player)) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
					if (!(entityiterator == enemy)) {
						continue;
					}
				}
				if (entityiterator == obj) {
					continue;
				}
				if (new Vec3(xx, yy, zz).distanceTo(new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()))) <= r) {
					damage = (obj instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate;
					if (isSonic) {
						entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_sonic"))), obj),
								(float) damage);
						deductSanity(entityiterator, damage * 1.5);
					} else {
						entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_attack"))), obj),
								(float) damage);
					}
				}
			}
		}
	}

	public static void wardenSonicBoom(LevelAccessor world, Entity obj, Entity target) {
		if (obj == null || target == null)
			return;
		double vx = 0;
		double vy = 0;
		double vz = 0;
		double len = 0;
		double tx = 0;
		double ty = 0;
		double tz = 0;
		vx = target.getX() - obj.getX();
		vy = target.getY() - obj.getY();
		vz = target.getZ() - obj.getZ();
		len = Math.sqrt(vx * vx + vy * vy + vz * vz);
		if (len > 0) {
			vx = vx / len;
			vy = vy / len;
			vz = vz / len;
		} else {
			vx = obj.getLookAngle().y;
			vy = obj.getLookAngle().x;
			vz = obj.getLookAngle().z;
		}
		for (int index0 = 0; index0 < 32; index0++) {
			tx = obj.getX() + vx * (index0 + 1);
			ty = obj.getY() + 1.5 + vy * (index0 + 1);
			tz = obj.getZ() + vz * (index0 + 1);
			wardenRangedAttack(world, obj, true, 0.25, tx, ty, tz);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.SONIC_BOOM, tx, ty, tz, 3, 0.1, 0.1, 0.1, 0.1);
		}
	}

	public static void wardenLightBoom(LevelAccessor world, Entity obj, Entity target) {
		if (obj == null || target == null)
			return;
		double vx = 0;
		double vy = 0;
		double vz = 0;
		double len = 0;
		double tx = 0;
		double ty = 0;
		double tz = 0;
		vx = target.getX() - obj.getX();
		vy = target.getY() - obj.getY();
		vz = target.getZ() - obj.getZ();
		len = Math.sqrt(vx * vx + vy * vy + vz * vz);
		if (len > 0) {
			vx = vx / len;
			vy = vy / len;
			vz = vz / len;
		} else {
			vx = obj.getLookAngle().y;
			vy = obj.getLookAngle().x;
			vz = obj.getLookAngle().z;
		}
		for (int index0 = 0; index0 < 32; index0++) {
			tx = obj.getX() + 0 + vx * (index0 + 1);
			ty = obj.getY() + 1.5 + vy * (index0 + 1);
			tz = obj.getZ() + 0 + vz * (index0 + 1);
			wardenRangedAttack(world, obj, true, 0.15, tx, ty, tz);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.SONIC_BOOM, tx, ty, tz, 1, 0.1, 0.1, 0.1, 0.1);
		}
	}

	public static void dropWardenExp(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			for (int index0 = 0; index0 < 64; index0++) {
				if (world instanceof ServerLevel _level)
					_level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 32, 64)));
			}
		}
	}

	public static void endspeakerTick(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double missR = 0;
		if (entity.tickCount % 5 == 0) {
			if (inquirybility(world, 2) && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.4) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25, 0, false, false));
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ENDSPEAER_BRANDGUIDE_BUFF.get(), 25, 0));
			}
			if (inquirybility(world, 4)) {
				entity.clearFire();
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(CaerulaArborModMobEffects.DIZZY.get());
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(CaerulaArborModMobEffects.MUTE.get());
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(MobEffects.WEAKNESS);
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(CaerulaArborModMobEffects.FROZEN.get());
				entity.setTicksFrozen(0);
				if (entity.tickCount % 200 == 0 && !(entity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CaerulaArborModMobEffects.ESSENCE_RESISTANCE.get()))) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ESSENCE_RESISTANCE.get(), 180, 2, false, false));
				}
				if (getSpeed(entity) > (entity instanceof LivingEntity _livingEntity15 && _livingEntity15.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED) ? _livingEntity15.getAttribute(Attributes.MOVEMENT_SPEED).getValue() : 0) * 1.25) {
					entity.setDeltaMovement(new Vec3(0, 0, 0));
				}
			}
			if (inquirybility(world, 1)) {
				missR = 50;
				if (entity.isOnFire() && !entity.fireImmune()) {
					missR = 0;
				}
				if (entity instanceof LivingEntity _livEnt19 && _livEnt19.hasEffect(CaerulaArborModMobEffects.DIZZY.get()) || entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(CaerulaArborModMobEffects.FROZEN.get())
						|| entity instanceof LivingEntity _livEnt21 && _livEnt21.hasEffect(MobEffects.LEVITATION) || entity instanceof LivingEntity _livEnt22 && _livEnt22.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
						|| entity instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(MobEffects.SLOW_FALLING)) {
					missR = 0;
				}
				if (entity instanceof LivingEntity _livingEntity24 && _livingEntity24.getAttributes().hasAttribute(CaerulaArborModAttributes.MISSRATE.get()))
					_livingEntity24.getAttribute(CaerulaArborModAttributes.MISSRATE.get()).setBaseValue(missR);
			}
		}
	}

	public static void spawnEndspeakerMobs(LevelAccessor world, double x, double y, double z, double elite_chan, double n) {
		double tx = 0;
		double ty = 0;
		double tz = 0;
		if (!CaerulaArborModVariables.MapVariables.get(world).endspeakerSummon) {
			return;
		}
		if (getSeabornNum(world, x, y, z) >= (world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.CLONE_NUMBER_LIMIT))) {
			return;
		}
		for (int index0 = 0; index0 < 8; index0++) {
			tx = x + Mth.nextInt(RandomSource.create(), -8, 8);
			tz = z + Mth.nextInt(RandomSource.create(), -8, 8);
			ty = WorldUtils.findValidYForCat(world, x, y, z, tx, y, tz);
			if (ty < 999) {
                double rand = 0;
                rand = Mth.nextInt(RandomSource.create(), 0, 5);
                if (rand == 0) {
                    if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.BASELAYER_ABYSSAL.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                } else if (rand == 1) {
                    if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.PREDATOR_ABYSSAL.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                } else if (rand == 2) {
                    if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.GUIDE_ABYSSAL.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                } else if (rand == 3) {
                    if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.SPLASHER_ABYSSAL.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                } else if (rand == 4) {
                    if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.UMBRELLA_ABYSSAL.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                } else if (rand == 5) {
                    if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = CaerulaArborModEntities.CRACKER_ABYSSAL.get().spawn(_level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                        }
                    }
                }
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.CLOUD, tx, (ty + 0.75), tz, 64, 0.75, 0.75, 0.75, 0.1);
                break;
			}
		}
		for (int index1 = 0; index1 < (int) (n - 1); index1++) {
			for (int index2 = 0; index2 < 8; index2++) {
				tx = x + Mth.nextInt(RandomSource.create(), -8, 8);
				tz = z + Mth.nextInt(RandomSource.create(), -8, 8);
				ty = WorldUtils.findValidYForCat(world, x, y, z, tx, y, tz);
				if (ty < 999) {
					WorldUtils.summonRandomSeaborn(world, elite_chan, tx, ty, tz);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.CLOUD, tx, (ty + 0.75), tz, 64, 0.75, 0.75, 0.75, 0.1);
					break;
				}
			}
		}
	}

	public static void hurtMartus(LevelAccessor world, Entity obj, Entity source, double num, double perc) {
		if (obj == null)
			return;
		double amount = 0;
		amount = (obj instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc + num;
		if (amount > 0) {
			obj.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer"))), source), (float) amount);
		}
	}

	public static void igniteRouteshaper(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()))
			_livingEntity0.getAttribute(CaerulaArborModAttributes.MAGIC_RESISTANCE.get()).setBaseValue(24);
		if (!(!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), e -> true).isEmpty())) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1800, 0, false, false));
		}
	}

	public static boolean isSameTeam(Entity a, Entity b) {
		if (a == null || b == null)
			return false;
		Team at = a.getTeam();
		Team bt = b.getTeam();
		if (at == null || bt == null) return false;
		return at.isAlliedTo(bt);
	}

	public static final TagKey<EntityType<?>> HUMAN = TagKey.create(
			Registries.ENTITY_TYPE,
			new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")
		);

	public static final TagKey<EntityType<?>> OCEAN_OFFSPRING = TagKey.create(
			Registries.ENTITY_TYPE,
			new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")
		);

	public static boolean isCorruptedSource(DamageSource source) {
		Entity entity = source.getEntity();
		if(entity == null) return true;
		if(entity.getType().is(HUMAN)) return true;
		return entity instanceof Player;
	}

	public static void killSelf(LevelAccessor world, Entity entity, Entity immediatesourceentity) {
		if (entity == null || immediatesourceentity == null)
			return;
		entity.invulnerableTime = 0;
		CaerulaArborMod.queueServerWork(2, () -> {
			if (!immediatesourceentity.level().isClientSide())
				immediatesourceentity.discard();
		});
	}

	public static void swallowCrystals(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		final Vec3 _center = new Vec3(x, y, z);
		List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
		for (Entity entityiterator : _entfound) {
			if (entityiterator instanceof MoistEnderCrystalEntity && entity.distanceTo(entityiterator) < 2.5) {
				if (!entityiterator.level().isClientSide())
					entityiterator.discard();
				heal(entity, (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.DRAGON_BREATH, (entityiterator.getX()), (entityiterator.getY() + 0.5), (entityiterator.getZ()), 16, 0.5, 0.5, 0.5, 0.1);
			}
		}
	}

	public static boolean isShulkerWalking(Entity entity) {
		if (entity == null)
			return false;
		if (entity.isAlive()) {
			return entity instanceof OceanizedShulkerEntity _datEntL1 && _datEntL1.getEntityData().get(OceanizedShulkerEntity.DATA_WALKING);
		}
		return false;
	}

	public static void summonHurtSkadi(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			LivingEntity entityToSpawn = CaerulaArborModEntities.SKADI.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
			if (entityToSpawn != null) {
				entityToSpawn.setHealth(entityToSpawn.getMaxHealth() * 0.4f);
				entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
			}
		}
	}

	public static void vanguardBuff(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double less = 0;
		if (entity.tickCount % 20 == 10) {
			if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CaerulaArborModMobEffects.INFANTRY.get()))) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(16 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator == entity) {
							continue;
						}
						if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "phalax")))) {
							less = less + 1;
						}
						if (less >= 10) {
							break;
						}
					}
				}
				if (less > 0) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.INFANTRY.get(), 40, (int) (less - 1)));
				}
			}
		}
	}

	public static boolean isOceanizedPlayerNearby(LevelAccessor world, double x, double y, double z) {
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(72 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof Player) {
					if ((entityiterator.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization >= 2.9) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void applyLastKnightFreeze(LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		double freeze = sourceentity.getTicksFrozen();
		if (freeze < 140) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
				sourceentity.setTicksFrozen((int) Math.min(freeze + 80, 200));
			} else {
				sourceentity.setTicksFrozen((int) Math.min(freeze + 40, 200));
			}
		} else {
			if (!(sourceentity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(CaerulaArborModMobEffects.FROZEN.get()))) {
				if (world instanceof Level _level) {
						_level.playSound(null, BlockPos.containing(sourceentity.getX(), sourceentity.getY(), sourceentity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "last_jnight_freeze")), SoundSource.HOSTILE,
								4, (float) Mth.nextDouble(RandomSource.create(), 1, 1.15));
				}
			}
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
				if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FROZEN.get(), 80, 0, false, false));
			} else {
				if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.FROZEN.get(), 40, 0, false, false));
			}
		}
	}

	public static void turnRounds(Entity another, Entity me) {
		if (another == null || me == null)
			return;
		Vec3 offset = another.position().add(me.position().reverse());
		if (offset.lengthSqr() <= 0.25)
			return;
		Vec3 delta = new Vec3(-offset.z, 0.15, offset.x).normalize().scale(0.5);
		another.setDeltaMovement(delta);
	}

	public static void pullToGladiia(Entity another, Entity me) {
		if (another == null || me == null)
			return;
		Vec3 offset = me.position().add(another.position().reverse());
		if (offset.lengthSqr() <= 0.01)
			return;
		offset = offset.normalize().scale(1.5);
		another.push(offset.x, offset.y, offset.z);
	}

	public static void clearTarget(Entity entity) {
		if (entity instanceof LivingEntity living) {
			Brain<?> brain = living.getBrain();
			brain.eraseMemory(MemoryModuleType.ANGRY_AT);
			brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
			brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
			brain.eraseMemory(MemoryModuleType.HURT_BY);
			if (living instanceof Mob mob) {
				mob.setTarget(null);
				mob.setLastHurtByMob(null);
				mob.setAggressive(false);
				mob.setLastHurtByPlayer(null);
			}
			if (living instanceof Animal animal) {
				animal.setTarget(null);
			}
			if (living instanceof NeutralMob n) {
				n.stopBeingAngry();
				n.setPersistentAngerTarget(null);
				n.setRemainingPersistentAngerTime(0);
			}
		}
	}

	public static Entity findNearestRidable(LevelAccessor world, double x, double y, double z, Entity entity, double distLimit, Class<? extends Entity> entityType) {
		if (entity == null)
			return null;
		Entity result = null;
		double minDist = 999;
		for (Entity entityiterator : world.getEntities(entity, new AABB((x + distLimit), (y + distLimit), (z + distLimit), (x - distLimit), (y - distLimit), (z - distLimit)))) {
			if (!entityType.isInstance(entityiterator)) {
				continue;
			}
			if (entityiterator.isVehicle()) {
				continue;
			}
			double d = entity.distanceTo(entityiterator);
			if (d < minDist && d < distLimit) {
				minDist = d;
				result = entityiterator;
			}
		}
		return result;
	}

	public static void teleportTo(LevelAccessor world, Entity entity, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		if (entity == null || !entity.isAlive())
			return;
		double vx = toX - fromX;
		double vy = toY - fromY;
		double vz = toZ - fromZ;
		double size = Math.max(Math.min(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 32), 1);
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(fromX, fromY, fromZ), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.teleport")), SoundSource.HOSTILE, 1, 1);
		}
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(CaerulaArborModParticleTypes.EDERMAN_PTC.get(), (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 0.65), (fromZ + (vz / size) * index0), 32, 0.65, 0.65, 0.65, 0.05);
		}
		entity.teleportTo(toX, toY, toZ);
		if (entity instanceof ServerPlayer _serverPlayer)
			_serverPlayer.connection.teleport(toX, toY, toZ, entity.getYRot(), entity.getXRot());
		entity.clearFire();
	}
}
