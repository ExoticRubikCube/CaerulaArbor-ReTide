package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.registries.ForgeRegistries;

public class SetBoilingProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		boolean valid = false;
		BlockState lower = Blocks.AIR.defaultBlockState();
        lower = (world.getBlockState(BlockPos.containing(x, y - 1, z)));
		if (lower.is(BlockTags.create(new ResourceLocation(CaerulaArborMod.MODID, "heat")))) {
			if (lower.getBlock() == Blocks.CAMPFIRE || lower.getBlock() == Blocks.SOUL_CAMPFIRE) {
				valid = lower.getBlock().getStateDefinition().getProperty("lit") instanceof BooleanProperty _getbp4 && lower.getValue(_getbp4);
			} else if (lower.getBlock() == Blocks.SMOKER) {
				valid = lower.getBlock().getStateDefinition().getProperty("lit") instanceof BooleanProperty _getbp6 && lower.getValue(_getbp6);
			} else {
				valid = true;
			}
		} else {
			for (String stringiterator : CaerulaConfigsConfiguration.BOIL_WATER.get()) {
				if ((ForgeRegistries.BLOCKS.getKey(lower.getBlock()).toString()).equals(stringiterator)) {
					if ((ForgeRegistries.BLOCKS.getKey(lower.getBlock()).toString()).equals("create:blaze_burner")) {
						if (!((lower.getBlock().getStateDefinition().getProperty("blaze") instanceof EnumProperty _getep10 ? lower.getValue(_getep10).toString() : "").equals("smouldering"))) {
							valid = true;
						}
					} else {
						valid = true;
					}
					break;
				}
			}
		}
		if (valid) {
			{
				int _value = 1;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			{
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty _booleanProp)
					world.setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
			}
		} else {
			{
				int _value = 0;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("blockstate") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			{
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("boiling") instanceof BooleanProperty _booleanProp)
					world.setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
			}
		}
	}
}

// TODO: Called 4 times; block-state side effects are dense, so keep as-is.
