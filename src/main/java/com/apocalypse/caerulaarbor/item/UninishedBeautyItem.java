
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.client.renderer.item.UninishedBeautyItemRenderer;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class UninishedBeautyItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public UninishedBeautyItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BlockEntityWithoutLevelRenderer renderer = new UninishedBeautyItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	private PlayState idlePredicate(AnimationState<?> event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.unfinished_beautuy.idle"));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState<?> event) {
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
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 2F;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 15d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -3, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.unfinished_beauty.description_0"));
		list.add(Component.translatable("item.caerula_arbor.unfinished_beauty.description_1"));
		list.add(Component.translatable("item.caerula_arbor.unfinished_beauty.description_2"));
		list.add(Component.translatable("item.caerula_arbor.unfinished_beauty.description_3"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.PASS;
        BlockState tgt;
        if (blockstate.is(BlockTags.create(ResourceLocation.parse("minecraft:mineable/axe")))) {
            {
                BlockPos pos = BlockPos.containing(x, y, z);
                Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x + 0.5, y, z + 0.5), null);
                world.destroyBlock(pos, false);
            }
            if (entity.isShiftKeyDown()) {
                for (int index0 = 0; index0 < 32; index0++) {
                    tgt = (world.getBlockState(BlockPos.containing(x, y + index0 + 1, z)));
                    if (tgt.is(BlockTags.create(ResourceLocation.parse("minecraft:mineable/axe")))) {
                        {
                            BlockPos pos = BlockPos.containing(x, y + index0 + 1, z);
                            Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x + 0.5, y + index0 + 1, z + 0.5), null);
                            world.destroyBlock(pos, false);
                        }
                    } else {
                        break;
                    }
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
            }.checkGamemode(entity))) {
				if (itemstack.hurt(1, RandomSource.create(), null)) {
					itemstack.shrink(1);
					itemstack.setDamageValue(0);
				}
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (((Entity) sourceentity instanceof Player plr ? plr.getAttackStrengthScale(0) : 0) >= 0.95) {
            if (itemstack.getItem() instanceof UninishedBeautyItem)
                itemstack.getOrCreateTag().putString("geckoAnim", "animation.unfinished_beautuy.attack");
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_CUT_SPECT.get(), SoundSource.PLAYERS, (float) 2.4, 1);
            }
            CaerulaArborMod.queueServerWork(12, () -> {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_SPECT_1.get(), SoundSource.PLAYERS, (float) 2.4, 1);
                }
                new Object() {
                    void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                        if (entity.distanceTo(sourceentity) <= 5 && ((Entity) entity).isAlive() && ((Entity) sourceentity).isAlive()) {
                            if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) >= ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1)
                                    / ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                                ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.SAW_CUT, sourceentity), (float) (((Entity) sourceentity instanceof LivingEntity livingEntity12 && livingEntity12.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity12.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                * 1));
                                if (((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                                    if ((Entity) sourceentity instanceof LivingEntity livingSourceEntity)
                                        livingSourceEntity.setHealth((float) Math.min(livingSourceEntity.getHealth() + livingSourceEntity.getMaxHealth() * 0.025,
                                                livingSourceEntity.getMaxHealth()));
                                }
                            } else {
                                ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.SAW_CUT, sourceentity), (float) (((Entity) sourceentity instanceof LivingEntity livingEntity21 && livingEntity21.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity21.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                * 0.5));
                            }
                        }
                        final int tick2 = ticks;
                        CaerulaArborMod.queueServerWork(tick2, () -> {
                            if (timedlooptotal > timedloopiterator + 1) {
                                timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                            }
                        });
                    }
                }.timedLoop(0, 10, 1);
            });
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
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}
