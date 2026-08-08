package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;


public class OmniKeyItem extends RelicItemBase {
	public OmniKeyItem() {
		super(Relic.UTIL_OMNIKEY, new Item.Properties().durability(64).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		String hoverText = null;
		if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
			list.add(Component.translatable("item.caerula_arbor.relics.used"));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            {
                boolean setval = true;
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                Relic.UTIL_OMNIKEY.set(capability, setval ? 1 : 0);
                capability.syncPlayerVariables(entity);
            }
            if ((Entity) entity instanceof Player player)
                player.giveExperienceLevels(3);
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 2, 1);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 72, 1, 1, 1, 1);
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
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
        ItemStack itemstack = context.getItemInHand();
        if (blockstate.getBlock() == Blocks.IRON_DOOR || blockstate.getBlock() == Blocks.IRON_TRAPDOOR) {
            if (!(blockstate.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty getbp5 && blockstate.getValue(getbp5))) {
                {
                    BlockPos pos = BlockPos.containing(x, y, z);
                    BlockState bs = world.getBlockState(pos);
                    if (bs.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty booleanProp)
                        world.setBlock(pos, bs.setValue(booleanProp, true), 3);
                }
                world.scheduleTick(BlockPos.containing(x, y, z), world.getBlockState(BlockPos.containing(x, y, z)).getBlock(), 22);
                if (world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.CHAIN_STEP, SoundSource.NEUTRAL, 1, 1);
                }
                CaerulaArbor.queueServerWork(20, () -> {
                    {
                        BlockPos pos = BlockPos.containing(x, y, z);
                        BlockState bs = world.getBlockState(pos);
                        if (bs.getBlock().getStateDefinition().getProperty("open") instanceof BooleanProperty booleanProp)
                            world.setBlock(pos, bs.setValue(booleanProp, false), 3);
                    }
                });
                if (world instanceof ServerLevel _level) {
                    itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}