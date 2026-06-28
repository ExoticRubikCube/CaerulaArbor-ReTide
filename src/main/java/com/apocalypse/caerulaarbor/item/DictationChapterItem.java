package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModBlocks;
import com.apocalypse.caerulaarbor.init.CaerulaArborModParticleTypes;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.procedures.UpgradeBreedProcedure;
import com.apocalypse.caerulaarbor.procedures.UpgradeGrowProcedure;
import com.apocalypse.caerulaarbor.procedures.UpgradeMigraProcedure;
import com.apocalypse.caerulaarbor.procedures.UpgradeSubsisProcedure;
import com.apocalypse.caerulaarbor.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class DictationChapterItem extends Item {
	public DictationChapterItem() {
		super(new Item.Properties().stacksTo(8).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.dictation_chapter.description_0"));
		list.add(Component.translatable("item.caerula_arbor.dictation_chapter.description_1"));
		list.add(Component.translatable("item.caerula_arbor.dictation_chapter.description_2"));
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
        String stra = "";
        String info = "";
        double target = 0;
        double p0 = 0;
        double p1 = 0;
        double p2 = 0;
        double p3 = 0;
        double l1 = 0;
        double l2 = 0;
        double l3 = 0;
        double l0 = 0;
        if (blockstate.getBlock() == CaerulaArborModBlocks.TIDE_OBSERVATION.get() && !world.isClientSide()) {
            p0 = CaerulaArborModVariables.MapVariables.get(world).evo_point_grow;
            p1 = CaerulaArborModVariables.MapVariables.get(world).evo_point_subsisting;
            p2 = CaerulaArborModVariables.MapVariables.get(world).evo_point_breed;
            p3 = CaerulaArborModVariables.MapVariables.get(world).evo_point_migration;
            l0 = CaerulaArborModVariables.MapVariables.get(world).strategy_grow;
            l1 = CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting;
            l2 = CaerulaArborModVariables.MapVariables.get(world).strategy_breed;
            l3 = CaerulaArborModVariables.MapVariables.get(world).strategy_migration;
            if (l0 + l1 + l2 + l3 >= 16) {
                if (entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal((Component.translatable("item.caerula_arbor.dictation_chapter.late").getString())), true);
                return InteractionResult.PASS;
            }
            target = MathUtils.minOfFour(l0, l1, l2, l3);
            if (target == l0) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "grow1")), SoundSource.PLAYERS, 1, 1);
                }
                CaerulaArborModVariables.MapVariables.get(world).evo_point_grow = p0 + 999999;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                UpgradeGrowProcedure.execute(world);
                stra = Component.translatable("gui.caerula_arbor.evo_tree.label_sreategy_grow").getString();
            } else if (target == l1) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "subsisting1")), SoundSource.PLAYERS, 1, 1);
                }
                CaerulaArborModVariables.MapVariables.get(world).evo_point_subsisting = p1 + 999999;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                UpgradeSubsisProcedure.execute(world);
                stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_subsisting").getString();
            } else if (target == l2) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "breed1")), SoundSource.PLAYERS, 1, 1);
                }
                CaerulaArborModVariables.MapVariables.get(world).evo_point_breed = p2 + 999999;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                UpgradeBreedProcedure.execute(world);
                stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_breed").getString();
            } else if (target == l3) {
                if (world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "migration1")), SoundSource.PLAYERS, 1, 1);
                }
                CaerulaArborModVariables.MapVariables.get(world).evo_point_migration = p3 + 999999;
                CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                UpgradeMigraProcedure.execute(world);
                stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_migration").getString();
            }
            if (world instanceof ServerLevel _level)
                _level.sendParticles((SimpleParticleType) (CaerulaArborModParticleTypes.ENDSPEAKER_INV.get()), (x + 0.5), (y + 1), (z + 0.5), 32, 0.75, 1, 0.75, 0.15);
            info = (Component.translatable("item.caerula_arbor.dictation_chapter.use").getString()).replace("{stra}", stra);
            if (entity instanceof Player _player && !_player.level().isClientSide())
                _player.displayClientMessage(Component.literal(info), false);
            itemstack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
