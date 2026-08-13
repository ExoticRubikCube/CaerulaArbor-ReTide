package com.susen36.caerulaarbor.network.send;

import com.susen36.babel.collectible.Collectibles;
import com.susen36.babel.network.BabelNetwork;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RelicShowcaseButtonMessage implements CustomPacketPayload {
	public static final Type<RelicShowcaseButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_showcase_button"));
	public static final StreamCodec<FriendlyByteBuf, RelicShowcaseButtonMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> RelicShowcaseButtonMessage.buffer(msg, buf),
			RelicShowcaseButtonMessage::new
	);

	private final int buttonID, x, y, z;

	public RelicShowcaseButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public RelicShowcaseButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(RelicShowcaseButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(RelicShowcaseButtonMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player entity = context.player();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
            if (entity != null) {
                handleButtonAction(entity, buttonID, x, y, z);
            }
        });
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		// 安全措施：防止任意区块生成
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

            if ((Entity) entity instanceof ServerPlayer ent) {
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
		if (buttonID == 1) {
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_CROWN.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_CROWN.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_CROWN.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_CROWN.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 2) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_SPEAR.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_SPEAR.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_SPEAR.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_SPEAR.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 3) {
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_ARMOR.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_ARMOR.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_ARMOR.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_ARMOR.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 4) {
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_EXTENSION.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_EXTENSION.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_EXTENSION.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_EXTENSION.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 5) {
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_CRYSTAL.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_CRYSTAL.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.KING_CRYSTAL.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.KING_CRYSTAL.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 6) {
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.SARKAZ_KING_ARTIFACT.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SARKAZ_KING_ARTIFACT.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.SARKAZ_KING_ARTIFACT.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SARKAZ_KING_ARTIFACT.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 7) {
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.SARKAZ_KING_FLAG.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SARKAZ_KING_FLAG.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.SARKAZ_KING_FLAG.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SARKAZ_KING_FLAG.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 8) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.SARKAZ_KING_BED.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SARKAZ_KING_BED.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.SARKAZ_KING_BED.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SARKAZ_KING_BED.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 10) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_THORNS.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_THORNS.get(), 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_THORNS.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_THORNS.get(), 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 11) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_STRANGLE.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_STRANGLE.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_STRANGLE.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_STRANGLE.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 12) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_FERTILITY.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_FERTILITY.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_FERTILITY.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_FERTILITY.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 13) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_OF_PULVERIZATION.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_OF_PULVERIZATION.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_OF_PULVERIZATION.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_OF_PULVERIZATION.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 14) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_SWIPE.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_SWIPE.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_SWIPE.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_SWIPE.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 15) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CAItems.HAND_OF_ENGRAVE.get()) >= 0) {
                    {
                        double setval = -1;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_OF_ENGRAVE.get(), (int) setval);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CAItems.HAND_OF_ENGRAVE.get()) >= 0) {
                        {
                            double setval = -1;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_OF_ENGRAVE.get(), (int) setval);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 16) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_FIREWORK.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_FIREWORK.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_FIREWORK.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_FIREWORK.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 17) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.TREATY.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.TREATY.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.TREATY.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.TREATY.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 18) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CAItems.SURVIVOR_CONTRACT.get()) >= 0) {
                    {
                        entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SURVIVOR_CONTRACT.get(), -1);
                        BabelNetwork.syncCollectibles(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).getLayer(CAItems.SURVIVOR_CONTRACT.get()) >= 0) {
                        {
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.SURVIVOR_CONTRACT.get(), -1);
                            BabelNetwork.syncCollectibles(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 19) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.CURSED_EMELIGHT.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.CURSED_EMELIGHT.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.CURSED_EMELIGHT.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.CURSED_EMELIGHT.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 20) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.CURSED_GLOWBODY.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.CURSED_GLOWBODY.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.CURSED_GLOWBODY.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.CURSED_GLOWBODY.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 21) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.CURSED_RESEARCH.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.CURSED_RESEARCH.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.CURSED_RESEARCH.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.CURSED_RESEARCH.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 35) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_SWORD.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_SWORD.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_SWORD.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_SWORD.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 36) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.LEGEND_CHITIN.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.LEGEND_CHITIN.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.LEGEND_CHITIN.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.LEGEND_CHITIN.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 37) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_SPEED.get())) {
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_SPEED.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HAND_SPEED.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HAND_SPEED.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 38) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HEMOST.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HEMOST.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.HEMOST.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.HEMOST.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 39) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.YEARNING.get())) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.YEARNING.get(), setval ? 1 : 0);
                            capability.syncPlayerVariables(entity);
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        player.getInventory().clearOrCountMatchingItems(p -> stktoremove.getItem() == p.getItem(), 1, player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((BuiltInRegistries.ITEM.getTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "relic_generic"))).flatMap(tag -> tag.getRandomElement(RandomSource.create())).map(Holder::value).orElse(Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player player) {
                        ItemStack setstack = togive.copy();
                        setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(player, setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if (entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE.get()).isUsed(CAItems.YEARNING.get())) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                entity.getData(Collectibles.ATTACHMENT_COLLECTIBLE_LAYER.get()).setLayer(CAItems.YEARNING.get(), setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}