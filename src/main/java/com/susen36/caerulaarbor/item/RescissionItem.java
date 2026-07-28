
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.capability.ModCapabilities;
import com.susen36.caerulaarbor.capability.player.PlayerVariable;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;


public class RescissionItem extends Item {
	public RescissionItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
        String hoverText = ItemUtils.getOneUseItemDescription(itemstack);
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
        }
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        Entity owner;
        if (!itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("used")) {
            boolean setval = true;
            PlayerVariable capability = ModCapabilities.getPlayerVariables(entity);
            capability.relic_util_RESCISSION = setval;
            capability.syncPlayerVariables(entity);
            if (entity instanceof Player player)
                player.giveExperienceLevels(2);
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 2, 1);
            }
            if (world instanceof ServerLevel level)
                level.sendParticles(ParticleTypes.ASH, x, y, z, 72, 1, 1, 1, 1);
            CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putBoolean("used", true));
        } else {
            {
                final Vec3 center = new Vec3(x, y, z);
                List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(2 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                for (Entity entityiterator : entfound) {
                    if (entityiterator == entity) {
                        continue;
                    }
                    owner = entityiterator;
                    if (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame()) {
                            owner = tamEnt.getOwner();
                    }
                    if (owner == entity) {
                        if (entityiterator instanceof LivingEntity livEnt11 && livEnt11.hasEffect(CAMobEffects.UNTAME_CONFIRM)) {
                            if (entityiterator instanceof TamableAnimal ent) {
                                ent.setTame(false);
                            }
                            if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                player.displayClientMessage(Component.literal((entityiterator.getDisplayName().getString() + Component.translatable("item.caerula_arbor.language_key.description_2").getString())), false);
                            livEnt11.removeEffect(CAMobEffects.UNTAME_CONFIRM);
                            if ((LevelAccessor) world instanceof ServerLevel level)
                                level.sendParticles(ParticleTypes.ASH, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 72, 1, 1, 1, 0.5);
                            itemstack.shrink(1);
                            if (entityiterator instanceof Wolf) {
                                CaerulaArborMod.queueServerWork(Mth.nextInt(RandomSource.create(), 40, 80), () -> {
                                    if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                        player.displayClientMessage(Component.literal(("鎼俹" + Component.translatable("item.caerula_arbor.language_key.description_3").getString())), false);
                                });
                            }
                        } else {
                            if ((Entity) entity instanceof Player player && !player.level().isClientSide())
                                player.displayClientMessage(Component.literal(("鎼俢" + Component.translatable("item.caerula_arbor.language_key.description_0").getString() + entityiterator.getDisplayName().getString()
                                        + Component.translatable("item.caerula_arbor.language_key.description_1").getString())), false);
                            if (entityiterator instanceof LivingEntity living && !entity.level().isClientSide())
                                living.addEffect(new MobEffectInstance(CAMobEffects.UNTAME_CONFIRM, 300, 0, false, false));
                        }
                        break;
                    }
                }
            }
        }
        return ar;
	}
}