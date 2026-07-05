
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.entity.OceanizedChickenEntity;
import com.apocalypse.caerulaarbor.init.CAEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class NetherseaChickenEggItem extends Item {
	public NetherseaChickenEggItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
        double rate = Math.max(itemstack.getOrCreateTag().getDouble("rate") * 0.1, 100);
        double offset = Math.max(itemstack.getOrCreateTag().getDouble("offset"), 4);
        String hoverText = Component.translatable("item.caerula_arbor.nethersea_chicken_egg.rate").getString() + new java.text.DecimalFormat("##.##").format(rate) + "%" + "\n"
                + Component.translatable("item.caerula_arbor.nethersea_chicken_egg.offset").getString() + new java.text.DecimalFormat("##.##").format(offset);
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
        }
    }

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (entity == null)
            return InteractionResult.SUCCESS;
        if (world.isClientSide()) return InteractionResult.SUCCESS;
        if (Math.random() < 0.75) {
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SNIFFER_EGG_CRACK, SoundSource.PLAYERS, 1, 1);
            }
            return InteractionResult.FAIL;
        }
        int count = (int) (1 + Math.pow(1.45 * Math.random(), 2));
        double rrr = Math.max(1, itemstack.getOrCreateTag().getDouble("rate") * 0.001);
        double ooo = Math.max(4, itemstack.getOrCreateTag().getDouble("offset"));
        if (world instanceof Level level) {
                level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.SNIFFER_EGG_HATCH, SoundSource.PLAYERS, 1, 1);
        }
        itemstack.shrink(1);
        BlockPos pos = BlockPos.containing(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
        for (int index0 = 0; index0 < count; index0++) {
            double fr = rrr + Mth.nextDouble(RandomSource.create(), -1 / ooo, ooo * 0.05);
            if (world instanceof ServerLevel level) {
                Entity entityToSpawn = CAEntities.OCEANIZED_CHICKEN.get().spawn(level, pos, MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn instanceof OceanizedChickenEntity chicken) {
                    chicken.setYRot(world.getRandom().nextFloat() * 360F);
                    AttributeInstance max_h = chicken.getAttribute(Attributes.MAX_HEALTH);
                    AttributeInstance atk = chicken.getAttribute(Attributes.ATTACK_DAMAGE);
                    if (max_h != null) max_h.setBaseValue(max_h.getBaseValue() * fr);
                    if (atk != null) atk.setBaseValue(atk.getBaseValue() * fr);
                    chicken.setHealth(chicken.getMaxHealth());
                    SynchedEntityData data = chicken.getEntityData();
                    data.set(OceanizedChickenEntity.DATA_EGG_RATE, (int) (fr * 1000));
                    data.set(OceanizedChickenEntity.DATA_EGG_OFFSET, (int) ooo);
                    data.set(OceanizedChickenEntity.DATA_IS_CHILD, true);
                }
            }
            //CaerulaArborMod.LOGGER.info(("Summon chicken with rate: " + new java.text.DecimalFormat("##.##").format(fr) + " and with offset: " + new java.text.DecimalFormat("##.##").format(ooo)));
        }
        return InteractionResult.SUCCESS;
    }
}
