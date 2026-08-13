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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;


public class CaerulaHeartItem extends CollectibleItem.CustomCollectibleItem {
	public CaerulaHeartItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), false, 25, CollectibleTiers.CURSED, 0, 1, 0,
				CollectibleActivation.forTier(CollectibleTiers.CURSED));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
		String hoverText = null;
        if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            list.add(Component.translatable("item.caerula_arbor.cursed.used"));
        }
    }

	@Override
	public void onUse(ItemStack stack, Level level, Player player) {
		EntityUtils.getLight(player);
		PlayerVariable capability = ModCapabilities.getPlayerVariables(player);
		double setval0 = capability.player_light - 50;
		capability.player_light = setval0;
		capability.syncPlayerVariables(player);
		if (ModCapabilities.getPlayerVariables(player).player_light < 0) {
			PlayerVariable capability2 = ModCapabilities.getPlayerVariables(player);
			double setval1 = 0;
			capability2.player_light = setval1;
			capability2.syncPlayerVariables(player);
		}
		PlayerVariable capability3 = ModCapabilities.getPlayerVariables(player);
		double setval2 = Mth.nextInt(RandomSource.create(), 1, 4);
		capability3.disoclusion = setval2;
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
