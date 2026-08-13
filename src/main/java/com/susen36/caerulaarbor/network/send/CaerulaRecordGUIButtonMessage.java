package com.susen36.caerulaarbor.network.send;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.menu.RelicShowcaseMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CaerulaRecordGUIButtonMessage implements CustomPacketPayload {
	public static final Type<CaerulaRecordGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "caerula_record_gui_button"));
	public static final StreamCodec<FriendlyByteBuf, CaerulaRecordGUIButtonMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> CaerulaRecordGUIButtonMessage.buffer(msg, buf),
			CaerulaRecordGUIButtonMessage::new
	);

	private final int buttonID, x, y, z;

	public CaerulaRecordGUIButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public CaerulaRecordGUIButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(CaerulaRecordGUIButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(CaerulaRecordGUIButtonMessage message, IPayloadContext context) {
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
		Level world = entity.level();
		// 安全措施：防止任意区块生成
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
			boolean setval = !(ModCapabilities.getPlayerVariables(entity)).show_stats;
			PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
			capability.show_stats = setval;
			capability.syncPlayerVariables(entity);
		}
		if (buttonID == 1) {
			if ((Entity) entity instanceof ServerPlayer ent) {
				BlockPos bpos = BlockPos.containing(x, y, z);
				ent.openMenu(new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("RelicShowcase");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new RelicShowcaseMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(bpos));
					}
				}, buf -> buf.writeBlockPos(bpos));
			}
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}