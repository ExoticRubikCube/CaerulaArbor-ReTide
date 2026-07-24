package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.sanity.SIHelper;
import com.susen36.caerulaarbor.entity.NetherseaSlimeEntity;
import com.susen36.caerulaarbor.entity.OceanIllusionEntity;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.Comparator;
import java.util.List;

public class EntityUtils {

	private EntityUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	// TODO: 评估是否迁移到更合适的粒子工具位置
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
		double minDist = -1.0D;
		double d;
		for (LivingEntity entityiterator : world.getEntitiesOfClass(LivingEntity.class, new AABB((x + 4), (y + 4), (z + 4), (x - 4), (y - 4), (z - 4)))) {
			if (entityiterator instanceof Monster || (entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == obj) {
				d = obj.distanceToSqr(entityiterator);
				if (d <= 16.0D) {
					if (minDist == -1.0D || d < minDist) {
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
		double setval = snt;
		player.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
			capability.player_light = setval;
			capability.syncPlayerVariables(player);
		});
	}

	public static boolean isAlive(Entity entity) {
		return entity != null && entity.isAlive();
	}

	public static void heal(LivingEntity entity, double amount) {
		if (entity != null)
			entity.heal((float) amount);
	}

	public static void healWithParticles(LevelAccessor world, Entity entity, double flatAmount, double maxHealthMultiplier) {
		if (entity == null || !entity.isAlive())
			return;
		if (entity instanceof LivingEntity living) {
			double maxHealth = living.getMaxHealth();
			heal(living, maxHealth * maxHealthMultiplier + flatAmount);
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
		} else if (entity instanceof Player && entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable()).player_oceanization >= 3) {
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

	// 施加 Nethseabrand 的伤害效果，可能需要做成接口
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
			if (!(entity instanceof LivingEntity livEnt1 && livEnt1.hasEffect(CAMobEffects.TRAIL_BUFF.get()))) {
				if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
					livingEntity.addEffect(new MobEffectInstance(CAMobEffects.TRAIL_BUFF.get(), 10, 0, false, false));
			}
			gap = 20;
			a0 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).copy();
			a1 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).copy();
			a2 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).copy();
			a3 = (entity instanceof LivingEntity entGetArmor ? entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).copy();
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
				if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
					livingEntity.addEffect(new MobEffectInstance(CAMobEffects.RUNNING_ON_TRAIL.get(), 30, (int) lvl, false, false));
				if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
					livingEntity.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 0, false, false));
			}
			if (entity.tickCount % gap == 0 && !entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "immue_to_nethersea_brand")))) {
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
				if (!(entity instanceof LivingEntity livEnt28 && livEnt28.getMobType() == MobType.UNDEAD)) {
					entity.hurt(CADamageTypes.source(world, CADamageTypes.TRAIL_DAMAGE), 2);
				}
				if (entity instanceof LivingEntity livingEntity) {
					SIHelper.causeSanityInjury(livingEntity, 20);
				}
			}
		}
	}

	// 同上，需评估
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
		final Vec3 searchCenter = new Vec3(x, y, z);
		List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(searchCenter, searchCenter).inflate(32 / 2d),
				e -> e != center && e.getType().is(OCEAN_OFFSPRING)
						&& !e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "bossoffspring")))
						&& !e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet"))));
		for (LivingEntity entityiterator : entfound) {
			count = count + 1;
		}
		return count;
	}

	public static double getSeabornNum(LevelAccessor world, double x, double y, double z) {
		double count = 0;
		final Vec3 center = new Vec3(x, y, z);
		List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(32 / 2d),
				e -> e.getType().is(OCEAN_OFFSPRING)
						&& !e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "bossoffspring")))
						&& !e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet"))));
		for (LivingEntity entityiterator : entfound) {
			count = count + 1;
		}
		return count;
	}

	// 给玩家发放储备相关物品
	public static void givePlayerReserve(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		double r = 0;
		double exp = 0;
		double r_a = 0;
		boolean creative;
		creative = new Object() {
			public boolean checkGamemode(Entity ent) {
				if (ent instanceof ServerPlayer serverPlayer) {
					return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (ent.level().isClientSide() && ent instanceof Player player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
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
			if ((entity instanceof Player plr ? plr.experienceLevel : 0) >= exp || creative) {
				{
					double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).reserve_quantity + r;
					entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
						capability.reserve_quantity = setval;
						capability.syncPlayerVariables(entity);
					});
				}
				{
					double setval = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).reserve_quality + r_a;
					entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
						capability.reserve_quality = setval;
						capability.syncPlayerVariables(entity);
					});
				}
				if (!creative) {
					if (entity instanceof Player player)
						player.giveExperienceLevels(-((int) exp));
				}
				if (world instanceof Level level) {
						level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 2, 1);
				}
				itemstack.shrink(1);
			} else {
				if (entity instanceof Player player && !player.level().isClientSide())
					player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.gene_sample.no_exp").getString())), true);
			}
		}
	}

	// 获取玩家的相关记录值
	public static String getPlayerEnrave(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE);
	}

	public static Entity getNearestEnemy(LevelAccessor world, double x, double y, double z, Entity exception0, Entity exception1, Entity obj) {
		if (exception0 == null || exception1 == null || obj == null)
			return null;
		Entity enemy = null;
		double minDist = -1.0D;
		double d;
		for (LivingEntity entityiterator : world.getEntitiesOfClass(LivingEntity.class, new AABB((x + 42), (y + 40), (z + 42), (x - 42), (y - 40), (z - 42)))) {
			if (entityiterator.getType().is(OCEAN_OFFSPRING)) {
				continue;
			}
			d = obj.distanceToSqr(entityiterator);
			if (d <= 1764.0D) {
				if (entityiterator == exception0) {
					continue;
				}
				if (entityiterator == exception1) {
					continue;
				}
				if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "sea_friend")))) {
					continue;
				}
				if (new Object() {
					public boolean checkGamemode(Entity ent) {
						if (ent instanceof ServerPlayer serverPlayer) {
							return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (ent.level().isClientSide() && ent instanceof Player player) {
							return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
									&& Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(entityiterator)) {
					continue;
				}
				if (minDist == -1.0D || d < minDist) {
					minDist = d;
					enemy = entityiterator;
				}
			}
		}
		return enemy;
	}

	// 计算两个实体之间的朝向余弦值
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
			final Vec3 center = new Vec3(x, y, z);
			List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(12 / 2d),
					e -> e != entity && e.getType() == entity.getType());
			for (LivingEntity entityiterator : entfound) {
				num = num + 1;
			}
		}
		return num;
	}

	public static double getIllusionNum(LevelAccessor world, double x, double y, double z) {
		final Vec3 center = new Vec3(x, y, z);
		return world.getEntitiesOfClass(OceanIllusionEntity.class, new AABB(center, center).inflate(48 / 2d), e -> true).size();
	}

	public static String getLiveMaxShown(Entity entity) {
		if (entity == null)
			return "";
		return "/" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_maxlive);
	}

	public static void giveSpearFight(Entity entity) {
		if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.SPEAR_FIGHT.get()))) {
			if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
				livingEntity.addEffect(new MobEffectInstance(CAMobEffects.SPEAR_FIGHT.get(), 60, 0, false, false));
		}
	}

	public static double getSlimeSize(Entity entity) {
		if (entity == null)
			return 0;
		return (entity instanceof NetherseaSlimeEntity datEntI ? datEntI.getEntityData().get(NetherseaSlimeEntity.DATA_SIZE) : 0) * 0.5;
	}

	// TODO: 护甲削减逻辑仍需进一步评估
	public static void giveLessArmor(Entity obj, double limit) {
		if (obj == null)
			return;
		if (obj instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.LESS_ARMOR.get())) {
			if ((obj instanceof LivingEntity livEnt && livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) < limit) {
				if (obj instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
					livingEntity.addEffect(new MobEffectInstance(CAMobEffects.LESS_ARMOR.get(), 300,
                            (obj instanceof LivingEntity livEnt && livEnt.hasEffect(CAMobEffects.LESS_ARMOR.get()) ? livEnt.getEffect(CAMobEffects.LESS_ARMOR.get()).getAmplifier() : 0) + 1, false, true));
			}
		} else {
			if (obj instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
				livingEntity.addEffect(new MobEffectInstance(CAMobEffects.LESS_ARMOR.get(), 300, 0, false, true));
		}
	}

	public static void repellerChop(LevelAccessor world, double x, double y, double z, Entity entity, double rate) {
		if (entity == null)
			return;
		if (world instanceof Level level) {
				level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 2, 1);
		}
		{
			final Vec3 center = new Vec3((x + 1.8 * entity.getLookAngle().x), (y + 1.5), (z + 1.8 * entity.getLookAngle().z));
			List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(5 / 2d), e -> true);
			for (LivingEntity entityiterator : entfound) {
				if (entityiterator.getType().is(OCEAN_OFFSPRING)) {
					if (!((entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entityiterator)) {
						continue;
					}
				}
				entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.REPELLER_ATTACK, entity), (float) ((entity instanceof LivingEntity livingEntity7 && livingEntity7.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity7.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * rate));
				if (entity instanceof LivingEntity livingEntity)
					livingEntity.setHealth(livingEntity.getHealth() + 3);
				for (int index0 = 0; index0 < 2; index0++) {
					giveLessArmor(entityiterator, 18);
				}
			}
		}
	}

	public static void gainLessSpeed(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.ADD_REACH.get()))) {
			if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
				livingEntity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 20, 3, false, false));
		}
	}

	public static void giveGuideLay(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
			if (!(entity instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.MUTE.get()))) {
				if (!(entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.GUIDE_PATH_AHEAD.get()))) {
					if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
						livingEntity.addEffect(new MobEffectInstance(CAMobEffects.GUIDE_PATH_AHEAD.get(), 20, 0));
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
		return "" + Math.round(entity instanceof LivingEntity livingEntity0 && livingEntity0.getAttributes().hasAttribute(CAAttributes.NUMB.get()) ? livingEntity0.getAttribute(CAAttributes.NUMB.get()).getBaseValue() : 0);
	}

	public static String getHealth(Entity entity) {
		if (entity == null)
			return "";
		return (new java.text.DecimalFormat("##.##").format(entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1)) + "/"
				+ (new java.text.DecimalFormat("##.#").format(entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1));
	}

	public static double getHealthPerc(Entity entity) {
		if (entity == null)
			return 0;
		return (entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1);
	}

	public static String getLight(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_light);
	}

	public static Comparator<Entity> compareDistOf(double x, double y, double z) {
		return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
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

	// 对 Martus 施加伤害逻辑
	public static void hurtMartus(LevelAccessor world, Entity obj, Entity source, double num, double perc) {
		if (obj == null)
			return;
		double amount;
		amount = (obj instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * perc + num;
		if (amount > 0) {
			obj.hurt(CADamageTypes.source(obj.level(), CADamageTypes.INV_KILLER, source), (float) amount);
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

	// 人类实体标签
	public static final TagKey<EntityType<?>> HUMAN = TagKey.create(
			Registries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "is_humanside")
		);

	public static final TagKey<EntityType<?>> OCEAN_OFFSPRING = TagKey.create(
			Registries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")
		);

	// 触发自杀伤害
	public static void killSelf(LevelAccessor world, Entity entity, Entity immediatesourceentity) {
		if (entity == null || immediatesourceentity == null)
			return;
		entity.invulnerableTime = 0;
		CaerulaArborMod.queueServerWork(2, () -> {
			if (!immediatesourceentity.level().isClientSide())
				immediatesourceentity.discard();
		});
	}

	// 应用先锋增益
	public static void vanguardBuff(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double less = 0;
		if (entity.tickCount % 20 == 10) {
			if (!(entity instanceof LivingEntity livEnt1 && livEnt1.hasEffect(CAMobEffects.INFANTRY.get()))) {
				{
					final Vec3 center = new Vec3(x, y, z);
					List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(16 / 2d),
							e -> e != entity && e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "phalax"))));
					for (LivingEntity entityiterator : entfound) {
						less = less + 1;
						if (less >= 10) {
							break;
						}
					}
				}
				if (less > 0) {
					if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
						livingEntity.addEffect(new MobEffectInstance(CAMobEffects.INFANTRY.get(), 40, (int) (less - 1)));
				}
			}
		}
	}

	public static boolean isOceanizedPlayerNearby(LevelAccessor world, double x, double y, double z) {
		final Vec3 center = new Vec3(x, y, z);
		List<Player> entfound = world.getEntitiesOfClass(Player.class, new AABB(center, center).inflate(72 / 2d), e -> true);
		for (Player entityiterator : entfound) {
			if ((entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 2.9) {
				return false;
			}
		}
		return true;
	}

	// 应用环绕运动
	public static void applyOrbitMotion(Entity another, Entity me) {
		if (another == null || me == null)
			return;
		Vec3 offset = another.position().add(me.position().reverse());
		if (offset.lengthSqr() <= 0.25)
			return;
		Vec3 delta = new Vec3(-offset.z, 0.15, offset.x).normalize().scale(0.5);
		another.setDeltaMovement(delta);
	}

	// 将目标拉向自身
	public static void pullToward(Entity another, Entity me) {
		if (another == null || me == null)
			return;
		Vec3 offset = me.position().add(another.position().reverse());
		if (offset.lengthSqr() <= 0.01)
			return;
		offset = offset.normalize().scale(1.5);
		another.push(offset.x, offset.y, offset.z);
	}

	// 清除实体当前目标
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

}
