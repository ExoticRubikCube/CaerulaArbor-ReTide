
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler;
import com.susen36.caerulaarbor.capability.map.MapVariablesHandler.StrategyType;
import com.susen36.caerulaarbor.util.StrategyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
	public int getUseDuration(ItemStack itemstack, LivingEntity user) {
		return 40;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.incandescent_anima.description_0"));
		list.add(Component.translatable("item.caerula_arbor.incandescent_anima.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
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
            double maxium_lvl;
            double lvl1;
            double lvl2;
            double lvl3;
            double lvl4;
            double useTick;
            double gameTick;
            String stra;
            String info;
            String info_raw;
            useTick = MapVariables.get(world).incandescentAnimaUseTick;
            gameTick = ((LevelAccessor) world).getLevelData().getGameTime();
            if (useTick > 0 && gameTick - useTick < 24000) {
                if (!(new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode((Entity) entity))) {
                    info = Component.translatable("item.caerula_arbor.incandescent_anima.cooldown").getString();
                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                        player.displayClientMessage(Component.literal(info), true);
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 3, 1);
                    }
                    if ((LevelAccessor) world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.ASH, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                    finished = true;
                }
            }
            if (!finished) {
                if (StrategyUtils.canEnableSilence(world)) {
                    lvl1 = MapVariables.get(world).strategy_silence;
                    if (lvl1 > 0) {
                        maxium_lvl = lvl1 - 1;
                        MapVariablesHandler.setStrategyLevel(world, StrategyType.SILENCE, maxium_lvl);
                        MapVariablesHandler.setEvoPoint(world, StrategyType.SILENCE, 0);
                        info = Component.translatable("item.caerula_arbor.incandescent_anima.use").getString();
                        stra = Component.translatable("caerula_arbor.strategy.silence").getString();
                        info = info.replace("{stra}", stra);
                        info = info.replace("{p}", "" + (int) maxium_lvl);
                        if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                            ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        shouldBroadCast = true;
                    } else {
                        MapVariablesHandler.setSilenceEnabled(world, false);
                        info = Component.translatable("item.caerula_arbor.incandescent_anima.disable").getString();
                        if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                            ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        shouldBroadCast = true;
                    }
                } else {
                    lvl1 = MapVariables.get(world).strategy_grow;
                    lvl2 = MapVariables.get(world).strategy_subsisting;
                    lvl3 = MapVariables.get(world).strategy_breed;
                    lvl4 = MapVariables.get(world).strategy_migration;
                    maxium_lvl = Math.max(Math.max(lvl1, lvl2), Math.max(lvl3, lvl4));
                    if (maxium_lvl == 0) {
                        info = Component.translatable("item.caerula_arbor.incandescent_anima.fail").getString();
                        if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                            player.displayClientMessage(Component.literal(info), true);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 3, 1);
                        }
                        if ((LevelAccessor) world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.ASH, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                        finished = true;
                    } else {
                        info_raw = Component.translatable("item.caerula_arbor.incandescent_anima.use").getString();
                        info_raw = info_raw.replace("{p}", "" + (int) (maxium_lvl - 1));
                        if (lvl1 == maxium_lvl) {
                            MapVariablesHandler.setStrategyLevel(world, StrategyType.GROW, maxium_lvl - 1);
                            MapVariablesHandler.setEvoPoint(world, StrategyType.GROW, 0);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_sreategy_grow").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                        if (lvl2 == maxium_lvl) {
                            MapVariablesHandler.setStrategyLevel(world, StrategyType.SUBSISTING, maxium_lvl - 1);
                            MapVariablesHandler.setEvoPoint(world, StrategyType.SUBSISTING, 0);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_subsisting").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                        if (lvl3 == maxium_lvl) {
                            MapVariablesHandler.setStrategyLevel(world, StrategyType.BREED, maxium_lvl - 1);
                            MapVariablesHandler.setEvoPoint(world, StrategyType.BREED, 0);
                            stra = Component.translatable("gui.caerula_arbor.evo_tree.label_strategy_breed").getString();
                            info = info_raw.replace("{stra}", stra);
                            shouldBroadCast = true;
                            if (!((LevelAccessor) world).isClientSide() && ((LevelAccessor) world).getServer() != null)
                                ((LevelAccessor) world).getServer().getPlayerList().broadcastSystemMessage(Component.literal(info), false);
                        }
                        if (lvl4 == maxium_lvl) {
                            MapVariablesHandler.setStrategyLevel(world, StrategyType.MIGRATION, maxium_lvl - 1);
                            MapVariablesHandler.setStrategyLevel(world, StrategyType.MIGRATION, 0);
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
                        MapVariablesHandler.setIncandescentUseTick(world, gameTick);
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 3, 1);
                        }
                        if ((LevelAccessor) world instanceof ServerLevel level)
                            level.sendParticles(ParticleTypes.END_ROD, x, (y + 0.75), z, 32, 0.75, 0.75, 0.75, 0.1);
                        itemstack.shrink(1);
                    }
                }
            }
        }
        return retval;
	}
}