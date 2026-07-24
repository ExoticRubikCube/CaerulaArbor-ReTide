package com.susen36.caerulaarbor.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FinalizeSpawnEventHandler {

    @SubscribeEvent
    public static void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity == null) {
            return;
        }
        if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
            entity.getPersistentData().putBoolean("caerulaNaturalSpawn", true);
        }
    }
}