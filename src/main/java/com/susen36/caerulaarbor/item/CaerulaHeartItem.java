package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CARelics;
import com.susen36.caerulaarbor.item.relic.RelicItemBase;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;


public class CaerulaHeartItem extends RelicItemBase {
	public CaerulaHeartItem() {
		super(CARelics.CURSED_HEART, new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
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
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		EntityUtils.getLight(entity);
		return retval;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (!itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            {
                boolean setval = true;
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                CARelics.CURSED_HEART.get().set(capability, setval ? 1 : 0);
                capability.syncPlayerVariables(entity);
            }
            {
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                double setval = capability.player_light - 50;
                capability.player_light = setval;
                capability.syncPlayerVariables(entity);
            }
            if (ModCapabilities.getPlayerVariables(entity).player_light < 0) {
                {
                    double setval = 0;
                    PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                    capability.player_light = setval;
                    capability.syncPlayerVariables(entity);
                }
            }
            {
                double setval = Mth.nextInt(RandomSource.create(), 1, 4);
                PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                capability.disoclusion = setval;
                capability.syncPlayerVariables(entity);
            }
            if (entity instanceof ServerPlayer player) {
                AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "to_we_many"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
                if (!ap.isDone()) {
                    for (String criteria : ap.getRemainingCriteria())
                        player.getAdvancements().award(adv, criteria);
                }
            }
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.value(), SoundSource.NEUTRAL, 2, 1);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 99, 1, 1, 1, 1);
            if (world.isClientSide())
                Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
        }
    }
}