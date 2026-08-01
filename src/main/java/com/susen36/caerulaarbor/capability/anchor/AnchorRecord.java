package com.susen36.caerulaarbor.capability.anchor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class AnchorRecord implements INBTSerializable<CompoundTag> {

    private final Long2ObjectMap<List<BlockPos>> section2anchorPosMap = new Long2ObjectOpenHashMap<>();

    private static Iterable<SectionPos> sectionsInAnchorRange(BlockPos anchorPos) {
        int x1 = SectionPos.blockToSectionCoord(anchorPos.getX() - 24);
        int y1 = SectionPos.blockToSectionCoord(anchorPos.getY() - 8);
        int z1 = SectionPos.blockToSectionCoord(anchorPos.getZ() - 24);
        int x2 = SectionPos.blockToSectionCoord(anchorPos.getX() + 24);
        int y2 = SectionPos.blockToSectionCoord(anchorPos.getY() + 8);
        int z2 = SectionPos.blockToSectionCoord(anchorPos.getZ() + 24);
        return () -> SectionPos.betweenClosedStream(x1, y1, z1, x2, y2, z2).iterator();
    }

    public void addAnchor(BlockPos anchorPos) {
        for (SectionPos sectionPos : sectionsInAnchorRange(anchorPos)) {
            section2anchorPosMap.computeIfAbsent(sectionPos.asLong(), l -> new ArrayList<>()).add(anchorPos);
        }
    }

    public void removeAnchor(BlockPos anchorPos) {
        for (SectionPos sectionPos : sectionsInAnchorRange(anchorPos)) {
            List<BlockPos> anchorList = section2anchorPosMap.get(sectionPos.asLong());
            if (anchorList != null) {
                anchorList.remove(anchorPos);
            }
        }
    }

    public boolean affectedByAnchor(BlockPos blockPos) {
        long sectionIn = SectionPos.asLong(blockPos);
        if (!section2anchorPosMap.containsKey(sectionIn)) {
            return false;
        }
        for (BlockPos anchorPos : section2anchorPosMap.get(sectionIn)) {
            if (Math.abs(anchorPos.getX() - blockPos.getX()) > 24) {
                continue;
            }
            if (Math.abs(anchorPos.getY() - blockPos.getY()) > 8) {
                continue;
            }
            if (Math.abs(anchorPos.getZ() - blockPos.getZ()) > 24) {
                continue;
            }
            return true;
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag sectionList = new ListTag();
        for (Long2ObjectMap.Entry<List<BlockPos>> entry : section2anchorPosMap.long2ObjectEntrySet()) {
            CompoundTag sectionTag = new CompoundTag();
            sectionTag.putLong("section", entry.getLongKey());
            ListTag posList = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                posList.add(LongTag.valueOf(pos.asLong()));
            }
            sectionTag.put("positions", posList);
            sectionList.add(sectionTag);
        }
        tag.put("sections", sectionList);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        section2anchorPosMap.clear();
        ListTag sectionList = nbt.getList("sections", Tag.TAG_COMPOUND);
        for (int i = 0; i < sectionList.size(); i++) {
            CompoundTag sectionTag = sectionList.getCompound(i);
            long section = sectionTag.getLong("section");
            List<BlockPos> posList = new ArrayList<>();
            ListTag positions = sectionTag.getList("positions", Tag.TAG_LONG);
            for (Tag position : positions) {
                posList.add(BlockPos.of(((LongTag) position).getAsLong()));
            }
            section2anchorPosMap.put(section, posList);
        }
    }
}