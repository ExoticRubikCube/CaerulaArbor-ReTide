
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEnchantments;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.item.renderer.PhloemBowItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.function.Consumer;

public class PhloemBowItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public PhloemBowItem() {
		super(new Item.Properties().durability(768).rarity(Rarity.COMMON));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new PhloemBowItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
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
	public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

        if (entity != null) {
            boolean valid = false;
            valid = true;
            if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY)) != 0
                        && ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getEnchantmentLevel(Enchantments.POWER_ARROWS) > itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())) {
                    {
                        Map<Enchantment, Integer> _enchantments = EnchantmentHelper.getEnchantments(itemstack);
                        if (_enchantments.containsKey(CaerulaArborModEnchantments.REFLECTION.get())) {
                            _enchantments.remove(CaerulaArborModEnchantments.REFLECTION.get());
                            EnchantmentHelper.setEnchantments(_enchantments, itemstack);
                        }
                    }
                    itemstack.enchant(CaerulaArborModEnchantments.REFLECTION.get(), ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getEnchantmentLevel(Enchantments.POWER_ARROWS));
                    {
                        Map<Enchantment, Integer> _enchantments = EnchantmentHelper.getEnchantments(((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY));
                        if (_enchantments.containsKey(Enchantments.POWER_ARROWS)) {
                            _enchantments.remove(Enchantments.POWER_ARROWS);
                            EnchantmentHelper.setEnchantments(_enchantments, ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY));
                        }
                    }
                    if ((LevelAccessor) world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 72, 1.2, 2, 1.2, 0.2);
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.PLAYERS, 3, 1);
                    }
                    valid = false;
                } else if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY)) != 0
                        && !(EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.METABOLISM.get(), itemstack) != 0)) {
                    {
                        Map<Enchantment, Integer> _enchantments = EnchantmentHelper.getEnchantments(itemstack);
                        if (_enchantments.containsKey(CaerulaArborModEnchantments.METABOLISM.get())) {
                            _enchantments.remove(CaerulaArborModEnchantments.METABOLISM.get());
                            EnchantmentHelper.setEnchantments(_enchantments, itemstack);
                        }
                    }
                    itemstack.enchant(CaerulaArborModEnchantments.METABOLISM.get(), 1);
                    {
                        Map<Enchantment, Integer> _enchantments = EnchantmentHelper.getEnchantments(((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY));
                        if (_enchantments.containsKey(Enchantments.INFINITY_ARROWS)) {
                            _enchantments.remove(Enchantments.INFINITY_ARROWS);
                            EnchantmentHelper.setEnchantments(_enchantments, ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY));
                        }
                    }
                    if ((LevelAccessor) world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 72, 1.2, 2, 1.2, 0.2);
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.PLAYERS, 3, 1);
                    }
                    valid = false;
                }
            }
            if (valid) {
                if (!((Entity) entity instanceof Player _plrCldCheck32 && _plrCldCheck32.getCooldowns().isOnCooldown(itemstack.getItem()))) {
                    if (((Entity) entity instanceof Player _playerHasItem ? _playerHasItem.getInventory().contains(new ItemStack(CaerulaArborModItems.OCEAN_ARROW.get())) : false) || new Object() {
                        public boolean checkGamemode(Entity _ent) {
                            if (_ent instanceof ServerPlayer _serverPlayer) {
                                return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                            } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                                return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                        && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                            }
                            return false;
                        }
                    }.checkGamemode((Entity) entity)) {
						if (entity != null) {
							CaerulaArborMod.queueServerWork(24, () -> {
								if ((((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CaerulaArborModItems.OCEAN_ARROW.get()))) || new Object() {
									public boolean checkGamemode(Entity _ent) {
										if (_ent instanceof ServerPlayer _serverPlayer) {
											return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
										} else if (_ent.level().isClientSide() && _ent instanceof Player _player1) {
											return Minecraft.getInstance().getConnection().getPlayerInfo(_player1.getGameProfile().getId()) != null
													&& Minecraft.getInstance().getConnection().getPlayerInfo(_player1.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
										}
										return false;
									}
								}.checkGamemode((Entity) entity) || EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.METABOLISM.get(), itemstack) != 0)
										&& (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()
										|| ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem())) {
									if ((LevelAccessor) world instanceof Level _level1) {
											_level1.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.arrow.shoot")), SoundSource.PLAYERS, (float) 1.8, 1);
									}
									if (!(new Object() {
										public boolean checkGamemode(Entity _ent) {
											if (_ent instanceof ServerPlayer _serverPlayer) {
												return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
											} else if (_ent.level().isClientSide() && _ent instanceof Player _player1) {
												return Minecraft.getInstance().getConnection().getPlayerInfo(_player1.getGameProfile().getId()) != null
														&& Minecraft.getInstance().getConnection().getPlayerInfo(_player1.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
											}
											return false;
										}
									}.checkGamemode((Entity) entity))) {
										{
											if (itemstack.hurt(1, RandomSource.create(), null)) {
												itemstack.shrink(1);
												itemstack.setDamageValue(0);
											}
										}
									}
									if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.METABOLISM.get(), itemstack) != 0) {
										{
											Entity _shootFrom = entity;
											Level projectileLevel = _shootFrom.level();
											if (!projectileLevel.isClientSide()) {
												Projectile _entityToSpawn = new Object() {
													public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
														AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, level);
														entityToSpawn.setOwner(shooter);
														entityToSpawn.setBaseDamage(damage);
														entityToSpawn.setKnockback(knockback);
														entityToSpawn.setPierceLevel(piercing);
														entityToSpawn.setCritArrow(true);
														entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
														return entityToSpawn;
													}
												}.getArrow(projectileLevel, (Entity) entity, (float) (7 + 1.5 * itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())), (int) 0.5, (byte) 1);
												_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
												_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, (float) (3 + 0.2 * itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())), 0);
												projectileLevel.addFreshEntity(_entityToSpawn);
											}
										}
									} else {
										if (!(new Object() {
											public boolean checkGamemode(Entity _ent) {
												if (_ent instanceof ServerPlayer _serverPlayer) {
													return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
												} else if (_ent.level().isClientSide() && _ent instanceof Player _player1) {
													return Minecraft.getInstance().getConnection().getPlayerInfo(_player1.getGameProfile().getId()) != null
															&& Minecraft.getInstance().getConnection().getPlayerInfo(_player1.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
												}
												return false;
											}
										}.checkGamemode((Entity) entity))) {
											if ((Entity) entity instanceof Player _player1) {
												ItemStack _stktoremove = new ItemStack(CaerulaArborModItems.OCEAN_ARROW.get());
												_player1.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player1.inventoryMenu.getCraftSlots());
											}
											{
												Entity _shootFrom = entity;
												Level projectileLevel = _shootFrom.level();
												if (!projectileLevel.isClientSide()) {
													Projectile _entityToSpawn = new Object() {
														public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
															AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, level);
															entityToSpawn.setOwner(shooter);
															entityToSpawn.setBaseDamage(damage);
															entityToSpawn.setKnockback(knockback);
															entityToSpawn.setPierceLevel(piercing);
															entityToSpawn.setCritArrow(true);
															entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
															return entityToSpawn;
														}
													}.getArrow(projectileLevel, (Entity) entity, (float) (7 + 1.5 * itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())), (int) 0.5, (byte) 1);
													_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
													_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, (float) (3 + 0.2 * itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())), 0);
													projectileLevel.addFreshEntity(_entityToSpawn);
												}
											}
										} else {
											{
												Entity _shootFrom = entity;
												Level projectileLevel = _shootFrom.level();
												if (!projectileLevel.isClientSide()) {
													Projectile _entityToSpawn = new Object() {
														public Projectile getArrow(Level level, Entity shooter, float damage, int knockback, byte piercing) {
															AbstractArrow entityToSpawn = new Arrow(EntityType.ARROW, level);
															entityToSpawn.setOwner(shooter);
															entityToSpawn.setBaseDamage(damage);
															entityToSpawn.setKnockback(knockback);
															entityToSpawn.setPierceLevel(piercing);
															entityToSpawn.setCritArrow(true);
															entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
															return entityToSpawn;
														}
													}.getArrow(projectileLevel, (Entity) entity, (float) (7 + 1.5 * itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())), (int) 0.5, (byte) 1);
													_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
													_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, (float) (3 + 0.2 * itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.REFLECTION.get())), 0);
													projectileLevel.addFreshEntity(_entityToSpawn);
												}
											}
										}
									}
									if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
										if ((Entity) entity instanceof LivingEntity _entity)
											_entity.swing(InteractionHand.MAIN_HAND, true);
									} else {
										if ((Entity) entity instanceof LivingEntity _entity)
											_entity.swing(InteractionHand.OFF_HAND, true);
									}
								}
							});
						}
						if (itemstack.getItem() instanceof PhloemBowItem)
                            itemstack.getOrCreateTag().putString("geckoAnim", "animation.bluebow.pull");
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.crossbow.quick_charge_1")), SoundSource.NEUTRAL, (float) 1.8, 1);
                        }
                        if ((Entity) entity instanceof Player _player)
                            _player.getCooldowns().addCooldown(itemstack.getItem(), 30);
                    }
                }
            }
        }
        return ar;
	}
}
