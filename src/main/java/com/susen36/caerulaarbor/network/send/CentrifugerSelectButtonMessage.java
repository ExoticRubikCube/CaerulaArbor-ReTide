package com.susen36.caerulaarbor.network.send;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CentrifugerSelectButtonMessage implements CustomPacketPayload {
	public static final Type<CentrifugerSelectButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "centrifuger_select_button"));
	public static final StreamCodec<FriendlyByteBuf, CentrifugerSelectButtonMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> CentrifugerSelectButtonMessage.buffer(msg, buf),
			CentrifugerSelectButtonMessage::new
	);

	private final int buttonID, x, y, z;

	public CentrifugerSelectButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public CentrifugerSelectButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(CentrifugerSelectButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(CentrifugerSelectButtonMessage message, IPayloadContext context) {
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
			entity.getPersistentData().putString("centrifugerSelection", "skadi");
		}
		if (buttonID == 1) {
			entity.getPersistentData().putString("centrifugerSelection", "ulpians");
		}
		if (buttonID == 2) {
			entity.getPersistentData().putString("centrifugerSelection", "gladiia");
		}
		if (buttonID == 3) {

			String selection;
			ItemStack res = ItemStack.EMPTY;
			if (entity.getMainHandItem().getItem() == CAItems.HUNTER_GENE.get()) {
				selection = entity.getPersistentData().getString("centrifugerSelection");
				res = switch ((selection)) {
					case "skadi" -> new ItemStack(CAItems.HUNTER_GENE_SKADI.get()).copy();
					case "ulpians" -> new ItemStack(CAItems.HUNTER_GENE_ULPIANS.get()).copy();
					case "gladiia" -> new ItemStack(CAItems.HUNTER_GENE_GLADIIA.get()).copy();
					case "specter" -> new ItemStack(CAItems.HUNTER_GENE_SPECTER.get()).copy();
					default -> res;
				};
				if (!(res.getItem() == ItemStack.EMPTY.getItem())) {
					entity.getMainHandItem().shrink(1);
					if ((Entity) entity instanceof Player player)
						player.closeContainer();
					{
						int value = 1;
						BlockPos pos = BlockPos.containing(x, y, z);
						BlockState bs = world.getBlockState(pos);
						if (bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty integerProp && integerProp.getPossibleValues().contains(value))
							world.setBlock(pos, bs.setValue(integerProp, value), 3);
					}
					if ((LevelAccessor) world instanceof Level level) {
						level.playSound(null, BlockPos.containing(x, y, z), CASounds.NOTICE.get(), SoundSource.BLOCKS, 2, 1);
					}
					if ((LevelAccessor) world instanceof ServerLevel level) {
						ItemEntity entityToSpawn = new ItemEntity(level, ((double) x + 0.5), ((double) y + 1), ((double) z + 0.5), res);
						entityToSpawn.setPickUpDelay(10);
						entityToSpawn.setUnlimitedLifetime();
						level.addFreshEntity(entityToSpawn);
					}
				}
			}
		}
		if (buttonID == 4) {
			entity.getPersistentData().putString("centrifugerSelection", "specter");
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}