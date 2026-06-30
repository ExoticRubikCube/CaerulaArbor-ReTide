package com.apocalypse.caerulaarbor.capability.map;

import com.apocalypse.caerulaarbor.init.CANetwork;
import com.apocalypse.caerulaarbor.network.receive.SavedDataSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;

public class MapVariables extends SavedData {

    public static final String DATA_NAME = "caerula_arbor_mapvars";
    public static MapVariables clientSide = new MapVariables();

    public double evo_point_grow = 0;
    public double evo_point_subsisting = 0;
    public double evo_point_breed = 0;
    public double evo_point_migration = 0;
    public double strategy_grow = 0;
    public double strategy_subsisting = 0;
    public double strategy_breed = 0.0;
    public double strategy_migration = 0;
    public double strategy_silence = 0;
    public double evo_point_silence = 0;
    public boolean silence_enabled = false;
    public double endspeaker_abolities = 0;
    public boolean endspeakerSummon = true;
    public double incandescentAnimaUseTick = 0;

    public static MapVariables load(CompoundTag tag) {
        MapVariables data = new MapVariables();
        data.read(tag);
        return data;
    }

    public void read(CompoundTag nbt) {
        evo_point_grow = nbt.getDouble("evo_point_grow");
        evo_point_subsisting = nbt.getDouble("evo_point_subsisting");
        evo_point_breed = nbt.getDouble("evo_point_breed");
        evo_point_migration = nbt.getDouble("evo_point_migration");
        strategy_grow = nbt.getDouble("strategy_grow");
        strategy_subsisting = nbt.getDouble("strategy_subsisting");
        strategy_breed = nbt.getDouble("strategy_breed");
        strategy_migration = nbt.getDouble("strategy_migration");
        strategy_silence = nbt.getDouble("strategy_silence");
        evo_point_silence = nbt.getDouble("evo_point_silence");
        silence_enabled = nbt.getBoolean("silence_enabled");
        endspeaker_abolities = nbt.getDouble("endspeaker_abolities");
        endspeakerSummon = nbt.getBoolean("endspeakerSummon");
        incandescentAnimaUseTick = nbt.getDouble("incandescentAnimaUseTick");
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putDouble("evo_point_grow", evo_point_grow);
        nbt.putDouble("evo_point_subsisting", evo_point_subsisting);
        nbt.putDouble("evo_point_breed", evo_point_breed);
        nbt.putDouble("evo_point_migration", evo_point_migration);
        nbt.putDouble("strategy_grow", strategy_grow);
        nbt.putDouble("strategy_subsisting", strategy_subsisting);
        nbt.putDouble("strategy_breed", strategy_breed);
        nbt.putDouble("strategy_migration", strategy_migration);
        nbt.putDouble("strategy_silence", strategy_silence);
        nbt.putDouble("evo_point_silence", evo_point_silence);
        nbt.putBoolean("silence_enabled", silence_enabled);
        nbt.putDouble("endspeaker_abolities", endspeaker_abolities);
        nbt.putBoolean("endspeakerSummon", endspeakerSummon);
        nbt.putDouble("incandescentAnimaUseTick", incandescentAnimaUseTick);
        return nbt;
    }

    public void syncData(LevelAccessor world) {
        this.setDirty();
        if (world instanceof Level level && !level.isClientSide()) {
            CANetwork.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
        }
    }

    public static MapVariables get(LevelAccessor world) {
        if (world instanceof ServerLevelAccessor serverLevelAccessor) {
            ServerLevel overworld = serverLevelAccessor.getLevel().getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) {
                return overworld.getDataStorage().computeIfAbsent(MapVariables::load, MapVariables::new, DATA_NAME);
            }
        }
        return clientSide;
    }
}
