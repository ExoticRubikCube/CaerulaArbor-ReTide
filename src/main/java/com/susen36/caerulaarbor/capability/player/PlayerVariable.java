package com.susen36.caerulaarbor.capability.player;

import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.network.receive.PlayerVariablesSyncMessage;
import com.susen36.caerulaarbor.relic.RelicType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

import static com.susen36.caerulaarbor.init.CARelics.RELICS_REGISTRY;

public class PlayerVariable implements INBTSerializable<CompoundTag> {

    /* ========== 非遗物基础状态（保留原样） ========== */
    public double player_light = 100.0;
    public double player_lives = CAConfigs.LP_INIT.get();
    public double player_maxlive = CAConfigs.LP_INIT.get();
    public double player_shield = 0;
    public double disoclusion = 0;
    public boolean show_stats = true;
    public boolean kingShowPtc = true;
    public ItemStack chitin_knife_selected = ItemStack.EMPTY;
    public boolean player_util_RAINBOW = false;
    public boolean player_util_AROMATIC = false;
    public double player_king_suit = 0;
    public double player_demon_suit = 0;
    public double player_oceanization = 0;
    public double plauyer_balance = 0;
    public boolean can_player_evo = false;
    public double reserve_quantity = 0;
    public double reserve_quality = 0;
    public boolean PEVO_NEXUS_no_rejection = false;
    public boolean PEVO_NEXUS_reg_sanity = false;
    public double PEVO_NODE_add_def = 0;
    public double PEVO_NODE_add_resis = 0;
    public double PEVO_NODE_add_speed = 0;
    public double PEVO_NODE_add_sanity = 0;
    public boolean PEVO_NEXUS_reg_lights = false;
    public double PEVO_NODE_add_damage = 0;
    public double PEVO_NODE_less_damage = 0;
    public double PEVO_NODE_living_barrier = 0;
    public double PEVO_NODE_add_miss = 0;
    public boolean PEVO_NEXUS_perc_damage = false;
    public double PEVO_NODE_real_damage = 0;
    public double PEVO_NODE_heal_damage = 0;
    public double PEVO_NODE_worse_break = 0;
    public boolean PEVO_NEXUS_expo_shield = false;
    public double PEVO_NODE_eunectes = 0;
    public double PEVO_NODE_less_armor = 0;

    /* ========== 遗物注册制存储 ========== */
    private final Map<ResourceKey<RelicType>, Integer> relicLevels = new HashMap<>();

    /**
     * 注册制 getter：按 ResourceKey 查遗物等级。
     * 若玩家尚未持有该 key（包括存档迁移前没读到的），返回对应 RelicType 的默认值。
     */
    public int getRelic(ResourceKey<RelicType> key) {
        Integer stored = relicLevels.get(key);
        if (stored != null) {
            return stored;
        }
        RelicType type = RELICS_REGISTRY.get(key);
        return type == null ? 0 : type.defaultLevel();
    }

    /**
     * 注册制 setter：统一 clamp，然后入 map（若和默认值相等就 remove 节省 NBT 空间）。
     */
    public void setRelic(ResourceKey<RelicType> key, int level) {
        RelicType type = RELICS_REGISTRY.get(key);
        int clamped = (type == null) ? level : Mth.clamp(level, type.minLevel(), type.maxLevel());
        int def = (type == null) ? 0 : type.defaultLevel();
        if (clamped == def) {
            relicLevels.remove(key);
        } else {
            relicLevels.put(key, clamped);
        }
    }

    public void syncPlayerVariables(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
        }
    }

    public Tag writeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("player_light", player_light);
        nbt.putDouble("player_lives", player_lives);
        nbt.putDouble("player_maxlive", player_maxlive);
        nbt.putDouble("player_shield", player_shield);
        nbt.putDouble("disoclusion", disoclusion);
        nbt.putBoolean("show_stats", show_stats);
        nbt.putBoolean("kingShowPtc", kingShowPtc);
        nbt.put("chitin_knife_selected", chitin_knife_selected.saveOptional(null));
        nbt.putBoolean("player_util_RAINBOW", player_util_RAINBOW);
        nbt.putBoolean("player_util_AROMATIC", player_util_AROMATIC);
        nbt.putDouble("player_king_suit", player_king_suit);
        nbt.putDouble("player_demon_suit", player_demon_suit);
        nbt.putDouble("player_oceanization", player_oceanization);
        nbt.putDouble("plauyer_balance", plauyer_balance);
        nbt.putBoolean("can_player_evo", can_player_evo);
        nbt.putDouble("reserve_quantity", reserve_quantity);
        nbt.putDouble("reserve_quality", reserve_quality);
        nbt.putBoolean("PEVO_NEXUS_no_rejection", PEVO_NEXUS_no_rejection);
        nbt.putBoolean("PEVO_NEXUS_reg_sanity", PEVO_NEXUS_reg_sanity);
        nbt.putDouble("PEVO_NODE_add_def", PEVO_NODE_add_def);
        nbt.putDouble("PEVO_NODE_add_resis", PEVO_NODE_add_resis);
        nbt.putDouble("PEVO_NODE_add_speed", PEVO_NODE_add_speed);
        nbt.putDouble("PEVO_NODE_add_sanity", PEVO_NODE_add_sanity);
        nbt.putBoolean("PEVO_NEXUS_reg_lights", PEVO_NEXUS_reg_lights);
        nbt.putDouble("PEVO_NODE_add_damage", PEVO_NODE_add_damage);
        nbt.putDouble("PEVO_NODE_less_damage", PEVO_NODE_less_damage);
        nbt.putDouble("PEVO_NODE_living_barrier", PEVO_NODE_living_barrier);
        nbt.putDouble("PEVO_NODE_add_miss", PEVO_NODE_add_miss);
        nbt.putBoolean("PEVO_NEXUS_perc_damage", PEVO_NEXUS_perc_damage);
        nbt.putDouble("PEVO_NODE_real_damage", PEVO_NODE_real_damage);
        nbt.putDouble("PEVO_NODE_heal_damage", PEVO_NODE_heal_damage);
        nbt.putDouble("PEVO_NODE_worse_break", PEVO_NODE_worse_break);
        nbt.putBoolean("PEVO_NEXUS_expo_shield", PEVO_NEXUS_expo_shield);
        nbt.putDouble("PEVO_NODE_eunectes", PEVO_NODE_eunectes);
        nbt.putDouble("PEVO_NODE_less_armor", PEVO_NODE_less_armor);

        /* 新格式：relics 子 tag，存非默认等级的遗物（key 用资源名） */
        CompoundTag relicsTag = new CompoundTag();
        for (Map.Entry<ResourceKey<RelicType>, Integer> e : relicLevels.entrySet()) {
            relicsTag.putInt(e.getKey().location().toString(), e.getValue());
        }
        nbt.put("relics", relicsTag);
        return nbt;
    }

    public void readNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        relicLevels.clear();

        if (nbt.contains("player_light", Tag.TAG_ANY_NUMERIC)) {
            player_light = nbt.getDouble("player_light");
        }
        if (nbt.contains("player_lives", Tag.TAG_ANY_NUMERIC)) {
            player_lives = nbt.getDouble("player_lives");
        }
        if (player_lives < 1.0D) {
            player_lives = 1.0D;
        }
        if (nbt.contains("player_maxlive", Tag.TAG_ANY_NUMERIC)) {
            player_maxlive = nbt.getDouble("player_maxlive");
        }
        if (player_maxlive < CAConfigs.LP_INIT.get()) {
            player_maxlive = CAConfigs.LP_INIT.get();
        } else if (player_maxlive > CAConfigs.LP_LIMIT.get()) {
            player_maxlive = CAConfigs.LP_LIMIT.get();
        }
        if (player_lives > player_maxlive) {
            player_lives = player_maxlive;
        }
        if (nbt.contains("player_shield", Tag.TAG_ANY_NUMERIC)) {
            player_shield = nbt.getDouble("player_shield");
        }
        disoclusion = nbt.getDouble("disoclusion");
        show_stats = nbt.getBoolean("show_stats");
        kingShowPtc = nbt.getBoolean("kingShowPtc");
        chitin_knife_selected = ItemStack.parseOptional(null, nbt.getCompound("chitin_knife_selected"));
        player_util_RAINBOW = nbt.getBoolean("player_util_RAINBOW");
        player_util_AROMATIC = nbt.getBoolean("player_util_AROMATIC");
        player_king_suit = nbt.getDouble("player_king_suit");
        player_demon_suit = nbt.getDouble("player_demon_suit");
        player_oceanization = nbt.getDouble("player_oceanization");
        plauyer_balance = nbt.getDouble("plauyer_balance");
        can_player_evo = nbt.getBoolean("can_player_evo");
        reserve_quantity = nbt.getDouble("reserve_quantity");
        reserve_quality = nbt.getDouble("reserve_quality");
        PEVO_NEXUS_no_rejection = nbt.getBoolean("PEVO_NEXUS_no_rejection");
        PEVO_NEXUS_reg_sanity = nbt.getBoolean("PEVO_NEXUS_reg_sanity");
        PEVO_NODE_add_def = nbt.getDouble("PEVO_NODE_add_def");
        PEVO_NODE_add_resis = nbt.getDouble("PEVO_NODE_add_resis");
        PEVO_NODE_add_speed = nbt.getDouble("PEVO_NODE_add_speed");
        PEVO_NODE_add_sanity = nbt.getDouble("PEVO_NODE_add_sanity");
        PEVO_NEXUS_reg_lights = nbt.getBoolean("PEVO_NEXUS_reg_lights");
        PEVO_NODE_add_damage = nbt.getDouble("PEVO_NODE_add_damage");
        PEVO_NODE_less_damage = nbt.getDouble("PEVO_NODE_less_damage");
        PEVO_NODE_living_barrier = nbt.getDouble("PEVO_NODE_living_barrier");
        PEVO_NODE_add_miss = nbt.getDouble("PEVO_NODE_add_miss");
        PEVO_NEXUS_perc_damage = nbt.getBoolean("PEVO_NEXUS_perc_damage");
        PEVO_NODE_real_damage = nbt.getDouble("PEVO_NODE_real_damage");
        PEVO_NODE_heal_damage = nbt.getDouble("PEVO_NODE_heal_damage");
        PEVO_NODE_worse_break = nbt.getDouble("PEVO_NODE_worse_break");
        PEVO_NEXUS_expo_shield = nbt.getBoolean("PEVO_NEXUS_expo_shield");
        PEVO_NODE_eunectes = nbt.getDouble("PEVO_NODE_eunectes");
        PEVO_NODE_less_armor = nbt.getDouble("PEVO_NODE_less_armor");

        /* 新格式：relics 子 tag 全量读 */
        if (nbt.contains("relics", Tag.TAG_COMPOUND)) {
            CompoundTag relicsTag = nbt.getCompound("relics");
            for (String rawKey : relicsTag.getAllKeys()) {
                ResourceKey<RelicType> key = ResourceKey.create(CARelics.RELICS_REGISTRY_KEY,
                    net.minecraft.resources.ResourceLocation.parse(rawKey));
                int lvl = relicsTag.getInt(rawKey);
                setRelic(key, lvl);
            }
        }
    }

    private void migrateLegacyBoolean(CompoundTag nbt, String oldKey, ResourceKey<RelicType> newKey) {
        if (nbt.contains(oldKey, Tag.TAG_ANY_NUMERIC)) {
            setRelic(newKey, nbt.getBoolean(oldKey) ? 1 : 0);
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return (CompoundTag) writeNBT();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        readNBT(nbt);
    }
}
