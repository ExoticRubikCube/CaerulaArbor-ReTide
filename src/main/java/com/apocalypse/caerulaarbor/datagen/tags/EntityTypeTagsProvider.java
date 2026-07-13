package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 生成实体类型标签数据
 */
public class EntityTypeTagsProvider extends TagsProvider.RegistryTagsProvider<EntityType<?>> {
    private static final TagKey<EntityType<?>> BOSSOFFSPRING = caEntityTypeTag("bossoffspring");
    private static final TagKey<EntityType<?>> CANNOT_TRANSFORM = caEntityTypeTag("cannot_transform");
    private static final TagKey<EntityType<?>> ENDSPEAKER_EDIBLE = caEntityTypeTag("endspeaker_edible");
    private static final TagKey<EntityType<?>> GOLEMS = caEntityTypeTag("golems");
    private static final TagKey<EntityType<?>> HOMO_SAPIENS = caEntityTypeTag("homo_sapiens");
    private static final TagKey<EntityType<?>> HUNTERS = caEntityTypeTag("hunters");
    private static final TagKey<EntityType<?>> IGNORE_MIGRATION = caEntityTypeTag("ignore_migration");
    private static final TagKey<EntityType<?>> IMMUE_TO_INST_SANITY = caEntityTypeTag("immue_to_inst_sanity");
    private static final TagKey<EntityType<?>> IMMUE_TO_NETHERSEA_BRAND = caEntityTypeTag("immue_to_nethersea_brand");
    private static final TagKey<EntityType<?>> INQUISITION = caEntityTypeTag("inquisition");
    private static final TagKey<EntityType<?>> IS_HUMANSIDE = caEntityTypeTag("is_humanside");
    private static final TagKey<EntityType<?>> IZUMIK_DISCOVERS = caEntityTypeTag("izumik_discovers");
    private static final TagKey<EntityType<?>> MARINEMOBS = caEntityTypeTag("marinemobs");
    private static final TagKey<EntityType<?>> NO_JOIN_WHIRL = caEntityTypeTag("no_join_whirl");
    private static final TagKey<EntityType<?>> OCEANELITE = caEntityTypeTag("oceanelite");
    private static final TagKey<EntityType<?>> OCEANOFFSPRING = caEntityTypeTag("oceanoffspring");
    private static final TagKey<EntityType<?>> OCEANPET = caEntityTypeTag("oceanpet");
    private static final TagKey<EntityType<?>> OCEANSPAWN = caEntityTypeTag("oceanspawn");
    private static final TagKey<EntityType<?>> PHALAX = caEntityTypeTag("phalax");
    private static final TagKey<EntityType<?>> PORTABLE = caEntityTypeTag("portable");
    private static final TagKey<EntityType<?>> SEA_FRIEND = caEntityTypeTag("sea_friend");
    private static final TagKey<EntityType<?>> SKIP_MIGRATION = caEntityTypeTag("skip_migration");
    private static final TagKey<EntityType<?>> WARRIORS = caEntityTypeTag("warriors");
    private static final TagKey<EntityType<?>> WITH_LOW_SANITY_MODIFIER = caEntityTypeTag("with_low_sanity_modifier");
    private static final TagKey<EntityType<?>> WITH_LOWER_SANITY_MODIFIER = caEntityTypeTag("with_lower_sanity_modifier");
    private static final TagKey<EntityType<?>> WITH_LOWEST_SANITY_MODIFIER = caEntityTypeTag("with_lowest_sanity_modifier");
    private static final TagKey<EntityType<?>> WITH_LOWEST_SMALLER_SANITY_MODIFIER = caEntityTypeTag("with_lowest_smaller_sanity_modifier");
    private static final TagKey<EntityType<?>> WITH_LOWEST_SMALLEST_SANITY_MODIFIER = caEntityTypeTag("with_lowest_smallest_sanity_modifier");
    private static final TagKey<EntityType<?>> WITH_ZERO_SANITY_MODIFIER = caEntityTypeTag("with_zero_sanity_modifier");

    private static final TagKey<EntityType<?>> FORGE_BOSSES = forgeEntityTypeTag("bosses");
    private static final TagKey<EntityType<?>> FORGE_NETHER_MOBS = forgeEntityTypeTag("nether_mobs");

    public EntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENTITY_TYPE, lookupProvider, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addEntityTypesToTag(BOSSOFFSPRING, CAEntities.SUPER_SLIDER, CAEntities.ROUTE_SHAPER, CAEntities.BISHOP_FISH, CAEntities.TIDE_BISHOP, CAEntities.TIDE_DEATHREPELLER, CAEntities.MEGA_CHEST, CAEntities.FIRST_TO_TALK, CAEntities.HIGHMORE, CAEntities.OCEANIZED_BRUTE, CAEntities.IZUMIK, CAEntities.MARTUS, CAEntities.OCEANIZED_WARDEN, CAEntities.SUPER_BIG_CAT, CAEntities.OCEANIZED_WARDENIS, CAEntities.OCEANIZED_WITHER, CAEntities.OCEANIZED_WITHERIA, CAEntities.ENDSPEAKER, CAEntities.LINGERING_PATHSHAPER, CAEntities.TIDE_CHIMERA, CAEntities.SKADI_CORRUPTED, CAEntities.OCEANIZED_ILLUSIONER, CAEntities.ISHARMLA, CAEntities.COMPASSION_PRAYER, CAEntities.OCEANIZED_ENDERINA, CAEntities.THIRSTER);
        addEntityTypesToTag(CANNOT_TRANSFORM, EntityType.IRON_GOLEM, CAEntities.CHITIN_GOLEM, EntityType.SNOW_GOLEM, EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.STRIDER, EntityType.VEX);
        addTagsToTag(CANNOT_TRANSFORM, OCEANOFFSPRING, GOLEMS, HUNTERS, SEA_FRIEND);
        addEntityTypesToTag(ENDSPEAKER_EDIBLE, CAEntities.BASELAYER_ABYSSAL, CAEntities.CRACKER_ABYSSAL, CAEntities.GUIDE_ABYSSAL, CAEntities.PREDATOR_ABYSSAL, CAEntities.SPLASHER_ABYSSAL, CAEntities.UMBRELLA_ABYSSAL);
        addEntityTypesToTag(GOLEMS, CAEntities.CHITIN_GOLEM, CAEntities.SPIKE_CHEST, CAEntities.COMPLEX_CHITIN_GOLEM, CAEntities.LAST_KNIGHT_AND_HORSE, CAEntities.THE_LAST_KNIGHT, CAEntities.FLAMARINE_STATUE, CAEntities.FLAMARINE_GOLEM, CAEntities.MOIST_DRAGON_BREATH);
        addEntityTypesToTag(HOMO_SAPIENS, CAEntities.APOCATA, CAEntities.GUNMU);
        addTagsToTag(HOMO_SAPIENS, INQUISITION);
        addEntityTypesToTag(HUNTERS, CAEntities.SKADI, CAEntities.ULPIANS, CAEntities.GLADIIA, CAEntities.SPECTER, CAEntities.SPECTER_DOLL);
        addEntityTypesToTag(IGNORE_MIGRATION, CAEntities.OCEANIZED_WARDEN, CAEntities.OCEANIZED_WARDENIS, CAEntities.IZUMIK, CAEntities.IZUMIK_OFFSPRING, CAEntities.OCEANIZED_WITHER, CAEntities.MARTUS, CAEntities.TIDE_CHIMERA, CAEntities.MOIST_DRAGON_BREATH, CAEntities.MOIST_ENDER_CRYSTAL);
        addEntityTypesToTag(IMMUE_TO_INST_SANITY, CAEntities.OCEANIZED_WITCH);
        addEntityTypesToTag(IMMUE_TO_NETHERSEA_BRAND, CAEntities.LITTLE_HELPER, CAEntities.AL_1_S_HELPER, CAEntities.FLAMARINE_STATUE, CAEntities.FLAMARINE_GOLEM, CAEntities.QUNYOU_WANTED_ISHARMLA);
        addTagsToTag(IMMUE_TO_NETHERSEA_BRAND, OCEANOFFSPRING, GOLEMS);
        addEntityTypesToTag(INQUISITION, CAEntities.TRIBUNAL_HEALER, CAEntities.IRENE, CAEntities.SAINT_CARMEN);
        addTagsToTag(INQUISITION, WARRIORS, PHALAX);
        addEntityTypesToTag(IS_HUMANSIDE, CAEntities.GLADIIA_WHIRL, CAEntities.CHITIN_GOLEM, CAEntities.COMPLEX_CHITIN_GOLEM, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER, CAEntities.LAST_KNIGHT_AND_HORSE, CAEntities.THE_LAST_KNIGHT);
        addTagsToTag(IS_HUMANSIDE, HUNTERS, INQUISITION, OCEANPET);
        addEntityTypesToTag(IZUMIK_DISCOVERS, CAEntities.BONE_FISH, CAEntities.CRACKER_ABYSSAL, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.DROWNED, EntityType.ENDERMAN, EntityType.SILVERFISH, EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, CAEntities.FLEE_FISH, EntityType.BLAZE, EntityType.CREEPER, CAEntities.FIRST_TO_TALK, EntityType.PHANTOM, EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.LLAMA, EntityType.SHULKER, EntityType.SKELETON, EntityType.WITHER_SKELETON, EntityType.MAGMA_CUBE, CAEntities.ROUTE_FRACTAL, CAEntities.OCEANIZED_COW, CAEntities.OCEANIZED_DOG, CAEntities.OCEANIZED_HORSE, CAEntities.OCEANIZED_PIG, CAEntities.OCEANIZED_PIGLIN, CAEntities.OCEANIZED_PILLAGER, CAEntities.OCEANIZED_RAVAGER, CAEntities.OCEANIZED_SHEEP, CAEntities.OCEANIZED_VILLAGER, CAEntities.OCEANIZED_VINDICATOR, CAEntities.OCEANIZED_WOLF, CAEntities.OCEANIZED_WITCH, CAEntities.OCEANIZED_ENDERMAN, CAEntities.OCEANIZED_SPIDER, CAEntities.OCEANIZED_BRUTE, CAEntities.OCEANIZED_CAT, CAEntities.OCEANIZED_EVOKER, CAEntities.OCEANIZED_WARDENIS, CAEntities.OCEANIZED_FOX, CAEntities.OCEANIZED_POLAR_BEAR, CAEntities.IZUMIK_OFFSPRING, CAEntities.LINGERING_FRACTAL, CAEntities.OCEANIZE_RABBIT, CAEntities.COMPASSION_PRAYER, CAEntities.THIRSTER, CAEntities.NETHERSEA_SLIME, CAEntities.OCEANIZED_CHICKEN);
        addEntityTypesToTag(MARINEMOBS, CAEntities.COLLECTOR_PROKARYOTE, CAEntities.BONE_FISH, CAEntities.APOSTLE_PROKARYOTE, CAEntities.FLOATER_PROKARYOTE, CAEntities.ACCUMULATOR_PROKARYOTE, CAEntities.ACCUMULATOR_CLONE, CAEntities.FEEDER_PROKARYOTE, CAEntities.NUCLEIC_MALEFICENT, CAEntities.DEPOSITER_PROKARYOTE, CAEntities.NAUTILUS_HEADHUNTER);
        addEntityTypesToTag(NO_JOIN_WHIRL, EntityType.GLOW_ITEM_FRAME, EntityType.ITEM_DISPLAY, EntityType.ITEM_FRAME, EntityType.ZOMBIE, EntityType.BOAT, EntityType.CHEST_BOAT, EntityType.MINECART, EntityType.CHEST_MINECART, EntityType.COMMAND_BLOCK_MINECART, EntityType.FURNACE_MINECART, EntityType.HOPPER_MINECART, EntityType.SPAWNER_MINECART, EntityType.TNT_MINECART, CAEntities.GLADIIA_WHIRL, CAEntities.AL_1_S_HELPER, CAEntities.LITTLE_HELPER, CAEntities.ISHARMLA_TEAR, CAEntities.QUNYOU_WANTED_ISHARMLA);
        addEntityTypesToTag(OCEANELITE, CAEntities.BASELAYER_ABYSSAL, CAEntities.CRACKER_ABYSSAL, CAEntities.CREEPER_FISH, CAEntities.FIRST_TO_TALK, CAEntities.FLEE_FISH, CAEntities.GUIDE_ABYSSAL, CAEntities.PREGNANT_FISH, CAEntities.PUNCTURE_FISH, CAEntities.REAPER_FISH, CAEntities.UMBRELLA_ABYSSAL, CAEntities.MEGA_CHEST, CAEntities.APOSTLE_PROKARYOTE, CAEntities.CHEST_FISH, CAEntities.OCEANIZED_VINDICATOR, CAEntities.OCEANIZED_ENDERMAN, CAEntities.OCEANIZED_RAVAGER, CAEntities.IZUMIK_OFFSPRING, CAEntities.OCEANIZED_EVOKER, CAEntities.THE_ABANDONED, CAEntities.NUCLEIC_MALEFICENT, CAEntities.TIDUTANT_ROCK_SPIDER, CAEntities.SCREAM_CHEST_FISH, CAEntities.OCEANIZED_SHULKER);
        addEntityTypesToTag(OCEANOFFSPRING, CAEntities.BASELAYER_ABYSSAL, CAEntities.CREEPER_FISH, CAEntities.FLY_FISH, CAEntities.PUNCTURE_FISH, CAEntities.REAPER_FISH, CAEntities.RUN_FISH, CAEntities.SHOOTER_FISH, CAEntities.SLIDER_FISH, CAEntities.SUPER_SLIDER, CAEntities.PREDATOR_ABYSSAL, CAEntities.GUIDE_ABYSSAL, CAEntities.SPLASHER_ABYSSAL, CAEntities.UMBRELLA_ABYSSAL, CAEntities.CRACKER_ABYSSAL, CAEntities.COLLECTOR_PROKARYOTE, CAEntities.FAKE_OFFSPRING, CAEntities.FLEE_FISH, CAEntities.PREGNANT_FISH, CAEntities.ROUTE_SHAPER, CAEntities.ROUTE_FRACTAL, CAEntities.FIRST_TO_TALK, CAEntities.CHISELER_FISH, CAEntities.BONE_FISH, CAEntities.REAPER_PET, CAEntities.BISHOP_FISH, CAEntities.SONS, CAEntities.TIDE_BISHOP, CAEntities.TIDE_DEATHREPELLER, CAEntities.MEGA_CHEST, CAEntities.FLOATER_PROKARYOTE, CAEntities.APOSTLE_PROKARYOTE, CAEntities.HIGHMORE, CAEntities.ACCUMULATOR_PROKARYOTE, CAEntities.FEEDER_PROKARYOTE, CAEntities.CHEST_FISH, CAEntities.OCEANIZED_VILLAGER, CAEntities.OCEANIZED_VINDICATOR, CAEntities.OCEANIZED_PILLAGER, CAEntities.DEPOSITER_PROKARYOTE, CAEntities.OCEANIZED_PIG, CAEntities.ACCUMULATOR_CLONE, CAEntities.OCEANIZED_SHEEP, CAEntities.OCEANIZED_COW, CAEntities.OCEANIZED_HORSE, CAEntities.OCEANIZED_PIGLIN, CAEntities.OCEANIZED_BRUTE, CAEntities.OCEANIZED_SPIDER, CAEntities.OCEANIZED_ENDERMAN, CAEntities.OCEANIZED_DOG, CAEntities.OCEANIZED_WOLF, CAEntities.OCEANIZED_RAVAGER, CAEntities.OCEANIZED_WITCH, CAEntities.IZUMIK_OFFSPRING, CAEntities.IZUMIK, CAEntities.OCEANIZED_EVOKER, CAEntities.THE_ABANDONED, CAEntities.MARTUS, CAEntities.OCEANIZED_WARDEN, CAEntities.DIVICELLULAR_GO, CAEntities.OCEANIZED_CAT, CAEntities.SUPER_BIG_CAT, CAEntities.OCEANIZED_WARDENIS, CAEntities.NUCLEIC_MALEFICENT, CAEntities.OCEANIZED_WITHER, CAEntities.OCEANIZED_WITHERIA, CAEntities.ROCINANTE, CAEntities.OCEANIZED_FOX, CAEntities.TIDUTANT_EXCRESCENCE, CAEntities.OCEANIZED_POLAR_BEAR, CAEntities.TIDUTANT_ROCK_SPIDER, CAEntities.ENDSPEAKER, CAEntities.LINGERING_PATHSHAPER, CAEntities.LINGERING_FRACTAL, CAEntities.TIDE_CHIMERA, CAEntities.SKADI_CORRUPTED, CAEntities.OCEANIZE_RABBIT, CAEntities.OCEANIZED_ILLUSIONER, CAEntities.OCEAN_ILLUSION, CAEntities.NAUTILUS_HEADHUNTER, CAEntities.OCEANIZED_VEX, CAEntities.ISHARMLA, CAEntities.ISHARMLA_TEAR, CAEntities.COMPASSION_PRAYER, CAEntities.OCEANIZED_ENDERINA, CAEntities.MOIST_ENDER_CRYSTAL, CAEntities.THIRSTER, CAEntities.ABSORBER_LIMB, CAEntities.SCREAM_CHEST_FISH, CAEntities.OCEANIZED_CHICKEN, CAEntities.NETHERSEA_SLIME, CAEntities.OCEANIZED_SHULKER);
        addEntityTypesToTag(OCEANPET, CAEntities.REAPER_PET, CAEntities.OCEANIZED_DOG, CAEntities.ROCINANTE);
        addEntityTypesToTag(OCEANSPAWN, CAEntities.FAKE_OFFSPRING, CAEntities.ROUTE_FRACTAL, CAEntities.SONS, CAEntities.ACCUMULATOR_CLONE, CAEntities.CHEST_FISH, CAEntities.OCEANIZED_VILLAGER, CAEntities.OCEANIZED_VINDICATOR, CAEntities.OCEANIZED_PILLAGER, CAEntities.OCEANIZED_PIG, CAEntities.OCEANIZED_SHEEP, CAEntities.OCEANIZED_COW, CAEntities.OCEANIZED_HORSE, CAEntities.OCEANIZED_PIGLIN, CAEntities.OCEANIZED_SPIDER, CAEntities.OCEANIZED_ENDERMAN, CAEntities.OCEANIZED_RAVAGER, CAEntities.OCEANIZED_WITCH, CAEntities.IZUMIK_OFFSPRING, CAEntities.OCEANIZED_EVOKER, CAEntities.THE_ABANDONED, CAEntities.DIVICELLULAR_GO, CAEntities.OCEANIZED_CAT, CAEntities.OCEANIZED_FOX, CAEntities.TIDUTANT_EXCRESCENCE, CAEntities.OCEANIZED_POLAR_BEAR, CAEntities.TIDUTANT_ROCK_SPIDER, CAEntities.LINGERING_FRACTAL, CAEntities.OCEANIZE_RABBIT, CAEntities.OCEANIZED_ILLUSIONER, CAEntities.OCEAN_ILLUSION, CAEntities.NAUTILUS_HEADHUNTER, CAEntities.OCEANIZED_VEX, CAEntities.ISHARMLA_TEAR, CAEntities.MOIST_ENDER_CRYSTAL, CAEntities.ABSORBER_LIMB, CAEntities.SCREAM_CHEST_FISH, CAEntities.OCEANIZED_CHICKEN, CAEntities.NETHERSEA_SLIME, CAEntities.OCEANIZED_SHULKER);
        addEntityTypesToTag(PHALAX, CAEntities.CORRECTIONAL_PHALAX_VANGUARD, CAEntities.CORRECTIONAL_PHALANXY_INFANTRY);
        addEntityTypesToTag(PORTABLE, CAEntities.THE_ABANDONED);
        addTagsToTag(PORTABLE, HOMO_SAPIENS, HUNTERS);
        addEntityTypesToTag(SEA_FRIEND, EntityType.GLOW_SQUID, EntityType.SQUID, EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN, EntityType.AXOLOTL, EntityType.PANDA, EntityType.DOLPHIN, EntityType.TADPOLE, EntityType.PUFFERFISH, EntityType.CREEPER, EntityType.GHAST, EntityType.TURTLE, EntityType.TROPICAL_FISH, EntityType.SALMON, EntityType.COD, CAEntities.APOCATA, CAEntities.LITTLE_HELPER, CAEntities.AL_1_S_HELPER, EntityType.ARMOR_STAND, CAEntities.MOIST_DRAGON_BREATH, CAEntities.SPIKE_CHEST);
        addEntityTypesToTag(SKIP_MIGRATION, CAEntities.OCEANIZED_WITHER, CAEntities.TIDE_CHIMERA, CAEntities.MOIST_DRAGON_BREATH, CAEntities.MOIST_ENDER_CRYSTAL);
        addEntityTypesToTag(WARRIORS, CAEntities.JUNIOR_WARRIOR_PRIEST, CAEntities.WARRIOR_PRIEST);
        addTagsToTag(WITH_LOW_SANITY_MODIFIER, INQUISITION);
        addTagsToTag(WITH_LOWER_SANITY_MODIFIER, OCEANOFFSPRING);
        addEntityTypesToTag(WITH_LOWEST_SANITY_MODIFIER, EntityType.WARDEN);
        addTagsToTag(WITH_LOWEST_SANITY_MODIFIER, OCEANELITE);
        addTagsToTag(WITH_LOWEST_SMALLER_SANITY_MODIFIER, FORGE_BOSSES, HUNTERS);
        addEntityTypesToTag(WITH_LOWEST_SMALLEST_SANITY_MODIFIER, EntityType.IRON_GOLEM, CAEntities.CHITIN_GOLEM, CAEntities.THE_LAST_KNIGHT, CAEntities.LAST_KNIGHT_AND_HORSE, CAEntities.COMPLEX_CHITIN_GOLEM, CAEntities.FLAMARINE_STATUE, CAEntities.FLAMARINE_GOLEM);
        addEntityTypesToTag(WITH_ZERO_SANITY_MODIFIER, CAEntities.GUNMU, CAEntities.AL_1_S_HELPER, CAEntities.LITTLE_HELPER, CAEntities.IZUMIK, CAEntities.QUNYOU_WANTED_ISHARMLA, CAEntities.ISHARMLA_TEAR);

        addEntityTypesToTag(FORGE_BOSSES, CAEntities.CHITIN_GOLEM, CAEntities.COMPLEX_CHITIN_GOLEM, CAEntities.THE_LAST_KNIGHT, CAEntities.LAST_KNIGHT_AND_HORSE, CAEntities.FLAMARINE_GOLEM);
        addTagsToTag(FORGE_BOSSES, BOSSOFFSPRING, HUNTERS);
        addEntityTypesToTag(FORGE_NETHER_MOBS, EntityType.BLAZE, EntityType.GHAST, EntityType.MAGMA_CUBE, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN, CAEntities.OCEANIZED_BRUTE, CAEntities.OCEANIZED_PIGLIN, CAEntities.OCEANIZED_WITHER, CAEntities.OCEANIZED_WITHERIA);

        addEntityTypesToTag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS, CAEntities.THE_LAST_KNIGHT, CAEntities.MEGA_CHEST, CAEntities.LAST_KNIGHT_AND_HORSE, CAEntities.OCEANIZED_FOX, CAEntities.OCEANIZED_POLAR_BEAR);
    }
    private static TagKey<EntityType<?>> caEntityTypeTag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, path));
    }

    private static TagKey<EntityType<?>> forgeEntityTypeTag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    /**
     * 向目标标签加入实体类型字段
     *
     * @param targetTag 目标标签
     * @param types     要加入的实体类型字段
     */
    private void addEntityTypesToTag(TagKey<EntityType<?>> targetTag, Object... types) {
        var appender = tag(targetTag);
        for (var type : types) {
            appender.add(entityTypeKey(type));
        }
    }

    private static ResourceKey<EntityType<?>> entityTypeKey(Object type) {
        if (type instanceof RegistryObject<?> registryObject) {
            return ResourceKey.create(Registries.ENTITY_TYPE, Objects.requireNonNull(registryObject.getId()));
        }
        if (type instanceof EntityType<?> entityType) {
            return ResourceKey.create(Registries.ENTITY_TYPE, Objects.requireNonNull(EntityType.getKey(entityType)));
        }
        throw new IllegalArgumentException("Unsupported entity type field: " + type);
    }
}
