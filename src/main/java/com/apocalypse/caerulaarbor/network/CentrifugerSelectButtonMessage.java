package com.apocalypse.caerulaarbor.network;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.network.NetworkEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import com.apocalypse.caerulaarbor.world.inventory.CentrifugerSelectMenu;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.HashMap;

public class CentrifugerSelectButtonMessage {
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

	public static void handler(CentrifugerSelectButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = CentrifugerSelectMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

            if (entity != null) {
                entity.getPersistentData().putString("centrifugerSelection", "skadi");
            }
        }
		if (buttonID == 1) {

            if (entity != null) {
                entity.getPersistentData().putString("centrifugerSelection", "ulpians");
            }
        }
		if (buttonID == 2) {

            if (entity != null) {
                entity.getPersistentData().putString("centrifugerSelection", "gladiia");
            }
        }
		if (buttonID == 3) {

            if (entity != null) {
                String selection = "";
                ItemStack res = ItemStack.EMPTY;
                if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.HUNTER_GENE.get()) {
                    selection = entity.getPersistentData().getString("centrifugerSelection");
                    if ((selection).equals("skadi")) {
                        res = new ItemStack(CaerulaArborModItems.HUNTER_GENE_SKADI.get()).copy();
                    } else if ((selection).equals("ulpians")) {
                        res = new ItemStack(CaerulaArborModItems.HUNTER_GENE_ULPIANS.get()).copy();
                    } else if ((selection).equals("gladiia")) {
                        res = new ItemStack(CaerulaArborModItems.HUNTER_GENE_GLADIIA.get()).copy();
                    } else if ((selection).equals("specter")) {
                        res = new ItemStack(CaerulaArborModItems.HUNTER_GENE_SPECTER.get()).copy();
                    }
                    if (!(res.getItem() == ItemStack.EMPTY.getItem())) {
                        ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
                        if ((Entity) entity instanceof Player _player)
                            _player.closeContainer();
                        {
                            int _value = 1;
                            BlockPos _pos = BlockPos.containing(x, y, z);
                            BlockState _bs = ((LevelAccessor) world).getBlockState(_pos);
                            if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
                                ((LevelAccessor) world).setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
                        }
                        if ((LevelAccessor) world instanceof Level _level) {
                            if (!_level.isClientSide()) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "notice")), SoundSource.BLOCKS, 2, 1);
                            } else {
                                _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "notice")), SoundSource.BLOCKS, 2, 1, false);
                            }
                        }
                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                            ItemEntity entityToSpawn = new ItemEntity(_level, ((double) x + 0.5), ((double) y + 1), ((double) z + 0.5), res);
                            entityToSpawn.setPickUpDelay(10);
                            entityToSpawn.setUnlimitedLifetime();
                            _level.addFreshEntity(entityToSpawn);
                        }
                    }
                }
            }
        }
		if (buttonID == 4) {

            if (entity == null)
                return;
            entity.getPersistentData().putString("centrifugerSelection", "specter");
        }
	}
}
