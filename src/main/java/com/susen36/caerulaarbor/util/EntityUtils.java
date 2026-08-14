package com.susen36.caerulaarbor.util;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAEntityTypeTags;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.List;

public class EntityUtils {

	private EntityUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void restorePlayerLights(Entity player, double num) {
		if (player == null)
			return;
		if (num <= 0)
			return;
		double snt = ModCapabilities.getPlayerVariables(player).player_light + num;
		if (snt > 100) {
			snt = 100;
		}
		double setval = snt;
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		capability.player_light = setval;
		capability.syncPlayerVariables(player);
	}

	public static boolean isAlive(Entity entity) {
		return entity != null && entity.isAlive();
	}

	public static void healWithParticles(LevelAccessor world, Entity entity, double flatAmount, double maxHealthMultiplier) {
		if (entity == null || !entity.isAlive())
			return;
		if (entity instanceof LivingEntity living) {
			double maxHealth = living.getMaxHealth();
			living.heal((float) (maxHealth * maxHealthMultiplier + flatAmount));
			if (world instanceof ServerLevel level) {
				level.sendParticles(ParticleTypes.CHERRY_LEAVES,
					entity.getX(), entity.getY() + 1, entity.getZ(),
					16, 1, 1, 1, 0.1);
			}
		}
	}

	//需要评估是否下放到海嗣的基类
	public static double getSeabornAround(Level world, double x, double y, double z, Entity center) {
		if (center == null) {
			return 0;
		}
		final Vec3 searchCenter = new Vec3(x, y, z);
		List<LivingEntity> entfound = world.getEntitiesOfClass(
				LivingEntity.class,
				new AABB(searchCenter, searchCenter).inflate(16.0),
				e -> e != center && e.getType().is(CAEntityTypeTags.SEABORN) && !e.getType().is(CAEntityTypeTags.SEABORN_BOSS) && !e.getType().is(CAEntityTypeTags.SEABORN_MINION)
		);
		double count = 0;
		for (LivingEntity ignored : entfound) {
			count = count + 1;
		}
		return count;
	}

	public static double getSeabornNum(Level world, double x, double y, double z) {
		double count = 0;
		final Vec3 center = new Vec3(x, y, z);
		List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(32 / 2d), e -> e.getType().is(CAEntityTypeTags.SEABORN) && !e.getType().is(CAEntityTypeTags.SEABORN_BOSS) && !e.getType().is(CAEntityTypeTags.SEABORN_PET));
		for (LivingEntity ignored : entfound) {
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
					PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                    capability.reserve_quantity = capability.reserve_quantity + r;
					capability.syncPlayerVariables(entity);
				}
				{
					PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                    capability.reserve_quality = capability.reserve_quality + r_a;
					capability.syncPlayerVariables(entity);
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

	public static Entity getNearestEnemy(LevelAccessor world, double x, double y, double z, Entity exception0, Entity exception1, Entity obj) {
		if (exception0 == null || exception1 == null || obj == null)
			return null;
		Entity enemy = null;
		double minDist = -1.0D;
		double d;
		for (LivingEntity entityiterator : world.getEntitiesOfClass(LivingEntity.class, new AABB((x + 42), (y + 40), (z + 42), (x - 42), (y - 40), (z - 42)))) {
			if (entityiterator.getType().is(CAEntityTypeTags.SEABORN)) {
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
				if (entityiterator.getType().is(CAEntityTypeTags.SEA_FRIEND)) {
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

	public static String getLiveMaxShown(Entity entity) {
		if (entity == null)
			return "";
		return "/" + Math.round((ModCapabilities.getPlayerVariables(entity)).player_maxlive);
	}

	public static void giveSpearFight(Entity entity) {
		if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.SPEAR_FIGHT))) {
			if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
				livingEntity.addEffect(new MobEffectInstance(CAMobEffects.SPEAR_FIGHT, 60, 0, false, false));
		}
	}

	public static void gainLessSpeed(Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.ADD_REACH))) {
			if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
				livingEntity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 20, 3, false, false));
		}
	}

	public static void giveGuideLay(Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
			if (!(entity instanceof LivingEntity livEnt2 && livEnt2.hasEffect(CAMobEffects.MUTE))) {
				if (!(entity instanceof LivingEntity livEnt3 && livEnt3.hasEffect(CAMobEffects.GUIDE_PATH_AHEAD))) {
					if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
						livingEntity.addEffect(new MobEffectInstance(CAMobEffects.GUIDE_PATH_AHEAD, 20, 0));
				}
			}
		}
	}

	public static String getLives(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((ModCapabilities.getPlayerVariables(entity)).player_lives);
	}

	public static String getShield(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((ModCapabilities.getPlayerVariables(entity)).player_shield);
	}

	public static double getHealthPerc(LivingEntity living) {
		float maxHealth = living.getMaxHealth();
		if (maxHealth <= 0.0f) {
			return 0.0;
		}
		return Math.clamp(living.getHealth() / (double) maxHealth, 0.0, 1.0);
	}

	public static String getLight(Entity entity) {
		if (entity == null)
			return "";
		return "" + Math.round((ModCapabilities.getPlayerVariables(entity)).player_light);
	}

	public static double getSpeed(Entity e) {
		if (e == null)
			return 0;
		return Math.sqrt(e.getDeltaMovement().x() * e.getDeltaMovement().x() + e.getDeltaMovement().y() * e.getDeltaMovement().y() + e.getDeltaMovement().z() * e.getDeltaMovement().z());
	}

	public static boolean isSameTeam(Entity a, Entity b) {
		if (a == null || b == null) {
			return false;
		}
		Team teamA = a.getTeam();
		Team teamB = b.getTeam();
		return teamA != null && teamB != null && teamA.isAlliedTo(teamB);
	}

	public static void vanguardBuff(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double less = 0;
		if (entity.tickCount % 20 == 10) {
			if (!(entity instanceof LivingEntity livEnt1 && livEnt1.hasEffect(CAMobEffects.INFANTRY))) {
				final Vec3 center = new Vec3(x, y, z);
				List<LivingEntity> entfound = world.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(16 / 2d),
						e -> e != entity && e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "phalax"))));
				for (LivingEntity ignored : entfound) {
					less = less + 1;
					if (less >= 10) {
						break;
					}
				}
				if (less > 0) {
					if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
						livingEntity.addEffect(new MobEffectInstance(CAMobEffects.INFANTRY, 40, (int) (less - 1)));
				}
			}
		}
	}

	public static boolean isOceanizedPlayer(Entity entity) {
		return entity instanceof Player player
				&& ModCapabilities.getPlayerVariables(player).player_oceanization >= 2.9D;
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

}