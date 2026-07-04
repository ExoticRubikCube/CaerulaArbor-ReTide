
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.client.renderer.item.LegendarySpearItemRenderer;
import com.apocalypse.caerulaarbor.init.CAEnchantments;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.ItemUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.sounds.SoundEvents;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class LegendarySpearItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public LegendarySpearItem() {
		super(new Item.Properties().durability(7445).fireResistant().rarity(Rarity.RARE));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new LegendarySpearItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
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

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 17d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.8, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
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
            public boolean checkGamemode(Entity _ent) {
                if (_ent instanceof ServerPlayer _serverPlayer) {
                    return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                    return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                }
                return false;
            }
        }.checkGamemode((Entity) entity))) {
            if (itemstack.hurt(1, RandomSource.create(), null)) {
                itemstack.shrink(1);
                itemstack.setDamageValue(0);
            }
        }
        if (!((Entity) sourceentity instanceof Player _plrCldCheck4 && _plrCldCheck4.getCooldowns().isOnCooldown(itemstack.getItem()))
                && ((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
            if (sourceentity.isShiftKeyDown()) {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    itemstack.getOrCreateTag().putString("geckoAnim", "animation.lengendspear.swing2");
                if ((Entity) sourceentity instanceof Player _player)
                    _player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.NEUTRAL, (float) 3.5, 1);
                    }
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (entityiterator.isAlive() && !(entityiterator == sourceentity)) {
                                if ((sourceentity != null ? entityiterator.distanceTo(sourceentity) : -1) <= 3) {
                                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.TRIDENT), sourceentity),
                                            (float) (((Entity) sourceentity instanceof LivingEntity _livingEntity17 && _livingEntity17.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                                                    ? _livingEntity17.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                                                    : 0) * (1 + 0.2 * itemstack.getEnchantmentLevel(CAEnchantments.SYNESTHESIA.get()))));
                                }
                            }
                        }
                    }
                });
            } else if (sourceentity.getDeltaMovement().y() < -0.1) {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    itemstack.getOrCreateTag().putString("geckoAnim", "animation.lengendspear.srike");
                if ((Entity) sourceentity instanceof Player _player)
                    _player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (((Entity) entity).isAlive()) {
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_HIT_GROUND, SoundSource.NEUTRAL, (float) 3.5, 1);
                        }
                        if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 4) {
                            ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.TRIDENT), sourceentity),
                                    (float) (((Entity) sourceentity instanceof LivingEntity _livingEntity32 && _livingEntity32.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity32.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (1 + 0.2 * itemstack.getEnchantmentLevel(CAEnchantments.SYNESTHESIA.get()))));
                        }
                    }
                });
            } else if (sourceentity.getDeltaMovement().y() > 0.1) {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    itemstack.getOrCreateTag().putString("geckoAnim", "animation.lengendspear.swing");
                if ((Entity) sourceentity instanceof Player _player)
                    _player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (((Entity) entity).isAlive()) {
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_THROW, SoundSource.NEUTRAL, (float) 3.5, 1);
                        }
                        if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 4) {
                            ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.TRIDENT), sourceentity),
                                    (float) (((Entity) sourceentity instanceof LivingEntity _livingEntity46 && _livingEntity46.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity46.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (1 + 0.2 * itemstack.getEnchantmentLevel(CAEnchantments.SYNESTHESIA.get()))));
                            entity.push(0, 0.5, 0);
                        }
                    }
                });
            } else {
                if (itemstack.getItem() instanceof LegendarySpearItem)
                    itemstack.getOrCreateTag().putString("geckoAnim", "animation.lengendspear.stab");
                if ((Entity) sourceentity instanceof Player _player)
                    _player.getCooldowns().addCooldown(itemstack.getItem(), 25);
                CaerulaArborMod.queueServerWork(10, () -> {
                    if (((Entity) entity).isAlive()) {
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TRIDENT_HIT, SoundSource.NEUTRAL, (float) 3.5, 1);
                        }
                        if ((sourceentity != null ? entity.distanceTo(sourceentity) : -1) <= 4) {
                            ((Entity) entity).hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.TRIDENT), sourceentity),
                                    (float) (((Entity) sourceentity instanceof LivingEntity _livingEntity60 && _livingEntity60.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity60.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                            * (1 + 0.2 * itemstack.getEnchantmentLevel(CAEnchantments.SYNESTHESIA.get()))));
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
