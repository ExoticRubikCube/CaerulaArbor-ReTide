package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.entity.bullets.AnchorFlyEntity;
import com.susen36.caerulaarbor.init.CAEntities;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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


public class UnambiguousDirectionItem extends Item {
	public UnambiguousDirectionItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC).attributes(createAttributes()));
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
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 72000;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 6f;
	}

	private static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "unambiguous_direction_attack_damage"), 26D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "unambiguous_direction_attack_speed"), -3.15D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
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
        if (sourceentity.hasEffect(CAMobEffects.PATH_TO_UNCOVER)) {
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
                AbstractArrow entityToSpawn = new AnchorFlyEntity(CAEntities.ANCHOR_FLY.get(), projectileLevel);
                entityToSpawn.setOwner(entity);
                entityToSpawn.setBaseDamage((float) ((Entity) entity instanceof LivingEntity livingEntity3 && livingEntity3.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity3.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0));
                entityToSpawn.setSilent(true);
                entityToSpawn.setCritArrow(true);
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