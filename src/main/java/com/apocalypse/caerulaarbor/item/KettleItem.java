
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class KettleItem extends Item {
	public KettleItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.kettle.description_0"));
		list.add(Component.translatable("item.caerula_arbor.kettle.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        {
            boolean _setval = true;
            ((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.relic_util_KETTLE = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
        if ((LevelAccessor) world instanceof Level _level) {
                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.levelup")), SoundSource.NEUTRAL, 2, 1);
        }
        if ((LevelAccessor) world instanceof ServerLevel _level)
            _level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 72, 0.75, 1, 0.75, 1);
        if (((LevelAccessor) world).isClientSide())
            Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
        {
            double _setval = (((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_maxlive + 1;
            ((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_maxlive = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
        {
            double _setval = (((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_lives + 1;
            ((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.player_lives = _setval;
                capability.syncPlayerVariables(entity);
            });
        }
        if ((Entity) entity instanceof Player _player) {
            ItemStack _setstack = new ItemStack(CaerulaArborModBlocks.BLOCK_KETTLE.get()).copy();
            _setstack.setCount(1);
            ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
        }
        for (int index0 = 0; index0 < 2; index0++) {
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 4));
        }
        itemstack.shrink(1);
        return ar;
	}
}
