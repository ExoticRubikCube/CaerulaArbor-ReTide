package com.susen36.caerulaarbor.menu;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAMenus;
import com.susen36.caerulaarbor.util.NodeUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlayerEvoMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private final IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();

	public PlayerEvoMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(CAMenus.PLAYER_EVO.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(0);
		BlockPos pos;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
		}
        entity.getPersistentData().putString("showcasingEvoNode", "");
    }

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
        if (entity == null)
            return;
        boolean result = NodeUtils.isNodeSet1Terminate(entity) && NodeUtils.isNodeSet2Terminate(entity) && NodeUtils.isNodeSet3Terminate(entity) && NodeUtils.isNodeEunectesAtLeast(entity, 4) && NodeUtils.isNodeLessArmorAtLeast(entity, 4);
        if (result) {
			if ((Entity) entity instanceof ServerPlayer player) {
				Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "end_player_evo"));
				AdvancementProgress ap = player.getAdvancements().getOrStartProgress(adv);
				if (!ap.isDone()) {
					for (String criteria : ap.getRemainingCriteria())
						player.getAdvancements().award(adv, criteria);
				}
			}
		}
    }

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
