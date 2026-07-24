package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = CaerulaArborMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CALootModifier {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "caerula_arbor");
    public static final RegistryObject<Codec<CaerulaArborModLootTableModifier>> LOOT_MODIFIER = LOOT_MODIFIERS.register("caerula_arbor_loot_modifier", CaerulaArborModLootTableModifier.CODEC);

    public static FMLJavaModLoadingContext context;

    public static void init(FMLJavaModLoadingContext ctx) {
        context = ctx;
    }

    @SubscribeEvent
    public static void register(FMLConstructModEvent event) {
        IEventBus bus = context.getModEventBus();
        event.enqueueWork(() -> LOOT_MODIFIERS.register(bus));
    }

    public static class CaerulaArborModLootTableModifier extends LootModifier {
        public static final Supplier<Codec<CaerulaArborModLootTableModifier>> CODEC = Suppliers
                .memoize(() -> RecordCodecBuilder.create(instance -> codecStart(instance).and(ResourceLocation.CODEC.fieldOf("lootTable").forGetter(m -> m.lootTable)).apply(instance, CaerulaArborModLootTableModifier::new)));
        private final ResourceLocation lootTable;

        public CaerulaArborModLootTableModifier(LootItemCondition[] conditions, ResourceLocation lootTable) {
            super(conditions);
            this.lootTable = lootTable;
        }

        @Override
        protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            context.getResolver().getLootTable(lootTable).getRandomItems(context, generatedLoot::add);
            return generatedLoot;
        }

        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }
}
