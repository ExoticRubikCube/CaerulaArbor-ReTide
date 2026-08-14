package com.susen36.caerulaarbor.network.send;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.network.BabelNetwork;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CACollectible;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.menu.CaerulaRecordGUIMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;

public class RelicShowcaseButtonMessage implements CustomPacketPayload {
	public static final Type<RelicShowcaseButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_showcase_button"));
	public static final StreamCodec<FriendlyByteBuf, RelicShowcaseButtonMessage> STREAM_CODEC = StreamCodec.of(
			RelicShowcaseButtonMessage::buffer,
			RelicShowcaseButtonMessage::new
	);

	// 消耗 1 枚贸易币兑换随机遗物，然后关闭该遗物（layer 置 0）—— setLayer 在兑换之后
	private static final Set<Item> REDEEM_STANDARD = Set.of(
			CACollectible.KING_CROWN.value(), CACollectible.KING_SPEAR.value(), CACollectible.KING_ARMOR.value(),
			CACollectible.KING_EXTENSION.value(), CACollectible.KING_CRYSTAL.value(), CACollectible.SARKAZ_KING_ARTIFACT.value(),
			CACollectible.SARKAZ_KING_FLAG.value(), CACollectible.HAND_THORNS.value(), CACollectible.HAND_STRANGLE.value(),
			CACollectible.HAND_FERTILITY.value(), CACollectible.HAND_OF_PULVERIZATION.value(), CACollectible.HAND_SWIPE.value(),
			CACollectible.CURSED_GLOWBODY.value(), CACollectible.CURSED_RESEARCH.value(), CACollectible.HAND_SPEED.value());

	// 同 REDEEM_STANDARD，但 setLayer 在兑换之前执行
	private static final Set<Item> REDEEM_LAYER_FIRST = Set.of(
			CACollectible.SARKAZ_KING_BED.value(), CACollectible.HAND_FIREWORK.value(), CACollectible.TREATY.value(),
			CACollectible.CURSED_EMELIGHT.value(), CACollectible.HAND_SWORD.value(), CACollectible.LEGEND_CHITIN.value(),
			CACollectible.HEMOST.value(), CACollectible.YEARNING.value());

	// itemId 为 null 表示"返回"按钮（打开记录 GUI）
	private final ResourceLocation itemId;
	private final int x, y, z;

	public RelicShowcaseButtonMessage(FriendlyByteBuf buffer) {
		this.itemId = buffer.readBoolean() ? buffer.readResourceLocation() : null;
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public RelicShowcaseButtonMessage(Item item, int x, int y, int z) {
		this.itemId = item == null ? null : BuiltInRegistries.ITEM.getKey(item);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(FriendlyByteBuf buffer, RelicShowcaseButtonMessage message) {
		boolean hasItem = message.itemId != null;
		buffer.writeBoolean(hasItem);
		if (hasItem) {
			buffer.writeResourceLocation(message.itemId);
		}
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(RelicShowcaseButtonMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player entity = context.player();
			if (entity != null) {
				Item item = message.itemId == null ? null : BuiltInRegistries.ITEM.get(message.itemId);
				handleButtonAction(entity, item, message.x, message.y, message.z);
			}
		});
	}

	public static void handleButtonAction(Player entity, Item item, int x, int y, int z) {
		Level world = entity.level();
		// 安全措施：防止任意区块生成
		if (world.hasChunkAt(new BlockPos(x, y, z))) {
			if (item == null) {
				openRecordGUI(entity, x, y, z);
			} else if (item == CACollectible.HAND_OF_ENGRAVE.value()) {
				redeemEngrave(entity);
			} else if (item == CACollectible.SURVIVOR_CONTRACT.value()) {
				redeemSurvivor(entity);
			} else if (REDEEM_LAYER_FIRST.contains(item)) {
				redeemRelic(entity, item, true);
			} else if (REDEEM_STANDARD.contains(item)) {
				redeemRelic(entity, item, false);
			}
		}
	}

	// 返回按钮：打开记录 GUI
	private static void openRecordGUI(Player entity, int x, int y, int z) {
		if (entity instanceof ServerPlayer ent) {
			BlockPos bpos = BlockPos.containing(x, y, z);
			ent.openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("CaerulaRecordGUI");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new CaerulaRecordGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(bpos));
				}
			}, buf -> buf.writeBlockPos(bpos));
		}
	}

	// 通用兑换：持有贸易币且已获得该遗物 → 消耗 1 币、给随机遗物、关闭遗物；创造模式下仅关闭遗物
	private static void redeemRelic(Player entity, Item relic, boolean layerFirst) {
		if (entity.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(relic)) {
				if (layerFirst) {
					setLayer(entity, relic, 0);
				}
				removeCoin(entity);
				giveRandomRelic(entity);
				if (!layerFirst) {
					setLayer(entity, relic, 0);
				}
			}
		} else if (isCreative(entity)) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(relic)) {
				setLayer(entity, relic, 0);
			}
		}
	}

	// HAND_ENGRAVE：以 layer >= 0 为条件，兑换后置为 -1
	private static void redeemEngrave(Player entity) {
		Item relic = CACollectible.HAND_OF_ENGRAVE.value();
		if (entity.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(relic) >= 0) {
				setLayer(entity, relic, -1);
				removeCoin(entity);
				giveRandomRelic(entity);
			}
		} else if (isCreative(entity)) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(relic) >= 0) {
				setLayer(entity, relic, -1);
			}
		}
	}

	// SURVIVOR_CONTRACT：以 layer >= 0 为条件，兑换后置为 -1，并同步收藏品
	private static void redeemSurvivor(Player entity) {
		Item relic = CACollectible.SURVIVOR_CONTRACT.value();
		if (entity.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(relic) >= 0) {
				entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(relic, -1);
				BabelNetwork.syncCollectibles(entity);
				removeCoin(entity);
				giveRandomRelic(entity);
			}
		} else if (isCreative(entity)) {
			if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(relic) >= 0) {
				entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(relic, -1);
				BabelNetwork.syncCollectibles(entity);
			}
		}
	}

	private static void removeCoin(Player entity) {
		ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
		entity.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, entity.inventoryMenu.getCraftSlots());
	}

	private static void giveRandomRelic(Player entity) {
		ItemStack togive = ItemStack.EMPTY;
		for (int index0 = 0; index0 < 64; index0++) {
			togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic")))
					.flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
			if (togive.getItem() != ItemStack.EMPTY.getItem()) {
				break;
			}
		}
		ItemStack setstack = togive.copy();
		setstack.setCount(1);
		ItemHandlerHelper.giveItemToPlayer(entity, setstack);
	}

	private static void setLayer(Player entity, Item relic, int value) {
		PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
		entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(relic, value);
		capability.syncPlayerVariables(entity);
	}

	private static boolean isCreative(Player entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
		} else if (entity.level().isClientSide() && entity instanceof Player player) {
			return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
					&& Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
		}
		return false;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}