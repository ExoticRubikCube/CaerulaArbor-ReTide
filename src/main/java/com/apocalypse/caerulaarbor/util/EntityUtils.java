package com.apocalypse.caerulaarbor.util;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.map.MapVariablesHandler;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.config.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.*;
import com.apocalypse.caerulaarbor.init.*;
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
import java.util.Objects;

public class EntityUtils {

	private EntityUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	//TODO需要下放回实体
	public static void spawnLinkParticles(LevelAccessor world, Entity a, Entity b) {
		if (a == null || b == null || !(world instanceof ServerLevel level))
			return;
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

	public static boolean canAttackAnimals() {
		return false;
	}

	public static boolean canPlayerEvo(Entity entity) {
		if (entity == null)
			return false;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).can_player_evo
				&& (RelicUtils.hasDiso(entity) || (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization > 2.9);
	}

	public static Entity catchNearestEnemy(LevelAccessor world, double x, double y, double z, Entity obj) {
		if (obj == null)
			return null;
		Entity enemy = null;
		double minDist;
		double d;
		minDist = 999;
		for (Entity entityiterator : world.getEntities(obj, new AABB((x + 4), (y + 4), (z + 4), (x - 4), (y - 4), (z - 4)))) {
			if (!(entityiterator instanceof LivingEntity)) {
				continue;
			}
			if (entityiterator instanceof Monster || (entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == obj) {
				d = obj.distanceTo(entityiterator);
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

	public static void restorePlayerLights(Entity player, double num) {
		if (player == null)
			return;
		if (num <= 0)
			return;
		double snt = (player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light + num;
		if (snt > 100) {
			snt = 100;
		}
		double _setval = snt;
		player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
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
		MapVariables mapVars = MapVariables.get(world);
		if (entity.getType().is(OCEAN_OFFSPRING)) {
			if (mapVars.strategy_silence >= 2) {
				living.setHealth((float) (living.getHealth() + living.getMaxHealth() * 0.0025));
			} else if (mapVars.strategy_subsisting >= 3) {
				living.setHealth((float) (living.getHealth() + living.getMaxHealth() * 0.001));
			}
			if (!living.level().isClientSide()) {
				living.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL.get(), 5, 0, false, false));
			}
		} else if (entity instanceof Player) {
			if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null)
					.orElse(new PlayerVariable())).player_oceanization >= 3) {
				if (mapVars.strategy_silence >= 2) {
					living.heal((float) (living.getMaxHealth() * 0.0025));
				} else if (mapVars.strategy_subsisting >= 3) {
					living.heal((float) (living.getMaxHealth() * 0.001));
				}
				if (!living.level().isClientSide()) {
					living.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL.get(), 5, 0, false, false));
					living.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 0, false, false));
				}
			}
		}
	}

	//TODO:这些init需要下放回实体
	public static void initAplusMagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(30);
	}

	public static void initBplusMagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(18);
	}

	public static void initSmagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(40);
	}

	public static void initBmagic(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(15);
	}

	public static void initWardenAttributes(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(75);
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
			living.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.01);
	}

	public static void initLastKnightAttributes(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE.get()))
			living.getAttribute(CAAttributes.GENERAL_DEFENSE.get()).setBaseValue(20);
		if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(60);
	}

	public static void initEndspeakerAbilities(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (inquirybility(world, 0)) {
			if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
				living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get())
						.setBaseValue((living.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get())
								? living.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).getBaseValue()
								: 0) + 40);
		}
		if (inquirybility(world, 1)) {
			if (entity instanceof LivingEntity living && living.getAttributes().hasAttribute(CAAttributes.MISSRATE.get()))
				living.getAttribute(CAAttributes.MISSRATE.get())
						.setBaseValue((living.getAttributes().hasAttribute(CAAttributes.MISSRATE.get())
								? living.getAttribute(CAAttributes.MISSRATE.get()).getBaseValue()
								: 0) + 50);
		}
	}

	//TODO可能需要下放回实体作为辅助方法
	public static void castDragonBreath(LevelAccessor world, double x, double y, double z, Entity owner, Entity target, double type) {
		if (owner == null) return;
		if (world instanceof ServerLevel _level) {
			Entity entityToSpawn = CAEntities.MOIST_DRAGON_BREATH.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
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

	//需要解释，大概率需要处理
	public static InteractionResult containFish(Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return InteractionResult.PASS;
		boolean given = false;
		if (sourceentity instanceof LivingEntity _entity && _entity.isHolding(Items.BUCKET)) {
			if (entity instanceof RunFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CAItems.BUCKET_RUNFISH.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof SliderFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CAItems.BUCKET_SLIDER.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof ChiselerFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CAItems.BUCKET_CHISELER.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof FloaterProkaryoteEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CAItems.BUCKET_FLOATER.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof BoneFishEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CAItems.BUCKET_BONEFISH.get()).copy();
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
				}
				given = true;
			} else if (entity instanceof CollectorProkaryoteEntity) {
				if (sourceentity instanceof Player _player) {
					ItemStack _setstack = new ItemStack(CAItems.BUCKET_COLLECTOR.get()).copy();
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

	//TODO需要下放
	public static void initDirection(Entity entity) {
		if (entity == null)
			return;
		{
            entity.setYRot((float) (90 * Mth.nextInt(RandomSource.create(), 0, 3)));
			entity.setXRot(0);
			entity.setYBodyRot(entity.getYRot());
			entity.setYHeadRot(entity.getYRot());
			entity.yRotO = entity.getYRot();
			entity.xRotO = entity.getXRot();
			if (entity instanceof LivingEntity _entity) {
				_entity.yBodyRotO = _entity.getYRot();
				_entity.yHeadRotO = _entity.getYRot();
			}
		}
		if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			_livingEntity2.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(18);
	}

	//可能需要评估放到哪个util合适
	public static void damagedByNethseabrand(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double lvl = 0;
		double gap;
		double lvl1 = 0;
		ItemStack a0;
		ItemStack a1;
		ItemStack a2;
		ItemStack a3;
		if (entity instanceof LivingEntity) {
			if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CAMobEffects.TRAIL_BUFF.get()))) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CAMobEffects.TRAIL_BUFF.get(), 10, 0, false, false));
			}
			gap = 20;
			a0 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();
			a1 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
			a2 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
			a3 = (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
			if (a0.getItem() == CAItems.SEALEATHER_BOOTS.get()) {
				gap = gap + 8;
			} else if (a0.getItem() == CAItems.SEALEATHER_CHITIN_BOOTS.get()) {
				gap = gap + 6;
			} else if (a0.getItem() == CAItems.TRAILRITE_ARMOR_BOOTS.get()) {
				gap = gap + 8;
			}
			if (a1.getItem() == CAItems.SEALEATHER_LEGGINGS.get()) {
				gap = gap + 6;
			} else if (a1.getItem() == CAItems.SEALEATHER_CHITIN_LEGGINGS.get()) {
				gap = gap + 5;
			} else if (a1.getItem() == CAItems.TRAILRITE_ARMOR_LEGGINGS.get()) {
				gap = gap + 6;
			}
			if (a2.getItem() == CAItems.SEALEATHER_CHESTPLATE.get()) {
				gap = gap + 4;
			} else if (a2.getItem() == CAItems.SEALEATHER_CHITIN_CHESTPLATE.get()) {
				gap = gap + 4;
			} else if (a2.getItem() == CAItems.TRAILRITE_ARMOR_CHESTPLATE.get()) {
				gap = gap + 4;
			}
			if (a3.getItem() == CAItems.SEALEATHER_HELMET.get()) {
				gap = gap + 2;
			} else if (a3.getItem() == CAItems.SEALEATHER_CHITIN_HELMET.get()) {
				gap = gap + 3;
			} else if (a3.getItem() == CAItems.TRAILRITE_ARMOR_HELMET.get()) {
				gap = gap + 2;
			}
			if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.NETHERSEA_WALKER.get(), a0) != 0) {
				lvl = a0.getEnchantmentLevel(CAEnchantments.NETHERSEA_WALKER.get());
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL.get(), 30, (int) lvl, false, false));
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
					if ((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 3) {
						return;
					}
				}
				if (!(entity instanceof LivingEntity _livEnt28 && _livEnt28.getMobType() == MobType.UNDEAD)) {
					entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "trail_damage")))), 2);
				}
				if (entity instanceof LivingEntity livingEntity) {
					SIHelper.causeSanityInjury(livingEntity, 20);
				}
			}
		}
	}

	//需要解释，大概率需要下放
	public static void endspeakerLinkPtcTo(LevelAccessor world, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		double vx;
		double vy;
		double vz;
		double size;
		vx = toX - fromX;
		vy = toY - fromY;
		vz = toZ - fromZ;
		size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
		if (world instanceof Level _level) {
				_level.playSound(null, BlockPos.containing(fromX, fromY, fromZ), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.enderman.teleport")), SoundSource.HOSTILE, 1, 1);
		}
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(CAParticleTypes.ENDSPEAKER_INV.get(), (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 0.5), (fromZ + (vz / size) * index0), 8, 0.32, 0.5, 0.32, 0.05);
		}
	}

	//同上
	public static void enderinaLinkPtcTo(LevelAccessor world, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		double vx;
		double vy;
		double vz;
		double size;
		vx = toX - fromX;
		vy = toY - fromY;
		vz = toZ - fromZ;
		size = Math.max(Math.min(Math.round(Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2))), 32), 1);
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel serverLevel)
				serverLevel.sendParticles(CAParticleTypes.EDERMAN_PTC.get(), (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 1), (fromZ + (vz / size) * index0), 1, 0, 0, 0, 0.01);
		}
	}

	public static double getNodeLivingBarrier(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_living_barrier;
	}

	//评估是否需要下放甚至内联
	public static String getSilenceMigration(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_migration.description_" + Math.round(MapVariables.get(world).strategy_silence + 5))).getString();
	}

	//同上
	public static double getNodeRealDamage(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_real_damage;
	}

	//同上
	public static double getNodeHealDamage(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_heal_damage;
	}

	//同上
	public static double getNodeWorseBreak(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_worse_break;
	}

	//同上
	public static String getSilenceSubsis(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_subsisting.description_" + Math.round(MapVariables.get(world).strategy_silence + 5))).getString();
	}

	//TODO评估是否需要需要下放或内联，然后处理或跳过
	public static double getComplexPulling(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return 0;
		if ((entity instanceof LivingEntity _entUseItem0 ? _entUseItem0.getUseItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
			return entity instanceof LivingEntity _entUseTicks3 ? _entUseTicks3.getTicksUsingItem() : 0;
		}
		return 0;
	}

	//同上，需要评估
	public static double getNodeAddDamage(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_add_damage;
	}

	public static String getPlayerSurvconta(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR);
	}

	//需要评估是否下放到海嗣的基类
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

	//查看参考文件是怎么做的，很可能需要下放
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

	//需要解释
	public static Entity getGladiiaAround(LevelAccessor world, double x, double y, double z) {
		Entity g = world.getEntitiesOfClass(GladiiaEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
            Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
            }
        }.compareDistOf(x, y, z)).orElse(null);
		if (!(g == null) && g.isAlive()) {
			return g;
		}
		return null;
	}

	//解释并评估是否需要放在其他util类
	public static void givePlayerReserve(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		double r = 0;
		double exp = 0;
		double r_a = 0;
		boolean creative;
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
		if (itemstack.getItem() == CAItems.GENE_SAMPLE_NORMAL.get()) {
			r = 1;
			exp = 1;
		} else if (itemstack.getItem() == CAItems.GENE_SAMPLE_UPGRADED.get()) {
			r = 4;
			exp = 2;
		} else if (itemstack.getItem() == CAItems.GENE_SAMPLE_SUPERB.get()) {
			r_a = 1;
			exp = 3;
		}
		if (exp > 0) {
			if ((entity instanceof Player _plr ? _plr.experienceLevel : 0) >= exp || creative) {
				{
					double _setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).reserve_quantity + r;
					entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
						capability.reserve_quantity = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
				{
					double _setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).reserve_quality + r_a;
					entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
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

	//依旧是同类型，这种有很多
	public static String getDescrSubsis(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_subsisting.description_" + Math.round(MapVariables.get(world).strategy_subsisting))).getString();
	}

	//同上
	public static String getSilenceBreed(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_breed.description_" + Math.round(MapVariables.get(world).strategy_silence + 5))).getString();
	}

	//同
	public static double getNodeAddResis(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_add_resis;
	}

	//同
	public static String getPlayerEnrave(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE);
	}

	//需要解释，可能需要参考参考文件来处理
	public static Entity getNearestEnemy(LevelAccessor world, double x, double y, double z, Entity exception0, Entity exception1, Entity obj) {
		if (exception0 == null || exception1 == null || obj == null)
			return null;
		Entity enemy = null;
		double minDist;
		double d;
		minDist = 999;
		for (Entity entityiterator : world.getEntities(obj, new AABB((x + 42), (y + 40), (z + 42), (x - 42), (y - 40), (z - 42)))) {
			if (!(entityiterator instanceof LivingEntity)) {
				continue;
			}
			if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
				continue;
			}
			d = obj.distanceTo(entityiterator);
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

	//他们真的需要放在这里吗
	public static double getStraSilence(LevelAccessor world) {
		return MapVariables.get(world).strategy_silence;
	}

	//他们真的需要放在这里吗
	public static double getStraSubsis(LevelAccessor world) {
		return MapVariables.get(world).strategy_subsisting;
	}

	//他们真的需要放在这里吗
	public static double getStraBreed(LevelAccessor world) {
		return MapVariables.get(world).strategy_breed;
	}

	//他们真的需要放在这里吗
	public static double getStraGrow(LevelAccessor world) {
		return MapVariables.get(world).strategy_grow;
	}

	//他们真的需要放在这里吗
	public static double getStraMigration(LevelAccessor world) {
		return MapVariables.get(world).strategy_migration;
	}

	//需要解释
	public static double getEntityCosine(Entity A, Entity B) {
		if (A == null || B == null)
			return 0;
		return MathUtils.getCosine(B.getX() - A.getX(), B.getZ() - A.getZ(), A.getLookAngle().x, A.getLookAngle().z);
	}

	//需要下放
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

	//同上
	public static double getNodeAddSpeed(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_add_speed;
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
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_add_miss;
	}

	public static String getLiveMaxShown(Entity entity) {
		if (entity == null)
			return "";
		return "/" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive);
	}

	public static double getNodeEutectes(Entity entity) {
		if (entity == null)
			return 0;
		return (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_eunectes;
	}

	public static String getSilenceGrow(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_grow.description_" + Math.round(MapVariables.get(world).strategy_silence + 5))).getString();
	}

	public static void giveSpearFight(Entity entity) {
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.SPEAR_FIGHT.get()))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CAMobEffects.SPEAR_FIGHT.get(), 60, 0, false, false));
		}
	}

	public static String getDescrBreed(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_breed.description_" + Math.round(MapVariables.get(world).strategy_breed))).getString();
	}

	public static String getDescrGrow(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_grow.description_" + Math.round(MapVariables.get(world).strategy_grow))).getString();
	}

	public static double getSlimeSize(Entity entity) {
		if (entity == null)
			return 0;
		return (entity instanceof NetherseaSlimeEntity _datEntI ? _datEntI.getEntityData().get(NetherseaSlimeEntity.DATA_SIZE) : 0) * 0.5;
	}

	//TODO:可能需要下放到药水类
	public static void giveLessArmor(Entity obj, double limit) {
		if (obj == null)
			return;
		if (obj instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.LESS_ARMOR.get())) {
			if ((obj instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) < limit) {
				if (obj instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CAMobEffects.LESS_ARMOR.get(), 300,
                            (obj instanceof LivingEntity _livEnt && _livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? _livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) + 1, false, true));
			}
		} else {
			if (obj instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CAMobEffects.LESS_ARMOR.get(), 300, 0, false, true));
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
					_entity.setHealth((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + 3);
				for (int index0 = 0; index0 < 2; index0++) {
					giveLessArmor(entityiterator, 18);
				}
			}
		}
	}

	public static String getDescrMigra(LevelAccessor world) {
		return Component.translatable(("item.caerula_arbor.sample_migration.description_" + Math.round(MapVariables.get(world).strategy_migration))).getString();
	}

	public static void gainLessSpeed(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CAMobEffects.ADD_REACH.get()))) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 20, 3, false, false));
		}
	}

	public static void giveGuideLay(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.5) {
			if (!(entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(CAMobEffects.MUTE.get()))) {
				if (!(entity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(CAMobEffects.GUIDE_PATH_AHEAD.get()))) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(CAMobEffects.GUIDE_PATH_AHEAD.get(), 20, 0));
				}
			}
		}
	}

	public static String getLives(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_lives);
	}

	public static String getShield(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_shield);
	}

	public static String getPalsy(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round(entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.NUMB.get()) ? _livingEntity0.getAttribute(CAAttributes.NUMB.get()).getBaseValue() : 0);
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
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light);
	}

	public static Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
		return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
	}

	//TODO 需要下放 评估是否应该分别为方块和实体添加辅助类
	public static void gladiiaLinkPtcToEntity(LevelAccessor world, Entity entity, Entity tgt) {
		if (entity == null || tgt == null)
			return;
        double fromX = entity.getX();
        double fromY = entity.getY();
        double fromZ = entity.getZ();
        double vx;
        double vy;
        double vz;
        double size;
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

	//需要注释
	public static boolean inquirybility(LevelAccessor world, double index) {
		int comparator = (int) Math.pow(2, index);
		int inq = (int) MapVariables.get(world).endspeaker_abolities & comparator;
		return inq == comparator;
	}

	//需要解释
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

	//TODO:需要下放
	public static void initHunter(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()))
			_livingEntity1.getAttribute(ForgeMod.SWIM_SPEED.get())
					.setBaseValue(((entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(ForgeMod.SWIM_SPEED.get()) ? _livingEntity0.getAttribute(ForgeMod.SWIM_SPEED.get()).getBaseValue() : 0) * 8));
		if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(CAAttributes.SANITY_MODIFIER.get()))
			_livingEntity2.getAttribute(CAAttributes.SANITY_MODIFIER.get()).setBaseValue(0.33);
	}

	public static void wardenRangedAttack(LevelAccessor world, Entity obj, boolean isSonic, double rate, double xx, double yy, double zz) {
		if (obj == null)
			return;
		Entity enemy;
		double damage;
		double r;
		enemy = obj instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
		r = 3;
		if (isSonic) {
			r = 4.5;
		}
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
					if (entityiterator instanceof LivingEntity livingEntity) {
						SIHelper.causeSanityInjury(livingEntity, damage * 1.5);
					}
				} else {
					entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "warden_attack"))), obj),
							(float) damage);
				}
			}
		}
	}

	//TODO:需要下放回实体，理念同WorldUtils的对凋零的处理
	public static void wardenSonicBoom(LevelAccessor world, Entity obj, Entity target) {
		if (obj == null || target == null)
			return;
		double vx;
		double vy;
		double vz;
		double len;
		double tx;
		double ty;
		double tz;
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

	//TODO:下放，同处理
	public static void wardenLightBoom(LevelAccessor world, Entity obj, Entity target) {
		if (obj == null || target == null)
			return;
		double vx;
		double vy;
		double vz;
		double len;
		double tx;
		double ty;
		double tz;
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

	//TODO:制作他俩的基类并下放
	public static void dropWardenExp(LevelAccessor world, double x, double y, double z) {
		if (world.getLevelData().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			for (int index0 = 0; index0 < 64; index0++) {
				if (world instanceof ServerLevel _level)
					_level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), y, (z + Mth.nextDouble(RandomSource.create(), -1, 1)), Mth.nextInt(RandomSource.create(), 32, 64)));
			}
		}
	}

	//TODO:endspeaker的几个类可以合并为一个类，使用状态机，模型和渲染就参考原版mc的河豚
	public static void endspeakerTick(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double missR;
		if (entity.tickCount % 5 == 0) {
			if (inquirybility(world, 2) && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) < (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.4) {
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25, 0, false, false));
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(CAMobEffects.ENDSPEAER_BRANDGUIDE_BUFF.get(), 25, 0));
			}
			if (inquirybility(world, 4)) {
				entity.clearFire();
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(CAMobEffects.DIZZY.get());
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(CAMobEffects.MUTE.get());
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(MobEffects.WEAKNESS);
				if (entity instanceof LivingEntity _entity)
					_entity.removeEffect(CAMobEffects.FROZEN.get());
				entity.setTicksFrozen(0);
				if (entity.tickCount % 200 == 0 && !(entity instanceof LivingEntity _livEnt13 && _livEnt13.hasEffect(CAMobEffects.ESSENCE_RESISTANCE.get()))) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(CAMobEffects.ESSENCE_RESISTANCE.get(), 180, 2, false, false));
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
				if (entity instanceof LivingEntity _livEnt19 && _livEnt19.hasEffect(CAMobEffects.DIZZY.get()) || entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(CAMobEffects.FROZEN.get())
						|| entity instanceof LivingEntity _livEnt21 && _livEnt21.hasEffect(MobEffects.LEVITATION) || entity instanceof LivingEntity _livEnt22 && _livEnt22.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
						|| entity instanceof LivingEntity _livEnt23 && _livEnt23.hasEffect(MobEffects.SLOW_FALLING)) {
					missR = 0;
				}
				if (entity instanceof LivingEntity _livingEntity24 && _livingEntity24.getAttributes().hasAttribute(CAAttributes.MISSRATE.get()))
					_livingEntity24.getAttribute(CAAttributes.MISSRATE.get()).setBaseValue(missR);
			}
		}
	}

	//需要解释
	public static void hurtMartus(LevelAccessor world, Entity obj, Entity source, double num, double perc) {
		if (obj == null)
			return;
		double amount;
		amount = (obj instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc + num;
		if (amount > 0) {
			obj.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inv_killer"))), source), (float) amount);
		}
	}

	public static void igniteRouteshaper(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(CAAttributes.MAGIC_RESISTANCE.get()))
			_livingEntity0.getAttribute(CAAttributes.MAGIC_RESISTANCE.get()).setBaseValue(24);
		if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 16, 16, 16), e -> true).isEmpty()) {
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

	//需要解释
	public static final TagKey<EntityType<?>> HUMAN = TagKey.create(
			Registries.ENTITY_TYPE,
			new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")
		);

	public static final TagKey<EntityType<?>> OCEAN_OFFSPRING = TagKey.create(
			Registries.ENTITY_TYPE,
			new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")
		);

	//可能需要评估，低优先级
	public static void killSelf(LevelAccessor world, Entity entity, Entity immediatesourceentity) {
		if (entity == null || immediatesourceentity == null)
			return;
		entity.invulnerableTime = 0;
		CaerulaArborMod.queueServerWork(2, () -> {
			if (!immediatesourceentity.level().isClientSide())
				immediatesourceentity.discard();
		});
	}

	//需要解释，特别可疑
	public static void vanguardBuff(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double less = 0;
		if (entity.tickCount % 20 == 10) {
			if (!(entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CAMobEffects.INFANTRY.get()))) {
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
						_entity.addEffect(new MobEffectInstance(CAMobEffects.INFANTRY.get(), 40, (int) (less - 1)));
				}
			}
		}
	}

	//TODO可以放入海嗣的基类,参考参考文件
	public static boolean isOceanizedPlayerNearby(LevelAccessor world, double x, double y, double z) {
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(72 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof Player) {
					if ((entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 2.9) {
						return false;
					}
				}
			}
		}
		return true;
	}

	//需要解释
	public static void turnRounds(Entity another, Entity me) {
		if (another == null || me == null)
			return;
		Vec3 offset = another.position().add(me.position().reverse());
		if (offset.lengthSqr() <= 0.25)
			return;
		Vec3 delta = new Vec3(-offset.z, 0.15, offset.x).normalize().scale(0.5);
		another.setDeltaMovement(delta);
	}

	//可疑，需要解释并评估怎么处理
	public static void pullToGladiia(Entity another, Entity me) {
		if (another == null || me == null)
			return;
		Vec3 offset = me.position().add(another.position().reverse());
		if (offset.lengthSqr() <= 0.01)
			return;
		offset = offset.normalize().scale(1.5);
		another.push(offset.x, offset.y, offset.z);
	}

	//需要评估
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

	//评估他和接口的关系是否合适
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

	//TODO需要下放回OceanizedEndermanEntity,LivingAttackEventHandler之后需要整个处理
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
				_level.sendParticles(CAParticleTypes.EDERMAN_PTC.get(), (fromX + (vx / size) * index0), (fromY + (vy / size) * index0 + 0.65), (fromZ + (vz / size) * index0), 32, 0.65, 0.65, 0.65, 0.05);
		}
		entity.teleportTo(toX, toY, toZ);
		if (entity instanceof ServerPlayer _serverPlayer)
			_serverPlayer.connection.teleport(toX, toY, toZ, entity.getYRot(), entity.getXRot());
		entity.clearFire();
	}
}
