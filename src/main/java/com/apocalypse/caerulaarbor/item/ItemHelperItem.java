package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ItemHelperItem extends Item {
	public ItemHelperItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.item_helper.description_0"));
		list.add(Component.translatable("item.caerula_arbor.item_helper.description_1"));
		list.add(Component.translatable("item.caerula_arbor.item_helper.description_2"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        ItemStack itemstack = context.getItemInHand();
        double tgtX;
        double tgtY;
        double tgtZ;
        tgtX = x + direction.getStepX() + 0.5;
        tgtY = y + direction.getStepY() + 0.5;
        tgtZ = z + direction.getStepZ() + 0.5;
        if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = CAEntities.LITTLE_HELPER.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
            }
        }
        if (world instanceof Level _level) {
                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "clean_bot_start")), SoundSource.BLOCKS, 3, 1);
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
