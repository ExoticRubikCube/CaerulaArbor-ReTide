package com.susen36.caerulaarbor.network.send;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.Relic;
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
                if (Relic.KING_CROWN.gained(entity)) {
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
                            Relic.KING_CROWN.set(capability, setval ? 1 : 0);
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
                    if (Relic.KING_CROWN.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.KING_CROWN.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 2) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.KING_SPEAR.gained(entity)) {
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
                            Relic.KING_SPEAR.set(capability, setval ? 1 : 0);
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
                    if (Relic.KING_SPEAR.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.KING_SPEAR.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 3) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.KING_ARMOR.gained(entity)) {
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
                            Relic.KING_ARMOR.set(capability, setval ? 1 : 0);
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
                    if (Relic.KING_ARMOR.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.KING_ARMOR.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 4) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.KING_EXTENSION.gained(entity)) {
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
                            Relic.KING_EXTENSION.set(capability, setval ? 1 : 0);
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
                    if (Relic.KING_EXTENSION.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.KING_EXTENSION.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 5) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.KING_CRYSTAL.gained(entity)) {
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
                            Relic.KING_CRYSTAL.set(capability, setval ? 1 : 0);
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
                    if (Relic.KING_CRYSTAL.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.KING_CRYSTAL.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 6) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.SARKAZ_KING_ARTIFACT.gained(entity)) {
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
                            Relic.SARKAZ_KING_ARTIFACT.set(capability, setval ? 1 : 0);
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
                    if (Relic.SARKAZ_KING_ARTIFACT.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.SARKAZ_KING_ARTIFACT.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 7) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.SARKAZ_KING_FLAG.gained(entity)) {
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
                            Relic.SARKAZ_KING_FLAG.set(capability, setval ? 1 : 0);
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
                    if (Relic.SARKAZ_KING_FLAG.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.SARKAZ_KING_FLAG.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 8) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.SARKAZ_KING_BED.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.SARKAZ_KING_BED.set(capability, setval ? 1 : 0);
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
                    if (Relic.SARKAZ_KING_BED.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.SARKAZ_KING_BED.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 10) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_THORNS.gained(entity)) {
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
                            Relic.HAND_THORNS.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_THORNS.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_THORNS.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 11) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_STRANGLE.gained(entity)) {
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
                            Relic.HAND_STRANGLE.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_STRANGLE.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_STRANGLE.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 12) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_FERTILITY.gained(entity)) {
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
                            Relic.HAND_FERTILITY.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_FERTILITY.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_FERTILITY.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 13) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_OF_PULVERIZATION.gained(entity)) {
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
                            Relic.HAND_OF_PULVERIZATION.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_OF_PULVERIZATION.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_OF_PULVERIZATION.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 14) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_SWIPE.gained(entity)) {
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
                            Relic.HAND_SWIPE.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_SWIPE.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_SWIPE.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 15) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_ENGRAVE.get(entity) >= 0) {
                    {
                        double setval = -1;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.HAND_ENGRAVE.set(capability, (int) setval);
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
                    if (Relic.HAND_ENGRAVE.get(entity) >= 0) {
                        {
                            double setval = -1;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_ENGRAVE.set(capability, (int) setval);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 16) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_FIREWORK.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.HAND_FIREWORK.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_FIREWORK.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_FIREWORK.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 17) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.TREATY.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.TREATY.set(capability, setval ? 1 : 0);
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
                    if (Relic.TREATY.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.TREATY.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 18) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.SURVIVOR_CONTRACT.get(entity) >= 0) {
                    {
                        double setval = -1;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.SURVIVOR_CONTRACT.set(capability, (int) setval);
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
                    if (Relic.SURVIVOR_CONTRACT.get(entity) >= 0) {
                        {
                            double setval = -1;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.SURVIVOR_CONTRACT.set(capability, (int) setval);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 19) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.CURSED_EMELIGHT.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.CURSED_EMELIGHT.set(capability, setval ? 1 : 0);
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
                    if (Relic.CURSED_EMELIGHT.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.CURSED_EMELIGHT.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 20) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.CURSED_GLOWBODY.gained(entity)) {
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
                            Relic.CURSED_GLOWBODY.set(capability, setval ? 1 : 0);
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
                    if (Relic.CURSED_GLOWBODY.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.CURSED_GLOWBODY.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 21) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.CURSED_RESEARCH.gained(entity)) {
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
                            Relic.CURSED_RESEARCH.set(capability, setval ? 1 : 0);
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
                    if (Relic.CURSED_RESEARCH.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.CURSED_RESEARCH.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 35) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_SWORD.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.HAND_SWORD.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_SWORD.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_SWORD.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 36) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.LEGEND_CHITIN.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.LEGEND_CHITIN.set(capability, setval ? 1 : 0);
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
                    if (Relic.LEGEND_CHITIN.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.LEGEND_CHITIN.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 37) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HAND_SPEED.gained(entity)) {
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
                            Relic.HAND_SPEED.set(capability, setval ? 1 : 0);
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
                    if (Relic.HAND_SPEED.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HAND_SPEED.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 38) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.HEMOST.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.HEMOST.set(capability, setval ? 1 : 0);
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
                    if (Relic.HEMOST.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.HEMOST.set(capability, setval ? 1 : 0);
                                capability.syncPlayerVariables(entity);
                        }
                    }
                }
            }
        }
		if (buttonID == 39) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player playerHasItem && playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if (Relic.YEARNING.gained(entity)) {
                    {
                        boolean setval = false;
                        PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                            Relic.YEARNING.set(capability, setval ? 1 : 0);
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
                    if (Relic.YEARNING.gained(entity)) {
                        {
                            boolean setval = false;
                            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
                                Relic.YEARNING.set(capability, setval ? 1 : 0);
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