
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.bullets.TellerShotEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;


public class TidelinkedWandItem extends Item {
	public TidelinkedWandItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON).attributes(createAttributes()));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 72000;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 3f;
	}

	private static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "tidelinked_wand_attack_damage"), 8D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "tidelinked_wand_attack_speed"), -2.4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.tidelinked_wand.description_0"));
		list.add(Component.translatable("item.caerula_arbor.tidelinked_wand.description_1"));
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
        double damage;
        if (!(entity instanceof Player plrCldCheck1 && plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem())) && (((Entity) entity instanceof Player plr ? plr.totalExperience : 0) >= 10 || new Object() {
            public boolean checkGamemode(Entity ent) {
                if (ent instanceof ServerPlayer serverPlayer) {
                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) entity))) {
            damage = (Entity) entity instanceof LivingEntity livingEntity4 && livingEntity4.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity4.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
            Level projectileLevel = entity.level();
            if (!projectileLevel.isClientSide()) {
                AbstractArrow entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), projectileLevel);
                entityToSpawn.setOwner((Entity) entity);
                entityToSpawn.setBaseDamage((float) (damage * 1));
                entityToSpawn.setKnockback(0);
                entityToSpawn.setSilent(true);
                entityToSpawn.setPierceLevel((byte) 1);
                entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
                entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, (float) 2.65, 0);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
            for (int index0 = 0; index0 < 2; index0++) {
                if (!projectileLevel.isClientSide()) {
                    AbstractArrow entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), projectileLevel);
                    entityToSpawn.setOwner((Entity) entity);
                    entityToSpawn.setBaseDamage((float) (damage * 0.75));
                    entityToSpawn.setKnockback(0);
                    entityToSpawn.setSilent(true);
                    entityToSpawn.setPierceLevel((byte) 1);
                    entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
                    entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, (float) 2.1, 5);
                    projectileLevel.addFreshEntity(entityToSpawn);
                }
            }
            for (int index1 = 0; index1 < 3; index1++) {
                if (!projectileLevel.isClientSide()) {
                    AbstractArrow entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), projectileLevel);
                    entityToSpawn.setOwner((Entity) entity);
                    entityToSpawn.setBaseDamage((float) (damage * 0.5));
                    entityToSpawn.setKnockback(0);
                    entityToSpawn.setSilent(true);
                    entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
                    entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, (float) 1.85, 25);
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
                    player.giveExperiencePoints(-(10));
            }
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, (float) 1.25, 1);
            }
            if ((Entity) entity instanceof Player player)
                player.getCooldowns().addCooldown(itemstack.getItem(), 40);
        }
    }
}