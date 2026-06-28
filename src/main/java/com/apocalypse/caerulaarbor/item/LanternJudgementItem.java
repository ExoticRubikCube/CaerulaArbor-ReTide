
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.utils.WorldUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

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
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if (entity != null) {
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.conduit.activate")), SoundSource.PLAYERS, 1, 1);
            }
            new Object() {
                void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                    for (int index0 = 0; index0 < 120; index0++) {
                        if ((LevelAccessor) world instanceof ServerLevel _level)
                            _level.sendParticles(CaerulaArborModParticleTypes.PURPLE_FLAME.get(), (x + 2 * (timedloopiterator + 1) * Math.sin(Math.toRadians(index0 * 3))), y,
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
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(36 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))
                            && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanpet"))) && (entityiterator != null ? entity.distanceTo(entityiterator) : -1) <= 18) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.DIZZY.get(), 200, 0, false, false));
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                            _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.MUTE.get(), 400, 0, false, false));
                        entityiterator.hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceankiller_damage"))), entity),
                                (float) Math.max(((Entity) entity instanceof LivingEntity _livingEntity10 && _livingEntity10.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity10.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 1.15,
                                        15));
                        entityiterator.setSecondsOnFire(5);
                    }
                }
            }
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
                {
                    ItemStack _ist = itemstack;
                    if (_ist.hurt(10, RandomSource.create(), null)) {
                        _ist.shrink(1);
                        _ist.setDamageValue(0);
                    }
                }
                if ((Entity) entity instanceof Player _player)
                    _player.getCooldowns().addCooldown(itemstack.getItem(), 400);
            }
            if ((Entity) entity instanceof LivingEntity _entity)
                _entity.removeEffect(CaerulaArborModMobEffects.DIZZY.get());
        }
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
        BlockState output = Blocks.AIR.defaultBlockState();
        ItemStack toGive = ItemStack.EMPTY;
        if (blockstate.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_INIT.get() || blockstate.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWING.get() || blockstate.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWN.get()
                || blockstate.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_STOP.get() || blockstate.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_SOLID.get() || blockstate.getBlock() == CaerulaArborModBlocks.TRAIL_PULSE.get()) {
            WorldUtils.burndownTrail(world, blockstate, x, y, z);
            if (world instanceof ServerLevel _level)
                _level.sendParticles(CaerulaArborModParticleTypes.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 48, 0.75, 0.75, 0.75, 0.15);
            for (Direction directioniterator : Direction.values()) {
                output = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ())));
                if (output.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_INIT.get() || output.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWING.get() || output.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_GROWN.get()
                        || output.getBlock() == CaerulaArborModBlocks.SEA_TRAIL_SOLID.get()) {
                    WorldUtils.burndownTrail(world, output, x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ());
                }
            }
            if (!(new Object() {
                public boolean checkGamemode(Entity _ent) {
                    if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                        return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                    }
                    return false;
                }
            }.checkGamemode(entity))) {
                {
                    ItemStack _ist = itemstack;
                    if (_ist.hurt(1, RandomSource.create(), null)) {
                        _ist.shrink(1);
                        _ist.setDamageValue(0);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (blockstate.getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get()) {
            {
                BlockPos _pos = BlockPos.containing(x, y, z);
                Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x + 0.5, y + 0.5, z + 0.5), null);
                world.destroyBlock(_pos, false);
            }
            if (world instanceof ServerLevel _level)
                _level.sendParticles(CaerulaArborModParticleTypes.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 48, 0.75, 0.75, 0.75, 0.15);
            for (Direction directioniterator : Direction.values()) {
                output = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ())));
                if (output.getBlock() == CaerulaArborModBlocks.TRAIL_LEAVE.get()) {
                    {
                        BlockPos _pos = BlockPos.containing(x + directioniterator.getStepX(), y + directioniterator.getStepY(), z + directioniterator.getStepZ());
                        Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x + directioniterator.getStepX() + 0.5, y + directioniterator.getStepY() + 0.5, z + directioniterator.getStepZ() + 0.5), null);
                        world.destroyBlock(_pos, false);
                    }
                }
            }
            {
                ItemStack _ist = itemstack;
                if (_ist.hurt(1, RandomSource.create(), null)) {
                    _ist.shrink(1);
                    _ist.setDamageValue(0);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (blockstate.is(BlockTags.create(new ResourceLocation("minecraft:logs")))) {
            toGive = new ItemStack(Items.CHARCOAL).copy();
            if (blockstate.getBlock() == CaerulaArborModBlocks.TRAIL_LOG.get() || blockstate.getBlock() == CaerulaArborModBlocks.STRIPPED_TRAIL_LOG.get()) {
                toGive = new ItemStack(CaerulaArborModItems.TRAIL_POWDER.get()).copy();
            }
            world.destroyBlock(BlockPos.containing(x, y, z), false);
            if (world instanceof ServerLevel _level)
                _level.sendParticles(CaerulaArborModParticleTypes.PURPLE_FLAME.get(), (x + 0.5), (y + 1), (z + 0.5), 48, 0.75, 0.75, 0.75, 0.15);
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.BLOCKS, 1, 1);
            }
            if (world instanceof ServerLevel _level) {
                ItemEntity entityToSpawn = new ItemEntity(_level, (x + 0.5), (y + 1), (z + 0.5), toGive);
                entityToSpawn.setPickUpDelay(10);
                _level.addFreshEntity(entityToSpawn);
            }
            if (Math.random() < 0.5) {
                if (world instanceof ServerLevel _level) {
                    ItemEntity entityToSpawn = new ItemEntity(_level, (x + 0.5), (y + 1), (z + 0.5), toGive);
                    entityToSpawn.setPickUpDelay(10);
                    _level.addFreshEntity(entityToSpawn);
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
		if (selected) {
            if (entity == null)
                return;
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(CaerulaArborModMobEffects.FROZEN.get());
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(MobEffects.DARKNESS);
            if (entity instanceof LivingEntity _entity)
                _entity.removeEffect(MobEffects.BLINDNESS);
        }
	}
}
