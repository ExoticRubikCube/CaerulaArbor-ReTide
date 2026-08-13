package com.susen36.caerulaarbor.item.relic;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.babel.collectible.Collectibles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

//TODO 需要清理和内联到注册处
public class SurvivorContractItem extends CollectibleItem.CustomCollectibleItem {

    public SurvivorContractItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC),
                false,
                25,
                CollectibleTiers.ADVANCED,
                0, 32, 0,
                CollectibleActivation.builder()
                        .sound(SoundEvents.BEACON_ACTIVATE, 3.2F, 1F)
                        .particle(ParticleTypes.GLOW, 72)
                        .showOverlay(true)
                        .build());
    }

    @Override
    public void onUse(ItemStack stack, Level level, Player player) {
        var layer = player.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get());
        layer.setLayer(stack.getItem(), 0);
    }
}