package com.susen36.caerulaarbor.item;

import com.google.common.collect.Iterables;
import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.network.BabelNetwork;
import com.susen36.babel.util.HealthUtils;
import com.susen36.caerulaarbor.init.CACollectible;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

public abstract class WearableChestItem extends ArmorItem {
	public WearableChestItem(ArmorItem.Type type, Item.Properties properties) {
		super(Holder.direct(new ArmorMaterial(
			Map.of(
				ArmorItem.Type.HELMET, 2,
				ArmorItem.Type.CHESTPLATE, 11,
				ArmorItem.Type.LEGGINGS, 5,
				ArmorItem.Type.BOOTS, 2
			),
			16,
			SoundEvents.ARMOR_EQUIP_NETHERITE,
			() -> Ingredient.of(),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("caerula_arbor", "kingarmor"))),
			2.5f,
			0.2f
		)), type, properties);
	}

	public static class Chestplate extends WearableChestItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant());
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean isFoil(ItemStack itemstack) {
			return true;
		}

		@Override
		public boolean makesPiglinsNeutral(ItemStack itemstack, LivingEntity entity) {
			return true;
		}

		@Override
		public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
			super.inventoryTick(itemstack, world, entity, slot, selected);
			if (entity instanceof Player player && Iterables.contains(player.getArmorSlots(), itemstack)) {
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CACollectible.KING_ARMOR))
					return;

				BlockPos pos = BlockPos.containing(x, y, z);
				double storedLives = HealthUtils.getLifePoint(entity);

				if ((LevelAccessor) world instanceof Level level) {
					level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 2, 1);
				}
				if ((LevelAccessor) world instanceof ServerLevel level)
					level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 72, 1, 1, 1, 1);

				entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).markUsed(CACollectible.KING_ARMOR);
				if (!world.isClientSide() && entity instanceof Player player1)
					BabelNetwork.syncCollectibles(player1);

				if (world.isClientSide())
					Minecraft.getInstance().gameRenderer.displayItemActivation(itemstack);

				if (storedLives > 1) {
					HealthUtils.setLifePoint(entity, 1);
				}

				double shieldAfterLifeTransfer = HealthUtils.getShieldPoint(entity) + storedLives;
				HealthUtils.setShieldPoint(entity, (int) shieldAfterLifeTransfer);
				HealthUtils.setShieldPoint(entity, (int) (shieldAfterLifeTransfer + 3));
			}
		}
	}
}