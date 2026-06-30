
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAParticles;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TideOfChitinMobEffect extends MobEffect {
    public TideOfChitinMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -13382401);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "6d392e1f-11d2-3f61-a82f-7dcfd9a507be", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MAX_HEALTH, "45591a17-debe-3f82-bab4-0fe652f64fd9", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        if (entity == null)
            return;
        {
            ItemStack _setval = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
            ((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).ifPresent(capability -> {
                capability.chitin_knife_selected = _setval.copy();
                capability.syncPlayerVariables(entity);
            });
        }
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (entity == null)
            return;
        double perc;
        if (!(((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)
                .getItem() == ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).chitin_knife_selected).getItem())) {
            if (world instanceof Level _level) {
                if (_level.isClientSide()) {
                    _level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.beacon.deactivate")), SoundSource.NEUTRAL, (float) 3.2, 1, false);
                }
            }
            perc = EntityUtils.getHealthPerc(entity);
            if ((Entity) entity instanceof LivingEntity _entity)
                _entity.removeEffect(CAMobEffects.TIDE_OF_CHITIN.get());
            if ((Entity) entity instanceof LivingEntity _entity)
                _entity.setHealth((float) (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * perc));
        }
        if ((((Entity) entity).getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).kingShowPtc) {
            world.addParticle(CAParticles.KNIFEPTC.get(), (x + Mth.nextDouble(RandomSource.create(), -0.45, 0.45)), (y + Mth.nextDouble(RandomSource.create(), 0, entity.getBbHeight() * 0.8)),
                    (z + Mth.nextDouble(RandomSource.create(), -0.45, 0.45)), Math.sin(Mth.nextDouble(RandomSource.create(), 0, 6.283)), 0.1, Math.cos(Mth.nextDouble(RandomSource.create(), 0, 6.283)));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
