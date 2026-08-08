
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;


public class VoyageOfGoldItem extends Item {
	public VoyageOfGoldItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
        String hoverText;
        String first_two;
        String locId;
        locId = itemstack.getDescriptionId();
        first_two = Component.translatable((locId + ".description_0")).getString() + "\n" + Component.translatable((locId + ".description_1")).getString() + "\n" + Component.translatable((locId + ".description_2")).getString() + "\n"
                + Component.translatable((locId + ".description_3")).getString();
        if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            hoverText = first_two + "\n" + Component.translatable("item.caerula_arbor.relics.used").getString();
        } else {
            hoverText = first_two;
        }
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
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
            for (int index0 = 0; index0 < 8; index0++) {
                if ((LevelAccessor) world instanceof ServerLevel level)
                    level.addFreshEntity(new ExperienceOrb(level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.6, 0.75)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 4));
            }
            if (!entity.level().isClientSide())
                entity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 400, 1, false, false));
            {
                boolean setval = true;
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                Relic.PURE_GOLD_EXPEDITION.set(capability, setval ? 1 : 0);
                capability.syncPlayerVariables(entity);
            }
            if ((LevelAccessor) world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 2, 1);
            }
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
        }
        return ar;
	}
}