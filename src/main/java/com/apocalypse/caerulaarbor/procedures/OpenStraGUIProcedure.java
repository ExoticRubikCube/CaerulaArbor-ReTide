package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.world.inventory.InfoStrategyAllMenu;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.NetworkHooks;

public class OpenStraGUIProcedure {
	public static InteractionResult execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return InteractionResult.PASS;
		if ((entity instanceof LivingEntity _entity) ? _entity.isHolding(CaerulaArborModItems.DICTATIONLESS_CHAPTER.get()) : false) {
			return InteractionResult.PASS;
		} else if ((entity instanceof LivingEntity _entity) ? _entity.isHolding(CaerulaArborModItems.DICTATION_CHAPTER.get()) : false) {
			return InteractionResult.PASS;
		}
		if (entity instanceof ServerPlayer _ent) {
			BlockPos _bpos = BlockPos.containing(x, y, z);
			NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("InfoStrategyAll");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new InfoStrategyAllMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
				}
			}, _bpos);
		}
		return InteractionResult.SUCCESS;
	}
}
