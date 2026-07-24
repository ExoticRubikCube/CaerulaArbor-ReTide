package com.susen36.caerulaarbor.datagen;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.datagen.tags.TagsProvider;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

/**
 * 数据生成器总入口，负责在 {@link GatherDataEvent} 中注册所有 datagen provider
 */
@EventBusSubscriber(modid = CaerulaArborMod.MODID)
public class DataGenerators {
    /**
     * 注册全部数据生成任务
     *
     * @param event Forge 提供的数据生成事件
     */
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var datapackProvider = new RegistryDataProvider(output, event.getLookupProvider());
        var lookupProvider = datapackProvider.getRegistryProvider();
        var existingFileHelper = event.getExistingFileHelper();

        // datapack registry things
        // worldgen is built into the single registry datapack provider
        generator.addProvider(event.includeServer(), datapackProvider);

        // advancements
        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(
                output,
                lookupProvider,
                existingFileHelper,
                List.of(new AdvancementProvider())
        ));

        // tags
        TagsProvider.addProviders(generator, event.includeServer(), output, lookupProvider, existingFileHelper);

        // loot tables
        generator.addProvider(event.includeServer(), LootTableProviders.create(output));

        // global loot modifiers
        generator.addProvider(event.includeServer(), new GlobalLootModifierProvider(
                output,
                CaerulaArborMod.MODID
        ));

        // biome modifiers
        generator.addProvider(event.includeServer(), new BiomeModifiersProvider(output));

        // recipes
        generator.addProvider(event.includeServer(), new RecipesProvider(output));
    }
}
