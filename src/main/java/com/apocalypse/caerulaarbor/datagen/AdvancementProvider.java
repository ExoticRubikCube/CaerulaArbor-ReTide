package com.apocalypse.caerulaarbor.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AdvancementProvider implements ForgeAdvancementProvider.AdvancementGenerator {

    public static ArrayList<Advancement> advancements = new ArrayList<>();

    @Override
    public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<Advancement> saver, @NotNull ExistingFileHelper existingFileHelper) {
        /* lore/research (root)
        var research = Advancement.Builder.advancement()
                .display(new DisplayInfo(HexItems.LORE_FRAGMENT.getDefaultInstance(),
                                Component.translatable("advancement.hexvoid:lore/research"),
                                Component.translatable("advancement.hexvoid:lore/research.desc"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/brain_coral_block.png"),
                                FrameType.TASK,
                                true,
                                true,
                                false
                        )
                )
                .addCriterion(HexvoidTags.ADV_CRITERION_GRANT, new Criterion(new ImpossibleTrigger.TriggerInstance()))
                .save(saver, HexvoidTags.ADV_LORE_RESEARCH, existingFileHelper);
        advancements.add(research);
*/
        //TODO : advancement
    }
}
