package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.datagen.DamageTypes;
import com.apocalypse.caerulaarbor.util.DatagenUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


/**
 * 生成伤害类型标签数据，覆盖本模组、minecraft、forge 和兼容模组命名空间
 */
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
                DatagenUtils.MinecraftDamageTypeTags.BYPASSES_EFFECTS
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
        addDamagesToTag(DatagenUtils.ForgeDamageTypes.IS_MAGIC,
                DamageTypes.GLADIIA_MAGIC,
                DamageTypes.OCEAN_MAGIC,
                DamageTypes.WIPE_MAGIC
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

        addValuesToTag("minecraft", "always_hurts_ender_dragons", "caerula_arbor:hand_firework", "caerula_arbor:highmore_attack", "caerula_arbor:hunter_attack", "caerula_arbor:izumik_normal_attack", "caerula_arbor:izumik_skill", "caerula_arbor:ocean_magic", "caerula_arbor:repeller_attack", "caerula_arbor:sanity_break", "caerula_arbor:wipe_magic");
        addValuesToTag("minecraft", "bypasses_armor", "caerula_arbor:sanity_break", "caerula_arbor:oceanize_damage", "caerula_arbor:oceankiller_damage", "caerula_arbor:ocean_magic", "caerula_arbor:wipe_magic", "caerula_arbor:trail_damage", "caerula_arbor:izumik_skill", "caerula_arbor:izumik_normal_attack", "caerula_arbor:inv_killer", "caerula_arbor:hand_of_choker", "caerula_arbor:gunmu_damage", "caerula_arbor:warden_sonic", "caerula_arbor:super_cat_attack", "caerula_arbor:ocean_wither", "caerula_arbor:immortal_punishment", "caerula_arbor:isharmla_cursed", "caerula_arbor:isharmla_attack");
        addValuesToTag("minecraft", "bypasses_cooldown", "caerula_arbor:oceankiller_damage", "caerula_arbor:repeller_attack", "caerula_arbor:sanity_break", "caerula_arbor:hand_spike", "caerula_arbor:axe_cleave", "caerula_arbor:ocean_magic", "caerula_arbor:cleaver_mix", "caerula_arbor:highmore_attack", "caerula_arbor:hand_firework", "caerula_arbor:hunter_attack", "caerula_arbor:generic_seaborn_attack", "caerula_arbor:brand_bomb", "caerula_arbor:saw_cut", "caerula_arbor:izumik_normal_attack", "caerula_arbor:izumik_skill", "caerula_arbor:ocean_counter", "caerula_arbor:generic_warrior_attack", "caerula_arbor:inv_killer", "caerula_arbor:hand_of_choker", "caerula_arbor:gunmu_damage", "caerula_arbor:warden_attack", "caerula_arbor:warden_sonic", "caerula_arbor:super_cat_attack", "caerula_arbor:golem_attack", "caerula_arbor:ocean_wither", "caerula_arbor:last_knight_attack", "caerula_arbor:puncture_attack", "caerula_arbor:endspeaker_attack", "caerula_arbor:immortal_punishment", "caerula_arbor:isharmla_cursed", "caerula_arbor:isharmla_attack", "caerula_arbor:wipe_magic");
        addValuesToTag("minecraft", "bypasses_effects", "caerula_arbor:sanity_break", "caerula_arbor:inv_killer", "caerula_arbor:gunmu_damage", "caerula_arbor:hand_of_choker", "caerula_arbor:immortal_punishment", "caerula_arbor:isharmla_cursed", "caerula_arbor:isharmla_attack", "caerula_arbor:oceankiller_damage");
        addValuesToTag("minecraft", "bypasses_enchantments", "caerula_arbor:sanity_break", "caerula_arbor:inv_killer", "caerula_arbor:gunmu_damage", "caerula_arbor:hand_of_choker", "caerula_arbor:immortal_punishment", "caerula_arbor:isharmla_cursed", "caerula_arbor:isharmla_attack");
        addValuesToTag("minecraft", "bypasses_invulnerability", "caerula_arbor:inv_killer", "caerula_arbor:sanity_break", "caerula_arbor:hand_of_choker", "caerula_arbor:immortal_punishment", "caerula_arbor:isharmla_cursed");
        addValuesToTag("minecraft", "bypasses_shield", "caerula_arbor:sanity_break", "caerula_arbor:izumik_skill", "caerula_arbor:inv_killer", "caerula_arbor:hand_of_choker", "caerula_arbor:gunmu_damage", "caerula_arbor:warden_attack", "caerula_arbor:warden_sonic", "caerula_arbor:ocean_wither", "caerula_arbor:immortal_punishment", "caerula_arbor:isharmla_cursed", "caerula_arbor:isharmla_attack");
        addValuesToTag("minecraft", "is_explosion", "caerula_arbor:brand_bomb");
        addValuesToTag("minecraft", "is_projectile", "caerula_arbor:highmore_attack", "caerula_arbor:hand_firework", "caerula_arbor:puncture_attack");
        addValuesToTag("cataclysm", "bypasses_hurt_time", "caerula_arbor:cleaver_mix", "caerula_arbor:hand_firework", "caerula_arbor:hand_spike", "caerula_arbor:highmore_attack", "caerula_arbor:hunter_attack", "caerula_arbor:izumik_normal_attack", "caerula_arbor:izumik_skill", "caerula_arbor:ocean_magic", "caerula_arbor:repeller_attack", "caerula_arbor:sanity_break", "caerula_arbor:wipe_magic", "caerula_arbor:inv_killer", "caerula_arbor:gunmu_damage", "caerula_arbor:warden_sonic", "caerula_arbor:super_cat_attack", "caerula_arbor:hand_of_choker", "caerula_arbor:endspeaker_attack", "caerula_arbor:isharmla_cursed", "caerula_arbor:isharmla_attack");
    }

    private void addValuesToTag(String namespace, String tagPath, String... values) {
        var targetTag = TagKey.create(Registries.DAMAGE_TYPE, location(namespace, tagPath));
        for (var value : values) {
            if (value.startsWith("#")) {
                tag(targetTag).addTag(TagKey.create(Registries.DAMAGE_TYPE, entryLocation(value.substring(1))));
            } else {
                tag(targetTag).add(ResourceKey.create(Registries.DAMAGE_TYPE, entryLocation(value)));
            }
        }
    }

    private static ResourceLocation entryLocation(String id) {
        int separator = id.indexOf(':');
        if (separator >= 0) {
            String namespace = id.substring(0, separator);
            String path = id.substring(separator + 1);
            return location(namespace, path);
        }
        return ResourceLocation.withDefaultNamespace(id);
    }

    private static ResourceLocation location(String namespace, String path) {
        if ("minecraft".equals(namespace)) {
            return ResourceLocation.withDefaultNamespace(path);
        }
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
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
