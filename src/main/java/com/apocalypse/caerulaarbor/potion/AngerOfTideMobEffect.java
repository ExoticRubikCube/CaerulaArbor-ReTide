package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.entity.IzumikOffspringEntity;
import com.apocalypse.caerulaarbor.entity.MartusEntity;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AngerOfTideMobEffect extends MobEffect {
    public AngerOfTideMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10092544);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double minDist = 0;
        Entity enemy = null;
        Entity curEnemy = null;
        minDist = 999;
        if (entity instanceof IzumikOffspringEntity) {
            return;
        }
        curEnemy = (Entity) entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
        if (!(curEnemy == null) && curEnemy.isAlive()) {
            return;
        }
        if (!(entity instanceof MartusEntity)) {
            for (Entity entityiterator : world.getEntities(entity, new AABB((x + 32), (y + 12), (z + 32), (x - 32), (y - 9), (z - 32)))) {
                if (entity.isInWater() ^ entityiterator.isInWater()) {
                    continue;
                }
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    continue;
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "sea_friend")))) {
                    continue;
                }
                if (entityiterator instanceof Player) {
                    continue;
                }
                if (entityiterator instanceof LivingEntity _livEnt11 && _livEnt11.isBaby()) {
                    continue;
                }
                if ((Entity) entity instanceof LivingEntity _livEnt12 && _livEnt12.getMobType() == MobType.UNDEAD && entityiterator instanceof LivingEntity _livEnt13 && _livEnt13.getMobType() == MobType.UNDEAD) {
                    continue;
                }
                if (entityiterator.getPersistentData().getBoolean("seabornForgive")) {
                    continue;
                }
                if (EntityUtils.isSameTeam(entity, entityiterator)) {
                    continue;
                }
                if (entity.distanceTo(entityiterator) < minDist) {
                    minDist = entity.distanceTo(entityiterator);
                    enemy = entityiterator;
                }
            }
            if (!(enemy == null)) {
                if ((Entity) entity instanceof Mob _entity && enemy instanceof LivingEntity _ent)
                    _entity.setTarget(_ent);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
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
