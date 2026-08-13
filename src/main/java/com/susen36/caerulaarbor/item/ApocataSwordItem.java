package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CADamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.Comparator;
import java.util.List;


public class ApocataSwordItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			114514,
			99f,
			65532f,
			99,
			() -> Ingredient.of(new ItemStack(Items.NETHERITE_INGOT))
	);

	public ApocataSwordItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(TIER, 3, 0f)));
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity, InteractionHand hand) {

        if (entity instanceof Player player && player.getMainHandItem() == itemstack) {
			double reach = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);

			HitResult hitResult = player.pick(reach, 0.0F, false);
			if (hitResult.getType() == HitResult.Type.MISS && !player.level().isClientSide()) {
				Vec3 center = player.position();
				List<Entity> entities = player.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32.0), target -> true).stream().sorted(Comparator.comparingDouble(target -> target.distanceToSqr(center))).toList();

				for (Entity target : entities) {
					if (target instanceof LightningBolt) {
						continue;
					}
					if (target.getDisplayName().getString().equals("item")) {
						continue;
					}
					if (target != entity) {
						target.hurt(CADamageTypes.source(player.level(), CADamageTypes.INV_KILLER), 114514.0F);
					}
				}
			}
		}
		return super.onEntitySwing(itemstack, entity, hand);
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(ItemStack itemstack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.apocata_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.apocata_sword.description_1"));
	}
}