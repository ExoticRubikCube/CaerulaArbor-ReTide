
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class BreathOfTideItem extends Item {
	public BreathOfTideItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 20;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.breath_of_tide.description_0"));
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
        if (!world.getBiome(BlockPos.containing(x, y, z)).is(TagKey.create(Registries.BIOME, new ResourceLocation(CaerulaArborMod.MODID, "danger_spawn_biome")))) {
            if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                _player.displayClientMessage(Component.literal((Component.translatable("spawn.last_knight.fail_1").getString())), true);
            CaerulaArborMod.queueServerWork(20, () -> {
                if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal((Component.translatable("spawn.last_knight.fail_2").getString())), true);
            });
        } else if (EntityUtils.getSeabornNum(world, x, y, z) < 6) {
            if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                _player.displayClientMessage(Component.literal((Component.translatable("spawn.last_knight.fail_3").getString())), true);
        } else {
            if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                _player.displayClientMessage(Component.literal((Component.translatable("spawn.last_knight").getString())), false);
            if ((LevelAccessor) world instanceof ServerLevel _level) {
                CaerulaArborModEntities.THE_LAST_KNIGHT.get().spawn(_level, BlockPos.containing(x + Mth.nextInt(RandomSource.create(), -5, 5), y + 3, z + Mth.nextInt(RandomSource.create(), -5, 5)), MobSpawnType.MOB_SUMMONED);
            }
            itemstack.shrink(1);
        }
        return retval;
	}
}
