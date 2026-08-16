package com.susen36.caerulaarbor.item;

import com.susen36.babel.collectible.CollectibleActivation;
import com.susen36.babel.collectible.CollectibleItem;
import com.susen36.babel.collectible.CollectibleTiers;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class CaerulaHeartItem extends CollectibleItem.CustomCollectibleItem {
	public CaerulaHeartItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), 25, false, CollectibleTiers.CURSED, new CollectibleItem.Levels(0, 1, 0),
				CollectibleActivation.forTier(CollectibleTiers.CURSED));
	}

	@Override
	public void onUse(ItemStack stack, Level level, Player player, CollectibleItem.CustomCollectibleItem self) {
		EntityUtils.getLight(player);
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
        capability.player_light = capability.player_light - 50;
		capability.syncPlayerVariables(player);
		if (ModCapabilities.getPlayerVariables(player).player_light < 0) {
			PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
            capability2.player_light = 0;
			capability2.syncPlayerVariables(player);
		}
		PlayerVariable capability3 = ModCapabilities.getPlayerVariables(player);
        capability3.disoclusion = Mth.nextInt(RandomSource.create(), 1, 4);
		capability3.syncPlayerVariables(player);
		if (player instanceof ServerPlayer serverPlayer) {
			AdvancementHolder adv = serverPlayer.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "to_we_many"));
			AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
			if (!ap.isDone()) {
				for (String criteria : ap.getRemainingCriteria())
					serverPlayer.getAdvancements().award(adv, criteria);
			}
		}
	}
}
