package com.susen36.caerulaarbor.datagen;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.datagen.worldgen.WorldgenProvider;
import com.susen36.caerulaarbor.init.CAEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 内置注册表数据的唯一 DatapackBuiltinEntriesProvider
 * <p>DamageType 和 worldgen 都应挂到同一个 BUILDER，避免重复 provider</p>
 */
public class RegistryDataProvider extends DatapackBuiltinEntriesProvider {
    /**
     * 共享注册表构建器，用于集中接入所有内置注册表 bootstrap
     */
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

    static {
        BUILDER.add(Registries.DAMAGE_TYPE, DamageTypes::bootstrap);
        BUILDER.add(Registries.ENCHANTMENT, CAEnchantments::bootstrap);
        WorldgenProvider.init(BUILDER);
    }

    /**
     * 创建内置注册表数据 provider
     *
     * @param output   datagen 输出位置
     * @param provider 上游注册表查询 provider
     */
    public RegistryDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", CaerulaArborMod.MODID));
    }
}
