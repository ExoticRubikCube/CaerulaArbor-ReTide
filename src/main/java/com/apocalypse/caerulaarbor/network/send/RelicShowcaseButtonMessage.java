package com.apocalypse.caerulaarbor.network.send;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.menu.CaerulaRecordGUIMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class RelicShowcaseButtonMessage {
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

	public static void handler(RelicShowcaseButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

            if ((Entity) entity instanceof ServerPlayer _ent) {
                BlockPos _bpos = BlockPos.containing(x, y, z);
                NetworkHooks.openScreen(_ent, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("CaerulaRecordGUI");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new CaerulaRecordGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
                    }
                }, _bpos);
            }
        }
		if (buttonID == 1) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CROWN) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_king_CROWN = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CROWN) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_king_CROWN = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 2) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_SPEAR) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_king_SPEAR = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_SPEAR) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_king_SPEAR = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 3) {

            double lives_left = 0;
            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_ARMOR) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_king_ARMOR = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_ARMOR) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_king_ARMOR = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 4) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_EXTENSION) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_king_EXTENSION = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_EXTENSION) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_king_EXTENSION = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 5) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CRYSTAL) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_king_CRYSTAL = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_king_CRYSTAL) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_king_CRYSTAL = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 6) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_ARTIFACT) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_archfi_ARTIFACT = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_ARTIFACT) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_archfi_ARTIFACT = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 7) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_FLAG) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_archfi_FLAG = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_FLAG) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_archfi_FLAG = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 8) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_BED) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_archfi_BED = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_archfi_BED) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_archfi_BED = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 10) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_THORNS) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_THORNS = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_THORNS) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_THORNS = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 11) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_STRANGLE) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_STRANGLE = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_STRANGLE) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_STRANGLE = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 12) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_FERTILITY) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_FERTILITY = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_FERTILITY) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_FERTILITY = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 13) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_BARREN) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_BARREN = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_BARREN) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_BARREN = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 14) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SWIPE) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_SWIPE = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SWIPE) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_SWIPE = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 15) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE >= 0) {
                    {
                        double _setval = -1;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_ENGRAVE = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_ENGRAVE >= 0) {
                        {
                            double _setval = -1;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_ENGRAVE = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 16) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_FIREWORK) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_FIREWORK = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_FIREWORK) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_FIREWORK = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 17) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_TREATY) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_TREATY = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_TREATY) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_TREATY = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 18) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR >= 0) {
                    {
                        double _setval = -1;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_SURVIVOR = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_SURVIVOR >= 0) {
                        {
                            double _setval = -1;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_SURVIVOR = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 19) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_EMELIGHT) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_cursed_EMELIGHT = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_EMELIGHT) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_cursed_EMELIGHT = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 20) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_GLOWBODY) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_cursed_GLOWBODY = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_GLOWBODY) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_cursed_GLOWBODY = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 21) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_RESEARCH) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_cursed_RESEARCH = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_cursed_RESEARCH) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_cursed_RESEARCH = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 35) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SWORD) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_SWORD = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SWORD) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_SWORD = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 36) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_legend_CHITIN) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_legend_CHITIN = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_legend_CHITIN) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_legend_CHITIN = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 37) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SPEED) {
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_hand_SPEED = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_hand_SPEED) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_hand_SPEED = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 38) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_HEMOST) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_HEMOST = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_HEMOST) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_HEMOST = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
		if (buttonID == 39) {

            ItemStack togive = ItemStack.EMPTY;
            if ((Entity) entity instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(new ItemStack(CAItems.COIN_OF_TRADE.get()))) {
                if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_YEARNING) {
                    {
                        boolean _setval = false;
                        ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                            capability.relic_YEARNING = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _stktoremove = new ItemStack(CAItems.COIN_OF_TRADE.get());
                        _player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
                    }
                    for (int index0 = 0; index0 < 64; index0++) {
                        togive = new ItemStack((ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(new ResourceLocation(CaerulaArborMod.MODID, "relic_generic"))).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))).copy();
                        if (!(togive.getItem() == ItemStack.EMPTY.getItem())) {
                            break;
                        }
                    }
                    if ((Entity) entity instanceof Player _player) {
                        ItemStack _setstack = togive.copy();
                        _setstack.setCount(1);
                        ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
                    }
                }
            } else {
                if (new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity)) {
                    if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).relic_YEARNING) {
                        {
                            boolean _setval = false;
                            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.relic_YEARNING = _setval;
                                capability.syncPlayerVariables(entity);
                            });
                        }
                    }
                }
            }
        }
	}
}

