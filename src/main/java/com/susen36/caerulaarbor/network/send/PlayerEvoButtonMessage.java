package com.susen36.caerulaarbor.network.send;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.menu.PlayerEvoMenu;
import com.susen36.caerulaarbor.util.NodeUtils;
import com.susen36.caerulaarbor.util.PlayerStateUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;

public class PlayerEvoButtonMessage implements CustomPacketPayload {
	public static final Type<PlayerEvoButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "player_evo_button"));
	public static final StreamCodec<FriendlyByteBuf, PlayerEvoButtonMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> PlayerEvoButtonMessage.buffer(msg, buf),
			PlayerEvoButtonMessage::new
	);

	private final int buttonID, x, y, z;

	public PlayerEvoButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public PlayerEvoButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(PlayerEvoButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handle(PlayerEvoButtonMessage message, IPayloadContext context) {
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
		HashMap<String, Object> guistate = PlayerEvoMenu.guistate;
		// 安全措施：防止任意区块生成
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

            entity.getPersistentData().putString("showcasingEvoNode", "nexus.no_rejection");
        }
		if (buttonID == 1) {

            String title;
            double quantity;
            double quality;
            double quantity_cost = 0;
            double quality_cost = 0;
            double add_def;
            quantity = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).reserve_quantity;
            quality = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).reserve_quality;
            title = entity.getPersistentData().getString("showcasingEvoNode");
            if (!PlayerStateUtils.isNexusNoRejectionSelected(entity) && (title).equals("nexus.no_rejection") && quality >= 1) {
                {
                    boolean setval = true;
                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.PEVO_NEXUS_no_rejection = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                quality_cost = 1;
            } else if (!PlayerStateUtils.isNexusRegSanitySelected(entity) && (title).equals("nexus.reg_sanity") && quantity >= 1) {
                {
                    boolean setval = true;
                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.PEVO_NEXUS_reg_sanity = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                quantity_cost = 2;
            } else if (!PlayerStateUtils.isNexusRegLightsSelected(entity) && (title).equals("nexus.reg_lights") && quality >= 2) {
                {
                    boolean setval = true;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.PEVO_NEXUS_reg_lights = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                quality_cost = 2;
            } else if (!PlayerStateUtils.isNexusPercDamageSelected(entity) && (title).equals("nexus.perc_damage") && quality >= 3) {
                {
                    boolean setval = true;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.PEVO_NEXUS_perc_damage = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                quality_cost = 3;
            } else if (!PlayerStateUtils.isNexusExpoShieldSelected(entity) && (title).equals("nexus.expo_shield") && quality >= 4) {
                {
                    boolean setval = true;
                    entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.PEVO_NEXUS_expo_shield = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                quality_cost = 4;
            } else if (title.contains("node.add_def")) {
                add_def = NodeUtils.getNodeAddDef(entity);
                for (int index0 = 0; index0 < 4; index0++) {
                    if (add_def < index0 + 1) {
                        if (quantity >= index0 + 1) {
                            {
                                double setval = index0 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_add_def = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            quantity_cost = index0 + 1;
                            break;
                        }
                    }
                }
            } else if (title.contains("node.add_resis")) {
                add_def = NodeUtils.getNodeAddResis(entity);
                for (int index1 = 0; index1 < 4; index1++) {
                    if (add_def < index1 + 1) {
                        if (quantity >= index1 + 1) {
                            {
                                double setval = index1 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_add_resis = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            quantity_cost = index1 + 1;
                            break;
                        }
                    }
                }
            } else if (title.contains("node.add_speed")) {
                add_def = NodeUtils.getNodeAddSpeed(entity);
                for (int index2 = 0; index2 < 4; index2++) {
                    if (add_def < index2 + 1) {
                        if (quantity >= index2 + 1) {
                            {
                                double setval = index2 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_add_speed = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            quantity_cost = index2 + 1;
                            break;
                        }
                    }
                }
            } else if (title.contains("node.add_sanity")) {
                add_def = NodeUtils.getNodeAddSanity(entity);
                for (int index3 = 0; index3 < 4; index3++) {
                    if (add_def < index3 + 1) {
                        if (quantity >= index3 + 1) {
                            {
                                double setval = index3 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_add_sanity = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            quantity_cost = index3 + 1;
                            break;
                        }
                    }
                }
            } else if (title.contains("node.add_damage")) {
                add_def = NodeUtils.getNodeAddDamage(entity);
                for (int index4 = 0; index4 < 4; index4++) {
                    if (add_def < index4 + 1) {
                        if (index4 + 1 <= 2) {
                            quantity_cost = 2;
                        } else {
                            quantity_cost = 4;
                        }
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index4 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_add_damage = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.less_damage")) {
                add_def = (entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_less_damage;
                for (int index5 = 0; index5 < 4; index5++) {
                    if (add_def < index5 + 1) {
                        if (index5 + 1 <= 2) {
                            quantity_cost = 2;
                        } else {
                            quantity_cost = 4;
                        }
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index5 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_less_damage = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.living_barrier")) {
                add_def = NodeUtils.getNodeLivingBarrier(entity);
                for (int index6 = 0; index6 < 4; index6++) {
                    if (add_def < index6 + 1) {
                        quantity_cost = index6 + 2;
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index6 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_living_barrier = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.add_miss")) {
                add_def = NodeUtils.getNodeAddMiss(entity);
                for (int index7 = 0; index7 < 4; index7++) {
                    if (add_def < index7 + 1) {
                        quantity_cost = index7 + 2;
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index7 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_add_miss = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.real_damage")) {
                add_def = NodeUtils.getNodeRealDamage(entity);
                for (int index8 = 0; index8 < 4; index8++) {
                    if (add_def < index8 + 1) {
                        quantity_cost = 2 * index8 + 2;
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index8 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_real_damage = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.heal_damage")) {
                add_def = NodeUtils.getNodeHealDamage(entity);
                for (int index9 = 0; index9 < 4; index9++) {
                    if (add_def < index9 + 1) {
                        quantity_cost = 2 * index9 + 2;
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index9 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_heal_damage = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.worse_break")) {
                add_def = NodeUtils.getNodeWorseBreak(entity);
                for (int index10 = 0; index10 < 4; index10++) {
                    if (add_def < index10 + 1) {
                        quantity_cost = 2 * index10 + 2;
                        if (quantity >= quantity_cost) {
                            double setval = index10 + 1;
                            entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                capability.PEVO_NODE_worse_break = setval;
                                capability.syncPlayerVariables(entity);
                            });
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.eunectes")) {
                add_def = NodeUtils.getNodeEunectes(entity);
                for (int index11 = 0; index11 < 4; index11++) {
                    if (add_def < index11 + 1) {
                        quantity_cost = 2 * index11 + 3;
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index11 + 1;
                                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_eunectes = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            } else if (title.contains("node.less_armor")) {
                double result;
                result = (((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).PEVO_NODE_less_armor;
                add_def = result;
                for (int index12 = 0; index12 < 4; index12++) {
                    if (add_def < index12 + 1) {
                        quantity_cost = 2 * index12 + 3;
                        if (quantity >= quantity_cost) {
                            {
                                double setval = index12 + 1;
                                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                                    capability.PEVO_NODE_less_armor = setval;
                                    capability.syncPlayerVariables(entity);
                                });
                            }
                            break;
                        }
                        quantity_cost = 0;
                    }
                }
            }
            if (quantity_cost > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.TAP.get(), SoundSource.PLAYERS, 2, 1);
                }
                {
                    double setval = quantity - quantity_cost;
                    ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                        capability.reserve_quantity = setval;
                        capability.syncPlayerVariables(entity);
                    });
                }
                if ((Entity) entity instanceof ServerPlayer ent) {
                    BlockPos bpos = BlockPos.containing(x, y, z);
                    ent.openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.literal("PlayerEvo");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                            return new PlayerEvoMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(bpos));
                        }
                    }, buf -> buf.writeBlockPos(bpos));
                }
            }
            if (quality_cost > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.ALERT.get(), SoundSource.PLAYERS, 2, 1);
                }
                double setval = quality - quality_cost;
                entity.getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.reserve_quality = setval;
                    capability.syncPlayerVariables(entity);
                });
                if (entity instanceof ServerPlayer ent) {
                    BlockPos bpos = BlockPos.containing(x, y, z);
                    ent.openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.literal("PlayerEvo");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                            return new PlayerEvoMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(bpos));
                        }
                    }, buf -> buf.writeBlockPos(bpos));
                }
            }
        }
		if (buttonID == 2) {

            entity.getPersistentData().putString("showcasingEvoNode", "nexus.reg_sanity");
        }
		if (buttonID == 3) {

			PlayerStateUtils.setEvoNode(entity, "node.add_def.1");
		}
		if (buttonID == 4) {

			PlayerStateUtils.setEvoNode(entity, "node.add_def.2");
		}
		if (buttonID == 5) {

			PlayerStateUtils.setEvoNode(entity, "node.add_def.3");
		}
		if (buttonID == 6) {

			PlayerStateUtils.setEvoNode(entity, "node.add_def.4");
		}
		if (buttonID == 7) {

			PlayerStateUtils.setEvoNode(entity, "node.add_resis.1");
		}
		if (buttonID == 8) {

			PlayerStateUtils.setEvoNode(entity, "node.add_resis.2");
		}
		if (buttonID == 9) {

			PlayerStateUtils.setEvoNode(entity, "node.add_resis.3");
		}
		if (buttonID == 10) {

			PlayerStateUtils.setEvoNode(entity, "node.add_resis.4");
		}
		if (buttonID == 11) {

			PlayerStateUtils.setEvoNode(entity, "node.add_speed.1");
		}
		if (buttonID == 12) {

			PlayerStateUtils.setEvoNode(entity, "node.add_speed.2");
		}
		if (buttonID == 13) {

			PlayerStateUtils.setEvoNode(entity, "node.add_speed.3");
		}
		if (buttonID == 14) {

			PlayerStateUtils.setEvoNode(entity, "node.add_speed.4");
		}
		if (buttonID == 15) {

			PlayerStateUtils.setEvoNode(entity, "node.add_sanity.1");
		}
		if (buttonID == 16) {

			PlayerStateUtils.setEvoNode(entity, "node.add_sanity.2");
		}
		if (buttonID == 17) {

			PlayerStateUtils.setEvoNode(entity, "node.add_sanity.3");
		}
		if (buttonID == 18) {

			PlayerStateUtils.setEvoNode(entity, "node.add_sanity.4");
		}
		if (buttonID == 19) {

            if (entity != null) {
                entity.getPersistentData().putString("showcasingEvoNode", "nexus.reg_lights");
            }
        }
		if (buttonID == 20) {

			PlayerStateUtils.setEvoNode(entity, "node.add_damage.1");
		}
		if (buttonID == 21) {

			PlayerStateUtils.setEvoNode(entity, "node.add_damage.2");
		}
		if (buttonID == 22) {

			PlayerStateUtils.setEvoNode(entity, "node.add_damage.3");
		}
		if (buttonID == 23) {

			PlayerStateUtils.setEvoNode(entity, "node.add_damage.4");
		}
		if (buttonID == 24) {

			PlayerStateUtils.setEvoNode(entity, "node.less_damage.1");
		}
		if (buttonID == 25) {

			PlayerStateUtils.setEvoNode(entity, "node.less_damage.2");
		}
		if (buttonID == 26) {

			PlayerStateUtils.setEvoNode(entity, "node.less_damage.3");
		}
		if (buttonID == 27) {

			PlayerStateUtils.setEvoNode(entity, "node.less_damage.4");
		}
		if (buttonID == 28) {

			PlayerStateUtils.setEvoNode(entity, "node.living_barrier.1");
		}
		if (buttonID == 29) {

			PlayerStateUtils.setEvoNode(entity, "node.living_barrier.2");
		}
		if (buttonID == 30) {

			PlayerStateUtils.setEvoNode(entity, "node.living_barrier.3");
		}
		if (buttonID == 31) {

            entity.getPersistentData().putString("showcasingEvoNode", "node.living_barrier.4");
        }
		if (buttonID == 32) {

			PlayerStateUtils.setEvoNode(entity, "node.add_miss.1");
		}
		if (buttonID == 33) {

			PlayerStateUtils.setEvoNode(entity, "node.add_miss.2");
		}
		if (buttonID == 34) {

			PlayerStateUtils.setEvoNode(entity, "node.add_miss.3");
		}
		if (buttonID == 35) {

			PlayerStateUtils.setEvoNode(entity, "node.add_miss.4");
		}
		if (buttonID == 36) {

            entity.getPersistentData().putString("showcasingEvoNode", "nexus.perc_damage");
        }
		if (buttonID == 37) {

			PlayerStateUtils.setEvoNode(entity, "node.real_damage.1");
		}
		if (buttonID == 38) {

			PlayerStateUtils.setEvoNode(entity, "node.real_damage.2");
		}
		if (buttonID == 39) {

			PlayerStateUtils.setEvoNode(entity, "node.real_damage.3");
		}
		if (buttonID == 40) {

			PlayerStateUtils.setEvoNode(entity, "node.real_damage.4");
		}
		if (buttonID == 41) {

			PlayerStateUtils.setEvoNode(entity, "node.heal_damage.1");
		}
		if (buttonID == 42) {

			PlayerStateUtils.setEvoNode(entity, "node.heal_damage.2");
		}
		if (buttonID == 43) {

			PlayerStateUtils.setEvoNode(entity, "node.heal_damage.3");
		}
		if (buttonID == 44) {

			PlayerStateUtils.setEvoNode(entity, "node.heal_damage.4");
		}
		if (buttonID == 45) {

			PlayerStateUtils.setEvoNode(entity, "node.worse_break.1");
		}
		if (buttonID == 46) {

			PlayerStateUtils.setEvoNode(entity, "node.worse_break.2");
		}
		if (buttonID == 47) {

			PlayerStateUtils.setEvoNode(entity, "node.worse_break.3");
		}
		if (buttonID == 48) {

			PlayerStateUtils.setEvoNode(entity, "node.worse_break.4");
		}
		if (buttonID == 49) {

            entity.getPersistentData().putString("showcasingEvoNode", "nexus.expo_shield");
        }
		if (buttonID == 50) {

			PlayerStateUtils.setEvoNode(entity, "node.eunectes.1");
		}
		if (buttonID == 51) {

			PlayerStateUtils.setEvoNode(entity, "node.eunectes.2");
		}
		if (buttonID == 52) {

			PlayerStateUtils.setEvoNode(entity, "node.eunectes.3");
		}
		if (buttonID == 53) {

			PlayerStateUtils.setEvoNode(entity, "node.eunectes.4");
		}
		if (buttonID == 54) {

			PlayerStateUtils.setEvoNode(entity, "node.less_armor.1");
		}
		if (buttonID == 55) {

			PlayerStateUtils.setEvoNode(entity, "node.less_armor.2");
		}
		if (buttonID == 56) {

			PlayerStateUtils.setEvoNode(entity, "node.less_armor.3");
		}
		if (buttonID == 57) {

			PlayerStateUtils.setEvoNode(entity, "node.less_armor.4");
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

