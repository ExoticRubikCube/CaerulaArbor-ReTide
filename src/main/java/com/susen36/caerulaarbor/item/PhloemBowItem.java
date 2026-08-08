
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PhloemBowItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public PhloemBowItem() {
		super(new Item.Properties().durability(768).rarity(Rarity.COMMON));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.bluebow.idle"));
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
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(ItemStack itemstack) {
		return false;
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
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

        boolean valid = true;
        if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.POWER), ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY)) != 0
                    && EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.POWER), ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY)) > EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.REFLECTION), itemstack)) {
                {
                    ItemEnchantments enchantments = itemstack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    if (enchantments.getLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.REFLECTION)) > 0) {
                        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                        mutable.removeIf(e -> e.is(CAEnchantments.REFLECTION));
                        itemstack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                    }
                }
                itemstack.enchant(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.REFLECTION), EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.POWER), ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY)));
                {
                    ItemStack offhandStack = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY);
                    ItemEnchantments enchantments = offhandStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    if (enchantments.getLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.POWER)) > 0) {
                        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                        mutable.removeIf(e -> e.is(Enchantments.POWER));
                        offhandStack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                    }
                }
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 72, 1.2, 2, 1.2, 0.2);
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 3, 1);
                }
                valid = false;
            } else if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.INFINITY), ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY)) != 0
                    && EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.METABOLISM), itemstack) == 0) {
                {
                    ItemEnchantments enchantments = itemstack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    if (enchantments.getLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.METABOLISM)) > 0) {
                        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                        mutable.removeIf(e -> e.is(CAEnchantments.METABOLISM));
                        itemstack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                    }
                }
                itemstack.enchant(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.METABOLISM), 1);
                {
                    ItemStack offhandStack = ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY);
                    ItemEnchantments enchantments = offhandStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    if (enchantments.getLevel(CAEnchantments.getHolder(entity.level().registryAccess(), Enchantments.INFINITY)) > 0) {
                        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchantments);
                        mutable.removeIf(e -> e.is(Enchantments.INFINITY));
                        offhandStack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                    }
                }
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 72, 1.2, 2, 1.2, 0.2);
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 3, 1);
                }
                valid = false;
            }
        }
        if (valid) {
            if (!((Entity) entity instanceof Player plrCldCheck32 && plrCldCheck32.getCooldowns().isOnCooldown(itemstack.getItem()))) {
                if (((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.OCEAN_ARROW.get()))) || isCreativeMode(entity)) {
                    if (entity != null) {
                        CaerulaArbor.queueServerWork(24, () -> {
                            if ((((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.OCEAN_ARROW.get()))) || isCreativeMode(entity) || EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.METABOLISM), itemstack) != 0)
                                    && (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()
                                    || ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem())) {
                                if (world instanceof Level level1) {
                                        level1.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, (float) 1.8, 1);
                                }
                                if (!isCreativeMode(entity)) {
                                    if (world instanceof ServerLevel _level) {
                                        itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                                    }
                                    if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.METABOLISM), itemstack) != 0) {
                                        spawnArrow(entity, itemstack);
                                    } else {
                                        if (!isCreativeMode(entity)) {
                                            if ((Entity) entity instanceof Player player1) {
                                                ItemStack stktoremove = new ItemStack(CAItems.OCEAN_ARROW.get());
                                                player1.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player1.inventoryMenu.getCraftSlots());
                                            }
                                            spawnArrow(entity, itemstack);
                                        } else {
                                            spawnArrow(entity, itemstack);
                                        }
                                    }
                                }
                                if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
                                    if ((Entity) entity instanceof LivingEntity livingEntity)
                                        livingEntity.swing(InteractionHand.MAIN_HAND, true);
                                } else {
                                    if ((Entity) entity instanceof LivingEntity livingEntity)
                                        livingEntity.swing(InteractionHand.OFF_HAND, true);
                                }
                            }
                        });
                    }
                    if (itemstack.getItem() instanceof PhloemBowItem)
                        CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.bluebow.pull"));
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CROSSBOW_QUICK_CHARGE_1.value(), SoundSource.NEUTRAL, (float) 1.8, 1);
                    }
                    if ((Entity) entity instanceof Player player)
                        player.getCooldowns().addCooldown(itemstack.getItem(), 30);
                }
            }
        }
        return ar;
	}


	private static boolean isCreativeMode(Entity ent) {
		if (ent instanceof ServerPlayer serverPlayer) {
			return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
		} else if (ent.level().isClientSide() && ent instanceof Player player) {
			return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
					&& Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
		}
		return false;
	}

	private static void spawnArrow(Entity shooter, ItemStack itemstack) {
		Level projectileLevel = shooter.level();
		if (!projectileLevel.isClientSide()) {
			AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, projectileLevel);
			entityToSpawn.setOwner(shooter);
			entityToSpawn.setBaseDamage((float) (7 + 1.5 * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(shooter.level().registryAccess(), CAEnchantments.REFLECTION), itemstack)));
			entityToSpawn.setCritArrow(true);
			entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			entityToSpawn.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
			entityToSpawn.shoot(shooter.getLookAngle().x, shooter.getLookAngle().y, shooter.getLookAngle().z, (float) (3 + 0.2 * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(shooter.level().registryAccess(), CAEnchantments.REFLECTION), itemstack)), 0);
			projectileLevel.addFreshEntity(entityToSpawn);
		}
	}

	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}