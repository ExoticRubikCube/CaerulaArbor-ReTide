package com.apocalypse.caerulaarbor.network.send;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.menu.PlayerEvoMenu;
import com.apocalypse.caerulaarbor.menu.RelicShowcaseMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class CaerulaRecordGUIButtonMessage {
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

	public static void handler(CaerulaRecordGUIButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
            if (entity != null) {
                handleButtonAction(entity, buttonID, x, y, z);
            }
        });
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
        // 安全措施：防止任意区块生成
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

            {
                boolean setval = !(entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).show_stats;
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.show_stats = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
        }
		if (buttonID == 1) {

            {
                boolean setval = !(entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).kingShowPtc;
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.kingShowPtc = setval;
                    capability.syncPlayerVariables(entity);
                });
            }
        }
		if (buttonID == 2) {

            if ((Entity) entity instanceof ServerPlayer ent) {
                BlockPos bpos = BlockPos.containing(x, y, z);
                NetworkHooks.openScreen(ent, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("RelicShowcase");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new RelicShowcaseMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(bpos));
                    }
                }, bpos);
            }
        }
		if (buttonID == 3) {

            if ((Entity) entity instanceof ServerPlayer ent) {
                BlockPos bpos = BlockPos.containing(x, y, z);
                NetworkHooks.openScreen(ent, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("PlayerEvo");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new PlayerEvoMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(bpos));
                    }
                }, bpos);
            }
        }
	}
}

