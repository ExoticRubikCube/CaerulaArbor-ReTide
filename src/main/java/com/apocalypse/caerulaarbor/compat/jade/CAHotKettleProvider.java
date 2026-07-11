package com.apocalypse.caerulaarbor.compat.jade;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum CAHotKettleProvider implements IBlockComponentProvider {
    INSTANCE;

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "hot_kettle");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockState bs = blockAccessor.getBlockState();
        Player player = blockAccessor.getPlayer();
        StateDefinition<Block, BlockState> state = bs.getBlock().getStateDefinition();
        boolean watered = false, boiling = false, noodled = false;
        if (state.getProperty("watered") instanceof BooleanProperty bP) watered = bs.getValue(bP);
        if (state.getProperty("boiling") instanceof BooleanProperty bP) boiling = bs.getValue(bP);
        if (state.getProperty("noodled") instanceof BooleanProperty bP) noodled = bs.getValue(bP);
        iTooltip.add(new CAHotKettleElement(watered, noodled, boiling, player.getMainHandItem()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
