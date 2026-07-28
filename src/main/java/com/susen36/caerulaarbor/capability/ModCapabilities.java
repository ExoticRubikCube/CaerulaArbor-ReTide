
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModCapabilities {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CaerulaArborMod.MODID);

    public static final Supplier<AttachmentType<PlayerVariable>> PLAYER_VARIABLE = ATTACHMENT_TYPES.register("player_variables",
            () -> AttachmentType.serializable((IAttachmentHolder holder) -> new PlayerVariable()).build());

    public static final Supplier<AttachmentType<SanityInjuryCapability>> SANITY_INJURY = ATTACHMENT_TYPES.register("sanity_injury",
            () -> AttachmentType.serializable((IAttachmentHolder holder) -> new SanityInjuryCapability(holder instanceof LivingEntity living ? living : null)).build());

    public static final Supplier<AttachmentType<ApoptosisInjuryCapability>> APOPTOSIS_INJURY = ATTACHMENT_TYPES.register("apoptosis_injury",
            () -> AttachmentType.serializable((IAttachmentHolder holder) -> new ApoptosisInjuryCapability(holder instanceof LivingEntity living ? living : null)).build());

    public static final Supplier<AttachmentType<AnchorRecord>> ANCHOR_RECORD = ATTACHMENT_TYPES.register("anchor_record",
            () -> AttachmentType.serializable((IAttachmentHolder holder) -> new AnchorRecord()).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    private ModCapabilities() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static PlayerVariable getPlayerVariables(Entity entity) {
        return entity.getData(PLAYER_VARIABLE.get());
    }

    public static SanityInjuryCapability getSanityInjury(LivingEntity entity) {
        return entity.getData(SANITY_INJURY.get());
    }

    public static ApoptosisInjuryCapability getApoptosisInjury(LivingEntity entity) {
        return entity.getData(APOPTOSIS_INJURY.get());
    }

    public static MapVariables getMapVariables(LevelAccessor world) {
        return MapVariables.get(world);
    }

    public static WorldVariables getWorldVariables(LevelAccessor world) {
        return WorldVariables.get(world);
    }

    public static AnchorRecord getAnchorRecord(ServerLevel level) {
        return level.getData(ANCHOR_RECORD.get());
    }
}
