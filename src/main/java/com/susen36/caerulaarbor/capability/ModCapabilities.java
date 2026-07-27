package com.susen36.caerulaarbor.capability;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.anchor.AnchorRecord;
import com.susen36.caerulaarbor.capability.apoptosis.ApoptosisInjuryCapability;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.susen36.caerulaarbor.capability.world.WorldVariables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class ModCapabilities {

    public static final Capability<PlayerVariable> PLAYER_VARIABLE = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<AnchorRecord> ANCHOR_RECORD = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<SanityInjuryCapability> SANITY_INJURY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<ApoptosisInjuryCapability> APOPTOSIS_INJURY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private ModCapabilities() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static PlayerVariable getPlayerVariables(Entity entity) {
        return entity.getCapability(PLAYER_VARIABLE, null).orElseGet(() -> {
            CaerulaArborMod.LOGGER.warn("Failed to get capability {} for blockentity {}", PLAYER_VARIABLE, entity);
            return new PlayerVariable();
        });
    }

    public static SanityInjuryCapability getSanityInjury(LivingEntity entity) {
        return entity.getCapability(SANITY_INJURY, null).orElseGet(() -> {
            CaerulaArborMod.LOGGER.warn("Failed to get capability {} for blockentity {}", SANITY_INJURY, entity);
            return new SanityInjuryCapability(entity);
        });
    }

    public static ApoptosisInjuryCapability getApoptosisInjury(LivingEntity entity) {
        return entity.getCapability(APOPTOSIS_INJURY, null).orElseGet(() -> {
            CaerulaArborMod.LOGGER.warn("Failed to get capability {} for blockentity {}", APOPTOSIS_INJURY, entity);
            return new ApoptosisInjuryCapability(entity);
        });
    }

    public static MapVariables getMapVariables(LevelAccessor world) {
        return MapVariables.get(world);
    }

    public static WorldVariables getWorldVariables(LevelAccessor world) {
        return WorldVariables.get(world);
    }

    public static AnchorRecord getAnchorRecord(ServerLevel level) {
        return level.getCapability(ANCHOR_RECORD, null).orElseGet(() -> {
            CaerulaArborMod.LOGGER.warn("Failed to get anchor record for level {}", level.dimension().location());
            return new AnchorRecord();
        });
    }
}