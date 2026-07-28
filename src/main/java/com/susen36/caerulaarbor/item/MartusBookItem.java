
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;


public class MartusBookItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public MartusBookItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.martus_book.idle"));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate);
		data.add(procedureController);
		AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
		data.add(idleController);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.martus_book.descr"));
		list.add(Component.translatable("item.caerula_arbor.martus_book.descr_0"));
		list.add(Component.translatable("item.caerula_arbor.martus_book.descr_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();

        boolean isCreative;
        if (!((Entity) entity instanceof Player plrCldCheck1) || !plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem())) {
            isCreative = new Object() {
                public boolean checkGamemode(Entity ent) {
                    if (ent instanceof ServerPlayer serverPlayer) {
                        return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (ent.level().isClientSide() && ent instanceof Player player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode((Entity) entity);
            if (((Entity) entity instanceof Player plr ? plr.experienceLevel : 0) >= 5 || isCreative) {
                if ((Entity) entity instanceof Player player)
                    player.getCooldowns().addCooldown(itemstack.getItem(), 1200);
                if (itemstack.getItem() instanceof MartusBookItem)
                    CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.martus_book.use"));
                if (!isCreative) {
                    if ((Entity) entity instanceof Player player)
                        player.giveExperienceLevels(-(5));
                }
                CaerulaArborMod.queueServerWork(10, () -> {
                    world.playSound(null, entity.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 2, 1);
                    entity.setHealth((float) Math.max(entity.getMaxHealth() * 0.5 + 1, entity.getHealth()));
                    entity.addEffect(new MobEffectInstance(CAMobEffects.MARTUS_PROTECTION, 400, 0, false, false));
                    entity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 40, 9, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 500, 6, false, false));
                });
            }
        }
        return ar;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        boolean isCreative;
        if (entity instanceof Player plrCldCheck1 && plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem())) {
            return;
        }
        if (!entity.isAlive()) {
            return;
        }
        isCreative = new Object() {
            public boolean checkGamemode(Entity ent) {
				if (ent instanceof ServerPlayer serverPlayer) {
					return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
				} else if (ent.level().isClientSide() && ent instanceof Player player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
				}
				return false;
            }
        }.checkGamemode(entity);
        if ((entity instanceof Player plr ? plr.experienceLevel : 0) < 5 && !isCreative) {
            return;
        }
        if (entity.tickCount % 5 == 0) {
            if ((entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) <= (entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) * 0.5) {
                if (entity instanceof Player player)
                    player.getCooldowns().addCooldown(itemstack.getItem(), 1200);
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 2, 1);
                }
                if (entity instanceof LivingEntity livingEntity)
                    livingEntity.setHealth((float) (livingEntity.getMaxHealth() * 0.5 + 1));
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.MARTUS_PROTECTION, 400, 0, false, false));
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 40, 9, false, false));
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 500, 5, false, false));
                if (!isCreative) {
                    if (entity instanceof Player player)
                        player.giveExperienceLevels(-(5));
                }
            }
        }
    }

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}