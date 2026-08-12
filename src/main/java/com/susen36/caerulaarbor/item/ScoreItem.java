package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAJukeboxSongs;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;


public class ScoreItem extends RelicItemBase {
	public ScoreItem() {
		super(CARelics.UTIL_SCORE, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).jukeboxPlayable(CAJukeboxSongs.SCORE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
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
            boolean setval = true;
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            CARelics.UTIL_SCORE.get().set(capability, setval ? 1 : 0);
            capability.syncPlayerVariables(entity);
            if ((Entity) entity instanceof Player player)
                player.giveExperienceLevels(2);
            if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 2, 1);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.NOTE, x, y, z, 48, 1, 1, 1, 1);
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
        }
        return ar;
	}
}