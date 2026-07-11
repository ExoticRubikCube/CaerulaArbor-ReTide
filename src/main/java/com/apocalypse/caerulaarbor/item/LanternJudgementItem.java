
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.*;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class LanternJudgementItem extends Item {
	public LanternJudgementItem() {
		super(new Item.Properties().durability(799).fireResistant().rarity(Rarity.UNCOMMON));
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
	public int getUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 3d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.4, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.lantern_judgement.description_0"));
		list.add(Component.translatable("item.caerula_arbor.lantern_judgement.description_1"));
		list.add(Component.translatable("item.caerula_arbor.lantern_judgement.description_2"));
		list.add(Component.translatable("item.caerula_arbor.lantern_judgement.description_3"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if ((LevelAccessor) world instanceof Level level) {
            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 1, 1);
        }
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                for (int index0 = 0; index0 < 120; index0++) {
                    if ((LevelAccessor) world instanceof ServerLevel level)
                        level.sendParticles(CAParticles.PURPLE_FLAME.get(), (x + 2 * (timedloopiterator + 1) * Math.sin(Math.toRadians(index0 * 3))), y,
                                (z + 2 * (timedloopiterator + 1) * Math.cos(Math.toRadians(index0 * 3))), 4, 0.15, 0.2, 0.15, 0.1);
                }
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 9, 1);
        {
            final Vec3 center = new Vec3(x, y, z);
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(36 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring"))) && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanpet"))) && entity.distanceTo(entityiterator) <= 18) {
                    if (entityiterator instanceof LivingEntity && !entity.level().isClientSide())
                        entity.addEffect(new MobEffectInstance(CAMobEffects.DIZZY.get(), 200, 0, false, false));
                    if (entityiterator instanceof LivingEntity && !entity.level().isClientSide())
                        entity.addEffect(new MobEffectInstance(CAMobEffects.MUTE.get(), 400, 0, false, false));
                    entityiterator.hurt(CADamageTypes.source((LevelAccessor) world, CADamageTypes.OCEANKILLER_DAMAGE, entity), (float) Math.max(((Entity) entity instanceof LivingEntity livingEntity10 && livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.15,
                                    15));
                    entityiterator.setSecondsOnFire(5);
                }
            }
        }
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
            {
                if (itemstack.hurt(10, RandomSource.create(), null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
            }
            if ((Entity) entity instanceof Player player)
                player.getCooldowns().addCooldown(itemstack.getItem(), 400);
        }
        entity.removeEffect(CAMobEffects.DIZZY.get());
        return retval;
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
        BlockState output;
        ItemStack toGive;
        if (blockstate.getBlock() == CABlocks.SEA_TRAIL_INIT.get() || blockstate.getBlock() == CABlocks.SEA_TRAIL_GROWING.get() || blockstate.getBlock() == CABlocks.SEA_TRAIL_GROWN.get()
                || blockstate.getBlock() == CABlocks.SEA_TRAIL_STOP.get() || blockstate.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || blockstate.getBlock() == CABlocks.TRAIL_PULSE.get()) {
            WorldUtils.burndownTrail(world, blockstate, x, y, z);
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 48, 0.75, 0.75, 0.75, 0.15);
            for (Direction directioniterator : Direction.values()) {
                output = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ())));
                if (output.getBlock() == CABlocks.SEA_TRAIL_INIT.get() || output.getBlock() == CABlocks.SEA_TRAIL_GROWING.get() || output.getBlock() == CABlocks.SEA_TRAIL_GROWN.get()
                        || output.getBlock() == CABlocks.SEA_TRAIL_SOLID.get()) {
                    WorldUtils.burndownTrail(world, output, x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ());
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
                {
                    if (itemstack.hurt(1, RandomSource.create(), null)) {
                        itemstack.shrink(1);
                        itemstack.setDamageValue(0);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (blockstate.getBlock() == CABlocks.TRAIL_LEAVE.get()) {
            {
                BlockPos pos = BlockPos.containing(x, y, z);
                Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x + 0.5, y + 0.5, z + 0.5), null);
                world.destroyBlock(pos, false);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 48, 0.75, 0.75, 0.75, 0.15);
            for (Direction directioniterator : Direction.values()) {
                output = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ())));
                if (output.getBlock() == CABlocks.TRAIL_LEAVE.get()) {
                    {
                        BlockPos pos = BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ());
                        Block.dropResources(world.getBlockState(pos), world, BlockPos.containing(x + directioniterator.getStepX() + 0.5, y + directioniterator.getStepY() + 0.5, z + directioniterator.getStepZ() + 0.5), null);
                        world.destroyBlock(pos, false);
                    }
                }
            }
            if (itemstack.hurt(1, RandomSource.create(), null)) {
                itemstack.shrink(1);
                itemstack.setDamageValue(0);
            }
            return InteractionResult.SUCCESS;
        }
        if (blockstate.is(BlockTags.create(ResourceLocation.parse("minecraft:logs")))) {
            toGive = new ItemStack(Items.CHARCOAL).copy();
            if (blockstate.getBlock() == CABlocks.TRAIL_LOG.get() || blockstate.getBlock() == CABlocks.STRIPPED_TRAIL_LOG.get()) {
                toGive = new ItemStack(CAItems.TRAIL_POWDER.get()).copy();
            }
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            if (world instanceof ServerLevel level)
                level.sendParticles(CAParticles.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 48, 0.75, 0.75, 0.75, 0.15);
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
            }
            if (world instanceof ServerLevel level) {
                ItemEntity entityToSpawn = new ItemEntity(level, (x + 0.5), (y + 1), (z + 0.5), toGive);
                entityToSpawn.setPickUpDelay(10);
                level.addFreshEntity(entityToSpawn);
            }
            if (Math.random() < 0.5) {
                if (world instanceof ServerLevel level) {
                    ItemEntity entityToSpawn = new ItemEntity(level, (x + 0.5), (y + 1), (z + 0.5), toGive);
                    entityToSpawn.setPickUpDelay(10);
                    level.addFreshEntity(entityToSpawn);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		itemstack.hurtAndBreak(1, entity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (selected && entity instanceof LivingEntity living) {
            living.removeEffect(CAMobEffects.FROZEN.get());
            living.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            living.removeEffect(MobEffects.DIG_SLOWDOWN);
            living.removeEffect(MobEffects.DARKNESS);
            living.removeEffect(MobEffects.BLINDNESS);
        }
    }
}
