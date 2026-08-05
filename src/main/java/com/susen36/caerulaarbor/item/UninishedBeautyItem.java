
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;


public class UninishedBeautyItem extends Item implements GeoItem, SyncedAnimationItem {
	private static final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlay("animation.unfinished_beautuy.attack");
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public UninishedBeautyItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC).attributes(createAttributes()));
		GeoItem.registerSyncedAnimatable(this);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.unfinished_beautuy.idle"));
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
		AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate)
				.triggerableAnim("attack", ATTACK_ANIMATION);
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

	private static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "unfinished_beauty_attack_damage"), 15.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "unfinished_beauty_attack_speed"), -3.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) { return true; }

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
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
            if (world instanceof ServerLevel serverLevel) {
                triggerAnim(entity, GeoItem.getOrAssignId(itemstack, serverLevel), "procedureController", "attack");
            }
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
				if (world instanceof ServerLevel _level) {
					itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
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
            if (world instanceof ServerLevel serverLevel) {
                triggerAnim(sourceentity, GeoItem.getOrAssignId(itemstack, serverLevel), "procedureController", "attack");
            }
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_CUT_SPECT.get(), SoundSource.PLAYERS, (float) 2.4, 1);
            }
            CaerulaArborMod.queueServerWork(12, () -> {
                if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.SAW_SPECT_1.get(), SoundSource.PLAYERS, (float) 2.4, 1);
                }
                new Object() {
                    void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                        if (entity.distanceTo(sourceentity) <= 5 && entity.isAlive() && sourceentity.isAlive()) {
                            if (((Entity) entity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) / ((Entity) entity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1) >= ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1)
                                    / ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                                entity.hurt(CADamageTypes.source(world, CADamageTypes.SAW_CUT, sourceentity), (float) (((Entity) sourceentity instanceof LivingEntity livingEntity12 && livingEntity12.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity12.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
                                                * 1));
                                if (((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getHealth() : -1) < ((Entity) sourceentity instanceof LivingEntity livEnt ? livEnt.getMaxHealth() : -1)) {
                                    if ((Entity) sourceentity instanceof LivingEntity livingSourceEntity)
                                        livingSourceEntity.setHealth((float) Math.min(livingSourceEntity.getHealth() + livingSourceEntity.getMaxHealth() * 0.025,
                                                livingSourceEntity.getMaxHealth()));
                                }
                            } else {
                                entity.hurt(CADamageTypes.source(world, CADamageTypes.SAW_CUT, sourceentity), (float) (((Entity) sourceentity instanceof LivingEntity livingEntity21 && livingEntity21.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity21.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0)
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