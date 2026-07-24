package com.susen36.caerulaarbor.datagen.tags;

import com.susen36.caerulaarbor.CaerulaArborMod;
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
     * 向 DataGenerator 注册全部标签 provider
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
     * 通用标签 provider 基类，支持向 caerula_arbor、forge 和 minecraft 命名空间写入标签
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

        /**
         * 按命名空间创建 ResourceLocation
         *
         * @param namespace 命名空间
         * @param path      路径
         * @return ResourceLocation
         */
        private static ResourceLocation location(String namespace, String path) {
            if ("minecraft".equals(namespace)) {
                return ResourceLocation.withDefaultNamespace(path);
            }
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }

        /**
         * 解析资源 ID，省略命名空间时按 minecraft 处理
         *
         * @param id 资源 ID
         * @return ResourceLocation
         */
        private static ResourceLocation location(String id) {
            int separator = id.indexOf(':');
            if (separator >= 0) {
                String namespace = id.substring(0, separator);
                String path = id.substring(separator + 1);
                return location(namespace, path);
            }
            return ResourceLocation.withDefaultNamespace(id);
        }

        /**
         * 创建 caerula_arbor 命名空间的标签 key
         *
         * @param tagPath 标签路径，不含命名空间
         * @return 标签 key
         */
        protected TagKey<T> modTag(String tagPath) {
            return TagKey.create(registryKey, location(CaerulaArborMod.MODID, tagPath));
        }

        /**
         * 创建 forge 命名空间的标签 key
         *
         * @param tagPath 标签路径，不含命名空间
         * @return 标签 key
         */
        protected TagKey<T> forgeTag(String tagPath) {
            return TagKey.create(registryKey, location("forge", tagPath));
        }

        /**
         * 创建 minecraft 命名空间的标签 key
         *
         * @param tagPath 标签路径，不含命名空间
         * @return 标签 key
         */
        protected TagKey<T> minecraftTag(String tagPath) {
            return TagKey.create(registryKey, ResourceLocation.withDefaultNamespace(tagPath));
        }

        /**
         * 创建指定 ID 的标签 key
         *
         * @param id 标签 ID
         * @return 标签 key
         */
        protected TagKey<T> tagKey(String id) {
            return TagKey.create(registryKey, location(id));
        }

        /**
         * 创建指定 ID 的注册表元素 key
         *
         * @param id 元素 ID
         * @return 元素 key
         */
        protected ResourceKey<T> entryKey(String id) {
            return ResourceKey.create(registryKey, location(id));
        }

        /**
         * 向目标标签加入元素 key
         *
         * @param targetTag 目标标签
         * @param entries   要加入的元素 key
         */
        @SafeVarargs
        protected final void addEntriesToTag(TagKey<T> targetTag, ResourceKey<T>... entries) {
            var appender = tag(targetTag);
            for (var entry : entries) {
                appender.add(entry);
            }
        }

        /**
         * 向目标标签加入子标签 key
         *
         * @param targetTag 目标标签
         * @param tags      要加入的子标签 key
         */
        @SafeVarargs
        protected final void addTagsToTag(TagKey<T> targetTag, TagKey<T>... tags) {
            var appender = tag(targetTag);
            for (var tag : tags) {
                appender.addTag(tag);
            }
        }
    }
}
