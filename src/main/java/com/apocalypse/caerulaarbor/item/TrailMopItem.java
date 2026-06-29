
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.init.CABlocks;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TrailMopItem extends Item {
	public TrailMopItem() {
		super(new Item.Properties().durability(275));
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
		return List.of(CABlocks.SEA_TRAIL_INIT.get(), CABlocks.SEA_TRAIL_GROWING.get(), CABlocks.SEA_TRAIL_GROWN.get()).contains(blockstate.getBlock()) ? 1.5f : 1;
	}

	@Override
	public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
		itemstack.hurtAndBreak(1, entity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		itemstack.hurtAndBreak(2, entity, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        LevelAccessor world = entity.level();
        SIHelper.causeSanityInjury(entity, sourceentity, 40, SanityEvent.Hurt.Type.ENTITY);
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                if (world instanceof ServerLevel _level)
                    _level.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), (entity.getY() + 0.5 * entity.getBbHeight()), entity.getZ(), 12, 0.86, 1.2, 0.86, 0.1);
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 3, 5);
        return true;
	}

	@Override
	public int getEnchantmentValue() {
		return 8;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 2f, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
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
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trail_mop.description_0"));
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
        double rate = 0;
        if (blockstate.getBlock() == CABlocks.SEA_TRAIL_INIT.get()) {
            rate = 0.9;
        } else if (blockstate.getBlock() == CABlocks.SEA_TRAIL_GROWING.get()) {
            rate = 0.65;
        } else if (blockstate.getBlock() == CABlocks.SEA_TRAIL_GROWN.get() || blockstate.getBlock() == CABlocks.SEA_TRAIL_STOP.get()) {
            rate = 0.5;
        } else if (blockstate.getBlock() == CABlocks.SEA_TRAIL_SOLID.get() || blockstate.getBlock() == CABlocks.TRAIL_PULSE.get()) {
            rate = 0.33;
        }
        if (rate > 0) {
            if (world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.brush.brushing.generic")), SoundSource.PLAYERS, 1, 1);
            }
            if (Math.random() < rate) {
                {
                    BlockPos _pos = BlockPos.containing(x, y, z);
                    Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x + 0.5, y, z + 0.5), null);
                    world.destroyBlock(_pos, false);
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
                if (itemstack.hurt(1, RandomSource.create(), null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
