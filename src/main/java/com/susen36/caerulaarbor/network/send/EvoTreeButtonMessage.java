package com.susen36.caerulaarbor.network.send;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.menu.InfoStrategyAllMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EvoTreeButtonMessage implements CustomPacketPayload {
	public static final Type<EvoTreeButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "evo_tree_button"));
	public static final StreamCodec<FriendlyByteBuf, EvoTreeButtonMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> EvoTreeButtonMessage.buffer(msg, buf),
			EvoTreeButtonMessage::new
	);

	private final int buttonID, x, y, z;

	public EvoTreeButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public EvoTreeButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(EvoTreeButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(EvoTreeButtonMessage message, IPayloadContext context) {
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
			InfoStrategyAllMenu.open(entity, BlockPos.containing(x, y, z));
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}