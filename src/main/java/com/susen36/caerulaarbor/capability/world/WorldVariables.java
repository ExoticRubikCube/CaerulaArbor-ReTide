package com.susen36.caerulaarbor.capability.world;

import com.susen36.caerulaarbor.network.receive.SavedDataSyncMessage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

public class WorldVariables extends SavedData {

    public static final String DATA_NAME = "caerula_arbor_worldvars";
    public static WorldVariables clientSide = new WorldVariables();

    public static WorldVariables load(CompoundTag tag) {
        WorldVariables data = new WorldVariables();
        data.read(tag);
        return data;
    }

    public void read(CompoundTag nbt) {
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        return nbt;
    }

    public void syncData(LevelAccessor world) {
        this.setDirty();
        if (world instanceof ServerLevel level) {
            PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, this, level.registryAccess()));
        }
    }

    public static WorldVariables get(LevelAccessor world) {
        if (world instanceof ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(WorldVariables::new, (tag, provider) -> WorldVariables.load(tag)), DATA_NAME);
        }
        return clientSide;
    }
}
