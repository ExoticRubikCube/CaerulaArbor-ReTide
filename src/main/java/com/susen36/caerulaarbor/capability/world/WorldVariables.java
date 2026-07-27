package com.susen36.caerulaarbor.capability.world;

import com.susen36.caerulaarbor.init.CANetwork;
import com.susen36.caerulaarbor.network.receive.SavedDataSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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
    public CompoundTag save(CompoundTag nbt) {
        return nbt;
    }

    public void syncData(LevelAccessor world) {
        this.setDirty();
        if (world instanceof Level level && !level.isClientSide()) {
            CANetwork.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
        }
    }

    public static WorldVariables get(LevelAccessor world) {
        if (world instanceof ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(WorldVariables::load, WorldVariables::new, DATA_NAME);
        }
        return clientSide;
    }
}