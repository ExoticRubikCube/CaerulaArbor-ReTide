
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class LegendarySpearItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public LegendarySpearItem() {
		super(new Item.Properties().durability(7445).fireResistant().rarity(Rarity.RARE).attributes(createAttributes()));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.lengendspear.idle"));
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
	public int getEnchantmentValue() {
		return 22;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 1.5F;
	}

	private static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "legendary_spear_attack_damage"), 17D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "legendary_spear_attack_speed"), -2.8D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		ItemUtils.transferSharpnessToSynesthesia(world, x, y, z, entity, itemstack);
		return ar;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!(new Object() {
            public boolean checkGamemode(Entity ent) {
                if (ent instanceof ServerPlayer serverPlayer) {
                    return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (ent.level().isClientSide() && ent instanceof Player player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) entity))) {
            if (world instanceof ServerLevel _level) {
                itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
            }
        }
        if (!((Entity) sourceentity instanceof Player plrCldCheck4 && plrCldCheck4.getCooldowns().isOnCooldown(itemstack.getItem()))
                && ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
            if (sourceentity.isShiftKeyDown()) {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.lengendspear.swing2"));
                if ((Entity) sourceentity instanceof Player player)
                    player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, (float) 3.5, 1);
                    }
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (entityiterator.isAlive() && !(entityiterator == sourceentity)) {
                                if ((sourceentity != null ? entityiterator.distanceTo(sourceentity) : -1) <= 3) {
                                    entityiterator.hurt(sourceentity.damageSources().trident(sourceentity, sourceentity),
                                            (float) (((Entity) sourceentity instanceof LivingEntity livingEntity17 && livingEntity17.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                    ? livingEntity17.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                    : 0) * (1 + 0.2 * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(sourceentity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack))));
                                }
                            }
                        }
                    }
                });
            } else if (sourceentity.getDeltaMovement().y() < -0.1) {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.lengendspear.srike"));
                if ((Entity) sourceentity instanceof Player player)
                    player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (entity.isAlive()) {
                        if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_HIT_GROUND, SoundSource.NEUTRAL, (float) 3.5, 1);
                        }
                        if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 4) {
                            entity.hurt(sourceentity.damageSources().trident(sourceentity, sourceentity),
                                    (float) (((Entity) sourceentity instanceof LivingEntity livingEntity32 && livingEntity32.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity32.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (1 + 0.2 * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(sourceentity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack))));
                        }
                    }
                });
            } else if (sourceentity.getDeltaMovement().y() > 0.1) {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.lengendspear.swing"));
                if ((Entity) sourceentity instanceof Player player)
                    player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (entity.isAlive()) {
                        if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, (float) 3.5, 1);
                        }
                        if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 4) {
                            entity.hurt(sourceentity.damageSources().trident(sourceentity, sourceentity),
                                    (float) (((Entity) sourceentity instanceof LivingEntity livingEntity46 && livingEntity46.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity46.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (1 + 0.2 * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(sourceentity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack))));
                            entity.push(0, 0.5, 0);
                        }
                    }
                });
            } else {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.lengendspear.stab"));
                if ((Entity) sourceentity instanceof Player player)
                    player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (entity.isAlive()) {
                        if (world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_HIT, SoundSource.NEUTRAL, (float) 3.5, 1);
                        }
                        if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 4) {
                            entity.hurt(sourceentity.damageSources().trident(sourceentity, sourceentity),
                                    (float) (((Entity) sourceentity instanceof LivingEntity livingEntity60 && livingEntity60.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity60.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (1 + 0.2 * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(sourceentity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack))));
                            entity.push((sourceentity.getLookAngle().x), 0, (sourceentity.getLookAngle().z));
                        }
                    }
                });
            }
        }
        return retval;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			EntityUtils.giveSpearFight(entity);
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}