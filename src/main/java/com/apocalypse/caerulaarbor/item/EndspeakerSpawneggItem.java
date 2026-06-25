
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.List;

public class EndspeakerSpawneggItem extends ForgeSpawnEggItem {
	public EndspeakerSpawneggItem() {
		super(CaerulaArborModEntities.ENDSPEAKER_3, -1, -1, new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.endspeaker_spawnegg.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		ItemStack item = entity.getItemInHand(hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (entity != null) {
            double phase = 0;
            double tgtX = 0;
            double tgtY = 0;
            double tgtZ = 0;
            phase = item.getOrCreateTag().getDouble("phase");
            if (entity.isShiftKeyDown()) {
                item.getOrCreateTag().putDouble("phase", ((phase + 1) % 4));
                if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal(((Component.translatable("item.caerula_arbor.endspeaker_spawnegg.use").getString()).replace("{p}", "" + Math.round(item.getOrCreateTag().getDouble("phase") + 1)))), true);
            } else {
                if (!((((LevelAccessor) world).getFluidState(BlockPos.containing(x, y, z)).createLegacyBlock()).getBlock() == Blocks.AIR)) {
                    tgtX = x + 0.5;
                    tgtY = y + 0.5;
                    tgtZ = z + 0.5;
                    if (phase == 3) {
                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                            Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_3.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    } else if (phase == 2) {
                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                            Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_2.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    } else if (phase == 1) {
                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                            Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_1.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    } else {
                        if ((LevelAccessor) world instanceof ServerLevel _level) {
                            Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_0.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                            if (entityToSpawn != null) {
                                entityToSpawn.setYRot(((LevelAccessor) world).getRandom().nextFloat() * 360F);
                            }
                        }
                    }
                    item.shrink(1);
                }
            }
        }
        return InteractionResultHolder.pass(item);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if(player != null && player.isShiftKeyDown()) return super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        ItemStack itemstack = context.getItemInHand();
        if (direction == null)
            return InteractionResult.PASS;
        double tgtX = 0;
        double tgtY = 0;
        double tgtZ = 0;
        double phase = 0;
        tgtX = x + direction.getStepX() + 0.5;
        tgtY = y + direction.getStepY() + 0.5;
        tgtZ = z + direction.getStepZ() + 0.5;
        phase = itemstack.getOrCreateTag().getDouble("phase");
        if (phase == 3) {
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_3.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        } else if (phase == 2) {
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_2.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        } else if (phase == 1) {
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_1.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        } else {
            if (world instanceof ServerLevel _level) {
                Entity entityToSpawn = CaerulaArborModEntities.ENDSPEAKER_0.get().spawn(_level, BlockPos.containing(tgtX, tgtY, tgtZ), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
                }
            }
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
