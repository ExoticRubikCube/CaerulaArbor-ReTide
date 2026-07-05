package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.entity.bullets.AnchorFlyEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

public class UnambiguousDirectionItem extends Item {
	public UnambiguousDirectionItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.SPEAR;
	}

	@Override
	public int getEnchantmentValue() {
		return 16;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 6f;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 26d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -3.15, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.unambiguous_direction.description_0"));
		list.add(Component.translatable("item.caerula_arbor.unambiguous_direction.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (sourceentity.hasEffect(CAMobEffects.PATH_TO_UNCOVER.get())) {
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_SKILLATTACK.get(), SoundSource.PLAYERS, 2, 1);
            }
        } else {
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_ATTACK.get(), SoundSource.PLAYERS, 2, 1);
            }
        }
        return retval;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			EntityUtils.gainLessSpeed(entity);
	}

	@Override
	public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!(entity instanceof Player plrCldCheck1 && plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.ANCHOR_THROW.get(), SoundSource.PLAYERS, (float) 1.8, 1);
            }
            Level projectileLevel = entity.level();
            if (!projectileLevel.isClientSide()) {
                Projectile entityToSpawn = new Object() {
                    public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                        AbstractArrow entityToSpawn = new AnchorFlyEntity(CAEntities.ANCHOR_FLY.get(), level);
                        entityToSpawn.setOwner(shooter);
                        entityToSpawn.setBaseDamage(damage);
                        entityToSpawn.setKnockback(knockback);
                        entityToSpawn.setSilent(true);
                        entityToSpawn.setCritArrow(true);
                        return entityToSpawn;
                    }
                }.getArrow(projectileLevel, (Entity) entity,
                        (float) ((Entity) entity instanceof LivingEntity livingEntity3 && livingEntity3.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity3.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0), 0);
                entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
                entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, (float) 2.4, 0);
                projectileLevel.addFreshEntity(entityToSpawn);
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
                    player.getCooldowns().addCooldown(itemstack.getItem(), 900);
            }
        }
    }
}
