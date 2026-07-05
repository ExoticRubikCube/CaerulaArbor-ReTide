
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CABlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public class RelicCROWNItem extends Item {
	public RelicCROWNItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.relic_crown.description_0"));
		list.add(Component.translatable("item.caerula_arbor.relic_crown.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!(((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CROWN) {
            if ((LevelAccessor) world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);
            {
                boolean setval = true;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.relic_king_CROWN = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if (((LevelAccessor) world).isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        }
        return ar;
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
        if (blockstate.getBlock() == Blocks.DEEPSLATE_BRICK_SLAB) {
            world.setBlock(BlockPos.containing(x, y, z), CABlocks.BLOCK_CROWN.get().defaultBlockState(), 3);
            {
                Direction dir = ((entity.getDirection()).getOpposite());
                BlockPos pos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(pos);
                Property<?> property = bs.getBlock().getStateDefinition().getProperty("facing");
                if (property instanceof DirectionProperty dp && dp.getPossibleValues().contains(dir)) {
                    world.setBlock(pos, bs.setValue(dp, dir), 3);
                } else {
                    property = bs.getBlock().getStateDefinition().getProperty("axis");
                    if (property instanceof EnumProperty ap && ap.getPossibleValues().contains(dir.getAxis()))
                        world.setBlock(pos, bs.setValue(ap, dir.getAxis()), 3);
                }
            }
            itemstack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
