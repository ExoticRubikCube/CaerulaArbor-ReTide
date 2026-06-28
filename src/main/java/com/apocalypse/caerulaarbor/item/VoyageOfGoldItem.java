
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class VoyageOfGoldItem extends Item {
	public VoyageOfGoldItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
        String hoverText;
        String first_two = "";
        String locId = "";
        locId = itemstack.getDescriptionId();
        first_two = Component.translatable((locId + ".description_0")).getString() + "\n" + Component.translatable((locId + ".description_1")).getString() + "\n" + Component.translatable((locId + ".description_2")).getString() + "\n"
                + Component.translatable((locId + ".description_3")).getString();
        if (itemstack.getOrCreateTag().getBoolean("used")) {
            hoverText = first_two + "\n" + Component.translatable("item.caerula_arbor.relics.used").getString();
        } else {
            hoverText = first_two;
        }
        if (hoverText != null) {
			for (String line : hoverText.split("\n")) {
				list.add(Component.literal(line));
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (entity != null) {
            if (!itemstack.getOrCreateTag().getBoolean("used")) {
                for (int index0 = 0; index0 < 8; index0++) {
                    if ((LevelAccessor) world instanceof ServerLevel _level)
                        _level.addFreshEntity(new ExperienceOrb(_level, (x + Mth.nextDouble(RandomSource.create(), -1, 1)), (y + Mth.nextDouble(RandomSource.create(), 0.6, 0.75)), (z + Mth.nextDouble(RandomSource.create(), -1, 1)), 4));
                }
                if ((Entity) entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_REACH.get(), 400, 1, false, false));
                {
                    boolean _setval = true;
                    ((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                        capability.relic_util_VOYGOLD = _setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.levelup")), SoundSource.NEUTRAL, 2, 1);
                }
                itemstack.getOrCreateTag().putBoolean("used", true);
            }
        }
        return ar;
	}
}
