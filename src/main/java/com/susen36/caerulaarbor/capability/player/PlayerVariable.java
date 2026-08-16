package com.susen36.caerulaarbor.capability.player;

import com.susen36.caerulaarbor.network.receive.PlayerVariablesSyncMessage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlayerVariable implements INBTSerializable<CompoundTag> {

    public double player_light = 100.0;
    public double disoclusion = 0;
    public boolean show_stats = true;
    public double player_oceanization = 0;
    public double plauyer_balance = 0;
    public boolean can_player_evo = false;
    public double reserve_quantity = 0;
    public double reserve_quality = 0;
    public String current_theme = "PARCHMENT";

    public void syncPlayerVariables(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
        }
    }

    public Tag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("player_light", player_light);
        nbt.putDouble("disoclusion", disoclusion);
        nbt.putBoolean("show_stats", show_stats);
        nbt.putDouble("player_oceanization", player_oceanization);
        nbt.putDouble("plauyer_balance", plauyer_balance);
        nbt.putBoolean("can_player_evo", can_player_evo);
        nbt.putDouble("reserve_quantity", reserve_quantity);
        nbt.putDouble("reserve_quality", reserve_quality);
        nbt.putString("current_theme", current_theme);
        return nbt;
    }

    public void readNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;

        if (nbt.contains("player_light", Tag.TAG_ANY_NUMERIC)) {
            player_light = nbt.getDouble("player_light");
        }
        disoclusion = nbt.getDouble("disoclusion");
        show_stats = nbt.getBoolean("show_stats");
        player_oceanization = nbt.getDouble("player_oceanization");
        plauyer_balance = nbt.getDouble("plauyer_balance");
        can_player_evo = nbt.getBoolean("can_player_evo");
        reserve_quantity = nbt.getDouble("reserve_quantity");
        reserve_quality = nbt.getDouble("reserve_quality");
        if (nbt.contains("current_theme")) {
            current_theme = nbt.getString("current_theme");
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return (CompoundTag) writeNBT();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        readNBT(nbt);
    }
}
