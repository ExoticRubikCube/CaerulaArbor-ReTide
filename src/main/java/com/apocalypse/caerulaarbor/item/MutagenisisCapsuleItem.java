package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class MutagenisisCapsuleItem extends Item {
	public MutagenisisCapsuleItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE).food((new FoodProperties.Builder()).nutrition(2).saturationMod(1f).alwaysEat().build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.mutagenisis_capsule.description_0"));
		list.add(Component.translatable("item.caerula_arbor.mutagenisis_capsule.description_1"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
        if (entity != null) {
            double ocean = 0;
            ocean = (((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_oceanization;
            if (ocean < 2.9) {
                EntityUtils.deductSanity(entity, (ocean + 1) * 40);
                ((Entity) entity).hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanize_damage")))), (float) (3 * (ocean + 1)));
                if (!entity.level().isClientSide()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, (int) ocean));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, (int) ocean));
                }
                if (((Entity) entity).isAlive()) {
                    {
                        double _setval = ocean + 1;
                        ((Entity) entity).getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                            capability.player_oceanization = _setval;
                            capability.syncPlayerVariables(entity);
                        });
                    }
                }
                if (ocean + 1 > 2.9) {
                    if ((Entity) entity instanceof ServerPlayer _player) {
                        Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "they_shall_pay"));
                        AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                        if (!_ap.isDone()) {
                            for (String criteria : _ap.getRemainingCriteria())
                                _player.getAdvancements().award(_adv, criteria);
                        }
                    }
                } else {
                    if ((Entity) entity instanceof ServerPlayer _player) {
                        Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "they_shall_welcome"));
                        AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                        if (!_ap.isDone()) {
                            for (String criteria : _ap.getRemainingCriteria())
                                _player.getAdvancements().award(_adv, criteria);
                        }
                    }
                }
            } else {
                if ((Entity) entity instanceof ServerPlayer _player) {
                    Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation(CaerulaArborMod.MODID, "they_shall_welcome"));
                    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
                    if (!_ap.isDone()) {
                        for (String criteria : _ap.getRemainingCriteria())
                            _player.getAdvancements().award(_adv, criteria);
                    }
                }
            }
        }
        return retval;
	}
}
