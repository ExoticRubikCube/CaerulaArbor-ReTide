package com.apocalypse.caerulaarbor.datagen.tags;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 标签 datagen 的聚合入口，统一注册所有 registry 类型的 tag provider
 */
public final class TagsProvider {

    private TagsProvider() {
    }

    /**
     * 向 DataGenerator 注册本模组使用的全部标签 provider
     *
     * @param generator          Forge 数据生成器
     * @param includeServer      是否生成服务端数据
     * @param output             datagen 输出位置
     * @param lookupProvider     上游注册表查询 provider
     * @param existingFileHelper 已有资源检查器，可为 null
     */
    public static void addProviders(DataGenerator generator, boolean includeServer, PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        generator.addProvider(includeServer, new DamageTypeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new BlockTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new EntityTypeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new ItemTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new BiomeTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new PaintingVariantTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(includeServer, new PoiTypeTagsProvider(output, lookupProvider, existingFileHelper));
    }

    /**
     * 通用标签 provider 基类，支持向本模组、forge 和 minecraft 命名空间写入标签
     *
     * @param <T> 标签所属注册表元素类型
     */
    protected static abstract class RegistryTagsProvider<T> extends net.minecraft.data.tags.TagsProvider<T> {
        private final ResourceKey<? extends Registry<T>> registryKey;

        /**
         * 创建指定注册表的标签 provider
         *
         * @param output             datagen 输出位置
         * @param registryKey        标签所属注册表
         * @param lookupProvider     上游注册表查询 provider
         * @param existingFileHelper 已有资源检查器，可为 null
         */
        protected RegistryTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
                                       CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       @Nullable ExistingFileHelper existingFileHelper) {
            super(output, registryKey, lookupProvider, CaerulaArborMod.MODID, existingFileHelper);
            this.registryKey = registryKey;
        }

        private static ResourceLocation tagLocation(String namespace, String path) {
            if ("minecraft".equals(namespace)) {
                return ResourceLocation.withDefaultNamespace(path);
            }
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }

        private static ResourceLocation entryLocation(String id) {
            int separator = id.indexOf(':');
            if (separator >= 0) {
                String namespace = id.substring(0, separator);
                String path = id.substring(separator + 1);
                if ("minecraft".equals(namespace)) {
                    return ResourceLocation.withDefaultNamespace(path);
                }
                return ResourceLocation.fromNamespaceAndPath(namespace, path);
            }
            return ResourceLocation.withDefaultNamespace(id);
        }

        /**
         * 向 caerula_arbor 命名空间的指定标签加入元素或子标签
         *
         * @param tagPath 标签路径，不含命名空间
         * @param values  元素 ID 或以 # 开头的子标签 ID
         */
        protected void addValues(String tagPath, String... values) {
            addValues(CaerulaArborMod.MODID, tagPath, values);
        }

        /**
         * 向 forge 命名空间的指定标签加入元素或子标签
         *
         * @param tagPath 标签路径，不含命名空间
         * @param values  元素 ID 或以 # 开头的子标签 ID
         */
        protected void addForgeValues(String tagPath, String... values) {
            addValues("forge", tagPath, values);
        }

        /**
         * 向 minecraft 命名空间的指定标签加入元素或子标签
         *
         * @param tagPath 标签路径，不含命名空间
         * @param values  元素 ID 或以 # 开头的子标签 ID
         */
        protected void addMinecraftValues(String tagPath, String... values) {
            addValues("minecraft", tagPath, values);
        }

        private void addValues(String namespace, String tagPath, String... values) {
            var appender = tag(TagKey.create(registryKey, tagLocation(namespace, tagPath)));
            for (var value : values) {
                if (value.startsWith("#")) {
                    appender.addTag(TagKey.create(registryKey, entryLocation(value.substring(1))));
                } else {
                    appender.add(ResourceKey.create(registryKey, entryLocation(value)));
                }
            }
        }
    }
}
