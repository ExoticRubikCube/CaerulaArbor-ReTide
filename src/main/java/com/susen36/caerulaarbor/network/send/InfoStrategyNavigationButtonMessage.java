package com.susen36.caerulaarbor.network.send;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.menu.*;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class InfoStrategyNavigationButtonMessage implements CustomPacketPayload {
	public static final Type<InfoStrategyNavigationButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "info_strategy_navigation_button"));
	public static final StreamCodec<FriendlyByteBuf, InfoStrategyNavigationButtonMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> InfoStrategyNavigationButtonMessage.buffer(msg, buf),
			InfoStrategyNavigationButtonMessage::new
	);

	private final int buttonID, x, y, z;

	public InfoStrategyNavigationButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public InfoStrategyNavigationButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(InfoStrategyNavigationButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(InfoStrategyNavigationButtonMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player entity = context.player();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
        // 安全措施：防止任意区块生成
		if (!entity.level().hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
			openScreen(entity, x, y, z, "EvoTree", EvoTreeMenu.class);
		}
		if (buttonID == 1) {
			openScreen(entity, x, y, z, "InfoStrategyBreed", InfoStrategyBreedMenu.class);
		}
		if (buttonID == 2) {
			openScreen(entity, x, y, z, "InfoStrategyGrow", InfoStrategyGrowMenu.class);
		}
		if (buttonID == 3) {
			openScreen(entity, x, y, z, "InfoStrategyMigration", InfoStrategyMigrationMenu.class);
		}
		if (buttonID == 4) {
			openScreen(entity, x, y, z, "InfoStrategySubsis", InfoStrategySubsisMenu.class);
		}
	}

	private static void openScreen(Player entity, int x, int y, int z, String title, Class<? extends AbstractContainerMenu> menuClass) {
		if (!(entity instanceof ServerPlayer serverPlayer))
			return;
		BlockPos blockPos = BlockPos.containing(x, y, z);
		serverPlayer.openMenu(new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return Component.literal(title);
			}

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
				FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(blockPos);
				if (menuClass == EvoTreeMenu.class) {
					return new EvoTreeMenu(id, inventory, buffer);
				}
				if (menuClass == InfoStrategyBreedMenu.class) {
					return new InfoStrategyBreedMenu(id, inventory, buffer);
				}
				if (menuClass == InfoStrategyGrowMenu.class) {
					return new InfoStrategyGrowMenu(id, inventory, buffer);
				}
				if (menuClass == InfoStrategyMigrationMenu.class) {
					return new InfoStrategyMigrationMenu(id, inventory, buffer);
				}
				if (menuClass == InfoStrategySubsisMenu.class) {
					return new InfoStrategySubsisMenu(id, inventory, buffer);
				}
				throw new IllegalArgumentException("Unsupported menu class: " + menuClass.getName());
			}
		}, buf -> buf.writeBlockPos(blockPos));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}