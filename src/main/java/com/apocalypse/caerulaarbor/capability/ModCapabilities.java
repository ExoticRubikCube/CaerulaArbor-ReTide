package com.apocalypse.caerulaarbor.capability;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.anchor.AnchorRecord;
import com.apocalypse.caerulaarbor.capability.apoptosis.ApoptosisInjuryCapability;
import com.apocalypse.caerulaarbor.capability.map.MapVariables;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SanityInjuryCapability;
import com.apocalypse.caerulaarbor.capability.world.WorldVariables;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

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
            CaerulaArborMod.LOGGER.warn("Failed to get capability {} for entity {}", PLAYER_VARIABLE, entity);
            return new PlayerVariable();
        });
    }

    public static SanityInjuryCapability getSanityInjury(LivingEntity entity) {
        return entity.getCapability(SANITY_INJURY, null).orElseGet(() -> {
            CaerulaArborMod.LOGGER.warn("Failed to get capability {} for entity {}", SANITY_INJURY, entity);
            return new SanityInjuryCapability(entity);
        });
    }

    public static ApoptosisInjuryCapability getApoptosisInjury(LivingEntity entity) {
        return entity.getCapability(APOPTOSIS_INJURY, null).orElseGet(() -> {
            CaerulaArborMod.LOGGER.warn("Failed to get capability {} for entity {}", APOPTOSIS_INJURY, entity);
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
