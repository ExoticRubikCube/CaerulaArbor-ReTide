
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemHandlerHelper;

public class CannedBoiledWaterItem extends Item {
	public CannedBoiledWaterItem() {
		super(new Item.Properties().durability(1200).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getEnchantmentValue() {
		return -1;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
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
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                entity.hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "boil_water")))), 4);
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 8, 10);
        return resultStack;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        itemstack.setDamageValue(itemstack.getDamageValue() + 1);
        if (itemstack.getDamageValue() >= 1199) {
            if (entity instanceof Player player) {
                ItemStack stktoremove = new ItemStack(CAItems.CANNED_BOILED_WATER.get());
                player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
            }
            if (entity instanceof Player player) {
                ItemStack setstack = new ItemStack(CAItems.CANNED_WATER.get()).copy();
                setstack.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(player, setstack);
            }
        }
    }
}
