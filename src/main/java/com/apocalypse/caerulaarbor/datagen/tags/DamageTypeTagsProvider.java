package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.datagen.DamageTypes;
import com.apocalypse.caerulaarbor.util.DatagenUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


public class DamageTypeTagsProvider extends TagsProvider<DamageType> {

    protected DamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.DAMAGE_TYPE, future, CaerulaArborMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        addDamagesToTag(DamageTypeTags.BYPASSES_DEFENSE,
                DamageTypes.OCEANIZE_DAMAGE,
                DamageTypes.SANITY_BREAK,
                DamageTypes.INV_KILLER,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.GUNMU_DAMAGE,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.SUPER_CAT_ATTACK,
                DamageTypes.OCEAN_WITHER,
                DamageTypes.IMMORTAL_PUNISHMENT,
                DamageTypes.ISHARMLA_ATTACK,
                DamageTypes.ISHARMLA_CURSED,
                DatagenUtils.MinecraftDamageTypes.GENERIC_KILL,
                DatagenUtils.MinecraftDamageTypes.FELL_OUT_OF_WORLD,
                DatagenUtils.MinecraftDamageTypes.OUTSIDE_BORDER,
                DatagenUtils.MinecraftDamageTypes.SONIC_BOOM,
                DatagenUtils.MinecraftDamageTypes.WITHER
        );
        addTagsToTag(DamageTypeTags.BYPASSES_DEFENSE,
                net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR,
                DatagenUtils.ForgeDamageTypes.IS_MAGIC
        );

        addDamagesToTag(DamageTypeTags.BYPASSES_MISS,
                DamageTypes.HAND_SPIKE,
                DamageTypes.OCEANIZE_DAMAGE,
                DamageTypes.SANITY_BREAK,
                DatagenUtils.MinecraftDamageTypes.BAD_RESPAWN_POINT,
                DatagenUtils.MinecraftDamageTypes.GENERIC_KILL,
                DatagenUtils.MinecraftDamageTypes.FELL_OUT_OF_WORLD,
                DatagenUtils.MinecraftDamageTypes.OUTSIDE_BORDER,
                DatagenUtils.MinecraftDamageTypes.SONIC_BOOM,
                DamageTypes.HAND_FIREWORK,
                DamageTypes.BRAND_BOMB,
                DamageTypes.ANCHOR_SMASH,
                DamageTypes.IZUMIK_SKILL,
                DamageTypes.INV_KILLER,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.GUNMU_DAMAGE,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.OCEAN_WITHER,
                DamageTypes.IMMORTAL_PUNISHMENT,
                DamageTypes.ISHARMLA_CURSED
        );
        addTagsToTag(DamageTypeTags.BYPASSES_MISS,
                net.minecraft.tags.DamageTypeTags.BYPASSES_EFFECTS
        );

        addDamagesToTag(DamageTypeTags.BYPASSES_PROTECTION,
                DamageTypes.INV_KILLER,
                DamageTypes.ISHARMLA_CURSED,
                DatagenUtils.MinecraftDamageTypes.GENERIC_KILL,
                DatagenUtils.MinecraftDamageTypes.FELL_OUT_OF_WORLD,
                DatagenUtils.MinecraftDamageTypes.OUTSIDE_BORDER
        );

        addDamagesToTag(DamageTypeTags.BYPASSES_ENDERMAN,
                DamageTypes.HAND_SPIKE,
                DamageTypes.GUNMU_DAMAGE,
                DamageTypes.OCEANKILLER_DAMAGE,
                DamageTypes.BRAND_BOMB,
                DamageTypes.WIPE_MAGIC,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.OCEAN_WITHER,
                DamageTypes.ISHARMLA_ATTACK,
                DamageTypes.ISHARMLA_CURSED,
                DatagenUtils.MinecraftDamageTypes.EXPLOSION,
                DatagenUtils.MinecraftDamageTypes.PLAYER_EXPLOSION,
                DatagenUtils.MinecraftDamageTypes.IN_FIRE,
                DatagenUtils.MinecraftDamageTypes.ON_FIRE,
                DatagenUtils.MinecraftDamageTypes.LAVA,
                DatagenUtils.MinecraftDamageTypes.FALL,
                DatagenUtils.MinecraftDamageTypes.FALLING_BLOCK,
                DatagenUtils.MinecraftDamageTypes.WITHER,
                DatagenUtils.MinecraftDamageTypes.INDIRECT_MAGIC
        );
        addTagsToTag(DamageTypeTags.BYPASSES_ENDERMAN,
                DamageTypeTags.BYPASSES_PROTECTION
        );

        addDamagesToTag(DamageTypeTags.BYPASSES_EVOLUTION,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.INV_KILLER,
                DatagenUtils.MinecraftDamageTypes.GENERIC_KILL,
                DatagenUtils.MinecraftDamageTypes.FELL_OUT_OF_WORLD,
                DatagenUtils.MinecraftDamageTypes.OUTSIDE_BORDER,
                DamageTypes.CHEST_ATTACK,
                DamageTypes.GENERIC_SEABORN_ATTACK,
                DamageTypes.HIGHMORE_ATTACK,
                DamageTypes.IZUMIK_NORMAL_ATTACK,
                DamageTypes.PUNCTURE_ATTACK,
                DamageTypes.REPELLER_ATTACK,
                DamageTypes.SUPER_CAT_ATTACK,
                DamageTypes.WARDEN_ATTACK,
                DatagenUtils.MinecraftDamageTypes.MOB_ATTACK_NO_AGGRO,
                DamageTypes.IMMORTAL_PUNISHMENT,
                DamageTypes.ISHARMLA_CURSED
        );

        addDamagesToTag(DamageTypeTags.BYPASSES_MIGRATION,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.HAND_SPIKE,
                DamageTypes.EXTRACTOR_DAMAGE,
                DamageTypes.INV_KILLER,
                DamageTypes.WIPE_MAGIC,
                DamageTypes.IMMORTAL_PUNISHMENT,
                DamageTypes.ISHARMLA_CURSED
        );

        addDamagesToTag(DamageTypeTags.CAN_TRIGGER_OCEANIZTION,
                DamageTypes.OCEAN_COUNTER,
                DamageTypes.OCEAN_MAGIC,
                DamageTypes.OCEAN_WITHER,
                DamageTypes.OCEANIZE_DAMAGE,
                DamageTypes.TRAIL_DAMAGE,
                DamageTypes.ISHARMLA_CURSED
        );

        addDamagesToTag(DamageTypeTags.HORROR,
                DatagenUtils.MinecraftDamageTypes.WITHER,
                DatagenUtils.MinecraftDamageTypes.WITHER_SKULL,
                DatagenUtils.MinecraftDamageTypes.EXPLOSION,
                DatagenUtils.MinecraftDamageTypes.PLAYER_EXPLOSION,
                DatagenUtils.MinecraftDamageTypes.FIREWORKS,
                DatagenUtils.MinecraftDamageTypes.FALLING_STALACTITE,
                DatagenUtils.MinecraftDamageTypes.STALAGMITE,
                DatagenUtils.MinecraftDamageTypes.FALLING_ANVIL,
                DatagenUtils.MinecraftDamageTypes.LAVA,
                DatagenUtils.MinecraftDamageTypes.SONIC_BOOM,
                DatagenUtils.MinecraftDamageTypes.DRAGON_BREATH,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.WARDEN_ATTACK,
                DamageTypes.IZUMIK_NORMAL_ATTACK,
                DamageTypes.IZUMIK_SKILL,
                DamageTypes.GOLEM_ATTACK
        );

        addDamagesToTag(DamageTypeTags.IS_MAGIC,
                DatagenUtils.MinecraftDamageTypes.INDIRECT_MAGIC,
                DatagenUtils.MinecraftDamageTypes.MAGIC,
                DamageTypes.IZUMIK_NORMAL_ATTACK,
                DamageTypes.IZUMIK_SKILL,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.SUPER_CAT_ATTACK
        );
        addTagsToTag(DamageTypeTags.IS_MAGIC,
                net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR,
                DatagenUtils.ForgeDamageTypes.IS_MAGIC
        );

        addDamagesToTag(DamageTypeTags.NEVER_TRIGGER_BOSS_PROTECTION,
                DamageTypes.ANCHOR_SMASH,
                DamageTypes.AXE_CLEAVE,
                DamageTypes.BRAND_BOMB,
                DamageTypes.GUNMU_DAMAGE,
                DamageTypes.HAND_FIREWORK,
                DamageTypes.HAND_OF_CHOKER,
                DamageTypes.HAND_SPIKE,
                DamageTypes.HIGHMORE_ATTACK,
                DamageTypes.INV_KILLER,
                DamageTypes.IZUMIK_NORMAL_ATTACK,
                DamageTypes.IZUMIK_SKILL,
                DamageTypes.OCEAN_COUNTER,
                DamageTypes.OCEAN_MAGIC,
                DamageTypes.OCEANKILLER_DAMAGE,
                DamageTypes.REPELLER_ATTACK,
                DamageTypes.SANITY_BREAK,
                DamageTypes.TRAIL_DAMAGE,
                DamageTypes.WARDEN_ATTACK,
                DamageTypes.WARDEN_SONIC,
                DamageTypes.WIPE_MAGIC,
                DamageTypes.SUPER_CAT_ATTACK,
                DamageTypes.GOLEM_ATTACK,
                DamageTypes.SAW_CUT
        );

        addDamagesToTag(DamageTypeTags.NO_BEAT_BACK,
                DatagenUtils.MinecraftDamageTypes.THORNS,
                DamageTypes.HAND_SPIKE,
                DamageTypes.WIPE_MAGIC
        );

        addDamagesToTag(DamageTypeTags.RARE,
                DatagenUtils.MinecraftDamageTypes.LIGHTNING_BOLT,
                DatagenUtils.MinecraftDamageTypes.OUTSIDE_BORDER,
                DatagenUtils.MinecraftDamageTypes.CRAMMING,
                DatagenUtils.MinecraftDamageTypes.FELL_OUT_OF_WORLD,
                DamageTypes.GUNMU_DAMAGE
        );
    }

    @SafeVarargs
    @SuppressWarnings("SameParameterValue")
    private void addTagsToTag(TagKey<DamageType> targetTag, TagKey<DamageType>... tags) {
        for (TagKey<DamageType> tag : tags) {
            tag(targetTag).addTag(tag);
        }
    }

    @SafeVarargs
    @SuppressWarnings("SameParameterValue")
    private void addDamagesToTag(TagKey<DamageType> tag, ResourceKey<DamageType>... types) {
        for (ResourceKey<DamageType> type : types) {
            tag(tag).add(type);
        }
    }
}
