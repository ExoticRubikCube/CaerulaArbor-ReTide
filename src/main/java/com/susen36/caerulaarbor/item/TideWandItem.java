
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.bullets.TellerShotEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TideWandItem extends Item {
	public TideWandItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 2f;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 4d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.4, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.tide_wand.description_0"));
		list.add(Component.translatable("item.caerula_arbor.tide_wand.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!(entity instanceof Player plrCldCheck1 && plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem())) && (new Object() {
            public boolean checkGamemode(Entity ent) {
                if (ent instanceof ServerPlayer serverPlayer) {
                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) entity) || ((Entity) entity instanceof Player plr ? plr.totalExperience : 0) >= 5)) {
            {
                Level projectileLevel = entity.level();
                if (!projectileLevel.isClientSide()) {
                    Projectile entityToSpawn = new Object() {
                        public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                            AbstractArrow entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), level);
                            entityToSpawn.setOwner(shooter);
                            entityToSpawn.setBaseDamage(damage);
                            entityToSpawn.setKnockback(knockback);
                            entityToSpawn.setSilent(true);
                            return entityToSpawn;
                        }
                    }.getArrow(projectileLevel, (Entity) entity,
                            (float) ((Entity) entity instanceof LivingEntity livingEntity4 && livingEntity4.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity4.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0), 0);
                    entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
                    entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, (float) 2.2, 0);
                    projectileLevel.addFreshEntity(entityToSpawn);
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
                    player.giveExperiencePoints(-(5));
            }
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, 1, 1);
            }
            if ((Entity) entity instanceof Player player)
                player.getCooldowns().addCooldown(itemstack.getItem(), 55);
        }
    }
}
