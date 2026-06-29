
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CannedLavaItem extends Item {
	public CannedLavaItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (entity != null) {
			entity.hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.LAVA)), 27);
			entity.setSecondsOnFire(12);
			if (!(entity instanceof Player)) {
				resultStack.shrink(1);
				ItemStack emptyCan = new ItemStack(CAItems.EMPTY_CAN.get());
				if (resultStack.isEmpty()) {
					return emptyCan;
				}
			} else if (entity instanceof Player player && !player.getAbilities().instabuild) {
				resultStack.shrink(1);
				ItemStack emptyCan = new ItemStack(CAItems.EMPTY_CAN.get());
				if (resultStack.isEmpty()) {
					return emptyCan;
				}
				if (!player.getInventory().add(emptyCan)) {
					player.drop(emptyCan, false);
				}
			}
		}
		return resultStack;
	}
}
