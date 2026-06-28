
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.util.StrategyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class IncandescentAnimaItem extends Item {
	public IncandescentAnimaItem() {
		super(new Item.Properties().stacksTo(4).fireResistant().rarity(Rarity.EPIC));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.incandescent_anima.description_0"));
		list.add(Component.translatable("item.caerula_arbor.incandescent_anima.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        boolean finished = false;
        if (entity != null) {
            boolean shouldBroadCast = false;
            double maxium_lvl = 0;
            double lvl1 = 0;
            double lvl2 = 0;
            double lvl3 = 0;
            double lvl4 = 0;
            double useTick = 0;
            double gameTick = 0;
            String stra = "";
            String info = "";
            String info_raw = "";
            useTick = CaerulaArborModVariables.MapVariables.get(world).incandescentAnimaUseTick;
            gameTick = ((LevelAccessor) world).getLevelData().getGameTime();
            if (useTick > 0 && gameTick - useTick < 24000) {
                if (!(new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity))) {
                    info = Component.translatable("item.caerula_arbor.incandescent_anima.cooldown").getString();
                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                        _player.displayClientMessage(Component.literal(info), true);
                    if ((LevelAccessor) world instanceof Level _level) {
                            _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.PLAYERS, 3, 1);
                    }
                    if ((LevelAccessor) world instanceof ServerLevel _level)
                        _level.sendParticles(ParticleTypes.ASH, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                    finished = true;
                }
            }
            if (!finished) {
                if (StrategyUtils.canEnableSilence(world)) {
                    lvl1 = CaerulaArborModVariables.MapVariables.get(world).strategy_silence;
                    if (lvl1 > 0) {
                        maxium_lvl = lvl1 - 1;
                        CaerulaArborModVariables.MapVariables.get(world).strategy_silence = maxium_lvl;
                        CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                        CaerulaArborModVariables.MapVariables.get(world).evo_point_silence = 0;
                        CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                        info = Component.translatable("item.caerula_arbor.incandescent_anima.use").getString();
                        stra = Component.translatable("caerula_arbor.strategy.silence").getString();
                        info = info.replace("{stra}", stra);
                        info = info.replace("{p}", "" + (int) maxium_lvl);
                        if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                            ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        shouldBroadCast = true;
                    } else {
                        CaerulaArborModVariables.MapVariables.get(world).silence_enabled = false;
                        CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                        info = Component.translatable("item.caerula_arbor.incandescent_anima.disable").getString();
                        if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                            ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        shouldBroadCast = true;
                    }
                } else {
                    lvl1 = CaerulaArborModVariables.MapVariables.get(world).strategy_grow;
                    lvl2 = CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting;
                    lvl3 = CaerulaArborModVariables.MapVariables.get(world).strategy_breed;
                    lvl4 = CaerulaArborModVariables.MapVariables.get(world).strategy_migration;
                    maxium_lvl = Math.max(Math.max(lvl1, lvl2), Math.max(lvl3, lvl4));
                    if (maxium_lvl == 0) {
                        info = Component.translatable("item.caerula_arbor.incandescent_anima.fail").getString();
                        if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                            _player.displayClientMessage(Component.literal(info), true);
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.PLAYERS, 3, 1);
                        }
                        if ((LevelAccessor) world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.ASH, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                        finished = true;
                    } else {
                        info_raw = Component.translatable("item.caerula_arbor.incandescent_anima.use").getString();
                        info_raw = info_raw.replace("{p}", "" + (int) (maxium_lvl - 1));
                        if (lvl1 == maxium_lvl) {
                            CaerulaArborModVariables.MapVariables.get(world).strategy_grow = maxium_lvl - 1;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            CaerulaArborModVariables.MapVariables.get(world).evo_point_grow = 0;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_sreategy_grow").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                        if (lvl2 == maxium_lvl) {
                            CaerulaArborModVariables.MapVariables.get(world).strategy_subsisting = maxium_lvl - 1;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            CaerulaArborModVariables.MapVariables.get(world).evo_point_subsisting = 0;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_subsisting").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                        if (lvl3 == maxium_lvl) {
                            CaerulaArborModVariables.MapVariables.get(world).strategy_breed = maxium_lvl - 1;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            CaerulaArborModVariables.MapVariables.get(world).evo_point_breed = 0;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_breed").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                        if (lvl4 == maxium_lvl) {
                            CaerulaArborModVariables.MapVariables.get(world).strategy_migration = maxium_lvl - 1;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            CaerulaArborModVariables.MapVariables.get(world).strategy_migration = 0;
                            CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_migration").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                    }
                }
                if (!finished) {
                    if (shouldBroadCast) {
                        CaerulaArborModVariables.MapVariables.get(world).incandescentAnimaUseTick = gameTick;
                        CaerulaArborModVariables.MapVariables.get(world).syncData(world);
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.end_portal.spawn")), SoundSource.PLAYERS, 3, 1);
                        }
                        if ((LevelAccessor) world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.END_ROD, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                        itemstack.shrink(1);
                    }
                }
            }
        }
        return retval;
	}
}
