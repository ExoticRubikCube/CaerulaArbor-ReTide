package com.susen36.caerulaarbor.init;

import com.susen36.caerulaarbor.CaerulaArbor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public final class CAJukeboxSongs {
    public static final ResourceKey<JukeboxSong> BLOODY_RECORD = key("bloody_record");
    public static final ResourceKey<JukeboxSong> RECORD_WHISPER = key("record_whisper");
    public static final ResourceKey<JukeboxSong> RECORD_ENDOSPORE = key("record_endospore");
    public static final ResourceKey<JukeboxSong> RECORD_PATH_AHEAD = key("record_path_ahead");
    public static final ResourceKey<JukeboxSong> RECORD_UNDERTIDES = key("record_undertides");
    public static final ResourceKey<JukeboxSong> RECORD_DEEPNESS = key("record_deepness");
    public static final ResourceKey<JukeboxSong> RECORD_ISHARMLA = key("record_isharmla");
    public static final ResourceKey<JukeboxSong> RECORD_OCEANWISH = key("record_oceanwish");
    public static final ResourceKey<JukeboxSong> RECORD_UNDERDAWN = key("record_underdawn");
    public static final ResourceKey<JukeboxSong> RECORD_MARE_NATUS = key("record_mare_natus");
    public static final ResourceKey<JukeboxSong> SCORE = key("score");

    private static ResourceKey<JukeboxSong> key(String id) {
        return ResourceKey.create(
            Registries.JUKEBOX_SONG,
            ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, id)
        );
    }

    private CAJukeboxSongs() {
    }
}
