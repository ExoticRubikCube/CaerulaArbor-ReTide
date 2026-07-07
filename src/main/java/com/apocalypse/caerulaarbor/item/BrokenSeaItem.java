package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.entity.GladiiaEntity;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class BrokenSeaItem extends SwordItem {
	public BrokenSeaItem() {
		super(new Tier() {
			public int getUses() {
				return 0;
			}

			public float getSpeed() {
				return 9f;
			}

			public float getAttackDamageBonus() {
				return 17f;
			}

			public int getLevel() {
				return 3;
			}

			public int getEnchantmentValue() {
				return 12;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -3f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if ((sourceentity instanceof Player plr ? plr.getAttackStrengthScale(0) : 0) > 0.9) {
            if (Math.random() < 0.5 && !(entity instanceof Player)) {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_ATTACK_HIT.get(), SoundSource.PLAYERS, 1, 1);
                }
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(CAMobEffects.HAEMOPHILIA.get(), 260, 1, false, false));
            }
        }
        return retval;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        double damage;
        double count = 0;
        if (!((Entity) entity instanceof Player plrCldCheck1 && plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            if (entity.isShiftKeyDown() && ((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 5) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_SKILL_RELEASE.get(), SoundSource.PLAYERS, 2, 1);
                }
                if ((LevelAccessor) world instanceof ServerLevel level) {
                    Entity entityToSpawn = CAEntities.GLADIIA_WHIRL.get().spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                    }
                }
                if (!(new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity))) {
                    if ((Entity) entity instanceof Player player)
                        player.getCooldowns().addCooldown(itemstack.getItem(), 400);
                    if ((Entity) entity instanceof Player player)
                        player.giveExperienceLevels(-(5));
                }
            } else {
                damage = entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(12 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator instanceof Monster || (entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entity) {
                        if (entity.distanceTo(entityiterator) <= 6) {
                            EntityUtils.pullToward(entityiterator, entity);
                            GladiiaEntity.spawnGladiiaLinkParticles(world, entity, entityiterator);
                            LivingEntity livingEntity = (LivingEntity) entityiterator;
                            if (!livingEntity.level().isClientSide())
                                livingEntity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 40, 0, false, false));
                            entityiterator.hurt(CADamageTypes.source((LevelAccessor) world, CADamageTypes.HUNTER_ATTACK, entity), (float) (damage * 3));
                            count = count + 1;
                        }
                    }
                    if (count >= 6) {
                        break;
                    }
                }
                if (count > 0 && !((LevelAccessor) world).isClientSide()) {
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), CASounds.GLADIIA_PULL_PULL.get(), SoundSource.PLAYERS, 2, 1);
                    }
                    if (!(new Object() {
                        public boolean checkGamemode(Entity ent) {
                            if (ent instanceof ServerPlayer serverPlayer) {
                                return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                            } else if (ent.level().isClientSide() && ent instanceof Player player) {
                                return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                        && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                            }
                            return false;
                        }
                    }.checkGamemode((Entity) entity))) {
                        if ((Entity) entity instanceof Player player)
                            player.getCooldowns().addCooldown(itemstack.getItem(), 140);
                    }
                }
            }
        }
        return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.broken_sea.description_0"));
		list.add(Component.translatable("item.caerula_arbor.broken_sea.description_1"));
		list.add(Component.translatable("item.caerula_arbor.broken_sea.description_2"));
		list.add(Component.translatable("item.caerula_arbor.broken_sea.description_3"));
	}
}
