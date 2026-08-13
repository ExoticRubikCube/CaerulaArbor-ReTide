package com.susen36.caerulaarbor.item.relic;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

//TODO 需要修改为idea可以直接内联到注册处的形态
public class BooleanCollectibleItem extends CollectibleItem.CustomCollectibleItem {

    public BooleanCollectibleItem(Rarity rarity, CollectibleActivation activation) {
        super(new Item.Properties().stacksTo(1).rarity(rarity),
                false, 25, CollectibleTiers.NORMAL, 0, 1, 0, activation);
    }

    /** 默认激活表现：对应原 {@code ActivateParams.standardBoolean()}。 */
    public static CollectibleActivation standardActivation() {
        return CollectibleActivation.builder()
                .sound(SoundEvents.PLAYER_LEVELUP, 2F, 1F)
                .particle(ParticleTypes.HAPPY_VILLAGER, 72)
                .showOverlay(true)
                .build();
    }

    @Override
    public void onUse(ItemStack stack, Level level, Player player) {
        // 布尔型收藏品：已拥有状态由 activate() 的 markUsed 维护
    }
}