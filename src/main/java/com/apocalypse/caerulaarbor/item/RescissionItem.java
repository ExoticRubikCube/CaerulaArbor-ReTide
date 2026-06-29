
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

public class RescissionItem extends Item {
	public RescissionItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
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
        if (!itemstack.getOrCreateTag().getBoolean("used")) {
            {
                boolean _setval = true;
                ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                    capability.relic_util_RESCISSION = _setval;
                    capability.syncPlayerVariables(entity);
                });
            }
            if ((Entity) entity instanceof Player _player)
                _player.giveExperienceLevels(2);
            if ((LevelAccessor) world instanceof Level _level) {
                    _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.levelup")), SoundSource.NEUTRAL, 2, 1);
            }
            if ((LevelAccessor) world instanceof ServerLevel _level)
                _level.sendParticles(ParticleTypes.ASH, x, y, z, 72, 1, 1, 1, 1);
            itemstack.getOrCreateTag().putBoolean("used", true);
        } else {
            {
                final Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                for (Entity entityiterator : _entfound) {
                    if (entityiterator == entity) {
                        continue;
                    }
                    owner = entityiterator;
                    if (entityiterator instanceof TamableAnimal _tamEnt && _tamEnt.isTame()) {
                            owner = _tamEnt.getOwner();
                    }
                    if (owner == entity) {
                        if (entityiterator instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(CAMobEffects.UNTAME_CONFIRM.get())) {
                            if (entityiterator instanceof TamableAnimal _ent) {
                                _ent.setTame(false);
                            }
                            if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                _player.displayClientMessage(Component.literal((entityiterator.getDisplayName().getString() + Component.translatable("item.caerula_arbor.language_key.description_2").getString())), false);
                            if (entityiterator instanceof LivingEntity _entity)
                                _entity.removeEffect(CAMobEffects.UNTAME_CONFIRM.get());
                            if ((LevelAccessor) world instanceof ServerLevel _level)
                                _level.sendParticles(ParticleTypes.ASH, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 72, 1, 1, 1, 0.5);
                            itemstack.shrink(1);
                            if (entityiterator instanceof Wolf) {
                                CaerulaArborMod.queueServerWork(Mth.nextInt(RandomSource.create(), 40, 80), () -> {
                                    if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                        _player.displayClientMessage(Component.literal(("\u00A7o" + Component.translatable("item.caerula_arbor.language_key.description_3").getString())), false);
                                });
                            }
                        } else {
                            if ((Entity) entity instanceof Player _player && !_player.level().isClientSide())
                                _player.displayClientMessage(Component.literal(("\u00A7c" + Component.translatable("item.caerula_arbor.language_key.description_0").getString() + entityiterator.getDisplayName().getString()
                                        + Component.translatable("item.caerula_arbor.language_key.description_1").getString())), false);
                            if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                                _entity.addEffect(new MobEffectInstance(CAMobEffects.UNTAME_CONFIRM.get(), 300, 0, false, false));
                        }
                        break;
                    }
                }
            }
        }
        return ar;
	}
}
