package com.apocalypse.caerulaarbor.datagen;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CaerulaArborMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var datapackProvider = new RegistryDataProvider(output, event.getLookupProvider());
        var lookupProvider = datapackProvider.getRegistryProvider();
        var existingFileHelper = event.getExistingFileHelper();

        // datapack registry things
        generator.addProvider(event.includeServer(), datapackProvider);

        // advancements
        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(
                output,
                lookupProvider,
                existingFileHelper,
                List.of(new AdvancementProvider())
        ));

        // damage type tags
        generator.addProvider(event.includeServer(), new DamageTypeTagsProvider(
                output,
                lookupProvider,
                existingFileHelper
        ));

        // loot tables
        generator.addProvider(event.includeServer(), LootTableProviders.create(output));

        // recipes
        //generator.addProvider(event.includeServer(), new RecipeProvider(output));
        //TODO : repice provider
    }
}
