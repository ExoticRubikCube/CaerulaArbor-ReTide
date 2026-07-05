
package com.apocalypse.caerulaarbor.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GoodsquidsServererItem extends Item {
	public GoodsquidsServererItem() {
		super(new Item.Properties().durability(800).rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.goodsquids_serverer.description_0"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (entity.tickCount % 20 == 10) {
            itemstack.setDamageValue(Math.min(100 + itemstack.getDamageValue(), 800));
            if (itemstack.getDamageValue() >= 799) {
                if (Math.random() < 0.2) {
                    if (((LevelAccessor) world).getLevelData().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                        if ((LevelAccessor) world instanceof Level level && !level.isClientSide())
                            level.explode(null, x, y, z, 12, Level.ExplosionInteraction.BLOCK);
                    }
                    if ((LevelAccessor) world instanceof ServerLevel level) {
                        LightningBolt entityToSpawn = EntityType.LIGHTNING_BOLT.create(level);
                        if (entityToSpawn != null) {
                            entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
                            level.addFreshEntity(entityToSpawn);
                        }
                    }
                    if ((LevelAccessor) world instanceof ServerLevel level)
                        level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 6, 4, 4, 4, 0);
                    if ((LevelAccessor) world instanceof Level level) {
                            level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 3, 1);
                    }
                    itemstack.shrink(1);
                }
            }
        }
    }
}
