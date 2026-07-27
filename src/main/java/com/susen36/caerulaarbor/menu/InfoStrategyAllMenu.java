
package com.susen36.caerulaarbor.menu;

import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMenus;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InfoStrategyAllMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
    private final Map<Integer, Slot> customSlots = new HashMap<>();

	public InfoStrategyAllMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(CAMenus.INFO_STRATEGY_ALL.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
        BlockPos pos;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}

	public static InteractionResult open(Entity entity, BlockPos blockPos) {
		if (entity == null)
			return InteractionResult.PASS;
		if (entity instanceof LivingEntity livingEntity && livingEntity.isHolding(CAItems.DICTATIONLESS_CHAPTER.get())) {
			return InteractionResult.PASS;
		}
		if (entity instanceof LivingEntity livingEntity && livingEntity.isHolding(CAItems.DICTATION_CHAPTER.get())) {
			return InteractionResult.PASS;
		}
		if (entity instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("InfoStrategyAll");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new InfoStrategyAllMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(blockPos));
				}
			}, buf -> buf.writeBlockPos(blockPos));
		}
		return InteractionResult.SUCCESS;
	}
}