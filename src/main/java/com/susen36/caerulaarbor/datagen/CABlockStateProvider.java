package com.susen36.caerulaarbor.datagen;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.GoldenChaliseBlock;
import com.susen36.caerulaarbor.init.CABlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 生成方块状态模型映射
 */
public class CABlockStateProvider extends BlockStateProvider {

    public CABlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CaerulaArbor.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        goldenChalise();
    }

    private void goldenChalise() {
        getVariantBuilder(CABlocks.GOLDEN_CHALISE.get()).forAllStates(state -> {
            int amount = state.getValue(GoldenChaliseBlock.AMOUNT);
            String model;
            if (amount == 0) {
                model = "golden_chalise";
            } else if (amount <= 21) {
                model = "golden_chalise_full_blockstate_0";
            } else {
                model = "golden_chalise_full_blockstate_1";
            }
            int y = switch (state.getValue(GoldenChaliseBlock.FACING)) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc("block/" + model)))
                    .rotationY(y)
                    .build();
        });
    }
}